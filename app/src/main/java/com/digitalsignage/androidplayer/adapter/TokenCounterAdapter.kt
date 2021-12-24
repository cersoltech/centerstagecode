package com.digitalsignage.androidplayer.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.base.BaseAdapter
import com.digitalsignage.androidplayer.base.BaseViewHolder
import com.digitalsignage.androidplayer.base.BaseViewModel
import com.digitalsignage.androidplayer.fileutils.FileUtility
import com.digitalsignage.androidplayer.model.TokenCounter
import com.digitalsignage.androidplayer.utils.getSoundTime
import com.digitalsignage.androidplayer.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.item_token_counter_view.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*


@ExperimentalCoroutinesApi
class TokenCounterAdapter(val context: Context, val onLongBackListenerCallBack: ItemLongPressListener,
                          var data: ArrayList<TokenCounter>?, viewModel: HomeViewModel,
                               val callback: RepeatTokenCallback) : BaseAdapter<HomeViewModel>() {

    private var txtfontFamily: String = ""
    private var txtColorCode: String = ""
    private var bgColorCode: String = ""
    private var fontStyleValue: String = ""
    private var alignmentvalue: String = ""
    private var itemRowCount: Int = 5
    private var fontSizeValue: Int = 0
    private var blinkDuration: String = "5"
    private var soundDuration: String = "2"
    private var selectedSoundName: String = "Ting Tong"
    private var queueLineColor: String = ""
    private var resetAdapterUI: Boolean = false
    private var isRightTokenTemplate: Boolean = false
    var homeViewModel = HomeViewModel()
    private var mContext: Context = context

    //lateinit var mBinding: ItemTokenCounterViewBinding
    private var mHolder: BaseViewHolder? = null


    init {
        homeViewModel = viewModel
     }

    override fun getAnyForPosition(position: Int): Any = data!![position]

    override fun getViewModel(): HomeViewModel = homeViewModel

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.item_token_counter_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
       val any: Any = getAnyForPosition(position)
        val viewModel: BaseViewModel = getViewModel()
        holder.bind(viewModel)

        holder.itemView.txt_counter_number.text = "${data?.get(position)?.counterNumber}"
        holder.itemView.txt_token_number.text = "${data?.get(position)?.tokenNumber}"

        if (!isRightTokenTemplate)
        {
            if (data?.get(position)!!.isAnimate && position == 0)
            {
                val blink_anim = AnimationUtils.loadAnimation(mContext, R.anim.blink)
                holder.itemView.txt_counter_number.startAnimation(blink_anim)
                holder.itemView.txt_token_number.startAnimation(blink_anim)

                Handler().postDelayed({
                    holder.itemView.txt_counter_number.clearAnimation()
                    holder.itemView.txt_token_number.clearAnimation()
                }, getSoundTime(blinkDuration).toLong())

                data?.get(position)!!.isAnimate = false
            }else
            {
                //set animation null
            }
        }
        //mHolder = holder
        setTextColor(holder,position)

        holder.itemView.const_item.setOnLongClickListener {
            onLongBackListenerCallBack.onLongClickPerform(position)
            return@setOnLongClickListener true
        }

    }

    private fun setTextColor(holder: BaseViewHolder,position: Int) {
        if (queueLineColor.isNotEmpty() && queueLineColor.isNotBlank())
        {
            if(position % 2 == 1)
            { //odd numbers 1357
                //holder.itemView.card_counter_number.setCardBackgroundColor(ContextCompat.getColor(mContext,Color.parseColor()))
                holder.itemView.card_counter_number.setCardBackgroundColor(Color.parseColor("#$queueLineColor"))
                holder.itemView.card_token_number.setCardBackgroundColor(Color.parseColor("#$queueLineColor"))
            }else
            { //even numbers2468
                holder.itemView.card_token_number.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.orange))
                holder.itemView.card_counter_number.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.orange))
            }
        }

        if (resetAdapterUI)
        {
            resetDesign(holder)
        }
        if (txtfontFamily.isNotEmpty() && txtfontFamily.isNotBlank()){
            FileUtility.setTextFont(holder.itemView.txt_counter_number, context = mContext, txtfontFamily)
            FileUtility.setTextFont(holder.itemView.txt_token_number, context = mContext, txtfontFamily)
            //FileUtility.setTextFont(holder.itemView.txt_hyphen, context = mContext, txtfontFamily)
        }


       /* if (bgColorCode.isNotEmpty() && bgColorCode.isNotBlank())
        {
            holder.itemView.const_item.setBackgroundColor(Color.parseColor("#$bgColorCode"))
        }*/

        if (txtColorCode.isNotEmpty() && txtColorCode.isNotBlank())
        {
            holder.itemView.txt_counter_number.setTextColor(Color.parseColor("#$txtColorCode"))
            holder.itemView.txt_token_number.setTextColor(Color.parseColor("#$txtColorCode"))
            //holder.itemView.txt_hyphen.setTextColor(Color.parseColor("#$txtColorCode"))
        }
        if (fontSizeValue != 0)
        {
            FileUtility.setFontSize(holder.itemView.txt_counter_number, mContext, fontSizeValue)
            FileUtility.setFontSize(holder.itemView.txt_token_number, mContext, fontSizeValue)
            //FileUtility.setFontSize(holder.itemView.txt_hyphen, mContext, fontSizeValue)
        }
        if (fontStyleValue.isNotEmpty() && fontStyleValue.isNotBlank())
        {
            FileUtility.setStyle(holder.itemView.txt_counter_number, mContext, fontStyleValue)
            FileUtility.setStyle(holder.itemView.txt_token_number, mContext, fontStyleValue)
           /* if (!fontStyleValue.equals("Underline"))
            {
                FileUtility.setStyle(holder.itemView.txt_hyphen,mContext,fontStyleValue)
            }*/
        }

        if (alignmentvalue.isNotEmpty() && alignmentvalue.isNotBlank())
        {
            FileUtility.setTextGravity(holder.itemView.txt_counter_number, alignmentvalue)
            FileUtility.setTextGravity(holder.itemView.txt_token_number, alignmentvalue)
            //FileUtility.setTextGravity(holder.itemView.txt_hyphen, alignmentvalue)
            //holder.itemView.txt_hyphen.
        }

    }

    private fun resetDesign(holder: BaseViewHolder) {
        holder.itemView.txt_counter_number.setTextColor(ContextCompat.getColor(context, R.color.text_color))
        holder.itemView.txt_token_number.setTextColor(ContextCompat.getColor(context, R.color.text_color))
        //holder.itemView.txt_hyphen.setTextColor(ContextCompat.getColor(context, R.color.text_color))
        FileUtility.setTextFont(holder.itemView.txt_counter_number, context, "proximanova-bold")
        FileUtility.setTextFont(holder.itemView.txt_token_number, context, "proximanova-bold")
        //FileUtility.setTextFont(holder.itemView.txt_hyphen, context, "proximanova-bold")
        holder.itemView.txt_counter_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.resources.getDimension(R.dimen.text_size25))
        holder.itemView.txt_token_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.resources.getDimension(R.dimen.text_size25))
        //holder.itemView.txt_hyphen.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.resources.getDimension(R.dimen.text_size25))

        holder.itemView.txt_counter_number.setPaintFlags(holder.itemView.txt_counter_number.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
//        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if(data?.size ?: 0 > itemRowCount){
            return itemRowCount
        } else
        {
            return data?.size ?: 0
        }
    }

    fun setViewsColors(resetView: Boolean, rowsCount: Int, colorCode: String, fontFamily: String,
                       bgColor: String, fontSize: Int, fontStyle: String, txtAlignment: String,
                       queueBlinkDuration: String, queueSoundDuration: String,
                       soundName: String,queueLineColor: String){

        resetAdapterUI = resetView

        if (rowsCount != 0)
        {
            itemRowCount = rowsCount
        }
        if (fontSize != 0)
        {
            fontSizeValue = fontSize
        }
        if (colorCode.isNotEmpty())
        {
            txtColorCode = colorCode
        }
        if (fontStyle.isNotEmpty())
        {
            fontStyleValue = fontStyle
        }
        if (txtAlignment.isNotEmpty())
        {
            alignmentvalue = txtAlignment
        }
      if (bgColor.isNotEmpty())
      {
          bgColorCode = bgColor
      }

        if (fontFamily.isNotEmpty())
        {
            txtfontFamily = fontFamily
        }
        if (queueBlinkDuration.isNotEmpty())
        {
            blinkDuration = queueBlinkDuration
        }
        if (queueSoundDuration.isNotEmpty())
        {
            soundDuration = queueSoundDuration
        }
        if (soundName.isNotEmpty())
        {
            selectedSoundName = soundName
        }
        if (queueLineColor.isNotEmpty())
        {
            this.queueLineColor = queueLineColor
        }
        //setTextColor(mHolder!!)
        notifyDataSetChanged()
    }

    fun setTokenCounter(tokenCounterData: TokenCounter,isRightTokenTemplate: Boolean){

     val filterList = data?.firstOrNull {
         it.tokenNumber == tokenCounterData.tokenNumber
     }
        if (filterList != null) {
          data?.remove(filterList)
      }
        data?.add(0,tokenCounterData)
        this.isRightTokenTemplate = isRightTokenTemplate
        if (!isRightTokenTemplate){
            tokenCounterData.isAnimate = true
            //interface callback
            callback.onRepeatToken(tokenCounterData,soundDuration,selectedSoundName)
        }

        data?.let {
            if (it.size > itemRowCount)
            {
                for (itemIndex in itemRowCount until it.size)
                {
                    data?.removeAt(itemIndex)
                }
            }
        }
     //tokenCounterData.isAnimate = true
     //interface callback
     //callback.onRepeatToken(tokenCounterData,soundDuration,selectedSoundName)
     notifyDataSetChanged()
    }

    interface ItemLongPressListener{
        fun onLongClickPerform(position: Int)
    }

    interface RepeatTokenCallback{
        fun onRepeatToken(tokenCounter: TokenCounter,soundSpeed: String,soundName: String)
    }
}