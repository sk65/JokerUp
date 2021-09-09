package com.yefimoyevhen.jokerup.util.dialog

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.yefimoyevhen.jokerup.databinding.FragmentGameOverDialogBinding
import com.yefimoyevhen.jokerup.viewmodel.GameViewModel


class GameOverDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "GameOverDialogFragment"
    }

    private lateinit var gameViewModel: GameViewModel

    private var _binding: FragmentGameOverDialogBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        isCancelable = false
        setStyle(
            STYLE_NORMAL,
            R.style.Theme_Light_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameOverDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonsListeners()
    }

    private fun setButtonsListeners() {
        binding.btnExit.setOnClickListener {
            gameViewModel.gameOver()
            dismiss()
        }
        binding.btnRestart.setOnClickListener {
            gameViewModel.restart()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}