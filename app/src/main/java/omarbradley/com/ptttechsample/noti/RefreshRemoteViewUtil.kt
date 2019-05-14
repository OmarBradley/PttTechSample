package omarbradley.com.ptttechsample.noti

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationManagerCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import omarbradley.com.ptttechsample.R
import java.util.concurrent.TimeUnit

const val TEST_NOTI_ID = 1

object RefreshRemoteViewUtil {

    var remoteView: RemoteViews? = null

    @SuppressLint("CheckResult")
    fun refresh(context: Context, notification: Notification) {
        Observable.interval(1000L, TimeUnit.MILLISECONDS)
            .timeInterval()
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.value() }
            .subscribe({ count ->
                remoteView?.setTextViewText(R.id.textView_message, count.toString())
                with(NotificationManagerCompat.from(context)) {
                    notify(count.toInt(), notification)
                }
            }, {
                remoteView?.setTextViewText(R.id.textView_message, "error")
            })
    }

}
