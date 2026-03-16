package carnerero.agustin.cuentaappandroid.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import carnerero.agustin.cuentaappandroid.R
import carnerero.agustin.cuentaappandroid.data.db.entities.CategoryType
import carnerero.agustin.cuentaappandroid.domain.database.accountusecase.GetAllAccountsCheckedUseCase
import carnerero.agustin.cuentaappandroid.domain.database.categoryusecase.GetAllCategoriesCheckedUseCase
import carnerero.agustin.cuentaappandroid.domain.database.entriesusecase.GetSumOfExpensesByCategoryAndDateUseCase
import carnerero.agustin.cuentaappandroid.domain.database.entriesusecase.GetSumTotalExpensesByAccountUseCase
import carnerero.agustin.cuentaappandroid.domain.datastore.GetCurrencyCodeUseCase
import carnerero.agustin.cuentaappandroid.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
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
                val currencyCode=_uiState.value.currencyCode

                val message = Utils.numberFormat(expenses, currencyCode)
                val titleRes=when{
                    percentage > 0.8f && percentage < 1.0f ->R.string.expenseslimit80
                    percentage >= 1.0f ->R.string.expenseslimit
                    else->R.string.expenselimitOkAccount
                }


                message.let {
                    _notificationEvent.emit(
                        NotificationEvent.CategoryNotification(
                            categoryId = category.id,
                            titleRes = titleRes,
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
                val currencyCode=_uiState.value.currencyCode
                val message =
                    "${account.name}: ${Utils.numberFormat(expenses, currencyCode)}"

                val titleRes=when{
                    percentage > 0.8f && percentage < 1.0f ->R.string.expenseslimit80
                    percentage >= 1.0f ->R.string.expenseslimit
                    else->R.string.expenselimitOkAccount
                }

                message.let {
                    _notificationEvent.emit(
                        NotificationEvent.AccountNotification(
                            accountId = account.id,
                            titleRes = titleRes,
                            message = it,
                            iconRes = R.drawable.importoption
                        )
                    )
                }
            }
        }
    }
}

