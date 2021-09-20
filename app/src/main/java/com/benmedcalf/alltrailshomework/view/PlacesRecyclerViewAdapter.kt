package com.benmedcalf.alltrailshomework.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.benmedcalf.alltrailshomework.databinding.FragmentItemBinding
import com.benmedcalf.alltrailshomework.model.Restaurant

class PlacesRecyclerViewAdapter(
    private val values: List<Restaurant>
) : RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.rating.toString()
        holder.contentView.text = item.name

        //TODO("Navigate to detail")
        // holder.idView.setOnClickListener { }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}