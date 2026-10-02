package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AlertType(val title: String, val description: String) {
    BLOCK_BEGINS("Inicio de Bloque", "El bloque de estudio ha comenzado"),
    BLOCK_ENDS("Fin de Bloque", "Tiempo de estudio terminado"),
    BREAK_BEGINS("Inicio de Descanso", "Momento de relajarse"),
    BREAK_ENDS("Fin de Descanso", "Hora de volver al enfoque"),
    SESSION_COMPLETE("Sesión Completada", "¡Todos los bloques terminados!")
}

class NotificationAlertManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun triggerAlert(type: AlertType, soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSound(type)
        }
        if (vibrationEnabled) {
            playVibration(type)
        }
    }

    private fun playSound(type: AlertType) {
        scope.launch {
            try {
                // Play system default notification sound
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone: Ringtone? = RingtoneManager.getRingtone(context, uri)
                ringtone?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        it.audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    }
                    it.play()
                }

                // Harmonize with distinct synthesized tone for clear audible cue
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                when (type) {
                    AlertType.BLOCK_BEGINS -> {
                        // Rising energetic prompt
                        toneGen.startTone(ToneGenerator.TONE_PROP_PROMPT, 250)
                    }
                    AlertType.BLOCK_ENDS -> {
                        // Concluding alert double beep
                        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
                    }
                    AlertType.BREAK_BEGINS -> {
                        // Gentle break chime
                        toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                    }
                    AlertType.BREAK_ENDS -> {
                        // Attention call
                        toneGen.startTone(ToneGenerator.TONE_PROP_PROMPT, 300)
                    }
                    AlertType.SESSION_COMPLETE -> {
                        // Celebratory 3-step chime sequence
                        toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 180)
                        delay(200)
                        toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 180)
                        delay(200)
                        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 450)
                    }
                }
                delay(650)
                toneGen.release()
            } catch (_: Exception) {
                // Ignore audio failure safely
            }
        }
    }

    private fun playVibration(type: AlertType) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (!vibrator.hasVibrator()) return

            val timings: LongArray
            val amplitudes: IntArray

            when (type) {
                AlertType.BLOCK_BEGINS -> {
                    // Quick energetic pulse: tap-tap
                    timings = longArrayOf(0, 100, 80, 220)
                    amplitudes = intArrayOf(0, 160, 0, 230)
                }
                AlertType.BLOCK_ENDS -> {
                    // Firm double alert: Bzzzz - pause - Bzzzz
                    timings = longArrayOf(0, 320, 160, 480)
                    amplitudes = intArrayOf(0, 210, 0, 255)
                }
                AlertType.BREAK_BEGINS -> {
                    // Calming gentle pulse
                    timings = longArrayOf(0, 220)
                    amplitudes = intArrayOf(0, 140)
                }
                AlertType.BREAK_ENDS -> {
                    // Wake-up double pulse
                    timings = longArrayOf(0, 140, 90, 280)
                    amplitudes = intArrayOf(0, 180, 0, 240)
                }
                AlertType.SESSION_COMPLETE -> {
                    // Victory vibration sequence: 3 triumphant pulses
                    timings = longArrayOf(0, 150, 100, 150, 100, 450)
                    amplitudes = intArrayOf(0, 200, 0, 220, 0, 255)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        } catch (_: Exception) {
            // Ignore vibration failure safely
        }
    }
}
