package com.example.dynamiclist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamiclist.databinding.ListItemBinding

class ListItemAdapter(
    var list : ArrayList<ListDataModel>
) : RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        val icon : ImageView = binding.ivItemIcon
        val title : TextView = binding.tvItemTitle
        val desc : TextView = binding.tvItemDesc
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            icon.setImageResource(list[position].icon)
            title.text = list[position].title
            desc.text = list[position].desc
        }
    }

    // Function to update the list
    fun updateList(newItems: ArrayList<ListDataModel>) {
        list = newItems
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}