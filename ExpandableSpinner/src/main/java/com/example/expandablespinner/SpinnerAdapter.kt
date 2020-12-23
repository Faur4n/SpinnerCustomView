package com.example.expandablespinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

internal class SpinnerAdapter<T>(
        @LayoutRes val layoutId: Int,
        @IdRes val textViewId: Int,
        val items: MutableList<T>,
        val listener: (item: T, isChanged: Boolean) -> Unit
) : RecyclerView.Adapter<SpinnerAdapter<T>.SpinnerViewHolder>() {

    private var onBindCallback : ((view: TextView, item: T) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpinnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return SpinnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpinnerViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            listener(items[position], position != 0)
            if(position != 0){
                swapItemToFirstPosition(position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun swapItemToFirstPosition(clickedPosition: Int){
        val temp = items[0]
        items[0] = items[clickedPosition]
        items[clickedPosition] = temp
        notifyDataSetChanged()
    }

    fun setOnBindCallback(callback: (view: TextView, item: T) -> Unit){
        onBindCallback = callback
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class SpinnerViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        private val textView: TextView = view.findViewById(textViewId)

        fun bind(position: Int){

            val item = items[position]
            if(onBindCallback == null){
                val text = if(item is CharSequence){
                    item
                }else{
                    item.toString()
                }
                textView.text = text
            }else{
                onBindCallback?.invoke(textView, item)
            }

        }
    }

}