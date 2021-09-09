package com.yefimoyevhen.jokerup.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yefimoyevhen.jokerup.viewmodel.GameViewModel

class GameViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
         //   GameViewModel()
        }
        return TODO()
    }
}