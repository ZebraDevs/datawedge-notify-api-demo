package com.nilac.zebra.dwnotifydemoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.nilac.zebra.dwnotifydemoapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        sendBroadcast(DWUtil.generateDWProfileIntent(this))

        binding.notifyButton.setOnClickListener {
            notifyScanner()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceivers()
        sendBroadcast(DWUtil.registerForStatusChange(packageName, true))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mainReceiver)
        sendBroadcast(DWUtil.registerForStatusChange(packageName, false))
    }

    private fun registerReceivers() {
        val filter = IntentFilter()
        filter.addAction(AppConstants.NOTIFICATION_ACTION)
        registerReceiver(mainReceiver, filter, RECEIVER_EXPORTED)
    }

    //TEST
    private fun notifyScanner() {
        val i = Intent()
        val bundleNotify = Bundle()
        val bundleNotificationConfig = Bundle()

        i.setAction("com.symbol.datawedge.api.ACTION")

        bundleNotificationConfig.putString("DEVICE_IDENTIFIER", "BLUETOOTH_RS5100")
        bundleNotificationConfig.putIntArray("NOTIFICATION_SETTINGS", intArrayOf(17, 23, 8, 43))
        bundleNotify.putBundle("NOTIFICATION_CONFIG", bundleNotificationConfig)

        i.putExtra("com.symbol.datawedge.api.notification.NOTIFY", bundleNotify)
        i.putExtra("SEND_RESULT", "true")
        sendBroadcast(i)
    }

    private val mainReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AppConstants.NOTIFICATION_ACTION) {
                val extras = intent.getBundleExtra(AppConstants.EXTRA_RESULT_NOTIFICATION)
                val scannerStatus = extras?.getString(AppConstants.EXTRA_RESULT_NOTIFICATION_STATUS)
                val scannerIdentifier =
                    extras?.getString(AppConstants.EXTRA_RESULT_NOTIFICATION_SCANNER_IDENTIFIER)

                binding.scannerIdentifier.text =
                    getString(R.string.scanner_identifier_in_use, scannerIdentifier)
                binding.scannerStatus.text =
                    getString(R.string.scanner_identifier_status, scannerStatus)

                Log.i(TAG, extras.toString())
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}