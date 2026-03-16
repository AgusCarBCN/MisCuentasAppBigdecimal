package carnerero.agustin.cuentaappandroid.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class NotificationService(
    private val context: Context
){

        private val notificationManager = context.getSystemService(NotificationManager::class.java)
        fun showBasicNotification(
            title: String,
            message: String,
            iconResource: Int
        ) {
            // Crea una notificación usando NotificationCompat.Builder y especifica el canal de notificación
            val notification = NotificationCompat.Builder(context, "NotificationChannel")
                .setContentTitle(title)                      // Establece el título de la notificación
                .setContentText(message)                      // Establece el texto del contenido de la notificación
                .setSmallIcon(iconResource)                   // Asigna el icono pequeño que se mostrará con la notificación
                .setPriority(NotificationManager.IMPORTANCE_HIGH)     // Define la prioridad como alta para alertas visibles y/o sonoras
                .setAutoCancel(true)                  // Hace que la notificación se cancele automáticamente cuando el usuario la toca
                .build()                                              // Construye la notificación

            // Muestra la notificación usando notificationManager.notify()
            notificationManager.notify(
                Random.nextInt(),                                     // Usa un ID único y aleatorio para cada notificación
                notification                                          // Pasa el objeto de notificación construido
            )
        }



}