package com.absolute.launcher.settings

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absolute.launcher.TutorialActivity
import com.absolute.launcher.R
import com.absolute.launcher.extern.*
import com.absolute.launcher.intendedSettingsPause
import kotlinx.android.synthetic.main.fragment_settings_meta.*

/** The 'Meta' Tab associated Fragment in Settings */

class SettingsFragmentMeta : Fragment() {

    /** Lifecycle functions */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_meta, container, false)
    }

    override fun onStart() {

        if (getSavedTheme(context!!) == "custom") {
            fragment_settings_meta_container.setBackgroundColor(dominantColor)

            setButtonColor(fragment_settings_meta_select_launcher_btn, vibrantColor)
            setButtonColor(fragment_settings_meta_view_tutorial_btn, vibrantColor)
        }

        // Button onClicks

        fragment_settings_meta_select_launcher_btn.setOnClickListener {
            intendedSettingsPause = true
            // on newer sdk: choose launcher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val callHomeSettingIntent = Intent(Settings.ACTION_HOME_SETTINGS)
                startActivity(callHomeSettingIntent)
            }
            // on older sdk: manage app details
            else {
                AlertDialog.Builder(this.context!!)
                    .setTitle(getString(R.string.alert_cant_choose_launcher))
                    .setMessage(getString(R.string.alert_cant_choose_launcher_message))
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { _, _ ->
                            try {
                                openAppSettings(this.context!!.packageName, this.context!!)
                            } catch ( e : ActivityNotFoundException) {
                                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                                startActivity(intent)
                            }
                        })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show()
            }
        }

        fragment_settings_meta_view_tutorial_btn.setOnClickListener {
            intendedSettingsPause = true
            startActivity(Intent(this.context, TutorialActivity::class.java))
        }

        // prompting for settings-reset confirmation


        // Footer onClicks

        // rate app
        fragment_settings_meta_footer_play_icon.setOnClickListener {
            try {
                val rateIntent = rateIntentForUrl("market://details")
                intendedSettingsPause = true
                startActivity(rateIntent)
            } catch (e: ActivityNotFoundException) {
                val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
                intendedSettingsPause = true
                startActivity(rateIntent)
            }
        }



        super.onStart()
    }

    /** Extra functions */

    // Rate App
    //  Just copied code from https://stackoverflow.com/q/10816757/12787264
    //   that is how we write good software ^^

    private fun rateIntentForUrl(url: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(String.format("%s?id=%s", url, this.context!!.packageName))
        )
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = if (Build.VERSION.SDK_INT >= 21) {
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        }
        intent.addFlags(flags)
        return intent
    }
}