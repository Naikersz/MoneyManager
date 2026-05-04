package com.example.moneymanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
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
import android.view.View
import android.view.ViewGroup
import android.view.ViewConfiguration
import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.res.Configuration
import java.text.SimpleDateFormat
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
        private const val KEY_PAYMENT_ACCOUNTS = "payment_accounts"
        private const val KEY_NEXT_PAYMENT_ACCOUNT_ID = "next_payment_account_id"
        private const val KEY_CATEGORY_BUDGETS = "category_budgets"
        private const val KEY_CASH_AMOUNT = "cash_amount"
        private const val KEY_ACCOUNT_BALANCES = "account_balances"
        private const val KEY_ACCOUNT_LABELS = "account_labels"
        private const val THEME_MINIMAL = "minimal"
        private const val THEME_HELL = "hell"
        private const val TAB_ALL = 0
        private const val TAB_INCOME = 1
        private const val TAB_SUBSCRIPTIONS = 2
        private const val TAB_EXPENSES = 3
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
    private lateinit var layoutHomeBudgetSummary: LinearLayout
    private lateinit var tvHomeBudgetSummaryValue: TextView
    private lateinit var tvHomeBudgetRemainingLabel: TextView
    private lateinit var tvHomeBudgetRemainingValue: TextView
    private lateinit var tvHomeBudgetAlerts: TextView
    private lateinit var progressHomeBudget: ProgressBar
    private lateinit var tvPageLanguageValue: TextView
    private lateinit var tvPageThemeValue: TextView
    private lateinit var tvPageCurrencyValue: TextView
    private lateinit var tvPageAccountsValue: TextView
    private lateinit var tvCalendarMonth: TextView
    private lateinit var tvHomeMonthLabel: TextView
    private lateinit var tvCalendarBalanceLabel: TextView
    private lateinit var tvCalendarBalanceValue: TextView
    private lateinit var tvCalendarMonthStats: TextView
    private lateinit var btnCalendarAccountPicker: Button
    private lateinit var btnHomeAccountPicker: Button
    private lateinit var btnHomePreviousMonth: View
    private lateinit var btnHomeNextMonth: View
    private lateinit var btnFabAddEntry: Button
    private lateinit var tvSectionHome: TextView
    private lateinit var tvSectionCategories: TextView
    private lateinit var tvSectionCalendar: TextView
    private lateinit var tvSectionSettings: TextView
    private lateinit var tvSectionCalendarBadge: TextView
    private lateinit var ivSectionHome: ImageView
    private lateinit var ivSectionCategories: ImageView
    private lateinit var ivSectionCalendar: ImageView
    private lateinit var ivSectionSettings: ImageView
    private lateinit var navHomeContent: LinearLayout
    private lateinit var navCategoriesContent: LinearLayout
    private lateinit var navCalendarContent: LinearLayout
    private lateinit var navSettingsContent: LinearLayout
    private lateinit var gridCalendarDays: GridLayout
    private lateinit var viewPagerSections: ViewPager2
    private lateinit var tabFilters: TabLayout
    private lateinit var viewPagerItems: ViewPager2
    private lateinit var btnSectionHome: FrameLayout
    private lateinit var btnSectionCategories: FrameLayout
    private lateinit var btnSectionCalendar: FrameLayout
    private lateinit var btnSectionSettings: FrameLayout
    private lateinit var homePageView: View
    private lateinit var categoriesPageView: View
    private lateinit var calendarPageView: View
    private lateinit var settingsPageView: View
    private lateinit var layoutCategoriesPageList: LinearLayout
    private lateinit var tvEmptyCategoriesPage: TextView

    private val incomes = mutableListOf<Income>()
    private val subscriptions = mutableListOf<Subscription>()
    private val expenses = mutableListOf<Expense>()
    private val categories = mutableListOf<String>()
    private val categoryBudgets = mutableListOf<CategoryBudget>()
    private val items = mutableListOf<Item>()
    private lateinit var pagerAdapter: ItemsPagerAdapter
    private lateinit var sectionPagerAdapter: StaticPagesAdapter
    private var tabMediator: TabLayoutMediator? = null
    private var selectedHomeAccount = FILTER_ALL
    private var cashAmount = 0.0
    private val paymentAccounts = mutableListOf<PaymentAccount>()
    private var nextPaymentAccountId = DEFAULT_CARD_ACCOUNT_ID + 1
    private var selectedCalendarAccount = FILTER_ALL
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
    private var selectedCalendarDateIso = isoDateFormatter().format(Date())

    private val gson = Gson()
    private var isUiInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        applySavedLocale()
        setContentView(R.layout.activity_main)

        btnSectionHome = findViewById(R.id.btnSectionHome)
        btnSectionCategories = findViewById(R.id.btnSectionCategories)
        btnSectionCalendar = findViewById(R.id.btnSectionCalendar)
        btnSectionSettings = findViewById(R.id.btnSectionSettings)
        navHomeContent = findViewById(R.id.navHomeContent)
        navCategoriesContent = findViewById(R.id.navCategoriesContent)
        navCalendarContent = findViewById(R.id.navCalendarContent)
        navSettingsContent = findViewById(R.id.navSettingsContent)
        ivSectionHome = findViewById(R.id.ivSectionHome)
        ivSectionCategories = findViewById(R.id.ivSectionCategories)
        ivSectionCalendar = findViewById(R.id.ivSectionCalendar)
        ivSectionSettings = findViewById(R.id.ivSectionSettings)
        tvSectionHome = findViewById(R.id.tvSectionHome)
        tvSectionCategories = findViewById(R.id.tvSectionCategories)
        tvSectionCalendar = findViewById(R.id.tvSectionCalendar)
        tvSectionSettings = findViewById(R.id.tvSectionSettings)
        tvSectionCalendarBadge = findViewById(R.id.tvSectionCalendarBadge)
        viewPagerSections = findViewById(R.id.viewPagerSections)

        homePageView = LayoutInflater.from(this).inflate(R.layout.page_home, viewPagerSections, false)
        categoriesPageView = LayoutInflater.from(this).inflate(R.layout.page_categories, viewPagerSections, false)
        calendarPageView = LayoutInflater.from(this).inflate(R.layout.page_calendar, viewPagerSections, false)
        settingsPageView = LayoutInflater.from(this).inflate(R.layout.page_settings, viewPagerSections, false)

        bindHomePageViews()
        bindCategoriesPageViews()
        bindSettingsPageViews()
        bindCalendarPageViews()

        loadData()
        populateItems()
        setupCalendarAccountFilter()

        setupSectionPager()
        setupPager()
        updateSettingsPageValues()
        updateCalendarBadge()
        addPressAnimation(btnSectionHome)
        addPressAnimation(btnSectionCategories)
        addPressAnimation(btnSectionCalendar)
        addPressAnimation(btnSectionSettings)
        btnSectionHome.setOnClickListener { viewPagerSections.currentItem = 0 }
        btnSectionCategories.setOnClickListener { viewPagerSections.currentItem = 1 }
        btnSectionCalendar.setOnClickListener { viewPagerSections.currentItem = 2 }
        btnSectionSettings.setOnClickListener { viewPagerSections.currentItem = 3 }

        isUiInitialized = true
        updateTotals()
        refreshPagedContent()
    }

    private fun bindHomePageViews() {
        tvRemaining = homePageView.findViewById(R.id.tvRemaining)
        tvCashTotal = homePageView.findViewById(R.id.tvCashTotal)
        tvIncomeTotal = homePageView.findViewById(R.id.tvIncomeTotal)
        tvSubscriptionsTotal = homePageView.findViewById(R.id.tvSubscriptionsTotal)
        tvExpensesTotal = homePageView.findViewById(R.id.tvExpensesTotal)
        layoutHomeBudgetSummary = homePageView.findViewById(R.id.layoutHomeBudgetSummary)
        tvHomeBudgetSummaryValue = homePageView.findViewById(R.id.tvHomeBudgetSummaryValue)
        tvHomeBudgetRemainingLabel = homePageView.findViewById(R.id.tvHomeBudgetRemainingLabel)
        tvHomeBudgetRemainingValue = homePageView.findViewById(R.id.tvHomeBudgetRemainingValue)
        tvHomeBudgetAlerts = homePageView.findViewById(R.id.tvHomeBudgetAlerts)
        progressHomeBudget = homePageView.findViewById(R.id.progressHomeBudget)
        tvHomeMonthLabel = homePageView.findViewById(R.id.tvHomeMonthLabel)
        btnHomePreviousMonth = homePageView.findViewById(R.id.btnHomePreviousMonth)
        btnHomeNextMonth = homePageView.findViewById(R.id.btnHomeNextMonth)
        btnHomeAccountPicker = homePageView.findViewById(R.id.btnHomeAccountPicker)
        btnFabAddEntry = homePageView.findViewById(R.id.btnFabAddEntry)
        tabFilters = homePageView.findViewById(R.id.tabFilters)
        viewPagerItems = homePageView.findViewById(R.id.viewPagerItems)
        updateHomeMonthLabel()
        btnHomeAccountPicker.text = getCalendarFilterLabel(selectedHomeAccount) + " \u25BC"
        layoutHomeBudgetSummary.setOnClickListener {
            viewPagerSections.currentItem = 1
        }
        addPressAnimation(layoutHomeBudgetSummary)
        addPressAnimation(btnHomePreviousMonth)
        addPressAnimation(btnHomeNextMonth)
        addPressAnimation(btnHomeAccountPicker)
        addPressAnimation(btnFabAddEntry)
        btnHomePreviousMonth.setOnClickListener { changeHomeDisplayedMonthBy(-1) }
        btnHomeNextMonth.setOnClickListener { changeHomeDisplayedMonthBy(1) }
        tvHomeMonthLabel.setOnClickListener { showHomeMonthPicker() }
        btnHomeAccountPicker.setOnClickListener { showHomeAccountPickerDialog() }
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

    private fun bindCalendarPageViews() {
        tvCalendarMonth = calendarPageView.findViewById(R.id.tvCalendarMonth)
        tvCalendarBalanceLabel = calendarPageView.findViewById(R.id.tvCalendarBalanceLabel)
        tvCalendarBalanceValue = calendarPageView.findViewById(R.id.tvCalendarBalanceValue)
        tvCalendarMonthStats = calendarPageView.findViewById(R.id.tvCalendarMonthStats)
        btnCalendarAccountPicker = calendarPageView.findViewById(R.id.btnCalendarAccountPicker)
        gridCalendarDays = calendarPageView.findViewById(R.id.gridCalendarDays)
        addPressAnimation(btnCalendarAccountPicker)
        btnCalendarAccountPicker.setOnClickListener { showCalendarAccountPickerDialog() }
        calendarPageView.findViewById<LinearLayout>(R.id.layoutCalendarWeekHeader)
            .setOnTouchListener(createCalendarSwipeTouchListener())
        val btnPreviousMonth = calendarPageView.findViewById<View>(R.id.btnPreviousMonth)
        val btnNextMonth = calendarPageView.findViewById<View>(R.id.btnNextMonth)
        addPressAnimation(btnPreviousMonth)
        addPressAnimation(btnNextMonth)
        btnPreviousMonth.setOnClickListener {
            changeDisplayedMonthBy(-1)
        }
        btnNextMonth.setOnClickListener {
            changeDisplayedMonthBy(1)
        }
    }

    private fun setupCalendarAccountFilter() {
        btnCalendarAccountPicker.text = getCalendarAccountButtonText()
    }

    private fun bindCategoriesPageViews() {
        layoutCategoriesPageList = categoriesPageView.findViewById(R.id.layoutCategoriesPageList)
        tvEmptyCategoriesPage = categoriesPageView.findViewById(R.id.tvEmptyCategoriesPage)
        val btnAddAccount = categoriesPageView.findViewById<Button>(R.id.btnAddAccountPage)
        val btnAddCategory = categoriesPageView.findViewById<Button>(R.id.btnAddCategoryPage)
        addPressAnimation(btnAddAccount)
        addPressAnimation(btnAddCategory)
        btnAddAccount.setOnClickListener {
            showPaymentAccountDialog()
        }
        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun bindSettingsPageViews() {
        tvPageLanguageValue = settingsPageView.findViewById(R.id.tvPageLanguageValue)
        tvPageThemeValue = settingsPageView.findViewById(R.id.tvPageThemeValue)
        tvPageCurrencyValue = settingsPageView.findViewById(R.id.tvPageCurrencyValue)
        tvPageAccountsValue = settingsPageView.findViewById(R.id.tvPageAccountsValue)
        settingsPageView.findViewById<View>(R.id.rowPageLanguage).setOnClickListener { showLanguagePickerDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageTheme).setOnClickListener { showThemePickerDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageCurrency).setOnClickListener { showCurrencyPickerDialog() }
        settingsPageView.findViewById<View>(R.id.rowPageAccounts).setOnClickListener { showManageAccountsDialog() }
    }

    private fun setupSectionPager() {
        sectionPagerAdapter = StaticPagesAdapter(listOf(homePageView, categoriesPageView, calendarPageView, settingsPageView))
        viewPagerSections.adapter = sectionPagerAdapter
        viewPagerSections.offscreenPageLimit = 4
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
        styleSectionTab(navSettingsContent, tvSectionSettings, ivSectionSettings, position == 3)
    }

    private fun updateSettingsPageValues() {
        tvPageLanguageValue.text = getCurrentLanguageLabel()
        tvPageThemeValue.text = getCurrentThemeLabel()
        tvPageCurrencyValue.text = getCurrentCurrencyLabel()
        tvPageAccountsValue.text = getAccountManagementSummary()
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
        val textColor = getColor(if (isSelected) R.color.text_primary else R.color.text_secondary)
        label.setTextColor(textColor)
        icon.imageTintList = ColorStateList.valueOf(textColor)
        container.alpha = if (isSelected) 1f else 0.9f
    }

    private fun setupPager() {
        pagerAdapter = ItemsPagerAdapter()
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
        tvTitle.setTextColor(getColor(if (isSelected) R.color.text_primary else R.color.text_secondary))
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

    private fun getFilteredItems(position: Int): List<Item> {
        val visibleItems = getHomeVisibleItems()
        return when (position) {
            TAB_INCOME -> visibleItems.filter { it.type == ItemType.INCOME }
            TAB_SUBSCRIPTIONS -> visibleItems.filter { it.type == ItemType.SUBSCRIPTION }
            TAB_EXPENSES -> visibleItems.filter { it.type == ItemType.EXPENSE }
            else -> visibleItems
        }
    }

    private fun getHomeVisibleItems(): List<Item> {
        return items.filter(::matchesHomeItemFilter)
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
            ItemType.EXPENSE -> {
                val expense = expenses.getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(expense.accountId, selectedHomeAccount)) return false
                val date = parseIsoDate(expense.date) ?: return false
                isSameMonth(date, homeDisplayedMonth)
            }
        }
    }

    private fun refreshPagedContent() {
        pagerAdapter.notifyDataSetChanged()
        updateTabViews()
        updateHomeMonthLabel()
        updateHomeBudgetSummary()
        updateCategoriesPage()
        btnHomeAccountPicker.text = "${getCalendarFilterLabel(selectedHomeAccount)} \u25BC"
        btnCalendarAccountPicker.text = getCalendarAccountButtonText()
        updateCalendarPage()
        updateSettingsPageValues()
        updateCalendarBadge()
    }

    private fun updateCategoriesPage() {
        layoutCategoriesPageList.removeAllViews()
        tvEmptyCategoriesPage.visibility = View.GONE
        ensureDefaultPaymentAccounts()

        val cashAccounts = paymentAccounts
            .filter { it.type == AccountType.CASH }
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
        if (cashAccounts.isNotEmpty()) {
            addCategoriesPageSection(getString(R.string.categories_section_cash))
            cashAccounts.forEach { account ->
                addCategoriesPageAccountRow(account) {
                    showCashDialog(account.id)
                }
            }
        }

        val cardAndWalletAccounts = paymentAccounts
            .filter { isCardType(it.type) || isWalletType(it.type) }
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
        if (cardAndWalletAccounts.isNotEmpty()) {
            addCategoriesPageSection(getString(R.string.categories_section_cards_wallets))
            cardAndWalletAccounts.forEach { account ->
                addCategoriesPageAccountRow(account) {
                    showPaymentAccountDialog(account.id)
                }
            }
        }

        addCategoriesPageSection(getString(R.string.categories_section_categories))
        categories.forEachIndexed { index, category ->
            addCategoryBudgetRow(category, index)
        }
        if (categories.isEmpty()) {
            addCategoriesPageEmptyText(getString(R.string.no_categories))
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
            text = category
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
                    getString(R.string.category_budget_over, formatAmount(spent - budget.amount))
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
                getString(R.string.category_budget_progress, formatAmount(spent), formatAmount(budget.amount))
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

        layoutCategoriesPageList.addView(row)
    }

    private fun addCategoriesPageAccountRow(account: PaymentAccount, onClick: () -> Unit) {
        val rowView = LayoutInflater.from(this).inflate(R.layout.item_category_manage, layoutCategoriesPageList, false)
        val button = rowView.findViewById<Button>(R.id.btnCategoryRow)
        button.text = "${getPaymentAccountIcon(account.id)} ${getPaymentAccountDisplayName(account)} · ${getAccountTypeLabel(account.type)} · ${formatAmount(account.balance)}"
        button.setOnClickListener { }
        button.setOnLongClickListener {
            onClick()
            true
        }
        layoutCategoriesPageList.addView(rowView)
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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
        styleDialogWindow(dialog)
    }

    private fun getCategoryBudget(category: String): CategoryBudget? {
        return categoryBudgets.firstOrNull { it.category == category && it.amount > 0.0 }
    }

    private fun getCategorySpentForMonth(category: String, month: Calendar): Double {
        return expenses
            .filter { expense ->
                val expenseCategory = expense.category ?: getString(R.string.general_category)
                expenseCategory == category && isSameMonth(parseIsoDate(expense.date), month)
            }
            .sumOf { it.amount }
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
        val statuses = getCategoryBudgetStatuses(homeDisplayedMonth)
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

        val alertStatuses = statuses.filter { it.ratio >= 0.75 }.take(3)
        tvHomeBudgetAlerts.text = if (alertStatuses.isEmpty()) {
            getString(R.string.home_budget_all_ok)
        } else {
            alertStatuses.joinToString("\n") { status ->
                if (status.ratio > 1.0) {
                    getString(
                        R.string.home_budget_alert_over,
                        status.budget.category,
                        formatAmount(status.spent - status.budget.amount)
                    )
                } else {
                    getString(
                        R.string.home_budget_alert_near,
                        status.budget.category,
                        (status.ratio * 100).toInt()
                    )
                }
            }
        }
    }

    private fun getCategoryBudgetStatuses(month: Calendar): List<CategoryBudgetStatus> {
        return categoryBudgets
            .mapNotNull { budget ->
                if (budget.amount <= 0.0 || !categories.contains(budget.category)) {
                    null
                } else {
                    CategoryBudgetStatus(
                        budget = budget,
                        spent = getCategorySpentForMonth(budget.category, month)
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnApply.setOnClickListener {
            dialog.dismiss()
            saveLanguageCode(newLanguageCode)
            setLocale(newLanguageCode)
        }

        dialog.show()
        styleDialogWindow(dialog)
        styleDialogActionButtons(dialog)
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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
        styleDialogWindow(dialog)
        styleDialogActionButtons(dialog)
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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
        styleDialogWindow(dialog)
        styleDialogActionButtons(dialog)
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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
        styleDialogWindow(dialog)
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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
        styleDialogWindow(dialog)
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
        val cashNames = setOf("Cash", "Bargeld", "Наличные")
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
            ItemType.EXPENSE -> "🔴"
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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
        styleDialogWindow(dialog)
    }

    private fun showHomeAccountPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account_picker, null)
        val layoutOptions = dialogView.findViewById<LinearLayout>(R.id.layoutAccountPickerOptions)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccountPicker)
        ensureDefaultPaymentAccounts()

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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
        styleDialogWindow(dialog)
    }

    private fun showAddEntryMenuDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry_menu, null)
        val btnIncome = dialogView.findViewById<Button>(R.id.btnAddMenuIncome)
        val btnExpense = dialogView.findViewById<Button>(R.id.btnAddMenuExpense)
        val btnSubscription = dialogView.findViewById<Button>(R.id.btnAddMenuSubscription)
        addPressAnimation(btnIncome)
        addPressAnimation(btnExpense)
        addPressAnimation(btnSubscription)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnIncome.setOnClickListener {
            dialog.dismiss()
            showAddIncomeDialog()
        }
        btnExpense.setOnClickListener {
            dialog.dismiss()
            showAddExpenseDialog()
        }
        btnSubscription.setOnClickListener {
            dialog.dismiss()
            showAddSubscriptionDialog()
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun showManageAccountsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_accounts, null)
        val layoutAccounts = dialogView.findViewById<LinearLayout>(R.id.layoutAccountRows)
        val btnAddAccount = dialogView.findViewById<Button>(R.id.btnAddAccount)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccounts)
        ensureDefaultPaymentAccounts()

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        paymentAccounts
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
            .forEach { account ->
            val button = LayoutInflater.from(this)
                .inflate(R.layout.item_category_manage, layoutAccounts, false)
                .findViewById<Button>(R.id.btnCategoryRow)
            button.text = "${getPaymentAccountDisplayName(account)} · ${getAccountTypeLabel(account.type)}"
            button.setOnClickListener { }
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
        styleDialogWindow(dialog)
    }

    private fun showPaymentAccountDialog(accountId: Long? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_account, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAccountDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etAccountName)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerAccountType)
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
            etBalance.setText(existingAccount.balance.toString())
            spinnerType.setSelection(typeOptions.indexOf(existingAccount.type).takeIf { it >= 0 } ?: 0)
            btnDelete.visibility = if (paymentAccounts.size > 1) View.VISIBLE else View.GONE
        } else {
            tvTitle.text = getString(R.string.add_account)
            etName.setText("")
            etBalance.setText("0.0")
            spinnerType.setSelection(typeOptions.indexOf(AccountType.BANK_CARD))
            btnDelete.visibility = View.GONE
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnDelete.setOnClickListener {
            val targetAccount = existingAccount ?: return@setOnClickListener
            deletePaymentAccount(targetAccount)
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            val newLabel = etName.text.toString().trim()
            val selectedType = typeOptions[spinnerType.selectedItemPosition]
            val balance = etBalance.text.toString().toDoubleOrNull() ?: 0.0
            if (newLabel.isNotEmpty()) {
                if (existingAccount != null) {
                    val index = paymentAccounts.indexOfFirst { it.id == existingAccount.id }
                    if (index >= 0) {
                        paymentAccounts[index] = existingAccount.copy(
                            name = newLabel,
                            type = selectedType,
                            balance = balance
                        )
                    }
                } else {
                    paymentAccounts.add(
                        PaymentAccount(
                            id = nextPaymentAccountId++,
                            name = newLabel,
                            type = selectedType,
                            balance = balance
                        )
                    )
                }
                saveData()
                populateItems()
                updateTotals()
                updateSettingsPageValues()
                btnCalendarAccountPicker.text = getCalendarAccountButtonText()
                refreshPagedContent()
            }
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
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
            for (index in expenses.indices) {
                if (expenses[index].accountId == targetAccount.id) {
                    expenses[index] = expenses[index].copy(accountId = fallbackAccount.id)
                }
            }
            paymentAccounts.removeAll { it.id == targetAccount.id }
            val removedAccountFilter = buildExactAccountFilter(targetAccount.id)
            if (selectedHomeAccount == removedAccountFilter) {
                selectedHomeAccount = FILTER_ALL
            }
            if (selectedCalendarAccount == removedAccountFilter) {
                selectedCalendarAccount = FILTER_ALL
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun styleDialogWindow(dialog: AlertDialog) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }

    private fun styleDialogActionButtons(dialog: AlertDialog) {
        styleDialogButton(
            button = dialog.getButton(AlertDialog.BUTTON_POSITIVE),
            backgroundRes = R.drawable.bg_button_dark,
            textColorRes = R.color.button_text_light
        )
        styleDialogButton(
            button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE),
            backgroundRes = R.drawable.bg_button_light,
            textColorRes = R.color.button_text_dark
        )
        styleDialogButton(
            button = dialog.getButton(AlertDialog.BUTTON_NEUTRAL),
            backgroundRes = R.drawable.bg_button_light,
            textColorRes = R.color.button_text_dark
        )
    }

    private fun styleDialogButton(button: Button?, backgroundRes: Int, textColorRes: Int) {
        if (button == null) return
        button.background = InsetDrawable(getDrawable(backgroundRes), 10, 8, 10, 8)
        button.setTextColor(getColor(textColorRes))
        button.setAllCaps(false)
        button.alpha = 0.98f
    }

    private fun showDeleteConfirmation(message: String, onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDeleteConfirmTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirmMessage)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelDeleteConfirm)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteDeleteConfirm)

        tvTitle.text = getString(R.string.delete_confirm_title)
        tvMessage.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
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

        buildCalendarGrid()
        updateCalendarBalancePreview()
        updateCalendarMonthStats()
    }

    private fun updateCalendarBalancePreview() {
        val selectedDateLabel = formatDisplayDate(selectedCalendarDateIso)
        val projectedBalance = getProjectedBalanceForDate(selectedCalendarDateIso, selectedCalendarAccount)
        tvCalendarBalanceLabel.text = getString(R.string.calendar_balance_for_day, selectedDateLabel)
        tvCalendarBalanceValue.text = formatAmount(projectedBalance)
        tvCalendarBalanceValue.setTextColor(getColor(R.color.balance_text))
    }

    private fun updateCalendarMonthStats() {
        val events = getEventsForMonth(displayedMonth, selectedCalendarAccount)
        val incomeTotal = events
            .filter { it.type == ItemType.INCOME }
            .sumOf { it.amount }
        val outgoingTotal = events
            .filter { it.type != ItemType.INCOME }
            .sumOf { it.amount }
        val monthDelta = incomeTotal - outgoingTotal

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
        return isoDateFormatter().format(calendar.time)
    }

    private fun buildCalendarGrid() {
        gridCalendarDays.removeAllViews()

        val monthCalendar = displayedMonth.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayIndex = ((monthCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7)
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        repeat(42) { cellIndex ->
            val view = if (cellIndex < firstDayIndex || cellIndex >= firstDayIndex + daysInMonth) {
                createEmptyCalendarCell()
            } else {
                val dayNumber = cellIndex - firstDayIndex + 1
                val dateCalendar = displayedMonth.clone() as Calendar
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayNumber)
                val dateIso = isoDateFormatter().format(dateCalendar.time)
                createCalendarDayCell(dayNumber, dateIso)
            }
            gridCalendarDays.addView(view)
        }
    }

    private fun createEmptyCalendarCell(): View {
        return FrameLayout(this).apply {
            layoutParams = createCalendarCellLayoutParams()
            minimumHeight = dpToPx(48)
            alpha = 0f
            setOnTouchListener(createCalendarSwipeTouchListener())
        }
    }

    private fun createCalendarDayCell(dayNumber: Int, dateIso: String): View {
        val events = getEventsForDate(dateIso, selectedCalendarAccount)
        val hasEvents = events.isNotEmpty()
        val isToday = dateIso == isoDateFormatter().format(Date())
        val isSelected = dateIso == selectedCalendarDateIso
        val incomeAmount = events.filter { it.type == ItemType.INCOME }.sumOf { it.amount }
        val expenseAmount = events.filter { it.type != ItemType.INCOME }.sumOf { it.amount }

        val container = LinearLayout(this).apply {
            layoutParams = createCalendarCellLayoutParams()
            minimumHeight = dpToPx(48)
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(dpToPx(1), dpToPx(1), dpToPx(1), dpToPx(1))
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
        }

        val dayLabel = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(28),
                dpToPx(20)
            )
            text = dayNumber.toString()
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            includeFontPadding = false
            background = when {
                isSelected && !isToday -> getDrawable(R.drawable.bg_calendar_day_number_selected)
                else -> null
            }
            setTextColor(
                getColor(
                    when {
                        isToday -> R.color.text_primary
                        isSelected -> R.color.calendar_accent
                        else -> R.color.text_primary
                    }
                )
            )
            if (isToday) {
                setShadowLayer(
                    dpToPx(4).toFloat(),
                    0f,
                    0f,
                    getColor(R.color.calendar_accent)
                )
            }
            typeface = if (isSelected || isToday) {
                android.graphics.Typeface.DEFAULT_BOLD
            } else {
                android.graphics.Typeface.DEFAULT
            }
        }

        val indicator = CalendarPieMarkerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(26),
                dpToPx(26)
            ).apply {
                topMargin = 0
            }
            setData(
                incomeAmount = incomeAmount,
                expenseAmount = expenseAmount,
                isSelected = isSelected,
                isToday = isToday
            )
            alpha = if (hasEvents || isToday || isSelected) 1f else 0.92f
        }

        container.addView(dayLabel)
        container.addView(indicator)
        return container
    }

    private fun changeDisplayedMonthBy(offset: Int) {
        displayedMonth.add(Calendar.MONTH, offset)
        selectedCalendarDateIso = isoDateFormatter().format(displayedMonth.time)
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
            setMargins(dpToPx(1), dpToPx(1), dpToPx(1), dpToPx(1))
        }
    }

    private fun getProjectedBalanceForDate(dateIso: String, accountFilter: String = FILTER_ALL): Double {
        val target = parseIsoDate(dateIso) ?: return 0.0
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
                val occurrences = countSubscriptionOccurrencesUntil(subscription, target)
                subscription.amount * occurrences
            }

        return baseBalance + incomeTotal - expenseTotal - subscriptionsTotal
    }

    private fun countSubscriptionOccurrencesUntil(subscription: Subscription, target: Calendar): Int {
        val occurrenceDate = parseIsoDate(subscription.nextChargeDate) ?: return 0
        occurrenceDate.set(Calendar.HOUR_OF_DAY, 0)
        occurrenceDate.set(Calendar.MINUTE, 0)
        occurrenceDate.set(Calendar.SECOND, 0)
        occurrenceDate.set(Calendar.MILLISECOND, 0)

        val targetDate = (target.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var count = 0
        while (!occurrenceDate.after(targetDate)) {
            count++
            if (subscription.period == "yearly") {
                occurrenceDate.add(Calendar.YEAR, 1)
            } else {
                occurrenceDate.add(Calendar.MONTH, 1)
            }
        }
        return count
    }

    private fun countIncomeOccurrencesUntil(income: Income, target: Calendar): Int {
        val occurrenceDate = parseIsoDate(income.expectedDate) ?: return 0
        occurrenceDate.set(Calendar.HOUR_OF_DAY, 0)
        occurrenceDate.set(Calendar.MINUTE, 0)
        occurrenceDate.set(Calendar.SECOND, 0)
        occurrenceDate.set(Calendar.MILLISECOND, 0)

        val targetDate = (target.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var count = 0
        while (!occurrenceDate.after(targetDate)) {
            count++
            occurrenceDate.add(Calendar.MONTH, 1)
        }
        return count
    }

    private fun showCashDialog(preferredAccountId: Long? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cash, null)
        val spinnerAccount = dialogView.findViewById<Spinner>(R.id.spinnerCashAccount)
        val etCashAmount = dialogView.findViewById<EditText>(R.id.etCashAmount)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCashDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteCashDialog)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveCashDialog)
        ensureDefaultPaymentAccounts()
        val accountValues = paymentAccounts.sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }

        spinnerAccount.adapter = createSpinnerAdapter(accountValues.map(::getPaymentAccountDisplayName))
        val selectedFilterAccountId = preferredAccountId ?: parseAccountIdFromFilter(selectedCalendarAccount)
        spinnerAccount.setSelection(accountValues.indexOfFirst { it.id == selectedFilterAccountId }.takeIf { it >= 0 } ?: 0)

        fun syncAccountAmount() {
            val selectedAccount = accountValues[spinnerAccount.selectedItemPosition]
            etCashAmount.setText(selectedAccount.balance.toString())
            btnDelete.visibility = if (paymentAccounts.size > 1) View.VISIBLE else View.GONE
        }

        syncAccountAmount()
        spinnerAccount.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                syncAccountAmount()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            val selectedAccount = accountValues.getOrNull(spinnerAccount.selectedItemPosition)
                ?: return@setOnClickListener
            deletePaymentAccount(selectedAccount) {
                dialog.dismiss()
            }
        }

        btnSave.setOnClickListener {
            val selectedAccount = accountValues[spinnerAccount.selectedItemPosition]
            val index = paymentAccounts.indexOfFirst { it.id == selectedAccount.id }
            if (index >= 0) {
                paymentAccounts[index] = selectedAccount.copy(
                    balance = etCashAmount.text.toString().toDoubleOrNull() ?: 0.0
                )
            }
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun showCalendarDayDialog(dateIso: String) {
        val events = getEventsForDate(dateIso, selectedCalendarAccount)
        if (events.isEmpty()) return

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_calendar_day, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCalendarDayDialogTitle)
        val tvEvents = dialogView.findViewById<TextView>(R.id.tvCalendarDayDialogEvents)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseCalendarDayDialog)

        tvTitle.text = getString(R.string.calendar_day_details, formatDisplayDate(dateIso))
        tvEvents.text = events.joinToString("\n") { formatCalendarEventLine(it) }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun formatCalendarEventLine(event: CalendarEvent): String {
        val sign = if (event.type == ItemType.INCOME) "+" else "-"
        val detail = event.detail.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty()
        return "$sign ${event.name} · ${formatAmount(event.amount)}$detail"
    }

    private fun getEventsForMonth(month: Calendar, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        return getDateStringsForMonth(month).flatMap { dateIso ->
            getEventsForDate(dateIso, accountFilter)
        }
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

    private fun getEventsForDate(dateIso: String, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()

        incomes.forEach { income ->
            val matchesDate = if (income.period == "monthly") {
                incomeOccursOnDate(income, dateIso)
            } else {
                sameCalendarDate(income.expectedDate, dateIso)
            }
            if (matchesDate && matchesAccountFilter(income.accountId, accountFilter)) {
                events.add(
                    CalendarEvent(
                        type = ItemType.INCOME,
                        name = income.name,
                        amount = income.amount,
                        detail = buildEventDetail(
                            getIncomeTypeDisplayName(income.type.ifBlank { getString(R.string.income_default_name) }),
                            income.accountId
                        )
                    )
                )
            }
        }

        expenses.forEach { expense ->
            if (sameCalendarDate(expense.date, dateIso) && matchesAccountFilter(expense.accountId, accountFilter)) {
                events.add(
                    CalendarEvent(
                        type = ItemType.EXPENSE,
                        name = expense.name,
                        amount = expense.amount,
                        detail = buildEventDetail(
                            expense.category ?: getString(R.string.general_category),
                            expense.accountId
                        )
                    )
                )
            }
        }

        subscriptions.forEach { subscription ->
            if (subscriptionOccursOnDate(subscription, dateIso) && matchesAccountFilter(subscription.accountId, accountFilter)) {
                events.add(
                    CalendarEvent(
                        type = ItemType.SUBSCRIPTION,
                        name = subscription.name,
                        amount = subscription.amount,
                        detail = buildEventDetail(
                            if (subscription.period == "yearly") getString(R.string.yearly) else getString(R.string.monthly),
                            subscription.accountId
                        )
                    )
                )
            }
        }

        return events.sortedWith(compareBy<CalendarEvent> { eventPriority(it.type) }.thenBy { it.name.lowercase(Locale.getDefault()) })
    }

    private fun incomeOccursOnDate(income: Income, dateIso: String): Boolean {
        val start = parseIsoDate(income.expectedDate) ?: return false
        val target = parseIsoDate(dateIso) ?: return false

        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)
        target.set(Calendar.HOUR_OF_DAY, 0)
        target.set(Calendar.MINUTE, 0)
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)

        if (target.before(start)) return false

        val anchorDay = start.get(Calendar.DAY_OF_MONTH)
        val expectedDay = minOf(anchorDay, target.getActualMaximum(Calendar.DAY_OF_MONTH))
        return target.get(Calendar.DAY_OF_MONTH) == expectedDay
    }

    private fun subscriptionOccursOnDate(subscription: Subscription, dateIso: String): Boolean {
        val start = parseIsoDate(subscription.nextChargeDate) ?: return false
        val target = parseIsoDate(dateIso) ?: return false

        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)
        target.set(Calendar.HOUR_OF_DAY, 0)
        target.set(Calendar.MINUTE, 0)
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)

        if (target.before(start)) return false

        val anchorDay = start.get(Calendar.DAY_OF_MONTH)
        return if (subscription.period == "yearly") {
            if (target.get(Calendar.MONTH) != start.get(Calendar.MONTH)) {
                false
            } else {
                val expectedDay = minOf(anchorDay, target.getActualMaximum(Calendar.DAY_OF_MONTH))
                target.get(Calendar.DAY_OF_MONTH) == expectedDay
            }
        } else {
            val monthsDiff =
                (target.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 +
                    (target.get(Calendar.MONTH) - start.get(Calendar.MONTH))
            if (monthsDiff < 0) {
                false
            } else {
                val expectedDay = minOf(anchorDay, target.getActualMaximum(Calendar.DAY_OF_MONTH))
                target.get(Calendar.DAY_OF_MONTH) == expectedDay
            }
        }
    }

    private fun eventPriority(type: ItemType): Int {
        return when (type) {
            ItemType.INCOME -> 0
            ItemType.SUBSCRIPTION -> 1
            ItemType.EXPENSE -> 2
        }
    }

    private fun sameCalendarDate(value: String?, expectedIso: String): Boolean {
        val left = parseIsoDate(value)?.timeInMillis ?: return false
        val right = parseIsoDate(expectedIso)?.timeInMillis ?: return false
        return left == right
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
        return subscription.nextChargeDate.isNullOrBlank() ||
            getSubscriptionOccurrenceDateInMonth(subscription, month) != null
    }

    private fun getSubscriptionOccurrenceDateInMonth(subscription: Subscription, month: Calendar): String? {
        if (subscription.nextChargeDate.isNullOrBlank()) return null
        return getDateStringsForMonth(month).firstOrNull { dateIso ->
            subscriptionOccursOnDate(subscription, dateIso)
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

    private fun loadData() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val incomesJson = prefs.getString("incomes", "[]")
        val subscriptionsJson = prefs.getString("subscriptions", "[]")
        val expensesJson = prefs.getString("expenses", "[]")
        val categoriesJson = prefs.getString("categories", "[]")
        val categoryBudgetsJson = prefs.getString(KEY_CATEGORY_BUDGETS, "[]")
        cashAmount = prefs.getFloat(KEY_CASH_AMOUNT, 0f).toDouble()
        val rawIncomes = runCatching {
            gson.fromJson<List<Income>>(incomesJson, object : TypeToken<List<Income>>() {}.type)
        }.getOrNull().orEmpty()
        val rawSubscriptions = runCatching {
            gson.fromJson<List<Subscription>>(subscriptionsJson, object : TypeToken<List<Subscription>>() {}.type)
        }.getOrNull().orEmpty()
        val rawExpenses = runCatching {
            gson.fromJson<List<Expense>>(expensesJson, object : TypeToken<List<Expense>>() {}.type)
        }.getOrNull().orEmpty()
        val legacyBalances = runCatching {
            gson.fromJson<Map<String, Double>>(prefs.getString(KEY_ACCOUNT_BALANCES, "{}"), object : TypeToken<Map<String, Double>>() {}.type)
        }.getOrNull().orEmpty()
        val legacyLabels = runCatching {
            gson.fromJson<Map<String, String>>(prefs.getString(KEY_ACCOUNT_LABELS, "{}"), object : TypeToken<Map<String, String>>() {}.type)
        }.getOrNull().orEmpty()
        val savedAccounts = runCatching {
            gson.fromJson<List<PaymentAccount>>(prefs.getString(KEY_PAYMENT_ACCOUNTS, "[]"), object : TypeToken<List<PaymentAccount>>() {}.type)
        }.getOrNull().orEmpty()

        paymentAccounts.clear()
        if (savedAccounts.isNotEmpty()) {
            paymentAccounts.addAll(savedAccounts.map(::sanitizePaymentAccount))
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
        nextPaymentAccountId = prefs.getLong(
            KEY_NEXT_PAYMENT_ACCOUNT_ID,
            (paymentAccounts.maxOfOrNull { it.id } ?: 0L) + 1L
        ).coerceAtLeast((paymentAccounts.maxOfOrNull { it.id } ?: 0L) + 1L)

        cashAmount = paymentAccounts.firstOrNull { it.type == AccountType.CASH }?.balance ?: cashAmount
        incomes.addAll(rawIncomes.map(::sanitizeIncome))
        subscriptions.addAll(rawSubscriptions.map(::sanitizeSubscription))
        expenses.addAll(rawExpenses.map(::sanitizeExpense))

        runCatching {
            gson.fromJson<List<String>>(categoriesJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull()?.let { categories.addAll(it) }
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
        prefs.putString("incomes", gson.toJson(incomes))
        prefs.putString("subscriptions", gson.toJson(subscriptions))
        prefs.putString("expenses", gson.toJson(expenses))
        prefs.putString("categories", gson.toJson(categories))
        prefs.putString(KEY_CATEGORY_BUDGETS, gson.toJson(categoryBudgets))
        prefs.putFloat(KEY_CASH_AMOUNT, cashAmount.toFloat())
        prefs.putString(KEY_PAYMENT_ACCOUNTS, gson.toJson(paymentAccounts))
        prefs.putLong(KEY_NEXT_PAYMENT_ACCOUNT_ID, nextPaymentAccountId)
        prefs.apply()
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
            val nextChargeDisplay = getSubscriptionOccurrenceDateInMonth(subscription, homeDisplayedMonth)
                ?.let { formatDisplayDate(it) }
                ?: getNextSubscriptionDate(subscription)?.let { formatDisplayDate(it) }
                ?: periodLabel
            items.add(
                Item(
                    name = subscription.name,
                    amount = -amount,
                    meta = "$periodLabel · ${getString(R.string.next_payment_short, nextChargeDisplay)}",
                    trailing = "${getPaymentAccountIcon(subscription.accountId)} ${getPaymentAccountName(subscription.accountId)}",
                    type = ItemType.SUBSCRIPTION,
                    sourceIndex = index
                )
            )
        }
        expenses.forEachIndexed { index, expense ->
            val categoryLabel = expense.category?.takeIf { it.isNotBlank() } ?: getString(R.string.general_category)
            items.add(
                Item(
                    name = expense.name,
                    amount = -expense.amount,
                    meta = categoryLabel,
                    trailing = "${getPaymentAccountIcon(expense.accountId)} ${getPaymentAccountName(expense.accountId)} · ${formatDisplayDate(expense.date)}",
                    type = ItemType.EXPENSE,
                    sourceIndex = index
                )
            )
        }
    }

    private fun updateTotals() {
        val visibleItems = getHomeVisibleItems()
        val baseBalance = getBaseBalanceForAccount(selectedHomeAccount)
        val incomeTotal = visibleItems
            .filter { it.type == ItemType.INCOME }
            .sumOf { it.amount }
        val subscriptionsTotal = visibleItems
            .filter { it.type == ItemType.SUBSCRIPTION }
            .sumOf { -it.amount }
        val expensesTotal = visibleItems
            .filter { it.type == ItemType.EXPENSE }
            .sumOf { -it.amount }
        val cashAccountsTotal = paymentAccounts.filter { it.type == AccountType.CASH }.sumOf { it.balance }

        tvCashTotal.text = formatAmount(cashAccountsTotal)
        setSignedTotal(tvIncomeTotal, incomeTotal)
        setSignedTotal(tvSubscriptionsTotal, -subscriptionsTotal)
        setSignedTotal(tvExpensesTotal, -expensesTotal)
        tvRemaining.text = formatAmount(baseBalance + visibleItems.sumOf { it.amount })
        tvRemaining.setTextColor(getColor(R.color.balance_text))
    }

    private fun setSignedTotal(view: TextView, value: Double) {
        view.text = formatSignedAmount(value)
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

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        if (categories.isEmpty()) {
            tvEmptyCategories.visibility = View.VISIBLE
        } else {
            tvEmptyCategories.visibility = View.GONE
            categories.forEachIndexed { index, category ->
                val rowView = LayoutInflater.from(this).inflate(R.layout.item_category_manage, layoutCategoryList, false)
                val button = rowView.findViewById<Button>(R.id.btnCategoryRow)
                button.text = category
                button.setOnClickListener { }
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
        styleDialogWindow(dialog)
    }

    private fun showCategoryDialog(categoryIndex: Int? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCategoryDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCategoryDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteCategoryDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryCategoryDialog)

        if (categoryIndex != null && categoryIndex in categories.indices) {
            tvTitle.text = getString(R.string.edit_category)
            etName.setText(categories[categoryIndex])
            btnDelete.visibility = View.VISIBLE
            btnPrimary.text = getString(R.string.save_changes)
        } else {
            tvTitle.text = getString(R.string.add_category)
            btnDelete.visibility = View.GONE
            btnPrimary.text = getString(R.string.add_category)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            if (categoryIndex != null && categoryIndex in categories.indices) {
                val categoryName = categories[categoryIndex]
                showDeleteConfirmation(getString(R.string.delete_category_message, categoryName)) {
                    val deletedCategory = categories.removeAt(categoryIndex)
                    categoryBudgets.removeAll { it.category == deletedCategory }
                    expenses.replaceAll { expense ->
                        if (expense.category == deletedCategory) {
                            val updatedName = if (expense.name == deletedCategory) {
                                getString(R.string.expense_default_name)
                            } else {
                                expense.name
                            }
                            expense.copy(name = updatedName, category = null)
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
                saveData()
                populateItems()
                updateTotals()
                refreshPagedContent()
            }
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun showAddIncomeDialog() {
        showIncomeDialog()
    }

    private fun showIncomeDialog(incomeIndex: Int? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_income, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvIncomeDialogTitle)
        val spinnerIncomeType = dialogView.findViewById<Spinner>(R.id.spinnerIncomeType)
        val spinnerIncomePeriod = dialogView.findViewById<Spinner>(R.id.spinnerIncomePeriod)
        val spinnerIncomeAccount = dialogView.findViewById<Spinner>(R.id.spinnerIncomeAccount)
        val etIncomeName = dialogView.findViewById<EditText>(R.id.etIncomeName)
        val etIncomeAmount = dialogView.findViewById<EditText>(R.id.etIncomeAmount)
        val btnIncomeDate = dialogView.findViewById<Button>(R.id.btnIncomeDate)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelIncomeDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteIncomeDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryIncomeDialog)
        var selectedIncomeDate: String? = isoDateFormatter().format(Date())

        val incomeTypes = getIncomeTypeOptions()
        spinnerIncomeType.adapter = createSpinnerAdapter(incomeTypes)
        val incomePeriods = getIncomePeriodOptions()
        spinnerIncomePeriod.adapter = createSpinnerAdapter(incomePeriods)
        val accountValues = getPaymentAccountOptions()
        spinnerIncomeAccount.adapter = createSpinnerAdapter(accountValues.map(::getPaymentAccountDisplayName))

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
        } else {
            tvTitle.text = getString(R.string.add_income)
            btnDelete.visibility = View.GONE
            btnPrimary.text = getString(R.string.add_income)
            spinnerIncomePeriod.setSelection(0)
        }
        updateDateButton(btnIncomeDate, selectedIncomeDate, R.string.select_income_date)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnIncomeDate.setOnClickListener {
            showDatePicker(selectedIncomeDate) { pickedDate ->
                selectedIncomeDate = pickedDate
                updateDateButton(btnIncomeDate, pickedDate, R.string.select_income_date)
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
            val amount = etIncomeAmount.text.toString().toDoubleOrNull() ?: 0.0
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
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun showAddSubscriptionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subscription, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvSubscriptionDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val spinnerPeriod = dialogView.findViewById<Spinner>(R.id.spinnerPeriod)
        val spinnerSubscriptionAccount = dialogView.findViewById<Spinner>(R.id.spinnerSubscriptionAccount)
        val btnSubscriptionDate = dialogView.findViewById<Button>(R.id.btnSubscriptionDate)
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
        spinnerSubscriptionAccount.adapter = createSpinnerAdapter(accountValues.map(::getPaymentAccountDisplayName))
        updateDateButton(btnSubscriptionDate, selectedChargeDate, R.string.select_charge_date)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubscriptionDate.setOnClickListener {
            showDatePicker(selectedChargeDate) { pickedDate ->
                selectedChargeDate = pickedDate
                updateDateButton(btnSubscriptionDate, pickedDate, R.string.select_charge_date)
            }
        }

        btnPrimary.setOnClickListener {
            val name = etName.text.toString().trim()
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val period = if (spinnerPeriod.selectedItemPosition == 0) "monthly" else "yearly"
            subscriptions.add(
                Subscription(
                    name,
                    amount,
                    period,
                    selectedChargeDate,
                    accountValues[spinnerSubscriptionAccount.selectedItemPosition].id
                )
            )
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun showEditSubscriptionDialog(subscriptionIndex: Int) {
        if (subscriptionIndex !in subscriptions.indices) return

        val subscription = subscriptions[subscriptionIndex]
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subscription, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvSubscriptionDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val spinnerPeriod = dialogView.findViewById<Spinner>(R.id.spinnerPeriod)
        val spinnerSubscriptionAccount = dialogView.findViewById<Spinner>(R.id.spinnerSubscriptionAccount)
        val btnSubscriptionDate = dialogView.findViewById<Button>(R.id.btnSubscriptionDate)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelSubscriptionDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteSubscriptionDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimarySubscriptionDialog)
        var selectedChargeDate: String? = subscription.nextChargeDate ?: isoDateFormatter().format(Date())

        tvTitle.text = getString(R.string.edit_subscription)
        btnDelete.visibility = View.VISIBLE
        btnPrimary.text = getString(R.string.save_changes)

        val periods = arrayOf(getString(R.string.monthly), getString(R.string.yearly))
        spinnerPeriod.adapter = createSpinnerAdapter(periods.toList())
        val accountValues = getPaymentAccountOptions()
        spinnerSubscriptionAccount.adapter = createSpinnerAdapter(accountValues.map(::getPaymentAccountDisplayName))

        etName.setText(subscription.name)
        etAmount.setText(subscription.amount.toString())
        spinnerPeriod.setSelection(if (subscription.period == "monthly") 0 else 1)
        spinnerSubscriptionAccount.setSelection(accountValues.indexOfFirst { it.id == subscription.accountId }.takeIf { it >= 0 } ?: 0)
        updateDateButton(btnSubscriptionDate, selectedChargeDate, R.string.select_charge_date)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubscriptionDate.setOnClickListener {
            showDatePicker(selectedChargeDate) { pickedDate ->
                selectedChargeDate = pickedDate
                updateDateButton(btnSubscriptionDate, pickedDate, R.string.select_charge_date)
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
            val updatedName = etName.text.toString().trim()
            val updatedAmount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val updatedPeriod = if (spinnerPeriod.selectedItemPosition == 0) "monthly" else "yearly"
            subscriptions[subscriptionIndex] = Subscription(
                updatedName,
                updatedAmount,
                updatedPeriod,
                selectedChargeDate,
                accountValues[spinnerSubscriptionAccount.selectedItemPosition].id
            )
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
    }

    private fun showAddExpenseDialog() {
        showExpenseDialog()
    }

    private fun showExpenseDialog(expenseIndex: Int? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvExpenseDialogTitle)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerExpenseAccount = dialogView.findViewById<Spinner>(R.id.spinnerExpenseAccount)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val btnExpenseDate = dialogView.findViewById<Button>(R.id.btnExpenseDate)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelExpenseDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteExpenseDialog)
        val btnPrimary = dialogView.findViewById<Button>(R.id.btnPrimaryExpenseDialog)
        var selectedExpenseDate = isoDateFormatter().format(Date())

        val categoryOptions = getCategoryOptions()
        spinnerCategory.adapter = createSpinnerAdapter(categoryOptions)
        val accountValues = getPaymentAccountOptions()
        spinnerExpenseAccount.adapter = createSpinnerAdapter(accountValues.map(::getPaymentAccountDisplayName))

        if (expenseIndex != null && expenseIndex in expenses.indices) {
            val expense = expenses[expenseIndex]
            tvTitle.text = getString(R.string.edit_expense)
            btnDelete.visibility = View.VISIBLE
            btnPrimary.text = getString(R.string.save_changes)
            etName.setText(expense.name)
            etAmount.setText(expense.amount.toString())
            val selectedCategory = expense.category ?: getString(R.string.general_category)
            spinnerCategory.setSelection(categoryOptions.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0)
            spinnerExpenseAccount.setSelection(accountValues.indexOfFirst { it.id == expense.accountId }.takeIf { it >= 0 } ?: 0)
            selectedExpenseDate = parseIsoDate(expense.date)?.let { isoDateFormatter().format(it.time) }
                ?: isoDateFormatter().format(Date())
        } else {
            tvTitle.text = getString(R.string.add_expense)
            btnDelete.visibility = View.GONE
            btnPrimary.text = getString(R.string.add_expense)
        }
        updateDateButton(btnExpenseDate, selectedExpenseDate, R.string.select_expense_date)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnExpenseDate.setOnClickListener {
            showDatePicker(selectedExpenseDate) { pickedDate ->
                selectedExpenseDate = pickedDate
                updateDateButton(btnExpenseDate, pickedDate, R.string.select_expense_date)
            }
        }

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
            val typedName = etName.text.toString().trim()
            val name = when {
                typedName.isNotEmpty() -> typedName
                normalizedCategory != null -> normalizedCategory
                else -> getString(R.string.expense_default_name)
            }
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val updatedExpense = Expense(
                name,
                amount,
                selectedExpenseDate,
                normalizedCategory,
                accountValues[spinnerExpenseAccount.selectedItemPosition].id
            )
            if (expenseIndex != null && expenseIndex in expenses.indices) {
                expenses[expenseIndex] = updatedExpense
            } else {
                expenses.add(updatedExpense)
            }
            populateItems()
            saveData()
            updateTotals()
            refreshPagedContent()
            dialog.dismiss()
        }

        dialog.show()
        styleDialogWindow(dialog)
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
        return mutableListOf(getString(R.string.general_category)).apply {
            addAll(categories)
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

    private fun sanitizeSubscription(subscription: Subscription): Subscription {
        val safeName = runCatching { subscription.name }.getOrNull().orEmpty().ifBlank { getString(R.string.subscription) }
        val safePeriod = if (runCatching { subscription.period }.getOrNull() == "yearly") "yearly" else "monthly"
        val safeDate = runCatching { subscription.nextChargeDate }.getOrNull()
        return Subscription(
            name = safeName,
            amount = subscription.amount,
            period = safePeriod,
            nextChargeDate = safeDate,
            accountId = resolvePaymentAccountId(runCatching { subscription.accountId }.getOrNull(), runCatching { subscription.accountSource }.getOrNull()),
            accountSource = null
        )
    }

    private fun sanitizeExpense(expense: Expense): Expense {
        val safeName = runCatching { expense.name }.getOrNull().orEmpty().ifBlank { getString(R.string.expense_default_name) }
        val safeDate = runCatching { expense.date }.getOrNull().orEmpty()
        val safeCategory = runCatching { expense.category }.getOrNull()
        return Expense(
            name = safeName,
            amount = expense.amount,
            date = safeDate,
            category = safeCategory,
            accountId = resolvePaymentAccountId(runCatching { expense.accountId }.getOrNull(), runCatching { expense.accountSource }.getOrNull()),
            accountSource = null
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

    private fun getBaseBalanceForAccount(account: String?): Double {
        return paymentAccounts
            .filter { matchesAccountFilter(it.id, account ?: FILTER_ALL) }
            .sumOf { it.balance }
    }

    private fun buildEventDetail(primary: String, accountId: Long): String {
        return "$primary · ${getPaymentAccountName(accountId)}"
    }

    inner class StaticPagesAdapter(private val pages: List<View>) :
        RecyclerView.Adapter<StaticPagesAdapter.PageHolder>() {

        inner class PageHolder(val container: ViewGroup) : RecyclerView.ViewHolder(container)

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

    inner class ItemsPagerAdapter : RecyclerView.Adapter<ItemsPagerAdapter.PageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.page_filtered_items, parent, false)
            return PageViewHolder(view)
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            holder.bind(getFilteredItems(position))
        }

        override fun getItemCount() = 4

        inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val recyclerView: RecyclerView = view.findViewById(R.id.rvPageItems)
            private val emptyState: TextView = view.findViewById(R.id.tvEmptyState)

            init {
                recyclerView.layoutManager = LinearLayoutManager(view.context)
            }

            fun bind(pageItems: List<Item>) {
                recyclerView.adapter = ItemAdapter(
                    items = pageItems,
                    onItemLongClick = { item ->
                        when (item.type) {
                            ItemType.INCOME -> showIncomeDialog(item.sourceIndex)
                            ItemType.SUBSCRIPTION -> showEditSubscriptionDialog(item.sourceIndex)
                            ItemType.EXPENSE -> showExpenseDialog(item.sourceIndex)
                        }
                    },
                    amountFormatter = ::formatSignedAmount,
                    itemMarkerFormatter = ::getItemMarker
                )
                emptyState.visibility = if (pageItems.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

}
