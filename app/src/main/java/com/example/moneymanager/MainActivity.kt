package com.example.moneymanager

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
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
import androidx.viewpager2.widget.ViewPager2
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.CheckBox
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.moneymanager.ui.AccountDetailsDialogController
import com.example.moneymanager.ui.AccountDisplayController
import com.example.moneymanager.ui.AccountPickerDialogController
import com.example.moneymanager.ui.AddEntryMenuController
import com.example.moneymanager.ui.AppLockDialogController
import com.example.moneymanager.ui.BottomSheetDialogController
import com.example.moneymanager.ui.BottomSheetStyler
import com.example.moneymanager.ui.CalendarDayDialogController
import com.example.moneymanager.ui.CalendarGridController
import com.example.moneymanager.ui.CategoryOptionsController
import com.example.moneymanager.ui.CategoryBudgetDialogController
import com.example.moneymanager.ui.FormInputController
import com.example.moneymanager.ui.HomeItemActionsController
import com.example.moneymanager.ui.HomeItemFilter
import com.example.moneymanager.ui.HomeItemsBuilder
import com.example.moneymanager.ui.HomeItemsPagerController
import com.example.moneymanager.ui.HomeMonthController
import com.example.moneymanager.ui.ImportExportController
import com.example.moneymanager.ui.MainSectionsController
import com.example.moneymanager.ui.ManageAccountsDialogController
import com.example.moneymanager.ui.PaymentOccurrenceOverrideDialogController
import com.example.moneymanager.ui.QuickOptionButtonsController
import com.example.moneymanager.ui.ReminderOptionsController
import com.example.moneymanager.ui.SettingsDialogsController
import com.example.moneymanager.ui.SettingsDisplayController
import com.example.moneymanager.ui.SettingsPageController
import com.example.moneymanager.ui.SpinnerOptionAdapter
import com.example.moneymanager.ui.SubscriptionServiceSearchController
import com.example.moneymanager.ui.TagsController
import com.example.moneymanager.ui.UiFeedbackController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.res.Configuration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
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
    private lateinit var homeItemsPagerController: HomeItemsPagerController
    private lateinit var settingsPageController: SettingsPageController
    private val settingsDialogsController by lazy {
        SettingsDialogsController(
            context = this,
            getLanguageLabel = ::getCurrentLanguageLabel,
            getThemeLabel = ::getCurrentThemeLabel,
            getSavedLanguageCode = ::getSavedLanguageCode,
            saveLanguageCode = ::saveLanguageCode,
            applyLocale = ::setLocale,
            getSavedTheme = ::getSavedTheme,
            updateTheme = ::updateTheme,
            getSavedCurrency = ::getSavedCurrency,
            updateCurrency = ::updateCurrency,
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val appLockDialogController by lazy {
        AppLockDialogController(
            context = this,
            isUnlocked = { appUnlocked },
            setUnlocked = { appUnlocked = it },
            isUnlockDialogShowing = { unlockDialogShowing },
            setUnlockDialogShowing = { unlockDialogShowing = it },
            isAppLockEnabled = ::isAppLockEnabled,
            isDeviceAuthEnabled = ::isDeviceAuthEnabled,
            isDeviceAuthAvailable = ::isDeviceAuthAvailable,
            isStoredPinValid = ::isStoredPinValid,
            getStoredPinHash = ::getStoredPinHash,
            getStoredPinSalt = ::getStoredPinSalt,
            setAppLockValues = ::setAppLockValues,
            clearAppLock = ::clearAppLock,
            launchDeviceAuth = ::launchDeviceAuth,
            onSettingsChanged = ::updateSettingsPageValues,
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val analyticsDataCalculator by lazy {
        AnalyticsDataCalculator(
            context = this,
            expensesProvider = { expenses },
            recurringExpensesProvider = { recurringExpenses },
            getEventsForMonth = ::getEventsForMonth,
            buildCategoryLabel = ::buildCategoryLabel,
            formatCompactAmount = ::formatCompactAmount,
            formatCompactSignedTotal = ::formatCompactSignedTotal
        )
    }
    private val calendarEventsCalculator by lazy {
        CalendarEventsCalculator(
            context = this,
            incomesProvider = { incomes },
            subscriptionsProvider = { subscriptions },
            recurringExpensesProvider = { recurringExpenses },
            expensesProvider = { expenses },
            transfersProvider = { transfers },
            matchesAccountFilter = ::matchesAccountFilter,
            matchesTransferAccountFilter = ::matchesTransferAccountFilter,
            getTransferDeltaForFilter = ::getTransferDeltaForFilter,
            getPaymentAccountName = ::getPaymentAccountName,
            getIncomeTypeDisplayName = ::getIncomeTypeDisplayName,
            buildCategoryLabel = ::buildCategoryLabel
        )
    }
    private val calendarBalanceCalculator by lazy {
        CalendarBalanceCalculator(
            incomesProvider = { incomes },
            subscriptionsProvider = { subscriptions },
            recurringExpensesProvider = { recurringExpenses },
            expensesProvider = { expenses },
            transfersProvider = { transfers },
            getBaseBalanceForAccount = ::getBaseBalanceForAccount,
            matchesAccountFilter = ::matchesAccountFilter,
            matchesTransferAccountFilter = ::matchesTransferAccountFilter,
            getTransferDeltaForFilter = ::getTransferDeltaForFilter
        )
    }
    private val calendarGridController by lazy {
        CalendarGridController(
            context = this,
            gridCalendarDays = gridCalendarDays,
            formatCompactSignedAmount = ::formatCompactSignedAmount,
            formatCompactCalendarAmount = ::formatCompactCalendarAmount,
            formatDisplayDate = ::formatDisplayDate,
            formatSignedAmount = ::formatSignedAmount,
            onDateSelected = { dateIso ->
                selectedCalendarDateIso = dateIso
                updateCalendarPage()
            },
            onDateDoubleTap = ::showCalendarDayDialog,
            onDateLongClick = { dateIso ->
                selectedCalendarDateIso = dateIso
                updateCalendarPage()
                showCalendarQuickAddSheet(dateIso)
            },
            onMonthSwipe = ::changeDisplayedMonthBy
        )
    }
    private val calendarDayDialogController by lazy {
        CalendarDayDialogController(
            context = this,
            formatDisplayDate = ::formatDisplayDate,
            formatSignedAmount = ::formatSignedAmount,
            onEventClick = ::openCalendarEventEditor,
            onMarkPaid = { event -> updateCalendarPaymentStatuses(listOf(event), markPaid = true) },
            onSkip = { event -> updateCalendarPaymentStatuses(listOf(event), markPaid = false) },
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val paymentOccurrenceOverrideController by lazy {
        PaymentOccurrenceOverrideController(
            subscriptionsProvider = { subscriptions },
            recurringExpensesProvider = { recurringExpenses },
            onChanged = ::refreshAfterPaymentOccurrenceOverrideChanged
        )
    }
    private val paymentOccurrenceOverrideDialogController by lazy {
        PaymentOccurrenceOverrideDialogController(
            context = this,
            formatDisplayDate = ::formatDisplayDate,
            showDatePicker = ::showDatePicker,
            onSaveOverride = ::savePaymentOccurrenceOverride,
            onResetOverride = ::removePaymentOccurrenceOverride,
            onEditSeries = { event ->
                when (event.type) {
                    ItemType.SUBSCRIPTION -> showEditSubscriptionDialog(event.sourceIndex)
                    ItemType.RECURRING_EXPENSE -> showRecurringExpenseDialog(event.sourceIndex)
                    else -> Unit
                }
            },
            styleBottomSheet = ::styleBottomSheet,
            focusAmountInput = ::focusAmountInput
        )
    }
    private val addEntryMenuController by lazy {
        AddEntryMenuController(
            context = this,
            formatDisplayDate = ::formatDisplayDate,
            showChoiceBottomSheet = ::showChoiceBottomSheet,
            onAddIncome = ::showAddIncomeDialog,
            onAddExpense = ::showAddExpenseDialog,
            onAddSubscription = ::showAddSubscriptionDialog,
            onAddRecurringExpense = { showRecurringExpenseDialog() },
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val accountPickerDialogController by lazy {
        AccountPickerDialogController(
            context = this,
            accountsProvider = { paymentAccounts },
            ensureDefaultPaymentAccounts = ::ensureDefaultPaymentAccounts,
            getPaymentAccountDisplayName = ::getPaymentAccountDisplayName,
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val bottomSheetDialogController by lazy {
        BottomSheetDialogController(
            context = this,
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val formInputController by lazy {
        FormInputController(
            context = this,
            uiDateFormatter = uiDateFormatter
        )
    }
    private val tagsController by lazy {
        TagsController(
            context = this,
            savedTagsProvider = { savedTags },
            savedSubcategoriesProvider = { savedSubcategories },
            expensesProvider = { expenses },
            recurringExpensesProvider = { recurringExpenses },
            showChoiceBottomSheet = ::showChoiceBottomSheet,
            saveData = ::saveData,
            updateSettingsPageValues = ::updateSettingsPageValues,
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val homeMonthController by lazy {
        HomeMonthController(
            context = this,
            displayedMonth = homeDisplayedMonth,
            monthLabelProvider = { tvHomeMonthLabel },
            onMonthChanged = {
                populateItems()
                updateHomeMonthLabel()
                updateTotals()
                refreshPagedContent()
            }
        )
    }
    private val accountDetailsDialogController by lazy {
        AccountDetailsDialogController(
            context = this,
            accountProvider = ::getPaymentAccountById,
            incomesProvider = { incomes },
            expensesProvider = { expenses },
            subscriptionsProvider = { subscriptions },
            recurringExpensesProvider = { recurringExpenses },
            transfersProvider = { transfers },
            getPaymentAccountDisplayName = ::getPaymentAccountDisplayName,
            getAccountTypeLabel = ::getAccountTypeLabel,
            getCurrentBalanceForAccount = ::getCurrentBalanceForAccount,
            formatAssetAmount = ::formatAssetAmount,
            formatDisplayDate = ::formatDisplayDate,
            getPaymentAccountName = ::getPaymentAccountName,
            getAssetAmountColor = ::getAssetAmountColor,
            onEditAccount = ::showPaymentAccountDialog,
            onAddTransfer = { showTransferDialog() },
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val categoryBudgetDialogController by lazy {
        CategoryBudgetDialogController(
            context = this,
            budgetsProvider = { categoryBudgets },
            displayedMonthProvider = { homeDisplayedMonth },
            getCategoryBudget = ::getCategoryBudget,
            getCategorySpentForMonth = { category, month -> getCategorySpentForMonth(category, month) },
            formatAmount = ::formatAmount,
            onChanged = {
                saveData()
                refreshPagedContent()
            },
            focusAmountInput = ::focusAmountInput,
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val manageAccountsDialogController by lazy {
        ManageAccountsDialogController(
            context = this,
            accountsProvider = { paymentAccounts },
            ensureDefaultPaymentAccounts = ::ensureDefaultPaymentAccounts,
            getPaymentAccountDisplayName = ::getPaymentAccountDisplayName,
            getAccountTypeLabel = ::getAccountTypeLabel,
            onEditAccount = ::showPaymentAccountDialog,
            styleBottomSheet = ::styleBottomSheet
        )
    }
    private val quickOptionButtonsController by lazy {
        QuickOptionButtonsController(context = this)
    }
    private val subscriptionServiceSearchController by lazy {
        SubscriptionServiceSearchController(
            addQuickOptionButtons = ::addQuickOptionButtons
        )
    }
    private val categoryOptionsController by lazy {
        CategoryOptionsController(
            context = this,
            categoriesProvider = { categories },
            favoriteCategoriesProvider = { favoriteCategories }
        )
    }
    private val uiFeedbackController by lazy {
        UiFeedbackController(
            context = this,
            rootProvider = { viewPagerSections },
            anchorProvider = { findViewById(R.id.layoutSectionSelector) }
        )
    }
    private val reminderOptionsController by lazy {
        ReminderOptionsController(context = this)
    }
    private val accountDisplayController by lazy {
        AccountDisplayController(
            context = this,
            accountProvider = ::getPaymentAccountById
        )
    }
    private val settingsDisplayController by lazy {
        SettingsDisplayController(context = this)
    }
    private val homeItemActionsController by lazy {
        HomeItemActionsController(
            context = this,
            onIncomeClick = { index -> showIncomeDialog(index) },
            onSubscriptionClick = { index -> showEditSubscriptionDialog(index) },
            onRecurringExpenseClick = { index -> showRecurringExpenseDialog(index) },
            onExpenseClick = { index -> showExpenseDialog(index) },
            onTransferClick = { index -> showTransferDialog(index) }
        )
    }
    private val homeItemsBuilder by lazy {
        HomeItemsBuilder(
            context = this,
            incomesProvider = { incomes },
            subscriptionsProvider = { subscriptions },
            recurringExpensesProvider = { recurringExpenses },
            expensesProvider = { expenses },
            transfersProvider = { transfers },
            displayedMonthProvider = { homeDisplayedMonth },
            selectedAccountProvider = { selectedHomeAccount },
            getIncomeOccurrenceDateInMonth = ::getIncomeOccurrenceDateInMonth,
            getSubscriptionOccurrenceDateInMonth = ::getSubscriptionOccurrenceDateInMonth,
            getRecurringExpenseOccurrenceDateInMonth = ::getRecurringExpenseOccurrenceDateInMonth,
            getNextSubscriptionDate = ::getNextSubscriptionDate,
            getSubscriptionLifecycleMeta = ::getSubscriptionLifecycleMeta,
            formatDisplayDate = ::formatDisplayDate,
            getIncomeTypeDisplayName = ::getIncomeTypeDisplayName,
            buildCategoryLabel = ::buildCategoryLabel,
            getPaymentAccountIcon = ::getPaymentAccountIcon,
            getPaymentAccountName = ::getPaymentAccountName,
            getTransferDeltaForFilter = ::getTransferDeltaForFilter
        )
    }
    private val homeItemFilter by lazy {
        HomeItemFilter(
            itemsProvider = { items },
            incomesProvider = { incomes },
            subscriptionsProvider = { subscriptions },
            recurringExpensesProvider = { recurringExpenses },
            expensesProvider = { expenses },
            transfersProvider = { transfers },
            selectedAccountProvider = { selectedHomeAccount },
            displayedMonthProvider = { homeDisplayedMonth },
            searchQueryProvider = { homeSearchQuery },
            formatAmount = ::formatAmount,
            formatSignedAmount = ::formatSignedAmount,
            getSubscriptionLifecycleLabel = ::getSubscriptionLifecycleLabel,
            getPaymentAccountName = ::getPaymentAccountName,
            getPaymentAccountType = ::getPaymentAccountType,
            getAccountTypeLabel = ::getAccountTypeLabel,
            matchesAccountFilter = ::matchesAccountFilter,
            matchesTransferAccountFilter = ::matchesTransferAccountFilter,
            monthContainsIncomeOccurrence = ::monthContainsIncomeOccurrence,
            monthContainsSubscriptionOccurrence = ::monthContainsSubscriptionOccurrence,
            monthContainsRecurringExpenseOccurrence = ::monthContainsRecurringExpenseOccurrence,
            parseIsoDate = ::parseIsoDate,
            isSameMonth = ::isSameMonth
        )
    }
    private lateinit var mainSectionsController: MainSectionsController
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
    private val uiDateFormatter = UiDateFormatter()
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

    private val gson = Gson()
    private val moneyAmountFormatter by lazy { MoneyAmountFormatter(::getCurrencySymbol) }
    private lateinit var moneyDao: MoneyDao
    private val storedValues by lazy { StoredValueStore(moneyDao) }
    private val csvConverter by lazy {
        MoneyManagerCsvConverter(
            context = this,
            paymentAccountsProvider = { paymentAccounts },
            getPaymentAccountName = ::getPaymentAccountName,
            getPaymentAccountType = ::getPaymentAccountType,
            parseIsoDate = ::parseIsoDate,
            todayIsoProvider = { isoDateWithOffset(0) }
        )
    }
    private var isUiInitialized = false
    private lateinit var importExportController: ImportExportController
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
        setupActivityResultLaunchers()
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
        btnSectionHome.addPressAnimation()
        btnSectionCategories.addPressAnimation()
        btnSectionCalendar.addPressAnimation()
        btnSectionAnalytics.addPressAnimation()
        btnSectionSettings.addPressAnimation()
        btnSectionHome.setOnClickListener { viewPagerSections.currentItem = 0 }
        btnSectionCategories.setOnClickListener { viewPagerSections.currentItem = 1 }
        btnSectionCalendar.setOnClickListener { viewPagerSections.currentItem = 2 }
        btnSectionAnalytics.setOnClickListener { viewPagerSections.currentItem = 3 }
        btnSectionSettings.setOnClickListener { viewPagerSections.currentItem = 4 }

        isUiInitialized = true
        updateTotals()
        refreshPagedContent()
        if (isAppLockEnabled()) {
            appLockDialogController.showUnlockDialog()
        }
    }

    private fun setupActivityResultLaunchers() {
        importExportController = ImportExportController(
            activity = this,
            backupJsonProvider = { gson.toJson(createBackupData()) },
            onBackupExported = { wrote ->
                showToast(
                    if (wrote) R.string.backup_export_success else R.string.backup_export_error
                )
            },
            onBackupImportText = ::readBackupFromText,
            csvProvider = {
                csvConverter.createCsvData(
                    incomes = incomes,
                    expenses = expenses,
                    subscriptions = subscriptions,
                    recurringExpenses = recurringExpenses,
                    transfers = transfers
                )
            },
            onCsvExported = { wrote ->
                showToast(
                    if (wrote) R.string.csv_export_success else R.string.csv_export_error
                )
            },
            onCsvImportText = ::readCsvFromText
        )
        deviceAuthLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                appUnlocked = true
                unlockDialogShowing = false
            } else if (isAppLockEnabled() && !appUnlocked) {
                appLockDialogController.showUnlockDialog()
            }
        }
    }

    private fun readBackupFromText(text: String?) {
        val backup = text?.let { json ->
            runCatching { gson.fromJson(json, MoneyManagerBackup::class.java) }.getOrNull()
        }

        if (backup == null) {
            showToast(R.string.backup_import_error)
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

    private fun readCsvFromText(text: String?) {
        val importData = runCatching {
            csvConverter.parseCsvImport(text.orEmpty())
        }.getOrNull()

        if (importData == null || importData.isEmpty()) {
            showToast(R.string.csv_import_error)
            return
        }

        val filteredImportData = filterCsvImportDuplicates(importData)
        if (filteredImportData.isEmpty()) {
            showToast(R.string.csv_import_no_new_records)
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
        showToast(R.string.csv_import_success)
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
        showToast(R.string.backup_import_success)
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
        layoutHomeBudgetSummary.addPressAnimation()
        layoutHomeMonthlyGoal.addPressAnimation()
        btnHomePreviousMonth.addPressAnimation()
        btnHomeNextMonth.addPressAnimation()
        btnHomeAccountPicker.addPressAnimation()
        btnHomeBalancesVisibility.addPressAnimation()
        btnFabAddEntry.addPressAnimation()
        etHomeSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                homeSearchQuery = s?.toString()?.trim().orEmpty()
                if (::homeItemsPagerController.isInitialized) {
                    homeItemsPagerController.refresh()
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
        homeMonthController.updateLabel()
    }

    private fun changeHomeDisplayedMonthBy(offset: Int) {
        homeMonthController.changeBy(offset)
    }

    private fun showHomeMonthPicker() {
        homeMonthController.showPicker()
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
        layoutCalendarDaySummary.addPressAnimation()
        btnCalendarAccountPicker.addPressAnimation()
        btnCalendarOpenDay.addPressAnimation()
        layoutCalendarDaySummary.setOnClickListener { showCalendarDayDialog(selectedCalendarDateIso) }
        btnCalendarAccountPicker.setOnClickListener { showCalendarAccountPickerDialog() }
        btnCalendarOpenDay.setOnClickListener { showCalendarDayDialog(selectedCalendarDateIso) }
        calendarPageView.findViewById<LinearLayout>(R.id.layoutCalendarWeekHeader)
            .setOnTouchListener(calendarGridController.createSwipeTouchListener())
        val btnPreviousMonth = calendarPageView.findViewById<View>(R.id.btnPreviousMonth)
        val btnNextMonth = calendarPageView.findViewById<View>(R.id.btnNextMonth)
        val btnCalendarToday = calendarPageView.findViewById<View>(R.id.btnCalendarToday)
        btnPreviousMonth.addPressAnimation()
        btnNextMonth.addPressAnimation()
        btnCalendarToday.addPressAnimation()
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
        btnAssetsAccountsTab.addPressAnimation()
        btnAssetsBudgetsTab.addPressAnimation()
        btnAddAccountPage.addPressAnimation()
        btnAddCategoryPage.addPressAnimation()
        btnTransferPage.addPressAnimation()
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
        btnAnalyticsAccountPicker.addPressAnimation()
        btnAnalyticsCategoryDetails.addPressAnimation()
        btnAnalyticsTrendDetails.addPressAnimation()
        btnPreviousMonth.addPressAnimation()
        btnNextMonth.addPressAnimation()
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
        settingsPageController = SettingsPageController(
            context = this,
            root = settingsPageView,
            getLanguageLabel = ::getCurrentLanguageLabel,
            getThemeLabel = ::getCurrentThemeLabel,
            getCurrencyLabel = ::getCurrentCurrencyLabel,
            getAccountsSummary = ::getAccountManagementSummary,
            isAppLockEnabled = ::isAppLockEnabled,
            getTagsCount = { getKnownTags().size },
            getSubcategoriesCount = { getKnownSubcategories().size },
            onLanguageClick = settingsDialogsController::showLanguagePickerDialog,
            onThemeClick = settingsDialogsController::showThemePickerDialog,
            onCurrencyClick = settingsDialogsController::showCurrencyPickerDialog,
            onAccountsClick = ::showManageAccountsDialog,
            onAppLockClick = appLockDialogController::showSettingsDialog,
            onTagsClick = ::showManageTagsDialog,
            onExportBackupClick = importExportController::exportBackup,
            onImportBackupClick = importExportController::importBackup,
            onExportCsvClick = importExportController::exportCsv,
            onImportCsvClick = importExportController::importCsv
        )
        settingsPageController.bind()
    }

    private fun setupSectionPager() {
        mainSectionsController = MainSectionsController(
            context = this,
            viewPagerSections = viewPagerSections,
            pages = listOf(homePageView, categoriesPageView, calendarPageView, analyticsPageView, settingsPageView),
            tabs = listOf(
                MainSectionsController.SectionTab(navHomeContent, tvSectionHome, ivSectionHome),
                MainSectionsController.SectionTab(navCategoriesContent, tvSectionCategories, ivSectionCategories),
                MainSectionsController.SectionTab(navCalendarContent, tvSectionCalendar, ivSectionCalendar),
                MainSectionsController.SectionTab(navAnalyticsContent, tvSectionAnalytics, ivSectionAnalytics),
                MainSectionsController.SectionTab(navSettingsContent, tvSectionSettings, ivSectionSettings)
            )
        )
        mainSectionsController.setup()
    }

    private fun updateSettingsPageValues() {
        if (::settingsPageController.isInitialized) {
            settingsPageController.updateValues()
        }
    }

    private fun updateCalendarBadge() {
        val todayIso = isoDateFormatter().format(Date())
        val count = getEventsForDate(todayIso).size
        tvSectionCalendarBadge.visibility = if (count > 0) View.VISIBLE else View.GONE
        if (count > 0) {
            tvSectionCalendarBadge.text = count.coerceAtMost(9).toString()
        }
    }

    private fun createSpinnerAdapter(values: List<String>): ArrayAdapter<String> {
        return SpinnerOptionAdapter(this, values)
    }

    private fun setupPager() {
        homeItemsPagerController = HomeItemsPagerController(
            context = this,
            tabFilters = tabFilters,
            viewPagerItems = viewPagerItems,
            getItemsForTab = homeItemFilter::getFilteredItems,
            onAddEntry = ::showAddEntryMenuDialog,
            onItemLongClick = homeItemActionsController::openItemEditor,
            amountFormatter = ::formatItemAmount,
            itemMarkerFormatter = ::getItemMarker,
            tabTitleProvider = homeItemActionsController::getTabTitle
        )
        homeItemsPagerController.setup()
    }

    private fun refreshPagedContent() {
        populateItems()
        updateTotals()
        if (::homeItemsPagerController.isInitialized) {
            homeItemsPagerController.refresh()
        }
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

        val categoryTotals = analyticsDataCalculator.getCategoryTotals(analyticsDisplayedMonth, selectedAnalyticsAccount)
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
        val points = analyticsDataCalculator.getMonthlyTrendPoints(analyticsDisplayedMonth, accountFilter)
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

    private fun showAnalyticsTrendDetailsDialog() {
        val details = analyticsDataCalculator.getMonthlyTrendDetails(analyticsDisplayedMonth, selectedAnalyticsAccount)
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
        val categoryTotals = analyticsDataCalculator.getCategoryTotals(analyticsDisplayedMonth, selectedAnalyticsAccount)
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
        }.also { it.addPressAnimation() }
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
        row.addPressAnimation()

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
        accountDetailsDialogController.show(accountId)
    }

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
        categoryBudgetDialogController.show(category)
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
                showToast(R.string.monthly_goal_sources_error)
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

    private fun launchDeviceAuth() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val intent = keyguardManager?.createConfirmDeviceCredentialIntent(
            getString(R.string.app_lock_unlock_title),
            getString(R.string.app_lock_unlock_message)
        )
        if (intent == null) {
            showToast(R.string.device_auth_unavailable)
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
        return storedValues.isAppLockEnabled(prefs)
    }

    private fun isDeviceAuthEnabled(): Boolean {
        if (!::moneyDao.isInitialized) return false
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return storedValues.isDeviceAuthEnabled(prefs)
    }

    private fun getStoredPinHash(): String {
        if (!::moneyDao.isInitialized) return ""
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return storedValues.getStoredPinHash(prefs)
    }

    private fun getStoredPinSalt(): String {
        if (!::moneyDao.isInitialized) return ""
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return storedValues.getStoredPinSalt(prefs)
    }

    private fun isStoredPinValid(pin: String): Boolean {
        val salt = getStoredPinSalt()
        val hash = getStoredPinHash()
        return pin.isNotBlank() && salt.isNotBlank() && hashPin(pin, salt) == hash
    }

    private fun setAppLockValues(enabled: Boolean, pinHash: String, pinSalt: String, deviceAuth: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        storedValues.setAppLockValues(prefs, enabled, pinHash, pinSalt, deviceAuth)
        prefs.apply()
    }

    private fun clearAppLock() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        storedValues.clearAppLock(prefs)
        prefs.apply()
    }

    private fun getCurrentLanguageLabel(): String {
        return settingsDisplayController.getLanguageLabel(getSavedLanguageCode())
    }

    private fun getCurrentThemeLabel(): String {
        return settingsDisplayController.getThemeLabel(getSavedTheme())
    }

    private fun getCurrentCurrencyLabel(): String {
        return settingsDisplayController.getCurrencyLabel(getSavedCurrency())
    }

    private fun getCalendarAccountButtonText(): String {
        return "${getCalendarFilterLabel(selectedCalendarAccount)} \u25BC"
    }

    private fun getAnalyticsAccountButtonText(): String {
        return "${getCalendarFilterLabel(selectedAnalyticsAccount)} \u25BC"
    }

    private fun getAccountManagementSummary(): String {
        ensureDefaultPaymentAccounts()
        return accountDisplayController.getAccountManagementSummary(paymentAccounts)
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
        return accountDisplayController.getCalendarFilterLabel(filter)
    }

    private fun getPaymentAccountById(accountId: Long?): PaymentAccount? {
        return paymentAccounts.firstOrNull { it.id == accountId }
    }

    private fun getPaymentAccountDisplayName(account: PaymentAccount): String {
        return accountDisplayController.getPaymentAccountDisplayName(account)
    }

    private fun getPaymentAccountName(accountId: Long?): String {
        return accountDisplayController.getPaymentAccountName(accountId)
    }

    private fun getPaymentAccountType(accountId: Long?): AccountType {
        return accountDisplayController.getPaymentAccountType(accountId)
    }

    private fun getPaymentAccountIcon(accountId: Long?): String {
        return accountDisplayController.getPaymentAccountIcon(accountId)
    }

    private fun getItemMarker(type: ItemType): String {
        return accountDisplayController.getItemMarker(type)
    }

    private fun getAccountTypeLabel(type: AccountType): String {
        return accountDisplayController.getAccountTypeLabel(type)
    }

    private fun showCalendarAccountPickerDialog() {
        showAccountPickerDialog { filter ->
            selectedCalendarAccount = filter
            btnCalendarAccountPicker.text = getCalendarAccountButtonText()
            updateCalendarPage()
        }
    }

    private fun showHomeAccountPickerDialog() {
        showAccountPickerDialog { filter ->
            selectedHomeAccount = filter
            btnHomeAccountPicker.text = "${getCalendarFilterLabel(selectedHomeAccount)} \u25BC"
            updateTotals()
            refreshPagedContent()
        }
    }

    private fun showAnalyticsAccountPickerDialog() {
        showAccountPickerDialog { filter ->
            selectedAnalyticsAccount = filter
            btnAnalyticsAccountPicker.text = getAnalyticsAccountButtonText()
            updateAnalyticsPage()
        }
    }

    private fun showAccountPickerDialog(onSelected: (String) -> Unit) {
        accountPickerDialogController.show(onSelected)
    }

    private fun showAddEntryMenuDialog(initialDate: String? = null) {
        addEntryMenuController.showAddEntryMenu(initialDate)
    }

    private fun showManageAccountsDialog() {
        manageAccountsDialogController.show()
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
        bottomSheetDialogController.showInfo()
    }

    private fun styleBottomSheet(dialog: BottomSheetDialog) {
        BottomSheetStyler.apply(dialog)
    }

    private fun focusAmountInput(input: EditText) {
        formInputController.focusAmountInput(input)
    }

    private fun parsePositiveAmount(input: EditText): Double? {
        return formInputController.parsePositiveAmount(input)
    }

    private fun setDetailsVisible(container: View, toggle: Button, visible: Boolean) {
        uiFeedbackController.setDetailsVisible(container, toggle, visible)
    }

    private fun isoDateWithOffset(days: Int): String {
        return uiDateFormatter.isoDateWithOffset(days)
    }

    private fun addQuickOptionButtons(
        container: LinearLayout,
        values: List<String>,
        selectedIndex: Int = 0,
        onSelected: (Int, String) -> Unit
    ) {
        quickOptionButtonsController.addButtons(container, values, selectedIndex, onSelected)
    }

    private fun showOptionBottomSheet(
        options: List<String>,
        selectedIndex: Int = 0,
        onSelected: (Int) -> Unit
    ) {
        bottomSheetDialogController.showOption(options, selectedIndex, onSelected)
    }

    private fun showChoiceBottomSheet(title: String, options: List<String>, onSelected: (String) -> Unit) {
        bottomSheetDialogController.showChoice(title, options, onSelected)
    }

    private fun showConfirmationBottomSheet(
        title: String,
        message: String,
        positiveText: String,
        onConfirm: () -> Unit
    ) {
        bottomSheetDialogController.showConfirmation(title, message, positiveText, onConfirm)
    }

    private fun useBottomSheetPicker(spinner: Spinner, options: List<String>, onSelected: (Int) -> Unit = {}) {
        bottomSheetDialogController.bindPicker(spinner, options, onSelected)
    }

    private fun showSavedSnackbar(undoAction: (() -> Unit)? = null) {
        uiFeedbackController.showSavedSnackbar(undoAction)
    }

    private fun showToast(messageRes: Int) {
        uiFeedbackController.showToast(messageRes)
    }

    private fun showDeleteConfirmation(message: String, onConfirm: () -> Unit) {
        bottomSheetDialogController.showDeleteConfirmation(message, onConfirm)
    }

    private fun showDatePicker(initialDate: String?, onDateSelected: (String) -> Unit) {
        formInputController.showDatePicker(initialDate, onDateSelected)
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
        calendarGridController.build(
            displayedMonth = displayedMonth,
            selectedDateIso = selectedCalendarDateIso,
            eventsByDate = calendarEventsByDate
        )
    }

    private fun showCalendarQuickAddSheet(dateIso: String) {
        addEntryMenuController.showCalendarQuickAdd(dateIso)
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

    private fun getProjectedBalanceForDate(dateIso: String, accountFilter: String = FILTER_ALL): Double {
        return calendarBalanceCalculator.getProjectedBalanceForDate(dateIso, accountFilter)
    }

    private fun getBalanceUntilDate(target: Calendar, accountFilter: String = FILTER_ALL): Double {
        return calendarBalanceCalculator.getBalanceUntilDate(target, accountFilter)
    }

    private fun showCalendarDayDialog(dateIso: String) {
        val events = getEventsForDate(dateIso, selectedCalendarAccount)
        calendarDayDialogController.show(dateIso, events)
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
        val baseAmount = getPaymentOccurrenceBaseAmount(event) ?: return
        paymentOccurrenceOverrideDialogController.show(
            event = event,
            originalDate = originalDate,
            currentOverride = currentOverride,
            baseAmount = baseAmount
        )
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
        return paymentOccurrenceOverrideController.getBaseAmount(event)
    }

    private fun getPaymentOccurrenceOverride(event: CalendarEvent): PaymentOccurrenceOverride? {
        return paymentOccurrenceOverrideController.getOverride(event)
    }

    private fun savePaymentOccurrenceOverride(
        event: CalendarEvent,
        originalDate: String,
        selectedDate: String,
        amount: Double,
        baseAmount: Double
    ) {
        paymentOccurrenceOverrideController.saveOverride(event, originalDate, selectedDate, amount, baseAmount)
    }

    private fun removePaymentOccurrenceOverride(event: CalendarEvent, originalDate: String) {
        paymentOccurrenceOverrideController.removeOverride(event, originalDate)
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
        return calendarEventsCalculator.getEventsForMonth(month, accountFilter)
    }

    private fun getCalendarEventsForMonth(month: Calendar, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        return calendarEventsCalculator.getEventsForMonth(month, accountFilter)
    }

    private fun getDateStringsForMonth(month: Calendar): List<String> {
        return calendarEventsCalculator.getDateStringsForMonth(month)
    }

    private fun buildCalendarEventsByDate(month: Calendar, accountFilter: String = FILTER_ALL): Map<String, List<CalendarEvent>> {
        return calendarEventsCalculator.buildEventsByDate(month, accountFilter)
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

    private fun todayCalendar(): Calendar {
        return uiDateFormatter.todayCalendar()
    }

    private fun isSameMonth(value: Calendar?, month: Calendar): Boolean {
        return uiDateFormatter.isSameMonth(value, month)
    }

    private fun monthContainsIncomeOccurrence(income: Income, month: Calendar): Boolean {
        return calendarEventsCalculator.monthContainsIncomeOccurrence(income, month)
    }

    private fun getIncomeOccurrenceDateInMonth(income: Income, month: Calendar): String? {
        return calendarEventsCalculator.getIncomeOccurrenceDateInMonth(income, month)
    }

    private fun monthContainsSubscriptionOccurrence(subscription: Subscription, month: Calendar): Boolean {
        return calendarEventsCalculator.monthContainsSubscriptionOccurrence(subscription, month)
    }

    private fun getSubscriptionOccurrenceDateInMonth(subscription: Subscription, month: Calendar): String? {
        return calendarEventsCalculator.getSubscriptionOccurrenceDateInMonth(subscription, month)
    }

    private fun monthContainsRecurringExpenseOccurrence(recurringExpense: RecurringExpense, month: Calendar): Boolean {
        return calendarEventsCalculator.monthContainsRecurringExpenseOccurrence(recurringExpense, month)
    }

    private fun getRecurringExpenseOccurrenceDateInMonth(recurringExpense: RecurringExpense, month: Calendar): String? {
        return calendarEventsCalculator.getRecurringExpenseOccurrenceDateInMonth(recurringExpense, month)
    }

    private fun parseIsoDate(value: String?): Calendar? {
        return uiDateFormatter.parseIsoDate(value)
    }

    private fun formatDisplayDate(value: String?): String {
        return uiDateFormatter.formatDisplayDate(value)
    }

    private fun isoDateFormatter(): SimpleDateFormat {
        return uiDateFormatter.isoDateFormatter()
    }

    private fun displayDateFormatter(): SimpleDateFormat {
        return uiDateFormatter.displayDateFormatter()
    }

    private fun homeMonthFormatter(): SimpleDateFormat {
        return uiDateFormatter.homeMonthFormatter()
    }

    private fun updateDateButton(button: Button, dateValue: String?, emptyTextRes: Int) {
        formInputController.updateDateButton(button, dateValue, emptyTextRes)
    }

    private fun updateDateShortcutButtons(selectedDate: String?, vararg shortcuts: Pair<Button, Int>) {
        formInputController.updateDateShortcutButtons(selectedDate, *shortcuts)
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
        return settingsDisplayController.getCurrencySymbol(getSavedCurrency())
    }

    private fun formatAmount(value: Double): String {
        return moneyAmountFormatter.formatAmount(value)
    }

    private fun formatSignedAmount(value: Double): String {
        return moneyAmountFormatter.formatSignedAmount(value)
    }

    private fun formatCompactCalendarAmount(value: Double): String {
        return moneyAmountFormatter.formatCompactCalendarAmount(value)
    }

    private fun formatCompactAmount(value: Double): String {
        return moneyAmountFormatter.formatCompactAmount(value)
    }

    private fun formatCompactSignedAmount(value: Double): String {
        return moneyAmountFormatter.formatCompactSignedAmount(value)
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

    private fun getReminderDaysLabels(): List<String> {
        return reminderOptionsController.getReminderDaysLabels()
    }

    private fun setReminderDaysSelection(spinner: Spinner, daysBefore: Int) {
        reminderOptionsController.setReminderDaysSelection(spinner, daysBefore)
    }

    private fun getSelectedReminderDays(spinner: Spinner): Int {
        return reminderOptionsController.getSelectedReminderDays(spinner)
    }

    private fun appendPaymentStatus(detail: String, isPaid: Boolean, isSkipped: Boolean): String {
        return reminderOptionsController.appendPaymentStatus(detail, isPaid, isSkipped)
    }

    private fun buildCategoryLabel(category: String?, subcategory: String?): String {
        val primary = category?.takeIf { it.isNotBlank() } ?: getString(R.string.general_category)
        val secondary = subcategory?.takeIf { it.isNotBlank() }
        return if (secondary == null) primary else "$primary / $secondary"
    }

    private fun rememberTagsAndSubcategoriesFrom(
        importedExpenses: List<Expense>,
        importedRecurringExpenses: List<RecurringExpense>
    ) {
        tagsController.rememberTagsAndSubcategoriesFrom(importedExpenses, importedRecurringExpenses)
    }

    private fun getKnownTags(): List<String> {
        return tagsController.getKnownTags()
    }

    private fun getKnownSubcategories(): List<String> {
        return tagsController.getKnownSubcategories()
    }

    private fun showSubcategoryPicker(target: EditText) {
        tagsController.showSubcategoryPicker(target)
    }

    private fun showTagPicker(target: EditText) {
        tagsController.showTagPicker(target)
    }

    private fun showManageTagsDialog() {
        tagsController.showManageTagsDialog()
    }

    private fun loadData() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        storedValues.migrateSharedPreferencesToRoomIfNeeded(prefs)
        val incomesJson = storedValues.readString(prefs, KEY_INCOMES, "[]")
        val subscriptionsJson = storedValues.readString(prefs, KEY_SUBSCRIPTIONS, "[]")
        val recurringExpensesJson = storedValues.readString(prefs, KEY_RECURRING_EXPENSES, "[]")
        val expensesJson = storedValues.readString(prefs, KEY_EXPENSES, "[]")
        val transfersJson = storedValues.readString(prefs, KEY_TRANSFERS, "[]")
        val categoriesJson = storedValues.readString(prefs, KEY_CATEGORIES, "[]")
        val categoryBudgetsJson = storedValues.readString(prefs, KEY_CATEGORY_BUDGETS, "[]")
        val favoriteCategoriesJson = storedValues.readString(prefs, KEY_FAVORITE_CATEGORIES, "[]")
        val savedTagsJson = storedValues.readString(prefs, KEY_SAVED_TAGS, "[]")
        val savedSubcategoriesJson = storedValues.readString(prefs, KEY_SAVED_SUBCATEGORIES, "[]")
        val monthlyGoalCategoriesJson = storedValues.readString(prefs, KEY_MONTHLY_GOAL_CATEGORIES, "[]")
        cashAmount = storedValues.readDouble(prefs, KEY_CASH_AMOUNT, 0.0)
        monthlyGoalAmount = storedValues.readDouble(prefs, KEY_MONTHLY_GOAL_AMOUNT, 0.0)
        monthlyGoalIncludeExpenses = storedValues.readBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_EXPENSES, true)
        monthlyGoalIncludeSubscriptions = storedValues.readBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_SUBSCRIPTIONS, true)
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
            gson.fromJson<Map<String, Double>>(storedValues.readString(prefs, KEY_ACCOUNT_BALANCES, "{}"), object : TypeToken<Map<String, Double>>() {}.type)
        }.getOrNull().orEmpty()
        val legacyLabels = runCatching {
            gson.fromJson<Map<String, String>>(storedValues.readString(prefs, KEY_ACCOUNT_LABELS, "{}"), object : TypeToken<Map<String, String>>() {}.type)
        }.getOrNull().orEmpty()
        val savedAccounts = runCatching {
            gson.fromJson<List<PaymentAccount>>(storedValues.readString(prefs, KEY_PAYMENT_ACCOUNTS, "[]"), object : TypeToken<List<PaymentAccount>>() {}.type)
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
        nextPaymentAccountId = storedValues.readLong(
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
        storedValues.storeString(prefs, KEY_INCOMES, gson.toJson(incomes))
        storedValues.storeString(prefs, KEY_SUBSCRIPTIONS, gson.toJson(subscriptions))
        storedValues.storeString(prefs, KEY_RECURRING_EXPENSES, gson.toJson(recurringExpenses))
        storedValues.storeString(prefs, KEY_EXPENSES, gson.toJson(expenses))
        storedValues.storeString(prefs, KEY_TRANSFERS, gson.toJson(transfers))
        storedValues.storeString(prefs, KEY_CATEGORIES, gson.toJson(categories))
        storedValues.storeString(prefs, KEY_FAVORITE_CATEGORIES, gson.toJson(favoriteCategories.toList()))
        storedValues.storeString(prefs, KEY_SAVED_TAGS, gson.toJson(getKnownTags()))
        storedValues.storeString(prefs, KEY_SAVED_SUBCATEGORIES, gson.toJson(getKnownSubcategories()))
        storedValues.storeString(prefs, KEY_CATEGORY_BUDGETS, gson.toJson(categoryBudgets))
        storedValues.storeDouble(prefs, KEY_MONTHLY_GOAL_AMOUNT, monthlyGoalAmount)
        storedValues.storeBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_EXPENSES, monthlyGoalIncludeExpenses)
        storedValues.storeBoolean(prefs, KEY_MONTHLY_GOAL_INCLUDE_SUBSCRIPTIONS, monthlyGoalIncludeSubscriptions)
        storedValues.storeString(prefs, KEY_MONTHLY_GOAL_CATEGORIES, gson.toJson(monthlyGoalCategories.toList()))
        storedValues.storeDouble(prefs, KEY_CASH_AMOUNT, cashAmount)
        storedValues.storeString(prefs, KEY_PAYMENT_ACCOUNTS, gson.toJson(paymentAccounts))
        storedValues.storeLong(prefs, KEY_NEXT_PAYMENT_ACCOUNT_ID, nextPaymentAccountId)
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

    private fun populateItems() {
        items.clear()
        items.addAll(homeItemsBuilder.build())
    }

    private fun updateTotals() {
        val visibleItems = homeItemFilter.getHomeVisibleItems()
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
        subscriptionServiceSearchController.setup(etName, container)
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
            showToast(R.string.transfer_need_two_accounts)
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
                showToast(R.string.transfer_same_account_error)
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
        return categoryOptionsController.getIncomeTypeOptions()
    }

    private fun getIncomeTypeKey(value: String): String {
        return categoryOptionsController.getIncomeTypeKey(value)
    }

    private fun getIncomeTypeDisplayName(value: String): String {
        return categoryOptionsController.getIncomeTypeDisplayName(value)
    }

    private fun getIncomePeriodOptions(): List<String> {
        return categoryOptionsController.getIncomePeriodOptions()
    }

    private fun getCategoryOptions(): MutableList<String> {
        return categoryOptionsController.getCategoryOptions()
    }

    private fun getCategoryDisplayName(category: String): String {
        return categoryOptionsController.getCategoryDisplayName(category)
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
        return categoryOptionsController.getPopularCategoryNames()
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
        return moneyAmountFormatter.formatCompactSignedTotal(value)
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

    private fun matchesAccountFilter(accountId: Long, filter: String?): Boolean {
        return matchesAccountFilter(getPaymentAccountById(accountId), filter)
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

}
