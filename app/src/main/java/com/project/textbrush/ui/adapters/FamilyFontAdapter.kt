package com.project.textbrush.ui.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.project.textbrush.R
import com.project.textbrush.databinding.TextItemBinding


class FamilyFontAdapter(
    private val colorList: Array<String>,
    private val selectFamilyFont: MutableLiveData<String>
) :
    RecyclerView.Adapter<FamilyFontAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: TextItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.text_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val style = colorList[position]
        holder.bind(style)
        holder.itemView.setOnClickListener { selectFamilyFont.value = style }
    }

    class ViewHolder(private val binding: TextItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(familyFont: String) {
            binding.text.typeface = Typeface.create(
                familyFont,
                Typeface.NORMAL
            )
        }
    }

}