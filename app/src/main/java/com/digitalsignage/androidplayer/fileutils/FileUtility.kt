package com.digitalsignage.androidplayer.fileutils

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.model.TokenCounter
import com.digitalsignage.androidplayer.utils.MarqueeNoFocus
import java.util.*
import kotlin.collections.ArrayList


class FileUtility {

   companion object{
       var progressDialog: ProgressDialog? = null

       fun showProgress(context: Context?) {
           hideProgress()
           if (progressDialog == null) {
               progressDialog =
                   ProgressDialog.show(context, null, null, true, false)
               progressDialog?.setContentView(R.layout.custom_dialog)
               progressDialog?.getWindow()?.setBackgroundDrawable(
                       ColorDrawable(
                               Color.TRANSPARENT
                       )
               )
               //progressDialog.setProgressStyle(R.style.popupStyle);
               progressDialog?.setCancelable(true)
           }
       }

       fun hideProgress() {
           if (progressDialog != null && progressDialog?.isShowing()!!) {
               progressDialog?.dismiss()
               progressDialog = null
           }
           // LoadingDialog.dismissLast();
       }

       fun setTextFont(txtStyle: TextView, context: Context, fontName: String){
           var typeface: Typeface? = null
           val fontInLowercase = fontName.toLowerCase(Locale.getDefault()).replace("-", "_")
           if (fontInLowercase.equals("notosans_italic"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.notosans_italic)
           }else if (fontInLowercase.equals("notosans_bolditalic"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.notosans_bolditalic)
           }else if (fontInLowercase.equals("notosans_bold"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.notosans_bold)
           }else if (fontInLowercase.equals("notosans_regular"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.notosans_regular)
           }else if (fontInLowercase.equals("opensans_bolditalic"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.opensans_bolditalic)
           }else if (fontInLowercase.equals("opensans_bold"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.opensans_bold)
           }else if (fontInLowercase.equals("opensans_regular"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.opensans_regular)
           }else if (fontInLowercase.equals("opensans_semibold"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.opensans_semibold)
           }else if (fontInLowercase.equals("poppins_bold"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.poppins_bold)
           }else if (fontInLowercase.equals("poppins_regular"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
           }else if (fontInLowercase.equals("poppins_semibold"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
           }else if (fontInLowercase.equals("proximanova_bold"))
           {
               typeface = ResourcesCompat.getFont(context, R.font.proximanova_bold)
           }

           //typeface = Typeface.createFromAsset(context.getAssets(), "fonts/$fontInLowercase.ttf")
           txtStyle.typeface = typeface
       }

       fun setFontSize(txtfont: TextView, context: Context, fontsize: Int){
           /*var textSizeInSp = 0f
           when(fontsize)
           {
               11 -> textSizeInSp = context.resources.getDimension(R.dimen.text_11)
           }*/
           //val fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSizeInSp, context.resources.displayMetrics)
           //val fontSize = TypedValue.COMPLEX_UNIT_PX, textSizeInSp, context.resources.displayMetrics)
           //txtfont.textSize = fontSize
           if (fontsize != 0)
           {
               //txtfont.setTextSize(COMPLEX_UNIT_PX, fontsize.toFloat())
               val fontConvertedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, fontsize.toFloat(), context.resources.displayMetrics)
               txtfont.textSize = fontConvertedSize
           }
        /* txtfont.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                   context.getResources().getDimension(R.dimen.result_font))
                    //text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ourFontsize)*/
       }

       fun setStyle(txtfont: TextView, context: Context, txtStyle: String){
           when(txtStyle){
               "Bold" -> txtfont.typeface = Typeface.DEFAULT_BOLD
               "Italic" -> txtfont.setTypeface(null, Typeface.ITALIC)
               "Underline" -> {
                   setUnderlineToText(txtfont, txtfont.text.toString().trim())
               }
               "Normal" -> txtfont.setTypeface(null, Typeface.NORMAL)
           }
       }

       fun setTextGravity(txtfield: TextView, txtGravity: String){
           when(txtGravity)
           {
               "Center" -> txtfield.gravity = Gravity.CENTER
               "Left" -> txtfield.gravity = Gravity.START
               "Right" -> txtfield.gravity = Gravity.END
               "Top" -> txtfield.gravity = Gravity.TOP
               "Bottom" -> txtfield.gravity = Gravity.BOTTOM
           }
       }

       fun setTextType(context: Context,token: TextView, counter: TextView,txtType: String){
           when(txtType)
           {
               "Single Line" -> {
                   token.text = context.resources.getString(R.string.token_no)
                   counter.text = context.resources.getString(R.string.counter_no)
               }
               "Double Line" -> {
                   token.text = context.resources.getString(R.string.token_number)
                   counter.text = context.resources.getString(R.string.counter_number)
               }
           }
       }

       fun setLayoutGravity(txtGravity: String, txtview: TextView) {
           /*when(txtGravity)
           {
               "Center" -> (view.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.CENTER
               "Left" -> (view.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.START
               "Right" -> (view.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.END
               "Top" -> (view.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.TOP
               "Bottom" -> (view.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.BOTTOM
           }*/
           //(view.layoutParams as LinearLayout.LayoutParams).gravity = gravity.toInt()
           val params = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.MATCH_PARENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
).apply {
    weight = 1.0f
    gravity = Gravity.RIGHT
}
           txtview.layoutParams = params
  }

       fun setTickerDirection(txtfield: MarqueeNoFocus, txtDirection: String){
          if (txtDirection.equals("Left-Right"))
          {
              txtfield.setScrollDirection(MarqueeNoFocus.LEFT_TO_RIGHT)
          }else if(txtDirection.equals("Right-Left")){
              txtfield.setScrollDirection(MarqueeNoFocus.RIGHT_TO_LEFT)
          }
           txtfield.isSelected = true
       }

       fun setTickerSpeed(txtfield: MarqueeNoFocus, txtspeed: String){
           txtfield.setScrollMode(txtspeed.toInt())
       }

       fun setUnderlineToText(txtfield: TextView, txtValue: String){
          /* val spannableString = SpannableString(txtValue).apply {
               setSpan(UnderlineSpan(), 0, txtValue.length, 0)
           }
           txtfield.text = spannableString*/
           //to remove
           //txtfield.setPaintFlags(txtfield.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
           //to set
           txtfield.setPaintFlags(txtfield.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
       }

       fun resetTickerArea(context: Context, tickerView: MarqueeNoFocus){
           tickerView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
           tickerView.setTextColor(ContextCompat.getColor(context, R.color.text_black))
           setTickerDirection(tickerView, "Right-Left")
           tickerView.setTextSize(COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size25))
       }

       fun resetHeaderArea(context: Context, headerBg: ConstraintLayout, token: TextView, counter: TextView){
           headerBg.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
           token.setTextColor(ContextCompat.getColor(context, R.color.white))
           counter.setTextColor(ContextCompat.getColor(context, R.color.white))
           setTextFont(token, context, "proximanova-bold")
           setTextFont(counter, context, "proximanova-bold")
           token.setTextSize(COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size25))
           counter.setTextSize(COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size25))
       }

       fun getTokenList(): ArrayList<TokenCounter>{
           val tokenCounterList = ArrayList<TokenCounter>()

           tokenCounterList.add(TokenCounter("1234", "1"))
           tokenCounterList.add(TokenCounter("1235", "2"))
           tokenCounterList.add(TokenCounter("1236", "3"))
           tokenCounterList.add(TokenCounter("1237", "4"))
           tokenCounterList.add(TokenCounter("1238", "5"))
           tokenCounterList.add(TokenCounter("1240", "6"))
           tokenCounterList.add(TokenCounter("1241", "7"))
           tokenCounterList.add(TokenCounter("1242", "8"))
           return  tokenCounterList
       }

       @SuppressLint("HardwareIds")
       fun getDeviceId(context: Context): String? {
           val deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               android.provider.Settings.Secure.getString(
                       context.contentResolver,
                       android.provider.Settings.Secure.ANDROID_ID
               )
           } else {
               val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
               if (mTelephony.deviceId != null) {
                   mTelephony.deviceId
               } else {
                   android.provider.Settings.Secure.getString(
                           context.contentResolver,
                           android.provider.Settings.Secure.ANDROID_ID
                   )
               }
           }
           return deviceId
       }

//60,62
       fun encryptText(keyText: String): String {
           var result = ""
           val textToEncrypt: String = keyText.replace("2","<").replace("4",">")

           val l = textToEncrypt.length
           var ch: Char
           for(i in 0 until l){
               ch = textToEncrypt[i]
               //Log.d(TAG, "before encryptText: $ch")
               ch += 10
               //Log.d(TAG, "after encryptText: $ch")
               result += ch
           }
           return result
       }

       fun decryptText( encryptedkey: String): String {
           var result = ""
           val l = encryptedkey.length
           var ch: Char
           for(i in 0 until l){
               ch = encryptedkey[i]
               ch -= 10
               result += ch
           }
           //result.replace("<","2")
           //result.replace(">","4")
           return result
       }

   }


}