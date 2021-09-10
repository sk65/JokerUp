package com.yefimoyevhen.jokerup.view


import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yefimoyevhen.jokerup.databinding.ActivityMainBinding
import com.yefimoyevhen.jokerup.util.hideStatusBar
import com.yefimoyevhen.jokerup.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gameViewModel: GameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar(this)
    }

    override fun onPause() {
        super.onPause()
        Log.i("dev", "onPause")
      //  gameViewModel.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        Log.i("dev", "onResume")
       // gameViewModel.resumeGame()
    }
}
