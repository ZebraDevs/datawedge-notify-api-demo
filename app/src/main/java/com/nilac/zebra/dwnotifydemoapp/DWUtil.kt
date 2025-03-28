package com.nilac.zebra.dwnotifydemoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

object DWUtil {

    private const val TAG = "DWUtil"

    fun generateDWProfileIntent(context: Context): Intent {
        Log.i(TAG, "Creating DW Profile unless it doesn't exists already")

        val bMain = Bundle().apply {
            putString("PROFILE_NAME", "NotifyDemoApp")
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
        }

        val configApplicationList = Bundle().apply {
            putString("PACKAGE_NAME", context.packageName)
            putStringArray(
                "ACTIVITY_LIST",
                arrayOf(
                    "com.nilac.zebra.dwnotifydemoapp.MainActivity"
                )
            )
        }

        val intentModuleParamList = Bundle().apply {
            putString("intent_output_enabled", "true")
            putString("intent_action", AppConstants.DW_SCANNER_INTENT_ACTION)
            putInt("intent_delivery", 2)
        }

        val intentModule = Bundle().apply {
            putString("PLUGIN_NAME", "INTENT")
            putString("RESET_CONFIG", "true")
            putBundle("PARAM_LIST", intentModuleParamList)
        }

        val keystrokeModuleParamList = Bundle().apply {
            putString("keystroke_output_enabled", "false")
        }

        val keystrokeModule = Bundle().apply {
            putString("PLUGIN_NAME", "KEYSTROKE")
            putString("RESET_CONFIG", "true")
            putBundle("PARAM_LIST", keystrokeModuleParamList)
        }

        bMain.putParcelableArrayList(
            "PLUGIN_CONFIG",
            arrayListOf(intentModule, keystrokeModule)
        )
        bMain.putParcelableArray("APP_LIST", arrayOf(configApplicationList))

        return Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            setPackage("com.symbol.datawedge")
            putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain)
        }
    }

    fun registerForStatusChange(packageName: String, shouldRegister: Boolean): Intent {
        val mBundle = Bundle().apply {
            putString(AppConstants.EXTRA_KEY_APPLICATION_NAME, packageName)
            putString(
                AppConstants.EXTRA_KEY_NOTIFICATION_TYPE,
                AppConstants.EXTRA_KEY_VALUE_SCANNER_STATUS
            )
        }

        val dwIntent = Intent().apply {
            action = AppConstants.DW_ACTION
            putExtra(
                if (shouldRegister)
                    AppConstants.EXTRA_REGISTER_NOTIFICATION else AppConstants.EXTRA_UNREGISTER_NOTIFICATION,
                mBundle
            )
        }

        return dwIntent
    }

    fun resetRedScannerLedStatus(scannerIdentifier: String): Intent {
        return notifyScanner(scannerIdentifier, intArrayOf(48))
    }

    fun resetGreenScannerLedStatus(scannerIdentifier: String): Intent {
        return notifyScanner(scannerIdentifier, intArrayOf(42))
    }

    fun notifyScanner(scannerIdentifier: String, selectedSettings: IntArray): Intent {
        val i = Intent()
        val bundleNotify = Bundle()
        val bundleNotificationConfig = Bundle()

        i.setAction(AppConstants.DW_ACTION)

        bundleNotificationConfig.putString(
            AppConstants.EXTRA_NOTIFY_SCANNER_IDENTIFIER,
            scannerIdentifier
        )
        bundleNotificationConfig.putIntArray(
            AppConstants.EXTRA_NOTIFY_NOTIFICATION_SETTINGS,
            selectedSettings
        )
        bundleNotify.putBundle(
            AppConstants.EXTRA_NOTIFY_NOTIFICATION_CONFIG,
            bundleNotificationConfig
        )

        i.putExtra(AppConstants.DW_NOTIFICATION_NOTIFY, bundleNotify)
        return i
    }
}