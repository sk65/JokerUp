package com.yefimoyevhen.jokerup.util

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.yefimoyevhen.jokerup.R
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GameManager(
    private val coroutineContext: CoroutineContext,
    private val context: Context,
    private val containers: Array<FrameLayout>
) {

    private val lives = MutableLiveData(INIT_NUMBER_OF_LIVES)
    private val score = MutableLiveData(INIT_NUMBER_OF_SCORES)
    private val imagesResource = arrayOf(
        R.drawable.ic_bomb,
        R.drawable.ic_joker1,
        R.drawable.ic_joker2,
        R.drawable.ic_joker3,
        R.drawable.ic_joker4
    )


    private fun createIcon(): ImageView {
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val random = (0..imagesResource.lastIndex).random()
        val imageRes = imagesResource[random]

        return ImageView(context).apply {
            this.layoutParams = layoutParams
            setImageResource(imageRes)
            if (random == 0) {
                this.tag = BOMB_ICON_TAG
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
                    var value = lives.value!!
                    lives.postValue(--value)
                }
                container.removeView(icon)
                scope.cancel()
            }
            icon.y += SHIFT
        }
    }

    fun startGame() {
        CoroutineScope(Dispatchers.Main + coroutineContext).launch {
            while (isActive) {
                delay(1000L)
                val icon = createIcon()
                when (Tracks.values()[getRandomTrack()]) {
                    Tracks.TRACK_1 -> manageIcon(icon, this, containers[0])
                    Tracks.TRACK_2 -> manageIcon(icon, this, containers[1])
                    Tracks.TRACK_3 -> manageIcon(icon, this, containers[2])
                    Tracks.TRACK_4 -> manageIcon(icon, this, containers[3])
                }
            }
        }
    }

    private suspend fun manageIcon(icon: ImageView, scope: CoroutineScope, container: FrameLayout) {
        if (coroutineContext.isActive) {
            val job = Job()
            container.addView(icon)
            if (icon.tag == BOMB_ICON_TAG) {
                icon.setOnClickListener {
                    container.removeView(icon)
                    job.cancel()
                    var value = lives.value!!
                    lives.postValue(--value)
                }
            } else {
                icon.setOnClickListener {
                    container.removeView(icon)
                    var scoreValue = score.value!!
                    score.postValue(++scoreValue)
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