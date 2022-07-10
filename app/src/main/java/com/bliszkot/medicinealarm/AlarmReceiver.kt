package com.bliszkot.medicinealarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val extras = intent.extras?.getString("extra")
        val resCode = intent.extras?.getInt("resCode")
        Intent(context, RingtonePlayingService::class.java).apply {
            putExtra("extra", extras)
            putExtra("resCode", resCode)
            context?.startService(this)
        }
    }
}