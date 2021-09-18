package com.idatagroup.knight

import android.graphics.Point
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.io.Console
import java.util.*

class MovesCalculator(model:KnightViewModel) {

    private val mModel = model
    private var job:Job? = null
    private lateinit var start:Point
    private lateinit var end:Point
    private var size:Int = 1
    private var moves:Int = 1
    private val resultsCache:ArrayList<String> = ArrayList(50)

    public fun Start(){
        resultsCache.clear()
        mModel.ChangeCalculating(true)
        start = mModel.KnightPos.value!!
        end = mModel.KingPos.value!!
        size = mModel.Size.value!!
        moves = mModel.Moves.value!!
        job = mModel.viewModelScope.launch(Dispatchers.Default){
            startCalculation()
        }
    }

    public fun Cancel(){
       mModel.viewModelScope.launch {
           job!!.cancelAndJoin()
           job = null
           mModel.ChangeCalculating(false,true)

       }
    }

    private var path = ArrayList<Point>()

    private suspend fun startCalculation(){
        var n = 1
       withContext(Dispatchers.Main) {
           mModel.ClearResults()
       }

        CheckPoint(start)
        PostResultsToModel()
        mModel.ChangeCalculating(false,true)

    }

    private val movements = listOf(Point(1,2),Point(2,1),Point(2,-1),Point(1,-2),Point(-1,-2),Point(-2,-1),Point(-2,1),Point(-1,2))

    private fun CheckPoint(point: Point){
        if(job!!.isActive){
            path.add(point)

            if(point.equals(end.x,end.y)) {
                resultsCache.add(pathToString())
                if(resultsCache.size>=50)
                    PostResultsToModel()
            }
            else {
                val actualTargets = CalculateTargets(point)
                for (p in actualTargets) {
                    CheckPoint(p)
                }
            }
            if(path.size>0)
                path.removeAt(path.size-1)
        }
    }

    private fun CalculateTargets(point: Point):ArrayList<Point>
    {
        val res = ArrayList<Point>(8)
        if (path.size<=moves) {
            for (m in movements) {
                val target = Point(point.x + m.x, point.y + m.y)
                if (target.x >= 0 && target.x < size && target.y >= 0 && target.y < size) {
                    val predicate: (Point) -> Boolean = {it.x == target.x && it.y == target.y}
                    if (!path.any(predicate))
                        res.add(target)
                }
            }
        }
        return res
    }

    private fun pathToString():String
    {
        var s = "";
        for(p in path){
            s += pointToString(p)+" : "
        }
        return s
    }

    private fun pointToString(p:Point):String
    {
        return (65+p.x).toChar().toString()+(p.y+1).toString();
    }

    private fun PostResultsToModel()
    {
        mModel.AddResults(resultsCache)
        resultsCache.clear()
    }

}