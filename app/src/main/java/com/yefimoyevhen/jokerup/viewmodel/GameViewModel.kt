package com.yefimoyevhen.jokerup.viewmodel

import android.content.Context
import android.view.ViewManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yefimoyevhen.jokerup.R
import com.yefimoyevhen.jokerup.util.BOMB_ICON_TAG
import com.yefimoyevhen.jokerup.util.Tracks
import com.yefimoyevhen.jokerup.util.getRandomTrack
import com.yefimoyevhen.jokerup.util.getScreenHeight
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class GameViewModel
@Inject
constructor(
    @ApplicationContext val context: Context
) : ViewModel() {
    private var isPause = false


    private val _isGameRestart = MutableLiveData(false)
    val isGameRestart: LiveData<Boolean> = _isGameRestart

    private val _isGameOver = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> = _isGameOver

    /***/
    private val _isDialogShouldBeShow = MutableLiveData(false)
    val isDialogShouldBeShow: LiveData<Boolean> = _isDialogShouldBeShow

    /**number of lives*/
    private val _lives = MutableLiveData("3")
    val lives: LiveData<String> = _lives

    /**number of score*/
    private val _score = MutableLiveData("0")
    val score: LiveData<String> = _score

    private val icons: MutableSet<ImageView> = HashSet()

    private var livesCounter = AtomicInteger()
    private var scoresCounter = AtomicInteger()

    private val imagesResource = arrayOf(
        R.drawable.ic_bomb,
        R.drawable.ic_joker1,
        R.drawable.ic_joker2,
        R.drawable.ic_joker3,
        R.drawable.ic_joker4
    )

    private lateinit var mainLoopJob: CompletableJob

    private lateinit var containers: Array<FrameLayout>

    private fun refreshValues() {
        mainLoopJob = Job()
        livesCounter = AtomicInteger(3)
        scoresCounter = AtomicInteger(0)
        _lives.postValue(livesCounter.get().toString())
        _score.postValue(scoresCounter.get().toString())
        _isGameRestart.postValue(false)
        _isDialogShouldBeShow.postValue(false)
    }

    fun startGame(containers: Array<FrameLayout>) {
        refreshValues()
        this.containers = containers
        startGameLoop()
    }

    /**
     * Starts game loop depends on random number stars one of five
     * script for displaying falling icons.
     * */
    private fun startGameLoop() {
        CoroutineScope(mainLoopJob).launch() {
            while (isActive) {
                if (!isPause) {
                    delay((700L..2000L).random())
                    when ((0..4).random()) {
                        0 -> startFirstCase(this)
                        1 -> startSecondCase(this)
                        2 -> startThirdCase(this)
                        3 -> startFourthCase(this)
                        4 -> startFifthCase(this)
                    }
                }
            }
        }
    }

    /**
     * In this scenario, the icons fall diagonally.
     * Depending on the randomness, they can change the order (from left to right or right to left)
     * */
    private suspend fun startFifthCase(scope: CoroutineScope) {
        val random = (100L..500L).random()
        when ((0..1).random()) {
            0 -> {
                containers.forEach { container ->
                    delay(random)
                    manageTrack(scope, createIcon(), container)
                }
            }
            1 -> {
                containers.reversed().forEach { container ->
                    delay(random)
                    manageTrack(scope, createIcon(), container)
                }
            }
        }
    }

    /**In this scenario, two icons fall from left.*/
    private suspend fun startThirdCase(scope: CoroutineScope) {
        manageTrack(scope, createIcon(), containers[0])
        manageTrack(scope, createIcon(), containers[1])
    }

    /**In this scenario, two icons fall from right.*/
    private suspend fun startFourthCase(scope: CoroutineScope) {
        manageTrack(scope, createIcon(), containers[2])
        manageTrack(scope, createIcon(), containers[3])
    }

    /** In this scenario, only single icon falls random. */
    private suspend fun startFirstCase(scope: CoroutineScope) {
        when (Tracks.values()[getRandomTrack()]) {
            Tracks.TRACK_1 -> manageTrack(scope, createIcon(), containers[0])
            Tracks.TRACK_2 -> manageTrack(scope, createIcon(), containers[1])
            Tracks.TRACK_3 -> manageTrack(scope, createIcon(), containers[2])
            Tracks.TRACK_4 -> manageTrack(scope, createIcon(), containers[0])
        }
    }

    /** In this scenario, icons fall in a single line. */
    private suspend fun startSecondCase(scope: CoroutineScope) =
        containers.forEach { container ->
            manageTrack(scope, createIcon(), container)
        }

    /**
     * It creates an icon. Set for the icon start location on the screen
     * and random image resource. If image recourse is a bomb it is assigned a bomb teg.
     * */
    private fun createIcon(): ImageView {
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        val icon = ImageView(context)
        icon.apply {
            this.layoutParams = layoutParams
            val random = (0..imagesResource.lastIndex).random()
            val imageRes = imagesResource[random]
            setImageResource(imageRes)
            if (random == 0) {
                icon.tag = BOMB_ICON_TAG
            }
        }
        return icon
    }

    /**
     * It adds an icon to current layout, sets onClickListener to icon and
     * start to move icon
     * */
    private suspend fun manageTrack(
        scope: CoroutineScope,
        icon: ImageView,
        container: FrameLayout
    ) {
        withContext(Main) { container.addView(icon) }
        icons.add(icon)
        val movingIconJob = Job()
        icon.setOnIconClickListener(movingIconJob, container)
        scope.launch(movingIconJob) {
            moveIcon(this, container, icon)
        }
    }


    private fun ImageView.setOnIconClickListener(job: CompletableJob, container: FrameLayout) {
        setOnClickListener { icon ->
            if (icon.tag == BOMB_ICON_TAG) {
                checkLivesAndDecrement()
            } else {
                val score = scoresCounter.incrementAndGet()
                _score.postValue(score.toString())
            }
            container.removeView(icon)
            icons.remove(icon)
            job.cancel()
        }
    }

    private fun checkLivesAndDecrement() {
        val livesCount = livesCounter.decrementAndGet()
        _lives.postValue(livesCount.toString())
        if (livesCount <= 0) {
            stopGame()
        }
    }

    private suspend fun moveIcon(
        scope: CoroutineScope,
        container: FrameLayout,
        icon: ImageView
    ) {
        while (scope.isActive && mainLoopJob.isActive) {
            if (!isPause) {
                if (icon.y > getScreenHeight() - 120) {
                    if (icon.tag != BOMB_ICON_TAG) {
                        checkLivesAndDecrement()
                    }
                    withContext(Main) {
                        container.removeView(icon)
                    }
                    icons.remove(icon)
                    scope.cancel()
                }
                delay(15L)
                withContext(Main) {
                    icon.y += 5
                }

            }
        }
    }

    private fun stopGame() {
        mainLoopJob.cancel()
        CoroutineScope(Main).launch {
            icons.forEach { icon ->
                (icon.parent as ViewManager).removeView(icon)
            }
            icons.clear()
            _isDialogShouldBeShow.postValue(true)
        }
    }

    fun gameOver() {
        _isGameOver.postValue(true)
    }

    fun restartGame() {
        _isGameRestart.postValue(true)
    }

    fun pauseGame() {
        isPause = true
    }

    fun resumeGame() {
        isPause = false
    }

}