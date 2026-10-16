package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object FuturisticAudioSynth {
    private var ambientTrack: AudioTrack? = null
    private var ambientJob: Job? = null
    private var isMuted = true

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        if (isMuted) {
            stopAmbient()
        }
        return isMuted
    }

    fun isAudioMuted(): Boolean = isMuted

    fun playBlip(pitchHz: Float = 880f, durationMs: Int = 45) {
        if (isMuted) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 44100
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = 1.0 - (i.toDouble() / numSamples)
                    val sample = (sin(2.0 * Math.PI * pitchHz * time) * 16000 * envelope).toInt()
                    buffer[i] = sample.coerceIn(-32768, 32767).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                delay(durationMs.toLong() + 50)
                track.release()
            } catch (_: Exception) {}
        }
    }

    fun playCompletionChord() {
        if (isMuted) return
        CoroutineScope(Dispatchers.Default).launch {
            playBlip(523.25f, 60)
            delay(50)
            playBlip(659.25f, 60)
            delay(50)
            playBlip(783.99f, 60)
            delay(50)
            playBlip(1046.50f, 180)
        }
    }

    fun startAmbient(preset: String = "CYBER_CITY") {
        if (isMuted) return
        stopAmbient()
        ambientJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 22050
                val bufferSize = sampleRate
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                ambientTrack = track
                track.play()

                val baseFreq = when (preset) {
                    "CYBER_CITY" -> 110.0
                    "DRAGON_VFX" -> 146.8
                    "CYBORG_SCAN" -> 164.8
                    else -> 130.8
                }

                val buffer = ShortArray(1024)
                var phase1 = 0.0
                var phase2 = 0.0
                var lfoPhase = 0.0

                while (isActive && !isMuted) {
                    for (i in buffer.indices) {
                        lfoPhase += 0.5 * 2 * Math.PI / sampleRate
                        val lfo = 0.8 + 0.2 * sin(lfoPhase)
                        phase1 += baseFreq * 2 * Math.PI / sampleRate
                        phase2 += (baseFreq * 1.5) * 2 * Math.PI / sampleRate

                        val osc1 = sin(phase1)
                        val osc2 = sin(phase2) * 0.4
                        val sample = ((osc1 + osc2) * 5000 * lfo).toInt()
                        buffer[i] = sample.coerceIn(-32768, 32767).toShort()
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (_: Exception) {}
        }
    }

    fun stopAmbient() {
        ambientJob?.cancel()
        ambientJob = null
        try {
            ambientTrack?.stop()
            ambientTrack?.release()
        } catch (_: Exception) {}
        ambientTrack = null
    }
}
