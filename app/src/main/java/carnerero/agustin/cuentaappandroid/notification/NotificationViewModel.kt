package carnerero.agustin.cuentaappandroid.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import carnerero.agustin.cuentaappandroid.R
import carnerero.agustin.cuentaappandroid.data.db.entities.Account
import carnerero.agustin.cuentaappandroid.data.db.entities.Category
import carnerero.agustin.cuentaappandroid.data.db.entities.CategoryType
import carnerero.agustin.cuentaappandroid.domain.database.accountusecase.GetAllAccountsCheckedUseCase
import carnerero.agustin.cuentaappandroid.domain.database.categoryusecase.GetAllCategoriesCheckedUseCase
import carnerero.agustin.cuentaappandroid.domain.database.entriesusecase.GetSumOfExpensesByCategoryAndDateUseCase
import carnerero.agustin.cuentaappandroid.domain.database.entriesusecase.GetSumTotalExpensesByAccountUseCase
import carnerero.agustin.cuentaappandroid.domain.datastore.GetCurrencyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.exp
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getSumExpensesByCategory: GetSumOfExpensesByCategoryAndDateUseCase,
    private val getSumExpensesByAccount: GetSumTotalExpensesByAccountUseCase,
    private val getAllCategoriesChecked: GetAllCategoriesCheckedUseCase,
    private val getAccountsChecked: GetAllAccountsCheckedUseCase,
    private val getCurrencyCode: GetCurrencyCodeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState

    // SharedFlow de eventos de notificación
    private val _notificationEvent = MutableSharedFlow<NotificationEvent>()
    val notificationEvent: SharedFlow<NotificationEvent> = _notificationEvent

    init {
        observeInitialData()
    }

    private fun observeInitialData() {
        viewModelScope.launch {
            combine(
                getCurrencyCode(),
                getAllCategoriesChecked(CategoryType.EXPENSE),
                getAccountsChecked()
            ) { currencyCode, categoriesChecked, accountsChecked ->
                _uiState.value.copy(
                    currencyCode = currencyCode,
                    categoriesChecked = categoriesChecked,
                    accountsChecked = accountsChecked
                )
            }.collect { newState ->
                _uiState.value = newState
                // Cada vez que llegue data nueva, revisamos notificaciones
                checkCategoryNotifications()
                checkAccountNotifications()
            }
        }
    }

    private fun calculatePercentage(expenses: BigDecimal, limit: BigDecimal): Float {
        if (limit == BigDecimal.ZERO) return 0f
        return (expenses.abs() / limit.abs()).toFloat().coerceIn(0f, 1f)
    }

    fun sumOfExpensesByAccount(accountId: Int, fromDate: String, toDate: String): Flow<BigDecimal> =
        getSumExpensesByAccount(accountId, fromDate, toDate).map { it ?: BigDecimal.ZERO }

    fun sumOfExpensesByCategories(categoryId: Int, fromDate: String, toDate: String): Flow<BigDecimal> =
        getSumExpensesByCategory(categoryId, fromDate, toDate).map { it ?: BigDecimal.ZERO }

    // -------------------- NOTIFICACIONES --------------------

    private fun checkCategoryNotifications() {
        viewModelScope.launch {
            val categories = _uiState.value.categoriesChecked
            categories.forEach { category ->
                val expenses = sumOfExpensesByCategories(category.id, category.fromDate, category.toDate).first()
                val percentage = calculatePercentage(expenses, category.spendingLimit)

                val message = when {
                    percentage > 0.8f && percentage < 1.0f ->
                        "${R.string.expenseslimit80}\nGasto: ${expenses}"
                    percentage >= 1.0f ->
                        "${R.string.expenseslimit}\nGasto: ${expenses}"
                    else -> null
                }

                message?.let {
                    _notificationEvent.emit(
                        NotificationEvent.CategoryNotification(
                            categoryId = category.id,
                            titleRes = R.string.categoryespendingcontrol,
                            message = it,
                            iconRes = category.iconResource
                        )
                    )
                }
            }
        }
    }

    private fun checkAccountNotifications() {
        viewModelScope.launch {
            val accounts = _uiState.value.accountsChecked
            accounts.forEach { account ->
                val expenses = sumOfExpensesByAccount(account.id, account.fromDate, account.toDate).first()
                val percentage = calculatePercentage(expenses, account.spendingLimit)

                val message = when {
                    percentage > 0.8f && percentage < 1.0f ->
                        "${R.string.expenseslimit80}\nGasto: ${expenses}"
                    percentage >= 1.0f ->
                        "${R.string.expenseslimit}\nGasto: ${expenses}"
                    else -> null
                }

                message?.let {
                    _notificationEvent.emit(
                        NotificationEvent.AccountNotification(
                            accountId = account.id,
                            titleRes = R.string.accountespendingcontrol,
                            message = it,
                            iconRes = R.drawable.importoption
                        )
                    )
                }
            }
        }
    }
}

// -------------------- CLASES DE EVENTOS --------------------

sealed class NotificationEvent {
    data class CategoryNotification(
        val categoryId: Int,
        val titleRes: Int,
        val message: String,
        val iconRes: Int
    ) : NotificationEvent()

    data class AccountNotification(
        val accountId: Int,
        val titleRes: Int,
        val message: String,
        val iconRes: Int
    ) : NotificationEvent()
}
/*
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getSumExpensesByCategory: GetSumOfExpensesByCategoryAndDateUseCase,
    private val getSumExpensesByAccount: GetSumTotalExpensesByAccountUseCase,
    private val getAllCategoriesChecked: GetAllCategoriesCheckedUseCase,
    private val getAccountsChecked: GetAllAccountsCheckedUseCase,
    private val getCurrencyCode: GetCurrencyCodeUseCase,
    ): ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState

    init {
        observeInitialData()
    }

    private fun observeInitialData() {
        viewModelScope.launch {
            combine(
                getCurrencyCode(),
                getAllCategoriesChecked(CategoryType.EXPENSE),
                getAccountsChecked()

            ) { currencyCode,categoriesChecked,accountsChecked ->
                _uiState.value.copy(
                   currencyCode=currencyCode,
                   categoriesChecked = categoriesChecked,
                   accountsChecked = accountsChecked
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun updateExpenseAccountsPercentages() {
        viewModelScope.launch {

            val accounts = uiState.value.accountsChecked

            if (accounts.isEmpty()) {
                _uiState.update {
                    it.copy(expensePercentageByAccount = emptyMap())
                }
                return@launch
            }

            val percentageMap = accounts.associateWith { account ->

                val expenses = sumOfExpensesByAccount(
                    account.id,
                    account.fromDate,
                    account.toDate
                ).first()

                calculatePercentage(expenses, account.spendingLimit)
            }

            _uiState.update {
                it.copy(expensePercentageByAccount = percentageMap)
            }
        }
    }
    fun updateExpenseCategoriesPercentages() {
        viewModelScope.launch {

            val categories = uiState.value.categoriesChecked

            if (categories.isEmpty()) {
                _uiState.update {
                    it.copy(expensePercentageByCategory = emptyMap())
                }
                return@launch
            }

            val percentageMap = categories.associateWith { category ->

                val expenses = sumOfExpensesByCategories(
                    category.id,
                    category.fromDate,
                    category.toDate
                ).first()

                calculatePercentage(expenses, category.spendingLimit)
            }

            _uiState.update {
                it.copy(expensePercentageByCategory = percentageMap)
            }
        }
    }

    private fun calculatePercentage(
        expenses: BigDecimal,
        limit: BigDecimal
    ): Float {
        if (limit == BigDecimal.ZERO) return 0f
        return (expenses.abs() / limit.abs())
            .toFloat()
            .coerceIn(0f, 1f)
    }


    fun sumOfExpensesByAccount(
        accountId: Int,
        fromDate: String,
        toDate: String
    ): Flow<BigDecimal> =
        getSumExpensesByAccount.invoke(accountId, fromDate, toDate)
            .map { it ?: BigDecimal.ZERO }

    fun sumOfExpensesByCategories(
        categoryId: Int,
        fromDate: String,
        toDate: String
    ): Flow<BigDecimal> =
        getSumExpensesByCategory.invoke(categoryId, fromDate, toDate)
            .map { it ?: BigDecimal.ZERO }

}*/