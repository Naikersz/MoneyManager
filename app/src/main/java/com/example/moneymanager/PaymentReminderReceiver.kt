package com.example.moneymanager

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object PaymentReminderContract {
    const val ACTION_REMINDER = "com.example.moneymanager.action.PAYMENT_REMINDER"
    const val ACTION_MARK_PAID = "com.example.moneymanager.action.MARK_PAYMENT_PAID"
    const val ACTION_SKIP = "com.example.moneymanager.action.SKIP_PAYMENT"
    const val EXTRA_TYPE = "type"
    const val EXTRA_ID = "id"
    const val EXTRA_DATE = "date"
    const val EXTRA_NAME = "name"
    const val EXTRA_AMOUNT = "amount"
    const val TYPE_SUBSCRIPTION = "subscription"
    const val TYPE_RECURRING_EXPENSE = "recurring_expense"
    const val CHANNEL_ID = "payment_reminders"
    const val KEY_SUBSCRIPTIONS = "subscriptions"
    const val KEY_RECURRING_EXPENSES = "recurring_expenses"
}

object PaymentReminderScheduler {
    private const val REMINDER_HOUR = 9
    private const val LOOKAHEAD_DAYS = 370

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            PaymentReminderContract.CHANNEL_ID,
            context.getString(R.string.payment_reminders_channel),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }

    fun scheduleAll(
        context: Context,
        subscriptions: List<Subscription>,
        recurringExpenses: List<RecurringExpense>
    ) {
        createChannel(context)
        subscriptions.forEach { subscription ->
            if (!subscription.remindersEnabled) return@forEach
            nextReminderOccurrence(
                startIso = subscription.nextChargeDate,
                period = subscription.period,
                daysBefore = subscription.reminderDaysBefore,
                paidDates = emptyList(),
                skippedDates = emptyList(),
                isOccurrenceEnabled = { occurrenceDate ->
                    isSubscriptionLifecycleActiveOnDate(
                        subscription.lifecycleStatus,
                        subscription.lifecycleDate,
                        occurrenceDate
                    )
                }
            )?.let { occurrenceDate ->
                scheduleReminder(
                    context = context,
                    type = PaymentReminderContract.TYPE_SUBSCRIPTION,
                    id = subscription.id,
                    name = subscription.name,
                    amount = subscription.amount,
                    occurrenceDate = occurrenceDate,
                    daysBefore = subscription.reminderDaysBefore
                )
            }
        }
        recurringExpenses.forEach { recurringExpense ->
            if (!recurringExpense.remindersEnabled) return@forEach
            nextReminderOccurrence(
                startIso = recurringExpense.startDate,
                period = recurringExpense.period,
                daysBefore = recurringExpense.reminderDaysBefore,
                paidDates = recurringExpense.paidDates,
                skippedDates = recurringExpense.skippedDates
            )?.let { occurrenceDate ->
                scheduleReminder(
                    context = context,
                    type = PaymentReminderContract.TYPE_RECURRING_EXPENSE,
                    id = recurringExpense.id,
                    name = recurringExpense.name,
                    amount = recurringExpense.amount,
                    occurrenceDate = occurrenceDate,
                    daysBefore = recurringExpense.reminderDaysBefore
                )
            }
        }
    }

    fun notificationId(type: String, id: Long, dateIso: String): Int {
        return abs("$type-$id-$dateIso".hashCode())
    }

    fun isOccurrence(startIso: String?, period: String, dateIso: String): Boolean {
        val start = parseIsoDate(startIso) ?: return false
        val target = parseIsoDate(dateIso) ?: return false
        clearTime(start)
        clearTime(target)
        if (target.before(start)) return false

        val anchorDay = start.get(Calendar.DAY_OF_MONTH)
        return if (period == "yearly") {
            target.get(Calendar.MONTH) == start.get(Calendar.MONTH) &&
                target.get(Calendar.DAY_OF_MONTH) == minOf(anchorDay, target.getActualMaximum(Calendar.DAY_OF_MONTH))
        } else {
            val monthsDiff =
                (target.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 +
                    (target.get(Calendar.MONTH) - start.get(Calendar.MONTH))
            monthsDiff >= 0 &&
                target.get(Calendar.DAY_OF_MONTH) == minOf(anchorDay, target.getActualMaximum(Calendar.DAY_OF_MONTH))
        }
    }

    private fun nextReminderOccurrence(
        startIso: String?,
        period: String,
        daysBefore: Int,
        paidDates: List<String>,
        skippedDates: List<String>,
        isOccurrenceEnabled: (String) -> Boolean = { true }
    ): String? {
        val today = Calendar.getInstance().apply { clearTime(this) }
        val limit = Calendar.getInstance().apply {
            clearTime(this)
            add(Calendar.DAY_OF_YEAR, LOOKAHEAD_DAYS)
        }
        val cursor = today.clone() as Calendar
        while (!cursor.after(limit)) {
            val occurrenceDate = formatIsoDate(cursor.time)
            if (
                isOccurrence(startIso, period, occurrenceDate) &&
                isOccurrenceEnabled(occurrenceDate) &&
                !paidDates.contains(occurrenceDate) &&
                !skippedDates.contains(occurrenceDate)
            ) {
                val reminderDate = parseIsoDate(occurrenceDate) ?: return null
                reminderDate.add(Calendar.DAY_OF_YEAR, -daysBefore.coerceAtLeast(0))
                reminderDate.set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
                reminderDate.set(Calendar.MINUTE, 0)
                reminderDate.set(Calendar.SECOND, 0)
                reminderDate.set(Calendar.MILLISECOND, 0)
                if (reminderDate.timeInMillis >= System.currentTimeMillis()) {
                    return occurrenceDate
                }
            }
            cursor.add(Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    private fun scheduleReminder(
        context: Context,
        type: String,
        id: Long,
        name: String,
        amount: Double,
        occurrenceDate: String,
        daysBefore: Int
    ) {
        val reminderDate = parseIsoDate(occurrenceDate) ?: return
        reminderDate.add(Calendar.DAY_OF_YEAR, -daysBefore.coerceAtLeast(0))
        reminderDate.set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
        reminderDate.set(Calendar.MINUTE, 0)
        reminderDate.set(Calendar.SECOND, 0)
        reminderDate.set(Calendar.MILLISECOND, 0)
        if (reminderDate.timeInMillis < System.currentTimeMillis()) return

        val intent = reminderIntent(context, PaymentReminderContract.ACTION_REMINDER, type, id, occurrenceDate, name, amount)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId(type, id, occurrenceDate),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderDate.timeInMillis, pendingIntent)
    }

    fun reminderIntent(
        context: Context,
        action: String,
        type: String,
        id: Long,
        occurrenceDate: String,
        name: String = "",
        amount: Double = 0.0
    ): Intent {
        return Intent(context, PaymentReminderReceiver::class.java).apply {
            this.action = action
            putExtra(PaymentReminderContract.EXTRA_TYPE, type)
            putExtra(PaymentReminderContract.EXTRA_ID, id)
            putExtra(PaymentReminderContract.EXTRA_DATE, occurrenceDate)
            putExtra(PaymentReminderContract.EXTRA_NAME, name)
            putExtra(PaymentReminderContract.EXTRA_AMOUNT, amount)
        }
    }

    private fun clearTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun parseIsoDate(value: String?): Calendar? {
        if (value.isNullOrBlank()) return null
        return runCatching {
            Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) ?: Date()
            }
        }.getOrNull()
    }

    private fun formatIsoDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }
}

class PaymentReminderReceiver : BroadcastReceiver() {
    private val gson = Gson()

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(PaymentReminderContract.EXTRA_TYPE) ?: return
        val id = intent.getLongExtra(PaymentReminderContract.EXTRA_ID, 0L)
        val date = intent.getStringExtra(PaymentReminderContract.EXTRA_DATE) ?: return
        val dao = MoneyDatabase.getInstance(context.applicationContext).moneyDao()
        val subscriptions = readSubscriptions(dao)
        val recurringExpenses = readRecurringExpenses(dao)
        val notificationId = PaymentReminderScheduler.notificationId(type, id, date)

        when (intent.action) {
            PaymentReminderContract.ACTION_REMINDER -> showReminderIfActive(context, type, id, date, subscriptions, recurringExpenses)
            PaymentReminderContract.ACTION_MARK_PAID -> {
                updateOccurrenceStatus(dao, type, id, date, markPaid = true, subscriptions, recurringExpenses)
                NotificationManagerCompat.from(context).cancel(notificationId)
                PaymentReminderScheduler.scheduleAll(context, readSubscriptions(dao), readRecurringExpenses(dao))
            }
            PaymentReminderContract.ACTION_SKIP -> {
                updateOccurrenceStatus(dao, type, id, date, markPaid = false, subscriptions, recurringExpenses)
                NotificationManagerCompat.from(context).cancel(notificationId)
                PaymentReminderScheduler.scheduleAll(context, readSubscriptions(dao), readRecurringExpenses(dao))
            }
        }
    }

    private fun showReminderIfActive(
        context: Context,
        type: String,
        id: Long,
        date: String,
        subscriptions: List<Subscription>,
        recurringExpenses: List<RecurringExpense>
    ) {
        val payment = when (type) {
            PaymentReminderContract.TYPE_SUBSCRIPTION -> subscriptions.firstOrNull { it.id == id }?.let {
                val lifecycleStatus = normalizeSubscriptionLifecycleStatus(
                    runCatching { it.lifecycleStatus }.getOrNull()
                )
                ReminderPayment(
                    it.name,
                    it.amount,
                    it.nextChargeDate,
                    it.period,
                    it.remindersEnabled,
                    emptyList(),
                    emptyList(),
                    lifecycleStatus,
                    it.lifecycleDate
                )
            }
            PaymentReminderContract.TYPE_RECURRING_EXPENSE -> recurringExpenses.firstOrNull { it.id == id }?.let {
                ReminderPayment(
                    it.name,
                    it.amount,
                    it.startDate,
                    it.period,
                    it.remindersEnabled,
                    it.paidDates,
                    it.skippedDates
                )
            }
            else -> null
        } ?: return

        val isSubscription = type == PaymentReminderContract.TYPE_SUBSCRIPTION
        if (
            !payment.remindersEnabled ||
            (!isSubscription && (payment.paidDates.contains(date) || payment.skippedDates.contains(date))) ||
            (isSubscription && !isSubscriptionLifecycleActiveOnDate(payment.lifecycleStatus, payment.lifecycleDate, date)) ||
            !PaymentReminderScheduler.isOccurrence(payment.startDate, payment.period, date)
        ) {
            return
        }

        val title = context.getString(R.string.payment_reminder_title, payment.name)
        val text = context.getString(R.string.payment_reminder_text, formatAmount(payment.amount), date)
        val openIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, PaymentReminderContract.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nav_calendar)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(openIntent)
            .setAutoCancel(true)
        if (!isSubscription) {
            val paidIntent = actionPendingIntent(context, PaymentReminderContract.ACTION_MARK_PAID, type, id, date, payment.name, payment.amount)
            val skipIntent = actionPendingIntent(context, PaymentReminderContract.ACTION_SKIP, type, id, date, payment.name, payment.amount)
            builder
                .addAction(R.drawable.ic_nav_calendar, context.getString(R.string.mark_paid), paidIntent)
                .addAction(R.drawable.ic_nav_calendar, context.getString(R.string.skip_payment), skipIntent)
        }
        val notification = builder.build()

        runCatching {
            NotificationManagerCompat.from(context)
                .notify(PaymentReminderScheduler.notificationId(type, id, date), notification)
        }
    }

    private fun actionPendingIntent(
        context: Context,
        action: String,
        type: String,
        id: Long,
        date: String,
        name: String,
        amount: Double
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            PaymentReminderScheduler.notificationId("$action-$type", id, date),
            PaymentReminderScheduler.reminderIntent(context, action, type, id, date, name, amount),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateOccurrenceStatus(
        dao: MoneyDao,
        type: String,
        id: Long,
        date: String,
        markPaid: Boolean,
        subscriptions: List<Subscription>,
        recurringExpenses: List<RecurringExpense>
    ) {
        if (type == PaymentReminderContract.TYPE_SUBSCRIPTION) return
        if (type == PaymentReminderContract.TYPE_RECURRING_EXPENSE) {
            val updated = recurringExpenses.map { recurringExpense ->
                if (recurringExpense.id != id) {
                    recurringExpense
                } else if (markPaid) {
                    recurringExpense.copy(
                        paidDates = (recurringExpense.paidDates + date).distinct(),
                        skippedDates = recurringExpense.skippedDates - date
                    )
                } else {
                    recurringExpense.copy(
                        skippedDates = (recurringExpense.skippedDates + date).distinct(),
                        paidDates = recurringExpense.paidDates - date
                    )
                }
            }
            dao.put(StoredValue(PaymentReminderContract.KEY_RECURRING_EXPENSES, gson.toJson(updated), System.currentTimeMillis()))
        }
    }

    private fun readSubscriptions(dao: MoneyDao): List<Subscription> {
        return runCatching {
            gson.fromJson<List<Subscription>>(
                dao.getValue(PaymentReminderContract.KEY_SUBSCRIPTIONS) ?: "[]",
                object : TypeToken<List<Subscription>>() {}.type
            )
        }.getOrNull().orEmpty()
    }

    private fun readRecurringExpenses(dao: MoneyDao): List<RecurringExpense> {
        return runCatching {
            gson.fromJson<List<RecurringExpense>>(
                dao.getValue(PaymentReminderContract.KEY_RECURRING_EXPENSES) ?: "[]",
                object : TypeToken<List<RecurringExpense>>() {}.type
            )
        }.getOrNull().orEmpty()
    }

    private fun formatAmount(value: Double): String {
        return "%.2f".format(Locale.getDefault(), value)
    }

    private data class ReminderPayment(
        val name: String,
        val amount: Double,
        val startDate: String?,
        val period: String,
        val remindersEnabled: Boolean,
        val paidDates: List<String>,
        val skippedDates: List<String>,
        val lifecycleStatus: String = SUBSCRIPTION_STATUS_ACTIVE,
        val lifecycleDate: String? = null
    )
}
