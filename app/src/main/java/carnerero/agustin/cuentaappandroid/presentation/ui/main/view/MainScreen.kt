package carnerero.agustin.cuentaappandroid.presentation.ui.main.view


import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import carnerero.agustin.cuentaappandroid.R
import carnerero.agustin.cuentaappandroid.admob.AdmobBanner
import carnerero.agustin.cuentaappandroid.notification.NotificationObserver
import carnerero.agustin.cuentaappandroid.notification.NotificationService
import carnerero.agustin.cuentaappandroid.notification.NotificationViewModel
import carnerero.agustin.cuentaappandroid.notification.RequestNotificationPermissionDialog
import carnerero.agustin.cuentaappandroid.presentation.common.sharedcomponents.ModelDialog
import carnerero.agustin.cuentaappandroid.presentation.ui.main.menu.components.BottomMyAccountsBar
import carnerero.agustin.cuentaappandroid.presentation.navigation.MainNavHost
import carnerero.agustin.cuentaappandroid.presentation.theme.AppTheme.colors
import carnerero.agustin.cuentaappandroid.presentation.theme.AppTheme.orientation
import carnerero.agustin.cuentaappandroid.presentation.ui.main.menu.components.DrawerMyAccountsContent
import carnerero.agustin.cuentaappandroid.presentation.ui.main.menu.components.TopMyAccountsBar
import carnerero.agustin.cuentaappandroid.utils.navigateTopLevel
import com.kapps.differentscreensizesyt.ui.theme.OrientationApp


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel

) {
    val state by mainViewModel.uiState.collectAsStateWithLifecycle()

    val innerNavController = rememberNavController()
    // Observa la entrada actual del back stack (la pantalla activa) como un estado observable
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()

    val context = LocalContext.current
    // Verifica si el contexto es una actividad
    val activity = context as? Activity
    val notificationService = NotificationService(context)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val notificationViewModel: NotificationViewModel= hiltViewModel()
    if (state.isGranted) {
        NotificationObserver(notificationViewModel,notificationService)
    }

    val isPortrait=orientation== OrientationApp.Portrait
    // Usar LaunchedEffect para cerrar el drawer cuando cambia la pantalla seleccionada
    //y actualizar titulo
    LaunchedEffect(key1 = navBackStackEntry,isPortrait) {
        val currentRoute = navBackStackEntry?.destination?.route
        if (drawerState.isOpen) {
            drawerState.close() // Cierra el drawer cuando se selecciona una opción
        }
        mainViewModel.onTitleChange(currentRoute?:"")
    }

    LaunchedEffect(Unit) {
        mainViewModel.effect.collect { effect ->
            when (effect) {
                is MainEffects.NavToScreen -> innerNavController.navigateTopLevel(effect.route)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState ,
        drawerContent = {
            if(isPortrait){
            DrawerMyAccountsContent(

                {mainViewModel.onUserEvent(MainUserEvents.OpenExitDialog)},
                onClickOption = {
                    mainViewModel.onUserEvent(
                        MainUserEvents.OnClickOption(it)
                    )
                }
            )}
        },
        scrimColor = Color.Transparent,
        content = {
            // Main content goes here
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                {
                    if(isPortrait){TopMyAccountsBar(
                        scope,drawerState,state.title
                    )}
                },
                {
                    if(isPortrait){
                    BottomMyAccountsBar { mainViewModel.onUserEvent(MainUserEvents.OnClickOption(it)) }
                    }
                },
                containerColor = colors.backgroundPrimary
            ) { innerPadding ->
                RequestNotificationPermissionDialog(state.isGranted)
                Column(
                    Modifier.padding(innerPadding)
                )
                 {
                    AdmobBanner()
                    MainNavHost(
                        innerNavController,
                        mainViewModel
                    )
                }
            }
        }
    )
    ModelDialog(R.string.exitapp,
        R.string.exitinfo,
        showDialog = state.showExitDialog,
        onConfirm = {
            activity?.finish()
        },
        onDismiss = {
            mainViewModel.onUserEvent(MainUserEvents.CloseExitDialog)
        })
}







