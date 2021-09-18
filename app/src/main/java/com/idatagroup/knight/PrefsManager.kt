package com.idatagroup.knight

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class PrefsManager(c: Context){

    companion object {
        private const val PREFSFILE = "com.idatagroup.knight.prefs"
        private const val SIZE = "size"
        private const val MOVES = "moves"
        private const val KNIGHTPOSX= "knightposx"
        private const val KNIGHTPOSY= "knightposy"
        private const val KINGPOSX= "kingposx"
        private const val KINGPOSY= "kingposy"
        private const val RESULTS = "results"
    }

    private val prefs: SharedPreferences =
        c.getSharedPreferences(PREFSFILE, Context.MODE_PRIVATE)

    var size: Int
        get() = prefs.getInt(SIZE, 6)
        set(value) = prefs.edit().putInt(SIZE,value).apply()

    var moves: Int
        get() = prefs.getInt(MOVES, 3)
        set(value) = prefs.edit().putInt(MOVES, value).apply()

    var knightPos: Point?
        get() = getPoint(KNIGHTPOSX, KNIGHTPOSY)
        set(value) = setPoint(KNIGHTPOSX, KNIGHTPOSY, value)

    var kingPos: Point?
        get() = getPoint(KINGPOSX, KINGPOSY)
        set(value) = setPoint(KINGPOSX, KINGPOSY, value)

    var results: MutableList<String>
        get() = getJSONArray(RESULTS)
        set(value) = setJSONArray(RESULTS,value)


    private fun getJSONArray(name: String):MutableList<String>
    {
        val data = prefs.getString(name,"[]")
        val jsondata = JSONArray(data)
        val list = ArrayList<String>(jsondata.length())
        for(i in 0 .. jsondata.length()-1)
            list.add(jsondata.getString(i))
        return list
    }

    private fun setJSONArray(name:String,list:MutableList<String>)
    {
        val jsondata = JSONArray()
        for(s in list)
            jsondata.put(s)
        val datastring = jsondata.toString()
        prefs.edit().putString(name,datastring).apply()
    }

    private fun getPoint(xsetting:String,ysetting:String):Point?
    {
        val x = prefs.getInt(xsetting,-1);
        val y = prefs.getInt(ysetting,-1);
        return if (x>=0 && y>=0) Point(x,y) else null
    }

    private fun setPoint(xsetting:String,ysetting:String,p:Point?)
    {
        if (p==null) {
            prefs.edit().remove(xsetting)
            prefs.edit().remove(ysetting)
        }
        else {
            prefs.edit().putInt(xsetting,p.x).apply();
            prefs.edit().putInt(ysetting,p.y).apply();
        }
    }
}