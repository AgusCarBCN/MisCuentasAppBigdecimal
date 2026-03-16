package carnerero.agustin.cuentaappandroid.notification


import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import carnerero.agustin.cuentaappandroid.R
import carnerero.agustin.cuentaappandroid.utils.Utils
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun NotificationObserver(
    notificationViewModel: NotificationViewModel,
    notificationService: NotificationService
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        notificationViewModel.notificationEvent.collect { event ->

            when(event) {
                is NotificationEvent.CategoryNotification -> {
                    notificationService.showBasicNotification(
                        title = context.getString(event.titleRes),
                        message = event.message,
                        iconResource = event.iconRes
                    )
                }
                is NotificationEvent.AccountNotification -> {
                    notificationService.showBasicNotification(
                        title = context.getString(event.titleRes),
                        message = event.message,
                        iconResource = event.iconRes
                    )
                }
            }
        }
    }
}

/*
@Composable
fun NotificationCategoriesObserver(
    notificationViewModel: NotificationViewModel,
    notificationService: NotificationService
) {
    val uiState by notificationViewModel.uiState.collectAsStateWithLifecycle()
    // Update expense percentage
    notificationViewModel.updateExpenseCategoriesPercentages()

    // Track categories that have already received a notification
    val notifiedCategories = remember { mutableSetOf<Int>() }

    uiState.expensePercentageByCategory.forEach { (category, percentage) ->
        // State to track the expenses by category and whether it has been loaded
        var expensesByCategory by remember { mutableStateOf<BigDecimal?>(null) }

        // Load expenses for each category when the category ID changes
        LaunchedEffect(category.id) {
            expensesByCategory = notificationViewModel.sumOfExpensesByCategories(category.id,
                category.fromDate,
                category.toDate).first()
        }

        // Only proceed with notifications if expensesByCategory has been loaded
        expensesByCategory?.let { expenses ->
            // Notification logic remains outside LaunchedEffect to allow stringResource usage

            if (!notifiedCategories.contains(category.id)) {
                val message = when {
                    percentage > 0.8f && percentage < 1.0f -> {
                        "${stringResource(id = R.string.expenseslimit80)}\n" +
                                "${stringResource(id = R.string.expensebycategory)} ${Utils.numberFormat(expenses, uiState.currencyCode)}."
                    }
                    percentage >= 1.0f -> {
                        "${stringResource(id = R.string.expenseslimit)}\n" +
                                "${stringResource(id = R.string.expensebycategory)} ${Utils.numberFormat(expenses, uiState.currencyCode)}."
                    }
                    else -> {
                        "${stringResource(id = R.string.expenselimitOkCategory)} \n" +
                                "${stringResource(id = R.string.expensebycategory)} ${Utils.numberFormat(expenses, uiState.currencyCode)}."
                    }
                }

                val categoryControl = stringResource(id = R.string.categoryespendingcontrol) +" "+
                        stringResource(id = category.nameResource)


                // Send the notification
                notificationService.showBasicNotification(
                    title = categoryControl,
                    message = message,
                    category.iconResource
                )

                // Mark category as notified
                notifiedCategories.add(category.id)
            }
        }
    }
}

@Composable
fun NotificationAccountObserver(

    notificationViewModel: NotificationViewModel,
    notificationService: NotificationService
) {
    val uiState by notificationViewModel.uiState.collectAsStateWithLifecycle()
    // Update expense percentage
    notificationViewModel.updateExpenseAccountsPercentages()


    // Track categories that have already received a notification
    val notifiedAccounts = remember { mutableSetOf<Int>() }

    uiState.expensePercentageByCategory.forEach { (account, percentage) ->
        // State to track the expenses by category and whether it has been loaded
        var expensesByAccounts by remember { mutableStateOf<BigDecimal?>(null) }

        // Load expenses for each category when the category ID changes
        LaunchedEffect(account.id) {
            expensesByAccounts = notificationViewModel.sumOfExpensesByAccount(account.id,
                account.fromDate,
                account.toDate).first()

        }

        // Only proceed with notifications if expensesByCategory has been loaded
        expensesByAccounts?.let { expenses ->
            // Notification logic remains outside LaunchedEffect to allow stringResource usage

            if (!notifiedAccounts.contains(account.id)) {
                val message = when {
                    percentage > 0.8f && percentage < 1.0f -> {
                        "${stringResource(id = R.string.expenseslimit80)}\n" +
                                "${stringResource(id = R.string.expensebyaccount)} ${Utils.numberFormat(expenses, uiState.currencyCode)}."
                    }
                    percentage >= 1.0f -> {
                        "${stringResource(id = R.string.expenseslimit)}\n" +
                                "${stringResource(id = R.string.expensebyaccount)} ${Utils.numberFormat(expenses, uiState.currencyCode)}."
                    }
                    else -> {
                        "${stringResource(id = R.string.expenselimitOkAccount)} \n" +
                                "${stringResource(id = R.string.expensebyaccount)} ${Utils.numberFormat(expenses, uiState.currencyCode)}."
                    }
                }

                val accountControl = stringResource(id = R.string.accountespendingcontrol) +" "+
                        account.nameResource


                // Send the notification
                notificationService.showBasicNotification(
                    title = accountControl,
                    message = message,
                    R.drawable.importoption)


                // Mark category as notified
                notifiedAccounts.add(account.id)
            }
        }
    }
}
*/

