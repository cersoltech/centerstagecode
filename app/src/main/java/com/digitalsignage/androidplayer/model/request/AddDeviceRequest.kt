package com.digitalsignage.androidplayer.model.request

data class AddDeviceRequest(
    val device_ip: String? = null,
    val device_name: String? = null,
    val device_no: String? = null,
    val device_screen_height: String? = null,
    val device_screen_width: String? = null
)

