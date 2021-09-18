package com.idatagroup.knight

import android.graphics.Point
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    })
}

fun EditText.validate(message: String, original:LiveData<Int>, validator: (String) -> Boolean, onEndEditing:(String) -> Unit) {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    this.setOnFocusChangeListener{_, hasFocus ->
        if(!hasFocus && this.error != null)
            (this as TextView).text = original.value.toString()
        else
            onEndEditing(this.text.toString())
    }
    this.error = if (validator(this.text.toString())) null else message
}

fun String.inRange(from:Int, to:Int): Boolean {
    try {
        val n = this.toInt()
        return n>=from && n<=to
        return true
    }
    catch (nfe:NumberFormatException){
        return false
    }
}

fun View.setOnClickListenerWithPoint(action: (Point) -> Unit) {
    val coordinates = Point()
    setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            coordinates.set(event.x.toInt(), event.y.toInt())
        }
        false
    }
    setOnClickListener {
        action.invoke(coordinates)
    }
}

operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(item: T) {
    val value = this.value ?: mutableListOf()
    value.add(item)
    this.postValue(value)
}

fun <T> MutableLiveData<MutableList<T>>.smartClear(){
    this.postValue(ArrayList<T>())
}

fun <T> MutableLiveData<MutableList<T>>.addItems(a:MutableList<T>){
    val value = this.value ?: mutableListOf()
    value.addAll(a)
    this.postValue(value)
}