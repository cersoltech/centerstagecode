package com.digitalsignage.androidplayer.model.response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DeviceGroupResponse {
    @SerializedName("data")
    @Expose
    var data: List<DeviceGroup>? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}



