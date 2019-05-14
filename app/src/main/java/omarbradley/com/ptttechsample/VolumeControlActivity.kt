package omarbradley.com.ptttechsample

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_volume_control.*

class VolumeControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume_control)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean = with(event.action) {
        Log.e("event.keyCode", event.keyCode.toString())
        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (this == KeyEvent.ACTION_DOWN) {
                    text.text = "볼륨 업"
                }
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (this == KeyEvent.ACTION_DOWN) {
                    text.text = "볼륨 다운"
                }
                true
            }
            KeyEvent.KEYCODE_HEADSETHOOK -> {
                if (this == KeyEvent.ACTION_DOWN) {
                    when {
                        text.text == "일시 정지" -> text.text = "시작"
                        text.text == "시작" -> text.text = "일시 정지"
                        else -> text.text = "일시 정지"
                    }
                }
                true
            }
            else -> super.dispatchKeyEvent(event)
        }
    }

}
