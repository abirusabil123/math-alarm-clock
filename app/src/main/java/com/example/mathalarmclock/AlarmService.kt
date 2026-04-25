package com.example.mathalarmclock

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
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

const val CHANNEL_ID = "alarm_channel"
const val NOTIFICATION_ID = 1

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private lateinit var audioManager: AudioManager
    private var focusRequest: AudioFocusRequest? = null

    private var hour: Int = 0
    private var minute: Int = 0
    private var repeatDays: IntArray = intArrayOf()

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK, AudioManager.AUDIOFOCUS_LOSS -> {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setBypassDnd(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            enableLights(true)
            enableVibration(true)
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun createNotification(): android.app.Notification {
        val intent = Intent(this, MathActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("repeatDays", repeatDays)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert).setContentTitle("Math Alarm")
            .setContentText("Alarm is ringing...").setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM).setContentIntent(pendingIntent)
            .setOngoing(true).setFullScreenIntent(pendingIntent, true).build()
    }

    private fun startAlarm() {
        try {
            val playbackAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(playbackAttributes).setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener).build()

            val result = audioManager.requestAudioFocus(focusRequest!!)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    this@AlarmService, "android.resource://${packageName}/${R.raw.alarm}".toUri()
                )
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
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            AlarmState.isPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        hour = intent?.getIntExtra("hour", 0) ?: 0
        minute = intent?.getIntExtra("minute", 0) ?: 0
        repeatDays = intent?.getIntArrayExtra("repeatDays") ?: intArrayOf()

        startForeground(NOTIFICATION_ID, createNotification())
        startAlarm()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }
}