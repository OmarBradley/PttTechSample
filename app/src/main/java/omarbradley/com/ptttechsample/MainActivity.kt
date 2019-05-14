package omarbradley.com.ptttechsample

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1.setOnClickListener {
            startActivity(Intent(this, AlarmControlActivity::class.java))
        }

        button2.setOnClickListener {
            startActivity(Intent(this, VolumeControlActivity::class.java))
        }

        button3.setOnClickListener {
            startActivity(Intent(this, BluetoothActivity::class.java))
        }

    }

}
