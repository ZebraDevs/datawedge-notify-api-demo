package com.nilac.zebra.dwnotifydemoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.nilac.zebra.dwnotifydemoapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mScannerIdentifier = ""
    private var mSelectedSettings = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        sendBroadcast(DWUtil.generateDWProfileIntent(this))

        binding.notifyButton.setOnClickListener {
            sendBroadcast(DWUtil.notifyScanner(mScannerIdentifier, mSelectedSettings.toIntArray()))
        }

        binding.resetButton.setOnClickListener {
            sendBroadcast(DWUtil.resetRedScannerLedStatus(mScannerIdentifier))
            sendBroadcast(DWUtil.resetGreenScannerLedStatus(mScannerIdentifier))

            mSelectedSettings.clear()
            binding.availableLedModes.setText("")
            binding.beepModeOne.setText("")
            binding.beepModeTwo.setText("")
            binding.beepModeThree.setText("")
            binding.beepModeFour.setText("")
        }

        prepareUI()
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

    private fun prepareUI() {
        val ledModesCodes = resources.getIntArray(R.array.led_modes_codes)
        val beepsModesCodes = resources.getIntArray(R.array.beeps_modes_codes)

        val ledModesAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.led_modes)
        )
        binding.availableLedModes.setAdapter(ledModesAdapter)
        binding.availableLedModes.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedLedMode = ledModesCodes[position]
                mSelectedSettings.replaceOrAdd(0, selectedLedMode)
            }

        val beepModesAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.beeps_modes)
        )

        binding.beepModeOne.setAdapter(beepModesAdapter)
        binding.beepModeOne.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedBeepMode = beepsModesCodes[position]
                mSelectedSettings.replaceOrAdd(1, selectedBeepMode)
            }

        binding.beepModeTwo.setAdapter(beepModesAdapter)
        binding.beepModeTwo.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedBeepMode = beepsModesCodes[position]
                mSelectedSettings.replaceOrAdd(2, selectedBeepMode)
            }

        binding.beepModeThree.setAdapter(beepModesAdapter)
        binding.beepModeThree.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedBeepMode = beepsModesCodes[position]
                mSelectedSettings.replaceOrAdd(3, selectedBeepMode)
            }

        binding.beepModeFour.setAdapter(beepModesAdapter)
        binding.beepModeFour.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedBeepMode = beepsModesCodes[position]
                mSelectedSettings.replaceOrAdd(4, selectedBeepMode)
            }
    }

    private fun registerReceivers() {
        val filter = IntentFilter()
        filter.addAction(AppConstants.NOTIFICATION_ACTION)
        registerReceiver(mainReceiver, filter, RECEIVER_EXPORTED)
    }

    private val mainReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AppConstants.NOTIFICATION_ACTION) {
                val extras = intent.getBundleExtra(AppConstants.EXTRA_RESULT_NOTIFICATION)
                val scannerStatus = extras?.getString(AppConstants.EXTRA_RESULT_NOTIFICATION_STATUS)
                mScannerIdentifier =
                    extras?.getString(AppConstants.EXTRA_RESULT_NOTIFICATION_SCANNER_IDENTIFIER)
                        ?: ""

                binding.scannerIdentifier.text =
                    getString(R.string.scanner_identifier_in_use, mScannerIdentifier)
                binding.scannerStatus.text =
                    getString(R.string.scanner_identifier_status, scannerStatus)

                Log.i(TAG, extras.toString())
            }
        }
    }

    private fun <T> MutableList<T>.replaceOrAdd(index: Int, element: T) {
        if (index in indices) {
            this[index] = element
        } else {
            this.add(element)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}