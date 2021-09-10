package com.yefimoyevhen.jokerup.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yefimoyevhen.jokerup.databinding.FragmentGameFieldBinding
import com.yefimoyevhen.jokerup.util.GoogleAdMobManager
import com.yefimoyevhen.jokerup.util.dialog.GameOverDialogFragment
import com.yefimoyevhen.jokerup.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFieldFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel

    private var _binding: FragmentGameFieldBinding? = null
    private val binding
        get() = _binding!!

    private var _adManager: GoogleAdMobManager? = null
    private val adMobManager
        get() = _adManager!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adManager = GoogleAdMobManager(
            requireActivity(),
            binding.adView
        ) {
            gameViewModel.startGame(getContainers())
        }

        initObservers()
        gameViewModel.startGame(getContainers())
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adManager = null
    }

    private fun showDialog() =
        GameOverDialogFragment().show(
            requireActivity().supportFragmentManager,
            GameOverDialogFragment.TAG
        )


    private fun getContainers(): Array<FrameLayout> =
        arrayOf(binding.track1, binding.track2, binding.track3, binding.track4)


    private fun initObservers() {

        gameViewModel.isDialogShouldBeShow.observe(viewLifecycleOwner) { isDialogShouldBeShow ->
            if (isDialogShouldBeShow == true) {
                showDialog()
            }
        }

        gameViewModel.lives.observe(viewLifecycleOwner) { numOfLives ->
            binding.livesContainer.text = numOfLives
        }

        gameViewModel.score.observe(viewLifecycleOwner) { score ->
            binding.scoreConteiner.text = score
        }

        gameViewModel.isGameOver.observe(viewLifecycleOwner) { isGameOver ->
            if (isGameOver == true) {
                requireActivity().finishAndRemoveTask();
            }
        }
        gameViewModel.isGameRestart.observe(viewLifecycleOwner) { isGameRestart ->
            if (isGameRestart == true) {
                adMobManager.showInterstitialAd()
            }
        }
    }
}