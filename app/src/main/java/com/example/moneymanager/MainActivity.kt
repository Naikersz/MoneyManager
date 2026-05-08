package com.example.moneymanager

import android.Manifest
import android.app.DatePickerDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Base64
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewConfiguration
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.res.Configuration
import java.text.SimpleDateFormat
import java.io.OutputStreamWriter
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PREFS_NAME = "MoneyManager"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_INCOMES = "incomes"
        private const val KEY_SUBSCRIPTIONS = "subscriptions"
        private const val KEY_EXPENSES = "expenses"
        private const val KEY_TRANSFERS = "transfers"
        private const val KEY_CATEGORIES = "categories"
        private const val KEY_PAYMENT_ACCOUNTS = "payment_accounts"
        private const val KEY_NEXT_PAYMENT_ACCOUNT_ID = "next_payment_account_id"
        private const val KEY_CATEGORY_BUDGETS = "category_budgets"
        private const val KEY_RECURRING_EXPENSES = "recurring_expenses"
        private const val KEY_FAVORITE_CATEGORIES = "favorite_categories"
        private const val KEY_SAVED_TAGS = "saved_tags"
        private const val KEY_SAVED_SUBCATEGORIES = "saved_subcategories"
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_APP_LOCK_PIN_HASH = "app_lock_pin_hash"
        private const val KEY_APP_LOCK_PIN_SALT = "app_lock_pin_salt"
        private const val KEY_APP_LOCK_DEVICE_AUTH = "app_lock_device_auth"
        private const val KEY_MONTHLY_GOAL_AMOUNT = "monthly_goal_amount"
        private const val KEY_MONTHLY_GOAL_INCLUDE_EXPENSES = "monthly_goal_include_expenses"
        private const val KEY_MONTHLY_GOAL_INCLUDE_SUBSCRIPTIONS = "monthly_goal_include_subscriptions"
        private const val KEY_MONTHLY_GOAL_CATEGORIES = "monthly_goal_categories"
        private const val KEY_CASH_AMOUNT = "cash_amount"
        private const val KEY_ACCOUNT_BALANCES = "account_balances"
        private const val KEY_ACCOUNT_LABELS = "account_labels"
        private const val KEY_ROOM_MIGRATION_DONE = "room_migration_done"
        private const val THEME_MINIMAL = "minimal"
        private const val THEME_HELL = "hell"
        private const val TAB_ALL = 0
        private const val TAB_INCOME = 1
        private const val TAB_SUBSCRIPTIONS = 2
        private const val TAB_EXPENSES = 3
        private const val ASSETS_TAB_ACCOUNTS = 0
        private const val ASSETS_TAB_BUDGETS = 1
        private const val FILTER_ALL = "all"
        private const val FILTER_GROUP_CARDS = "group_cards"
        private const val FILTER_GROUP_WALLETS = "group_wallets"
        private const val FILTER_GROUP_CASH = "group_cash"
        private const val ACCOUNT_CASH = "cash"
        private const val ACCOUNT_BANK = "bank"
        private const val ACCOUNT_PAYPAL = "paypal"
        private const val DEFAULT_CASH_ACCOUNT_ID = 1L
        private const val DEFAULT_CARD_ACCOUNT_ID = 2L
    }

    private lateinit var tvRemaining: TextView
    private lateinit var tvCashTotal: TextView
    private lateinit var tvIncomeTotal: TextView
    private lateinit var tvSubscriptionsTotal: TextView
    private lateinit var tvExpensesTotal: TextView
    private lateinit var tvHomeNetTotal: TextView
    private lateinit var layoutHomeBudgetSummary: LinearLayout
    private lateinit var tvHomeBudgetSummaryValue: TextView
    private lateinit var tvHomeBudgetRemainingLabel: TextView
    private lateinit var tvHomeBudgetRemainingValue: TextView
    private lateinit var tvHomeBudgetAlerts: TextView
    private lateinit var progressHomeBudget: ProgressBar
    private lateinit var layoutHomeMonthlyGoal: LinearLayout
    private lateinit var tvHomeMonthlyGoalValue: TextView
    private lateinit var tvHomeMonthlyGoalMeta: TextView
    private lateinit var progressHomeMonthlyGoal: ProgressBar
    private lateinit var tvPageLanguageValue: TextView
    private lateinit var tvPageThemeValue: TextView
    private lateinit var tvPageCurrencyValue: TextView
    private lateinit var tvPageAccountsValue: TextView
    private lateinit var tvPageAppLockValue: TextView
    private lateinit var tvPageTagsValue: TextView
    private lateinit var tvCalendarMonth: TextView
    private lateinit var tvHomeMonthLabel: TextView
    private lateinit var tvCalendarBalanceLabel: TextView
    private lateinit var tvCalendarBalanceValue: TextView
    private lateinit var tvCalendarMonthStats: TextView
    private lateinit var tvCalendarBudgetStats: TextView
    private lateinit var tvCalendarMonthlyGoalStats: TextView
    private lateinit var layoutCalendarDaySummary: LinearLayout
    private lateinit var tvCalendarSelectedDayTitle: TextView
    private lateinit var tvCalendarSelectedDayEvents: TextView
    private lateinit var tvCalendarSelectedDayAmount: TextView
    private lateinit var btnCalendarAccountPicker: Button
    private lateinit var btnCalendarOpenDay: Button
    private lateinit var btnHomeAccountPicker: Button
    private lateinit var btnHomeBalancesVisibility: Button
    private lateinit var btnAnalyticsAccountPicker: Button
    private lateinit var etHomeSearch: EditText
    private lateinit var btnHomePreviousMonth: View
    private lateinit var btnHomeNextMonth: View
    private lateinit var btnFabAddEntry: Button
    private lateinit var tvSectionHome: TextView
    private lateinit var tvSectionCategories: TextView
    private lateinit var tvSectionCalendar: TextView
    private lateinit var tvSectionAnalytics: TextView
    private lateinit var tvSectionSettings: TextView
    private lateinit var tvSectionCalendarBadge: TextView
    private lateinit var ivSectionHome: ImageView
    private lateinit var ivSectionCategories: ImageView
    private lateinit var ivSectionCalendar: ImageView
    private lateinit var ivSectionAnalytics: ImageView
    private lateinit var ivSectionSettings: ImageView
    private lateinit var navHomeContent: LinearLayout
    private lateinit var navCategoriesContent: LinearLayout
    private lateinit var navCalendarContent: LinearLayout
    private lateinit var navAnalyticsContent: LinearLayout
    private lateinit var navSettingsContent: LinearLayout
    private lateinit var gridCalendarDays: GridLayout
    private lateinit var viewPagerSections: ViewPager2
    private lateinit var tabFilters: TabLayout
    private lateinit var viewPagerItems: ViewPager2
    private lateinit var btnSectionHome: FrameLayout
    private lateinit var btnSectionCategories: FrameLayout
    private lateinit var btnSectionCalendar: FrameLayout
    private lateinit var btnSectionAnalytics: FrameLayout
    private lateinit var btnSectionSettings: FrameLayout
    private lateinit var homePageView: View
    private lateinit var categoriesPageView: View
    private lateinit var calendarPageView: View
    private lateinit var analyticsPageView: View
    private lateinit var settingsPageView: View
    private lateinit var layoutCategoriesPageList: LinearLayout
    private lateinit var tvEmptyCategoriesPage: TextView
    private lateinit var btnAssetsAccountsTab: Button
    private lateinit var btnAssetsBudgetsTab: Button
    private lateinit var btnAddAccountPage: Button
    private lateinit var btnAddCategoryPage: Button
    private lateinit var btnTransferPage: Button
    private lateinit var tvAnalyticsMonth: TextView
    private lateinit var tvAnalyticsTotal: TextView
    private lateinit var tvAnalyticsDynamicsSummary: TextView
    private lateinit var tvAnalyticsEmptyState: TextView
    private lateinit var tvAnalyticsTopTitle: TextView
    private lateinit var layoutAnalyticsBudgetAttention: LinearLayout
    private lateinit var tvAnalyticsBudgetAttention: TextView
    private lateinit var chartAnalyticsCategories: CategoryPieChartView
    private lateinit var chartAnalyticsMonthlyTrend: MonthlyTrendChartView
    private lateinit var layoutAnalyticsTopCategories: LinearLayout
    private lateinit var btnAnalyticsCategoryDetails: View
    private lateinit var btnAnalyticsTrendDetails: View

    private val incomes = mutableListOf<Income>()
    private val subscriptions = mutableListOf<Subscription>()
    private val recurringExpenses = mutableListOf<RecurringExpense>()
    private val expenses = mutableListOf<Expense>()
    private val transfers = mutableListOf<AccountTransfer>()
    private val categories = mutableListOf<String>()
    private val categoryBudgets = mutableListOf<CategoryBudget>()
    private val monthlyGoalCategories = mutableSetOf<String>()
    private val favoriteCategories = mutableSetOf<String>()
    private val savedTags = mutableSetOf<String>()
    private val savedSubcategories = mutableSetOf<String>()
    private val items = mutableListOf<Item>()
    private lateinit var pagerAdapter: ItemsPagerAdapter
    private lateinit var sectionPagerAdapter: StaticPagesAdapter
    private var tabMediator: TabLayoutMediator? = null
    private var selectedHomeAccount = FILTER_ALL
    private var selectedAssetsTab = ASSETS_TAB_ACCOUNTS
    private var balancesHidden = false
    private var homeSearchQuery = ""
    private var cashAmount = 0.0
    private var monthlyGoalAmount = 0.0
    private var monthlyGoalIncludeExpenses = true
    private var monthlyGoalIncludeSubscriptions = true
    private val paymentAccounts = mutableListOf<PaymentAccount>()
    private var nextPaymentAccountId = DEFAULT_CARD_ACCOUNT_ID + 1
    private var selectedCalendarAccount = FILTER_ALL
    private var selectedAnalyticsAccount = FILTER_ALL
    private var lastCalendarTapDateIso: String? = null
    private var lastCalendarTapAt: Long = 0L
    private val displayedMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private val homeDisplayedMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private val analyticsDisplayedMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private var selectedCalendarDateIso = isoDateFormatter().format(Date())
    private var calendarEventsByDate: Map<String, List<CalendarEvent>> = emptyMap()
    private var calendarMaxDayDelta = 1.0

    private val gson = Gson()
    private lateinit var moneyDao: MoneyDao
    private var isUiInitialized = false
    private lateinit var exportBackupLauncher: ActivityResultLauncher<String>
    private lateinit var importBackupLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var exportCsvLauncher: ActivityResultLauncher<String>
    private lateinit var importCsvLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var deviceAuthLauncher: ActivityResultLauncher<Intent>
    private var appUnlocked = false
    private var unlockDialogShowing = false

    override fun attachBaseContext(newBase: Context) {
        val languageCode = newBase
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "en") ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        applySavedLocale()
        setContentView(R.layout.activity_main)
        setupBackupLaunchers()
        moneyDao = MoneyDatabase.getInstance(applicationContext).moneyDao()
        PaymentReminderScheduler.createChannel(this)
        ensurePaymentReminderPermission()

        btnSectionHome = findViewById(R.id.btnSectionHome)
        btnSectionCategories = findViewById(R.id.btnSectionCategories)
        btnSectionCalendar = findViewById(R.id.btnSectionCalendar)
        btnSectionAnalytics = findViewById(R.id.btnSectionAnalytics)
        btnSectionSettings = findViewById(R.id.btnSectionSettings)
        navHomeContent = findViewById(R.id.navHomeContent)
        navCategoriesContent = findViewById(R.id.navCategoriesContent)
        navCalendarContent = findViewById(R.id.navCalendarContent)
        navAnalyticsContent = findViewById(R.id.navAnalyticsContent)
        navSettingsContent = findViewById(R.id.navSettingsContent)
        ivSectionHome = findViewById(R.id.ivSectionHome)
        ivSectionCategories = findViewById(R.id.ivSectionCategories)
        ivSectionCalendar = findViewById(R.id.ivSectionCalendar)
        ivSectionAnalytics = findViewById(R.id.ivSectionAnalytics)
        ivSectionSettings = findViewById(R.id.ivSectionSettings)
        tvSectionHome = findViewById(R.id.tvSectionHome)
        tvSectionCategories = findViewById(R.id.tvSectionCategories)
        tvSectionCalendar = findViewById(R.id.tvSectionCalendar)
        tvSectionAnalytics = findViewById(R.id.tvSectionAnalytics)
        tvSectionSettings = findViewById(R.id.tvSectionSettings)
        tvSectionCalendarBadge = findViewById(R.id.tvSectionCalendarBadge)
        viewPagerSections = findViewById(R.id.viewPagerSections)

        homePageView = LayoutInflater.from(this).inflate(R.layout.page_home, viewPagerSections, false)
        categoriesPageView = LayoutInflater.from(this).inflate(R.layout.page_categories, viewPagerSections, false)
        calendarPageView = LayoutInflater.from(this).inflate(R.layout.page_calendar, viewPagerSections, false)
        analyticsPageView = LayoutInflater.from(this).inflate(R.layout.page_analytics, viewPagerSections, false)
        settingsPageView = LayoutInflater.from(this).inflate(R.layout.page_settings, viewPagerSections, false)

        bindHomePageViews()
        bindCategoriesPageViews()
        bindAnalyticsPageViews()
        bindSettingsPageViews()
        bindCalendarPageViews()

        loadData()
        saveData()
        populateItems()
        setupCalendarAccountFilter()

        setupSectionPager()
        setupPager()
        updateSettingsPageValues()
        updateCalendarBadge()
        addPressAnimation(btnSectionHome)
        addPressAnimation(btnSectionCategories)
        addPressAnimation(btnSectionCalendar)
        addPressAnimation(btnSectionAnalytics)
        addPressAnimation(btnSectionSettings)
        btnSectionHome.setOnClickListener { viewPagerSections.currentItem = 0 }
        btnSectionCategories.setOnClickListener { viewPagerSections.currentItem = 1 }
        btnSectionCalendar.setOnClickListener { viewPagerSections.currentItem = 2 }
        btnSectionAnalytics.setOnClickListener { viewPagerSections.currentItem = 3 }
        btnSectionSettings.setOnClickListener { viewPagerSections.currentItem = 4 }

        isUiInitialized = true
        updateTotals()
        refreshPagedContent()
        if (isAppLockEnabled()) {
            showUnlockDialog()
        }
    }

    private fun setupBackupLaunchers() {
        exportBackupLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri: Uri? ->
            uri?.let(::writeBackupToUri)
        }
        importBackupLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let(::readBackupFromUri)
        }
        exportCsvLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("text/csv")
        ) { uri: Uri? ->
            uri?.let(::writeCsvToUri)
        }
        importCsvLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let(::readCsvFromUri)
        }
        deviceAuthLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                appUnlocked = true
                unlockDialogShowing = false
            } else if (isAppLockEnabled() && !appUnlocked) {
                showUnlockDialog()
            }
        }
    }

    private fun exportBackup() {
        val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
        exportBackupLauncher.launch("money-manager-backup-$stamp.json")
    }

    private fun importBackup() {
        importBackupLauncher.launch(arrayOf("application/json", "text/json", "text/*"))
    }

    private fun exportCsv() {
        val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
        exportCsvLauncher.launch("money-manager-records-$stamp.csv")
    }

    private fun importCsv() {
        importCsvLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "text/*"))
    }

    private fun writeBackupToUri(uri: Uri) {
        val backupJson = gson.toJson(createBackupData())
        val wrote = runCatching {
            contentResolver.openOutputStream(uri)?.use { stream ->
                OutputStreamWriter(stream, Charsets.UTF_8).use { writer ->
                    writer.write(backupJson)
                }
            } ?: error("No output stream")
        }.isSuccess
        Toast.makeText(
            this,
            if (wrote) R.string.backup_export_success else R.string.backup_export_error,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun readBackupFromUri(uri: Uri) {
        val backup = runCatching {
            contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8).use { reader ->
                reader?.let { gson.fromJson(it, MoneyManagerBackup::class.java) }
            }
        }.getOrNull()

        if (backup == null) {
            Toast.makeText(this, R.string.backup_import_error, Toast.LENGTH_SHORT).show()
            return
        }

        showConfirmationBottomSheet(
            title = getString(R.string.backup_import_title),
            message = buildBackupImportPreview(backup),
            positiveText = getString(R.string.restore_backup)
        ) {
            applyBackupData(backup)
        }
    }

    private fun writeCsvToUri(uri: Uri) {
        val csv = createCsvData()
        val wrote = runCatching {
            contentResolver.openOutputStream(uri)?.use { stream ->
                OutputStreamWriter(stream, Charsets.UTF_8).use { writer ->
                    writer.write(csv)
                }
            } ?: error("No output stream")
        }.isSuccess
        Toast.makeText(
            this,
            if (wrote) R.string.csv_export_success else R.string.csv_export_error,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun readCsvFromUri(uri: Uri) {
        val importData = runCatching {
            val csv = contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8).use { reader ->
                reader?.readText().orEmpty()
            }
            parseCsvImport(csv)
        }.getOrNull()

        if (importData == null || importData.isEmpty()) {
            Toast.makeText(this, R.string.csv_import_error, Toast.LENGTH_SHORT).show()
            return
        }

        val filteredImportData = filterCsvImportDuplicates(importData)
        if (filteredImportData.isEmpty()) {
            Toast.makeText(this, R.string.csv_import_no_new_records, Toast.LENGTH_SHORT).show()
            return
        }
        val duplicateCount = importData.totalCount() - filteredImportData.totalCount()

        showConfirmationBottomSheet(
            title = getString(R.string.csv_import_title),
            message = buildCsvImportPreview(filteredImportData, duplicateCount),
            positiveText = getString(R.string.csv_import)
        ) {
            applyCsvImport(filteredImportData)
        }
    }

    private fun createCsvData(): String {
        val header = listOf(
            "type",
            "name",
            "amount",
            "date",
            "period",
            "category",
            "subcategory",
            "tags",
            "account",
            "account_type",
            "reminders_enabled",
            "reminder_days_before",
            "paid_dates",
            "skipped_dates",
            "subscription_status",
            "subscription_status_date",
            "to_account",
            "to_account_type",
            "note"
        )
        val rows = mutableListOf(header)
        incomes.forEach { income ->
            rows.add(
                listOf(
                    "income",
                    income.name,
                    income.amount.toString(),
                    income.expectedDate ?: income.date,
                    income.period,
                    income.type,
                    "",
                    "",
                    getPaymentAccountName(income.accountId),
                    getPaymentAccountType(income.accountId).name,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        expenses.forEach { expense ->
            rows.add(
                listOf(
                    "expense",
                    expense.name,
                    expense.amount.toString(),
                    expense.date,
                    "",
                    expense.category.orEmpty(),
                    expense.subcategory.orEmpty(),
                    expense.tags.joinToString("|"),
                    getPaymentAccountName(expense.accountId),
                    getPaymentAccountType(expense.accountId).name,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        subscriptions.forEach { subscription ->
            rows.add(
                listOf(
                    "subscription",
                    subscription.name,
                    subscription.amount.toString(),
                    subscription.nextChargeDate.orEmpty(),
                    subscription.period,
                    "",
                    "",
                    "",
                    getPaymentAccountName(subscription.accountId),
                    getPaymentAccountType(subscription.accountId).name,
                    subscription.remindersEnabled.toString(),
                    subscription.reminderDaysBefore.toString(),
                    subscription.paidDates.joinToString("|"),
                    subscription.skippedDates.joinToString("|"),
                    normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus),
                    subscription.lifecycleDate.orEmpty(),
                    "",
                    "",
                    ""
                )
            )
        }
        recurringExpenses.forEach { recurringExpense ->
            rows.add(
                listOf(
                    "recurring_expense",
                    recurringExpense.name,
                    recurringExpense.amount.toString(),
                    recurringExpense.startDate.orEmpty(),
                    recurringExpense.period,
                    recurringExpense.category.orEmpty(),
                    recurringExpense.subcategory.orEmpty(),
                    recurringExpense.tags.joinToString("|"),
                    getPaymentAccountName(recurringExpense.accountId),
                    getPaymentAccountType(recurringExpense.accountId).name,
                    recurringExpense.remindersEnabled.toString(),
                    recurringExpense.reminderDaysBefore.toString(),
                    recurringExpense.paidDates.joinToString("|"),
                    recurringExpense.skippedDates.joinToString("|"),
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        transfers.forEach { transfer ->
            rows.add(
                listOf(
                    "transfer",
                    transfer.note.orEmpty().ifBlank { getString(R.string.transfer) },
                    transfer.amount.toString(),
                    transfer.date,
                    "",
                    "",
                    "",
                    "",
                    getPaymentAccountName(transfer.fromAccountId),
                    getPaymentAccountType(transfer.fromAccountId).name,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    getPaymentAccountName(transfer.toAccountId),
                    getPaymentAccountType(transfer.toAccountId).name,
                    transfer.note.orEmpty()
                )
            )
        }
        return rows.joinToString("\n") { row ->
            row.joinToString(",") { csvEscape(it) }
        }
    }

    private fun parseCsvImport(csv: String): CsvImportData {
        val rows = parseCsvRows(csv, detectCsvDelimiter(csv)).filter { row -> row.any { it.isNotBlank() } }
        if (rows.size < 2) return CsvImportData()
        val headers = rows.first().map { it.trim().removePrefix("\uFEFF").lowercase(Locale.US) }
        fun rowValue(row: List<String>, name: String): String {
            val index = headers.indexOf(name)
            return if (index in row.indices) row[index].trim() else ""
        }

        val importData = CsvImportData()
        rows.drop(1).forEach { row ->
            val type = rowValue(row, "type").lowercase(Locale.US)
            val name = rowValue(row, "name").ifBlank { type.replace('_', ' ') }
            val amount = rowValue(row, "amount").replace(',', '.').toDoubleOrNull() ?: return@forEach
            val date = rowValue(row, "date")
            val period = rowValue(row, "period").lowercase(Locale.US).takeIf { it == "yearly" || it == "monthly" || it == "once" }
            val category = rowValue(row, "category").takeIf { it.isNotBlank() }
            val subcategory = rowValue(row, "subcategory").takeIf { it.isNotBlank() }
            val tags = parseTags(rowValue(row, "tags"))
            val accountId = resolveCsvAccountId(rowValue(row, "account"), rowValue(row, "account_type"))
            val toAccountId = resolveCsvAccountId(rowValue(row, "to_account"), rowValue(row, "to_account_type"))
            val remindersEnabled = rowValue(row, "reminders_enabled").toBooleanStrictOrNull() ?: true
            val reminderDays = rowValue(row, "reminder_days_before").toIntOrNull()?.let(::sanitizeReminderDays) ?: 1
            val paidDates = parseDateList(rowValue(row, "paid_dates"))
            val skippedDates = parseDateList(rowValue(row, "skipped_dates")) - paidDates.toSet()
            val subscriptionLifecycleStatus = normalizeSubscriptionLifecycleStatus(
                rowValue(row, "subscription_status").takeIf { it.isNotBlank() }
            )
            val subscriptionLifecycleDate = rowValue(row, "subscription_status_date")
                .takeIf { parseIsoDate(it) != null }
                .takeIf { subscriptionLifecycleStatus != SUBSCRIPTION_STATUS_ACTIVE }
                ?: isoDateWithOffset(0).takeIf { subscriptionLifecycleStatus != SUBSCRIPTION_STATUS_ACTIVE }
            val note = normalizeOptionalText(rowValue(row, "note"))

            when (type) {
                "income" -> importData.incomes.add(
                    Income(
                        name = name.ifBlank { getString(R.string.income_default_name) },
                        amount = amount,
                        date = date.ifBlank { isoDateFormatter().format(Date()) },
                        type = category ?: getString(R.string.income_other),
                        expectedDate = date.takeIf { it.isNotBlank() },
                        period = if (period == "monthly") "monthly" else "once",
                        accountId = accountId
                    )
                )
                "expense" -> importData.expenses.add(
                    Expense(
                        name = name.ifBlank { category ?: getString(R.string.expense_default_name) },
                        amount = amount,
                        date = date.ifBlank { isoDateFormatter().format(Date()) },
                        category = category,
                        accountId = accountId,
                        subcategory = subcategory,
                        tags = tags
                    )
                )
                "subscription" -> importData.subscriptions.add(
                    Subscription(
                        name = name.ifBlank { getString(R.string.subscription) },
                        amount = amount,
                        period = if (period == "yearly") "yearly" else "monthly",
                        nextChargeDate = date.takeIf { it.isNotBlank() },
                        accountId = accountId,
                        remindersEnabled = remindersEnabled,
                        reminderDaysBefore = reminderDays,
                        lifecycleStatus = subscriptionLifecycleStatus,
                        lifecycleDate = subscriptionLifecycleDate,
                        paidDates = paidDates,
                        skippedDates = skippedDates
                    )
                )
                "recurring_expense", "recurring" -> importData.recurringExpenses.add(
                    RecurringExpense(
                        name = name.ifBlank { category ?: getString(R.string.recurring_expense) },
                        amount = amount,
                        category = category,
                        period = if (period == "yearly") "yearly" else "monthly",
                        startDate = date.takeIf { it.isNotBlank() },
                        accountId = accountId,
                        remindersEnabled = remindersEnabled,
                        reminderDaysBefore = reminderDays,
                        paidDates = paidDates,
                        skippedDates = skippedDates,
                        subcategory = subcategory,
                        tags = tags
                    )
                )
                "transfer" -> if (accountId != toAccountId) {
                    importData.transfers.add(
                        AccountTransfer(
                            fromAccountId = accountId,
                            toAccountId = toAccountId,
                            amount = amount,
                            date = date.ifBlank { isoDateFormatter().format(Date()) },
                            note = note ?: name.takeIf { it.isNotBlank() && it != "transfer" }
                        )
                    )
                }
            }
        }
        return importData
    }

    private fun applyCsvImport(importData: CsvImportData) {
        incomes.addAll(importData.incomes.map(::sanitizeIncome))
        subscriptions.addAll(importData.subscriptions.map(::sanitizeSubscription))
        recurringExpenses.addAll(importData.recurringExpenses.map(::sanitizeRecurringExpense))
        expenses.addAll(importData.expenses.map(::sanitizeExpense))
        transfers.addAll(importData.transfers.map(::sanitizeTransfer))
        (importData.expenses.mapNotNull { it.category } + importData.recurringExpenses.mapNotNull { it.category })
            .forEach(::addCategoryIfNeeded)
        rememberTagsAndSubcategoriesFrom(importData.expenses, importData.recurringExpenses)
        populateItems()
        saveData()
        updateTotals()
        refreshPagedContent()
        Toast.makeText(this, R.string.csv_import_success, Toast.LENGTH_SHORT).show()
    }

    private fun csvEscape(value: String): String {
        val normalized = value.replace("\r\n", "\n").replace('\r', '\n')
        return if (normalized.any { it == ',' || it == '"' || it == '\n' }) {
            "\"${normalized.replace("\"", "\"\"")}\""
        } else {
            normalized
        }
    }

    private fun parseCsvRows(csv: String, delimiter: Char = ','): List<List<String>> {
        val rows = mutableListOf<MutableList<String>>()
        var row = mutableListOf<String>()
        val field = StringBuilder()
        var inQuotes = false
        var index = 0
        while (index < csv.length) {
            val char = csv[index]
            when {
                inQuotes && char == '"' && index + 1 < csv.length && csv[index + 1] == '"' -> {
                    field.append('"')
                    index++
                }
                char == '"' -> inQuotes = !inQuotes
                !inQuotes && char == delimiter -> {
                    row.add(field.toString())
                    field.clear()
                }
                !inQuotes && (char == '\n' || char == '\r') -> {
                    if (char == '\r' && index + 1 < csv.length && csv[index + 1] == '\n') index++
                    row.add(field.toString())
                    field.clear()
                    rows.add(row)
                    row = mutableListOf()
                }
                else -> field.append(char)
            }
            index++
        }
        row.add(field.toString())
        rows.add(row)
        return rows
    }

    private fun detectCsvDelimiter(csv: String): Char {
        val firstLine = csv.lineSequence().firstOrNull { it.isNotBlank() }.orEmpty()
        val commaCount = countDelimiterOutsideQuotes(firstLine, ',')
        val semicolonCount = countDelimiterOutsideQuotes(firstLine, ';')
        return if (semicolonCount > commaCount) ';' else ','
    }

    private fun countDelimiterOutsideQuotes(line: String, delimiter: Char): Int {
        var count = 0
        var inQuotes = false
        var index = 0
        while (index < line.length) {
            val char = line[index]
            when {
                inQuotes && char == '"' && index + 1 < line.length && line[index + 1] == '"' -> index++
                char == '"' -> inQuotes = !inQuotes
                !inQuotes && char == delimiter -> count++
            }
            index++
        }
        return count
    }

    private fun resolveCsvAccountId(name: String, typeValue: String): Long {
        val safeName = name.trim()
        if (safeName.isBlank()) return DEFAULT_CASH_ACCOUNT_ID
        paymentAccounts.firstOrNull { it.name.equals(safeName, ignoreCase = true) }?.let { return it.id }
        return paymentAccounts.firstOrNull { it.type == parseCsvAccountType(typeValue) }?.id ?: DEFAULT_CASH_ACCOUNT_ID
    }

    private fun parseCsvAccountType(value: String): AccountType {
        return runCatching { AccountType.valueOf(value.trim().uppercase(Locale.US)) }.getOrNull()
            ?: when (value.trim().lowercase(Locale.US)) {
                "cash", "bar", "наличные" -> AccountType.CASH
                "paypal", "wallet", "e-wallet" -> AccountType.PAYPAL
                "bank_account", "bank account" -> AccountType.BANK_ACCOUNT
                "card", "bank_card", "bank card" -> AccountType.BANK_CARD
                else -> AccountType.BANK_CARD
            }
    }

    private fun parseDateList(value: String): List<String> {
        return value.split("|", ";")
            .map { it.trim() }
            .filter { parseIsoDate(it) != null }
            .distinct()
    }

    private fun filterCsvImportDuplicates(importData: CsvImportData): CsvImportData {
        val filtered = CsvImportData()

        val incomeKeys = incomes.map(::incomeImportKey).toMutableSet()
        importData.incomes.map(::sanitizeIncome).forEach { income ->
            if (incomeKeys.add(incomeImportKey(income))) filtered.incomes.add(income)
        }

        val subscriptionKeys = subscriptions.map(::subscriptionImportKey).toMutableSet()
        importData.subscriptions.map(::sanitizeSubscription).forEach { subscription ->
            if (subscriptionKeys.add(subscriptionImportKey(subscription))) filtered.subscriptions.add(subscription)
        }

        val recurringKeys = recurringExpenses.map(::recurringExpenseImportKey).toMutableSet()
        importData.recurringExpenses.map(::sanitizeRecurringExpense).forEach { recurringExpense ->
            if (recurringKeys.add(recurringExpenseImportKey(recurringExpense))) filtered.recurringExpenses.add(recurringExpense)
        }

        val expenseKeys = expenses.map(::expenseImportKey).toMutableSet()
        importData.expenses.map(::sanitizeExpense).forEach { expense ->
            if (expenseKeys.add(expenseImportKey(expense))) filtered.expenses.add(expense)
        }

        val transferKeys = transfers.map(::transferImportKey).toMutableSet()
        importData.transfers.map(::sanitizeTransfer).forEach { transfer ->
            if (transferKeys.add(transferImportKey(transfer))) filtered.transfers.add(transfer)
        }

        return filtered
    }

    private fun buildCsvImportPreview(importData: CsvImportData, duplicateCount: Int): String {
        return getString(R.string.csv_import_confirm) + "\n\n" + buildImportPreviewText(
            incomeCount = importData.incomes.size,
            expenseCount = importData.expenses.size,
            subscriptionCount = importData.subscriptions.size,
            recurringCount = importData.recurringExpenses.size,
            transferCount = importData.transfers.size,
            duplicateCount = duplicateCount
        )
    }

    private fun buildBackupImportPreview(backup: MoneyManagerBackup): String {
        val safeIncomes = backup.incomes.orEmpty().map(::sanitizeIncome).distinctBy(::incomeImportKey)
        val safeSubscriptions = backup.subscriptions.orEmpty().map(::sanitizeSubscription).distinctBy(::subscriptionImportKey)
        val safeRecurring = backup.recurringExpenses.orEmpty().map(::sanitizeRecurringExpense).distinctBy(::recurringExpenseImportKey)
        val safeExpenses = backup.expenses.orEmpty().map(::sanitizeExpense).distinctBy(::expenseImportKey)
        val safeTransfers = backup.transfers.orEmpty().map(::sanitizeTransfer).distinctBy(::transferImportKey)
        val rawCount = backup.incomes.orEmpty().size +
            backup.subscriptions.orEmpty().size +
            backup.recurringExpenses.orEmpty().size +
            backup.expenses.orEmpty().size +
            backup.transfers.orEmpty().size
        val uniqueCount = safeIncomes.size + safeSubscriptions.size + safeRecurring.size + safeExpenses.size + safeTransfers.size

        return getString(R.string.backup_import_confirm) + "\n\n" + buildImportPreviewText(
            incomeCount = safeIncomes.size,
            expenseCount = safeExpenses.size,
            subscriptionCount = safeSubscriptions.size,
            recurringCount = safeRecurring.size,
            transferCount = safeTransfers.size,
            duplicateCount = rawCount - uniqueCount
        )
    }

    private fun buildImportPreviewText(
        incomeCount: Int,
        expenseCount: Int,
        subscriptionCount: Int,
        recurringCount: Int,
        transferCount: Int,
        duplicateCount: Int
    ): String {
        return getString(
            R.string.import_preview_summary,
            incomeCount,
            expenseCount,
            subscriptionCount,
            recurringCount,
            transferCount,
            duplicateCount.coerceAtLeast(0)
        )
    }

    private fun incomeImportKey(income: Income): String {
        return listOf(
            normalizeKeyPart(income.name),
            amountKey(income.amount),
            normalizeKeyPart(income.expectedDate ?: income.date),
            normalizeKeyPart(income.type),
            normalizeKeyPart(income.period),
            income.accountId.toString()
        ).joinToString("|")
    }

    private fun subscriptionImportKey(subscription: Subscription): String {
        return listOf(
            normalizeKeyPart(subscription.name),
            amountKey(subscription.amount),
            normalizeKeyPart(subscription.period),
            normalizeKeyPart(subscription.nextChargeDate),
            subscription.accountId.toString()
        ).joinToString("|")
    }

    private fun recurringExpenseImportKey(recurringExpense: RecurringExpense): String {
        return listOf(
            normalizeKeyPart(recurringExpense.name),
            amountKey(recurringExpense.amount),
            normalizeKeyPart(recurringExpense.category),
            normalizeKeyPart(recurringExpense.subcategory),
            normalizeKeyPart(recurringExpense.period),
            normalizeKeyPart(recurringExpense.startDate),
            recurringExpense.accountId.toString()
        ).joinToString("|")
    }

    private fun expenseImportKey(expense: Expense): String {
        return listOf(
            normalizeKeyPart(expense.name),
            amountKey(expense.amount),
            normalizeKeyPart(expense.date),
            normalizeKeyPart(expense.category),
            normalizeKeyPart(expense.subcategory),
            expense.accountId.toString()
        ).joinToString("|")
    }

    private fun transferImportKey(transfer: AccountTransfer): String {
        return listOf(
            transfer.fromAccountId.toString(),
            transfer.toAccountId.toString(),
            amountKey(transfer.amount),
            normalizeKeyPart(transfer.date),
            normalizeKeyPart(transfer.note)
        ).joinToString("|")
    }

    private fun normalizeKeyPart(value: String?): String {
        return value.orEmpty().trim().lowercase(Locale.getDefault())
    }

    private fun amountKey(value: Double): String {
        return String.format(Locale.US, "%.4f", value)
    }

    private fun createBackupData(): MoneyManagerBackup {
        return MoneyManagerBackup(
            exportedAt = isoDateFormatter().format(Date()),
            incomes = incomes,
            subscriptions = subscriptions,
            recurringExpenses = recurringExpenses,
            expenses = expenses,
            transfers = transfers,
            categories = categories,
            favoriteCategories = favoriteCategories.toList(),
            savedTags = getKnownTags(),
            savedSubcategories = getKnownSubcategories(),
            categoryBudgets = categoryBudgets,
            monthlyGoalAmount = monthlyGoalAmount,
            monthlyGoalIncludeExpenses = monthlyGoalIncludeExpenses,
            monthlyGoalIncludeSubscriptions = monthlyGoalIncludeSubscriptions,
            monthlyGoalCategories = monthlyGoalCategories.toList(),
            paymentAccounts = paymentAccounts,
            nextPaymentAccountId = nextPaymentAccountId,
            cashAmount = cashAmount,
            language = getSavedLanguageCode(),
            theme = getSavedTheme(),
            currency = getSavedCurrency()
        )
    }

    private fun applyBackupData(backup: MoneyManagerBackup) {
        paymentAccounts.clear()
        paymentAccounts.addAll(backup.paymentAccounts.orEmpty().map(::sanitizePaymentAccount).distinctBy { it.id })
        ensureDefaultPaymentAccounts()
        nextPaymentAccountId = backup.nextPaymentAccountId
            .coerceAtLeast((paymentAccounts.maxOfOrNull { it.id } ?: 0L) + 1L)

        incomes.clear()
        subscriptions.clear()
        recurringExpenses.clear()
        expenses.clear()
        transfers.clear()
        categories.clear()
        categoryBudgets.clear()
        monthlyGoalCategories.clear()
        favoriteCategories.clear()
        savedTags.clear()
        savedSubcategories.clear()

        categories.addAll(backup.categories.orEmpty().map { it.trim() }.filter { it.isNotEmpty() }.distinct())
        favoriteCategories.addAll(backup.favoriteCategories.orEmpty().filter { categories.contains(it) }.distinct())
        categoryBudgets.addAll(
            backup.categoryBudgets.orEmpty()
                .filter { it.amount > 0.0 && categories.contains(it.category) }
                .distinctBy { it.category }
        )
        monthlyGoalAmount = backup.monthlyGoalAmount
        monthlyGoalIncludeExpenses = backup.monthlyGoalIncludeExpenses
        monthlyGoalIncludeSubscriptions = backup.monthlyGoalIncludeSubscriptions
        monthlyGoalCategories.addAll(backup.monthlyGoalCategories.orEmpty().filter { categories.contains(it) }.distinct())

        incomes.addAll(backup.incomes.orEmpty().map(::sanitizeIncome).distinctBy(::incomeImportKey))
        subscriptions.addAll(backup.subscriptions.orEmpty().map(::sanitizeSubscription).distinctBy(::subscriptionImportKey))
        recurringExpenses.addAll(backup.recurringExpenses.orEmpty().map(::sanitizeRecurringExpense).distinctBy(::recurringExpenseImportKey))
        expenses.addAll(backup.expenses.orEmpty().map(::sanitizeExpense).distinctBy(::expenseImportKey))
        transfers.addAll(backup.transfers.orEmpty().map(::sanitizeTransfer).distinctBy(::transferImportKey))
        savedTags.addAll(backup.savedTags.orEmpty().flatMap { parseTags(it) })
        savedSubcategories.addAll(backup.savedSubcategories.orEmpty().mapNotNull(::normalizeOptionalText))
        rememberTagsAndSubcategoriesFrom(expenses, recurringExpenses)

        saveLanguageCode(backup.language.takeIf { it in listOf("en", "ru", "de") } ?: "en")
        saveTheme(backup.theme.takeIf { it in listOf(THEME_MINIMAL, THEME_HELL) } ?: THEME_MINIMAL)
        saveCurrency(backup.currency.takeIf { it in listOf("EUR", "USD") } ?: "EUR")
        saveData()
        populateItems()
        updateTotals()
        refreshPagedContent()
        Toast.makeText(this, R.string.backup_import_success, Toast.LENGTH_SHORT).show()
    }

    private fun bindHomePageViews() {
        tvRemaining = homePageView.findViewById(R.id.tvRemaining)
        tvCashTotal = homePageView.findViewById(R.id.tvCashTotal)
        tvIncomeTotal = homePageView.findViewById(R.id.tvIncomeTotal)
        tvSubscriptionsTotal = homePageView.findViewById(R.id.tvSubscriptionsTotal)
        tvExpensesTotal = homePageView.findViewById(R.id.tvExpensesTotal)
        tvHomeNetTotal = homePageView.findViewById(R.id.tvHomeNetTotal)
        layoutHomeBudgetSummary = homePageView.findViewById(R.id.layoutHomeBudgetSummary)
        tvHomeBudgetSummaryValue = homePageView.findViewById(R.id.tvHomeBudgetSummaryValue)
        tvHomeBudgetRemainingLabel = homePageView.findViewById(R.id.tvHomeBudgetRemainingLabel)
        tvHomeBudgetRemainingValue = homePageView.findViewById(R.id.tvHomeBudgetRemainingValue)
        tvHomeBudgetAlerts = homePageView.findViewById(R.id.tvHomeBudgetAlerts)
        progressHomeBudget = homePageView.findViewById(R.id.progressHomeBudget)
        layoutHomeMonthlyGoal = homePageView.findViewById(R.id.layoutHomeMonthlyGoal)
        tvHomeMonthlyGoalValue = homePageView.findViewById(R.id.tvHomeMonthlyGoalValue)
        tvHomeMonthlyGoalMeta = homePageView.findViewById(R.id.tvHomeMonthlyGoalMeta)
        progressHomeMonthlyGoal = homePageView.findViewById(R.id.progressHomeMonthlyGoal)
        tvHomeMonthLabel = homePageView.findViewById(R.id.tvHomeMonthLabel)
        btnHomePreviousMonth = homePageView.findViewById(R.id.btnHomePreviousMonth)
        btnHomeNextMonth = homePageView.findViewById(R.id.btnHomeNextMonth)
        btnHomeAccountPicker = homePageView.findViewById(R.id.btnHomeAccountPicker)
        btnHomeBalancesVisibility = homePageView.findViewById(R.id.btnHomeBalancesVisibility)
        etHomeSearch = homePageView.findViewById(R.id.etHomeSearch)
        btnFabAddEntry = homePageView.findViewById(R.id.btnFabAddEntry)
        tabFilters = homePageView.findViewById(R.id.tabFilters)
        viewPagerItems = homePageView.findViewById(R.id.viewPagerItems)
        updateHomeMonthLabel()
        btnHomeAccountPicker.text = getCalendarFilterLabel(selectedHomeAccount) + " \u25BC"
        layoutHomeBudgetSummary.setOnClickListener {
            viewPagerSections.currentItem = 1
        }
        layoutHomeMonthlyGoal.setOnClickListener {
            showMonthlyGoalDialog()
        }
        addPressAnimation(layoutHomeBudgetSummary)
        addPressAnimation(layoutHomeMonthlyGoal)
        addPressAnimation(btnHomePreviousMonth)
        addPressAnimation(btnHomeNextMonth)
        addPressAnimation(btnHomeAccountPicker)
        addPressAnimation(btnHomeBalancesVisibility)
        addPressAnimation(btnFabAddEntry)
        etHomeSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                homeSearchQuery = s?.toString()?.trim().orEmpty()
                if (::pagerAdapter.isInitialized) {
                    pagerAdapter.notifyDataSetChanged()
                    updateTabViews()
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
        btnHomePreviousMonth.setOnClickListener { changeHomeDisplayedMonthBy(-1) }
        btnHomeNextMonth.setOnClickListener { changeHomeDisplayedMonthBy(1) }
        tvHomeMonthLabel.setOnClickListener { showHomeMonthPicker() }
        btnHomeAccountPicker.setOnClickListener { showHomeAccountPickerDialog() }
        btnHomeBalancesVisibility.setOnClickListener {
            balancesHidden = !balancesHidden
            updateBalanceVisibilityUi()
        }
        btnFabAddEntry.setOnClickListener { showAddEntryMenuDialog() }
    }

    private fun updateHomeMonthLabel() {
        val monthTitle = SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(homeDisplayedMonth.time)
        tvHomeMonthLabel.text = monthTitle.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    private fun changeHomeDisplayedMonthBy(offset: Int) {
        homeDisplayedMonth.add(Calendar.MONTH, offset)
        populateItems()
        updateHomeMonthLabel()
        updateTotals()
        refreshPagedContent()
    }

    private fun showHomeMonthPicker() {
        val calendar = homeDisplayedMonth.clone() as Calendar
        DatePickerDialog(
            this,
            { _, year, month, _ ->
                homeDisplayedMonth.set(Calendar.YEAR, year)
                homeDisplayedMonth.set(Calendar.MONTH, month)
                homeDisplayedMonth.set(Calendar.DAY_OF_MONTH, 1)
                populateItems()
                updateHomeMonthLabel()
                updateTotals()
                refreshPagedContent()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun changeAnalyticsDisplayedMonthBy(offset: Int) {
        analyticsDisplayedMonth.add(Calendar.MONTH, offset)
        updateAnalyticsPage()
    }

    private fun bindCalendarPageViews() {
        tvCalendarMonth = calendarPageView.findViewById(R.id.tvCalendarMonth)
        tvCalendarBalanceLabel = calendarPageView.findViewById(R.id.tvCalendarBalanceLabel)
        tvCalendarBalanceValue = calendarPageView.findViewById(R.id.tvCalendarBalanceValue)
        tvCalendarMonthStats = calendarPageView.findViewById(R.id.tvCalendarMonthStats)
        tvCalendarBudgetStats = calendarPageView.findViewById(R.id.tvCalendarBudgetStats)
        tvCalendarMonthlyGoalStats = calendarPageView.findViewById(R.id.tvCalendarMonthlyGoalStats)
        layoutCalendarDaySummary = calendarPageView.findViewById(R.id.layoutCalendarDaySummary)
        tvCalendarSelectedDayTitle = calendarPageView.findViewById(R.id.tvCalendarSelectedDayTitle)
        tvCalendarSelectedDayEvents = calendarPageView.findViewById(R.id.tvCalendarSelectedDayEvents)
        tvCalendarSelectedDayAmount = calendarPageView.findViewById(R.id.tvCalendarSelectedDayAmount)
        btnCalendarAccountPicker = calendarPageView.findViewById(R.id.btnCalendarAccountPicker)
        btnCalendarOpenDay = calendarPageView.findViewById(R.id.btnCalendarOpenDay)
        gridCalendarDays = calendarPageView.findViewById(R.id.gridCalendarDays)
        addPressAnimation(layoutCalendarDaySummary)
        addPressAnimation(btnCalendarAccountPicker)
        addPressAnimation(btnCalendarOpenDay)
        layoutCalendarDaySummary.setOnClickListener { showCalendarDayDialog(selectedCalendarDateIso) }
        btnCalendarAccountPicker.setOnClickListener { showCalendarAccountPickerDialog() }
        btnCalendarOpenDay.setOnClickListener { showCalendarDayDialog(selectedCalendarDateIso) }
        calendarPageView.findViewById<LinearLayout>(R.id.layoutCalendarWeekHeader)
            .setOnTouchListener(createCalendarSwipeTouchListener())
        val btnPreviousMonth = calendarPageView.findViewById<View>(R.id.btnPreviousMonth)
        val btnNextMonth = calendarPageView.findViewById<View>(R.id.btnNextMonth)
        val btnCalendarToday = calendarPageView.findViewById<View>(R.id.btnCalendarToday)
        addPressAnimation(btnPreviousMonth)
        addPressAnimation(btnNextMonth)
        addPressAnimation(btnCalendarToday)
        btnPreviousMonth.setOnClickListener {
            changeDisplayedMonthBy(-1)
        }
        btnNextMonth.setOnClickListener {
            changeDisplayedMonthBy(1)
        }
        btnCalendarToday.setOnClickListener {
            showTodayInCalendar()
        }
    }

    private fun setupCalendarAccountFilter() {
        btnCalendarAccountPicker.text = getCalendarAccountButtonText()
    }

    private fun bindCategoriesPageViews() {
        layoutCategoriesPageList = categoriesPageView.findViewById(R.id.layoutCategoriesPageList)
        tvEmptyCategoriesPage = categoriesPageView.findViewById(R.id.tvEmptyCategoriesPage)
        btnAssetsAccountsTab = categoriesPageView.findViewById(R.id.btnAssetsAccountsTab)
        btnAssetsBudgetsTab = categoriesPageView.findViewById(R.id.btnAssetsBudgetsTab)
        btnAddAccountPage = categoriesPageView.findViewById(R.id.btnAddAccountPage)
        btnAddCategoryPage = categoriesPageView.findViewById(R.id.btnAddCategoryPage)
        btnTransferPage = categoriesPageView.findViewById(R.id.btnTransferPage)
        addPressAnimation(btnAssetsAccountsTab)
        addPressAnimation(btnAssetsBudgetsTab)
        addPressAnimation(btnAddAccountPage)
        addPressAnimation(btnAddCategoryPage)
        addPressAnimation(btnTransferPage)
        btnAssetsAccountsTab.setOnClickListener {
            selectedAssetsTab = ASSETS_TAB_ACCOUNTS
            updateCategoriesPage()
        }
        btnAssetsBudgetsTab.setOnClickListener {
            selectedAssetsTab = ASSETS_TAB_BUDGETS
            updateCategoriesPage()
        }
        btnAddAccountPage.setOnClickListener {
            showPaymentAccountDialog()
        }
        btnAddCategoryPage.setOnClickListener {
            showAddCategoryDialog()
        }
        btnTransferPage.setOnClickListener {
            showTransferDialog()
        }
    }

    private fun bindAnalyticsPageViews() {
        btnAnalyticsAccountPicker = analyticsPageView.findViewById(R.id.btnAnalyticsAccountPicker)
        tvAnalyticsMonth = analyticsPageView.findViewById(R.id.tvAnalyticsMonth)
        tvAnalyticsTotal = analyticsPageView.findViewById(R.id.tvAnalyticsTotal)
        tvAnalyticsDynamicsSummary = analyticsPageView.findViewById(R.id.tvAnalyticsDynamicsSummary)
        tvAnalyticsEmptyState = analyticsPageView.findViewById(R.id.tvAnalyticsEmptyState)
        tvAnalyticsTopTitle = analyticsPageView.findViewById(R.id.tvAnalyticsTopTitle)
        layoutAnalyticsBudgetAttention = analyticsPageView.findViewById(R.id.layoutAnalyticsBudgetAttention)
        tvAnalyticsBudgetAttention = analyticsPageView.findViewById(R.id.tvAnalyticsBudgetAttention)
        chartAnalyticsCategories = analyticsPageView.findViewById(R.id.chartAnalyticsCategories)
        chartAnalyticsMonthlyTrend = analyticsPageView.findViewById(R.id.chartAnalyticsMonthlyTrend)
        layoutAnalyticsTopCategories = analyticsPageView.findViewById(R.id.layoutAnalyticsTopCategories)
        btnAnalyticsCategoryDetails = analyticsPageView.findViewById(R.id.btnAnalyticsCategoryDetails)
        btnAnalyticsTrendDetails = analyticsPageView.findViewById(R.id.btnAnalyticsTrendDetails)
        val btnPreviousMonth = analyticsPageView.findViewById<View>(R.id.btnAnalyticsPreviousMonth)
        val btnNextMonth = analyticsPageView.findViewById<View>(R.id.btnAnalyticsNextMonth)
        btnAnalyticsAccountPicker.text = getAnalyticsAccountButtonText()
        addPressAnimation(btnAnalyticsAccountPicker)
        addPressAnimation(btnAnalyticsCategoryDetails)
        addPressAnimation(btnAnalyticsTrendDetails)
        addPressAnimation(btnPreviousMonth)
        addPressAnimation(btnNextMonth)
        btnAnalyticsAccountPicker.setOnClickListener { showAnalyticsAccountPickerDialog() }
        btnAnalyticsCategoryDetails.setOnClickListener { showAnalyticsCategoryDetailsDialog() }
        btnAnalyticsTrendDetails.setOnClickListener { showAnalyticsTrendDetailsDialog() }
        btnPreviousMonth.setOnClickListener {
            changeAnalyticsDisplayedMonthBy(-1)
        }
        btnNextMonth.setOnClickListener {
            changeAnalyticsDisplayedMonthBy(1)
        }
    }

    private fun bindSettingsPageViews() {
        tvPageLanguageValue = settingsPageView.findViewById(R.id.tvPageLanguageValue)
        tvPageThemeValue = settingsPageView.findViewById(R.id.tvPageThemeValue)
        tvPageCurrencyValue = settingsPageView.findViewById(R.id.tvPageCurrencyValue)
        tvPageAccountsValue = settingsPageView.findViewById(R.id.tvPageAccountsValue)
        tvPageAppLockValue = settingsPageView.findViewById(R.id.tvPageAppLockValue)
        tvPageTagsValue = settingsPageView.findViewById(R.id.tvPageTagsValue)
        settingsPageView.findViewById<View>(R.id.rowPageLanguage).setOnClickListener { showLanguagePickerDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageTheme).setOnClickListener { showThemePickerDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageCurrency).setOnClickListener { showCurrencyPickerDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageAccounts).setOnClickListener { showManageAccountsDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageAppLock).setOnClickListener { showAppLockSettingsDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageTags).setOnClickListener { showManageTagsDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageExportBackup).setOnClickListener { exportBackup() }
        settingsPageView.findViewById<View>(R.id.rowPageImportBackup).setOnClickListener { importBackup() }
        settingsPageView.findViewById<View>(R.id.rowPageExportCsv).setOnClickListener { exportCsv() }
        settingsPageView.findViewById<View>(R.id.rowPageImportCsv).setOnClickListener { importCsv() }
    }

    private fun setupSectionPager() {
        sectionPagerAdapter = StaticPagesAdapter(
            listOf(homePageView, categoriesPageView, calendarPageView, analyticsPageView, settingsPageView)
        )
        viewPagerSections.adapter = sectionPagerAdapter
        viewPagerSections.offscreenPageLimit = 5
        viewPagerSections.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateSectionTabs(position)
            }
        })
        updateSectionTabs(0)
    }

    private fun updateSectionTabs(position: Int) {
        styleSectionTab(navHomeContent, tvSectionHome, ivSectionHome, position == 0)
        styleSectionTab(navCategoriesContent, tvSectionCategories, ivSectionCategories, position == 1)
        styleSectionTab(navCalendarContent, tvSectionCalendar, ivSectionCalendar, position == 2)
        styleSectionTab(navAnalyticsContent, tvSectionAnalytics, ivSectionAnalytics, position == 3)
        styleSectionTab(navSettingsContent, tvSectionSettings, ivSectionSettings, position == 4)
    }

    private fun updateSettingsPageValues() {
        tvPageLanguageValue.text = getCurrentLanguageLabel()
        tvPageThemeValue.text = getCurrentThemeLabel()
        tvPageCurrencyValue.text = getCurrentCurrencyLabel()
        tvPageAccountsValue.text = getAccountManagementSummary()
        tvPageAppLockValue.text = if (isAppLockEnabled()) getString(R.string.enabled) else getString(R.string.disabled)
        tvPageTagsValue.text = getString(R.string.tags_summary, getKnownTags().size, getKnownSubcategories().size)
    }

    private fun updateCalendarBadge() {
        val todayIso = isoDateFormatter().format(Date())
        val count = getEventsForDate(todayIso).size
        tvSectionCalendarBadge.visibility = if (count > 0) View.VISIBLE else View.GONE
        if (count > 0) {
            tvSectionCalendarBadge.text = count.coerceAtMost(9).toString()
        }
    }

    private fun addPressAnimation(view: View) {
        view.setOnTouchListener { pressedView, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> pressedView.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(80L)
                    .start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> pressedView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120L)
                    .start()
            }
            false
        }
    }

    private fun createSpinnerAdapter(values: List<String>): ArrayAdapter<String> {
        return SpinnerOptionAdapter(values)
    }

    private inner class SpinnerOptionAdapter(values: List<String>) : ArrayAdapter<String>(
        this@MainActivity,
        R.layout.item_spinner_selected,
        R.id.tvSpinnerText,
        values
    ) {
        init {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            view.findViewById<View>(R.id.spinnerDivider)?.visibility =
                if (position == count - 1) View.GONE else View.VISIBLE
            return view
        }
    }

    private fun styleSectionTab(container: LinearLayout, label: TextView, icon: ImageView, isSelected: Boolean) {
        container.background = getDrawable(
            if (isSelected) R.drawable.bg_bottom_nav_item_selected else R.drawable.bg_bottom_nav_item_unselected
        )
        val textColor = getColor(if (isSelected) R.color.home_mint else R.color.text_secondary)
        label.setTextColor(textColor)
        icon.imageTintList = ColorStateList.valueOf(textColor)
        container.alpha = if (isSelected) 1f else 0.9f
    }

    private fun setupPager() {
        pagerAdapter = ItemsPagerAdapter(
            getItemsForTab = ::getFilteredItems,
            onAddEntry = ::showAddEntryMenuDialog,
            onItemLongClick = ::openItemEditor,
            amountFormatter = ::formatItemAmount,
            itemMarkerFormatter = ::getItemMarker
        )
        viewPagerItems.adapter = pagerAdapter
        tabMediator?.detach()
        tabMediator = TabLayoutMediator(tabFilters, viewPagerItems) { tab, position ->
            tab.customView = createTabView(position)
        }
        tabMediator?.attach()
        tabFilters.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = updateTabViews()
            override fun onTabUnselected(tab: TabLayout.Tab) = updateTabViews()
            override fun onTabReselected(tab: TabLayout.Tab) = updateTabViews()
        })
        viewPagerItems.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabViews()
            }
        })
        updateTabViews()
    }

    private fun createTabView(position: Int): View {
        val tabView = LayoutInflater.from(this).inflate(R.layout.item_filter_tab, tabFilters, false)
        val tvTitle = tabView.findViewById<TextView>(R.id.tvTabTitle)
        val tvCount = tabView.findViewById<TextView>(R.id.tvTabCount)
        tvTitle.text = getTabTitle(position)
        tvCount.text = getFilteredItems(position).size.toString()
        tvCount.backgroundTintList = ColorStateList.valueOf(getTabDotColor(position))
        updateTabViewStyle(tabView, position == viewPagerItems.currentItem)
        return tabView
    }

    private fun updateTabViews() {
        for (index in 0 until tabFilters.tabCount) {
            val tab = tabFilters.getTabAt(index) ?: continue
            val tabView = tab.customView ?: continue
            val tvTitle = tabView.findViewById<TextView>(R.id.tvTabTitle)
            val tvCount = tabView.findViewById<TextView>(R.id.tvTabCount)
            tvTitle.text = getTabTitle(index)
            tvCount.text = getFilteredItems(index).size.toString()
            tvCount.backgroundTintList = ColorStateList.valueOf(getTabDotColor(index))
            updateTabViewStyle(tabView, index == viewPagerItems.currentItem)
        }
    }

    private fun updateTabViewStyle(tabView: View, isSelected: Boolean) {
        val root = tabView as LinearLayout
        val tvTitle = tabView.findViewById<TextView>(R.id.tvTabTitle)
        val tvCount = tabView.findViewById<TextView>(R.id.tvTabCount)
        root.background = getDrawable(
            if (isSelected) R.drawable.bg_tab_chip_selected else R.drawable.bg_tab_chip_unselected
        )
        tvTitle.setTextColor(getColor(if (isSelected) R.color.home_dark_text else R.color.text_secondary))
        tvCount.alpha = if (isSelected) 1f else 0.82f
    }

    private fun getTabDotColor(position: Int): Int {
        return getColor(R.color.tab_badge)
    }

    private fun getTabTitle(position: Int): String {
        return when (position) {
            TAB_INCOME -> getString(R.string.income_items)
            TAB_SUBSCRIPTIONS -> getString(R.string.subscriptions)
            TAB_EXPENSES -> getString(R.string.expenses)
            else -> getString(R.string.all_items)
        }
    }

    private fun openItemEditor(item: Item) {
        when (item.type) {
            ItemType.INCOME -> showIncomeDialog(item.sourceIndex)
            ItemType.SUBSCRIPTION -> showEditSubscriptionDialog(item.sourceIndex)
            ItemType.RECURRING_EXPENSE -> showRecurringExpenseDialog(item.sourceIndex)
            ItemType.EXPENSE -> showExpenseDialog(item.sourceIndex)
            ItemType.TRANSFER -> showTransferDialog(item.sourceIndex)
        }
    }

    private fun getFilteredItems(position: Int): List<Item> {
        val visibleItems = getHomeSearchFilteredItems()
        return when (position) {
            TAB_INCOME -> visibleItems.filter { it.type == ItemType.INCOME }
            TAB_SUBSCRIPTIONS -> visibleItems.filter { it.type == ItemType.SUBSCRIPTION }
            TAB_EXPENSES -> visibleItems.filter { it.type == ItemType.EXPENSE || it.type == ItemType.RECURRING_EXPENSE }
            else -> visibleItems
        }
    }

    private fun getHomeVisibleItems(): List<Item> {
        return items.filter(::matchesHomeItemFilter)
    }

    private fun getHomeSearchFilteredItems(): List<Item> {
        return getHomeVisibleItems().filter(::matchesHomeSearchFilter)
    }

    private fun matchesHomeSearchFilter(item: Item): Boolean {
        val query = homeSearchQuery.lowercase(Locale.getDefault())
        if (query.isBlank()) return true

        val searchableText = listOf(
            item.name,
            item.meta,
            item.trailing,
            formatAmount(abs(item.amount)),
            formatSignedAmount(item.amount)
        )
            .plus(getHomeItemSearchExtras(item))
            .joinToString(" ")
            .lowercase(Locale.getDefault())

        return searchableText.contains(query)
    }

    private fun getHomeItemSearchExtras(item: Item): List<String> {
        return when (item.type) {
            ItemType.INCOME -> {
                val income = incomes.getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(income.accountId) + listOf(income.date, income.expectedDate.orEmpty(), income.type)
            }
            ItemType.SUBSCRIPTION -> {
                val subscription = subscriptions.getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(subscription.accountId) + listOf(
                    subscription.period,
                    subscription.nextChargeDate.orEmpty(),
                    getSubscriptionLifecycleLabel(subscription.lifecycleStatus),
                    subscription.lifecycleDate.orEmpty()
                )
            }
            ItemType.RECURRING_EXPENSE -> {
                val recurringExpense = recurringExpenses.getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(recurringExpense.accountId) + listOf(
                    recurringExpense.period,
                    recurringExpense.startDate.orEmpty(),
                    recurringExpense.category.orEmpty(),
                    recurringExpense.subcategory.orEmpty()
                ) + recurringExpense.tags
            }
            ItemType.EXPENSE -> {
                val expense = expenses.getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(expense.accountId) + listOf(
                    expense.date,
                    expense.category.orEmpty(),
                    expense.subcategory.orEmpty()
                ) + expense.tags
            }
            ItemType.TRANSFER -> {
                val transfer = transfers.getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(transfer.fromAccountId) +
                    getAccountSearchLabels(transfer.toAccountId) +
                    listOf(transfer.date, transfer.note.orEmpty())
            }
        }
    }

    private fun getAccountSearchLabels(accountId: Long): List<String> {
        val type = getPaymentAccountType(accountId)
        return listOf(
            getPaymentAccountName(accountId),
            getAccountTypeLabel(type),
            type.name
        )
    }

    private fun matchesHomeItemFilter(item: Item): Boolean {
        return when (item.type) {
            ItemType.INCOME -> {
                val income = incomes.getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(income.accountId, selectedHomeAccount)) return false
                if (income.period == "monthly") {
                    monthContainsIncomeOccurrence(income, homeDisplayedMonth)
                } else {
                    val date = parseIsoDate(income.expectedDate ?: income.date) ?: return true
                    isSameMonth(date, homeDisplayedMonth)
                }
            }
            ItemType.SUBSCRIPTION -> {
                val subscription = subscriptions.getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(subscription.accountId, selectedHomeAccount)) return false
                if (subscription.nextChargeDate.isNullOrBlank()) {
                    true
                } else {
                    monthContainsSubscriptionOccurrence(subscription, homeDisplayedMonth)
                }
            }
            ItemType.RECURRING_EXPENSE -> {
                val recurringExpense = recurringExpenses.getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(recurringExpense.accountId, selectedHomeAccount)) return false
                if (recurringExpense.startDate.isNullOrBlank()) {
                    true
                } else {
                    monthContainsRecurringExpenseOccurrence(recurringExpense, homeDisplayedMonth)
                }
            }
            ItemType.EXPENSE -> {
                val expense = expenses.getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(expense.accountId, selectedHomeAccount)) return false
                val date = parseIsoDate(expense.date) ?: return false
                isSameMonth(date, homeDisplayedMonth)
            }
            ItemType.TRANSFER -> {
                val transfer = transfers.getOrNull(item.sourceIndex) ?: return false
                if (!matchesTransferAccountFilter(transfer, selectedHomeAccount)) return false
                val date = parseIsoDate(transfer.date) ?: return false
                isSameMonth(date, homeDisplayedMonth)
            }
        }
    }

    private fun refreshPagedContent() {
        populateItems()
        updateTotals()
        pagerAdapter.notifyDataSetChanged()
        updateTabViews()
        updateHomeMonthLabel()
        updateHomeBudgetSummary()
        updateHomeMonthlyGoal()
        updateCategoriesPage()
        btnHomeAccountPicker.text = "${getCalendarFilterLabel(selectedHomeAccount)} \u25BC"
        btnCalendarAccountPicker.text = getCalendarAccountButtonText()
        btnAnalyticsAccountPicker.text = getAnalyticsAccountButtonText()
        updateCalendarPage()
        updateAnalyticsPage()
        updateSettingsPageValues()
        updateCalendarBadge()
    }

    private fun updateCategoriesPage() {
        layoutCategoriesPageList.removeAllViews()
        tvEmptyCategoriesPage.visibility = View.GONE
        ensureDefaultPaymentAccounts()
        updateAssetsTabControls()

        if (selectedAssetsTab == ASSETS_TAB_BUDGETS) {
            addCategoriesPageSection(getString(R.string.categories_section_budgets))
            categories.forEachIndexed { index, category ->
                addCategoryBudgetRow(category, index)
            }
            if (categories.isEmpty()) {
                addCategoriesPageEmptyAction(
                    text = getString(R.string.assets_empty_budgets),
                    buttonText = getString(R.string.create_budget)
                ) {
                    showAddCategoryDialog()
                }
            }
            return
        }

        addAssetsSummaryCard()

        val cashAccounts = paymentAccounts
            .filter { it.type == AccountType.CASH }
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
        if (cashAccounts.isNotEmpty()) {
            addCategoriesPageSection(getString(R.string.categories_section_cash))
            cashAccounts.forEach { account ->
                addAssetsPageAccountRow(account) {
                    showAccountDetailsDialog(account.id)
                }
            }
        }

        val cardAndWalletAccounts = paymentAccounts
            .filter { isCardType(it.type) || isWalletType(it.type) }
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
        if (cardAndWalletAccounts.isNotEmpty()) {
            addCategoriesPageSection(getString(R.string.categories_section_cards_wallets))
            cardAndWalletAccounts.forEach { account ->
                addAssetsPageAccountRow(account) {
                    showAccountDetailsDialog(account.id)
                }
            }
        }
    }

    private fun updateAnalyticsPage() {
        val monthTitle = SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(analyticsDisplayedMonth.time)
        tvAnalyticsMonth.text = monthTitle.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        updateAnalyticsBudgetAttention()

        val categoryTotals = getAnalyticsCategoryTotals(analyticsDisplayedMonth, selectedAnalyticsAccount)
        val total = categoryTotals.sumOf { it.amount }
        tvAnalyticsTotal.text = getString(R.string.analytics_total_expenses, formatAmount(total))
        chartAnalyticsCategories.setCenterText(formatAmount(total))
        chartAnalyticsCategories.setSlices(
            categoryTotals.map {
                CategoryPieChartView.Slice(
                    label = it.category,
                    amount = it.amount,
                    color = it.color,
                    valueText = formatCompactAmount(it.amount)
                )
            }
        )

        layoutAnalyticsTopCategories.removeAllViews()
        val hasCategoryTotals = categoryTotals.isNotEmpty()
        tvAnalyticsEmptyState.visibility = if (hasCategoryTotals) View.GONE else View.VISIBLE
        tvAnalyticsTopTitle.visibility = View.GONE
        layoutAnalyticsTopCategories.visibility = View.GONE
        btnAnalyticsCategoryDetails.isEnabled = hasCategoryTotals
        btnAnalyticsCategoryDetails.alpha = if (hasCategoryTotals) 1f else 0.45f

        updateAnalyticsMonthlyTrend(selectedAnalyticsAccount)
    }

    private fun updateAnalyticsMonthlyTrend(accountFilter: String) {
        val points = getAnalyticsMonthlyTrendPoints(analyticsDisplayedMonth, accountFilter)
        chartAnalyticsMonthlyTrend.setPoints(points)
        chartAnalyticsMonthlyTrend.setSelectedIndex(points.lastIndex)
        val hasTrendData = points.any {
            abs(it.income) >= 0.005 || abs(it.outgoing) >= 0.005 || abs(it.net) >= 0.005
        }
        btnAnalyticsTrendDetails.isEnabled = hasTrendData
        btnAnalyticsTrendDetails.alpha = if (hasTrendData) 1f else 0.45f
        val incomeTotal = points.sumOf { it.income }
        val outgoingTotal = points.sumOf { it.outgoing }
        val netTotal = incomeTotal - outgoingTotal
        tvAnalyticsDynamicsSummary.text = getString(
            R.string.analytics_dynamics_summary,
            formatAmount(incomeTotal),
            formatAmount(outgoingTotal),
            formatSignedAmount(netTotal)
        )
    }

    private fun getAnalyticsMonthlyTrendPoints(
        endMonth: Calendar,
        accountFilter: String
    ): List<MonthlyTrendChartView.MonthPoint> {
        val end = (endMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return (5 downTo 0).map { offset ->
            val month = (end.clone() as Calendar).apply {
                add(Calendar.MONTH, -offset)
            }
            val events = getEventsForMonth(month, accountFilter)
            val incomeTotal = events
                .filter { it.type == ItemType.INCOME }
                .sumOf { it.amount }
            val outgoingTotal = events
                .filter { it.type == ItemType.EXPENSE || it.type == ItemType.SUBSCRIPTION || it.type == ItemType.RECURRING_EXPENSE }
                .sumOf { it.amount }
            MonthlyTrendChartView.MonthPoint(
                label = SimpleDateFormat("MMM", Locale.getDefault()).format(month.time).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                },
                income = incomeTotal,
                outgoing = outgoingTotal,
                net = incomeTotal - outgoingTotal,
                incomeText = formatCompactAmount(incomeTotal),
                outgoingText = formatCompactAmount(outgoingTotal),
                netText = formatCompactSignedTotal(incomeTotal - outgoingTotal)
            )
        }
    }

    private fun getAnalyticsMonthlyTrendDetails(
        endMonth: Calendar,
        accountFilter: String
    ): List<MonthlyTrendDetail> {
        val end = (endMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return (5 downTo 0).map { offset ->
            val month = (end.clone() as Calendar).apply {
                add(Calendar.MONTH, -offset)
            }
            val events = getEventsForMonth(month, accountFilter)
            val income = events
                .filter { it.type == ItemType.INCOME }
                .sumOf { it.amount }
            val expensesTotal = events
                .filter { it.type == ItemType.EXPENSE }
                .sumOf { it.amount }
            val subscriptionsTotal = events
                .filter { it.type == ItemType.SUBSCRIPTION }
                .sumOf { it.amount }
            val recurringTotal = events
                .filter { it.type == ItemType.RECURRING_EXPENSE }
                .sumOf { it.amount }
            val outgoing = expensesTotal + subscriptionsTotal + recurringTotal
            val net = income - outgoing
            MonthlyTrendDetail(
                label = SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(month.time).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                },
                income = income,
                expenses = expensesTotal,
                subscriptions = subscriptionsTotal,
                recurring = recurringTotal,
                outgoing = outgoing,
                net = net
            )
        }
    }

    private fun showAnalyticsTrendDetailsDialog() {
        val details = getAnalyticsMonthlyTrendDetails(analyticsDisplayedMonth, selectedAnalyticsAccount)
        if (details.none { it.hasMoney }) return

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            background = getDrawable(R.drawable.bg_home_balance_card)
        }
        val totalIncome = details.sumOf { it.income }
        val totalOutgoing = details.sumOf { it.outgoing }
        content.addView(TextView(this).apply {
            text = getString(R.string.analytics_trend_details)
            setTextColor(getColor(R.color.text_primary))
            textSize = 20f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        content.addView(TextView(this).apply {
            text = getString(
                R.string.analytics_dynamics_summary,
                formatAmount(totalIncome),
                formatAmount(totalOutgoing),
                formatSignedAmount(totalIncome - totalOutgoing)
            )
            setTextColor(getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(0, dpToPx(4), 0, dpToPx(4))
        })

        details.forEach { detail ->
            content.addView(createAnalyticsTrendDetailRow(detail))
        }

        val root = android.widget.ScrollView(this).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = true
            addView(content)
        }
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(root)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun createAnalyticsTrendDetailRow(detail: MonthlyTrendDetail): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_home_panel)
            setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(10)
            }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(TextView(this).apply {
            text = detail.label
            setTextColor(getColor(R.color.text_primary))
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(TextView(this).apply {
            text = formatSignedAmount(detail.net)
            setTextColor(getColor(if (detail.net >= 0.0) R.color.amount_positive else R.color.amount_negative))
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        row.addView(header)

        addAnalyticsTrendAmountLine(row, getString(R.string.analytics_legend_income), detail.income, R.color.amount_positive)
        addAnalyticsTrendAmountLine(row, getString(R.string.analytics_legend_outgoing), detail.outgoing, R.color.amount_negative)
        row.addView(TextView(this).apply {
            text = getString(
                R.string.analytics_trend_outgoing_breakdown,
                formatAmount(detail.expenses),
                formatAmount(detail.subscriptions),
                formatAmount(detail.recurring)
            )
            setTextColor(getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(0, dpToPx(4), 0, 0)
        })
        return row
    }

    private fun addAnalyticsTrendAmountLine(container: LinearLayout, label: String, value: Double, colorRes: Int) {
        container.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dpToPx(8), 0, 0)
            addView(TextView(this@MainActivity).apply {
                text = label
                setTextColor(getColor(colorRes))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })
            addView(TextView(this@MainActivity).apply {
                text = formatAmount(value)
                setTextColor(getColor(R.color.text_primary))
                textSize = 13f
            })
        })
    }

    private fun getAnalyticsCategoryTotals(month: Calendar, accountFilter: String): List<CategoryExpenseTotal> {
        val previousMonth = (month.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        val previousTotals = getAnalyticsCategoryAmountMap(previousMonth, accountFilter)
        return getAnalyticsCategoryAmountMap(month, accountFilter)
            .toList()
            .filter { it.second > 0.0 }
            .sortedByDescending { it.second }
            .mapIndexed { index, (category, amount) ->
                CategoryExpenseTotal(
                    category = category,
                    amount = amount,
                    previousAmount = previousTotals[category] ?: 0.0,
                    color = getAnalyticsSliceColor(index)
                )
            }
    }

    private fun getAnalyticsCategoryAmountMap(month: Calendar, accountFilter: String): Map<String, Double> {
        val totals = mutableMapOf<String, Double>()
        getEventsForMonth(month, accountFilter)
            .filter { event ->
                event.type == ItemType.EXPENSE ||
                    event.type == ItemType.RECURRING_EXPENSE ||
                    event.type == ItemType.SUBSCRIPTION
            }
            .filter { it.amount > 0.0 }
            .forEach { event ->
                val category = getAnalyticsCategoryLabel(event) ?: return@forEach
                totals[category] = (totals[category] ?: 0.0) + event.amount
            }
        return totals
    }

    private fun getAnalyticsCategoryLabel(event: CalendarEvent): String? {
        return when (event.type) {
            ItemType.EXPENSE -> expenses.getOrNull(event.sourceIndex)
                ?.let { buildCategoryLabel(it.category, it.subcategory) }
            ItemType.RECURRING_EXPENSE -> recurringExpenses.getOrNull(event.sourceIndex)
                ?.let { buildCategoryLabel(it.category, it.subcategory) }
            ItemType.SUBSCRIPTION -> getString(R.string.subscriptions)
            else -> null
        }
    }

    private fun addAnalyticsCategoryRow(categoryTotal: CategoryExpenseTotal, total: Double) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(0, dpToPx(7), 0, dpToPx(7))
        }

        val swatch = View(this).apply {
            background = ColorDrawable(categoryTotal.color)
            layoutParams = LinearLayout.LayoutParams(dpToPx(10), dpToPx(10)).apply {
                marginEnd = dpToPx(10)
            }
        }

        val textGroup = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val title = TextView(this).apply {
            text = categoryTotal.category
            setTextColor(getColor(R.color.text_primary))
            textSize = 14f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val comparison = TextView(this).apply {
            text = getAnalyticsCategoryComparisonText(categoryTotal.delta)
            setTextColor(getColor(getAnalyticsCategoryComparisonColor(categoryTotal.delta)))
            textSize = 12f
            setPadding(0, dpToPx(2), 0, 0)
        }

        val percent = if (total > 0.0) {
            (categoryTotal.amount / total * 100.0).toInt()
        } else {
            0
        }
        val amount = TextView(this).apply {
            text = getString(R.string.analytics_category_line, formatAmount(categoryTotal.amount), percent)
            setTextColor(getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(dpToPx(8), 0, 0, 0)
        }

        textGroup.addView(title)
        textGroup.addView(comparison)
        row.addView(swatch)
        row.addView(textGroup)
        row.addView(amount)
        layoutAnalyticsTopCategories.addView(row)
    }

    private fun showAnalyticsCategoryDetailsDialog() {
        val categoryTotals = getAnalyticsCategoryTotals(analyticsDisplayedMonth, selectedAnalyticsAccount)
        if (categoryTotals.isEmpty()) return

        val total = categoryTotals.sumOf { it.amount }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            background = getDrawable(R.drawable.bg_home_balance_card)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(TextView(this).apply {
            text = getString(R.string.analytics_category_details)
            setTextColor(getColor(R.color.text_primary))
            textSize = 20f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(TextView(this).apply {
            text = formatAmount(total)
            setTextColor(getColor(R.color.text_secondary))
            textSize = 14f
        })
        content.addView(header)

        categoryTotals.forEach { categoryTotal ->
            content.addView(createAnalyticsCategoryDetailRow(categoryTotal, total))
        }

        val root = android.widget.ScrollView(this).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = true
            addView(content)
        }
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(root)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun createAnalyticsCategoryDetailRow(categoryTotal: CategoryExpenseTotal, total: Double): View {
        val percent = if (total > 0.0) {
            (categoryTotal.amount / total * 1000.0).toInt()
        } else {
            0
        }
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dpToPx(12), 0, dpToPx(4))
        }
        val topLine = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        topLine.addView(View(this).apply {
            background = ColorDrawable(categoryTotal.color)
            layoutParams = LinearLayout.LayoutParams(dpToPx(10), dpToPx(10)).apply {
                marginEnd = dpToPx(10)
            }
        })
        topLine.addView(TextView(this).apply {
            text = categoryTotal.category
            setTextColor(getColor(R.color.text_primary))
            textSize = 14f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        topLine.addView(TextView(this).apply {
            text = getString(R.string.analytics_category_line, formatAmount(categoryTotal.amount), percent / 10)
            setTextColor(getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(dpToPx(8), 0, 0, 0)
        })
        row.addView(topLine)

        row.addView(ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 1000
            progress = percent.coerceIn(0, 1000)
            progressTintList = ColorStateList.valueOf(categoryTotal.color)
            progressBackgroundTintList = ColorStateList.valueOf(getColor(R.color.divider_soft))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(5)
            ).apply {
                topMargin = dpToPx(7)
            }
        })

        row.addView(TextView(this).apply {
            text = getAnalyticsCategoryComparisonText(categoryTotal.delta)
            setTextColor(getColor(getAnalyticsCategoryComparisonColor(categoryTotal.delta)))
            textSize = 12f
            setPadding(dpToPx(20), dpToPx(5), 0, 0)
        })
        return row
    }

    private fun getAnalyticsCategoryComparisonText(delta: Double): String {
        return when {
            abs(delta) < 0.005 -> getString(R.string.analytics_category_delta_same)
            delta > 0.0 -> getString(R.string.analytics_category_delta_up, formatAmount(delta))
            else -> getString(R.string.analytics_category_delta_down, formatAmount(abs(delta)))
        }
    }

    private fun getAnalyticsCategoryComparisonColor(delta: Double): Int {
        return when {
            abs(delta) < 0.005 -> R.color.text_secondary
            delta > 0.0 -> R.color.amount_negative
            else -> R.color.amount_positive
        }
    }

    private fun getAnalyticsSliceColor(index: Int): Int {
        val colors = intArrayOf(
            getColor(R.color.calendar_accent),
            getColor(R.color.amount_negative),
            getColor(R.color.budget_warning),
            getColor(R.color.avatar_subscription_end),
            getColor(R.color.avatar_subscription_start),
            getColor(R.color.home_blue),
            Color.rgb(255, 174, 126),
            Color.rgb(201, 184, 255),
            Color.rgb(186, 230, 253),
            getColor(R.color.text_secondary)
        )
        return colors[index % colors.size]
    }

    private data class CategoryExpenseTotal(
        val category: String,
        val amount: Double,
        val previousAmount: Double,
        val color: Int
    ) {
        val delta: Double
            get() = amount - previousAmount
    }

    private data class MonthlyTrendDetail(
        val label: String,
        val income: Double,
        val expenses: Double,
        val subscriptions: Double,
        val recurring: Double,
        val outgoing: Double,
        val net: Double
    ) {
        val hasMoney: Boolean
            get() = abs(income) >= 0.005 ||
                abs(expenses) >= 0.005 ||
                abs(subscriptions) >= 0.005 ||
                abs(recurring) >= 0.005 ||
                abs(net) >= 0.005
    }

    private fun addCategoriesPageSection(title: String) {
        val label = TextView(this).apply {
            text = title
            setTextColor(getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(dpToPx(8), dpToPx(14), dpToPx(8), dpToPx(6))
        }
        layoutCategoriesPageList.addView(label)
    }

    private fun updateAssetsTabControls() {
        val accountsSelected = selectedAssetsTab == ASSETS_TAB_ACCOUNTS
        btnAssetsAccountsTab.background = getDrawable(
            if (accountsSelected) R.drawable.bg_tab_chip_selected else R.drawable.bg_tab_chip_unselected
        )
        btnAssetsBudgetsTab.background = getDrawable(
            if (accountsSelected) R.drawable.bg_tab_chip_unselected else R.drawable.bg_tab_chip_selected
        )
        btnAssetsAccountsTab.setTextColor(getColor(if (accountsSelected) R.color.home_dark_text else R.color.text_primary))
        btnAssetsBudgetsTab.setTextColor(getColor(if (accountsSelected) R.color.text_primary else R.color.home_dark_text))
        btnAddAccountPage.visibility = if (accountsSelected) View.VISIBLE else View.GONE
        btnTransferPage.visibility = if (accountsSelected) View.VISIBLE else View.GONE
        btnAddCategoryPage.visibility = if (accountsSelected) View.GONE else View.VISIBLE
        (btnTransferPage.layoutParams as? LinearLayout.LayoutParams)?.let { params ->
            params.marginStart = if (accountsSelected) dpToPx(8) else 0
            btnTransferPage.layoutParams = params
        }
        (btnAddCategoryPage.layoutParams as? LinearLayout.LayoutParams)?.let { params ->
            params.marginStart = if (accountsSelected) dpToPx(8) else 0
            btnAddCategoryPage.layoutParams = params
        }
    }

    private fun addAssetsSummaryCard() {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_settings_row)
            setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
                bottomMargin = dpToPx(8)
            }
        }

        val label = TextView(this).apply {
            text = getString(R.string.assets_total_balance)
            setTextColor(getColor(R.color.text_secondary))
            textSize = 13f
        }

        val total = TextView(this).apply {
            text = formatAssetAmount(getCurrentBalanceForFilter(FILTER_ALL))
            setTextColor(getColor(R.color.text_primary))
            textSize = 24f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, dpToPx(4), 0, 0)
        }

        val groups = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(12), 0, dpToPx(2))
        }
        groups.addView(
            createAssetGroupTile(
                getString(R.string.assets_group_cash),
                getCurrentBalanceForFilter(FILTER_GROUP_CASH)
            )
        )
        groups.addView(
            createAssetGroupTile(
                getString(R.string.assets_group_cards),
                getCurrentBalanceForFilter(FILTER_GROUP_CARDS)
            )
        )
        groups.addView(
            createAssetGroupTile(
                getString(R.string.assets_group_wallets),
                getCurrentBalanceForFilter(FILTER_GROUP_WALLETS)
            )
        )

        val meta = TextView(this).apply {
            text = if (paymentAccounts.isEmpty()) {
                getString(R.string.assets_no_accounts)
            } else {
                getString(R.string.assets_accounts_count, paymentAccounts.size)
            }
            setTextColor(getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(0, dpToPx(4), 0, 0)
        }

        row.addView(label)
        row.addView(total)
        row.addView(groups)
        row.addView(meta)
        layoutCategoriesPageList.addView(row)
    }

    private fun createAssetGroupTile(labelValue: String, amountValue: Double): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                cornerRadius = dpToPx(14).toFloat()
                setColor(getColor(R.color.card_surface_alt))
                setStroke(dpToPx(1), getColor(R.color.home_line))
            }
            setPadding(dpToPx(10), dpToPx(8), dpToPx(10), dpToPx(8))
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = dpToPx(6)
            }
            addView(TextView(this@MainActivity).apply {
                text = labelValue
                setTextColor(getColor(R.color.text_secondary))
                textSize = 11f
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            })
            addView(TextView(this@MainActivity).apply {
                text = formatAssetAmount(amountValue)
                setTextColor(getColor(R.color.text_primary))
                textSize = 13f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, dpToPx(3), 0, 0)
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            })
        }
    }

    private fun formatAssetAmount(amount: Double): String {
        return if (balancesHidden) "****" else formatAmount(amount)
    }

    private fun getAssetAmountColor(amount: Double): Int {
        return if (balancesHidden) {
            R.color.text_primary
        } else if (amount >= 0.0) {
            R.color.amount_positive
        } else {
            R.color.amount_negative
        }
    }

    private fun addCategoryBudgetRow(category: String, categoryIndex: Int) {
        val budget = getCategoryBudget(category)
        val spent = getCategorySpentForMonth(category, homeDisplayedMonth)
        val progressRatio = if (budget != null && budget.amount > 0.0) {
            spent / budget.amount
        } else {
            0.0
        }
        val statusColor = if (budget != null) {
            getColor(getBudgetProgressColor(spent, budget.amount))
        } else {
            getColor(R.color.text_secondary)
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_settings_row)
            isClickable = true
            isFocusable = true
            setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
            setOnClickListener {
                showCategoryBudgetDialog(category)
            }
            setOnLongClickListener {
                showCategoryDialog(categoryIndex)
                true
            }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        val title = TextView(this).apply {
            text = getCategoryDisplayName(category)
            setTextColor(getColor(R.color.text_primary))
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        val status = TextView(this).apply {
            text = if (budget != null) {
                if (progressRatio > 1.0) {
                    getString(R.string.category_budget_over, formatAssetAmount(spent - budget.amount))
                } else {
                    "${(progressRatio * 100).toInt()}%"
                }
            } else {
                getString(R.string.set_budget)
            }
            setTextColor(statusColor)
            textSize = 13f
            maxLines = 1
            setPadding(dpToPx(8), 0, 0, 0)
        }
        header.addView(title)
        header.addView(status)
        row.addView(header)

        val summary = TextView(this).apply {
            text = if (budget != null) {
                getString(R.string.category_budget_progress, formatAssetAmount(spent), formatAssetAmount(budget.amount))
            } else {
                getString(R.string.category_budget_not_set)
            }
            setTextColor(getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(0, dpToPx(4), 0, 0)
        }
        row.addView(summary)

        if (budget != null) {
            val progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                isIndeterminate = false
                max = 1000
                this.progress = (progressRatio.coerceIn(0.0, 1.0) * 1000).toInt()
                progressTintList = ColorStateList.valueOf(statusColor)
                progressBackgroundTintList = ColorStateList.valueOf(getColor(R.color.divider_soft))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(6)
                ).apply {
                    topMargin = dpToPx(8)
                }
            }
            row.addView(progress)
        }

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(10), 0, 0)
        }
        actions.addView(
            createCategoryActionPill(
                textValue = if (budget == null) getString(R.string.set_budget) else getString(R.string.category_action_budget),
                primary = true
            ) {
                showCategoryBudgetDialog(category)
            }
        )
        actions.addView(
            createCategoryActionPill(
                textValue = getString(R.string.edit),
                primary = false
            ) {
                showCategoryDialog(categoryIndex)
            }
        )
        row.addView(actions)

        layoutCategoriesPageList.addView(row)
    }

    private fun createCategoryActionPill(
        textValue: String,
        primary: Boolean,
        onClick: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = textValue
            gravity = android.view.Gravity.CENTER
            minWidth = dpToPx(72)
            minHeight = dpToPx(48)
            setPadding(dpToPx(12), 0, dpToPx(12), 0)
            textSize = 12f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(getColor(if (primary) R.color.button_text_light else R.color.text_primary))
            background = GradientDrawable().apply {
                cornerRadius = dpToPx(16).toFloat()
                setColor(getColor(if (primary) R.color.button_dark else R.color.card_surface_alt))
                if (!primary) {
                    setStroke(dpToPx(1), getColor(R.color.home_line))
                }
            }
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dpToPx(48)
            ).apply {
                marginEnd = dpToPx(8)
            }
        }.also { addPressAnimation(it) }
    }

    private fun addAssetsPageAccountRow(account: PaymentAccount, onClick: () -> Unit) {
        val balance = getCurrentBalanceForAccount(account)
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            background = getDrawable(R.drawable.bg_settings_row)
            isClickable = true
            isFocusable = true
            setPadding(dpToPx(14), dpToPx(12), dpToPx(12), dpToPx(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
            setOnClickListener { onClick() }
            setOnLongClickListener {
                onClick()
                true
            }
        }
        addPressAnimation(row)

        val avatar = TextView(this).apply {
            text = getPaymentAccountIcon(account.id)
            textSize = 17f
            gravity = android.view.Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(getColor(getAccountAvatarColor(account.type)))
            }
            layoutParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40)).apply {
                marginEnd = dpToPx(12)
            }
        }

        val textGroup = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val title = TextView(this).apply {
            text = getPaymentAccountDisplayName(account)
            setTextColor(getColor(R.color.text_primary))
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val meta = TextView(this).apply {
            text = getAccountTypeLabel(account.type)
            setTextColor(getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(0, dpToPx(3), 0, 0)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val amount = TextView(this).apply {
            text = formatAssetAmount(balance)
            setTextColor(getColor(getAssetAmountColor(balance)))
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = android.view.Gravity.END
            setPadding(dpToPx(10), 0, dpToPx(6), 0)
            maxLines = 1
        }

        val chevron = ImageView(this).apply {
            setImageResource(R.drawable.ic_chevron_right)
            setColorFilter(getColor(R.color.text_secondary))
            contentDescription = getString(R.string.edit_account_name)
            layoutParams = LinearLayout.LayoutParams(dpToPx(18), dpToPx(18))
        }

        textGroup.addView(title)
        textGroup.addView(meta)
        row.addView(avatar)
        row.addView(textGroup)
        row.addView(amount)
        row.addView(chevron)
        layoutCategoriesPageList.addView(row)
    }

    private fun getAccountAvatarColor(type: AccountType): Int {
        return when (type) {
            AccountType.BANK_CARD,
            AccountType.BANK_ACCOUNT -> R.color.home_mint
            AccountType.CASH -> R.color.home_yellow
            AccountType.PAYPAL,
            AccountType.OTHER -> R.color.home_blue
        }
    }

    private fun showAccountDetailsDialog(accountId: Long) {
        val account = getPaymentAccountById(accountId) ?: return
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_home_balance_card)
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        val title = TextView(this).apply {
            text = getPaymentAccountDisplayName(account)
            setTextColor(getColor(R.color.text_primary))
            textSize = 18f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val meta = TextView(this).apply {
            text = getAccountTypeLabel(account.type)
            setTextColor(getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(0, dpToPx(4), 0, 0)
        }
        val balance = TextView(this).apply {
            text = formatAssetAmount(getCurrentBalanceForAccount(account))
            setTextColor(getColor(R.color.text_primary))
            textSize = 26f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, dpToPx(10), 0, 0)
        }

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(12), 0, dpToPx(14))
        }

        val dialog = BottomSheetDialog(this)
        actions.addView(
            createCategoryActionPill(getString(R.string.edit), primary = true) {
                dialog.dismiss()
                showPaymentAccountDialog(account.id)
            }
        )
        actions.addView(
            createCategoryActionPill(getString(R.string.add_transfer_menu), primary = false) {
                dialog.dismiss()
                showTransferDialog()
            }
        )

        val recentTitle = TextView(this).apply {
            text = getString(R.string.account_details_recent)
            setTextColor(getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(0, dpToPx(4), 0, dpToPx(6))
        }

        dialogView.addView(title)
        dialogView.addView(meta)
        dialogView.addView(balance)
        dialogView.addView(actions)
        dialogView.addView(recentTitle)

        val activity = getAccountActivityLines(account.id).take(5)
        if (activity.isEmpty()) {
            dialogView.addView(TextView(this).apply {
                text = getString(R.string.account_details_no_activity)
                setTextColor(getColor(R.color.text_secondary))
                textSize = 14f
                setPadding(0, dpToPx(4), 0, 0)
            })
        } else {
            activity.forEach { line ->
                dialogView.addView(createAccountActivityRow(line))
            }
        }

        dialog.setContentView(dialogView)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun createAccountActivityRow(line: AccountActivityLine): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(0, dpToPx(8), 0, dpToPx(8))
        }
        val textGroup = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        textGroup.addView(TextView(this).apply {
            text = line.title
            setTextColor(getColor(R.color.text_primary))
            textSize = 14f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        textGroup.addView(TextView(this).apply {
            text = line.meta
            setTextColor(getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(0, dpToPx(2), 0, 0)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        row.addView(textGroup)
        row.addView(TextView(this).apply {
            text = formatAssetAmount(line.amount)
            setTextColor(getColor(getAssetAmountColor(line.amount)))
            textSize = 13f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(dpToPx(10), 0, 0, 0)
        })
        return row
    }

    private fun getAccountActivityLines(accountId: Long): List<AccountActivityLine> {
        val lines = mutableListOf<AccountActivityLine>()
        incomes.filter { it.accountId == accountId }.forEach {
            lines.add(AccountActivityLine(it.name, formatDisplayDate(it.date), it.amount, it.date))
        }
        expenses.filter { it.accountId == accountId }.forEach {
            lines.add(AccountActivityLine(it.name, formatDisplayDate(it.date), -it.amount, it.date))
        }
        subscriptions.filter { it.accountId == accountId && !it.nextChargeDate.isNullOrBlank() }.forEach {
            val date = it.nextChargeDate.orEmpty()
            lines.add(AccountActivityLine(it.name, formatDisplayDate(date), -it.amount, date))
        }
        recurringExpenses.filter { it.accountId == accountId && !it.startDate.isNullOrBlank() }.forEach {
            val date = it.startDate.orEmpty()
            lines.add(AccountActivityLine(it.name, formatDisplayDate(date), -it.amount, date))
        }
        transfers.filter { it.fromAccountId == accountId || it.toAccountId == accountId }.forEach {
            val amount = when (accountId) {
                it.fromAccountId -> -it.amount
                it.toAccountId -> it.amount
                else -> 0.0
            }
            lines.add(
                AccountActivityLine(
                    title = it.note ?: getString(R.string.transfer),
                    meta = getString(
                        R.string.transfer_route,
                        getPaymentAccountName(it.fromAccountId),
                        getPaymentAccountName(it.toAccountId)
                    ),
                    amount = amount,
                    date = it.date
                )
            )
        }
        return lines.sortedByDescending { it.date }
    }

    private data class AccountActivityLine(
        val title: String,
        val meta: String,
        val amount: Double,
        val date: String
    )

    private fun addCategoriesPageEmptyText(text: String) {
        val label = TextView(this).apply {
            this.text = text
            setTextColor(getColor(R.color.text_secondary))
            textSize = 14f
            setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(12))
        }
        layoutCategoriesPageList.addView(label)
    }

    private fun addCategoriesPageEmptyAction(text: String, buttonText: String, onClick: () -> Unit) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_settings_row)
            setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }
        container.addView(TextView(this).apply {
            this.text = text
            setTextColor(getColor(R.color.text_secondary))
            textSize = 14f
        })
        container.addView(createCategoryActionPill(buttonText, primary = true, onClick = onClick).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dpToPx(48)
            ).apply {
                topMargin = dpToPx(10)
            }
        })
        layoutCategoriesPageList.addView(container)
    }

    private fun showCategoryBudgetDialog(category: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_category_budget, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCategoryBudgetTitle)
        val tvSpent = dialogView.findViewById<TextView>(R.id.tvCategoryBudgetSpent)
        val etAmount = dialogView.findViewById<EditText>(R.id.etCategoryBudgetAmount)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCategoryBudgetDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteCategoryBudgetDialog)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveCategoryBudgetDialog)
        val existingBudget = getCategoryBudget(category)

        tvTitle.text = getString(R.string.category_budget_title, category)
        tvSpent.text = getString(
            R.string.category_budget_spent,
            formatAmount(getCategorySpentForMonth(category, homeDisplayedMonth))
        )
        if (existingBudget != null) {
            etAmount.setText(existingBudget.amount.toString())
            etAmount.selectAll()
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
        }

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            categoryBudgets.removeAll { it.category == category }
            saveData()
            refreshPagedContent()
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().trim().replace(',', '.').toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                return@setOnClickListener
            }
            categoryBudgets.removeAll { it.category == category }
            categoryBudgets.add(CategoryBudget(category = category, amount = amount))
            saveData()
            refreshPagedContent()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
        focusAmountInput(etAmount)
    }

    private fun getCategoryBudget(category: String): CategoryBudget? {
        return categoryBudgets.firstOrNull { it.category == category && it.amount > 0.0 }
    }

    private fun getCategorySpentForMonth(
        category: String,
        month: Calendar,
        accountFilter: String = FILTER_ALL
    ): Double {
        val oneTimeTotal = expenses
            .filter { expense ->
                val expenseCategory = expense.category ?: getString(R.string.general_category)
                expenseCategory == category &&
                    matchesAccountFilter(expense.accountId, accountFilter) &&
                    isSameMonth(parseIsoDate(expense.date), month)
            }
            .sumOf { it.amount }
        val monthEvents = getCalendarEventsForMonth(month, accountFilter)
        val recurringTotal = recurringExpenses
            .withIndex()
            .filter { indexed ->
                val recurringCategory = indexed.value.category ?: getString(R.string.general_category)
                recurringCategory == category && matchesAccountFilter(indexed.value.accountId, accountFilter)
            }
            .sumOf { indexed ->
                monthEvents
                    .filter { event -> event.type == ItemType.RECURRING_EXPENSE && event.sourceIndex == indexed.index }
                    .sumOf { event -> event.amount }
            }
        return oneTimeTotal + recurringTotal
    }

    private fun getBudgetProgressColor(spent: Double, budget: Double): Int {
        val ratio = if (budget > 0.0) spent / budget else 0.0
        return when {
            ratio >= 0.9 -> R.color.amount_negative
            ratio >= 0.75 -> R.color.budget_warning
            else -> R.color.amount_positive
        }
    }

    private fun updateHomeBudgetSummary() {
        val statuses = getCategoryBudgetStatuses(homeDisplayedMonth, selectedHomeAccount)
        if (statuses.isEmpty()) {
            layoutHomeBudgetSummary.visibility = View.GONE
            return
        }

        val totalBudget = statuses.sumOf { it.budget.amount }
        val totalSpent = statuses.sumOf { it.spent }
        val remaining = totalBudget - totalSpent
        val progressRatio = if (totalBudget > 0.0) totalSpent / totalBudget else 0.0
        val progressColor = getColor(getBudgetProgressColor(totalSpent, totalBudget))

        layoutHomeBudgetSummary.visibility = View.VISIBLE
        tvHomeBudgetSummaryValue.text = getString(
            R.string.home_budget_summary,
            formatAmount(totalSpent),
            formatAmount(totalBudget)
        )
        progressHomeBudget.progress = (progressRatio.coerceIn(0.0, 1.0) * 1000).toInt()
        progressHomeBudget.progressTintList = ColorStateList.valueOf(progressColor)
        progressHomeBudget.progressBackgroundTintList = ColorStateList.valueOf(getColor(R.color.divider_soft))

        if (remaining >= -0.005) {
            tvHomeBudgetRemainingLabel.text = getString(R.string.home_budget_left)
            tvHomeBudgetRemainingValue.text = formatAmount(remaining.coerceAtLeast(0.0))
            tvHomeBudgetRemainingValue.setTextColor(progressColor)
        } else {
            tvHomeBudgetRemainingLabel.text = getString(R.string.home_budget_over)
            tvHomeBudgetRemainingValue.text = formatAmount(abs(remaining))
            tvHomeBudgetRemainingValue.setTextColor(getColor(R.color.amount_negative))
        }

        if (getBudgetAttentionStatuses(statuses).isEmpty()) {
            tvHomeBudgetAlerts.visibility = View.GONE
        } else {
            tvHomeBudgetAlerts.visibility = View.VISIBLE
            tvHomeBudgetAlerts.text = buildBudgetAttentionText(statuses, maxItems = 1)
            tvHomeBudgetAlerts.setTextColor(getColor(getBudgetAttentionColor(statuses)))
        }
    }

    private fun updateAnalyticsBudgetAttention() {
        if (selectedAnalyticsAccount != FILTER_ALL) {
            layoutAnalyticsBudgetAttention.visibility = View.GONE
            return
        }

        val statuses = getCategoryBudgetStatuses(analyticsDisplayedMonth, selectedAnalyticsAccount)
        if (statuses.isEmpty()) {
            layoutAnalyticsBudgetAttention.visibility = View.GONE
            return
        }

        layoutAnalyticsBudgetAttention.visibility = View.VISIBLE
        tvAnalyticsBudgetAttention.text = buildBudgetAttentionText(statuses, maxItems = 5)
        tvAnalyticsBudgetAttention.setTextColor(getColor(getBudgetAttentionColor(statuses)))
    }

    private fun buildBudgetAttentionText(statuses: List<CategoryBudgetStatus>, maxItems: Int): String {
        val attentionStatuses = getBudgetAttentionStatuses(statuses).take(maxItems)
        if (attentionStatuses.isEmpty()) {
            return getString(R.string.budget_attention_all_ok)
        }

        return attentionStatuses.joinToString("\n") { status ->
            if (status.isOver) {
                getString(
                    R.string.budget_attention_over,
                    status.budget.category,
                    formatAmount(abs(status.remaining))
                )
            } else {
                getString(
                    R.string.budget_attention_left,
                    status.budget.category,
                    formatAmount(status.remaining.coerceAtLeast(0.0))
                )
            }
        }
    }

    private fun getBudgetAttentionStatuses(statuses: List<CategoryBudgetStatus>): List<CategoryBudgetStatus> {
        return statuses.filter { it.ratio >= 0.75 }
    }

    private fun getBudgetAttentionColor(statuses: List<CategoryBudgetStatus>): Int {
        val attentionStatuses = getBudgetAttentionStatuses(statuses)
        return when {
            attentionStatuses.any { it.isOver } -> R.color.amount_negative
            attentionStatuses.isNotEmpty() -> R.color.budget_warning
            else -> R.color.text_secondary
        }
    }

    private fun getCategoryBudgetStatuses(
        month: Calendar,
        accountFilter: String = FILTER_ALL
    ): List<CategoryBudgetStatus> {
        return categoryBudgets
            .mapNotNull { budget ->
                if (budget.amount <= 0.0 || !categories.contains(budget.category)) {
                    null
                } else {
                    CategoryBudgetStatus(
                        budget = budget,
                        spent = getCategorySpentForMonth(budget.category, month, accountFilter)
                    )
                }
            }
            .sortedWith(
                compareByDescending<CategoryBudgetStatus> { it.ratio }
                    .thenBy { it.budget.category.lowercase(Locale.getDefault()) }
            )
    }

    private data class CategoryBudgetStatus(
        val budget: CategoryBudget,
        val spent: Double
    ) {
        val ratio: Double
            get() = if (budget.amount > 0.0) spent / budget.amount else 0.0

        val remaining: Double
            get() = budget.amount - spent

        val isOver: Boolean
            get() = remaining < -0.005
    }

    private fun updateHomeMonthlyGoal() {
        if (!hasMonthlyGoal()) {
            progressHomeMonthlyGoal.visibility = View.GONE
            tvHomeMonthlyGoalMeta.visibility = View.GONE
            tvHomeMonthlyGoalValue.text = getString(R.string.monthly_goal_set)
            tvHomeMonthlyGoalValue.setTextColor(getColor(R.color.text_secondary))
            tvHomeMonthlyGoalMeta.text = getString(R.string.monthly_goal_not_set)
            return
        }

        val spent = getMonthlyGoalSpentForMonth(homeDisplayedMonth)
        val remaining = monthlyGoalAmount - spent
        val progressRatio = spent / monthlyGoalAmount
        val progressColor = getColor(getBudgetProgressColor(spent, monthlyGoalAmount))

        progressHomeMonthlyGoal.visibility = View.VISIBLE
        tvHomeMonthlyGoalMeta.visibility = View.VISIBLE
        progressHomeMonthlyGoal.progress = (progressRatio.coerceIn(0.0, 1.0) * 1000).toInt()
        progressHomeMonthlyGoal.progressTintList = ColorStateList.valueOf(progressColor)
        progressHomeMonthlyGoal.progressBackgroundTintList = ColorStateList.valueOf(getColor(R.color.divider_soft))

        tvHomeMonthlyGoalValue.text = getString(
            R.string.monthly_goal_summary,
            formatAmount(spent),
            formatAmount(monthlyGoalAmount)
        )
        tvHomeMonthlyGoalValue.setTextColor(progressColor)
        tvHomeMonthlyGoalMeta.text = if (remaining >= -0.005) {
            getString(
                R.string.monthly_goal_left_with_sources,
                formatAmount(remaining.coerceAtLeast(0.0)),
                getMonthlyGoalSourcesLabel()
            )
        } else {
            getString(
                R.string.monthly_goal_over_with_sources,
                formatAmount(abs(remaining)),
                getMonthlyGoalSourcesLabel()
            )
        }
    }

    private fun showMonthlyGoalDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_monthly_goal, null)
        val etAmount = dialogView.findViewById<EditText>(R.id.etMonthlyGoalAmount)
        val cbExpenses = dialogView.findViewById<CheckBox>(R.id.cbMonthlyGoalExpenses)
        val cbSubscriptions = dialogView.findViewById<CheckBox>(R.id.cbMonthlyGoalSubscriptions)
        val tvCategoriesTitle = dialogView.findViewById<TextView>(R.id.tvMonthlyGoalCategoriesTitle)
        val layoutCategories = dialogView.findViewById<LinearLayout>(R.id.layoutMonthlyGoalCategories)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelMonthlyGoalDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteMonthlyGoalDialog)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveMonthlyGoalDialog)
        val categoryCheckboxes = mutableMapOf<String, CheckBox>()

        if (hasMonthlyGoal()) {
            etAmount.setText(monthlyGoalAmount.toString())
            etAmount.selectAll()
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
        }
        cbExpenses.isChecked = monthlyGoalIncludeExpenses
        cbSubscriptions.isChecked = monthlyGoalIncludeSubscriptions
        val availableCategories = categories.sortedBy { it.lowercase(Locale.getDefault()) }
        if (availableCategories.isEmpty()) {
            tvCategoriesTitle.visibility = View.GONE
            layoutCategories.visibility = View.GONE
        } else {
            tvCategoriesTitle.visibility = View.VISIBLE
            layoutCategories.visibility = View.VISIBLE
            availableCategories.forEach { category ->
                val checkbox = CheckBox(this).apply {
                    text = category
                    isChecked = monthlyGoalCategories.contains(category)
                    buttonTintList = ColorStateList.valueOf(getColor(R.color.calendar_accent))
                    setTextColor(getColor(R.color.text_primary))
                    textSize = 14f
                    setPadding(0, dpToPx(2), 0, dpToPx(2))
                }
                categoryCheckboxes[category] = checkbox
                layoutCategories.addView(checkbox)
            }
        }

        fun updateCategoryOptionsState() {
            val useSpecificCategories = !cbExpenses.isChecked
            tvCategoriesTitle.alpha = if (useSpecificCategories) 1f else 0.45f
            categoryCheckboxes.values.forEach { checkbox ->
                checkbox.isEnabled = useSpecificCategories
                checkbox.alpha = if (useSpecificCategories) 1f else 0.45f
            }
        }
        cbExpenses.setOnCheckedChangeListener { _, _ ->
            updateCategoryOptionsState()
        }
        updateCategoryOptionsState()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            monthlyGoalAmount = 0.0
            monthlyGoalCategories.clear()
            saveData()
            refreshPagedContent()
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().trim().replace(',', '.').toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                return@setOnClickListener
            }
            val selectedCategories = categoryCheckboxes
                .filter { it.value.isChecked }
                .keys
                .toSet()
            if (!cbExpenses.isChecked && selectedCategories.isEmpty() && !cbSubscriptions.isChecked) {
                Toast.makeText(this, R.string.monthly_goal_sources_error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            monthlyGoalAmount = amount
            monthlyGoalIncludeExpenses = cbExpenses.isChecked
            monthlyGoalIncludeSubscriptions = cbSubscriptions.isChecked
            monthlyGoalCategories.clear()
            monthlyGoalCategories.addAll(selectedCategories)
            saveData()
            refreshPagedContent()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
        focusAmountInput(etAmount)
    }

    private fun hasMonthlyGoal(): Boolean {
        return monthlyGoalAmount > 0.0
    }

    private fun getMonthlyGoalSpentForMonth(month: Calendar): Double {
        var total = 0.0
        val monthEvents = getCalendarEventsForMonth(month, FILTER_ALL)
        if (monthlyGoalIncludeExpenses) {
            total += expenses
                .filter { isSameMonth(parseIsoDate(it.date), month) }
                .sumOf { it.amount }
            total += monthEvents
                .filter { it.type == ItemType.RECURRING_EXPENSE }
                .sumOf { it.amount }
        } else if (monthlyGoalCategories.isNotEmpty()) {
            total += expenses
                .filter { expense ->
                    val expenseCategory = expense.category ?: getString(R.string.general_category)
                    monthlyGoalCategories.contains(expenseCategory) &&
                        isSameMonth(parseIsoDate(expense.date), month)
                }
                .sumOf { it.amount }
            total += recurringExpenses
                .withIndex()
                .filter { indexed ->
                    val recurringCategory = indexed.value.category ?: getString(R.string.general_category)
                    monthlyGoalCategories.contains(recurringCategory)
                }
                .sumOf { indexed ->
                    monthEvents
                        .filter { event -> event.type == ItemType.RECURRING_EXPENSE && event.sourceIndex == indexed.index }
                        .sumOf { event -> event.amount }
                }
        }
        if (monthlyGoalIncludeSubscriptions) {
            total += monthEvents
                .filter { it.type == ItemType.SUBSCRIPTION }
                .sumOf { it.amount }
        }
        return total
    }

    private fun getMonthlyGoalSourcesLabel(): String {
        val parts = mutableListOf<String>()
        if (monthlyGoalIncludeExpenses) {
            parts.add(getString(R.string.monthly_goal_sources_expenses))
        } else if (monthlyGoalCategories.isNotEmpty()) {
            parts.add(getString(R.string.monthly_goal_sources_categories, getMonthlyGoalCategoriesLabel()))
        }
        if (monthlyGoalIncludeSubscriptions) {
            parts.add(getString(R.string.monthly_goal_sources_subscriptions))
        }
        return if (parts.isEmpty()) {
            getString(R.string.monthly_goal_sources_none)
        } else {
            parts.joinToString(" + ")
        }
    }

    private fun getMonthlyGoalCategoriesLabel(): String {
        val selectedCategories = monthlyGoalCategories
            .filter { categories.contains(it) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
        return if (selectedCategories.size <= 2) {
            selectedCategories.joinToString(", ")
        } else {
            getString(R.string.monthly_goal_sources_categories_count, selectedCategories.size)
        }
    }

    private fun applySavedLocale() {
        val locale = Locale(getSavedLanguageCode())
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun applySavedTheme() {
        val nightMode = if (getSavedTheme() == THEME_HELL) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    private fun showLanguageChangeDialog(newLanguageCode: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_language_confirm, null)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelLanguageChange)
        val btnApply = dialogView.findViewById<Button>(R.id.btnApplyLanguageChange)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnApply.setOnClickListener {
            dialog.dismiss()
            saveLanguageCode(newLanguageCode)
            setLocale(newLanguageCode)
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
        val tvLanguageValue = dialogView.findViewById<TextView>(R.id.tvLanguageValue)
        val tvThemeValue = dialogView.findViewById<TextView>(R.id.tvThemeValue)
        val rowLanguage = dialogView.findViewById<View>(R.id.rowLanguage)
        val rowTheme = dialogView.findViewById<View>(R.id.rowTheme)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseSettings)

        tvLanguageValue.text = getCurrentLanguageLabel()
        tvThemeValue.text = getCurrentThemeLabel()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        rowLanguage.setOnClickListener {
            dialog.dismiss()
            showLanguagePickerDialog()
        }

        rowTheme.setOnClickListener {
            dialog.dismiss()
            showThemePickerDialog()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showLanguagePickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_language_picker, null)
        val rowEnglish = dialogView.findViewById<View>(R.id.rowEnglish)
        val rowRussian = dialogView.findViewById<View>(R.id.rowRussian)
        val rowGerman = dialogView.findViewById<View>(R.id.rowGerman)
        val tvEnglishState = dialogView.findViewById<TextView>(R.id.tvEnglishState)
        val tvRussianState = dialogView.findViewById<TextView>(R.id.tvRussianState)
        val tvGermanState = dialogView.findViewById<TextView>(R.id.tvGermanState)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseLanguagePicker)

        val currentLanguageCode = getSavedLanguageCode()
        tvEnglishState.text = if (currentLanguageCode == "en") getString(R.string.selected) else ""
        tvRussianState.text = if (currentLanguageCode == "ru") getString(R.string.selected) else ""
        tvGermanState.text = if (currentLanguageCode == "de") getString(R.string.selected) else ""

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        rowEnglish.setOnClickListener {
            dialog.dismiss()
            if (currentLanguageCode != "en") showLanguageChangeDialog("en")
        }

        rowRussian.setOnClickListener {
            dialog.dismiss()
            if (currentLanguageCode != "ru") showLanguageChangeDialog("ru")
        }

        rowGerman.setOnClickListener {
            dialog.dismiss()
            if (currentLanguageCode != "de") showLanguageChangeDialog("de")
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showThemePickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_theme_picker, null)
        val rowLight = dialogView.findViewById<View>(R.id.rowThemeLight)
        val rowDark = dialogView.findViewById<View>(R.id.rowThemeDark)
        val tvLightState = dialogView.findViewById<TextView>(R.id.tvThemeLightState)
        val tvDarkState = dialogView.findViewById<TextView>(R.id.tvThemeDarkState)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseThemePicker)

        val currentTheme = getSavedTheme()
        tvLightState.text = if (currentTheme == THEME_MINIMAL) getString(R.string.selected) else ""
        tvDarkState.text = if (currentTheme == THEME_HELL) getString(R.string.selected) else ""

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        rowLight.setOnClickListener {
            dialog.dismiss()
            updateTheme(THEME_MINIMAL)
        }

        rowDark.setOnClickListener {
            dialog.dismiss()
            updateTheme(THEME_HELL)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showCurrencyPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_theme_picker, null)
        val rowPrimary = dialogView.findViewById<View>(R.id.rowThemeLight)
        val rowSecondary = dialogView.findViewById<View>(R.id.rowThemeDark)
        val tvPrimaryState = dialogView.findViewById<TextView>(R.id.tvThemeLightState)
        val tvSecondaryState = dialogView.findViewById<TextView>(R.id.tvThemeDarkState)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseThemePicker)
        dialogView.findViewById<TextView>(R.id.tvThemePickerTitle).text = getString(R.string.settings_currency)
        dialogView.findViewById<TextView>(R.id.tvThemeLightLabel).text = getString(R.string.currency_eur)
        dialogView.findViewById<TextView>(R.id.tvThemeDarkLabel).text = getString(R.string.currency_usd)

        val currentCurrency = getSavedCurrency()
        tvPrimaryState.text = if (currentCurrency == "EUR") getString(R.string.selected) else ""
        tvSecondaryState.text = if (currentCurrency == "USD") getString(R.string.selected) else ""

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        rowPrimary.setOnClickListener {
            dialog.dismiss()
            updateCurrency("EUR")
        }
        rowSecondary.setOnClickListener {
            dialog.dismiss()
            updateCurrency("USD")
        }
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showAppLockSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_app_lock, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAppLockTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvAppLockMessage)
        val etCurrentPin = dialogView.findViewById<EditText>(R.id.etCurrentPin)
        val etNewPin = dialogView.findViewById<EditText>(R.id.etNewPin)
        val cbDeviceAuth = dialogView.findViewById<CheckBox>(R.id.cbDeviceAuth)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAppLock)
        val btnDisable = dialogView.findViewById<Button>(R.id.btnDisableAppLock)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveAppLock)
        val lockEnabled = isAppLockEnabled()
        val deviceAuthAvailable = isDeviceAuthAvailable()

        tvTitle.text = getString(R.string.app_lock_title)
        tvMessage.text = getString(R.string.app_lock_hint)
        etCurrentPin.visibility = if (lockEnabled) View.VISIBLE else View.GONE
        etNewPin.hint = if (lockEnabled) getString(R.string.new_pin_optional_hint) else getString(R.string.new_pin_hint)
        cbDeviceAuth.isEnabled = deviceAuthAvailable
        cbDeviceAuth.isChecked = lockEnabled && isDeviceAuthEnabled() && deviceAuthAvailable
        btnDisable.visibility = if (lockEnabled) View.VISIBLE else View.GONE

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnDisable.setOnClickListener {
            if (!isStoredPinValid(etCurrentPin.text.toString())) {
                Toast.makeText(this, R.string.pin_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            clearAppLock()
            appUnlocked = false
            updateSettingsPageValues()
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            val currentPin = etCurrentPin.text.toString()
            val newPin = etNewPin.text.toString()
            if (lockEnabled && !isStoredPinValid(currentPin)) {
                Toast.makeText(this, R.string.pin_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPin.isBlank() && !lockEnabled) {
                Toast.makeText(this, R.string.pin_too_short, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPin.isNotBlank() && newPin.length < 4) {
                Toast.makeText(this, R.string.pin_too_short, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val salt = if (newPin.isNotBlank()) generatePinSalt() else getStoredPinSalt()
            val hash = if (newPin.isNotBlank()) hashPin(newPin, salt) else getStoredPinHash()
            setAppLockValues(
                enabled = true,
                pinHash = hash,
                pinSalt = salt,
                deviceAuth = cbDeviceAuth.isChecked && deviceAuthAvailable
            )
            appUnlocked = true
            updateSettingsPageValues()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showUnlockDialog() {
        if (appUnlocked || unlockDialogShowing) return
        unlockDialogShowing = true

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_app_lock, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAppLockTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvAppLockMessage)
        val etCurrentPin = dialogView.findViewById<EditText>(R.id.etCurrentPin)
        val etNewPin = dialogView.findViewById<EditText>(R.id.etNewPin)
        val cbDeviceAuth = dialogView.findViewById<CheckBox>(R.id.cbDeviceAuth)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAppLock)
        val btnDeviceAuth = dialogView.findViewById<Button>(R.id.btnDisableAppLock)
        val btnUnlock = dialogView.findViewById<Button>(R.id.btnSaveAppLock)
        val canUseDeviceAuth = isDeviceAuthEnabled() && isDeviceAuthAvailable()

        tvTitle.text = getString(R.string.app_lock_unlock_title)
        tvMessage.text = getString(R.string.app_lock_unlock_message)
        etCurrentPin.hint = getString(R.string.current_pin_hint)
        etCurrentPin.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        etNewPin.visibility = View.GONE
        cbDeviceAuth.visibility = View.GONE
        btnCancel.visibility = View.GONE
        btnDeviceAuth.visibility = if (canUseDeviceAuth) View.VISIBLE else View.GONE
        btnDeviceAuth.text = getString(R.string.device_auth_unlock)
        btnUnlock.text = getString(R.string.app_lock_unlock)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        btnDeviceAuth.setOnClickListener {
            launchDeviceAuth()
            dialog.dismiss()
            unlockDialogShowing = false
        }
        btnUnlock.setOnClickListener {
            if (isStoredPinValid(etCurrentPin.text.toString())) {
                appUnlocked = true
                unlockDialogShowing = false
                dialog.dismiss()
            } else {
                Toast.makeText(this, R.string.pin_invalid, Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setOnDismissListener {
            if (!appUnlocked) unlockDialogShowing = false
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun launchDeviceAuth() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val intent = keyguardManager?.createConfirmDeviceCredentialIntent(
            getString(R.string.app_lock_unlock_title),
            getString(R.string.app_lock_unlock_message)
        )
        if (intent == null) {
            Toast.makeText(this, R.string.device_auth_unavailable, Toast.LENGTH_SHORT).show()
            return
        }
        deviceAuthLauncher.launch(intent)
    }

    private fun isDeviceAuthAvailable(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        return keyguardManager?.isKeyguardSecure == true
    }

    private fun isAppLockEnabled(): Boolean {
        if (!::moneyDao.isInitialized) return false
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return readStoredBoolean(prefs, KEY_APP_LOCK_ENABLED, false) && getStoredPinHash().isNotBlank()
    }

    private fun isDeviceAuthEnabled(): Boolean {
        if (!::moneyDao.isInitialized) return false
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return readStoredBoolean(prefs, KEY_APP_LOCK_DEVICE_AUTH, false)
    }

    private fun getStoredPinHash(): String {
        if (!::moneyDao.isInitialized) return ""
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return readStoredString(prefs, KEY_APP_LOCK_PIN_HASH, "")
    }

    private fun getStoredPinSalt(): String {
        if (!::moneyDao.isInitialized) return ""
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return readStoredString(prefs, KEY_APP_LOCK_PIN_SALT, "")
    }

    private fun isStoredPinValid(pin: String): Boolean {
        val salt = getStoredPinSalt()
        val hash = getStoredPinHash()
        return pin.isNotBlank() && salt.isNotBlank() && hashPin(pin, salt) == hash
    }

    private fun setAppLockValues(enabled: Boolean, pinHash: String, pinSalt: String, deviceAuth: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        storeBoolean(prefs, KEY_APP_LOCK_ENABLED, enabled)
        storeString(prefs, KEY_APP_LOCK_PIN_HASH, pinHash)
        storeString(prefs, KEY_APP_LOCK_PIN_SALT, pinSalt)
        storeBoolean(prefs, KEY_APP_LOCK_DEVICE_AUTH, deviceAuth)
        prefs.apply()
    }

    private fun clearAppLock() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        storeBoolean(prefs, KEY_APP_LOCK_ENABLED, false)
        storeString(prefs, KEY_APP_LOCK_PIN_HASH, "")
        storeString(prefs, KEY_APP_LOCK_PIN_SALT, "")
        storeBoolean(prefs, KEY_APP_LOCK_DEVICE_AUTH, false)
        prefs.apply()
    }

    private fun generatePinSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun hashPin(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("$salt:$pin".toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun getSavedLanguageCode(): String {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    private fun saveLanguageCode(languageCode: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, languageCode)
            .apply()
    }

    private fun getSavedTheme(): String {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_THEME, THEME_MINIMAL) ?: THEME_MINIMAL
    }

    private fun getSavedCurrency(): String {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_CURRENCY, "EUR") ?: "EUR"
    }

    private fun saveTheme(theme: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, theme)
            .apply()
    }

    private fun saveCurrency(currency: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_CURRENCY, currency)
            .apply()
    }

    private fun getLanguagePosition(languageCode: String): Int {
        return when (languageCode) {
            "ru" -> 1
            "de" -> 2
            else -> 0
        }
    }

    private fun getLanguageCodeForPosition(position: Int): String {
        return when (position) {
            1 -> "ru"
            2 -> "de"
            else -> "en"
        }
    }

    private fun getCurrentLanguageLabel(): String {
        return when (getSavedLanguageCode()) {
            "ru" -> getString(R.string.russian)
            "de" -> getString(R.string.german)
            else -> getString(R.string.english)
        }
    }

    private fun getCurrentThemeLabel(): String {
        return if (getSavedTheme() == THEME_HELL) {
            getString(R.string.theme_dark)
        } else {
            getString(R.string.theme_light)
        }
    }

    private fun getCurrentCurrencyLabel(): String {
        return if (getSavedCurrency() == "USD") {
            getString(R.string.currency_usd)
        } else {
            getString(R.string.currency_eur)
        }
    }

    private fun getCalendarAccountButtonText(): String {
        return "${getCalendarFilterLabel(selectedCalendarAccount)} \u25BC"
    }

    private fun getAnalyticsAccountButtonText(): String {
        return "${getCalendarFilterLabel(selectedAnalyticsAccount)} \u25BC"
    }

    private fun getAccountManagementSummary(): String {
        ensureDefaultPaymentAccounts()
        return paymentAccounts.joinToString(" · ") { getPaymentAccountDisplayName(it) }
    }

    private fun ensureDefaultPaymentAccounts() {
        if (paymentAccounts.isNotEmpty()) return
        paymentAccounts.add(
            PaymentAccount(
                id = DEFAULT_CASH_ACCOUNT_ID,
                name = getString(R.string.account_cash),
                type = AccountType.CASH,
                balance = 0.0
            )
        )
        paymentAccounts.add(
            PaymentAccount(
                id = DEFAULT_CARD_ACCOUNT_ID,
                name = getString(R.string.account_default_card),
                type = AccountType.BANK_CARD,
                balance = 0.0
            )
        )
        nextPaymentAccountId = DEFAULT_CARD_ACCOUNT_ID + 1
    }

    private fun getCalendarFilterLabel(filter: String): String {
        return when (filter) {
            FILTER_GROUP_CARDS -> getString(R.string.calendar_filter_all_cards)
            FILTER_GROUP_WALLETS -> getString(R.string.calendar_filter_all_wallets)
            FILTER_GROUP_CASH -> getString(R.string.calendar_filter_all_cash)
            FILTER_ALL -> getString(R.string.calendar_filter_all_sources)
            else -> getPaymentAccountById(parseAccountIdFromFilter(filter))?.let(::getPaymentAccountDisplayName)
                ?: getString(R.string.calendar_filter_all_sources)
        }
    }

    private fun buildExactAccountFilter(accountId: Long): String = "account_$accountId"

    private fun parseAccountIdFromFilter(filter: String?): Long? {
        if (filter.isNullOrBlank()) return null
        return filter.removePrefix("account_").takeIf { filter.startsWith("account_") }?.toLongOrNull()
    }

    private fun getPaymentAccountById(accountId: Long?): PaymentAccount? {
        return paymentAccounts.firstOrNull { it.id == accountId }
    }

    private fun getPaymentAccountDisplayName(account: PaymentAccount): String {
        val name = account.name.trim()
        val cashNames = setOf("Cash", "Bargeld", "Наличные", "Наличка")
        val cardNames = setOf("Card", "Karte", "Карта")
        return when {
            account.id == DEFAULT_CASH_ACCOUNT_ID && name in cashNames -> getString(R.string.account_cash)
            account.id == DEFAULT_CARD_ACCOUNT_ID && name in cardNames -> getString(R.string.account_default_card)
            else -> account.name
        }
    }

    private fun getPaymentAccountName(accountId: Long?): String {
        return getPaymentAccountById(accountId)?.let(::getPaymentAccountDisplayName) ?: getString(R.string.account_cash)
    }

    private fun getPaymentAccountType(accountId: Long?): AccountType {
        return getPaymentAccountById(accountId)?.type ?: AccountType.CASH
    }

    private fun getPaymentAccountIcon(accountId: Long?): String {
        return when (getPaymentAccountType(accountId)) {
            AccountType.BANK_CARD,
            AccountType.BANK_ACCOUNT -> "💳"
            AccountType.PAYPAL -> "👛"
            AccountType.CASH -> "💵"
            AccountType.OTHER -> "👛"
        }
    }

    private fun getItemMarker(type: ItemType): String {
        return when (type) {
            ItemType.INCOME -> "🟢"
            ItemType.SUBSCRIPTION -> "🟣"
            ItemType.RECURRING_EXPENSE -> "🔁"
            ItemType.EXPENSE -> "🔴"
            ItemType.TRANSFER -> "↔"
        }
    }

    private fun getAccountTypeLabel(type: AccountType): String {
        return when (type) {
            AccountType.BANK_CARD -> getString(R.string.account_type_bank_card)
            AccountType.BANK_ACCOUNT -> getString(R.string.account_type_bank_account)
            AccountType.PAYPAL -> getString(R.string.account_type_paypal)
            AccountType.CASH -> getString(R.string.account_type_cash)
            AccountType.OTHER -> getString(R.string.account_type_other)
        }
    }

    private fun isCardType(type: AccountType): Boolean {
        return type == AccountType.BANK_CARD || type == AccountType.BANK_ACCOUNT
    }

    private fun isWalletType(type: AccountType): Boolean {
        return type == AccountType.PAYPAL || type == AccountType.OTHER
    }

    private fun addAccountPickerSection(container: LinearLayout, title: String) {
        val label = TextView(this).apply {
            text = title
            setTextColor(getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(6))
        }
        container.addView(label)
    }

    private fun showCalendarAccountPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account_picker, null)
        val layoutOptions = dialogView.findViewById<LinearLayout>(R.id.layoutAccountPickerOptions)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccountPicker)
        ensureDefaultPaymentAccounts()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        fun addOption(label: String, filter: String) {
            val button = LayoutInflater.from(this)
                .inflate(R.layout.item_category_manage, layoutOptions, false)
                .findViewById<Button>(R.id.btnCategoryRow)
            button.text = label
            button.setOnClickListener {
                selectedCalendarAccount = filter
                btnCalendarAccountPicker.text = getCalendarAccountButtonText()
                updateCalendarPage()
                dialog.dismiss()
            }
            layoutOptions.addView(button)
        }

        addOption(getString(R.string.calendar_filter_all_sources), FILTER_ALL)

        val hasCards = paymentAccounts.any { isCardType(it.type) }
        val hasWallets = paymentAccounts.any { isWalletType(it.type) }
        val hasCash = paymentAccounts.any { it.type == AccountType.CASH }
        if (hasCards || hasWallets || hasCash) {
            addAccountPickerSection(layoutOptions, getString(R.string.account_type))
            if (hasCards) addOption(getString(R.string.calendar_filter_all_cards), FILTER_GROUP_CARDS)
            if (hasWallets) addOption(getString(R.string.calendar_filter_all_wallets), FILTER_GROUP_WALLETS)
            if (hasCash) addOption(getString(R.string.calendar_filter_all_cash), FILTER_GROUP_CASH)
        }

        addAccountPickerSection(layoutOptions, getString(R.string.settings_accounts))
        paymentAccounts
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
            .forEach { account ->
                addOption(getPaymentAccountDisplayName(account), buildExactAccountFilter(account.id))
            }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showHomeAccountPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account_picker, null)
        val layoutOptions = dialogView.findViewById<LinearLayout>(R.id.layoutAccountPickerOptions)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccountPicker)
        ensureDefaultPaymentAccounts()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        fun addOption(label: String, filter: String) {
            val button = LayoutInflater.from(this)
                .inflate(R.layout.item_category_manage, layoutOptions, false)
                .findViewById<Button>(R.id.btnCategoryRow)
            button.text = label
            button.setOnClickListener {
                selectedHomeAccount = filter
                btnHomeAccountPicker.text = "${getCalendarFilterLabel(selectedHomeAccount)} \u25BC"
                updateTotals()
                refreshPagedContent()
                dialog.dismiss()
            }
            layoutOptions.addView(button)
        }

        addOption(getString(R.string.calendar_filter_all_sources), FILTER_ALL)

        val hasCards = paymentAccounts.any { isCardType(it.type) }
        val hasWallets = paymentAccounts.any { isWalletType(it.type) }
        val hasCash = paymentAccounts.any { it.type == AccountType.CASH }
        if (hasCards || hasWallets || hasCash) {
            addAccountPickerSection(layoutOptions, getString(R.string.account_type))
            if (hasCards) addOption(getString(R.string.calendar_filter_all_cards), FILTER_GROUP_CARDS)
            if (hasWallets) addOption(getString(R.string.calendar_filter_all_wallets), FILTER_GROUP_WALLETS)
            if (hasCash) addOption(getString(R.string.calendar_filter_all_cash), FILTER_GROUP_CASH)
        }

        addAccountPickerSection(layoutOptions, getString(R.string.settings_accounts))
        paymentAccounts
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
            .forEach { account ->
                addOption(getPaymentAccountDisplayName(account), buildExactAccountFilter(account.id))
            }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showAnalyticsAccountPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account_picker, null)
        val layoutOptions = dialogView.findViewById<LinearLayout>(R.id.layoutAccountPickerOptions)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccountPicker)
        ensureDefaultPaymentAccounts()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        fun addOption(label: String, filter: String) {
            val button = LayoutInflater.from(this)
                .inflate(R.layout.item_category_manage, layoutOptions, false)
                .findViewById<Button>(R.id.btnCategoryRow)
            button.text = label
            button.setOnClickListener {
                selectedAnalyticsAccount = filter
                btnAnalyticsAccountPicker.text = getAnalyticsAccountButtonText()
                updateAnalyticsPage()
                dialog.dismiss()
            }
            layoutOptions.addView(button)
        }

        addOption(getString(R.string.calendar_filter_all_sources), FILTER_ALL)

        val hasCards = paymentAccounts.any { isCardType(it.type) }
        val hasWallets = paymentAccounts.any { isWalletType(it.type) }
        val hasCash = paymentAccounts.any { it.type == AccountType.CASH }
        if (hasCards || hasWallets || hasCash) {
            addAccountPickerSection(layoutOptions, getString(R.string.account_type))
            if (hasCards) addOption(getString(R.string.calendar_filter_all_cards), FILTER_GROUP_CARDS)
            if (hasWallets) addOption(getString(R.string.calendar_filter_all_wallets), FILTER_GROUP_WALLETS)
            if (hasCash) addOption(getString(R.string.calendar_filter_all_cash), FILTER_GROUP_CASH)
        }

        addAccountPickerSection(layoutOptions, getString(R.string.settings_accounts))
        paymentAccounts
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
            .forEach { account ->
                addOption(getPaymentAccountDisplayName(account), buildExactAccountFilter(account.id))
            }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showAddEntryMenuDialog(initialDate: String? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry_menu, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAddEntryMenuTitle)
        val btnIncome = dialogView.findViewById<Button>(R.id.btnAddMenuIncome)
        val btnExpense = dialogView.findViewById<Button>(R.id.btnAddMenuExpense)
        val btnSubscription = dialogView.findViewById<Button>(R.id.btnAddMenuSubscription)
        val btnRecurringExpense = dialogView.findViewById<Button>(R.id.btnAddMenuRecurringExpense)
        tvTitle.text = getString(R.string.add_record_title)
        btnIncome.text = getString(R.string.add_income_menu)
        btnExpense.text = getString(R.string.add_expense_menu)
        btnSubscription.text = getString(R.string.add_subscription_menu)
        btnRecurringExpense.text = getString(R.string.add_recurring_expense_menu)
        addPressAnimation(btnIncome)
        addPressAnimation(btnExpense)
        addPressAnimation(btnSubscription)
        addPressAnimation(btnRecurringExpense)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnIncome.setOnClickListener {
            dialog.dismiss()
            showAddIncomeDialog(initialDate)
        }
        btnExpense.setOnClickListener {
            dialog.dismiss()
            showAddExpenseDialog(initialDate)
        }
        btnSubscription.setOnClickListener {
            dialog.dismiss()
            showAddSubscriptionDialog()
        }
        btnRecurringExpense.setOnClickListener {
            dialog.dismiss()
            showRecurringExpenseDialog()
        }
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showManageAccountsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_accounts, null)
        val layoutAccounts = dialogView.findViewById<LinearLayout>(R.id.layoutAccountRows)
        val btnAddAccount = dialogView.findViewById<Button>(R.id.btnAddAccount)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccounts)
        ensureDefaultPaymentAccounts()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        paymentAccounts
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
            .forEach { account ->
            val button = LayoutInflater.from(this)
                .inflate(R.layout.item_category_manage, layoutAccounts, false)
                .findViewById<Button>(R.id.btnCategoryRow)
            button.text = "${getPaymentAccountDisplayName(account)} · ${getAccountTypeLabel(account.type)}"
            button.setOnClickListener {
                dialog.dismiss()
                showPaymentAccountDialog(account.id)
            }
            button.setOnLongClickListener {
                dialog.dismiss()
                showPaymentAccountDialog(account.id)
                true
            }
            layoutAccounts.addView(button)
        }

        btnAddAccount.setOnClickListener {
            dialog.dismiss()
            showPaymentAccountDialog()
        }
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showPaymentAccountDialog(accountId: Long? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_account, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAccountDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etAccountName)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerAccountType)
        val layoutQuickTypes = dialogView.findViewById<LinearLayout>(R.id.layoutQuickAccountTypes)
        val etBalance = dialogView.findViewById<EditText>(R.id.etAccountBalance)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAccountDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteAccountDialog)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveAccountDialog)
        val typeOptions = AccountType.values().toList()
        spinnerType.adapter = createSpinnerAdapter(typeOptions.map(::getAccountTypeLabel))
        val existingAccount = getPaymentAccountById(accountId)

        if (existingAccount != null) {
            tvTitle.text = getString(R.string.edit_account_name)
            etName.setText(getPaymentAccountDisplayName(existingAccount))
            etBalance.setText(getCurrentBalanceForAccount(existingAccount).toString())
            spinnerType.setSelection(typeOptions.indexOf(existingAccount.type).takeIf { it >= 0 } ?: 0)
            btnDelete.visibility = if (paymentAccounts.size > 1) View.VISIBLE else View.GONE
        } else {
            tvTitle.text = getString(R.string.add_account)
            etName.setText("")
            etBalance.setText("0.0")
            spinnerType.setSelection(typeOptions.indexOf(AccountType.BANK_CARD))
            btnDelete.visibility = View.GONE
        }
        addQuickOptionButtons(
            layoutQuickTypes,
            typeOptions.map(::getAccountTypeLabel),
            spinnerType.selectedItemPosition
        ) { index, _ ->
            spinnerType.setSelection(index)
        }

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnDelete.setOnClickListener {
            val targetAccount = existingAccount ?: return@setOnClickListener
            deletePaymentAccount(targetAccount)
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            val newLabel = etName.text.toString().trim()
            val selectedType = typeOptions[spinnerType.selectedItemPosition]
            val enteredBalance = etBalance.text.toString().trim().replace(',', '.').toDoubleOrNull() ?: 0.0
            var addedAccount: PaymentAccount? = null
            if (newLabel.isNotEmpty()) {
                if (existingAccount != null) {
                    val index = paymentAccounts.indexOfFirst { it.id == existingAccount.id }
                    if (index >= 0) {
                        val baseBalance = getBaseBalanceForEnteredCurrentBalance(existingAccount, enteredBalance)
                        paymentAccounts[index] = existingAccount.copy(
                            name = newLabel,
                            type = selectedType,
                            balance = baseBalance
                        )
                    }
                } else {
                    addedAccount = PaymentAccount(
                        id = nextPaymentAccountId++,
                        name = newLabel,
                        type = selectedType,
                        balance = enteredBalance
                    )
                    paymentAccounts.add(addedAccount)
                }
                saveData()
                populateItems()
                updateTotals()
                updateSettingsPageValues()
                btnCalendarAccountPicker.text = getCalendarAccountButtonText()
                refreshPagedContent()
            }
            dialog.dismiss()
            val accountToUndo = addedAccount
            if (accountToUndo != null) {
                showSavedSnackbar {
                    paymentAccounts.remove(accountToUndo)
                    saveData()
                    populateItems()
                    updateTotals()
                    updateSettingsPageValues()
                    btnCalendarAccountPicker.text = getCalendarAccountButtonText()
                    refreshPagedContent()
                }
            } else if (newLabel.isNotEmpty()) {
                showSavedSnackbar()
            }
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun deletePaymentAccount(targetAccount: PaymentAccount, onDeleted: () -> Unit = {}) {
        if (paymentAccounts.size <= 1) return
        val fallbackAccount = paymentAccounts.firstOrNull { it.id != targetAccount.id } ?: return
        showDeleteConfirmation(
            getString(
                R.string.account_delete_message,
                getPaymentAccountDisplayName(targetAccount),
                getPaymentAccountDisplayName(fallbackAccount)
            )
        ) {
            for (index in incomes.indices) {
                if (incomes[index].accountId == targetAccount.id) {
                    incomes[index] = incomes[index].copy(accountId = fallbackAccount.id)
                }
            }
            for (index in subscriptions.indices) {
                if (subscriptions[index].accountId == targetAccount.id) {
                    subscriptions[index] = subscriptions[index].copy(accountId = fallbackAccount.id)
                }
            }
            for (index in recurringExpenses.indices) {
                if (recurringExpenses[index].accountId == targetAccount.id) {
                    recurringExpenses[index] = recurringExpenses[index].copy(accountId = fallbackAccount.id)
                }
            }
            for (index in expenses.indices) {
                if (expenses[index].accountId == targetAccount.id) {
                    expenses[index] = expenses[index].copy(accountId = fallbackAccount.id)
                }
            }
            for (index in transfers.indices) {
                val transfer = transfers[index]
                transfers[index] = transfer.copy(
                    fromAccountId = if (transfer.fromAccountId == targetAccount.id) fallbackAccount.id else transfer.fromAccountId,
                    toAccountId = if (transfer.toAccountId == targetAccount.id) fallbackAccount.id else transfer.toAccountId
                )
            }
            transfers.removeAll { it.fromAccountId == it.toAccountId }
            paymentAccounts.removeAll { it.id == targetAccount.id }
            val removedAccountFilter = buildExactAccountFilter(targetAccount.id)
            if (selectedHomeAccount == removedAccountFilter) {
                selectedHomeAccount = FILTER_ALL
            }
            if (selectedCalendarAccount == removedAccountFilter) {
                selectedCalendarAccount = FILTER_ALL
            }
            if (selectedAnalyticsAccount == removedAccountFilter) {
                selectedAnalyticsAccount = FILTER_ALL
            }
            saveData()
            populateItems()
            updateTotals()
            refreshPagedContent()
            onDeleted()
        }
    }

    private fun showInfoDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseInfo)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun styleBottomSheet(dialog: BottomSheetDialog) {
        dialog.window?.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }

    private fun focusAmountInput(input: EditText) {
        input.post {
            input.requestFocus()
            input.selectAll()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
            inputMethodManager?.showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun parsePositiveAmount(input: EditText): Double? {
        val amount = input.text.toString().trim().replace(',', '.').toDoubleOrNull()
        return if (amount == null || amount <= 0.0) {
            input.error = getString(R.string.invalid_amount)
            null
        } else {
            input.error = null
            amount
        }
    }

    private fun setDetailsVisible(container: View, toggle: Button, visible: Boolean) {
        container.visibility = if (visible) View.VISIBLE else View.GONE
        toggle.text = getString(if (visible) R.string.hide_details else R.string.more_details)
    }

    private fun isoDateWithOffset(days: Int): String {
        return isoDateFormatter().format(
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, days)
            }.time
        )
    }

    private fun addQuickOptionButtons(
        container: LinearLayout,
        values: List<String>,
        selectedIndex: Int = 0,
        onSelected: (Int, String) -> Unit
    ) {
        container.removeAllViews()
        values.forEachIndexed { index, value ->
            val button = Button(this).apply {
                text = value
                isAllCaps = false
                textSize = 11f
                minWidth = 0
                minHeight = 0
                maxLines = 1
                includeFontPadding = false
                setPadding(dpToPx(10), 0, dpToPx(10), 0)
                setTextColor(getColor(if (index == selectedIndex) R.color.home_dark_text else R.color.text_primary))
                background = getDrawable(if (index == selectedIndex) R.drawable.bg_tab_chip_selected else R.drawable.bg_home_panel)
                backgroundTintList = null
                setOnClickListener {
                    onSelected(index, value)
                    addQuickOptionButtons(container, values, index, onSelected)
                }
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    dpToPx(48)
                ).apply {
                    marginEnd = dpToPx(8)
                }
            }
            container.addView(button)
        }
    }

    private fun showOptionBottomSheet(
        options: List<String>,
        selectedIndex: Int = 0,
        onSelected: (Int) -> Unit
    ) {
        if (options.size <= 1) return

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14))
            background = getDrawable(R.drawable.bg_home_balance_card)
        }

        options.forEachIndexed { index, option ->
            val button = Button(this).apply {
                text = option
                isAllCaps = false
                textSize = 14f
                minHeight = 0
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                setPadding(dpToPx(16), 0, dpToPx(16), 0)
                setTextColor(getColor(if (index == selectedIndex) R.color.home_dark_text else R.color.text_primary))
                background = getDrawable(if (index == selectedIndex) R.drawable.bg_tab_chip_selected else R.drawable.bg_home_panel)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(48)
                ).apply {
                    if (index > 0) topMargin = dpToPx(8)
                }
            }
            content.addView(button)
        }

        val root = android.widget.ScrollView(this).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = false
            addView(content)
        }

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(root)
        for (index in 0 until content.childCount) {
            content.getChildAt(index).setOnClickListener {
                dialog.dismiss()
                onSelected(index)
            }
        }
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showChoiceBottomSheet(title: String, options: List<String>, onSelected: (String) -> Unit) {
        if (options.isEmpty()) return

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14))
            background = getDrawable(R.drawable.bg_home_balance_card)
        }
        content.addView(TextView(this).apply {
            text = title
            setTextColor(getColor(R.color.text_primary))
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })

        val dialog = BottomSheetDialog(this)
        options.forEach { option ->
            val button = Button(this).apply {
                text = option
                isAllCaps = false
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                minHeight = 0
                setPadding(dpToPx(16), 0, dpToPx(16), 0)
                setTextColor(getColor(R.color.text_primary))
                background = getDrawable(R.drawable.bg_home_panel)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(48)
                ).apply {
                    topMargin = dpToPx(8)
                }
            }
            button.setOnClickListener {
                onSelected(option)
                dialog.dismiss()
            }
            content.addView(button)
        }
        val root = android.widget.ScrollView(this).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = true
            addView(content)
        }
        dialog.setContentView(root)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showConfirmationBottomSheet(
        title: String,
        message: String,
        positiveText: String,
        onConfirm: () -> Unit
    ) {
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14))
            background = getDrawable(R.drawable.bg_home_balance_card)
        }
        content.addView(TextView(this).apply {
            text = title
            setTextColor(getColor(R.color.text_primary))
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        content.addView(TextView(this).apply {
            text = message
            setTextColor(getColor(R.color.text_secondary))
            textSize = 14f
            setPadding(0, dpToPx(10), 0, dpToPx(12))
        })

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val dialog = BottomSheetDialog(this)
        actions.addView(Button(this).apply {
            text = getString(R.string.cancel)
            isAllCaps = false
            background = getDrawable(R.drawable.bg_button_light)
            backgroundTintList = null
            setTextColor(getColor(R.color.button_text_dark))
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1f)
            setOnClickListener { dialog.dismiss() }
        })
        actions.addView(Button(this).apply {
            text = positiveText
            isAllCaps = false
            background = getDrawable(R.drawable.bg_button_dark)
            backgroundTintList = null
            setTextColor(getColor(R.color.button_text_light))
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1.2f).apply {
                marginStart = dpToPx(8)
            }
            setOnClickListener {
                onConfirm()
                dialog.dismiss()
            }
        })
        content.addView(actions)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun useBottomSheetPicker(spinner: Spinner, options: List<String>, onSelected: (Int) -> Unit = {}) {
        spinner.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                showOptionBottomSheet(options, spinner.selectedItemPosition) { index ->
                    spinner.setSelection(index)
                    onSelected(index)
                }
            }
            true
        }
    }

    private fun showSavedSnackbar(undoAction: (() -> Unit)? = null) {
        val snackbarLength = if (undoAction != null) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
        val snackbar = Snackbar.make(viewPagerSections, R.string.entry_saved, snackbarLength)
        snackbar.anchorView = findViewById(R.id.layoutSectionSelector)
        if (undoAction != null) {
            snackbar.setAction(R.string.undo) {
                undoAction()
            }
        }
        snackbar.show()
    }

    private fun showDeleteConfirmation(message: String, onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDeleteConfirmTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirmMessage)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelDeleteConfirm)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteDeleteConfirm)

        tvTitle.text = getString(R.string.delete_confirm_title)
        tvMessage.text = message

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showDatePicker(initialDate: String?, onDateSelected: (String) -> Unit) {
        val calendar = parseIsoDate(initialDate) ?: Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val pickedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                onDateSelected(isoDateFormatter().format(pickedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateCalendarPage() {
        val monthTitle = SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(displayedMonth.time)
        tvCalendarMonth.text = monthTitle.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        val selectedDate = parseIsoDate(selectedCalendarDateIso)
        if (selectedDate == null || !isSameMonth(selectedDate, displayedMonth)) {
            selectedCalendarDateIso = isoDateFormatter().format(displayedMonth.time)
        }

        rebuildCalendarMonthCache()
        buildCalendarGrid()
        updateCalendarBalancePreview()
        updateCalendarSelectedDaySummary()
        updateCalendarMonthStats()
    }

    private fun rebuildCalendarMonthCache() {
        calendarEventsByDate = buildCalendarEventsByDate(displayedMonth, selectedCalendarAccount)
        calendarMaxDayDelta = calendarEventsByDate.values
            .map { events -> abs(calendarEventsDelta(events)) }
            .maxOrNull()
            ?.takeIf { it > 0.005 }
            ?: 1.0
    }

    private fun updateCalendarBalancePreview() {
        val selectedDateLabel = formatDisplayDate(selectedCalendarDateIso)
        val projectedBalance = getProjectedBalanceForDate(selectedCalendarDateIso, selectedCalendarAccount)
        tvCalendarBalanceLabel.text = getString(R.string.calendar_balance_for_day, selectedDateLabel)
        tvCalendarBalanceValue.text = formatAssetAmount(projectedBalance)
        tvCalendarBalanceValue.setTextColor(getColor(R.color.balance_text))
    }

    private fun updateCalendarSelectedDaySummary() {
        val events = calendarEventsByDate[selectedCalendarDateIso]
            ?: getEventsForDate(selectedCalendarDateIso, selectedCalendarAccount)
        val dayDelta = calendarEventsDelta(events)
        val selectedDateLabel = formatDisplayDate(selectedCalendarDateIso)
        tvCalendarSelectedDayTitle.text = getString(R.string.calendar_day_details, selectedDateLabel)
        tvCalendarSelectedDayAmount.text = if (events.isEmpty()) {
            formatAmount(0.0)
        } else {
            formatSignedAmount(dayDelta)
        }
        btnCalendarOpenDay.isEnabled = events.isNotEmpty()
        btnCalendarOpenDay.alpha = if (events.isEmpty()) 0.45f else 1f
        tvCalendarSelectedDayAmount.setTextColor(
            getColor(
                when {
                    dayDelta > 0.0 -> R.color.amount_positive
                    dayDelta < 0.0 -> R.color.amount_negative
                    else -> R.color.text_secondary
                }
            )
        )
        tvCalendarSelectedDayEvents.text = if (events.isEmpty()) {
            getString(R.string.calendar_no_events)
        } else {
            val visibleEvents = events.take(4).joinToString("\n") { formatCalendarEventLine(it) }
            if (events.size > 4) {
                "$visibleEvents\n+${events.size - 4}"
            } else {
                visibleEvents
            }
        }
    }

    private fun updateCalendarMonthStats() {
        val events = calendarEventsByDate.values.flatten()
        val monthDelta = calendarEventsDelta(events)

        tvCalendarMonthStats.text = formatSignedAmount(monthDelta)
        tvCalendarMonthStats.setTextColor(
            getColor(
                when {
                    abs(monthDelta) < 0.005 -> R.color.balance_text
                    monthDelta > 0 -> R.color.amount_positive
                    else -> R.color.amount_negative
                }
            )
        )
        updateCalendarBudgetStats()
        updateCalendarMonthlyGoalStats()
    }

    private fun updateCalendarBudgetStats() {
        val statuses = getCategoryBudgetStatuses(displayedMonth, selectedCalendarAccount)
        if (statuses.isEmpty()) {
            tvCalendarBudgetStats.visibility = View.GONE
            return
        }

        val totalBudget = statuses.sumOf { it.budget.amount }
        val totalSpent = statuses.sumOf { it.spent }
        val remaining = totalBudget - totalSpent
        val colorRes = getBudgetProgressColor(totalSpent, totalBudget)

        tvCalendarBudgetStats.visibility = View.VISIBLE
        tvCalendarBudgetStats.text = if (remaining >= -0.005) {
            getString(
                R.string.calendar_budget_left,
                formatAmount(remaining.coerceAtLeast(0.0)),
                formatAmount(totalSpent),
                formatAmount(totalBudget)
            )
        } else {
            getString(
                R.string.calendar_budget_over,
                formatAmount(abs(remaining)),
                formatAmount(totalSpent),
                formatAmount(totalBudget)
            )
        }
        tvCalendarBudgetStats.setTextColor(getColor(colorRes))
    }

    private fun updateCalendarMonthlyGoalStats() {
        if (!hasMonthlyGoal()) {
            tvCalendarMonthlyGoalStats.visibility = View.GONE
            return
        }

        val spent = getMonthlyGoalSpentForMonth(displayedMonth)
        val remaining = monthlyGoalAmount - spent
        val colorRes = getBudgetProgressColor(spent, monthlyGoalAmount)

        tvCalendarMonthlyGoalStats.visibility = View.VISIBLE
        tvCalendarMonthlyGoalStats.text = if (remaining >= -0.005) {
            getString(
                R.string.calendar_monthly_goal_left,
                formatAmount(spent),
                formatAmount(monthlyGoalAmount),
                formatAmount(remaining.coerceAtLeast(0.0))
            )
        } else {
            getString(
                R.string.calendar_monthly_goal_over,
                formatAmount(spent),
                formatAmount(monthlyGoalAmount),
                formatAmount(abs(remaining))
            )
        }
        tvCalendarMonthlyGoalStats.setTextColor(getColor(colorRes))
    }

    private fun getNextSubscriptionDate(subscription: Subscription): String? {
        val startDate = subscription.nextChargeDate ?: return null
        val calendar = parseIsoDate(startDate) ?: return null
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        while (calendar.before(today)) {
            if (subscription.period == "yearly") {
                calendar.add(Calendar.YEAR, 1)
            } else {
                calendar.add(Calendar.MONTH, 1)
            }
        }
        val nextDate = isoDateFormatter().format(calendar.time)
        return nextDate.takeIf {
            isSubscriptionLifecycleActiveOnDate(subscription.lifecycleStatus, subscription.lifecycleDate, it)
        }
    }

    private fun buildCalendarGrid() {
        gridCalendarDays.removeAllViews()
        gridCalendarDays.columnCount = 7
        gridCalendarDays.rowCount = GridLayout.UNDEFINED

        val monthCalendar = displayedMonth.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayIndex = ((monthCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7)
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        repeat(6) { weekIndex ->
            val weekStart = weekIndex * 7
            val weekEnd = weekStart + 6
            val weekHasMonthDay = weekEnd >= firstDayIndex && weekStart < firstDayIndex + daysInMonth
            if (!weekHasMonthDay) return@repeat

            val weekDateIsos = mutableListOf<String>()
            repeat(7) { dayOffset ->
                val cellIndex = weekStart + dayOffset
                val view = if (cellIndex < firstDayIndex || cellIndex >= firstDayIndex + daysInMonth) {
                    createEmptyCalendarCell()
                } else {
                    val dayNumber = cellIndex - firstDayIndex + 1
                    val dateCalendar = displayedMonth.clone() as Calendar
                    dateCalendar.set(Calendar.DAY_OF_MONTH, dayNumber)
                    val dateIso = isoDateFormatter().format(dateCalendar.time)
                    weekDateIsos.add(dateIso)
                    createCalendarDayCell(dayNumber, dateIso)
                }
                gridCalendarDays.addView(view)
            }

            gridCalendarDays.addView(createCalendarWeekSummaryCell(weekDateIsos))
        }
    }

    private fun createEmptyCalendarCell(): View {
        return FrameLayout(this).apply {
            layoutParams = createCalendarCellLayoutParams()
            minimumHeight = dpToPx(34)
            alpha = 0f
            setOnTouchListener(createCalendarSwipeTouchListener())
        }
    }

    private fun createCalendarDayCell(dayNumber: Int, dateIso: String): View {
        val events = calendarEventsByDate[dateIso] ?: getEventsForDate(dateIso, selectedCalendarAccount)
        val hasEvents = events.isNotEmpty()
        val isToday = dateIso == isoDateFormatter().format(Date())
        val isSelected = dateIso == selectedCalendarDateIso
        val dayDelta = calendarEventsDelta(events)
        val isForecastOnly = hasEvents && events.all { it.isForecast }

        val container = LinearLayout(this).apply {
            layoutParams = createCalendarCellLayoutParams()
            minimumHeight = dpToPx(if (hasEvents) 42 else 34)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dpToPx(2), dpToPx(3), dpToPx(2), dpToPx(3))
            background = createCalendarDayBackground(dayDelta, isToday, isSelected, isForecastOnly)
            contentDescription = buildCalendarDayContentDescription(dateIso, events, dayDelta)
            setOnTouchListener(createCalendarSwipeTouchListener())
            setOnClickListener {
                val now = System.currentTimeMillis()
                val isDoubleTap = lastCalendarTapDateIso == dateIso && now - lastCalendarTapAt <= 350L
                lastCalendarTapDateIso = dateIso
                lastCalendarTapAt = now
                selectedCalendarDateIso = dateIso
                updateCalendarPage()
                if (isDoubleTap && hasEvents) {
                    showCalendarDayDialog(dateIso)
                }
            }
            setOnLongClickListener {
                selectedCalendarDateIso = dateIso
                updateCalendarPage()
                showCalendarQuickAddSheet(dateIso)
                true
            }
        }

        val dayLabel = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(15)
            )
            text = dayNumber.toString()
            textSize = 11f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(
                getColor(
                    when {
                        isToday -> R.color.budget_warning
                        else -> R.color.text_primary
                    }
                )
            )
            typeface = if (isSelected || isToday) {
                android.graphics.Typeface.DEFAULT_BOLD
            } else {
                android.graphics.Typeface.DEFAULT
            }
        }

        val amountLabel = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(if (hasEvents) 13 else 0)
            )
            text = formatCompactSignedAmount(dayDelta)
            textSize = 8f
            gravity = Gravity.CENTER
            includeFontPadding = false
            maxLines = 1
            visibility = if (hasEvents) View.VISIBLE else View.GONE
            setTextColor(
                getColor(
                    when {
                        dayDelta > 0.0 -> R.color.amount_positive
                        dayDelta < 0.0 -> R.color.amount_negative
                        else -> R.color.text_secondary
                    }
                )
            )
            alpha = if (isForecastOnly) 0.72f else 1f
        }

        val dotsRow = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dpToPx(if (hasEvents) 6 else 0)
            )
            gravity = Gravity.CENTER
            orientation = LinearLayout.HORIZONTAL
            getCalendarDayDotTypes(events).take(3).forEach { type ->
                addView(createCalendarEventDot(type))
            }
            alpha = if (isForecastOnly) 0.62f else 1f
            visibility = if (hasEvents) View.VISIBLE else View.GONE
        }

        container.addView(dayLabel)
        container.addView(amountLabel)
        container.addView(dotsRow)
        return container
    }

    private fun createCalendarWeekSummaryCell(dateIsos: List<String>): View {
        val weekDelta = dateIsos
            .flatMap { dateIso -> calendarEventsByDate[dateIso].orEmpty() }
            .let(::calendarEventsDelta)
        val isZeroWeek = abs(weekDelta) < 0.005

        return TextView(this).apply {
            layoutParams = createCalendarWeekSummaryLayoutParams()
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
            visibility = if (isZeroWeek) View.GONE else View.VISIBLE
            text = if (isZeroWeek) {
                ""
            } else {
                getString(R.string.calendar_week_total, formatCompactCalendarAmount(weekDelta))
            }
            textSize = 10f
            includeFontPadding = false
            setPadding(dpToPx(4), 0, dpToPx(4), 0)
            setTextColor(
                getColor(
                    when {
                        weekDelta > 0.0 -> R.color.amount_positive
                        weekDelta < 0.0 -> R.color.amount_negative
                        else -> R.color.text_secondary
                    }
                )
            )
            alpha = if (isZeroWeek) 0f else 1f
        }
    }

    private fun createCalendarDayBackground(
        dayDelta: Double,
        isToday: Boolean,
        isSelected: Boolean,
        isForecastOnly: Boolean
    ): GradientDrawable {
        val alphaMultiplier = if (isForecastOnly) 0.52 else 1.0
        val fillColor = when {
            isSelected -> colorWithAlpha(getColor(R.color.calendar_accent), 92)
            isToday -> colorWithAlpha(getColor(R.color.budget_warning), 34)
            abs(dayDelta) < 0.005 -> colorWithAlpha(getColor(R.color.text_primary), 10)
            dayDelta > 0.0 -> colorWithAlpha(
                getColor(R.color.amount_positive),
                (calendarDayFillAlpha(dayDelta) * alphaMultiplier).toInt()
            )
            else -> colorWithAlpha(
                getColor(R.color.amount_negative),
                (calendarDayFillAlpha(dayDelta) * alphaMultiplier).toInt()
            )
        }
        val strokeWidth = when {
            isSelected -> dpToPx(2)
            isToday -> dpToPx(2)
            else -> 0
        }
        val strokeColor = when {
            isSelected -> getColor(R.color.calendar_accent)
            isToday -> getColor(R.color.budget_warning)
            else -> Color.TRANSPARENT
        }

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(7).toFloat()
            setColor(fillColor)
            setStroke(strokeWidth, strokeColor)
        }
    }

    private fun calendarDayFillAlpha(dayDelta: Double): Int {
        val ratio = (abs(dayDelta) / calendarMaxDayDelta).coerceIn(0.0, 1.0)
        return (28 + ratio * 74).toInt()
    }

    private fun colorWithAlpha(color: Int, alpha: Int): Int {
        return Color.argb(
            alpha.coerceIn(0, 255),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    private fun getCalendarDayDotTypes(events: List<CalendarEvent>): List<ItemType> {
        return events
            .map { it.type }
            .distinct()
            .sortedBy(::calendarEventPriority)
    }

    private fun createCalendarEventDot(type: ItemType): View {
        val dotColor = getColor(
            when (type) {
                ItemType.INCOME -> R.color.amount_positive
                ItemType.SUBSCRIPTION -> R.color.avatar_subscription_start
                ItemType.RECURRING_EXPENSE -> R.color.budget_warning
                ItemType.EXPENSE -> R.color.amount_negative
                ItemType.TRANSFER -> R.color.home_blue
            }
        )
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(5), dpToPx(5)).apply {
                marginStart = dpToPx(1)
                marginEnd = dpToPx(1)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(dotColor)
            }
        }
    }

    private fun showCalendarQuickAddSheet(dateIso: String) {
        val expenseLabel = getString(R.string.add_expense)
        val incomeLabel = getString(R.string.add_income)
        showChoiceBottomSheet(formatDisplayDate(dateIso), listOf(expenseLabel, incomeLabel)) { selected ->
            when (selected) {
                expenseLabel -> showAddExpenseDialog(dateIso)
                incomeLabel -> showAddIncomeDialog(dateIso)
            }
        }
    }

    private fun buildCalendarDayContentDescription(
        dateIso: String,
        events: List<CalendarEvent>,
        dayDelta: Double
    ): String {
        val eventSummary = if (events.isEmpty()) {
            getString(R.string.calendar_no_events)
        } else {
            getString(R.string.calendar_events_summary, events.size)
        }
        return "${formatDisplayDate(dateIso)}. ${formatSignedAmount(dayDelta)}. $eventSummary"
    }

    private fun changeDisplayedMonthBy(offset: Int) {
        displayedMonth.add(Calendar.MONTH, offset)
        selectedCalendarDateIso = isoDateFormatter().format(displayedMonth.time)
        updateCalendarPage()
    }

    private fun showTodayInCalendar() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        displayedMonth.set(Calendar.YEAR, today.get(Calendar.YEAR))
        displayedMonth.set(Calendar.MONTH, today.get(Calendar.MONTH))
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1)
        selectedCalendarDateIso = isoDateFormatter().format(today.time)
        updateCalendarPage()
    }

    private fun createCalendarSwipeTouchListener(): View.OnTouchListener {
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        var downX = 0f
        var downY = 0f
        var swipeHandled = false

        return View.OnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    swipeHandled = false
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    if (swipeHandled) {
                        return@OnTouchListener true
                    }

                    val deltaX = event.x - downX
                    val deltaY = event.y - downY
                    if (abs(deltaX) > touchSlop && abs(deltaX) > abs(deltaY)) {
                        swipeHandled = true
                        changeDisplayedMonthBy(if (deltaX > 0f) -1 else 1)
                        true
                    } else {
                        false
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val handled = swipeHandled
                    swipeHandled = false
                    handled
                }

                else -> false
            }
        }
    }

    private fun createCalendarCellLayoutParams(): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width = 0
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(dpToPx(2), dpToPx(1), dpToPx(2), dpToPx(1))
        }
    }

    private fun createCalendarWeekSummaryLayoutParams(): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width = 0
            height = dpToPx(16)
            columnSpec = GridLayout.spec(0, 7, 1f)
            setMargins(dpToPx(2), 0, dpToPx(2), dpToPx(2))
        }
    }

    private fun getProjectedBalanceForDate(dateIso: String, accountFilter: String = FILTER_ALL): Double {
        val target = parseIsoDate(dateIso) ?: return 0.0
        return getBalanceUntilDate(target, accountFilter)
    }

    private fun getBalanceUntilDate(target: Calendar, accountFilter: String = FILTER_ALL): Double {
        val baseBalance = getBaseBalanceForAccount(accountFilter)
        val incomeTotal = incomes
            .filter { income ->
                if (!matchesAccountFilter(income.accountId, accountFilter)) return@filter false
                if (income.period == "monthly") {
                    true
                } else {
                    val incomeDate = parseIsoDate(income.expectedDate) ?: return@filter false
                    !incomeDate.after(target)
                }
            }
            .sumOf { income ->
                if (income.period == "monthly") {
                    income.amount * countIncomeOccurrencesUntil(income, target)
                } else {
                    income.amount
                }
            }

        val expenseTotal = expenses
            .filter { expense ->
                if (!matchesAccountFilter(expense.accountId, accountFilter)) return@filter false
                val expenseDate = parseIsoDate(expense.date) ?: return@filter false
                !expenseDate.after(target)
            }
            .sumOf { it.amount }

        val subscriptionsTotal = subscriptions
            .filter { matchesAccountFilter(it.accountId, accountFilter) }
            .sumOf { subscription ->
                getPaymentScheduleTotalUntil(
                    startIso = subscription.nextChargeDate,
                    period = subscription.period,
                    baseAmount = subscription.amount,
                    target = target,
                    skippedDates = emptyList(),
                    overrides = subscription.occurrenceOverrides,
                    isOccurrenceEnabled = { originalDate ->
                        isSubscriptionLifecycleActiveOnDate(
                            subscription.lifecycleStatus,
                            subscription.lifecycleDate,
                            originalDate
                        )
                    }
                )
            }

        val recurringExpenseTotal = recurringExpenses
            .filter { matchesAccountFilter(it.accountId, accountFilter) }
            .sumOf { recurringExpense ->
                getPaymentScheduleTotalUntil(
                    startIso = recurringExpense.startDate,
                    period = recurringExpense.period,
                    baseAmount = recurringExpense.amount,
                    target = target,
                    skippedDates = recurringExpense.skippedDates,
                    overrides = recurringExpense.occurrenceOverrides
                )
            }

        val transferDelta = transfers
            .filter { transfer ->
                val transferDate = parseIsoDate(transfer.date) ?: return@filter false
                !transferDate.after(target) && matchesTransferAccountFilter(transfer, accountFilter)
            }
            .sumOf { transfer -> getTransferDeltaForFilter(transfer, accountFilter) }

        return baseBalance + incomeTotal - expenseTotal - subscriptionsTotal - recurringExpenseTotal + transferDelta
    }

    private fun countIncomeOccurrencesUntil(income: Income, target: Calendar): Int {
        return CalendarRecurrence.countMonthlyOccurrencesUntil(
            startIso = income.expectedDate,
            targetIso = isoDateFormatter().format(target.time)
        )
    }

    private fun getPaymentScheduleTotalUntil(
        startIso: String?,
        period: String,
        baseAmount: Double,
        target: Calendar,
        skippedDates: List<String>,
        overrides: List<PaymentOccurrenceOverride>,
        isOccurrenceEnabled: (String) -> Boolean = { true }
    ): Double {
        val occurrenceDate = parseIsoDate(startIso) ?: return 0.0
        val targetDate = normalizedCalendar(target)
        val anchorDay = occurrenceDate.get(Calendar.DAY_OF_MONTH)
        val lastOriginalDate = overrides
            .filter { paymentOverride ->
                val displayDate = parseIsoDate(paymentOverride.date ?: paymentOverride.originalDate)
                displayDate != null && !displayDate.after(targetDate)
            }
            .mapNotNull { parseIsoDate(it.originalDate) }
            .maxByOrNull { it.timeInMillis }
        val iterationEnd = listOfNotNull(targetDate, lastOriginalDate).maxByOrNull { it.timeInMillis } ?: targetDate

        var total = 0.0
        while (!occurrenceDate.after(iterationEnd)) {
            val originalDate = isoDateFormatter().format(occurrenceDate.time)
            val paymentOverride = findPaymentOverride(overrides, originalDate)
            val displayDate = parseIsoDate(paymentOverride?.date ?: originalDate)
            if (
                displayDate != null &&
                !displayDate.after(targetDate) &&
                isOccurrenceEnabled(originalDate) &&
                !isPaymentSkipped(skippedDates, originalDate)
            ) {
                total += paymentOverride?.amount ?: baseAmount
            }
            if (period == "yearly") {
                occurrenceDate.add(Calendar.YEAR, 1)
            } else {
                occurrenceDate.add(Calendar.MONTH, 1)
            }
            occurrenceDate.set(
                Calendar.DAY_OF_MONTH,
                minOf(anchorDay, occurrenceDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            )
        }
        return total
    }

    private fun normalizedCalendar(value: Calendar): Calendar {
        return (value.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun showCalendarDayDialog(dateIso: String) {
        val events = getEventsForDate(dateIso, selectedCalendarAccount)
        if (events.isEmpty()) return

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_calendar_day, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCalendarDayDialogTitle)
        val layoutEvents = dialogView.findViewById<LinearLayout>(R.id.layoutCalendarDayDialogEvents)
        val layoutActions = dialogView.findViewById<LinearLayout>(R.id.layoutCalendarPaymentActions)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseCalendarDayDialog)
        val dialog = BottomSheetDialog(this)

        tvTitle.text = getString(R.string.calendar_day_details, formatDisplayDate(dateIso))
        layoutEvents.removeAllViews()
        events.forEach { event ->
            layoutEvents.addView(
                createCalendarEventRow(
                    event = event,
                    onClick = {
                        dialog.dismiss()
                        openCalendarEventEditor(event)
                    },
                    onMarkPaid = {
                        updateCalendarPaymentStatuses(listOf(event), markPaid = true)
                        dialog.dismiss()
                    },
                    onSkip = {
                        updateCalendarPaymentStatuses(listOf(event), markPaid = false)
                        dialog.dismiss()
                    }
                )
            )
        }
        layoutActions.visibility = View.GONE

        dialog.setContentView(dialogView)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun createCalendarEventRow(
        event: CalendarEvent,
        onClick: () -> Unit,
        onMarkPaid: () -> Unit,
        onSkip: () -> Unit
    ): View {
        val signedAmount = calendarEventSignedAmount(event)
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10))
            background = createCalendarEventRowBackground(event)
            isClickable = event.sourceIndex >= 0
            isFocusable = event.sourceIndex >= 0
            alpha = when {
                event.isSkipped -> 0.54f
                event.isForecast -> 0.72f
                else -> 1f
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
            if (event.sourceIndex >= 0) {
                setOnClickListener { onClick() }
            }
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        topRow.addView(TextView(this).apply {
            text = event.name
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.text_primary))
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        topRow.addView(TextView(this).apply {
            text = formatSignedAmount(signedAmount)
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(
                getColor(
                    when {
                        signedAmount > 0.0 -> R.color.amount_positive
                        signedAmount < 0.0 -> R.color.amount_negative
                        else -> R.color.text_secondary
                    }
                )
            )
        })

        row.addView(topRow)
        row.addView(TextView(this).apply {
            text = buildCalendarEventMeta(event)
            textSize = 12f
            setTextColor(getColor(if (event.isOverdue) R.color.amount_negative else R.color.text_secondary))
            setPadding(0, dpToPx(5), 0, 0)
        })

        if (canUpdateCalendarPaymentStatus(event)) {
            row.addView(createCalendarEventActionsRow(onMarkPaid, onSkip))
        }
        return row
    }

    private fun createCalendarEventRowBackground(event: CalendarEvent): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(14).toFloat()
            setColor(getColor(R.color.home_panel))
            setStroke(dpToPx(1), getColor(if (event.isOverdue) R.color.amount_negative else R.color.home_line))
        }
    }

    private fun createCalendarEventActionsRow(onMarkPaid: () -> Unit, onSkip: () -> Unit): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dpToPx(10), 0, 0)
            addView(Button(this@MainActivity).apply {
                text = getString(R.string.mark_paid)
                isAllCaps = false
                textSize = 12f
                minHeight = 0
                minWidth = 0
                setTextColor(getColor(R.color.button_text_light))
                background = getDrawable(R.drawable.bg_button_dark)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(0, dpToPx(38), 1f).apply {
                    marginEnd = dpToPx(6)
                }
                setOnClickListener { onMarkPaid() }
            })
            addView(Button(this@MainActivity).apply {
                text = getString(R.string.skip_payment)
                isAllCaps = false
                textSize = 12f
                minHeight = 0
                minWidth = 0
                setTextColor(getColor(R.color.button_text_dark))
                background = getDrawable(R.drawable.bg_button_light)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(0, dpToPx(38), 1f).apply {
                    marginStart = dpToPx(6)
                }
                setOnClickListener { onSkip() }
            })
        }
    }

    private fun buildCalendarEventMeta(event: CalendarEvent): String {
        return listOf(
            getCalendarEventTypeLabel(event.type),
            event.detail,
            getCalendarEventStatusLabel(event)
        ).filter { it.isNotBlank() }.joinToString(" · ")
    }

    private fun getCalendarEventTypeLabel(type: ItemType): String {
        return when (type) {
            ItemType.INCOME -> getString(R.string.income)
            ItemType.SUBSCRIPTION -> getString(R.string.subscription)
            ItemType.RECURRING_EXPENSE -> getString(R.string.recurring_expense)
            ItemType.EXPENSE -> getString(R.string.action_expense)
            ItemType.TRANSFER -> getString(R.string.transfer)
        }
    }

    private fun getSubscriptionLifecycleLabel(status: String?): String {
        return when (normalizeSubscriptionLifecycleStatus(status)) {
            SUBSCRIPTION_STATUS_PAUSED -> getString(R.string.subscription_status_paused)
            SUBSCRIPTION_STATUS_CANCELED -> getString(R.string.subscription_status_canceled)
            else -> getString(R.string.subscription_status_active)
        }
    }

    private fun getSubscriptionLifecycleMeta(subscription: Subscription): String {
        val label = getSubscriptionLifecycleLabel(subscription.lifecycleStatus)
        val date = subscription.lifecycleDate
            ?.takeIf { parseIsoDate(it) != null }
            ?.let { formatDisplayDate(it) }
        return if (date == null || normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus) == SUBSCRIPTION_STATUS_ACTIVE) {
            label
        } else {
            "$label \u00B7 $date"
        }
    }

    private fun getSubscriptionLifecycleStatuses(): List<String> {
        return listOf(
            SUBSCRIPTION_STATUS_ACTIVE,
            SUBSCRIPTION_STATUS_PAUSED,
            SUBSCRIPTION_STATUS_CANCELED
        )
    }

    private fun getCalendarEventStatusLabel(event: CalendarEvent): String {
        val status = when {
            event.isSkipped -> getString(R.string.payment_status_skipped)
            event.isPaid -> getString(R.string.payment_status_paid)
            event.isOverdue -> getString(R.string.calendar_overdue)
            event.isForecast -> getString(R.string.calendar_forecast)
            else -> ""
        }
        return status.takeUnless { it.isNotBlank() && event.detail.contains(it) }.orEmpty()
    }

    private fun openCalendarEventEditor(event: CalendarEvent) {
        if (event.sourceIndex < 0) return
        when (event.type) {
            ItemType.INCOME -> showIncomeDialog(event.sourceIndex)
            ItemType.SUBSCRIPTION -> showPaymentOccurrenceOverrideDialog(event)
            ItemType.RECURRING_EXPENSE -> showPaymentOccurrenceOverrideDialog(event)
            ItemType.EXPENSE -> showExpenseDialog(event.sourceIndex)
            ItemType.TRANSFER -> showTransferDialog(event.sourceIndex)
        }
    }

    private fun showPaymentOccurrenceOverrideDialog(event: CalendarEvent) {
        val originalDate = event.originalOccurrenceDate ?: event.occurrenceDate ?: return
        val currentOverride = getPaymentOccurrenceOverride(event)
        var selectedDate = currentOverride?.date ?: event.occurrenceDate ?: originalDate
        val baseAmount = getPaymentOccurrenceBaseAmount(event) ?: return

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18))
            background = getDrawable(R.drawable.bg_home_balance_card)
        }

        content.addView(TextView(this).apply {
            text = getString(R.string.edit_payment_occurrence)
            textSize = 20f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.text_primary))
        })
        content.addView(TextView(this).apply {
            text = event.name
            textSize = 13f
            setTextColor(getColor(R.color.text_secondary))
            setPadding(0, dpToPx(4), 0, dpToPx(12))
        })

        val etAmount = EditText(this).apply {
            setText((currentOverride?.amount ?: event.amount.takeIf { it > 0.0 } ?: baseAmount).toString())
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = getString(R.string.amount)
            setSingleLine(true)
            setTextColor(getColor(R.color.text_primary))
            setHintTextColor(getColor(R.color.text_hint))
            highlightColor = getColor(R.color.calendar_accent_soft)
            background = getDrawable(R.drawable.bg_input_surface)
            setPadding(dpToPx(14), 0, dpToPx(14), 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(48)
            )
        }
        content.addView(etAmount)

        val btnDate = Button(this).apply {
            isAllCaps = false
            textSize = 14f
            setTextColor(getColor(R.color.text_primary))
            background = getDrawable(R.drawable.bg_input_surface)
            backgroundTintList = null
            minHeight = 0
            minWidth = 0
            includeFontPadding = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(48)
            ).apply {
                topMargin = dpToPx(10)
            }
        }
        updateDateButton(btnDate, selectedDate, R.string.select_charge_date)
        content.addView(btnDate)

        val btnSave = createOccurrenceDialogButton(getString(R.string.save_changes), dark = true)
        val btnReset = createOccurrenceDialogButton(getString(R.string.reset_payment_occurrence), dark = false)
        val btnEditSeries = createOccurrenceDialogButton(getString(R.string.edit_payment_series), dark = false)
        val btnClose = createOccurrenceDialogButton(getString(R.string.close), dark = false)
        content.addView(btnSave)
        content.addView(btnReset)
        content.addView(btnEditSeries)
        content.addView(btnClose)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(content)

        btnDate.setOnClickListener {
            showDatePicker(selectedDate) { pickedDate ->
                selectedDate = pickedDate
                updateDateButton(btnDate, selectedDate, R.string.select_charge_date)
            }
        }
        btnSave.setOnClickListener {
            val amount = parsePositiveAmount(etAmount) ?: return@setOnClickListener
            savePaymentOccurrenceOverride(event, originalDate, selectedDate, amount, baseAmount)
            dialog.dismiss()
        }
        btnReset.setOnClickListener {
            removePaymentOccurrenceOverride(event, originalDate)
            dialog.dismiss()
        }
        btnEditSeries.setOnClickListener {
            dialog.dismiss()
            when (event.type) {
                ItemType.SUBSCRIPTION -> showEditSubscriptionDialog(event.sourceIndex)
                ItemType.RECURRING_EXPENSE -> showRecurringExpenseDialog(event.sourceIndex)
                else -> Unit
            }
        }
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
        focusAmountInput(etAmount)
    }

    private fun createOccurrenceDialogButton(textValue: String, dark: Boolean): Button {
        return Button(this).apply {
            text = textValue
            isAllCaps = false
            textSize = 14f
            setTextColor(getColor(if (dark) R.color.button_text_light else R.color.button_text_dark))
            background = getDrawable(if (dark) R.drawable.bg_button_dark else R.drawable.bg_button_light)
            backgroundTintList = null
            minHeight = 0
            minWidth = 0
            includeFontPadding = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(48)
            ).apply {
                topMargin = dpToPx(10)
            }
        }
    }

    private fun createSubscriptionDetailsButton(): Button {
        return Button(this).apply {
            isAllCaps = false
            textSize = 14f
            minHeight = 0
            gravity = Gravity.CENTER_VERTICAL or Gravity.START
            setPadding(dpToPx(14), 0, dpToPx(14), 0)
            setTextColor(getColor(R.color.text_primary))
            background = getDrawable(R.drawable.bg_home_panel)
            backgroundTintList = null
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(48)
            ).apply {
                topMargin = dpToPx(8)
            }
        }
    }

    private fun getPaymentOccurrenceBaseAmount(event: CalendarEvent): Double? {
        return when (event.type) {
            ItemType.SUBSCRIPTION -> subscriptions.getOrNull(event.sourceIndex)?.amount
            ItemType.RECURRING_EXPENSE -> recurringExpenses.getOrNull(event.sourceIndex)?.amount
            else -> null
        }
    }

    private fun getPaymentOccurrenceOverride(event: CalendarEvent): PaymentOccurrenceOverride? {
        val originalDate = event.originalOccurrenceDate ?: event.occurrenceDate ?: return null
        return when (event.type) {
            ItemType.SUBSCRIPTION -> subscriptions.getOrNull(event.sourceIndex)
                ?.occurrenceOverrides
                ?.let { findPaymentOverride(it, originalDate) }
            ItemType.RECURRING_EXPENSE -> recurringExpenses.getOrNull(event.sourceIndex)
                ?.occurrenceOverrides
                ?.let { findPaymentOverride(it, originalDate) }
            else -> null
        }
    }

    private fun savePaymentOccurrenceOverride(
        event: CalendarEvent,
        originalDate: String,
        selectedDate: String,
        amount: Double,
        baseAmount: Double
    ) {
        val amountOverride = amount.takeUnless { abs(it - baseAmount) < 0.005 }
        val dateOverride = selectedDate.takeUnless { it == originalDate }
        val paymentOverride = PaymentOccurrenceOverride(
            originalDate = originalDate,
            date = dateOverride,
            amount = amountOverride
        )

        if (paymentOverride.date == null && paymentOverride.amount == null) {
            removePaymentOccurrenceOverride(event, originalDate)
            return
        }

        when (event.type) {
            ItemType.SUBSCRIPTION -> {
                val index = event.sourceIndex
                val subscription = subscriptions.getOrNull(index) ?: return
                subscriptions[index] = subscription.copy(
                    occurrenceOverrides = replacePaymentOverride(subscription.occurrenceOverrides, paymentOverride)
                )
            }
            ItemType.RECURRING_EXPENSE -> {
                val index = event.sourceIndex
                val recurringExpense = recurringExpenses.getOrNull(index) ?: return
                recurringExpenses[index] = recurringExpense.copy(
                    occurrenceOverrides = replacePaymentOverride(recurringExpense.occurrenceOverrides, paymentOverride)
                )
            }
            else -> Unit
        }
        refreshAfterPaymentOccurrenceOverrideChanged()
    }

    private fun removePaymentOccurrenceOverride(event: CalendarEvent, originalDate: String) {
        when (event.type) {
            ItemType.SUBSCRIPTION -> {
                val index = event.sourceIndex
                val subscription = subscriptions.getOrNull(index) ?: return
                subscriptions[index] = subscription.copy(
                    occurrenceOverrides = subscription.occurrenceOverrides.filterNot { it.originalDate == originalDate }
                )
            }
            ItemType.RECURRING_EXPENSE -> {
                val index = event.sourceIndex
                val recurringExpense = recurringExpenses.getOrNull(index) ?: return
                recurringExpenses[index] = recurringExpense.copy(
                    occurrenceOverrides = recurringExpense.occurrenceOverrides.filterNot { it.originalDate == originalDate }
                )
            }
            else -> Unit
        }
        refreshAfterPaymentOccurrenceOverrideChanged()
    }

    private fun replacePaymentOverride(
        overrides: List<PaymentOccurrenceOverride>,
        paymentOverride: PaymentOccurrenceOverride
    ): List<PaymentOccurrenceOverride> {
        return (overrides.filterNot { it.originalDate == paymentOverride.originalDate } + paymentOverride)
            .sortedBy { it.originalDate }
    }

    private fun refreshAfterPaymentOccurrenceOverrideChanged() {
        populateItems()
        saveData()
        updateTotals()
        refreshPagedContent()
    }

    private fun formatCalendarEventLine(event: CalendarEvent): String {
        val signedAmount = calendarEventSignedAmount(event)
        val sign = when {
            event.type == ItemType.TRANSFER && abs(signedAmount) < 0.005 -> "↔"
            signedAmount >= 0.0 -> "+"
            else -> "-"
        }
        val detail = event.detail.takeIf { it.isNotBlank() }?.let { " \u00B7 $it" }.orEmpty()
        return "$sign ${event.name} \u00B7 ${formatAmount(event.amount)}$detail"
    }

    private fun updateCalendarPaymentStatuses(events: List<CalendarEvent>, markPaid: Boolean) {
        events.forEach { event ->
            val dateIso = event.originalOccurrenceDate ?: event.occurrenceDate ?: return@forEach
            when (event.type) {
                ItemType.RECURRING_EXPENSE -> {
                    val index = event.sourceIndex
                    if (index in recurringExpenses.indices) {
                        val recurringExpense = recurringExpenses[index]
                        recurringExpenses[index] = if (markPaid) {
                            recurringExpense.copy(
                                paidDates = (recurringExpense.paidDates + dateIso).distinct(),
                                skippedDates = recurringExpense.skippedDates - dateIso
                            )
                        } else {
                            recurringExpense.copy(
                                skippedDates = (recurringExpense.skippedDates + dateIso).distinct(),
                                paidDates = recurringExpense.paidDates - dateIso
                            )
                        }
                    }
                }
                else -> Unit
            }
        }
        populateItems()
        saveData()
        updateTotals()
        refreshPagedContent()
        updateCalendarPage()
    }

    private fun getEventsForMonth(month: Calendar, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        return getCalendarEventsForMonth(month, accountFilter)
    }

    private fun getCalendarEventsForMonth(month: Calendar, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        return buildCalendarEventsByDate(month, accountFilter).values.flatten()
    }

    private fun getDateStringsForMonth(month: Calendar): List<String> {
        val calendar = (month.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (1..daysInMonth).map { day ->
            calendar.set(Calendar.DAY_OF_MONTH, day)
            isoDateFormatter().format(calendar.time)
        }
    }

    private fun buildCalendarEventsByDate(month: Calendar, accountFilter: String = FILTER_ALL): Map<String, List<CalendarEvent>> {
        val dateStrings = getDateStringsForMonth(month)
        val monthDates = dateStrings.toSet()
        val eventsByDate = dateStrings.associateWith { mutableListOf<CalendarEvent>() }.toMutableMap()

        fun addEvent(displayDate: String, event: CalendarEvent) {
            eventsByDate[displayDate]?.add(event)
        }

        incomes.forEachIndexed { index, income ->
            if (!matchesAccountFilter(income.accountId, accountFilter)) return@forEachIndexed
            val fallbackDateIso = parseIsoDate(income.date)
                ?.let { isoDateFormatter().format(it.time) }
                ?: income.date
            if (income.period == "monthly") {
                val sourceIncome = if (income.expectedDate.isNullOrBlank()) {
                    income.copy(expectedDate = fallbackDateIso)
                } else {
                    income
                }
                dateStrings
                    .filter { dateIso -> incomeOccursOnDate(sourceIncome, dateIso) }
                    .forEach { dateIso -> addEvent(dateIso, buildIncomeCalendarEvent(income, index, dateIso)) }
            } else {
                val dateIso = income.expectedDate?.takeIf { it.isNotBlank() } ?: fallbackDateIso
                if (dateIso in monthDates) {
                    addEvent(dateIso, buildIncomeCalendarEvent(income, index, dateIso))
                }
            }
        }

        expenses.forEachIndexed { index, expense ->
            if (expense.date in monthDates && matchesAccountFilter(expense.accountId, accountFilter)) {
                addEvent(expense.date, buildExpenseCalendarEvent(expense, index, expense.date))
            }
        }

        subscriptions.forEachIndexed { index, subscription ->
            if (!matchesAccountFilter(subscription.accountId, accountFilter)) return@forEachIndexed
            val canonicalDates = dateStrings.filter { dateIso ->
                subscriptionOccursOnDate(subscription, dateIso) &&
                    isSubscriptionLifecycleActiveOnDate(
                        subscription.lifecycleStatus,
                        subscription.lifecycleDate,
                        dateIso
                    )
            }
            canonicalDates.forEach { originalDate ->
                addSubscriptionOccurrenceEvent(eventsByDate, subscription, index, originalDate)
            }
            subscription.occurrenceOverrides
                .filter { paymentOverride -> paymentOverride.date in monthDates && paymentOverride.originalDate !in canonicalDates }
                .filter { paymentOverride ->
                    subscriptionOccursOnDate(subscription, paymentOverride.originalDate) &&
                        isSubscriptionLifecycleActiveOnDate(
                            subscription.lifecycleStatus,
                            subscription.lifecycleDate,
                            paymentOverride.originalDate
                        )
                }
                .forEach { paymentOverride ->
                    addSubscriptionOccurrenceEvent(eventsByDate, subscription, index, paymentOverride.originalDate)
                }
        }

        recurringExpenses.forEachIndexed { index, recurringExpense ->
            if (!matchesAccountFilter(recurringExpense.accountId, accountFilter)) return@forEachIndexed
            val canonicalDates = dateStrings.filter { dateIso -> recurringExpenseOccursOnDate(recurringExpense, dateIso) }
            canonicalDates.forEach { originalDate ->
                addRecurringExpenseOccurrenceEvent(eventsByDate, recurringExpense, index, originalDate)
            }
            recurringExpense.occurrenceOverrides
                .filter { paymentOverride -> paymentOverride.date in monthDates && paymentOverride.originalDate !in canonicalDates }
                .filter { paymentOverride -> recurringExpenseOccursOnDate(recurringExpense, paymentOverride.originalDate) }
                .forEach { paymentOverride ->
                    addRecurringExpenseOccurrenceEvent(eventsByDate, recurringExpense, index, paymentOverride.originalDate)
                }
        }

        transfers.forEachIndexed { index, transfer ->
            if (transfer.date in monthDates && matchesTransferAccountFilter(transfer, accountFilter)) {
                addEvent(transfer.date, buildTransferCalendarEvent(transfer, index, transfer.date, accountFilter))
            }
        }

        return eventsByDate.mapValues { (_, events) -> sortCalendarEvents(events) }
    }

    private fun addSubscriptionOccurrenceEvent(
        eventsByDate: MutableMap<String, MutableList<CalendarEvent>>,
        subscription: Subscription,
        sourceIndex: Int,
        originalDate: String
    ) {
        val paymentOverride = findPaymentOverride(subscription.occurrenceOverrides, originalDate)
        val displayDate = paymentOverride?.date ?: originalDate
        val targetEvents = eventsByDate[displayDate] ?: return

        targetEvents.add(
            CalendarEvent(
                type = ItemType.SUBSCRIPTION,
                name = subscription.name,
                amount = paymentOverride?.amount ?: subscription.amount,
                detail = buildEventDetail(
                    if (subscription.period == "yearly") getString(R.string.yearly) else getString(R.string.monthly),
                    subscription.accountId
                ),
                sourceIndex = sourceIndex,
                occurrenceDate = displayDate,
                originalOccurrenceDate = originalDate,
                isForecast = isFutureDate(displayDate)
            )
        )
    }

    private fun addRecurringExpenseOccurrenceEvent(
        eventsByDate: MutableMap<String, MutableList<CalendarEvent>>,
        recurringExpense: RecurringExpense,
        sourceIndex: Int,
        originalDate: String
    ) {
        val paymentOverride = findPaymentOverride(recurringExpense.occurrenceOverrides, originalDate)
        val displayDate = paymentOverride?.date ?: originalDate
        val targetEvents = eventsByDate[displayDate] ?: return
        val isSkipped = isPaymentSkipped(recurringExpense.skippedDates, originalDate)
        val isPaid = isPaymentPaid(recurringExpense.paidDates, originalDate)
        val isOverdue = isPastDate(displayDate) && !isPaid && !isSkipped

        targetEvents.add(
            CalendarEvent(
                type = ItemType.RECURRING_EXPENSE,
                name = recurringExpense.name,
                amount = if (isSkipped) 0.0 else paymentOverride?.amount ?: recurringExpense.amount,
                detail = appendPaymentStatus(
                    buildEventDetail(
                        appendTags(buildCategoryLabel(recurringExpense.category, recurringExpense.subcategory), recurringExpense.tags),
                        recurringExpense.accountId
                    ),
                    isPaid,
                    isSkipped
                ),
                sourceIndex = sourceIndex,
                occurrenceDate = displayDate,
                originalOccurrenceDate = originalDate,
                isPaid = isPaid,
                isSkipped = isSkipped,
                isForecast = isFutureDate(displayDate) && !isPaid && !isSkipped,
                isOverdue = isOverdue
            )
        )
    }

    private fun buildIncomeCalendarEvent(income: Income, sourceIndex: Int, dateIso: String): CalendarEvent {
        return CalendarEvent(
            type = ItemType.INCOME,
            name = income.name,
            amount = income.amount,
            detail = buildEventDetail(
                getIncomeTypeDisplayName(income.type.ifBlank { getString(R.string.income_default_name) }),
                income.accountId
            ),
            sourceIndex = sourceIndex,
            occurrenceDate = dateIso,
            isForecast = isFutureDate(dateIso)
        )
    }

    private fun buildExpenseCalendarEvent(expense: Expense, sourceIndex: Int, dateIso: String): CalendarEvent {
        return CalendarEvent(
            type = ItemType.EXPENSE,
            name = expense.name,
            amount = expense.amount,
            detail = buildEventDetail(
                appendTags(buildCategoryLabel(expense.category, expense.subcategory), expense.tags),
                expense.accountId
            ),
            sourceIndex = sourceIndex,
            occurrenceDate = dateIso,
            isForecast = isFutureDate(dateIso)
        )
    }

    private fun buildTransferCalendarEvent(
        transfer: AccountTransfer,
        sourceIndex: Int,
        dateIso: String,
        accountFilter: String
    ): CalendarEvent {
        return CalendarEvent(
            type = ItemType.TRANSFER,
            name = transfer.note ?: getString(R.string.transfer),
            amount = transfer.amount,
            detail = getString(
                R.string.transfer_route,
                getPaymentAccountName(transfer.fromAccountId),
                getPaymentAccountName(transfer.toAccountId)
            ),
            sourceIndex = sourceIndex,
            occurrenceDate = dateIso,
            accountDelta = getTransferDeltaForFilter(transfer, accountFilter),
            isForecast = isFutureDate(dateIso)
        )
    }

    private fun findPaymentOverride(
        overrides: List<PaymentOccurrenceOverride>,
        originalDate: String
    ): PaymentOccurrenceOverride? {
        return overrides.lastOrNull { it.originalDate == originalDate }
    }

    private fun sortCalendarEvents(events: List<CalendarEvent>): List<CalendarEvent> {
        return events.sortedWith(
            compareBy<CalendarEvent> { calendarEventPriority(it.type) }
                .thenBy { it.name.lowercase(Locale.getDefault()) }
        )
    }

    private fun getEventsForDate(dateIso: String, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        val date = parseIsoDate(dateIso) ?: return emptyList()
        if (
            accountFilter == selectedCalendarAccount &&
            isSameMonth(date, displayedMonth) &&
            calendarEventsByDate.isNotEmpty()
        ) {
            return calendarEventsByDate[dateIso].orEmpty()
        }
        return buildCalendarEventsByDate(date, accountFilter)[dateIso].orEmpty()
    }

    private fun incomeOccursOnDate(income: Income, dateIso: String): Boolean {
        return CalendarRecurrence.monthlyOccurrenceMatches(income.expectedDate, dateIso)
    }

    private fun subscriptionOccursOnDate(subscription: Subscription, dateIso: String): Boolean {
        return CalendarRecurrence.recurringOccurrenceMatches(subscription.nextChargeDate, subscription.period, dateIso)
    }

    private fun recurringExpenseOccursOnDate(recurringExpense: RecurringExpense, dateIso: String): Boolean {
        return CalendarRecurrence.recurringOccurrenceMatches(recurringExpense.startDate, recurringExpense.period, dateIso)
    }

    private fun sameCalendarDate(value: String?, expectedIso: String): Boolean {
        val left = parseIsoDate(value)?.timeInMillis ?: return false
        val right = parseIsoDate(expectedIso)?.timeInMillis ?: return false
        return left == right
    }

    private fun isFutureDate(dateIso: String): Boolean {
        val date = parseIsoDate(dateIso) ?: return false
        return date.after(todayCalendar())
    }

    private fun isPastDate(dateIso: String): Boolean {
        val date = parseIsoDate(dateIso) ?: return false
        return date.before(todayCalendar())
    }

    private fun todayCalendar(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun isSameMonth(value: Calendar?, month: Calendar): Boolean {
        return value != null &&
            value.get(Calendar.YEAR) == month.get(Calendar.YEAR) &&
            value.get(Calendar.MONTH) == month.get(Calendar.MONTH)
    }

    private fun monthContainsIncomeOccurrence(income: Income, month: Calendar): Boolean {
        return (income.expectedDate.isNullOrBlank() && income.date.isBlank()) ||
            getIncomeOccurrenceDateInMonth(income, month) != null
    }

    private fun getIncomeOccurrenceDateInMonth(income: Income, month: Calendar): String? {
        val source = if (income.expectedDate.isNullOrBlank()) {
            income.copy(expectedDate = income.date)
        } else {
            income
        }
        if (source.expectedDate.isNullOrBlank()) return null
        return getDateStringsForMonth(month).firstOrNull { dateIso ->
            incomeOccursOnDate(source, dateIso)
        }
    }

    private fun monthContainsSubscriptionOccurrence(subscription: Subscription, month: Calendar): Boolean {
        if (normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus) != SUBSCRIPTION_STATUS_ACTIVE) return true
        return subscription.nextChargeDate.isNullOrBlank() ||
            getSubscriptionOccurrenceDateInMonth(subscription, month) != null
    }

    private fun getSubscriptionOccurrenceDateInMonth(subscription: Subscription, month: Calendar): String? {
        if (subscription.nextChargeDate.isNullOrBlank()) return null
        return getDateStringsForMonth(month).firstOrNull { dateIso ->
            subscriptionOccursOnDate(subscription, dateIso) &&
                isSubscriptionLifecycleActiveOnDate(
                    subscription.lifecycleStatus,
                    subscription.lifecycleDate,
                    dateIso
                )
        }
    }

    private fun monthContainsRecurringExpenseOccurrence(recurringExpense: RecurringExpense, month: Calendar): Boolean {
        return recurringExpense.startDate.isNullOrBlank() ||
            getRecurringExpenseOccurrenceDateInMonth(recurringExpense, month) != null
    }

    private fun getRecurringExpenseOccurrenceDateInMonth(recurringExpense: RecurringExpense, month: Calendar): String? {
        if (recurringExpense.startDate.isNullOrBlank()) return null
        return getDateStringsForMonth(month).firstOrNull { dateIso ->
            recurringExpenseOccursOnDate(recurringExpense, dateIso)
        }
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun parseIsoDate(value: String?): Calendar? {
        if (value.isNullOrBlank()) return null
        return runCatching {
            Calendar.getInstance().apply {
                time = isoDateFormatter().parse(value)
                    ?: displayDateFormatter().parse(value)
                    ?: Date()
            }
        }.getOrNull()
    }

    private fun formatDisplayDate(value: String?): String {
        val parsed = parseIsoDate(value)?.time ?: return "-"
        return displayDateFormatter().format(parsed)
    }

    private fun isoDateFormatter(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    private fun displayDateFormatter(): SimpleDateFormat {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    }

    private fun homeMonthFormatter(): SimpleDateFormat {
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    }

    private fun updateDateButton(button: Button, dateValue: String?, emptyTextRes: Int) {
        button.text = if (dateValue.isNullOrBlank()) {
            getString(emptyTextRes)
        } else {
            getString(R.string.selected_date, formatDisplayDate(dateValue))
        }
    }

    private fun updateDateShortcutButtons(selectedDate: String?, vararg shortcuts: Pair<Button, Int>) {
        shortcuts.forEach { (button, offsetDays) ->
            val isSelected = selectedDate == isoDateWithOffset(offsetDays)
            button.background = getDrawable(if (isSelected) R.drawable.bg_tab_chip_selected else R.drawable.bg_button_light)
            button.backgroundTintList = null
            button.setTextColor(getColor(if (isSelected) R.color.home_dark_text else R.color.button_text_dark))
        }
    }

    private fun updateTheme(theme: String) {
        if (theme == getSavedTheme()) return
        saveTheme(theme)
        applySavedTheme()
        recreate()
    }

    private fun updateCurrency(currency: String) {
        if (currency == getSavedCurrency()) return
        saveCurrency(currency)
        updateSettingsPageValues()
        updateTotals()
        refreshPagedContent()
    }

    private fun getCurrencySymbol(): String {
        return if (getSavedCurrency() == "USD") "$" else "€"
    }

    private fun formatAmount(value: Double): String {
        return "%.2f %s".format(value, getCurrencySymbol())
    }

    private fun formatSignedAmount(value: Double): String {
        if (abs(value) < 0.005) {
            return formatAmount(0.0)
        }
        return if (value > 0) {
            "+${formatAmount(value)}"
        } else {
            "-${formatAmount(abs(value))}"
        }
    }

    private fun formatCompactCalendarAmount(value: Double): String {
        return formatCompactSignedAmount(value).ifBlank { "0${getCurrencySymbol()}" }
    }

    private fun formatCompactAmount(value: Double): String {
        return formatCompactSignedAmount(value)
            .removePrefix("+")
            .removePrefix("-")
            .ifBlank { "0${getCurrencySymbol()}" }
    }

    private fun formatCompactSignedAmount(value: Double): String {
        if (abs(value) < 0.005) return ""
        val sign = if (value > 0) "+" else "-"
        val amount = abs(value)
        val amountText = if (amount >= 1000.0) {
            String.format(Locale.US, "%.1fk", amount / 1000.0).replace(".0k", "k")
        } else {
            String.format(Locale.US, "%.0f", amount)
        }
        return "$sign$amountText${getCurrencySymbol()}"
    }

    private fun formatItemAmount(item: Item): String {
        if (item.type != ItemType.TRANSFER) return formatSignedAmount(item.amount)
        val transferAmount = transfers.getOrNull(item.sourceIndex)?.amount ?: abs(item.amount)
        return if (abs(item.amount) < 0.005) {
            formatAmount(transferAmount)
        } else {
            formatSignedAmount(item.amount)
        }
    }

    private fun getReminderDaysOptions(): List<Int> = listOf(0, 1, 3, 7)

    private fun getReminderDaysLabels(): List<String> {
        return getReminderDaysOptions().map { days ->
            when (days) {
                0 -> getString(R.string.payment_reminder_same_day)
                1 -> getString(R.string.payment_reminder_one_day)
                else -> getString(R.string.payment_reminder_days_before, days)
            }
        }
    }

    private fun setReminderDaysSelection(spinner: Spinner, daysBefore: Int) {
        val index = getReminderDaysOptions().indexOf(daysBefore).takeIf { it >= 0 } ?: 1
        spinner.setSelection(index)
    }

    private fun getSelectedReminderDays(spinner: Spinner): Int {
        return getReminderDaysOptions().getOrElse(spinner.selectedItemPosition) { 1 }
    }

    private fun isPaymentSkipped(skippedDates: List<String>?, dateIso: String?): Boolean {
        return !dateIso.isNullOrBlank() && skippedDates.orEmpty().contains(dateIso)
    }

    private fun isPaymentPaid(paidDates: List<String>?, dateIso: String?): Boolean {
        return !dateIso.isNullOrBlank() && paidDates.orEmpty().contains(dateIso)
    }

    private fun appendPaymentStatus(detail: String, isPaid: Boolean, isSkipped: Boolean): String {
        val status = when {
            isSkipped -> getString(R.string.payment_status_skipped)
            isPaid -> getString(R.string.payment_status_paid)
            else -> return detail
        }
        return "$detail \u00B7 $status"
    }

    private fun normalizeOptionalText(value: String?): String? {
        return value?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun parseTags(value: String?): List<String> {
        return value.orEmpty()
            .split(",", "|", ";")
            .map { it.trim().removePrefix("#") }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase(Locale.getDefault()) }
    }

    private fun tagsToInput(tags: List<String>): String {
        return tags.joinToString(", ")
    }

    private fun formatTags(tags: List<String>): String {
        return tags.joinToString(" ") { "#$it" }
    }

    private fun buildCategoryLabel(category: String?, subcategory: String?): String {
        val primary = category?.takeIf { it.isNotBlank() } ?: getString(R.string.general_category)
        val secondary = subcategory?.takeIf { it.isNotBlank() }
        return if (secondary == null) primary else "$primary / $secondary"
    }

    private fun appendTags(text: String, tags: List<String>): String {
        val tagText = formatTags(tags)
        return if (tagText.isBlank()) text else "$text \u00B7 $tagText"
    }

    private fun rememberTagsAndSubcategoriesFrom(
        importedExpenses: List<Expense>,
        importedRecurringExpenses: List<RecurringExpense>
    ) {
        importedExpenses.forEach { expense ->
            savedTags.addAll(expense.tags)
            normalizeOptionalText(expense.subcategory)?.let(savedSubcategories::add)
        }
        importedRecurringExpenses.forEach { recurringExpense ->
            savedTags.addAll(recurringExpense.tags)
            normalizeOptionalText(recurringExpense.subcategory)?.let(savedSubcategories::add)
        }
    }

    private fun getPopularTags(): List<String> {
        return listOf(
            getString(R.string.tag_work),
            getString(R.string.tag_family),
            getString(R.string.tag_health),
            getString(R.string.tag_urgent),
            getString(R.string.tag_cash),
            getString(R.string.tag_online),
            getString(R.string.tag_refund),
            getString(R.string.tag_trip)
        )
    }

    private fun getPopularSubcategories(): List<String> {
        return listOf(
            getString(R.string.subcategory_groceries),
            getString(R.string.subcategory_restaurants),
            getString(R.string.subcategory_fuel),
            getString(R.string.subcategory_taxi),
            getString(R.string.subcategory_rent),
            getString(R.string.subcategory_utilities),
            getString(R.string.subcategory_insurance),
            getString(R.string.subcategory_installment),
            getString(R.string.subcategory_medicine),
            getString(R.string.subcategory_clothes),
            getString(R.string.subcategory_gifts),
            getString(R.string.subcategory_transfer)
        )
    }

    private fun getKnownTags(): List<String> {
        return (savedTags + expenses.flatMap { it.tags } + recurringExpenses.flatMap { it.tags } + getPopularTags())
            .mapNotNull(::normalizeOptionalText)
            .distinctBy { it.lowercase(Locale.getDefault()) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
    }

    private fun getKnownSubcategories(): List<String> {
        return (
            savedSubcategories +
                expenses.mapNotNull { normalizeOptionalText(it.subcategory) } +
                recurringExpenses.mapNotNull { normalizeOptionalText(it.subcategory) } +
                getPopularSubcategories()
            )
            .distinctBy { it.lowercase(Locale.getDefault()) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
    }

    private fun showSubcategoryPicker(target: EditText) {
        val options = getKnownSubcategories()
        if (options.isEmpty()) return
        showChoiceBottomSheet(getString(R.string.choose_subcategory), options) { selected ->
            target.setText(selected)
            savedSubcategories.add(selected)
        }
    }

    private fun showTagPicker(target: EditText) {
        val options = getKnownTags()
        if (options.isEmpty()) return
        val selected = parseTags(target.text.toString()).toMutableSet()
        val checked = options.map { option ->
            selected.any { it.equals(option, ignoreCase = true) }
        }.toBooleanArray()
        showTagBottomSheet(options, checked, selected, target)
    }

    private fun showTagBottomSheet(
        options: List<String>,
        checked: BooleanArray,
        selected: MutableSet<String>,
        target: EditText
    ) {
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14))
            background = getDrawable(R.drawable.bg_home_balance_card)
        }
        content.addView(TextView(this).apply {
            text = getString(R.string.choose_tags)
            setTextColor(getColor(R.color.text_primary))
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })

        options.forEachIndexed { index, option ->
            content.addView(CheckBox(this).apply {
                text = option
                isChecked = checked.getOrNull(index) == true
                buttonTintList = ColorStateList.valueOf(getColor(R.color.calendar_accent))
                setTextColor(getColor(R.color.text_primary))
                textSize = 14f
                setPadding(0, dpToPx(4), 0, dpToPx(4))
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selected.add(option)
                    } else {
                        selected.removeAll { it.equals(option, ignoreCase = true) }
                    }
                }
            })
        }

        val dialog = BottomSheetDialog(this)
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(10), 0, 0)
        }
        actions.addView(Button(this).apply {
            text = getString(R.string.cancel)
            isAllCaps = false
            background = getDrawable(R.drawable.bg_button_light)
            backgroundTintList = null
            setTextColor(getColor(R.color.button_text_dark))
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1f)
            setOnClickListener { dialog.dismiss() }
        })
        actions.addView(Button(this).apply {
            text = getString(R.string.save_changes)
            isAllCaps = false
            background = getDrawable(R.drawable.bg_button_dark)
            backgroundTintList = null
            setTextColor(getColor(R.color.button_text_light))
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1.2f).apply {
                marginStart = dpToPx(8)
            }
            setOnClickListener {
                val tags = selected.toList().distinctBy { it.lowercase(Locale.getDefault()) }
                target.setText(tagsToInput(tags))
                savedTags.addAll(tags)
                dialog.dismiss()
            }
        })
        content.addView(actions)
        val root = android.widget.ScrollView(this).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = true
            addView(content)
        }
        dialog.setContentView(root)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showManageTagsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_tags, null)
        val etTags = dialogView.findViewById<EditText>(R.id.etManagedTags)
        val etSubcategories = dialogView.findViewById<EditText>(R.id.etManagedSubcategories)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelManageTags)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveManageTags)

        etTags.setText(getKnownTags().joinToString(", "))
        etSubcategories.setText(getKnownSubcategories().joinToString(", "))

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener {
            savedTags.clear()
            savedTags.addAll(parseTags(etTags.text.toString()))
            savedSubcategories.clear()
            savedSubcategories.addAll(
                etSubcategories.text.toString()
                    .split(",", "|", ";")
                    .mapNotNull(::normalizeOptionalText)
                    .distinctBy { it.lowercase(Locale.getDefault()) }
            )
            saveData()
            updateSettingsPageValues()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun loadData() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        migrateSharedPreferencesToRoomIfNeeded(prefs)
        val incomesJson = readStoredString(prefs, KEY_INCOMES, "[]")
        val subscriptionsJson = readStoredString(prefs, KEY_SUBSCRIPTIONS, "[]")
        val recurringExpensesJson = readStoredString(prefs, KEY_RECURRING_EXPENSES, "[]")
        val expensesJson = readStoredString(prefs, KEY_EXPENSES, "[]")
        val transfersJson = readStoredString(prefs, KEY_TRANSFERS, "[]")
        val categoriesJson = readStoredString(prefs, KEY_CATEGORIES, "[]")
        val categoryBudgetsJson = readStoredString(prefs, KEY_CATEGORY_BUDGETS, "[]")
        val favoriteCategoriesJson = readStoredString(prefs, KEY_FAVORITE_CATEGORIES, "[]")
        val savedTagsJson = readStoredString(prefs, KEY_SAVED_TAGS, "[]")
        val savedSubcategoriesJson = readStoredString(prefs, KEY_SAVED_SUBCATEGORIES, "[]")
        val monthlyGoalCategoriesJson = readStoredString(prefs, KEY_MONTHLY_GOAL_CATEGORIES, "[]")
        cashAmount = readStoredDouble(prefs, KEY_CASH_AMOUNT, 0.0)
        monthlyGoalAmount = readStoredDouble(prefs, KEY_MONTHLY_GOAL_AMOUNT, 0.0)
        monthlyGoalIncludeExpenses = readStoredBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_EXPENSES, true)
        monthlyGoalIncludeSubscriptions = readStoredBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_SUBSCRIPTIONS, true)
        val rawIncomes = runCatching {
            gson.fromJson<List<Income>>(incomesJson, object : TypeToken<List<Income>>() {}.type)
        }.getOrNull().orEmpty()
        val rawSubscriptions = runCatching {
            gson.fromJson<List<Subscription>>(subscriptionsJson, object : TypeToken<List<Subscription>>() {}.type)
        }.getOrNull().orEmpty()
        val rawRecurringExpenses = runCatching {
            gson.fromJson<List<RecurringExpense>>(recurringExpensesJson, object : TypeToken<List<RecurringExpense>>() {}.type)
        }.getOrNull().orEmpty()
        val rawExpenses = runCatching {
            gson.fromJson<List<Expense>>(expensesJson, object : TypeToken<List<Expense>>() {}.type)
        }.getOrNull().orEmpty()
        val rawTransfers = runCatching {
            gson.fromJson<List<AccountTransfer>>(transfersJson, object : TypeToken<List<AccountTransfer>>() {}.type)
        }.getOrNull().orEmpty()
        val legacyBalances = runCatching {
            gson.fromJson<Map<String, Double>>(readStoredString(prefs, KEY_ACCOUNT_BALANCES, "{}"), object : TypeToken<Map<String, Double>>() {}.type)
        }.getOrNull().orEmpty()
        val legacyLabels = runCatching {
            gson.fromJson<Map<String, String>>(readStoredString(prefs, KEY_ACCOUNT_LABELS, "{}"), object : TypeToken<Map<String, String>>() {}.type)
        }.getOrNull().orEmpty()
        val savedAccounts = runCatching {
            gson.fromJson<List<PaymentAccount>>(readStoredString(prefs, KEY_PAYMENT_ACCOUNTS, "[]"), object : TypeToken<List<PaymentAccount>>() {}.type)
        }.getOrNull().orEmpty()

        paymentAccounts.clear()
        if (savedAccounts.isNotEmpty()) {
            paymentAccounts.addAll(savedAccounts.map(::sanitizePaymentAccount).distinctBy { it.id })
        } else {
            paymentAccounts.addAll(
                migrateLegacyAccounts(
                    legacyBalances = legacyBalances,
                    legacyLabels = legacyLabels,
                    rawIncomes = rawIncomes,
                    rawSubscriptions = rawSubscriptions,
                    rawExpenses = rawExpenses,
                    legacyCashAmount = cashAmount
                )
            )
        }
        ensureDefaultPaymentAccounts()
        nextPaymentAccountId = readStoredLong(
            prefs,
            KEY_NEXT_PAYMENT_ACCOUNT_ID,
            (paymentAccounts.maxOfOrNull { it.id } ?: 0L) + 1L
        ).coerceAtLeast((paymentAccounts.maxOfOrNull { it.id } ?: 0L) + 1L)

        cashAmount = paymentAccounts.firstOrNull { it.type == AccountType.CASH }?.balance ?: cashAmount
        incomes.addAll(rawIncomes.map(::sanitizeIncome))
        subscriptions.addAll(rawSubscriptions.map(::sanitizeSubscription))
        recurringExpenses.addAll(rawRecurringExpenses.map(::sanitizeRecurringExpense))
        expenses.addAll(rawExpenses.map(::sanitizeExpense))
        transfers.addAll(rawTransfers.map(::sanitizeTransfer).distinctBy(::transferImportKey))

        runCatching {
            gson.fromJson<List<String>>(categoriesJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull()?.let { categories.addAll(it) }
        val savedFavoriteCategories = runCatching {
            gson.fromJson<List<String>>(favoriteCategoriesJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull().orEmpty()
        favoriteCategories.addAll(
            savedFavoriteCategories
                .filter { categories.contains(it) }
                .distinct()
        )
        runCatching {
            gson.fromJson<List<String>>(savedTagsJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull().orEmpty().flatMap(::parseTags).forEach(savedTags::add)
        runCatching {
            gson.fromJson<List<String>>(savedSubcategoriesJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull().orEmpty().mapNotNull(::normalizeOptionalText).forEach(savedSubcategories::add)
        rememberTagsAndSubcategoriesFrom(expenses, recurringExpenses)
        val savedMonthlyGoalCategories = runCatching {
            gson.fromJson<List<String>>(monthlyGoalCategoriesJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull().orEmpty()
        monthlyGoalCategories.addAll(
            savedMonthlyGoalCategories
                .filter { categories.contains(it) }
                .distinct()
        )
        val savedCategoryBudgets = runCatching {
            gson.fromJson<List<CategoryBudget>>(categoryBudgetsJson, object : TypeToken<List<CategoryBudget>>() {}.type)
        }.getOrNull().orEmpty()
        categoryBudgets.addAll(
            savedCategoryBudgets
                .filter { it.amount > 0.0 && categories.contains(it.category) }
                .distinctBy { it.category }
        )
    }

    private fun saveData() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        cashAmount = paymentAccounts.firstOrNull { it.type == AccountType.CASH }?.balance ?: 0.0
        storeString(prefs, KEY_INCOMES, gson.toJson(incomes))
        storeString(prefs, KEY_SUBSCRIPTIONS, gson.toJson(subscriptions))
        storeString(prefs, KEY_RECURRING_EXPENSES, gson.toJson(recurringExpenses))
        storeString(prefs, KEY_EXPENSES, gson.toJson(expenses))
        storeString(prefs, KEY_TRANSFERS, gson.toJson(transfers))
        storeString(prefs, KEY_CATEGORIES, gson.toJson(categories))
        storeString(prefs, KEY_FAVORITE_CATEGORIES, gson.toJson(favoriteCategories.toList()))
        storeString(prefs, KEY_SAVED_TAGS, gson.toJson(getKnownTags()))
        storeString(prefs, KEY_SAVED_SUBCATEGORIES, gson.toJson(getKnownSubcategories()))
        storeString(prefs, KEY_CATEGORY_BUDGETS, gson.toJson(categoryBudgets))
        storeDouble(prefs, KEY_MONTHLY_GOAL_AMOUNT, monthlyGoalAmount)
        storeBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_EXPENSES, monthlyGoalIncludeExpenses)
        storeBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_SUBSCRIPTIONS, monthlyGoalIncludeSubscriptions)
        storeString(prefs, KEY_MONTHLY_GOAL_CATEGORIES, gson.toJson(monthlyGoalCategories.toList()))
        storeDouble(prefs, KEY_CASH_AMOUNT, cashAmount)
        storeString(prefs, KEY_PAYMENT_ACCOUNTS, gson.toJson(paymentAccounts))
        storeLong(prefs, KEY_NEXT_PAYMENT_ACCOUNT_ID, nextPaymentAccountId)
        prefs.putBoolean(KEY_ROOM_MIGRATION_DONE, true)
        prefs.apply()
        schedulePaymentReminders()
    }

    private fun schedulePaymentReminders() {
        if (::moneyDao.isInitialized) {
            PaymentReminderScheduler.scheduleAll(this, subscriptions, recurringExpenses)
        }
    }

    private fun ensurePaymentReminderPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 2001)
    }

    private fun migrateSharedPreferencesToRoomIfNeeded(prefs: SharedPreferences) {
        if (prefs.getBoolean(KEY_ROOM_MIGRATION_DONE, false)) {
            return
        }
        if (moneyDao.count() > 0) {
            prefs.edit().putBoolean(KEY_ROOM_MIGRATION_DONE, true).apply()
            return
        }

        val now = System.currentTimeMillis()
        val values = mutableListOf<StoredValue>()
        fun addString(key: String, defaultValue: String) {
            values.add(StoredValue(key, prefs.getString(key, defaultValue) ?: defaultValue, now))
        }
        fun addDouble(key: String, defaultValue: Double) {
            values.add(StoredValue(key, readPrefsDouble(prefs, key, defaultValue).toString(), now))
        }
        fun addBoolean(key: String, defaultValue: Boolean) {
            values.add(StoredValue(key, readPrefsBoolean(prefs, key, defaultValue).toString(), now))
        }
        fun addLong(key: String, defaultValue: Long) {
            values.add(StoredValue(key, readPrefsLong(prefs, key, defaultValue).toString(), now))
        }

        addString(KEY_INCOMES, "[]")
        addString(KEY_SUBSCRIPTIONS, "[]")
        addString(KEY_RECURRING_EXPENSES, "[]")
        addString(KEY_EXPENSES, "[]")
        addString(KEY_TRANSFERS, "[]")
        addString(KEY_CATEGORIES, "[]")
        addString(KEY_FAVORITE_CATEGORIES, "[]")
        addString(KEY_SAVED_TAGS, "[]")
        addString(KEY_SAVED_SUBCATEGORIES, "[]")
        addString(KEY_CATEGORY_BUDGETS, "[]")
        addString(KEY_MONTHLY_GOAL_CATEGORIES, "[]")
        addString(KEY_PAYMENT_ACCOUNTS, "[]")
        addString(KEY_ACCOUNT_BALANCES, "{}")
        addString(KEY_ACCOUNT_LABELS, "{}")
        addString(KEY_APP_LOCK_PIN_HASH, "")
        addString(KEY_APP_LOCK_PIN_SALT, "")
        addDouble(KEY_CASH_AMOUNT, 0.0)
        addDouble(KEY_MONTHLY_GOAL_AMOUNT, 0.0)
        addBoolean(KEY_MONTHLY_GOAL_INCLUDE_EXPENSES, true)
        addBoolean(KEY_MONTHLY_GOAL_INCLUDE_SUBSCRIPTIONS, true)
        addBoolean(KEY_APP_LOCK_ENABLED, false)
        addBoolean(KEY_APP_LOCK_DEVICE_AUTH, false)
        addLong(KEY_NEXT_PAYMENT_ACCOUNT_ID, DEFAULT_CARD_ACCOUNT_ID + 1L)

        moneyDao.putAll(values)
        prefs.edit().putBoolean(KEY_ROOM_MIGRATION_DONE, true).apply()
    }

    private fun readStoredString(prefs: SharedPreferences, key: String, defaultValue: String): String {
        return moneyDao.getValue(key) ?: prefs.getString(key, defaultValue) ?: defaultValue
    }

    private fun readStoredDouble(prefs: SharedPreferences, key: String, defaultValue: Double): Double {
        return moneyDao.getValue(key)?.toDoubleOrNull() ?: readPrefsDouble(prefs, key, defaultValue)
    }

    private fun readStoredBoolean(prefs: SharedPreferences, key: String, defaultValue: Boolean): Boolean {
        return moneyDao.getValue(key)?.toBooleanStrictOrNull() ?: readPrefsBoolean(prefs, key, defaultValue)
    }

    private fun readStoredLong(prefs: SharedPreferences, key: String, defaultValue: Long): Long {
        return moneyDao.getValue(key)?.toLongOrNull() ?: readPrefsLong(prefs, key, defaultValue)
    }

    private fun readPrefsDouble(prefs: SharedPreferences, key: String, defaultValue: Double): Double {
        return runCatching { prefs.getFloat(key, defaultValue.toFloat()).toDouble() }
            .getOrElse { prefs.getString(key, defaultValue.toString())?.toDoubleOrNull() ?: defaultValue }
    }

    private fun readPrefsBoolean(prefs: SharedPreferences, key: String, defaultValue: Boolean): Boolean {
        return runCatching { prefs.getBoolean(key, defaultValue) }
            .getOrElse { prefs.getString(key, defaultValue.toString())?.toBooleanStrictOrNull() ?: defaultValue }
    }

    private fun readPrefsLong(prefs: SharedPreferences, key: String, defaultValue: Long): Long {
        return runCatching { prefs.getLong(key, defaultValue) }
            .getOrElse { prefs.getString(key, defaultValue.toString())?.toLongOrNull() ?: defaultValue }
    }

    private fun storeString(prefs: SharedPreferences.Editor, key: String, value: String) {
        moneyDao.put(StoredValue(key, value, System.currentTimeMillis()))
        prefs.putString(key, value)
    }

    private fun storeDouble(prefs: SharedPreferences.Editor, key: String, value: Double) {
        moneyDao.put(StoredValue(key, value.toString(), System.currentTimeMillis()))
        prefs.putFloat(key, value.toFloat())
    }

    private fun storeBoolean(prefs: SharedPreferences.Editor, key: String, value: Boolean) {
        moneyDao.put(StoredValue(key, value.toString(), System.currentTimeMillis()))
        prefs.putBoolean(key, value)
    }

    private fun storeLong(prefs: SharedPreferences.Editor, key: String, value: Long) {
        moneyDao.put(StoredValue(key, value.toString(), System.currentTimeMillis()))
        prefs.putLong(key, value)
    }

    private fun populateItems() {
        items.clear()
        incomes.forEachIndexed { index, income ->
            val trailingValue = if (income.period == "monthly") {
                getIncomeOccurrenceDateInMonth(income, homeDisplayedMonth)
                    ?.let { formatDisplayDate(it) }
                    ?: income.expectedDate?.let { formatDisplayDate(it) }
                    ?: income.date
            } else {
                income.expectedDate?.let { formatDisplayDate(it) } ?: income.date
            }
            val periodLabel = if (income.period == "monthly") {
                getString(R.string.monthly)
            } else {
                getString(R.string.once)
            }
            val typeLabel = income.type.takeIf { it.isNotBlank() }
                ?.let(::getIncomeTypeDisplayName)
                ?: getString(R.string.income)
            val metaValue = "$typeLabel · $periodLabel"
            items.add(
                Item(
                    name = income.name,
                    amount = income.amount,
                    meta = metaValue,
                    trailing = "${getPaymentAccountIcon(income.accountId)} ${getPaymentAccountName(income.accountId)} · $trailingValue",
                    type = ItemType.INCOME,
                    sourceIndex = index
                )
            )
        }
        subscriptions.forEachIndexed { index, subscription ->
            val amount = subscription.amount
            val periodLabel = if (subscription.period == "monthly") getString(R.string.monthly) else getString(R.string.yearly)
            val lifecycleStatus = normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus)
            val occurrenceDate = getSubscriptionOccurrenceDateInMonth(subscription, homeDisplayedMonth)
            val lifecycleMeta = getSubscriptionLifecycleMeta(subscription)
            val nextChargeDisplay = if (lifecycleStatus == SUBSCRIPTION_STATUS_ACTIVE) {
                occurrenceDate?.let { formatDisplayDate(it) }
                    ?: getNextSubscriptionDate(subscription)?.let { formatDisplayDate(it) }
                    ?: periodLabel
            } else {
                lifecycleMeta
            }
            items.add(
                Item(
                    name = subscription.name,
                    amount = if (lifecycleStatus == SUBSCRIPTION_STATUS_ACTIVE) -amount else 0.0,
                    meta = "$periodLabel \u00B7 $nextChargeDisplay",
                    trailing = "${getPaymentAccountIcon(subscription.accountId)} ${getPaymentAccountName(subscription.accountId)}",
                    type = ItemType.SUBSCRIPTION,
                    sourceIndex = index
                )
            )
        }
        recurringExpenses.forEachIndexed { index, recurringExpense ->
            val categoryLabel = buildCategoryLabel(recurringExpense.category, recurringExpense.subcategory)
            val periodLabel = if (recurringExpense.period == "yearly") getString(R.string.yearly) else getString(R.string.monthly)
            val occurrenceDate = getRecurringExpenseOccurrenceDateInMonth(recurringExpense, homeDisplayedMonth)
            val isSkipped = isPaymentSkipped(recurringExpense.skippedDates, occurrenceDate)
            val isPaid = isPaymentPaid(recurringExpense.paidDates, occurrenceDate)
            val statusLabel = when {
                isSkipped -> " \u00B7 ${getString(R.string.payment_status_skipped)}"
                isPaid -> " \u00B7 ${getString(R.string.payment_status_paid)}"
                else -> ""
            }
            val occurrenceDisplay = occurrenceDate?.let { formatDisplayDate(it) }
                ?: recurringExpense.startDate?.let { formatDisplayDate(it) }
                ?: periodLabel
            items.add(
                Item(
                    name = if (recurringExpense.favorite) {
                        "${getString(R.string.favorite_marker)} ${recurringExpense.name}"
                    } else {
                        recurringExpense.name
                    },
                    amount = if (isSkipped) 0.0 else -recurringExpense.amount,
                    meta = appendTags("$categoryLabel \u00B7 $periodLabel$statusLabel", recurringExpense.tags),
                    trailing = "${getPaymentAccountIcon(recurringExpense.accountId)} ${getPaymentAccountName(recurringExpense.accountId)} \u00B7 $occurrenceDisplay",
                    type = ItemType.RECURRING_EXPENSE,
                    sourceIndex = index
                )
            )
        }
        expenses.forEachIndexed { index, expense ->
            val categoryLabel = buildCategoryLabel(expense.category, expense.subcategory)
            items.add(
                Item(
                    name = expense.name,
                    amount = -expense.amount,
                    meta = appendTags(categoryLabel, expense.tags),
                    trailing = "${getPaymentAccountIcon(expense.accountId)} ${getPaymentAccountName(expense.accountId)} · ${formatDisplayDate(expense.date)}",
                    type = ItemType.EXPENSE,
                    sourceIndex = index
                )
            )
        }
        transfers.forEachIndexed { index, transfer ->
            items.add(
                Item(
                    name = transfer.note ?: getString(R.string.transfer),
                    amount = getTransferDeltaForFilter(transfer, selectedHomeAccount),
                    meta = getString(
                        R.string.transfer_route,
                        getPaymentAccountName(transfer.fromAccountId),
                        getPaymentAccountName(transfer.toAccountId)
                    ),
                    trailing = formatDisplayDate(transfer.date),
                    type = ItemType.TRANSFER,
                    sourceIndex = index
                )
            )
        }
    }

    private fun updateTotals() {
        val visibleItems = getHomeVisibleItems()
        val incomeTotal = visibleItems
            .filter { it.type == ItemType.INCOME }
            .sumOf { it.amount }
        val subscriptionsTotal = visibleItems
            .filter { it.type == ItemType.SUBSCRIPTION }
            .sumOf { -it.amount }
        val expensesTotal = visibleItems
            .filter { it.type == ItemType.EXPENSE || it.type == ItemType.RECURRING_EXPENSE }
            .sumOf { -it.amount }
        val cashAccountsTotal = paymentAccounts
            .filter { it.type == AccountType.CASH }
            .sumOf(::getCurrentBalanceForAccount)

        tvCashTotal.text = formatAssetAmount(cashAccountsTotal)
        setSignedTotal(tvHomeNetTotal, incomeTotal - subscriptionsTotal - expensesTotal)
        setSignedTotal(tvIncomeTotal, incomeTotal, compact = true)
        setSignedTotal(tvSubscriptionsTotal, -subscriptionsTotal, compact = true)
        setSignedTotal(tvExpensesTotal, -expensesTotal, compact = true)
        tvIncomeTotal.setTextColor(getColor(R.color.home_dark_text))
        tvSubscriptionsTotal.setTextColor(getColor(R.color.home_dark_text))
        tvExpensesTotal.setTextColor(getColor(R.color.home_dark_text))
        tvRemaining.text = formatAssetAmount(getCurrentBalanceForFilter(selectedHomeAccount))
        tvRemaining.setTextColor(getColor(R.color.balance_text))
        updateHomeBalancesVisibilityButton()
    }

    private fun updateHomeBalancesVisibilityButton() {
        if (!::btnHomeBalancesVisibility.isInitialized) return
        btnHomeBalancesVisibility.text = getString(
            if (balancesHidden) R.string.assets_show_balances else R.string.assets_hide_balances
        )
    }

    private fun updateBalanceVisibilityUi() {
        updateHomeBalancesVisibilityButton()
        if (::tvRemaining.isInitialized) {
            updateTotals()
        }
        if (::tvCalendarBalanceValue.isInitialized) {
            updateCalendarBalancePreview()
        }
        if (::layoutCategoriesPageList.isInitialized) {
            updateCategoriesPage()
        }
    }

    private fun setSignedTotal(view: TextView, value: Double, compact: Boolean = false) {
        view.text = if (compact) formatCompactSignedTotal(value) else formatSignedAmount(value)
        view.setTextColor(
            getColor(
                when {
                    abs(value) < 0.005 -> R.color.balance_text
                    value > 0 -> R.color.amount_positive
                    else -> R.color.amount_negative
                }
            )
        )
    }

    private fun showAddCategoryDialog() {
        showCategoryDialog()
    }

    private fun showManageCategoriesDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_categories, null)
        val layoutCategoryList = dialogView.findViewById<LinearLayout>(R.id.layoutCategoryList)
        val tvEmptyCategories = dialogView.findViewById<TextView>(R.id.tvEmptyCategories)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseCategoryManager)
        val btnAddNew = dialogView.findViewById<Button>(R.id.btnAddNewCategory)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        if (categories.isEmpty()) {
            tvEmptyCategories.visibility = View.VISIBLE
        } else {
            tvEmptyCategories.visibility = View.GONE
            categories.forEachIndexed { index, category ->
                val rowView = LayoutInflater.from(this).inflate(R.layout.item_category_manage, layoutCategoryList, false)
                val button = rowView.findViewById<Button>(R.id.btnCategoryRow)
                button.text = getCategoryDisplayName(category)
                button.setOnClickListener {
                    dialog.dismiss()
                    showCategoryDialog(index)
                }
                button.setOnLongClickListener {
                    dialog.dismiss()
                    showCategoryDialog(index)
                    true
                }
                layoutCategoryList.addView(rowView)
            }
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnAddNew.setOnClickListener {
            dialog.dismiss()
            showCategoryDialog()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showCategoryDialog(categoryIndex: Int? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCategoryDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val btnChoosePopular = dialogView.findViewById<Button>(R.id.btnChoosePopularCategory)
        val cbFavorite = dialogView.findViewById<CheckBox>(R.id.cbFavoriteCategory)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCategoryDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteCategoryDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryCategoryDialog)

        if (categoryIndex != null && categoryIndex in categories.indices) {
            tvTitle.text = getString(R.string.edit_category)
            etName.setText(categories[categoryIndex])
            cbFavorite.isChecked = favoriteCategories.contains(categories[categoryIndex])
            btnDelete.visibility = View.VISIBLE
            btnPrimary.text = getString(R.string.save_changes)
        } else {
            tvTitle.text = getString(R.string.add_category)
            btnDelete.visibility = View.GONE
            btnPrimary.text = getString(R.string.add_category)
        }

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnChoosePopular.setOnClickListener {
            showPopularCategoryPicker { categoryName ->
                etName.setText(categoryName)
                etName.setSelection(categoryName.length)
            }
        }

        btnDelete.setOnClickListener {
            if (categoryIndex != null && categoryIndex in categories.indices) {
                val categoryName = categories[categoryIndex]
                showDeleteConfirmation(getString(R.string.delete_category_message, categoryName)) {
                    val deletedCategory = categories.removeAt(categoryIndex)
                    categoryBudgets.removeAll { it.category == deletedCategory }
                    monthlyGoalCategories.remove(deletedCategory)
                    favoriteCategories.remove(deletedCategory)
                    recurringExpenses.replaceAll { recurringExpense ->
                        if (recurringExpense.category == deletedCategory) {
                            recurringExpense.copy(category = null, subcategory = null)
                        } else {
                            recurringExpense
                        }
                    }
                    expenses.replaceAll { expense ->
                        if (expense.category == deletedCategory) {
                            val updatedName = if (expense.name == deletedCategory) {
                                getString(R.string.expense_default_name)
                            } else {
                                expense.name
                            }
                            expense.copy(name = updatedName, category = null, subcategory = null)
                        } else {
                            expense
                        }
                    }
                    populateItems()
                    saveData()
                    refreshPagedContent()
                }
            }
            dialog.dismiss()
        }

        btnPrimary.setOnClickListener {
            val categoryName = etName.text.toString().trim()
            if (categoryName.isEmpty()) {
                return@setOnClickListener
            }

            if (categoryIndex != null && categoryIndex in categories.indices) {
                val oldName = categories[categoryIndex]
                val duplicateExists = categories.withIndex().any { it.index != categoryIndex && it.value == categoryName }
                if (!duplicateExists) {
                    val renamedBudget = getCategoryBudget(oldName)?.copy(category = categoryName)
                    categories[categoryIndex] = categoryName
                    categoryBudgets.removeAll { it.category == oldName || it.category == categoryName }
                    if (renamedBudget != null) {
                        categoryBudgets.add(renamedBudget)
                    }
                    if (monthlyGoalCategories.remove(oldName)) {
                        monthlyGoalCategories.add(categoryName)
                    }
                    if (favoriteCategories.remove(oldName)) {
                        favoriteCategories.add(categoryName)
                    }
                    if (cbFavorite.isChecked) {
                        favoriteCategories.add(categoryName)
                    } else {
                        favoriteCategories.remove(categoryName)
                    }
                    recurringExpenses.replaceAll { recurringExpense ->
                        if (recurringExpense.category == oldName) {
                            recurringExpense.copy(
                                name = if (recurringExpense.name == oldName) categoryName else recurringExpense.name,
                                category = categoryName
                            )
                        } else {
                            recurringExpense
                        }
                    }
                    expenses.replaceAll { expense ->
                        if (expense.category == oldName) {
                            val updatedName = if (expense.name == oldName) categoryName else expense.name
                            expense.copy(name = updatedName, category = categoryName)
                        } else {
                            expense
                        }
                    }
                }
                populateItems()
                saveData()
                updateTotals()
                refreshPagedContent()
            } else if (!categories.contains(categoryName)) {
                categories.add(categoryName)
                if (cbFavorite.isChecked) {
                    favoriteCategories.add(categoryName)
                }
                saveData()
                populateItems()
                updateTotals()
                refreshPagedContent()
            }
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showAddIncomeDialog(initialDate: String? = null) {
        showIncomeDialog(initialDate = initialDate)
    }

    private fun showIncomeDialog(incomeIndex: Int? = null, initialDate: String? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_income, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvIncomeDialogTitle)
        val spinnerIncomeType = dialogView.findViewById<Spinner>(R.id.spinnerIncomeType)
        val spinnerIncomePeriod = dialogView.findViewById<Spinner>(R.id.spinnerIncomePeriod)
        val spinnerIncomeAccount = dialogView.findViewById<Spinner>(R.id.spinnerIncomeAccount)
        val layoutQuickIncomeTypes = dialogView.findViewById<LinearLayout>(R.id.layoutQuickIncomeTypes)
        val layoutIncomeAdvanced = dialogView.findViewById<LinearLayout>(R.id.layoutIncomeAdvanced)
        val etIncomeName = dialogView.findViewById<EditText>(R.id.etIncomeName)
        val etIncomeAmount = dialogView.findViewById<EditText>(R.id.etIncomeAmount)
        val btnIncomeDate = dialogView.findViewById<Button>(R.id.btnIncomeDate)
        val btnIncomeToday = dialogView.findViewById<Button>(R.id.btnIncomeToday)
        val btnIncomeYesterday = dialogView.findViewById<Button>(R.id.btnIncomeYesterday)
        val btnToggleDetails = dialogView.findViewById<Button>(R.id.btnToggleIncomeDetails)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelIncomeDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteIncomeDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryIncomeDialog)
        var selectedIncomeDate: String? = initialDate ?: isoDateFormatter().format(Date())
        var detailsVisible = false

        val incomeTypes = getIncomeTypeOptions()
        spinnerIncomeType.adapter = createSpinnerAdapter(incomeTypes)
        val incomePeriods = getIncomePeriodOptions()
        spinnerIncomePeriod.adapter = createSpinnerAdapter(incomePeriods)
        val accountValues = getPaymentAccountOptions()
        val accountLabels = accountValues.map(::getPaymentAccountDisplayName)
        spinnerIncomeAccount.adapter = createSpinnerAdapter(accountLabels)
        useBottomSheetPicker(spinnerIncomeAccount, accountLabels)
        useBottomSheetPicker(spinnerIncomePeriod, incomePeriods)

        if (incomeIndex != null && incomeIndex in incomes.indices) {
            val income = incomes[incomeIndex]
            tvTitle.text = getString(R.string.edit_income)
            btnDelete.visibility = View.VISIBLE
            btnPrimary.text = getString(R.string.save_changes)
            etIncomeName.setText(income.name)
            etIncomeAmount.setText(income.amount.toString())
            spinnerIncomeType.setSelection(incomeTypes.indexOf(getIncomeTypeDisplayName(income.type)).takeIf { it >= 0 } ?: 0)
            spinnerIncomePeriod.setSelection(if (income.period == "monthly") 1 else 0)
            spinnerIncomeAccount.setSelection(accountValues.indexOfFirst { it.id == income.accountId }.takeIf { it >= 0 } ?: 0)
            selectedIncomeDate = income.expectedDate ?: isoDateFormatter().format(Date())
            detailsVisible = income.period == "monthly"
        } else {
            tvTitle.text = getString(R.string.add_income)
            btnDelete.visibility = View.GONE
            btnPrimary.text = getString(R.string.add_income)
            spinnerIncomePeriod.setSelection(0)
        }
        addQuickOptionButtons(layoutQuickIncomeTypes, incomeTypes, spinnerIncomeType.selectedItemPosition) { index, _ ->
            spinnerIncomeType.setSelection(index)
        }
        fun updateIncomeDateUi() {
            updateDateButton(btnIncomeDate, selectedIncomeDate, R.string.select_income_date)
            updateDateShortcutButtons(
                selectedIncomeDate,
                btnIncomeToday to 0,
                btnIncomeYesterday to -1
            )
        }

        updateIncomeDateUi()
        setDetailsVisible(layoutIncomeAdvanced, btnToggleDetails, detailsVisible)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnToggleDetails.setOnClickListener {
            detailsVisible = !detailsVisible
            setDetailsVisible(layoutIncomeAdvanced, btnToggleDetails, detailsVisible)
        }

        btnIncomeToday.setOnClickListener {
            selectedIncomeDate = isoDateWithOffset(0)
            updateIncomeDateUi()
        }

        btnIncomeYesterday.setOnClickListener {
            selectedIncomeDate = isoDateWithOffset(-1)
            updateIncomeDateUi()
        }

        btnIncomeDate.setOnClickListener {
            showDatePicker(selectedIncomeDate) { pickedDate ->
                selectedIncomeDate = pickedDate
                updateIncomeDateUi()
            }
        }

        btnDelete.setOnClickListener {
            if (incomeIndex != null && incomeIndex in incomes.indices) {
                val incomeName = incomes[incomeIndex].name
                showDeleteConfirmation(getString(R.string.delete_income_message, incomeName)) {
                    incomes.removeAt(incomeIndex)
                    populateItems()
                    saveData()
                    updateTotals()
                    refreshPagedContent()
                }
            }
            dialog.dismiss()
        }

        btnPrimary.setOnClickListener {
            val selectedTypeLabel = spinnerIncomeType.selectedItem?.toString().orEmpty()
            val selectedType = getIncomeTypeKey(selectedTypeLabel)
            val selectedPeriod = if (spinnerIncomePeriod.selectedItemPosition == 1) "monthly" else "once"
            val typedName = etIncomeName.text.toString().trim()
            val name = typedName.ifEmpty {
                if (selectedTypeLabel.isNotEmpty()) selectedTypeLabel else getString(R.string.income_default_name)
            }
            val amount = parsePositiveAmount(etIncomeAmount) ?: return@setOnClickListener
            val date = if (incomeIndex != null && incomeIndex in incomes.indices) {
                incomes[incomeIndex].date
            } else {
                displayDateFormatter().format(Date())
            }
            val updatedIncome = Income(
                name,
                amount,
                date,
                selectedType,
                selectedIncomeDate,
                selectedPeriod,
                accountValues[spinnerIncomeAccount.selectedItemPosition].id
            )
            if (incomeIndex != null && incomeIndex in incomes.indices) {
                incomes[incomeIndex] = updatedIncome
            } else {
                incomes.add(updatedIncome)
            }
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
            if (incomeIndex == null) {
                showSavedSnackbar {
                    incomes.remove(updatedIncome)
                    populateItems()
                    saveData()
                    updateTotals()
                    refreshPagedContent()
                }
            } else {
                showSavedSnackbar()
            }
        }

        dialog.show()
        styleBottomSheet(dialog)
        if (incomeIndex == null) {
            focusAmountInput(etIncomeAmount)
        }
    }

    private fun showAddSubscriptionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subscription, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvSubscriptionDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val layoutPresets = dialogView.findViewById<LinearLayout>(R.id.layoutSubscriptionPresets)
        val spinnerPeriod = dialogView.findViewById<Spinner>(R.id.spinnerPeriod)
        val spinnerSubscriptionAccount = dialogView.findViewById<Spinner>(R.id.spinnerSubscriptionAccount)
        val btnSubscriptionDate = dialogView.findViewById<Button>(R.id.btnSubscriptionDate)
        val btnSubscriptionToday = dialogView.findViewById<Button>(R.id.btnSubscriptionToday)
        val btnSubscriptionTomorrow = dialogView.findViewById<Button>(R.id.btnSubscriptionTomorrow)
        val btnToggleDetails = dialogView.findViewById<Button>(R.id.btnToggleSubscriptionDetails)
        val layoutDetails = dialogView.findViewById<LinearLayout>(R.id.layoutSubscriptionDetails)
        val cbReminder = dialogView.findViewById<CheckBox>(R.id.cbSubscriptionReminder)
        val spinnerReminderDays = dialogView.findViewById<Spinner>(R.id.spinnerSubscriptionReminderDays)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelSubscriptionDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteSubscriptionDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimarySubscriptionDialog)
        var selectedChargeDate: String? = isoDateFormatter().format(Date())

        tvTitle.text = getString(R.string.add_subscription)
        btnDelete.visibility = View.GONE
        btnPrimary.text = getString(R.string.add_subscription)

        val periods = arrayOf(getString(R.string.monthly), getString(R.string.yearly))
        spinnerPeriod.adapter = createSpinnerAdapter(periods.toList())
        val accountValues = getPaymentAccountOptions()
        val accountLabels = accountValues.map(::getPaymentAccountDisplayName)
        spinnerSubscriptionAccount.adapter = createSpinnerAdapter(accountLabels)
        val reminderLabels = getReminderDaysLabels()
        spinnerReminderDays.adapter = createSpinnerAdapter(reminderLabels)
        useBottomSheetPicker(spinnerSubscriptionAccount, accountLabels)
        useBottomSheetPicker(spinnerPeriod, periods.toList())
        useBottomSheetPicker(spinnerReminderDays, reminderLabels)
        cbReminder.isChecked = true
        setReminderDaysSelection(spinnerReminderDays, 1)
        setupSubscriptionServiceSearch(etName, layoutPresets)
        fun updateSubscriptionDateUi() {
            updateDateButton(btnSubscriptionDate, selectedChargeDate, R.string.select_charge_date)
            updateDateShortcutButtons(
                selectedChargeDate,
                btnSubscriptionToday to 0,
                btnSubscriptionTomorrow to 1
            )
        }
        updateSubscriptionDateUi()
        var detailsVisible = false
        setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnToggleDetails.setOnClickListener {
            detailsVisible = !detailsVisible
            setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)
        }

        btnSubscriptionToday.setOnClickListener {
            selectedChargeDate = isoDateWithOffset(0)
            updateSubscriptionDateUi()
        }

        btnSubscriptionTomorrow.setOnClickListener {
            selectedChargeDate = isoDateWithOffset(1)
            updateSubscriptionDateUi()
        }

        btnSubscriptionDate.setOnClickListener {
            showDatePicker(selectedChargeDate) { pickedDate ->
                selectedChargeDate = pickedDate
                updateSubscriptionDateUi()
            }
        }

        btnPrimary.setOnClickListener {
            val name = etName.text.toString().trim().ifEmpty { getString(R.string.subscription) }
            val amount = parsePositiveAmount(etAmount) ?: return@setOnClickListener
            val period = if (spinnerPeriod.selectedItemPosition == 0) "monthly" else "yearly"
            val subscription = Subscription(
                name = name,
                amount = amount,
                period = period,
                nextChargeDate = selectedChargeDate,
                accountId = accountValues[spinnerSubscriptionAccount.selectedItemPosition].id,
                remindersEnabled = cbReminder.isChecked,
                reminderDaysBefore = getSelectedReminderDays(spinnerReminderDays)
            )
            subscriptions.add(subscription)
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
            showSavedSnackbar {
                subscriptions.remove(subscription)
                populateItems()
                saveData()
                updateTotals()
                refreshPagedContent()
            }
        }

        dialog.show()
        styleBottomSheet(dialog)
        focusAmountInput(etAmount)
    }

    private fun showEditSubscriptionDialog(subscriptionIndex: Int) {
        if (subscriptionIndex !in subscriptions.indices) return

        val subscription = subscriptions[subscriptionIndex]
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subscription, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvSubscriptionDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val layoutPresets = dialogView.findViewById<LinearLayout>(R.id.layoutSubscriptionPresets)
        val spinnerPeriod = dialogView.findViewById<Spinner>(R.id.spinnerPeriod)
        val spinnerSubscriptionAccount = dialogView.findViewById<Spinner>(R.id.spinnerSubscriptionAccount)
        val btnSubscriptionDate = dialogView.findViewById<Button>(R.id.btnSubscriptionDate)
        val btnSubscriptionToday = dialogView.findViewById<Button>(R.id.btnSubscriptionToday)
        val btnSubscriptionTomorrow = dialogView.findViewById<Button>(R.id.btnSubscriptionTomorrow)
        val btnToggleDetails = dialogView.findViewById<Button>(R.id.btnToggleSubscriptionDetails)
        val layoutDetails = dialogView.findViewById<LinearLayout>(R.id.layoutSubscriptionDetails)
        val cbReminder = dialogView.findViewById<CheckBox>(R.id.cbSubscriptionReminder)
        val spinnerReminderDays = dialogView.findViewById<Spinner>(R.id.spinnerSubscriptionReminderDays)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelSubscriptionDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteSubscriptionDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimarySubscriptionDialog)
        var selectedChargeDate: String? = subscription.nextChargeDate ?: isoDateFormatter().format(Date())
        var selectedLifecycleStatus = normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus)
        var selectedLifecycleDate = subscription.lifecycleDate?.takeIf { parseIsoDate(it) != null }
        if (selectedLifecycleStatus != SUBSCRIPTION_STATUS_ACTIVE && selectedLifecycleDate == null) {
            selectedLifecycleDate = isoDateWithOffset(0)
        }

        tvTitle.text = getString(R.string.edit_subscription)
        btnDelete.visibility = View.VISIBLE
        btnPrimary.text = getString(R.string.save_changes)

        val periods = arrayOf(getString(R.string.monthly), getString(R.string.yearly))
        spinnerPeriod.adapter = createSpinnerAdapter(periods.toList())
        val accountValues = getPaymentAccountOptions()
        val accountLabels = accountValues.map(::getPaymentAccountDisplayName)
        spinnerSubscriptionAccount.adapter = createSpinnerAdapter(accountLabels)
        val reminderLabels = getReminderDaysLabels()
        spinnerReminderDays.adapter = createSpinnerAdapter(reminderLabels)
        useBottomSheetPicker(spinnerSubscriptionAccount, accountLabels)
        useBottomSheetPicker(spinnerPeriod, periods.toList())
        useBottomSheetPicker(spinnerReminderDays, reminderLabels)

        etName.setText(subscription.name)
        etAmount.setText(subscription.amount.toString())
        setupSubscriptionServiceSearch(etName, layoutPresets)
        spinnerPeriod.setSelection(if (subscription.period == "monthly") 0 else 1)
        spinnerSubscriptionAccount.setSelection(accountValues.indexOfFirst { it.id == subscription.accountId }.takeIf { it >= 0 } ?: 0)
        cbReminder.isChecked = subscription.remindersEnabled
        setReminderDaysSelection(spinnerReminderDays, subscription.reminderDaysBefore)
        fun updateSubscriptionDateUi() {
            updateDateButton(btnSubscriptionDate, selectedChargeDate, R.string.select_charge_date)
            updateDateShortcutButtons(
                selectedChargeDate,
                btnSubscriptionToday to 0,
                btnSubscriptionTomorrow to 1
            )
        }
        updateSubscriptionDateUi()

        val btnLifecycleStatus = createSubscriptionDetailsButton()
        val btnLifecycleDate = createSubscriptionDetailsButton()
        fun refreshLifecycleButtons() {
            btnLifecycleStatus.text = getString(
                R.string.subscription_status_button,
                getSubscriptionLifecycleLabel(selectedLifecycleStatus)
            )
            btnLifecycleDate.visibility = if (selectedLifecycleStatus == SUBSCRIPTION_STATUS_ACTIVE) {
                View.GONE
            } else {
                View.VISIBLE
            }
            updateDateButton(btnLifecycleDate, selectedLifecycleDate, R.string.subscription_status_date)
        }
        layoutDetails.addView(btnLifecycleStatus, 0)
        layoutDetails.addView(btnLifecycleDate, 1)
        refreshLifecycleButtons()

        var detailsVisible = selectedLifecycleStatus != SUBSCRIPTION_STATUS_ACTIVE ||
            !subscription.remindersEnabled ||
            subscription.reminderDaysBefore != 1
        setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnToggleDetails.setOnClickListener {
            detailsVisible = !detailsVisible
            setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)
        }

        btnSubscriptionToday.setOnClickListener {
            selectedChargeDate = isoDateWithOffset(0)
            updateSubscriptionDateUi()
        }

        btnSubscriptionTomorrow.setOnClickListener {
            selectedChargeDate = isoDateWithOffset(1)
            updateSubscriptionDateUi()
        }

        btnSubscriptionDate.setOnClickListener {
            showDatePicker(selectedChargeDate) { pickedDate ->
                selectedChargeDate = pickedDate
                updateSubscriptionDateUi()
            }
        }

        btnLifecycleStatus.setOnClickListener {
            val statuses = getSubscriptionLifecycleStatuses()
            val labels = statuses.map(::getSubscriptionLifecycleLabel)
            val selectedIndex = statuses.indexOf(selectedLifecycleStatus).takeIf { it >= 0 } ?: 0
            showOptionBottomSheet(labels, selectedIndex) { index ->
                selectedLifecycleStatus = statuses[index]
                selectedLifecycleDate = if (selectedLifecycleStatus == SUBSCRIPTION_STATUS_ACTIVE) {
                    null
                } else {
                    selectedLifecycleDate ?: isoDateWithOffset(0)
                }
                refreshLifecycleButtons()
            }
        }

        btnLifecycleDate.setOnClickListener {
            showDatePicker(selectedLifecycleDate ?: isoDateWithOffset(0)) { pickedDate ->
                selectedLifecycleDate = pickedDate
                refreshLifecycleButtons()
            }
        }

        btnDelete.setOnClickListener {
            val subscriptionName = subscriptions[subscriptionIndex].name
            showDeleteConfirmation(getString(R.string.delete_subscription_message, subscriptionName)) {
                subscriptions.removeAt(subscriptionIndex)
                populateItems()
                saveData()
                updateTotals()
                refreshPagedContent()
            }
            dialog.dismiss()
        }

        btnPrimary.setOnClickListener {
            val updatedName = etName.text.toString().trim().ifEmpty { getString(R.string.subscription) }
            val updatedAmount = parsePositiveAmount(etAmount) ?: return@setOnClickListener
            val updatedPeriod = if (spinnerPeriod.selectedItemPosition == 0) "monthly" else "yearly"
            subscriptions[subscriptionIndex] = Subscription(
                name = updatedName,
                amount = updatedAmount,
                period = updatedPeriod,
                nextChargeDate = selectedChargeDate,
                accountId = accountValues[spinnerSubscriptionAccount.selectedItemPosition].id,
                id = subscription.id,
                remindersEnabled = cbReminder.isChecked,
                reminderDaysBefore = getSelectedReminderDays(spinnerReminderDays),
                lifecycleStatus = selectedLifecycleStatus,
                lifecycleDate = selectedLifecycleDate.takeIf { selectedLifecycleStatus != SUBSCRIPTION_STATUS_ACTIVE },
                paidDates = subscription.paidDates,
                skippedDates = subscription.skippedDates,
                occurrenceOverrides = subscription.occurrenceOverrides
            )
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun setupSubscriptionServiceSearch(etName: EditText, container: LinearLayout) {
        val services = getPopularSubscriptionServices()
        var updatingFromPreset = false

        fun render(query: String) {
            val normalizedQuery = query.trim()
            val filteredServices = services
                .filter { service ->
                    normalizedQuery.isEmpty() || service.contains(normalizedQuery, ignoreCase = true)
                }
                .take(14)
            val selectedIndex = filteredServices.indexOfFirst {
                it.equals(normalizedQuery, ignoreCase = true)
            }

            container.visibility = if (filteredServices.isEmpty()) View.GONE else View.VISIBLE
            addQuickOptionButtons(container, filteredServices, selectedIndex) { _, service ->
                updatingFromPreset = true
                etName.setText(service)
                etName.setSelection(service.length)
                updatingFromPreset = false
                render(service)
            }
        }

        render(etName.text.toString())
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!updatingFromPreset) {
                    render(s?.toString().orEmpty())
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun getPopularSubscriptionServices(): List<String> {
        return listOf(
            "Netflix",
            "Spotify",
            "YouTube Premium",
            "Apple Music",
            "iCloud+",
            "Google One",
            "Microsoft 365",
            "Amazon Prime",
            "Disney+",
            "Max",
            "PlayStation Plus",
            "Xbox Game Pass",
            "Adobe",
            "Telegram Premium",
            "ChatGPT Plus",
            "GitHub Copilot",
            "Notion",
            "Figma",
            "Dropbox",
            "VPN",
            "Internet",
            "Mobile plan"
        ).distinct()
    }

    private fun showRecurringExpenseDialog(recurringIndex: Int? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_recurring_expense, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvRecurringExpenseDialogTitle)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerRecurringCategory)
        val btnCategoryPicker = dialogView.findViewById<Button>(R.id.btnRecurringCategoryPicker)
        val layoutQuickCategories = dialogView.findViewById<LinearLayout>(R.id.layoutQuickRecurringCategories)
        val spinnerAccount = dialogView.findViewById<Spinner>(R.id.spinnerRecurringAccount)
        val spinnerPeriod = dialogView.findViewById<Spinner>(R.id.spinnerRecurringPeriod)
        val etName = dialogView.findViewById<EditText>(R.id.etRecurringName)
        val etSubcategory = dialogView.findViewById<EditText>(R.id.etRecurringSubcategory)
        val etTags = dialogView.findViewById<EditText>(R.id.etRecurringTags)
        val btnChooseSubcategory = dialogView.findViewById<Button>(R.id.btnChooseRecurringSubcategory)
        val btnChooseTags = dialogView.findViewById<Button>(R.id.btnChooseRecurringTags)
        val etAmount = dialogView.findViewById<EditText>(R.id.etRecurringAmount)
        val btnDate = dialogView.findViewById<Button>(R.id.btnRecurringDate)
        val btnToday = dialogView.findViewById<Button>(R.id.btnRecurringToday)
        val btnTomorrow = dialogView.findViewById<Button>(R.id.btnRecurringTomorrow)
        val btnToggleDetails = dialogView.findViewById<Button>(R.id.btnToggleRecurringDetails)
        val layoutDetails = dialogView.findViewById<LinearLayout>(R.id.layoutRecurringDetails)
        val cbReminder = dialogView.findViewById<CheckBox>(R.id.cbRecurringReminder)
        val spinnerReminderDays = dialogView.findViewById<Spinner>(R.id.spinnerRecurringReminderDays)
        val cbFavorite = dialogView.findViewById<CheckBox>(R.id.cbRecurringFavorite)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelRecurringDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteRecurringDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryRecurringDialog)
        val existing = recurringIndex?.takeIf { it in recurringExpenses.indices }?.let { recurringExpenses[it] }
        var selectedStartDate = existing?.startDate ?: isoDateFormatter().format(Date())

        val categoryOptions = getCategoryOptions()
        spinnerCategory.adapter = createSpinnerAdapter(categoryOptions)
        val quickCategories = categoryOptions
            .filterNot { it == getString(R.string.general_category) }
            .take(8)
        val accountValues = getPaymentAccountOptions()
        val accountLabels = accountValues.map(::getPaymentAccountDisplayName)
        spinnerAccount.adapter = createSpinnerAdapter(accountLabels)
        val periods = arrayOf(getString(R.string.monthly), getString(R.string.yearly))
        spinnerPeriod.adapter = createSpinnerAdapter(periods.toList())
        val reminderLabels = getReminderDaysLabels()
        spinnerReminderDays.adapter = createSpinnerAdapter(reminderLabels)
        useBottomSheetPicker(spinnerAccount, accountLabels)
        useBottomSheetPicker(spinnerPeriod, periods.toList())
        useBottomSheetPicker(spinnerReminderDays, reminderLabels)

        fun updateCategoryPickerText() {
            btnCategoryPicker.text = spinnerCategory.selectedItem?.toString().orEmpty()
                .ifEmpty { getString(R.string.general_category) }
        }

        fun selectCategory(category: String?) {
            val label = category?.takeIf { it.isNotBlank() } ?: getString(R.string.general_category)
            val index = categoryOptions.indexOf(label)
            if (index >= 0) {
                spinnerCategory.setSelection(index)
                updateCategoryPickerText()
            }
        }

        fun renderCategoryQuickOptions() {
            val selectedQuickIndex = quickCategories.indexOf(
                categoryOptions.getOrNull(spinnerCategory.selectedItemPosition).orEmpty()
            )
            addQuickOptionButtons(layoutQuickCategories, quickCategories, selectedQuickIndex) { _, category ->
                selectCategory(category)
            }
        }

        if (existing != null) {
            tvTitle.text = getString(R.string.edit_recurring_expense)
            btnDelete.visibility = View.VISIBLE
            btnPrimary.text = getString(R.string.save_changes)
            etName.setText(existing.name)
            etSubcategory.setText(existing.subcategory.orEmpty())
            etTags.setText(tagsToInput(existing.tags))
            etAmount.setText(existing.amount.toString())
            selectCategory(existing.category)
            spinnerAccount.setSelection(accountValues.indexOfFirst { it.id == existing.accountId }.takeIf { it >= 0 } ?: 0)
            spinnerPeriod.setSelection(if (existing.period == "yearly") 1 else 0)
            cbReminder.isChecked = existing.remindersEnabled
            setReminderDaysSelection(spinnerReminderDays, existing.reminderDaysBefore)
            cbFavorite.isChecked = existing.favorite
        } else {
            tvTitle.text = getString(R.string.add_recurring_expense)
            btnDelete.visibility = View.GONE
            btnPrimary.text = getString(R.string.add_recurring_expense)
            cbReminder.isChecked = true
            setReminderDaysSelection(spinnerReminderDays, 1)
        }
        fun updateRecurringDateUi() {
            updateDateButton(btnDate, selectedStartDate, R.string.select_start_date)
            updateDateShortcutButtons(
                selectedStartDate,
                btnToday to 0,
                btnTomorrow to 1
            )
        }
        updateRecurringDateUi()
        var detailsVisible = existing?.let {
            !it.subcategory.isNullOrBlank() ||
                it.tags.isNotEmpty() ||
                !it.remindersEnabled ||
                it.reminderDaysBefore != 1 ||
                it.favorite
        } ?: false
        setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)
        renderCategoryQuickOptions()
        updateCategoryPickerText()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnToggleDetails.setOnClickListener {
            detailsVisible = !detailsVisible
            setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)
        }
        btnToday.setOnClickListener {
            selectedStartDate = isoDateWithOffset(0)
            updateRecurringDateUi()
        }
        btnTomorrow.setOnClickListener {
            selectedStartDate = isoDateWithOffset(1)
            updateRecurringDateUi()
        }
        btnDate.setOnClickListener {
            showDatePicker(selectedStartDate) { pickedDate ->
                selectedStartDate = pickedDate
                updateRecurringDateUi()
            }
        }
        btnCategoryPicker.setOnClickListener {
            showOptionBottomSheet(categoryOptions, spinnerCategory.selectedItemPosition) { which ->
                spinnerCategory.setSelection(which)
                updateCategoryPickerText()
                addQuickOptionButtons(
                    layoutQuickCategories,
                    quickCategories,
                    quickCategories.indexOf(categoryOptions[which])
                ) { _, category ->
                    val optionIndex = categoryOptions.indexOf(category)
                    if (optionIndex >= 0) {
                        spinnerCategory.setSelection(optionIndex)
                        updateCategoryPickerText()
                    }
                }
            }
        }
        btnChooseSubcategory.setOnClickListener { showSubcategoryPicker(etSubcategory) }
        btnChooseTags.setOnClickListener { showTagPicker(etTags) }

        btnDelete.setOnClickListener {
            val index = recurringIndex ?: return@setOnClickListener
            if (index in recurringExpenses.indices) {
                val name = recurringExpenses[index].name
                showDeleteConfirmation(getString(R.string.delete_recurring_expense_message, name)) {
                    recurringExpenses.removeAt(index)
                    populateItems()
                    saveData()
                    updateTotals()
                    refreshPagedContent()
                }
            }
            dialog.dismiss()
        }

        btnPrimary.setOnClickListener {
            val selectedCategory = spinnerCategory.selectedItem?.toString().orEmpty()
            val normalizedCategory = if (selectedCategory == getString(R.string.general_category)) null else selectedCategory
            val subcategory = normalizeOptionalText(etSubcategory.text.toString())
            val tags = parseTags(etTags.text.toString())
            addCategoryIfNeeded(normalizedCategory)
            val typedName = etName.text.toString().trim()
            val name = typedName.ifEmpty {
                normalizedCategory ?: getString(R.string.recurring_expense)
            }
            val amount = parsePositiveAmount(etAmount) ?: return@setOnClickListener
            val period = if (spinnerPeriod.selectedItemPosition == 1) "yearly" else "monthly"
            val updated = RecurringExpense(
                name = name,
                amount = amount,
                category = normalizedCategory,
                period = period,
                startDate = selectedStartDate,
                accountId = accountValues[spinnerAccount.selectedItemPosition].id,
                favorite = cbFavorite.isChecked,
                id = existing?.id ?: System.currentTimeMillis(),
                remindersEnabled = cbReminder.isChecked,
                reminderDaysBefore = getSelectedReminderDays(spinnerReminderDays),
                paidDates = existing?.paidDates.orEmpty(),
                skippedDates = existing?.skippedDates.orEmpty(),
                subcategory = subcategory,
                tags = tags,
                occurrenceOverrides = existing?.occurrenceOverrides.orEmpty()
            )
            if (recurringIndex != null && recurringIndex in recurringExpenses.indices) {
                recurringExpenses[recurringIndex] = updated
            } else {
                recurringExpenses.add(updated)
            }
            rememberTagsAndSubcategoriesFrom(emptyList(), listOf(updated))
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
            if (recurringIndex == null) {
                showSavedSnackbar {
                    recurringExpenses.remove(updated)
                    rememberTagsAndSubcategoriesFrom(expenses, recurringExpenses)
                    populateItems()
                    saveData()
                    updateTotals()
                    refreshPagedContent()
                }
            } else {
                showSavedSnackbar()
            }
        }

        dialog.show()
        styleBottomSheet(dialog)
        if (recurringIndex == null) {
            focusAmountInput(etAmount)
        }
    }

    private fun showAddExpenseDialog(initialDate: String? = null) {
        showExpenseDialog(initialDate = initialDate)
    }

    private fun showTransferDialog(transferIndex: Int? = null) {
        ensureDefaultPaymentAccounts()
        val accountValues = getPaymentAccountOptions()
        if (accountValues.size < 2) {
            Toast.makeText(this, R.string.transfer_need_two_accounts, Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transfer, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTransferDialogTitle)
        val spinnerFrom = dialogView.findViewById<Spinner>(R.id.spinnerTransferFrom)
        val spinnerTo = dialogView.findViewById<Spinner>(R.id.spinnerTransferTo)
        val etAmount = dialogView.findViewById<EditText>(R.id.etTransferAmount)
        val etNote = dialogView.findViewById<EditText>(R.id.etTransferNote)
        val btnDate = dialogView.findViewById<Button>(R.id.btnTransferDate)
        val btnToday = dialogView.findViewById<Button>(R.id.btnTransferToday)
        val btnYesterday = dialogView.findViewById<Button>(R.id.btnTransferYesterday)
        val btnToggleDetails = dialogView.findViewById<Button>(R.id.btnToggleTransferDetails)
        val layoutDetails = dialogView.findViewById<LinearLayout>(R.id.layoutTransferDetails)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelTransferDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteTransferDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryTransferDialog)
        val existing = transferIndex?.let { transfers.getOrNull(it) }
        var selectedDate = existing?.date ?: isoDateFormatter().format(Date())

        val accountLabels = accountValues.map(::getPaymentAccountDisplayName)
        spinnerFrom.adapter = createSpinnerAdapter(accountLabels)
        spinnerTo.adapter = createSpinnerAdapter(accountLabels)
        useBottomSheetPicker(spinnerFrom, accountLabels)
        useBottomSheetPicker(spinnerTo, accountLabels)
        spinnerFrom.setSelection(accountValues.indexOfFirst { it.id == existing?.fromAccountId }.takeIf { it >= 0 } ?: 0)
        spinnerTo.setSelection(
            accountValues.indexOfFirst { it.id == existing?.toAccountId }.takeIf { it >= 0 }
                ?: 1.coerceAtMost(accountValues.lastIndex)
        )

        if (existing != null) {
            tvTitle.text = getString(R.string.edit_transfer)
            btnPrimary.text = getString(R.string.save_changes)
            etAmount.setText(existing.amount.toString())
            etNote.setText(existing.note.orEmpty())
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
        }
        fun updateTransferDateUi() {
            updateDateButton(btnDate, selectedDate, R.string.select_transfer_date)
            updateDateShortcutButtons(
                selectedDate,
                btnToday to 0,
                btnYesterday to -1
            )
        }
        updateTransferDateUi()
        var detailsVisible = !existing?.note.isNullOrBlank()
        setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnToggleDetails.setOnClickListener {
            detailsVisible = !detailsVisible
            setDetailsVisible(layoutDetails, btnToggleDetails, detailsVisible)
        }

        btnToday.setOnClickListener {
            selectedDate = isoDateWithOffset(0)
            updateTransferDateUi()
        }

        btnYesterday.setOnClickListener {
            selectedDate = isoDateWithOffset(-1)
            updateTransferDateUi()
        }

        btnDate.setOnClickListener {
            showDatePicker(selectedDate) { pickedDate ->
                selectedDate = pickedDate
                updateTransferDateUi()
            }
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnDelete.setOnClickListener {
            if (transferIndex != null && transferIndex in transfers.indices) {
                transfers.removeAt(transferIndex)
                saveData()
                populateItems()
                updateTotals()
                refreshPagedContent()
            }
            dialog.dismiss()
        }

        btnPrimary.setOnClickListener {
            val fromAccount = accountValues[spinnerFrom.selectedItemPosition]
            val toAccount = accountValues[spinnerTo.selectedItemPosition]
            if (fromAccount.id == toAccount.id) {
                Toast.makeText(this, R.string.transfer_same_account_error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val amount = parsePositiveAmount(etAmount) ?: return@setOnClickListener
            val updatedTransfer = AccountTransfer(
                fromAccountId = fromAccount.id,
                toAccountId = toAccount.id,
                amount = amount,
                date = selectedDate,
                note = normalizeOptionalText(etNote.text.toString()),
                id = existing?.id ?: System.currentTimeMillis()
            )
            if (transferIndex != null && transferIndex in transfers.indices) {
                transfers[transferIndex] = updatedTransfer
            } else {
                transfers.add(updatedTransfer)
            }
            saveData()
            populateItems()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
            if (transferIndex == null) {
                showSavedSnackbar {
                    transfers.remove(updatedTransfer)
                    saveData()
                    populateItems()
                    updateTotals()
                    refreshPagedContent()
                }
            } else {
                showSavedSnackbar()
            }
        }

        dialog.show()
        styleBottomSheet(dialog)
        if (transferIndex == null) {
            focusAmountInput(etAmount)
        }
    }

    private fun showExpenseDialog(expenseIndex: Int? = null, initialDate: String? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvExpenseDialogTitle)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val btnCategoryPicker = dialogView.findViewById<Button>(R.id.btnExpenseCategoryPicker)
        val spinnerExpenseAccount = dialogView.findViewById<Spinner>(R.id.spinnerExpenseAccount)
        val layoutQuickCategories = dialogView.findViewById<LinearLayout>(R.id.layoutQuickExpenseCategories)
        val layoutAdvanced = dialogView.findViewById<LinearLayout>(R.id.layoutExpenseAdvanced)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etSubcategory = dialogView.findViewById<EditText>(R.id.etExpenseSubcategory)
        val etTags = dialogView.findViewById<EditText>(R.id.etExpenseTags)
        val btnChooseSubcategory = dialogView.findViewById<Button>(R.id.btnChooseExpenseSubcategory)
        val btnChooseTags = dialogView.findViewById<Button>(R.id.btnChooseExpenseTags)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val btnExpenseDate = dialogView.findViewById<Button>(R.id.btnExpenseDate)
        val btnExpenseToday = dialogView.findViewById<Button>(R.id.btnExpenseToday)
        val btnExpenseYesterday = dialogView.findViewById<Button>(R.id.btnExpenseYesterday)
        val btnToggleDetails = dialogView.findViewById<Button>(R.id.btnToggleExpenseDetails)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelExpenseDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteExpenseDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryExpenseDialog)
        var selectedExpenseDate = initialDate ?: isoDateFormatter().format(Date())
        var detailsVisible = false

        val categoryOptions = getCategoryOptions()
        spinnerCategory.adapter = createSpinnerAdapter(categoryOptions)
        val quickCategories = categoryOptions
            .filterNot { it == getString(R.string.general_category) }
            .take(8)
        val accountValues = getPaymentAccountOptions()
        val accountLabels = accountValues.map(::getPaymentAccountDisplayName)
        spinnerExpenseAccount.adapter = createSpinnerAdapter(accountLabels)
        useBottomSheetPicker(spinnerExpenseAccount, accountLabels)

        fun updateCategoryPickerText() {
            btnCategoryPicker.text = spinnerCategory.selectedItem?.toString().orEmpty()
                .ifEmpty { getString(R.string.general_category) }
        }

        if (expenseIndex != null && expenseIndex in expenses.indices) {
            val expense = expenses[expenseIndex]
            tvTitle.text = getString(R.string.edit_expense)
            btnDelete.visibility = View.VISIBLE
            btnPrimary.text = getString(R.string.save_changes)
            etName.setText(expense.name)
            etSubcategory.setText(expense.subcategory.orEmpty())
            etTags.setText(tagsToInput(expense.tags))
            etAmount.setText(expense.amount.toString())
            val selectedCategory = expense.category ?: getString(R.string.general_category)
            spinnerCategory.setSelection(categoryOptions.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0)
            spinnerExpenseAccount.setSelection(accountValues.indexOfFirst { it.id == expense.accountId }.takeIf { it >= 0 } ?: 0)
            selectedExpenseDate = parseIsoDate(expense.date)?.let { isoDateFormatter().format(it.time) }
                ?: isoDateFormatter().format(Date())
            detailsVisible = !expense.subcategory.isNullOrBlank() || expense.tags.isNotEmpty()
        } else {
            tvTitle.text = getString(R.string.add_expense)
            btnDelete.visibility = View.GONE
            btnPrimary.text = getString(R.string.add_expense)
        }
        val selectedQuickCategoryIndex = quickCategories.indexOf(
            categoryOptions.getOrNull(spinnerCategory.selectedItemPosition).orEmpty()
        )
        addQuickOptionButtons(layoutQuickCategories, quickCategories, selectedQuickCategoryIndex) { _, category ->
            val optionIndex = categoryOptions.indexOf(category)
            if (optionIndex >= 0) {
                spinnerCategory.setSelection(optionIndex)
                updateCategoryPickerText()
            }
        }
        updateCategoryPickerText()
        fun updateExpenseDateUi() {
            updateDateButton(btnExpenseDate, selectedExpenseDate, R.string.select_expense_date)
            updateDateShortcutButtons(
                selectedExpenseDate,
                btnExpenseToday to 0,
                btnExpenseYesterday to -1
            )
        }
        updateExpenseDateUi()
        setDetailsVisible(layoutAdvanced, btnToggleDetails, detailsVisible)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnToggleDetails.setOnClickListener {
            detailsVisible = !detailsVisible
            setDetailsVisible(layoutAdvanced, btnToggleDetails, detailsVisible)
        }

        btnExpenseToday.setOnClickListener {
            selectedExpenseDate = isoDateWithOffset(0)
            updateExpenseDateUi()
        }

        btnExpenseYesterday.setOnClickListener {
            selectedExpenseDate = isoDateWithOffset(-1)
            updateExpenseDateUi()
        }

        btnExpenseDate.setOnClickListener {
            showDatePicker(selectedExpenseDate) { pickedDate ->
                selectedExpenseDate = pickedDate
                updateExpenseDateUi()
            }
        }
        btnCategoryPicker.setOnClickListener {
            showOptionBottomSheet(categoryOptions, spinnerCategory.selectedItemPosition) { which ->
                spinnerCategory.setSelection(which)
                updateCategoryPickerText()
                addQuickOptionButtons(
                    layoutQuickCategories,
                    quickCategories,
                    quickCategories.indexOf(categoryOptions[which])
                ) { _, category ->
                    val optionIndex = categoryOptions.indexOf(category)
                    if (optionIndex >= 0) {
                        spinnerCategory.setSelection(optionIndex)
                        updateCategoryPickerText()
                    }
                }
            }
        }
        btnChooseSubcategory.setOnClickListener { showSubcategoryPicker(etSubcategory) }
        btnChooseTags.setOnClickListener { showTagPicker(etTags) }

        btnDelete.setOnClickListener {
            if (expenseIndex != null && expenseIndex in expenses.indices) {
                val expenseName = expenses[expenseIndex].name
                showDeleteConfirmation(getString(R.string.delete_expense_message, expenseName)) {
                    expenses.removeAt(expenseIndex)
                    populateItems()
                    saveData()
                    updateTotals()
                    refreshPagedContent()
                }
            }
            dialog.dismiss()
        }

        btnPrimary.setOnClickListener {
            val selectedCategory = spinnerCategory.selectedItem?.toString().orEmpty()
            val normalizedCategory = if (selectedCategory == getString(R.string.general_category)) null else selectedCategory
            val subcategory = normalizeOptionalText(etSubcategory.text.toString())
            val tags = parseTags(etTags.text.toString())
            addCategoryIfNeeded(normalizedCategory)
            val typedName = etName.text.toString().trim()
            val name = when {
                typedName.isNotEmpty() -> typedName
                normalizedCategory != null -> normalizedCategory
                else -> getString(R.string.expense_default_name)
            }
            val amount = parsePositiveAmount(etAmount) ?: return@setOnClickListener
            val updatedExpense = Expense(
                name = name,
                amount = amount,
                date = selectedExpenseDate,
                category = normalizedCategory,
                accountId = accountValues[spinnerExpenseAccount.selectedItemPosition].id,
                subcategory = subcategory,
                tags = tags
            )
            if (expenseIndex != null && expenseIndex in expenses.indices) {
                expenses[expenseIndex] = updatedExpense
            } else {
                expenses.add(updatedExpense)
            }
            rememberTagsAndSubcategoriesFrom(listOf(updatedExpense), emptyList())
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
            if (expenseIndex == null) {
                showSavedSnackbar {
                    expenses.remove(updatedExpense)
                    rememberTagsAndSubcategoriesFrom(expenses, recurringExpenses)
                    populateItems()
                    saveData()
                    updateTotals()
                    refreshPagedContent()
                }
            } else {
                showSavedSnackbar()
            }
        }

        dialog.show()
        styleBottomSheet(dialog)
        if (expenseIndex == null) {
            focusAmountInput(etAmount)
        }
    }

    private fun getIncomeTypeOptions(): List<String> {
        return listOf(
            getString(R.string.income_salary),
            getString(R.string.income_freelance),
            getString(R.string.income_bonus),
            getString(R.string.income_other)
        )
    }

    private fun getIncomeTypeKey(value: String): String {
        return when (value.trim().lowercase(Locale.getDefault())) {
            "salary", "gehalt", "зарплата" -> "salary"
            "freelance", "фриланс" -> "freelance"
            "bonus", "премия" -> "bonus"
            "other", "sonstiges", "другое" -> "other"
            else -> value
        }
    }

    private fun getIncomeTypeDisplayName(value: String): String {
        return when (getIncomeTypeKey(value)) {
            "salary" -> getString(R.string.income_salary)
            "freelance" -> getString(R.string.income_freelance)
            "bonus" -> getString(R.string.income_bonus)
            "other" -> getString(R.string.income_other)
            else -> value
        }
    }

    private fun getIncomePeriodOptions(): List<String> {
        return listOf(
            getString(R.string.once),
            getString(R.string.monthly)
        )
    }

    private fun getCategoryOptions(): MutableList<String> {
        val favoriteOptions = categories
            .filter { favoriteCategories.contains(it) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
        val regularOptions = categories
            .filterNot { favoriteCategories.contains(it) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
        val popularOptions = getPopularCategoryNames()
            .filterNot { categories.contains(it) }
        return mutableListOf(getString(R.string.general_category)).apply {
            addAll(favoriteOptions)
            addAll(regularOptions)
            addAll(popularOptions)
        }
    }

    private fun getCategoryDisplayName(category: String): String {
        return if (favoriteCategories.contains(category)) {
            "${getString(R.string.favorite_marker)} $category"
        } else {
            category
        }
    }

    private fun showPopularCategoryPicker(onSelected: (String) -> Unit) {
        val popularCategories = getPopularCategoryNames()
        showChoiceBottomSheet(getString(R.string.choose_popular_category), popularCategories, onSelected)
    }

    private fun addCategoryIfNeeded(category: String?) {
        val safeCategory = category?.takeIf { it.isNotBlank() && it != getString(R.string.general_category) } ?: return
        if (!categories.contains(safeCategory)) {
            categories.add(safeCategory)
        }
    }

    private fun getPopularCategoryNames(): List<String> {
        return listOf(
            getString(R.string.category_food),
            getString(R.string.category_transport),
            getString(R.string.category_housing),
            getString(R.string.category_rent),
            getString(R.string.category_utilities),
            getString(R.string.category_health),
            getString(R.string.category_insurance),
            getString(R.string.category_credit_loans),
            getString(R.string.category_shopping),
            getString(R.string.category_entertainment),
            getString(R.string.category_internet_phone),
            getString(R.string.category_education),
            getString(R.string.category_travel),
            getString(R.string.category_transfers)
        ).distinct()
    }

    private data class CsvImportData(
        val incomes: MutableList<Income> = mutableListOf(),
        val subscriptions: MutableList<Subscription> = mutableListOf(),
        val recurringExpenses: MutableList<RecurringExpense> = mutableListOf(),
        val expenses: MutableList<Expense> = mutableListOf(),
        val transfers: MutableList<AccountTransfer> = mutableListOf()
    ) {
        fun isEmpty(): Boolean {
            return incomes.isEmpty() &&
                subscriptions.isEmpty() &&
                recurringExpenses.isEmpty() &&
                expenses.isEmpty() &&
                transfers.isEmpty()
        }

        fun totalCount(): Int {
            return incomes.size + subscriptions.size + recurringExpenses.size + expenses.size + transfers.size
        }
    }

    private fun sanitizeIncome(income: Income): Income {
        val safeName = runCatching { income.name }.getOrNull().orEmpty().ifBlank { getString(R.string.income_default_name) }
        val safeType = getIncomeTypeKey(runCatching { income.type }.getOrNull().orEmpty())
        val safePeriod = if (runCatching { income.period }.getOrNull() == "monthly") "monthly" else "once"
        val safeDate = runCatching { income.date }.getOrNull().orEmpty()
        val safeExpectedDate = runCatching { income.expectedDate }.getOrNull()
        return Income(
            name = safeName,
            amount = income.amount,
            date = safeDate,
            type = safeType,
            expectedDate = safeExpectedDate,
            period = safePeriod,
            accountId = resolvePaymentAccountId(runCatching { income.accountId }.getOrNull(), runCatching { income.accountSource }.getOrNull()),
            accountSource = null
        )
    }

    private fun sanitizePaymentId(candidate: Long?, seed: String): Long {
        return candidate?.takeIf { it > 0L } ?: (System.currentTimeMillis() + abs(seed.hashCode().toLong()))
    }

    private fun sanitizeReminderDays(days: Int): Int {
        return getReminderDaysOptions().firstOrNull { it == days } ?: 1
    }

    private fun sanitizeOccurrenceDates(values: List<String>?): List<String> {
        return values.orEmpty()
            .filter { parseIsoDate(it) != null }
            .distinct()
    }

    private fun sanitizePaymentOverrides(values: List<PaymentOccurrenceOverride>?): List<PaymentOccurrenceOverride> {
        return values.orEmpty()
            .mapNotNull { paymentOverride ->
                val originalDate = paymentOverride.originalDate.takeIf { parseIsoDate(it) != null } ?: return@mapNotNull null
                val date = paymentOverride.date?.takeIf { parseIsoDate(it) != null && it != originalDate }
                val amount = paymentOverride.amount?.takeIf { it > 0.0 }
                if (date == null && amount == null) {
                    null
                } else {
                    PaymentOccurrenceOverride(originalDate = originalDate, date = date, amount = amount)
                }
            }
            .distinctBy { it.originalDate }
    }

    private fun sanitizeSubscription(subscription: Subscription): Subscription {
        val safeName = runCatching { subscription.name }.getOrNull().orEmpty().ifBlank { getString(R.string.subscription) }
        val safePeriod = if (runCatching { subscription.period }.getOrNull() == "yearly") "yearly" else "monthly"
        val safeDate = runCatching { subscription.nextChargeDate }.getOrNull()
        val safePaidDates = sanitizeOccurrenceDates(runCatching { subscription.paidDates }.getOrNull())
        val safeSkippedDates = sanitizeOccurrenceDates(runCatching { subscription.skippedDates }.getOrNull())
        val safeOverrides = sanitizePaymentOverrides(runCatching { subscription.occurrenceOverrides }.getOrNull())
        val safeLifecycleStatus = normalizeSubscriptionLifecycleStatus(
            runCatching { subscription.lifecycleStatus }.getOrNull()
        )
        val safeLifecycleDate = if (safeLifecycleStatus == SUBSCRIPTION_STATUS_ACTIVE) {
            null
        } else {
            runCatching { subscription.lifecycleDate }.getOrNull()
                ?.takeIf { parseIsoDate(it) != null }
                ?: isoDateWithOffset(0)
        }
        return Subscription(
            name = safeName,
            amount = subscription.amount,
            period = safePeriod,
            nextChargeDate = safeDate,
            accountId = resolvePaymentAccountId(runCatching { subscription.accountId }.getOrNull(), runCatching { subscription.accountSource }.getOrNull()),
            accountSource = null,
            id = sanitizePaymentId(runCatching { subscription.id }.getOrNull(), "$safeName-$safeDate"),
            remindersEnabled = runCatching { subscription.remindersEnabled }.getOrDefault(true),
            reminderDaysBefore = sanitizeReminderDays(runCatching { subscription.reminderDaysBefore }.getOrDefault(1)),
            lifecycleStatus = safeLifecycleStatus,
            lifecycleDate = safeLifecycleDate,
            paidDates = safePaidDates,
            skippedDates = safeSkippedDates - safePaidDates.toSet(),
            occurrenceOverrides = safeOverrides
        )
    }

    private fun formatCompactSignedTotal(value: Double): String {
        return formatCompactSignedAmount(value).ifBlank { "0${getCurrencySymbol()}" }
    }

    private fun sanitizeRecurringExpense(recurringExpense: RecurringExpense): RecurringExpense {
        val safeName = runCatching { recurringExpense.name }.getOrNull().orEmpty()
            .ifBlank { getString(R.string.recurring_expense) }
        val safePeriod = if (runCatching { recurringExpense.period }.getOrNull() == "yearly") "yearly" else "monthly"
        val safeCategory = runCatching { recurringExpense.category }.getOrNull()
        val safeSubcategory = normalizeOptionalText(runCatching { recurringExpense.subcategory }.getOrNull())
        val safeTags = parseTags(runCatching { recurringExpense.tags }.getOrNull()?.joinToString(","))
        val safeStartDate = runCatching { recurringExpense.startDate }.getOrNull()
        val safePaidDates = sanitizeOccurrenceDates(runCatching { recurringExpense.paidDates }.getOrNull())
        val safeSkippedDates = sanitizeOccurrenceDates(runCatching { recurringExpense.skippedDates }.getOrNull())
        val safeOverrides = sanitizePaymentOverrides(runCatching { recurringExpense.occurrenceOverrides }.getOrNull())
        return RecurringExpense(
            name = safeName,
            amount = recurringExpense.amount,
            category = safeCategory,
            period = safePeriod,
            startDate = safeStartDate,
            accountId = resolvePaymentAccountId(
                runCatching { recurringExpense.accountId }.getOrNull(),
                runCatching { recurringExpense.accountSource }.getOrNull()
            ),
            accountSource = null,
            favorite = runCatching { recurringExpense.favorite }.getOrDefault(false),
            id = sanitizePaymentId(runCatching { recurringExpense.id }.getOrNull(), "$safeName-$safeStartDate"),
            remindersEnabled = runCatching { recurringExpense.remindersEnabled }.getOrDefault(true),
            reminderDaysBefore = sanitizeReminderDays(runCatching { recurringExpense.reminderDaysBefore }.getOrDefault(1)),
            paidDates = safePaidDates,
            skippedDates = safeSkippedDates - safePaidDates.toSet(),
            subcategory = safeSubcategory,
            tags = safeTags,
            occurrenceOverrides = safeOverrides
        )
    }

    private fun sanitizeExpense(expense: Expense): Expense {
        val safeName = runCatching { expense.name }.getOrNull().orEmpty().ifBlank { getString(R.string.expense_default_name) }
        val safeDate = runCatching { expense.date }.getOrNull().orEmpty()
        val safeCategory = runCatching { expense.category }.getOrNull()
        val safeSubcategory = normalizeOptionalText(runCatching { expense.subcategory }.getOrNull())
        val safeTags = parseTags(runCatching { expense.tags }.getOrNull()?.joinToString(","))
        return Expense(
            name = safeName,
            amount = expense.amount,
            date = safeDate,
            category = safeCategory,
            accountId = resolvePaymentAccountId(runCatching { expense.accountId }.getOrNull(), runCatching { expense.accountSource }.getOrNull()),
            accountSource = null,
            subcategory = safeSubcategory,
            tags = safeTags
        )
    }

    private fun sanitizeTransfer(transfer: AccountTransfer): AccountTransfer {
        val fromAccountId = resolvePaymentAccountId(runCatching { transfer.fromAccountId }.getOrNull(), null)
        val rawToAccountId = runCatching { transfer.toAccountId }.getOrNull()
        val toAccountId = resolvePaymentAccountId(rawToAccountId, null).takeIf { it != fromAccountId }
            ?: paymentAccounts.firstOrNull { it.id != fromAccountId }?.id
            ?: fromAccountId
        return AccountTransfer(
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amount = runCatching { transfer.amount }.getOrDefault(0.0).coerceAtLeast(0.0),
            date = runCatching { transfer.date }.getOrNull().orEmpty().ifBlank { isoDateFormatter().format(Date()) },
            note = normalizeOptionalText(runCatching { transfer.note }.getOrNull()),
            id = sanitizePaymentId(runCatching { transfer.id }.getOrNull(), "$fromAccountId-$toAccountId-${runCatching { transfer.date }.getOrNull()}")
        )
    }

    private fun sanitizePaymentAccount(account: PaymentAccount): PaymentAccount {
        val safeName = account.name.ifBlank { getString(R.string.account_default_card) }
        return account.copy(name = safeName)
    }

    private fun migrateLegacyAccounts(
        legacyBalances: Map<String, Double>,
        legacyLabels: Map<String, String>,
        rawIncomes: List<Income>,
        rawSubscriptions: List<Subscription>,
        rawExpenses: List<Expense>,
        legacyCashAmount: Double
    ): List<PaymentAccount> {
        val migrated = mutableListOf<PaymentAccount>()
        migrated.add(
            PaymentAccount(
                id = DEFAULT_CASH_ACCOUNT_ID,
                name = legacyLabels[ACCOUNT_CASH]?.takeIf { it.isNotBlank() } ?: getString(R.string.account_cash),
                type = AccountType.CASH,
                balance = legacyBalances[ACCOUNT_CASH] ?: legacyCashAmount
            )
        )
        migrated.add(
            PaymentAccount(
                id = DEFAULT_CARD_ACCOUNT_ID,
                name = legacyLabels[ACCOUNT_BANK]?.takeIf { it.isNotBlank() } ?: getString(R.string.account_default_card),
                type = AccountType.BANK_CARD,
                balance = legacyBalances[ACCOUNT_BANK] ?: 0.0
            )
        )

        val paypalUsed = rawIncomes.any { it.accountSource == ACCOUNT_PAYPAL } ||
            rawSubscriptions.any { it.accountSource == ACCOUNT_PAYPAL } ||
            rawExpenses.any { it.accountSource == ACCOUNT_PAYPAL } ||
            (legacyBalances[ACCOUNT_PAYPAL] ?: 0.0) != 0.0

        if (paypalUsed) {
            migrated.add(
                PaymentAccount(
                    id = DEFAULT_CARD_ACCOUNT_ID + 1L,
                    name = legacyLabels[ACCOUNT_PAYPAL]?.takeIf { it.isNotBlank() } ?: getString(R.string.account_paypal),
                    type = AccountType.PAYPAL,
                    balance = legacyBalances[ACCOUNT_PAYPAL] ?: 0.0
                )
            )
        }

        return migrated
    }

    private fun getPaymentAccountOptions(): List<PaymentAccount> {
        ensureDefaultPaymentAccounts()
        return paymentAccounts.sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
    }

    private fun resolvePaymentAccountId(accountId: Long?, legacySource: String?): Long {
        val explicitAccount = getPaymentAccountById(accountId)
        if (explicitAccount != null) return explicitAccount.id

        val normalizedLegacy = normalizeLegacyAccountSource(legacySource)
        return when (normalizedLegacy) {
            ACCOUNT_BANK -> paymentAccounts.firstOrNull { isCardType(it.type) }?.id
            ACCOUNT_PAYPAL -> paymentAccounts.firstOrNull { it.type == AccountType.PAYPAL }?.id
            else -> paymentAccounts.firstOrNull { it.type == AccountType.CASH }?.id
        } ?: paymentAccounts.firstOrNull()?.id ?: DEFAULT_CASH_ACCOUNT_ID
    }

    private fun normalizeLegacyAccountSource(account: String?): String {
        return when (account) {
            ACCOUNT_BANK -> ACCOUNT_BANK
            ACCOUNT_PAYPAL -> ACCOUNT_PAYPAL
            else -> ACCOUNT_CASH
        }
    }

    private fun matchesAccountFilter(accountId: Long, filter: String?): Boolean {
        val account = getPaymentAccountById(accountId) ?: return false
        return when (filter) {
            null, FILTER_ALL -> true
            FILTER_GROUP_CARDS -> isCardType(account.type)
            FILTER_GROUP_WALLETS -> isWalletType(account.type)
            FILTER_GROUP_CASH -> account.type == AccountType.CASH
            else -> parseAccountIdFromFilter(filter) == account.id
        }
    }

    private fun matchesTransferAccountFilter(transfer: AccountTransfer, filter: String?): Boolean {
        return matchesAccountFilter(transfer.fromAccountId, filter) || matchesAccountFilter(transfer.toAccountId, filter)
    }

    private fun getTransferDeltaForFilter(transfer: AccountTransfer, filter: String?): Double {
        val fromMatches = matchesAccountFilter(transfer.fromAccountId, filter)
        val toMatches = matchesAccountFilter(transfer.toAccountId, filter)
        return when {
            fromMatches && !toMatches -> -transfer.amount
            !fromMatches && toMatches -> transfer.amount
            else -> 0.0
        }
    }

    private fun getBaseBalanceForAccount(account: String?): Double {
        return paymentAccounts
            .filter { matchesAccountFilter(it.id, account ?: FILTER_ALL) }
            .sumOf { it.balance }
    }

    private fun getCurrentBalanceForAccount(account: PaymentAccount): Double {
        return getCurrentBalanceForFilter(buildExactAccountFilter(account.id))
    }

    private fun getCurrentBalanceForFilter(accountFilter: String = FILTER_ALL): Double {
        return getBalanceUntilDate(Calendar.getInstance(), accountFilter)
    }

    private fun getBaseBalanceForEnteredCurrentBalance(account: PaymentAccount, enteredCurrentBalance: Double): Double {
        val accountActivityDelta = getCurrentBalanceForAccount(account) - account.balance
        return enteredCurrentBalance - accountActivityDelta
    }

    private fun buildEventDetail(primary: String, accountId: Long): String {
        return "$primary · ${getPaymentAccountName(accountId)}"
    }

    class StaticPagesAdapter(private val pages: List<View>) :
        RecyclerView.Adapter<StaticPagesAdapter.PageHolder>() {

        class PageHolder(val container: ViewGroup) : RecyclerView.ViewHolder(container)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
            val container = FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            return PageHolder(container)
        }

        override fun onBindViewHolder(holder: PageHolder, position: Int) {
            val page = pages[position]
            (page.parent as? ViewGroup)?.removeView(page)
            holder.container.removeAllViews()
            holder.container.addView(page)
        }

        override fun getItemCount() = pages.size
    }

    class ItemsPagerAdapter(
        private val getItemsForTab: (Int) -> List<Item>,
        private val onAddEntry: () -> Unit,
        private val onItemLongClick: (Item) -> Unit,
        private val amountFormatter: (Item) -> String,
        private val itemMarkerFormatter: (ItemType) -> String
    ) : RecyclerView.Adapter<ItemsPagerAdapter.PageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.page_filtered_items, parent, false)
            return PageViewHolder(
                view = view,
                onAddEntry = onAddEntry,
                onItemLongClick = onItemLongClick,
                amountFormatter = amountFormatter,
                itemMarkerFormatter = itemMarkerFormatter
            )
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            holder.bind(getItemsForTab(position))
        }

        override fun getItemCount() = 4

        class PageViewHolder(
            view: View,
            private val onAddEntry: () -> Unit,
            private val onItemLongClick: (Item) -> Unit,
            private val amountFormatter: (Item) -> String,
            private val itemMarkerFormatter: (ItemType) -> String
        ) : RecyclerView.ViewHolder(view) {
            private val recyclerView: RecyclerView = view.findViewById(R.id.rvPageItems)
            private val emptyStateLayout: View = view.findViewById(R.id.layoutEmptyState)
            private val emptyState: TextView = view.findViewById(R.id.tvEmptyState)
            private val emptyAddButton: Button = view.findViewById(R.id.btnEmptyAddEntry)

            init {
                recyclerView.layoutManager = LinearLayoutManager(view.context)
                emptyAddButton.setOnClickListener { onAddEntry() }
            }

            fun bind(pageItems: List<Item>) {
                recyclerView.adapter = ItemAdapter(
                    items = pageItems,
                    onItemLongClick = onItemLongClick,
                    amountFormatter = amountFormatter,
                    itemMarkerFormatter = itemMarkerFormatter
                )
                val isEmpty = pageItems.isEmpty()
                emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
                emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }
    }

}
