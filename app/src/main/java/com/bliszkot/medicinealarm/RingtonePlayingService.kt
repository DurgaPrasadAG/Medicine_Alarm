package com.bliszkot.medicinealarm

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class RingtonePlayingService : Service() {
    private var mp: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resCode = intent?.getIntExtra("resCode", 0)

        val intent1 = Intent(this.applicationContext, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, resCode!!, intent1, PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "Notify1")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Alarm")
            .setContentText("Alarm is running")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val mNotificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm"
            val channel = NotificationChannel(
                channelId,
                "Alarm",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }

        val extras = intent.getStringExtra("extra")

        if (extras == "play") {
            mNotificationManager.notify(resCode, builder.build())
            mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
            mp?.start()
            Toast.makeText(this, "Alarm started.", Toast.LENGTH_SHORT).show()
        }

        if (extras == "stop") {
            mp?.stop()
            mp?.reset()
            Toast.makeText(this, "Alarm stopped.", Toast.LENGTH_SHORT).show()
        }

        return START_NOT_STICKY
    }
}