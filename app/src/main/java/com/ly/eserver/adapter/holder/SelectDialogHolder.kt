package com.ly.eserver.adapter.holder

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import com.ly.eserver.R
import com.ly.eserver.adapter.OnItemClick

/**
 * Created by Max on 2017/8/7.
 */
class SelectDialogHolder (itemView: View, click: OnItemClick) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var textView: TextView

    private var click: OnItemClick? = null

    init {
        textView = itemView.findViewOften(R.id.select_item)
        this.click = click
        itemView.setOnClickListener(this)
    }


    fun <T : View> View.findViewOften(viewId: Int): T {
        val viewHolder: SparseArray<View> = tag as? SparseArray<View> ?: SparseArray()
        tag = viewHolder
        var childView: View? = viewHolder.get(viewId)
        if (null == childView) {
            childView = findViewById(viewId)
            viewHolder.put(viewId, childView)
        }
        return childView as T
    }

    override fun onClick(v: View?) {
        if (v != null) {
            click!!.onItemClick(v, adapterPosition)
        }
    }

}