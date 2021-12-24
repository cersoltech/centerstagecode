package com.digitalsignage.androidplayer.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T : BaseViewModel?> : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),viewType,parent,false))

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    abstract fun getAnyForPosition(position: Int): Any
    abstract  fun getViewModel(): T
    abstract fun getLayoutIdForPosition(position: Int): Int
}