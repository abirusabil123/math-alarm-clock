/* Copyright (c) 2025 Mohammad Sheraj */
package com.example.mathalarmclock

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    companion object {
        var isAlarmPlaying = false
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("AlarmService", "onCreate called")
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        startForeground(1, createNotification())
        startAlarm()
    }

    private fun createNotification(): android.app.Notification {
        val intent = Intent(this, MathActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "alarm_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert).setContentTitle("Math Alarm")
            .setContentText("Alarm is ringing...").setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).setOngoing(true)
            .setFullScreenIntent(pendingIntent, true)  // Add this
            .build()
    }

    private fun startAlarm() {
        try {
            // Request audio focus
            val result = audioManager.requestAudioFocus(
                null, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )

            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    this@AlarmService,
                    android.net.Uri.parse("android.resource://${packageName}/${R.raw.alarm}")
                )
                setAudioStreamType(AudioManager.STREAM_ALARM)
                setWakeMode(this@AlarmService, PowerManager.PARTIAL_WAKE_LOCK)
                isLooping = true
                prepare()

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    start()
                }
            }

            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION") getSystemService(VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 1000, 1000), intArrayOf(0, 255, 0), 0
                    )
                )
            } else {
                @Suppress("DEPRECATION") vibrator?.vibrate(longArrayOf(0, 1000, 1000), 0)
            }

            isAlarmPlaying = true  // Add this after starting
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAlarm() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            vibrator?.cancel()
            vibrator = null

            // Abandon audio focus
            audioManager.abandonAudioFocus(null)
            isAlarmPlaying = false  // Add this before stopping
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        android.util.Log.d("AlarmService", "onStartCommand called")
        return START_STICKY
    }

    override fun onDestroy() {
        android.util.Log.d("AlarmService", "onDestroy called")
        super.onDestroy()
        stopAlarm()
    }
}