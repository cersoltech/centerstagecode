package com.digitalsignage.androidplayer.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(@SerializedName("username") var userName: String = "Pritee") {
}