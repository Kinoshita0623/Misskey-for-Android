package org.panta.misskey_for_android_v2.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.panta.misskey_for_android_v2.R
import org.panta.misskey_for_android_v2.adapter.NoteViewHolder
import org.panta.misskey_for_android_v2.adapter.NotificationViewHolder
import org.panta.misskey_for_android_v2.constant.NotificationType
import org.panta.misskey_for_android_v2.interfaces.ErrorCallBackListener
import org.panta.misskey_for_android_v2.repository.NotificationRepository
import org.panta.misskey_for_android_v2.repository.SecretRepository
import org.panta.misskey_for_android_v2.storage.SharedPreferenceOperator
import org.panta.misskey_for_android_v2.usecase.NoteAdjustment
import org.panta.misskey_for_android_v2.usecase.PagingController
import org.panta.misskey_for_android_v2.view_data.NotificationViewData
import org.panta.misskey_for_android_v2.view_presenter.MainActivity
import java.lang.IllegalArgumentException
import java.util.*
import android.app.PendingIntent



class NotificationService : Service() {

    private lateinit var notificationRepository: NotificationRepository
    private lateinit var pagingController: PagingController<NotificationViewData>

    private val notificationChannelId = "Misskey for Adnroid Notification"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        //~init
        val conProperty = SecretRepository(SharedPreferenceOperator(applicationContext)).getConnectionInfo()
        if(conProperty == null){
            Log.d("NotificationService", "connectionInfo不明のため停止します")
            this.stopSelf()
            return
        }
        notificationRepository = NotificationRepository(conProperty.domain, conProperty.i)

        pagingController = PagingController(notificationRepository, object : ErrorCallBackListener{
            override fun callBack(e: Exception) {
                Log.w("NotificationService", "エラー発生", e)
            }
        })
        //init~

        pagingController.getInit { out ->
            val notReadNotifications = out.filter{ inner ->
                ! inner.notificationProperty.isRead
            }

            notReadNotifications.forEach{inner ->
                Log.d("NotificationService", "未読の通知 ${inner.notificationProperty}")
                showNotificationCompat(inner)
            }

            watchDogNotification(20000)
        }


    }

    private fun watchDogNotification(sleepMillSeconds: Long){
        if(sleepMillSeconds.toString().length < 4) throw IllegalArgumentException("watchDogNotificationsError　1000ミリ秒未満を指定することはできません。")
        GlobalScope.launch{
            while(true){
                pagingController.getNewItems {
                    val notReadNotifications = it.filter{ inner ->
                        ! inner.notificationProperty.isRead
                    }

                    notReadNotifications.forEach{inner ->
                        Log.d("NotificationService", "未読の通知 ${inner.notificationProperty}")
                        showNotificationCompat(inner)

                    }
                }
                delay(sleepMillSeconds)
            }
        }
    }

    private fun showNotificationCompat(notificationViewData: NotificationViewData){

        try{
            val type = NotificationType.getEnumFromString(notificationViewData.notificationProperty.type)
            val typeMessage = when(type){
                NotificationType.REACTION -> ""
                NotificationType.RENOTE -> "リノート"
                NotificationType.FOLLOW -> "フォローされました"
                NotificationType.MENTION -> "あなたについて投稿"
                NotificationType.QUOTE -> "引用リノート"
                NotificationType.REPLY -> "リプライ"
                NotificationType.RECEIVE_FOLLOW_REQUEST -> "フォローリクエスト"
                NotificationType.POLL_VOTE -> "投票"
            }
            val userName = notificationViewData.notificationProperty.user.name
            val title = "$userName さんが$typeMessage しました"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if(notificationManager.getNotificationChannel(notificationChannelId) == null){
                    val channel = NotificationChannel(notificationChannelId, "Misskey", NotificationManager.IMPORTANCE_HIGH)
                    channel.apply{
                        description = "詳細"
                    }
                    notificationManager.createNotificationChannel(channel)
                }
            }

            val notification = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Notification.Builder(applicationContext, notificationChannelId)
            }else{
                Notification.Builder(applicationContext)
            }

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(MainActivity.SHOW_FRAGMENT_TAG, MainActivity.NOTIFICATION)

            val contentIntent = PendingIntent.getActivity(applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)

            val build = notification.apply {
                setSmallIcon(org.panta.misskey_for_android_v2.R.drawable.misskey_icon)
                setContentTitle(title)
                setContentIntent(contentIntent)
            }.build()


            val nm = NotificationManagerCompat.from(this)
            nm.notify(1, build)
        }catch(e: Exception){
            Log.w("NotificationService", "通知表示中にエラー発生", e)
        }

    }
}
