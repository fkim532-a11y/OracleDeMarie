package com.kim.oraclemarie.ui.prayer

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kim.oraclemarie.R
import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Prayer
import com.kim.oraclemarie.data.repository.PrayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PrayerMode { LIST, DETAIL, CHOICE, TIMER }
enum class Ambiance { EAU, OISEAUX, SILENCE }

// Silences de respiration autour de la méditation
private const val PAUSE_AVANT_MUSIQUE_MS = 4000L  // entre la fin de la prière et le fond sonore
private const val PAUSE_AVANT_CLOCHE_MS = 5000L   // entre la fin du minuteur et la cloche

// Fondus du fond sonore
private const val FONDU_OUVERTURE_MS = 3000L      // montée du volume au démarrage
private const val FONDU_FERMETURE_MS = 4000L      // descente du volume en fin de minuteur
private const val FONDU_PAS_MS = 50L              // finesse des paliers de volume

data class PrayerUiState(
    val prayers: List<Prayer> = emptyList(),
    val isLoading: Boolean = true,
    val selectedPrayer: Prayer? = null,
    val selectedPrayerIndex: Int = 1,  // position 1-based dans la liste (≠ prayer.id)
    val mode: PrayerMode = PrayerMode.LIST,
    val timerProgress: Float = 0f,
    val remainingSeconds: Int = 0,
    val timerRunning: Boolean = false,
    val timerFinished: Boolean = false,
    val isPlayingPrayer: Boolean = false,
    val selectedAmbiance: Ambiance = Ambiance.EAU,
    val choiceMinutes: Int = 5,
)

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrayerRepository(OracleDatabase.getInstance(application))
    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var pauseJob: Job? = null   // silences entre prière/musique et minuteur/cloche
    private var fadeJob: Job? = null    // fondu de volume du fond sonore
    private var prayerPlayer: MediaPlayer? = null
    private var ambiancePlayer: MediaPlayer? = null
    private var gongPlayer: MediaPlayer? = null

    init {
        loadPrayers()
    }

    private fun loadPrayers() {
        viewModelScope.launch {
            repository.getAllPrayers().collect { list ->
                _uiState.update { it.copy(prayers = list, isLoading = false) }
            }
        }
    }

    fun selectPrayer(prayer: Prayer) {
        stopAllAudio()
        val index = (_uiState.value.prayers.indexOf(prayer) + 1).coerceAtLeast(1)
        _uiState.update {
            it.copy(
                selectedPrayer = prayer,
                selectedPrayerIndex = index,
                mode = PrayerMode.DETAIL,
                choiceMinutes = prayer.durationMinutes
            )
        }
    }

    fun openChoice() {
        _uiState.update { it.copy(mode = PrayerMode.CHOICE) }
    }

    fun backFromChoice() {
        _uiState.update { it.copy(mode = PrayerMode.DETAIL) }
    }

    fun backToList() {
        stopTimer()
        stopAllAudio()
        _uiState.update { it.copy(mode = PrayerMode.LIST) }
    }

    fun backToDetail() {
        stopTimer()
        stopAllAudio()
        _uiState.update {
            it.copy(
                mode = PrayerMode.DETAIL,
                timerProgress = 0f,
                remainingSeconds = 0,
                timerFinished = false
            )
        }
    }

    fun setAmbiance(ambiance: Ambiance) {
        _uiState.update { it.copy(selectedAmbiance = ambiance) }
    }

    fun setChoiceMinutes(minutes: Int) {
        _uiState.update { it.copy(choiceMinutes = minutes.coerceIn(1, 60)) }
    }

    fun justListen() {
        val state = _uiState.value
        if (state.selectedPrayer == null) return
        _uiState.update { it.copy(mode = PrayerMode.DETAIL) }
        playPrayerAudio(state.selectedPrayerIndex, onComplete = null)
    }

    fun stopPrayerAudio() {
        prayerPlayer?.stop()
        prayerPlayer?.release()
        prayerPlayer = null
        _uiState.update { it.copy(isPlayingPrayer = false) }
    }

    fun launchMeditation() {
        val state = _uiState.value
        if (state.selectedPrayer == null) return
        val totalSec = state.choiceMinutes * 60
        val ambiance = state.selectedAmbiance
        _uiState.update {
            it.copy(
                mode = PrayerMode.TIMER,
                timerProgress = 0f,
                remainingSeconds = totalSec,
                timerFinished = false,
                timerRunning = false
            )
        }
        playPrayerAudio(state.selectedPrayerIndex) {
            pauseJob?.cancel()
            pauseJob = viewModelScope.launch {
                delay(PAUSE_AVANT_MUSIQUE_MS)
                if (ambiance != Ambiance.SILENCE) startAmbiance(ambiance)
                startTimer(totalSec)
            }
        }
    }

    private fun playPrayerAudio(prayerIndex: Int, onComplete: (() -> Unit)?) {
        val context = getApplication<Application>()
        val resId = prayerRawResId(prayerIndex)
        prayerPlayer?.release()
        prayerPlayer = null
        if (resId == 0) {
            onComplete?.invoke()
            return
        }
        val player = MediaPlayer.create(context, resId) ?: run {
            onComplete?.invoke()
            return
        }
        player.setOnCompletionListener {
            it.release()
            prayerPlayer = null
            _uiState.update { s -> s.copy(isPlayingPrayer = false) }
            onComplete?.invoke()
        }
        prayerPlayer = player
        player.start()
        _uiState.update { it.copy(isPlayingPrayer = true) }
    }

    private fun startAmbiance(ambiance: Ambiance) {
        val context = getApplication<Application>()
        val resId = when (ambiance) {
            Ambiance.EAU -> R.raw.bruit_eau
            Ambiance.OISEAUX -> R.raw.chant_oiseau
            Ambiance.SILENCE -> return
        }
        ambiancePlayer?.release()
        val player = MediaPlayer.create(context, resId) ?: return
        player.isLooping = true
        player.setVolume(0f, 0f)
        player.start()
        ambiancePlayer = player
        fadeJob?.cancel()
        fadeJob = viewModelScope.launch { fadeVolume(player, 0f, 1f, FONDU_OUVERTURE_MS) }
    }

    /** Fait varier le volume par paliers. Ignore un lecteur déjà libéré. */
    private suspend fun fadeVolume(player: MediaPlayer, from: Float, to: Float, durationMs: Long) {
        val steps = (durationMs / FONDU_PAS_MS).toInt().coerceAtLeast(1)
        for (i in 1..steps) {
            val volume = from + (to - from) * i / steps
            runCatching { player.setVolume(volume, volume) }
            delay(FONDU_PAS_MS)
        }
    }

    /** Fin naturelle du minuteur : descente progressive puis arrêt. */
    private suspend fun fadeOutAmbiance() {
        val player = ambiancePlayer ?: return
        fadeJob?.cancel()
        fadeJob = null
        fadeVolume(player, 1f, 0f, FONDU_FERMETURE_MS)
        stopAmbiance()
    }

    /** Arrêt immédiat, sans fondu (sortie d'écran). */
    private fun stopAmbiance() {
        fadeJob?.cancel()
        fadeJob = null
        ambiancePlayer?.stop()
        ambiancePlayer?.release()
        ambiancePlayer = null
    }

    private fun stopAllAudio() {
        pauseJob?.cancel()
        pauseJob = null
        stopPrayerAudio()
        stopAmbiance()
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        pauseJob?.cancel()
        pauseJob = null
        _uiState.update { it.copy(timerRunning = false) }
    }

    private fun startTimer(totalSeconds: Int) {
        timerJob?.cancel()
        _uiState.update { it.copy(timerRunning = true) }
        timerJob = viewModelScope.launch {
            var elapsed = 0
            while (elapsed < totalSeconds) {
                delay(1000L)
                elapsed++
                _uiState.update {
                    it.copy(
                        timerProgress = elapsed.toFloat() / totalSeconds,
                        remainingSeconds = (totalSeconds - elapsed).coerceAtLeast(0)
                    )
                }
            }
            fadeOutAmbiance()
            _uiState.update { it.copy(timerRunning = false, timerFinished = true, timerProgress = 1f) }
            delay(PAUSE_AVANT_CLOCHE_MS)
            triggerVibration()
            playGong()
        }
    }

    private fun playGong() {
        val context = getApplication<Application>()
        gongPlayer?.release()
        gongPlayer = MediaPlayer.create(context, R.raw.cloche)
        gongPlayer?.start()
        gongPlayer?.setOnCompletionListener { it.release(); gongPlayer = null }
    }

    @Suppress("DEPRECATION")
    private fun triggerVibration() {
        val context = getApplication<Application>()
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 400, 150, 400, 150, 800), -1)
            )
        } else {
            vibrator.vibrate(longArrayOf(0, 400, 150, 400, 150, 800), -1)
        }
    }

    private fun prayerRawResId(prayerIndex: Int): Int = when (prayerIndex) {
        1  -> R.raw.priere_01
        2  -> R.raw.priere_02
        3  -> R.raw.priere_03
        4  -> R.raw.priere_04
        5  -> R.raw.priere_05
        6  -> R.raw.priere_06
        7  -> R.raw.priere_07
        8  -> R.raw.priere_08
        9  -> R.raw.priere_09
        10 -> R.raw.priere_10
        11 -> R.raw.priere_11
        12 -> R.raw.priere_12
        13 -> R.raw.priere_13
        14 -> R.raw.priere_14
        else -> 0
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        stopAllAudio()
        gongPlayer?.release()
        gongPlayer = null
    }
}
