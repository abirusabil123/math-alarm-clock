package com.example.mathalarmclock

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private lateinit var audioManager: AudioManager

    // 1. Add this to track focus request for Android 8.0+
    private var focusRequest: AudioFocusRequest? = null

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("AlarmService", "onCreate called")
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        startForeground(1, createNotification())
        startAlarm()
    }

    @SuppressLint("FullScreenIntentPolicy")
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
            .setFullScreenIntent(pendingIntent, true).build()
    }

    private fun startAlarm() {
        try {
            // 2. Updated Audio Focus Logic
            val playbackAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(playbackAttributes).setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { /* Handle focus changes if needed */ }.build()

            val result = audioManager.requestAudioFocus(focusRequest!!)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    this@AlarmService, "android.resource://${packageName}/${R.raw.alarm}".toUri()
                )

                // FIX: Use setAudioAttributes instead of setAudioStreamType
                setAudioAttributes(playbackAttributes)

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

            vibrator?.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 1000, 1000), intArrayOf(0, 255, 0), 0
                )
            )

            AlarmState.isPlaying = true
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

            // 3. Updated Abandon Focus Logic
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }

            AlarmState.isPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Use the modern flag-based method
        stopForeground(STOP_FOREGROUND_REMOVE)

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