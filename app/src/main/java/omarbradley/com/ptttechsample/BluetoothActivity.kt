package omarbradley.com.ptttechsample

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_volume_control.*

const val TAG = "BluetoothActivity"

class BluetoothActivity : AppCompatActivity() {

    protected var mBluetoothAdapter: BluetoothAdapter? = null
    protected var mBluetoothHeadset: BluetoothHeadset? = null
    protected var mConnectedHeadset: BluetoothDevice? = null
    protected var mAudioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (mAudioManager?.isBluetoothScoAvailableOffCall == true) {
                mBluetoothAdapter?.getProfileProxy(this, mHeadsetProfileListener, BluetoothProfile.HEADSET);
            }
        }
    }

    private val mHeadsetProfileListener = object : BluetoothProfile.ServiceListener {

        override fun onServiceDisconnected(profile: Int) {
            mBluetoothHeadset?.stopVoiceRecognition(mConnectedHeadset)
            unregisterReceiver(mHeadsetBroadcastReceiver)
            mBluetoothHeadset = null
        }

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            mBluetoothHeadset = proxy as BluetoothHeadset

            val devices = mBluetoothHeadset?.connectedDevices

            if (devices.isNullOrEmpty().not()) {
                mConnectedHeadset = devices?.get(0)
                val log = if (mBluetoothHeadset?.isAudioConnected(mConnectedHeadset) == true) {
                    "Profile listener audio already connected" //$NON-NLS-1$
                } else {
                    if (mBluetoothHeadset?.startVoiceRecognition(mConnectedHeadset) == true)
                        "Profile listener startVoiceRecognition returns true" //$NON-NLS-1$
                    else
                        "Profile listener startVoiceRecognition returns false" //$NON-NLS-1$
                }
                Log.d(TAG, log)
            }
            registerReceiver(mHeadsetBroadcastReceiver, IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
            registerReceiver(
                mHeadsetBroadcastReceiver,
                IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED).apply { priority = Integer.MAX_VALUE })
        }
    }

    val mHeadsetBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            var state = 1
            var previousState =
                intent.getIntExtra(BluetoothHeadset.EXTRA_PREVIOUS_STATE, BluetoothHeadset.STATE_DISCONNECTED)
            var log = ""
            text?.text = intent.action
            if (action == BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) {
                state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED)
                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    mConnectedHeadset = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    log = if (mBluetoothHeadset?.isAudioConnected(mConnectedHeadset) == true) {
                        "Headset connected audio already connected"
                    } else {
                        if (mBluetoothHeadset?.startVoiceRecognition(mConnectedHeadset) == true) {
                            "Headset connected startVoiceRecognition returns true"
                        } else {
                            "Headset connected startVoiceRecognition returns false"
                        }
                    }
                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    mConnectedHeadset = null
                }
            } else {
                state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED)
                mBluetoothHeadset?.stopVoiceRecognition(mConnectedHeadset)
                when (state) {
                    BluetoothHeadset.STATE_AUDIO_CONNECTED -> text?.text = "STATE_AUDIO_CONNECTED"
                    BluetoothHeadset.STATE_AUDIO_CONNECTING -> text?.text = "STATE_AUDIO_CONNECTING"
                    BluetoothHeadset.STATE_AUDIO_DISCONNECTED -> {
                        // The headset audio is disconnected, but calling
                        // stopVoiceRecognition always returns true here.
                        val returnValue = mBluetoothHeadset?.stopVoiceRecognition(mConnectedHeadset)
                        log = "Audio disconnected stopVoiceRecognition return $returnValue"
                    }
                }
            }
            log += "\nAction = $action\nState = $state previous state = $previousState"
            Log.d(TAG, log)
        }
    }
}
