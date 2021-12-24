package com.digitalsignage.androidplayer.utils

import android.content.Context
import android.content.SharedPreferences
import com.digitalsignage.androidplayer.App

object PreferanceRepository {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun init(context: App) {
        sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }


    fun setString(key: String, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String = sharedPreferences.getString(key, "")!!

    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    fun setInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int = sharedPreferences.getInt(key, 0)

    fun setDownloadRefList(refList: ArrayList<String>){
        val set: MutableSet<String> = HashSet()
        set.addAll(refList)
        editor.putStringSet("downloadRefSet", set)
        editor.apply()
    }

    fun getDownloadRefList(key: String): Set<String>?{
        val set: Set<String>? = sharedPreferences.getStringSet(key, null)
        return set
    }

    fun setUdpTokenList(tokenList: ArrayList<String>){
        val set: MutableSet<String> = HashSet()
        set.addAll(tokenList)
        editor.putStringSet("UdpTokenList", set)
        editor.apply()
    }

    fun getUdpTokenList(key: String): Set<String>?{
        val set: Set<String>? = sharedPreferences.getStringSet(key, null)
        return set
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }

    fun clearData() {
        //mSharedPreferences.edit().clear().apply()
    }

    //fun setLayoutID(){}
}