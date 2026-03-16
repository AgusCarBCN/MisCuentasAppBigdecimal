package carnerero.agustin.cuentaappandroid.notification

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