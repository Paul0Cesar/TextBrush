package com.project.textbrush.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.project.textbrush.R
import com.project.textbrush.databinding.ColorItemBinding

class ColorAdapter(
    private val colorList: Array<Int>,
    private val selectColor: MutableLiveData<Int>
) :
    RecyclerView.Adapter<ColorAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ColorItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.color_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = colorList[position]
        holder.bind(color)
        holder.itemView.setOnClickListener { selectColor.value = color }
    }

    class ViewHolder(private val binding: ColorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(color: Int) {
            binding.btnColor.setCardBackgroundColor(color)
        }
    }

}