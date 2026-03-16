package carnerero.agustin.cuentaappandroid.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import carnerero.agustin.cuentaappandroid.presentation.common.sharedcomponents.NotificationDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(
    isGranted: Boolean
) {

    val permissionState =
        rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)

    if (!isGranted && permissionState.status.shouldShowRationale) {

        NotificationDialog(
            showDialog = true,
            onDismiss = {
                permissionState.launchPermissionRequest()
            },
            onConfirm = {
                permissionState.launchPermissionRequest()
            }
        )

    } else if (!isGranted) {

        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }

    }
}
/*
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(mainViewModel: MainViewModel) {
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
    // Actualizar el estado del permiso en el ViewModel
    LaunchedEffect(permissionState.status.isGranted) {
        //mainViewModel.updateNotificationPermission(permissionState.status.isGranted)
    }

    if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale) {
        // the permission denied once, so show the rationale dialog and request permission
        NotificationDialog(
            true,
            onDismiss = {
                permissionState.launchPermissionRequest()

            },
           onConfirm = {
               //mainViewModel.selectScreen(IconOptions.SETTINGS)
           })
    } else {
        // permission granted or forever denied
        LaunchedEffect(key1 = Unit, block = { permissionState.launchPermissionRequest() })

    }
}*/

