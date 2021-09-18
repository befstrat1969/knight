package com.idatagroup.knight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultsAdapter(private val model: KnightViewModel): RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        var pos:Int = -1

        init {
            textView = view.findViewById(R.id.lbl_title)
            view.setOnClickListener({
                model.ChangeSelectedResultIndex(pos)
            })
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.cell_result, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.pos = position
        viewHolder.textView.text = model.Results.value!![position]
    }

    override fun getItemCount() = model.Results.value!!.size
}