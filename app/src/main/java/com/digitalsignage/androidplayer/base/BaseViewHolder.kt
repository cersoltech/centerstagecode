package com.digitalsignage.androidplayer.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.digitalsignage.androidplayer.BR

class BaseViewHolder(viewDataBinding: ViewDataBinding): RecyclerView.ViewHolder(viewDataBinding.root) {
    private val binding: ViewDataBinding?

    init {
        this.binding = viewDataBinding
    }

    fun bind(viewModel: BaseViewModel?): ViewDataBinding?{
        //binding?.setVariable(BR.any,any)
        binding?.setVariable(BR.viewModel,viewModel)
        binding?.executePendingBindings()
        return binding
    }
}