package com.yefimoyevhen.jokerup.view


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yefimoyevhen.jokerup.databinding.ActivityMainBinding
import com.yefimoyevhen.jokerup.util.hideStatusBar

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar(this)
    }
}
