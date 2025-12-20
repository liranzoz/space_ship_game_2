package com.example.space_ship_game_2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.example.space_ship_game_2.databinding.FragmentRecordListBinding

class RecordListFragment : Fragment() {

    private lateinit var binding: FragmentRecordListBinding
    var itemClickListener: OnScoreItemClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scores = GameManager.getTopScores(requireContext())
        updateListUI(scores)
    }

    private fun updateListUI(scores: List<ScoreItem>) {
        val nameViews = listOf(
            binding.txtName1, binding.txtName2, binding.txtName3, binding.txtName4, binding.txtName5,
            binding.txtName6, binding.txtName7, binding.txtName8, binding.txtName9, binding.txtName10
        )
        val scoreViews = listOf(
            binding.txtScore1, binding.txtScore2, binding.txtScore3, binding.txtScore4, binding.txtScore5,
            binding.txtScore6, binding.txtScore7, binding.txtScore8, binding.txtScore9, binding.txtScore10
        )
        val layouts = listOf(
            binding.txtNum1.parent as View, binding.txtNum2.parent as View, binding.txtNum3.parent as View,
            binding.txtNum4.parent as View, binding.txtNum5.parent as View, binding.txtNum6.parent as View,
            binding.txtNum7.parent as View, binding.txtNum8.parent as View, binding.txtNum9.parent as View,
            binding.txtNum10.parent as View
        )

        // Clear all first
        layouts.forEach { it.visibility = View.INVISIBLE }

        // Populate existing
        for (i in scores.indices) {
            val item = scores[i]
            layouts[i].visibility = View.VISIBLE
            nameViews[i].text = item.name
            scoreViews[i].text = item.score.toString()

            layouts[i].setOnClickListener {
                itemClickListener?.onScoreItemClicked(item.lat, item.lon)
            }
        }
    }
}