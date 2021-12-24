package com.digitalsignage.androidplayer.fileutils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.databinding.CustomizationDialogBinding
import com.digitalsignage.androidplayer.utils.CustDialogListerner
import com.digitalsignage.androidplayer.utils.PreferanceRepository
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams


object DialogHelper {

    fun showCustmizeDialog(
            context: Context,
            layoutInflater: LayoutInflater,
            templatedId: Int,
            listerner: CustDialogListerner
    ){
        var radioGroup: RadioGroup? = null
        var radiogrpHeaderLable: RadioGroup? = null

        var headertextColor = ""
        var headerbgColor = ""
        var headerfontFamily = ""
        var headerfontSize = 0
        var headerfontAlign = ""
        var headerfontStyle = ""
        var headerLableType = ""

        var queuetextColor = ""
        var queueLineColor = ""
        var queuebgColor = ""
        var rowsCount = 6
        var columnsCount = 2
        var queuefontFamily = ""
        var queuefontSize = 0
        var queuefontAlign = ""
        var queuefontStyle = ""
        var queueBlinkSpeed = ""
        var queueSoundSpeed = ""
        var selectedSound = ""

        var rtqueueTextColor = ""
        var rtqueueBgColor = ""
        var rtqueueFontFamily = ""
        var rtqueueFontSize = 0
        var rtqueueFontAlign = ""
        var rtqueueFontStyle = ""

        var tickertextColor = ""
        var tickerbgColor = ""
        var tickerfontFamily = ""
        var tickerfontSize = 0
        var tickerfontStyle = ""
        var tickerDirection = ""
        var tickerSpeed = ""
        val currentBackgroundColor = 0xffffffff

        var hedaerReset = false
        var queueReset = false
        var tickerReset = false
        var rtqueueReset = false
//        var data_save = PreferanceRepository.getInt(Constants.ZONE_ID).toString()+"_"+
//                PreferanceRepository.getInt(Constants.CITY_ID).toString()+"_"+
//                PreferanceRepository.getInt(Constants.BRANCH_ID).toString()+"_"+
//                PreferanceRepository.getInt(Constants.DEVICEGRP_ID).toString()+"_"+
//                PreferanceRepository.getString(Constants.DEVICE_ID).toString()

                var data_save = "SAVED_THEME"


        val rowsList = arrayListOf(
                "Select No. of Rows",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20"
        )
        val columnsList = arrayListOf("Select No. of Columns", "2")
        val alignList = arrayListOf("Select Text Align", "Center","Left","Right","Top","Bottom")
        val directionList = arrayListOf("Select Direction", "Left-Right","Right-Left")
        val fontstyleList = arrayListOf("Select Font Style","Normal","Bold","Italic")
        val fontSizeList = arrayListOf("Select Font Size", "11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30")
        val fontList = arrayListOf(
                "Select Font", "NotoSans-Regular", "NotoSans-Bold", "NotoSans-BoldItalic", "NotoSans-Italic",
                "OpenSans-Regular", "OpenSans-Bold", "OpenSans-BoldItalic", "OpenSans-Italic", "OpenSans-SemiBold",
                "Poppins-Regular", "Poppins-Bold", "Poppins-SemiBold","Proximanova-Bold"
        )


        val designDialog = Dialog(context)

        val designDialogBinding = CustomizationDialogBinding.inflate(layoutInflater, null, false)
        designDialog.setContentView(designDialogBinding.root)
        designDialog.setCancelable(true)
        designDialog.setCanceledOnTouchOutside(true)
        designDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //filterBinding.progress = viewmodel
        val rowsAdapter = ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                rowsList
        )
        val columnsAdapter = ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                columnsList
        )

        val fontfamilyAdapter = ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                fontList
        )
        val fontSizeAdapter = ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                fontSizeList
        )
        val fontStyleAdapter = ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                fontstyleList
        )
        val textAlignAdapter = ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                alignList
        )
        val tickerDirectionAdapter = ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                directionList
        )


        designDialogBinding.spinnerNoofrows.adapter = rowsAdapter
        designDialogBinding.spinnerNoofcolumns.adapter = columnsAdapter
        designDialogBinding.spinnerQueuefontfamily.adapter = fontfamilyAdapter
        designDialogBinding.spinnerHeaderfontfamily.adapter = fontfamilyAdapter
        designDialogBinding.spinnerRtqueuqefontfamily.adapter = fontfamilyAdapter
        designDialogBinding.spinnerHeaderfontstyle.adapter = fontStyleAdapter
        designDialogBinding.spinnerQueuefontstyle.adapter = fontStyleAdapter
        designDialogBinding.spinnerRtqueuqefontstyle.adapter = fontStyleAdapter
        //designDialogBinding.spinnerHeaderfontsize.adapter = fontSizeAdapter
        //designDialogBinding.spinnerQueuefontsize.adapter = fontSizeAdapter
        designDialogBinding.spinnerHeadertxtAlign.adapter = textAlignAdapter
        designDialogBinding.spinnerQueuetxtAlign.adapter = textAlignAdapter
        designDialogBinding.spinnerRtqueuqetxtAlign.adapter = textAlignAdapter

        designDialogBinding.spinnerTickerfontfamily.adapter = fontfamilyAdapter
        //designDialogBinding.spinnerTickerfontsize.adapter = fontSizeAdapter
        designDialogBinding.spinnerTickerfontstyle.adapter = fontStyleAdapter
        designDialogBinding.spinnerTickerDirection.adapter = tickerDirectionAdapter

        //designDialogBinding.txtColortext.visibility = View.GONE
        designDialogBinding.txtHeaderreset.setOnClickListener {
            hedaerReset = true
            PreferanceRepository.setBoolean(data_save+"_HeaderReset",hedaerReset)

            designDialogBinding.viewHedaercolor.setBackgroundColor(Color.parseColor("#FFFFFF"))
            designDialogBinding.viewHedaerbgcolor.setBackgroundColor(Color.parseColor("#0a5688"))

            designDialogBinding.spinnerHeaderfontfamily.setSelection(1)
            designDialogBinding.edtHeaderfontsize.setText("20")
            designDialogBinding.spinnerHeadertxtAlign.setSelection(1)
            designDialogBinding.spinnerHeaderfontstyle.setSelection(1)

            headertextColor = "FFFFFF"
            headerbgColor = "0a5688"
            headerfontFamily = "Proximanova-Bold"
            headerfontSize = 20
            headerfontAlign = "Left"
            headerfontStyle = "Normal"

            PreferanceRepository.setString(data_save+"_Header_txt","FFFFFF")
            PreferanceRepository.setString(data_save+"_Header_bg_txt","0a5688")
            PreferanceRepository.setString(data_save+"_Spinner_header_font_family","Proximanova-Bold")
            PreferanceRepository.setInt(data_save+"_Spinner_header_font_size",20)
            PreferanceRepository.setString(data_save+"_Spinner_header_text_align","Left")
            PreferanceRepository.setString(data_save+"_Spinner_header_font_style","Normal")

        }
         designDialogBinding.txtQueuereset.setOnClickListener {
            queueReset = true

             designDialogBinding.spinnerNoofrows.setSelection(1)
             designDialogBinding.spinnerNoofcolumns.setSelection(1)
             designDialogBinding.spinnerQueuefontfamily.setSelection(1)
             designDialogBinding.edtQueuefontsize.setText("0")
             designDialogBinding.spinnerQueuetxtAlign.setSelection(1)
             designDialogBinding.spinnerQueuefontstyle.setSelection(1)
             designDialogBinding.seekBarSoundspeed.setProgress(2f)
             designDialogBinding.seekBarBlinkspeed.setProgress(5f)
             designDialogBinding.radioBtnTing.isChecked = true

             queuetextColor = "000000"
             queueLineColor = "f3954f"
             queuebgColor = "FFFFFF"
             rowsCount = 6
             columnsCount = 2
             queuefontFamily = "Proximanova-Bold"
             queuefontSize = 20
             queuefontAlign = "Left"
             queuefontStyle = "Normal"
             queueBlinkSpeed = "5"
             queueSoundSpeed = "2"
             selectedSound = "Ting Tong"

             designDialogBinding.viewQueuetxtcolor.setBackgroundColor(Color.parseColor("#000000"))
             designDialogBinding.viewQueuebgcolor.setBackgroundColor(Color.parseColor("#FFFFFF"))
             PreferanceRepository.setBoolean(data_save+"_QueueReset",queueReset)
             PreferanceRepository.setString(data_save+"_Queue_txt","000000")
             PreferanceRepository.setString(data_save+"_Queue_bg_txt","FFFFFF")
             PreferanceRepository.setInt(data_save+"_Spinner_no_of_row_txt",6)
             PreferanceRepository.setInt(data_save+"_Spinner_no_of_column_txt",2)
             PreferanceRepository.setString(data_save+"_Spinner_queue_font_family","Proximanova-Bold")
             PreferanceRepository.setInt(data_save+"_Spinner_queue_font_size",20)
             PreferanceRepository.setString(data_save+"_Spinner_queue_text_align","Left")
             PreferanceRepository.setString(data_save+"_Spinner_queue_font_style","Normal")
             PreferanceRepository.setString(data_save+"_Queue_blinkspeed","5")
             PreferanceRepository.setString(data_save+"_Queue_soundspeed","2")
             PreferanceRepository.setString(data_save+"_Queue_sound","Ting Tong")
         }
         designDialogBinding.txtTickereset.setOnClickListener {
            tickerReset = true

             designDialogBinding.spinnerTickerfontfamily.setSelection(1)
             designDialogBinding.edtTickerfontsize.setText("20")
             designDialogBinding.spinnerTickerfontstyle.setSelection(1)
             designDialogBinding.spinnerTickerDirection.setSelection(2)
             designDialogBinding.seekBarTickerspeed.setProgress(1f)

             tickertextColor = "000000"
             tickerbgColor = "FFFFFF"
             tickerfontFamily = "Proximanova-Bold"
             tickerfontSize = 20
             tickerfontStyle = "Normal"
             tickerDirection = "Right-Left"
             tickerSpeed = "1"

             designDialogBinding.viewTickertxtcolor.setBackgroundColor(Color.parseColor("#000000"))
             designDialogBinding.viewTickerbgcolor.setBackgroundColor(Color.parseColor("#FFFFFF"))
             PreferanceRepository.setBoolean(data_save+"_TickerReset",tickerReset)
             PreferanceRepository.setString(data_save+"_Ticker_txt","000000")
             PreferanceRepository.setString(data_save+"_Ticker_bg_txt","FFFFFF")
             PreferanceRepository.setString(data_save+"_Spinner_ticker_font_family","Proximanova-Bold")
             PreferanceRepository.setInt(data_save+"_Spinner_ticker_font_size",20)
             PreferanceRepository.setString(data_save+"_Spinner_ticker_font_style","Normal")
             PreferanceRepository.setString(data_save+"_Spinner_ticker_direction","Right-Left")
             PreferanceRepository.setString(data_save+"_Ticker_speed","1")
        }
         designDialogBinding.txtRtqueuereset.setOnClickListener {
             rtqueueReset = true
             rtqueueTextColor = "FFFFFF"
             rtqueueBgColor = "C64C605F"
             rtqueueFontFamily = "Proximanova-Bold"
             rtqueueFontSize = 45
             rtqueueFontAlign = "Center"
             rtqueueFontStyle = "Normal"

             PreferanceRepository.setBoolean(data_save+"_rtQueueReset",rtqueueReset)
             designDialogBinding.viewRtqueuqecolor.setBackgroundColor(Color.parseColor("#FFFFFF"))
             designDialogBinding.viewRtqueuqebgcolor.setBackgroundColor(Color.parseColor("#C64C605F"))
             designDialogBinding.spinnerRtqueuqefontfamily.setSelection(1)
             designDialogBinding.edtRtqueuqefontsize.setText("45")
             designDialogBinding.spinnerRtqueuqetxtAlign.setSelection(1)
             designDialogBinding.spinnerRtqueuqefontstyle.setSelection(1)
         }

        if(!PreferanceRepository.getString(data_save+"_Queue_txt").isNullOrEmpty()){
            queuetextColor = PreferanceRepository.getString(data_save+"_Queue_txt")
            designDialogBinding.viewQueuetxtcolor.setBackgroundColor(Color.parseColor("#$queuetextColor"))
        }
        if(!PreferanceRepository.getString(data_save+"_Queue_bg_txt").isNullOrEmpty()){
            queuebgColor = PreferanceRepository.getString(data_save+"_Queue_bg_txt")
            designDialogBinding.viewQueuebgcolor.setBackgroundColor(Color.parseColor("#$queuebgColor"))
        }
        if(!PreferanceRepository.getString(data_save+"_Queue_Line").isNullOrEmpty()){
            queueLineColor = PreferanceRepository.getString(data_save+"_Queue_Line")
            designDialogBinding.viewQueueLinecolor.setBackgroundColor(Color.parseColor("#$queueLineColor"))
        }

        if(!PreferanceRepository.getString(data_save+"_RtQueue_txtColor").isNullOrEmpty()){
            rtqueueTextColor = PreferanceRepository.getString(data_save+"_RtQueue_txtColor")
            designDialogBinding.viewRtqueuqecolor.setBackgroundColor(Color.parseColor("#$rtqueueTextColor"))
        }

        if(!PreferanceRepository.getString(data_save+"_RtQueue_bgColor").isNullOrEmpty()){
            rtqueueBgColor = PreferanceRepository.getString(data_save+"_RtQueue_bgColor")
            designDialogBinding.viewRtqueuqebgcolor.setBackgroundColor(Color.parseColor("#$rtqueueBgColor"))
        }
        if(!PreferanceRepository.getString(data_save+"_Header_txt").isNullOrEmpty()){
            headertextColor = PreferanceRepository.getString(data_save+"_Header_txt")
            designDialogBinding.viewHedaercolor.setBackgroundColor(Color.parseColor("#$headertextColor"))
        }
        if(!PreferanceRepository.getString(data_save+"_Header_bg_txt").isNullOrEmpty()){
            headerbgColor = PreferanceRepository.getString(data_save+"_Header_bg_txt")
            designDialogBinding.viewHedaerbgcolor.setBackgroundColor(Color.parseColor("#$headerbgColor"))
        }
        if(!PreferanceRepository.getString(data_save+"_Ticker_txt").isNullOrEmpty()){
            tickertextColor = PreferanceRepository.getString(data_save+"_Ticker_txt")
            designDialogBinding.viewTickertxtcolor.setBackgroundColor(Color.parseColor("#$tickertextColor"))
        }
        if(!PreferanceRepository.getString(data_save+"_Ticker_bg_txt").isNullOrEmpty()){
            tickerbgColor = PreferanceRepository.getString(data_save+"_Ticker_bg_txt")
            designDialogBinding.viewTickerbgcolor.setBackgroundColor(Color.parseColor("#$tickerbgColor"))
        }
        if(PreferanceRepository.getInt(data_save+"_Spinner_no_of_row_txt")!= 0){
            rowsCount = PreferanceRepository.getInt(data_save+"_Spinner_no_of_row_txt")
            designDialogBinding.spinnerNoofrows.setSelection(rowsList.indexOf(rowsCount.toString()))
        }
        if(PreferanceRepository.getInt(data_save+"_Spinner_no_of_column_txt")!= 2){
            columnsCount = PreferanceRepository.getInt(data_save+"_Spinner_no_of_column_txt")
            designDialogBinding.spinnerNoofcolumns.setSelection(columnsList.indexOf(columnsCount.toString()))
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_queue_font_family").isNullOrEmpty()){
            queuefontFamily = PreferanceRepository.getString(data_save+"_Spinner_queue_font_family")
            designDialogBinding.spinnerQueuefontfamily.setSelection(fontList.indexOf(queuefontFamily))
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_header_font_family").isNullOrEmpty()){
            headerfontFamily = PreferanceRepository.getString(data_save+"_Spinner_header_font_family")
            designDialogBinding.spinnerHeaderfontfamily.setSelection(fontList.indexOf(headerfontFamily))
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_ticker_font_family").isNullOrEmpty()){
            tickerfontFamily = PreferanceRepository.getString(data_save+"_Spinner_ticker_font_family")
            designDialogBinding.spinnerTickerfontfamily.setSelection(fontList.indexOf(tickerfontFamily))
        }
        if(!PreferanceRepository.getString(data_save+"_rtqueue_font_family").isNullOrEmpty()){
            rtqueueFontFamily = PreferanceRepository.getString(data_save+"_rtqueue_font_family")
            designDialogBinding.spinnerRtqueuqefontfamily.setSelection(fontList.indexOf(rtqueueFontFamily))
        }
        if(!PreferanceRepository.getInt(data_save+"_Spinner_header_font_size").toString().isNullOrEmpty()){
            headerfontSize = PreferanceRepository.getInt(data_save+"_Spinner_header_font_size")
            if (headerfontSize != 0)
            {
                designDialogBinding.edtHeaderfontsize.setText(headerfontSize.toString())
            }
        }
        if(!PreferanceRepository.getString(data_save+"_Queue_sound").isNullOrEmpty()){
            selectedSound = PreferanceRepository.getString(data_save+"_Queue_sound")
            if (selectedSound == "Ding Dong")
            {
              designDialogBinding.radioBtnDing.isChecked = true
            }else if (selectedSound == "Ting Tong")
            {
                designDialogBinding.radioBtnTing.isChecked = true
            }else if (selectedSound == "Blip Blip")
            {
                designDialogBinding.radioBtnBlip.isChecked = true
            }else if (selectedSound == "Beep")
            {
                designDialogBinding.radioBtnBeep.isChecked = true
            }else if (selectedSound == "Knock Knock")
            {
                designDialogBinding.radioBtnKnock.isChecked = true
            }else if (selectedSound == "Single Beep")
            {
                designDialogBinding.radioBtnSinglebeep.isChecked = true
            }
        }

         if(!PreferanceRepository.getString(data_save+"_HeaderLable_type").isNullOrEmpty()) {
             headerLableType = PreferanceRepository.getString(data_save+"_HeaderLable_type")
             if (headerLableType == "Single Line") {
                 designDialogBinding.radioBtnSingleline.isChecked = true
             } else if (headerLableType == "Double Line") {
                 designDialogBinding.radioBtnDoubleline.isChecked = true
             }
         }
        if(!PreferanceRepository.getString(data_save+"_Ticker_speed").isNullOrEmpty()){
            tickerSpeed = PreferanceRepository.getString(data_save+"_Ticker_speed")
            designDialogBinding.seekBarTickerspeed.setProgress(tickerSpeed.toFloat())
        }else{
            designDialogBinding.seekBarTickerspeed.setProgress(1f)
        }

        if(!PreferanceRepository.getString(data_save+"_Queue_blinkspeed").isNullOrEmpty()){
            queueBlinkSpeed = PreferanceRepository.getString(data_save+"_Queue_blinkspeed")
            designDialogBinding.seekBarBlinkspeed.setProgress(queueBlinkSpeed.toFloat())
        }else{
            designDialogBinding.seekBarBlinkspeed.setProgress(5f)
        }

        if(!PreferanceRepository.getString(data_save+"_Queue_soundspeed").isNullOrEmpty()){
            queueSoundSpeed = PreferanceRepository.getString(data_save+"_Queue_soundspeed")
            designDialogBinding.seekBarSoundspeed.setProgress(queueSoundSpeed.toFloat())
        }else{
            designDialogBinding.seekBarSoundspeed.setProgress(2f)
        }

        if(!PreferanceRepository.getInt(data_save+"_Spinner_queue_font_size").toString().isNullOrEmpty()){
            queuefontSize = PreferanceRepository.getInt(data_save+"_Spinner_queue_font_size")
            if (queuefontSize != 0)
            {
                designDialogBinding.edtQueuefontsize.setText(queuefontSize.toString())
            }
        }
        if(!PreferanceRepository.getInt(data_save+"_Spinner_ticker_font_size").toString().isNullOrEmpty()){
            tickerfontSize = PreferanceRepository.getInt(data_save+"_Spinner_ticker_font_size")
            if (tickerfontSize != 0)
            {
                designDialogBinding.edtTickerfontsize.setText(tickerfontSize.toString())
            }
        }
        if(!PreferanceRepository.getInt(data_save+"_RtQueue_fontsize").toString().isNullOrEmpty()){
            rtqueueFontSize = PreferanceRepository.getInt(data_save+"_RtQueue_fontsize")
            if (rtqueueFontSize != 0)
            {
                designDialogBinding.edtRtqueuqefontsize.setText(rtqueueFontSize.toString())
            }
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_header_font_style").isNullOrEmpty()){
            headerfontStyle = PreferanceRepository.getString(data_save+"_Spinner_header_font_style")
            designDialogBinding.spinnerHeaderfontstyle.setSelection(fontstyleList.indexOf(headerfontStyle))
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_ticker_font_style").isNullOrEmpty()){
            tickerfontStyle = PreferanceRepository.getString(data_save+"_Spinner_ticker_font_style")
            designDialogBinding.spinnerTickerfontstyle.setSelection(fontstyleList.indexOf(tickerfontStyle))
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_queue_font_style").isNullOrEmpty()){
            queuefontStyle = PreferanceRepository.getString(data_save+"_Spinner_queue_font_style")
            designDialogBinding.spinnerQueuefontstyle.setSelection(fontstyleList.indexOf(queuefontStyle))
        }
        if(!PreferanceRepository.getString(data_save+"_RtQueue_fontStyle").isNullOrEmpty()){
            rtqueueFontStyle = PreferanceRepository.getString(data_save+"_RtQueue_fontStyle")
            designDialogBinding.spinnerRtqueuqefontstyle.setSelection(fontstyleList.indexOf(rtqueueFontStyle))
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_header_text_align").isNullOrEmpty()){
            headerfontAlign = PreferanceRepository.getString(data_save+"_Spinner_header_text_align")
            designDialogBinding.spinnerHeadertxtAlign.setSelection(alignList.indexOf(headerfontAlign))
        }
        if(!PreferanceRepository.getString(data_save+"_Spinner_ticker_direction").isNullOrEmpty()){
            tickerDirection = PreferanceRepository.getString(data_save+"_Spinner_ticker_direction")
            designDialogBinding.spinnerTickerDirection.setSelection(directionList.indexOf(tickerDirection))
        }

        if(!PreferanceRepository.getString(data_save+"_Spinner_queue_text_align").isNullOrEmpty()){
            queuefontAlign = PreferanceRepository.getString(data_save+"_Spinner_queue_text_align")
            designDialogBinding.spinnerQueuetxtAlign.setSelection(alignList.indexOf(queuefontAlign))
        }
        if(!PreferanceRepository.getString(data_save+"queue_text_align").isNullOrEmpty()){
            rtqueueFontAlign = PreferanceRepository.getString(data_save+"queue_text_align")
            designDialogBinding.spinnerRtqueuqetxtAlign.setSelection(alignList.indexOf(rtqueueFontAlign))
        }

        designDialogBinding.btnQueuetxtColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            queuetextColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewQueuetxtcolor.setBackgroundColor(Color.parseColor("#$queuetextColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnQueueLineColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                        // Handle on color change
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            queueLineColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewQueueLinecolor.setBackgroundColor(Color.parseColor("#$queueLineColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnQueuebgColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            queuebgColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewQueuebgcolor.setBackgroundColor(Color.parseColor("#$queuebgColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnHeadertxtColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                        // Handle on color change
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            headertextColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewHedaercolor.setBackgroundColor(Color.parseColor("#$headertextColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnHeaderbgColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            headerbgColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewHedaerbgcolor.setBackgroundColor(Color.parseColor("#$headerbgColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnRtqueuqetxtColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                        // Handle on color change
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            rtqueueTextColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewRtqueuqecolor.setBackgroundColor(Color.parseColor("#$rtqueueTextColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnRtqueuqebgColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            rtqueueBgColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewRtqueuqebgcolor.setBackgroundColor(Color.parseColor("#$rtqueueBgColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnTickertxtColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            tickertextColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewTickertxtcolor.setBackgroundColor(Color.parseColor("#$tickertextColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }
        designDialogBinding.btnTickerbgColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.color_dialog_title)
                    .initialColor(currentBackgroundColor.toInt())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorChangedListener { selectedColor ->
                    }
                    .setOnColorSelectedListener { selectedColor ->
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                    .setPositiveButton("OK", object : ColorPickerClickListener {
                        override fun onClick(
                                d: DialogInterface?,
                                lastSelectedColor: Int,
                                allColors: Array<out Int>?
                        ) {
                            tickerbgColor = Integer.toHexString(lastSelectedColor)
                            designDialogBinding.viewTickerbgcolor.setBackgroundColor(Color.parseColor("#$tickerbgColor"))
                        }
                    })
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })
                    .showColorEdit(true)
                    .setColorEditTextColor(
                            ContextCompat.getColor(
                                    context,
                                    android.R.color.holo_blue_bright
                            )
                    )
                    .build()
                    .show()
        }

        designDialogBinding.spinnerNoofrows.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    if (position > 0) {
                     rowsCount = rowsList[position].toInt()
                        designDialogBinding.spinnerNoofrows.setSelection(rowsList.indexOf(rowsCount.toString()))
                    }
                }
            }

        designDialogBinding.spinnerNoofcolumns.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {

                    if (position > 0) {
                        columnsCount = columnsList[position].toInt()
                        designDialogBinding.spinnerNoofcolumns.setSelection(columnsList.indexOf(columnsCount.toString()))
                    }
                }

            }

        designDialogBinding.spinnerQueuefontfamily.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {

                    if (position > 0) {
                        queuefontFamily = fontList[position]
                        designDialogBinding.spinnerQueuefontfamily.setSelection(fontList.indexOf(queuefontFamily))
                    }
                }

            }

        designDialogBinding.spinnerHeaderfontfamily.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            headerfontFamily = fontList[position]
                            designDialogBinding.spinnerHeaderfontfamily.setSelection(fontList.indexOf(headerfontFamily))
                        }
                    }

                }

        designDialogBinding.spinnerRtqueuqefontfamily.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            rtqueueFontFamily = fontList[position]
                            designDialogBinding.spinnerRtqueuqefontfamily.setSelection(fontList.indexOf(rtqueueFontFamily))
                        }
                    }
                }


        designDialogBinding.spinnerTickerfontfamily.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            tickerfontFamily = fontList[position]
                            designDialogBinding.spinnerTickerfontfamily.setSelection(fontList.indexOf(tickerfontFamily))
                        }
                    }

                }

               designDialogBinding.spinnerHeaderfontstyle.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            headerfontStyle = fontstyleList[position]
                            designDialogBinding.spinnerHeaderfontstyle.setSelection(fontstyleList.indexOf(headerfontStyle))
                        }
                    }

                }
        designDialogBinding.spinnerRtqueuqefontstyle.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            rtqueueFontStyle = fontstyleList[position]
                            designDialogBinding.spinnerHeaderfontstyle.setSelection(fontstyleList.indexOf(rtqueueFontStyle))
                        }
                    }

                }
        designDialogBinding.spinnerTickerfontstyle.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            tickerfontStyle = fontstyleList[position]
                            designDialogBinding.spinnerTickerfontstyle.setSelection(fontstyleList.indexOf(tickerfontStyle))
                        }
                    }

                }
        designDialogBinding.spinnerQueuefontstyle.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            queuefontStyle = fontstyleList[position]
                            designDialogBinding.spinnerQueuefontstyle.setSelection(fontstyleList.indexOf(queuefontStyle))
                        }
                    }

                }
        designDialogBinding.spinnerHeadertxtAlign.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            headerfontAlign = alignList[position]
                            designDialogBinding.spinnerHeadertxtAlign.setSelection(alignList.indexOf(headerfontAlign))
                        }
                    }

                }
        designDialogBinding.spinnerTickerDirection.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            tickerDirection = directionList[position]
                            designDialogBinding.spinnerTickerDirection.setSelection(directionList.indexOf(tickerDirection))
                        }
                    }

                }
        designDialogBinding.spinnerQueuetxtAlign.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            queuefontAlign = alignList[position]
                            designDialogBinding.spinnerQueuetxtAlign.setSelection(alignList.indexOf(queuefontAlign))
                        }
                    }

                }
        designDialogBinding.spinnerRtqueuqetxtAlign.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        if (position > 0) {
                            rtqueueFontAlign = alignList[position]
                            designDialogBinding.spinnerQueuetxtAlign.setSelection(alignList.indexOf(rtqueueFontAlign))
                        }
                    }

                }

       /* designDialogBinding.seekBarTickerspeed?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                //showShortToast(context, seek.progress.toString())
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                //showShortToast(context, seek.progress.toString())
                tickerSpeed = seek.progress.toString()
            }
        })*/
        designDialogBinding.seekBarTickerspeed.onSeekChangeListener = object : OnSeekChangeListener{
            override fun onSeeking(seekParams: SeekParams?) {
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                tickerSpeed = seekBar?.progress.toString()
            }
        }

        designDialogBinding.seekBarBlinkspeed.onSeekChangeListener = object : OnSeekChangeListener{
            override fun onSeeking(seekParams: SeekParams?) {
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                queueBlinkSpeed = seekBar?.progress.toString()
            }
        }

        designDialogBinding.seekBarSoundspeed.onSeekChangeListener = object : OnSeekChangeListener{
            override fun onSeeking(seekParams: SeekParams?) {
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                queueSoundSpeed = seekBar?.progress.toString()
            }
        }

        designDialogBinding.btnApply.setOnClickListener {

            radioGroup = designDialogBinding.radioGroup
            val selectedButton = radioGroup?.checkedRadioButtonId

            val radioButton: RadioButton = designDialog.findViewById(selectedButton!!) as RadioButton
                    //designDialogBinding.root.findViewById(selectedButton!!)
            selectedSound = radioButton.text.toString()
            Log.d("pppp", "showCustmizeDialog: $selectedSound")
            PreferanceRepository.setString(data_save+"_Queue_sound",selectedSound)

            radiogrpHeaderLable = designDialogBinding.radioGroupLable
            val selectedButton1 = radiogrpHeaderLable?.checkedRadioButtonId

            val radioButton1: RadioButton = designDialog.findViewById(selectedButton1!!) as RadioButton
            headerLableType = radioButton1.text.toString()
            Log.d("pppp", "showCustmizeDialog1: $headerLableType")
            PreferanceRepository.setString(data_save+"_HeaderLable_type",headerLableType)

            if (designDialogBinding.edtHeaderfontsize.text.toString().trim().isNotEmpty())
            {
                headerfontSize = designDialogBinding.edtHeaderfontsize.text.toString().trim().toInt()
                PreferanceRepository.setInt(data_save+"_Spinner_header_font_size",headerfontSize)
            }
            if (designDialogBinding.edtRtqueuqefontsize.text.toString().trim().isNotEmpty())
            {
                rtqueueFontSize = designDialogBinding.edtRtqueuqefontsize.text.toString().trim().toInt()
                PreferanceRepository.setInt(data_save+"_RtQueue_fontsize",rtqueueFontSize)
            }
            if (designDialogBinding.edtQueuefontsize.text.toString().trim().isNotEmpty())
            {
                queuefontSize = designDialogBinding.edtQueuefontsize.text.toString().trim().toInt()
                PreferanceRepository.setInt(data_save+"_Spinner_queue_font_size",queuefontSize)
            }
            if (designDialogBinding.edtTickerfontsize.text.toString().trim().isNotEmpty())
            {
                tickerfontSize = designDialogBinding.edtTickerfontsize.text.toString().trim().toInt()
                PreferanceRepository.setInt(data_save+"_Spinner_ticker_font_size",tickerfontSize)
            }

            /*left queue*/
            if (!queuetextColor.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Queue_txt",queuetextColor)
            }
            if (!queueLineColor.isNullOrBlank()){
                PreferanceRepository.setString(data_save+"_Queue_Line",queueLineColor)
            }
            if (!queuebgColor.isNullOrBlank()){
                PreferanceRepository.setString(data_save+"_Queue_bg_txt",queuebgColor)
            }
            if (queuefontFamily.isNotBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_queue_font_family",queuefontFamily)
            }
            if (!queuefontStyle.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_queue_font_style",queuefontStyle)
            }
            if (queuefontAlign.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_queue_text_align",queuefontAlign)
            }
            PreferanceRepository.setInt(data_save+"_Spinner_no_of_row_txt",rowsCount)
            PreferanceRepository.setInt(data_save+"_Spinner_no_of_column_txt",columnsCount)
            if (queueBlinkSpeed.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Queue_blinkspeed",queueBlinkSpeed)
            }
            if (queueSoundSpeed.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Queue_soundspeed",queueSoundSpeed)
            }
            /*rt token counter*/
            if (!rtqueueTextColor.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_RtQueue_txtColor",rtqueueTextColor)
            }
            if (rtqueueBgColor.isNotBlank())
            {
                PreferanceRepository.setString(data_save+"_RtQueue_bgColor",rtqueueBgColor)
            }
            if (!rtqueueFontFamily.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_rtqueue_font_family",rtqueueFontFamily)
            }
            if (!rtqueueFontStyle.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_RtQueue_fontStyle",rtqueueFontStyle)
            }
            if (!rtqueueFontAlign.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"queue_text_align",rtqueueFontAlign)
            }
            /*ticker*/
            if (tickertextColor.isNotBlank())
            {
                PreferanceRepository.setString(data_save+"_Ticker_txt",tickertextColor)
            }
            if (tickerbgColor.isNotBlank())
            {
                PreferanceRepository.setString(data_save+"_Ticker_bg_txt",tickerbgColor)
            }
            if (!tickerfontFamily.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_ticker_font_family",tickerfontFamily)
            }
            if (!tickerfontStyle.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_ticker_font_style",tickerfontStyle)
            }
            if (!tickerDirection.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_ticker_direction",tickerDirection)
            }
            if (tickerSpeed.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Ticker_speed",tickerSpeed)
            }
            /*header*/
            if (!headertextColor.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Header_txt",headertextColor)
            }
            if (!headerbgColor.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Header_bg_txt",headerbgColor)
            }
            if (!headerfontFamily.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_header_font_family",headerfontFamily)
            }
            if (!headerfontStyle.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_header_font_style",headerfontStyle)
            }
            if (!headerfontAlign.isNullOrBlank())
            {
                PreferanceRepository.setString(data_save+"_Spinner_header_text_align",headerfontAlign)
            }


            listerner.onFilerValuesSet(
                    templatedId,
                    headerTxtcolorCode = headertextColor,
                    headerBgColor = headerbgColor,
                    headerFont = headerfontFamily,
                    headerFontSize = headerfontSize,
                    headerTxtAlign = headerfontAlign,
                    headerFontStye = headerfontStyle,
                    headerLableType,
                    queueTxtcolorCode = queuetextColor,
                    queueBgColor = queuebgColor,
                    queueFont = queuefontFamily,
                    queueFontSize = queuefontSize,
                    queueTxtAlign = queuefontAlign,
                    queueFontStye = queuefontStyle,
                    rowsCount, columnsCount,tickertextColor,tickerbgColor,tickerfontFamily,
                    tickerfontSize,tickerDirection,tickerfontStyle,tickerSpeed,queueBlinkSpeed,
                    queueSoundSpeed,selectedSound,headerReset = hedaerReset,queueReset,
                    tickerReset,queueLineColor,rtqueueTextColor,rtqueueBgColor,
                    rtqueueFontFamily,rtqueueFontSize,rtqueueFontAlign,rtqueueFontStyle)

            designDialog.dismiss()
        }
        designDialog.show()
    }
    
}