package omarbradley.com.ptttechsample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.reactivex.disposables.Disposable
import omarbradley.com.ptttechsample.noti.RefreshRemoteViewUtil
import omarbradley.com.ptttechsample.noti.TEST_NOTI_ID

class AlarmControlActivity : AppCompatActivity() {

    val CHANNEL_ID = "testing"

    var disposable: Disposable? = null

    var notification: Notification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_control)

        val pendingIntent = createPendingIntent()
        RefreshRemoteViewUtil.remoteView = RemoteViews(this.packageName, R.layout.remote_test_reactive_view)
        notification = createNotification(pendingIntent)
        createNotificationChannel()
        with(NotificationManagerCompat.from(this)) {
            notify(TEST_NOTI_ID, notification!!)
        }
    }

    override fun onResume() {
        super.onResume()
        RefreshRemoteViewUtil.refresh(this, notification!!)
    }

    private fun createNotification(pendingIntent: PendingIntent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }.setContent(RefreshRemoteViewUtil.remoteView)
            .setContentTitle("test")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent()
            .apply {
                setClass(this@AlarmControlActivity, MainActivity::class.java)
            }
        return PendingIntent.getActivities(
            this,
            0,
            listOf(intent).toTypedArray(),
            PendingIntent.FLAG_NO_CREATE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

}
