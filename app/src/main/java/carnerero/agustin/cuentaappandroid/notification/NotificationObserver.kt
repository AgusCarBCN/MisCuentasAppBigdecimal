package carnerero.agustin.cuentaappandroid.notification


import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
