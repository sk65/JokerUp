package com.yefimoyevhen.jokerup.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import com.yefimoyevhen.jokerup.R

fun hideStatusBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}

fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

fun getRandomTrack(): Int = (0..Tracks.values().lastIndex).random()

private val imagesResource = arrayOf(
    R.drawable.ic_bomb,
    R.drawable.ic_joker1,
    R.drawable.ic_joker2,
    R.drawable.ic_joker3,
    R.drawable.ic_joker4
)
fun createIcon(context: Context): ImageView {
    val layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    )
   // val imagesResource = context.resources.getIntArray(R.array.icons)
    val random = (0..imagesResource.lastIndex).random()
    val imageRes = imagesResource[random]

    Log.i("dev", imageRes.toString())
    return ImageView(context).apply {
        this.layoutParams = layoutParams
        setImageResource(imageRes)
        if (random == 0) {
            this.tag = BOMB_ICON_TAG
        }
    }
}

