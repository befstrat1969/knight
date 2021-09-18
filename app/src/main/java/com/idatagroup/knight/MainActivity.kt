package com.idatagroup.knight

import android.app.Application
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.NavUtils
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.idatagroup.knight.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var prefsManager:PrefsManager
    private lateinit var viewModel:KnightViewModel

    private lateinit var adapter:ResultsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        viewModel = KnightViewModel(this.applicationContext as Application)
        adapter = ResultsAdapter(viewModel)


        //UI handlers
        binding.tfSize.validate(getString(R.string.sizeErr),viewModel.Size, {s -> s.inRange(6,16)},{s -> viewModel.ChangeSize(s.toInt()) })
        binding.tfMoves.validate(getString(R.string.movesErr),viewModel.Moves, {s -> s.inRange(2,10)},{s -> viewModel.ChangeMoves(s.toInt())})

        binding.tfMoves.setOnEditorActionListener { _, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
                binding.tfMoves.clearFocus()
            false
        }

        binding.vwChessboard.setOnClickListenerWithPoint { point ->
            if(viewModel.KnightPos.value == null || viewModel.KingPos.value == null){
                val p = binding.vwChessboard.chessCoordinatesFromViewCoordinates(point)
                if(p != null){
                    if (viewModel.KnightPos.value == null)
                        viewModel.ChangeKnight(p)
                    else
                        viewModel.ChangeKing(p)
                }
            }
            0}

        binding.btnReset.setOnClickListener(
            {
                viewModel.Reset(true)
            }
        )

        binding.btnCalculate.setOnClickListener({
            prefsManager.results = ArrayList<String>()
            viewModel.StartCalculator()

        })

        binding.btnCancel.setOnClickListener({
            viewModel.StopCalculator()
        })

        binding.lvResults.layoutManager = LinearLayoutManager(this)
        binding.lvResults.adapter = adapter


        //view model observers

        val sizeObserver = Observer<Int?> { newSize ->
            (binding.tfSize  as TextView).text = newSize.toString()
            binding.vwChessboard.setGridSize(newSize)
            prefsManager.size = newSize
        }
        viewModel.Size.observe(this,sizeObserver)

        val movesObserver = Observer<Int?> { newMoves ->
            (binding.tfMoves  as TextView).text = newMoves.toString()
            prefsManager.moves = newMoves
        }
        viewModel.Moves.observe(this,movesObserver)

        val knightObserver = Observer<Point?> { newKnight ->
            if(newKnight == null){
                binding.lblPutKnight.isVisible = true
                binding.lblPutKing.isVisible = false
            }
            else{
                binding.lblPutKnight.isVisible = false
                binding.lblPutKing.isVisible = true
            }
            binding.vwChessboard.setKnightPos(newKnight)
            calcEnabler()
            prefsManager.knightPos = newKnight
        }
        viewModel.KnightPos.observe(this,knightObserver)

        val kingObserver = Observer<Point?> { newKing ->
            if(newKing == null){
                if(viewModel.KnightPos.value != null)
                    binding.lblPutKing.isVisible = true
            }
            else
                binding.lblPutKing.isVisible = false
            binding.vwChessboard.setKingPos(newKing)
            calcEnabler()
            prefsManager.kingPos = newKing
        }
        viewModel.KingPos.observe(this,kingObserver)

        val resultsObserver = Observer<MutableList<String>> { nl ->
            adapter.notifyDataSetChanged()
            binding.lblSolutions.text = nl.size.toString()+" "+getString(R.string.resoultsfound)
        }
        viewModel.Results.observe(this,resultsObserver)

        val calcObjerver = Observer<Boolean> { v ->
            binding.btnCalculate.isVisible = !v
            binding.btnCancel.isVisible = v
            if(!v) {
                viewModel.CleanCalculator()
            }
        }
        viewModel.Calculating.observe(this,calcObjerver)

        val shouldShowResultsObserver = Observer<Boolean> { v ->
            binding.lblSolutions.isVisible = v
        }
        viewModel.ShouldShowResults.observe(this,shouldShowResultsObserver)

        val selectedResultObserver = Observer<Int?> { idx ->
            if(idx != null) {
                val path = viewModel.resultAtIndex(idx)
                binding.vwChessboard.setShownMove(path)
            }
            else
                binding.vwChessboard.setShownMove(null)
        }
        viewModel.SelectedResultIndex.observe(this,selectedResultObserver)


        viewModel.InitWithPrefs(prefsManager)
        calcEnabler()
    }



    override fun onStop() {
        super.onStop()
        prefsManager.results = viewModel.Results.value!!
    }

    private fun calcEnabler(){
        binding.btnCalculate.isEnabled = viewModel.isReady()
    }


}