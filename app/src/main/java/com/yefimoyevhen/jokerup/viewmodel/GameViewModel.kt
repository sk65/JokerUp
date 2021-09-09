package com.yefimoyevhen.jokerup.viewmodel

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yefimoyevhen.jokerup.util.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

class GameViewModel : ViewModel() {


    private val _isGameRestart = MutableLiveData(false)
    val isGameRestart: LiveData<Boolean> = _isGameRestart

    private val _isGameOver = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> = _isGameOver

    /**number of lives*/
    private val _lives = MutableLiveData("3")
    val lives: LiveData<String> = _lives

    /**number of score*/
    private val _score = MutableLiveData("0")
    val score: LiveData<String> = _score

    val livesCounter = AtomicInteger(3)

    fun gameOver() = _isGameOver.postValue(true)
    fun restart() = _isGameRestart.postValue(true)

    private val job = Job()

    fun stopGame() = job.cancel()


    fun startGame(context: Context, containers: Array<FrameLayout>) {
        //_isGameRestart.postValue(false)
        viewModelScope.launch(Dispatchers.Main + job) {

            while (isActive) {
                if (livesCounter.get() != 0) {
                    delay(1000L)
                    val icon = createIcon(context)
                    when (Tracks.values()[getRandomTrack()]) {
                        Tracks.TRACK_1 -> manageIcon(icon, this, containers[0])
                        Tracks.TRACK_2 -> manageIcon(icon, this, containers[1])
                        Tracks.TRACK_3 -> manageIcon(icon, this, containers[2])
                        Tracks.TRACK_4 -> manageIcon(icon, this, containers[3])
                    }
                } else {
                    stopGame()

                }
            }
        }
    }

    private suspend fun moveIcon(
        scope: CoroutineScope,
        container: FrameLayout,
        icon: ImageView,
        speed: Long
    ) {
        if (scope.isActive) {
            delay(speed)
            if (icon.y > getScreenHeight()) {
                if (icon.tag != BOMB_ICON_TAG) {
                    var value = lives.value!!.toInt()
                    _lives.postValue((--value).toString())
                }
                container.removeView(icon)
                scope.cancel()
            }
            icon.y += SHIFT
        }
    }

    private suspend fun manageIcon(icon: ImageView, scope: CoroutineScope, container: FrameLayout) {
        if (this.job.isActive) {
            val job = Job()
            container.addView(icon)
            if (icon.tag == BOMB_ICON_TAG) {
                icon.setOnClickListener {
                    container.removeView(icon)
                    job.cancel()
                    var value = lives.value!!.toInt()
                    _lives.postValue((--value).toString())
                }
            } else {
                icon.setOnClickListener {
                    container.removeView(icon)
                    var scoreValue = score.value!!.toInt()
                    _score.postValue((++scoreValue).toString())
                    job.cancel()
                }
            }
            scope.launch(job) {
                while (isActive) {
                    moveIcon(this, container, icon, 10)
                }
            }
        }
    }
}