package com.idatagroup.knight

import android.app.Application
import android.graphics.Point
import android.os.Handler
import android.os.Looper.getMainLooper
import androidx.lifecycle.*
import java.util.*

class KnightViewModel(app:Application) : AndroidViewModel(app) {

    private val mSize = MutableLiveData<Int>(6)
    val Size: LiveData<Int> get() = mSize

    private val mMoves = MutableLiveData<Int>(3)
    val Moves: LiveData<Int> get() = mMoves

    private val mKnightPos = MutableLiveData<Point?>(null)
    val KnightPos: LiveData<Point?> get() = mKnightPos

    private val mKingPos = MutableLiveData<Point?>(null)
    val KingPos: LiveData<Point?> get() = mKingPos

    private val mResults = MutableLiveData<MutableList<String>>(ArrayList<String>())
    val Results: LiveData<MutableList<String>> get() = mResults

    private val mCalculating = MutableLiveData<Boolean>(false)
    val Calculating: LiveData<Boolean> get() = mCalculating

    private val mShouldShowResults = MutableLiveData<Boolean>(false)
    val ShouldShowResults:LiveData<Boolean> get() = mShouldShowResults

    private val mSelectedResultIndex = MutableLiveData<Int?>(null)
    val SelectedResultIndex: LiveData<Int?> get() = mSelectedResultIndex

    private var calculator:MovesCalculator? = null

    public fun isReady():Boolean{
        return mKnightPos.value != null && mKingPos.value != null
    }

    public fun Reset(removePoints:Boolean){
        if(removePoints){
            mKnightPos.value = null
            mKingPos.value = null
        }
        mResults.smartClear()
        mShouldShowResults.value = false
        mSelectedResultIndex.value = null
        if(calculator != null)
            calculator!!.Cancel()
    }

    public fun ChangeSize(sz:Int){
        if(sz != mSize.value) {
            mSize.value = sz
            Reset(true)
        }
    }

    public fun ChangeMoves(mv:Int){
        if(mv != mMoves.value) {
            mMoves.value = mv
            Reset(false)
        }
    }

    public fun ChangeKnight(p:Point?){
        mKnightPos.value = p
        if(p == null) {
            mKingPos.value = null
        }
    }

    public fun ChangeKing(p:Point?){
        mKingPos.value = p
    }

    public fun ChangeSelectedResultIndex(idx:Int?){
        mSelectedResultIndex.value = idx
    }

    public fun ChangeCalculating(c:Boolean,post:Boolean=false){
        if(post)
            mCalculating.postValue(c)
        else
            mCalculating.value = c
    }

    public fun StartCalculator(){
        calculator = MovesCalculator(this)
        calculator!!.Start()
        mShouldShowResults.value = true
        mSelectedResultIndex.value = null
    }

    public fun StopCalculator(){
        if(calculator != null)
            calculator!!.Cancel()
    }

    public fun CleanCalculator(){
        calculator = null
    }

    public fun ClearResults()
    {
        mResults.smartClear()
    }

    public fun AddResults(a:ArrayList<String>)
    {
        mResults.addItems(a)
    }

    public fun AddResult(s:String)
    {
        mResults += s
    }


    public fun InitWithPrefs(prefs:PrefsManager)
    {
        val originalSize = prefs.size
        val originalMoves = prefs.moves
        val originalKnightPos = prefs.knightPos
        val originalKingPos = prefs.kingPos
        val originalResults = prefs.results
        mSize.postValue(originalSize)
        mMoves.postValue(originalMoves)
        mKnightPos.postValue(originalKnightPos)
        mKingPos.postValue(originalKingPos)
        mResults.postValue(originalResults)
        if(originalResults.size>0)
            mShouldShowResults.postValue(true)
    }

    public fun resultAtIndex(idx:Int):ArrayList<Point>
    {
        val a = ArrayList<Point>()
        val s = mResults.value!![idx]
        val parts = s.split(" : ")
        for (p in parts){
            if(p.length>0){
                val c = p.first() as Char
                val point = Point(c.toByte().toInt()-65,p.substring(1).toInt()-1)
                a.add(point)
            }
        }
        return a
    }

}