package com.ly.eserver.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.ly.eserver.R
import com.ly.eserver.adapter.holder.SelectDialogHolder

/**
 *
 * Created by Max on 2017/8/7.
 */
class SelectDialogAdapter : RecyclerView.Adapter<SelectDialogHolder>() {

    var list: List<String> = ArrayList<String>()

    var click: OnItemClick? = null

    fun add(list: List<String>) {
        this.list = list
    }

    override fun onBindViewHolder(holder: SelectDialogHolder?, position: Int) {
        holder!!.textView.setText(list!!.get(position))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SelectDialogHolder {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.dialog_select_list_item, parent, false)
        val holder = SelectDialogHolder(view, click!!)
        return holder
    }

    fun setItemClick(click: OnItemClick) {
        this.click = click
    }

}