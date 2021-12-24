package com.digitalsignage.androidplayer.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AddDeviceResponse {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: AddDevice? = null
}

class AddDevice{
    @SerializedName("branch_id")
    @Expose
    private var branchId: Any? = null

    @SerializedName("device_uid")
    @Expose
    private var deviceUid: String? = null

    @SerializedName("device_name")
    @Expose
    private var deviceName: String? = null

    @SerializedName("device_no")
    @Expose
    private var deviceNo: Any? = null

    @SerializedName("device_mac_address")
    @Expose
    private var deviceMacAddress: String? = null

    @SerializedName("device_model")
    @Expose
    private var deviceModel: Any? = null

    @SerializedName("device_screen_height")
    @Expose
    private var deviceScreenHeight: String? = null

    @SerializedName("device_screen_width")
    @Expose
    private var deviceScreenWidth: String? = null

    @SerializedName("device_storage_memory")
    @Expose
    private var deviceStorageMemory: Any? = null

    @SerializedName("screen_resolution")
    @Expose
    private var screenResolution: Any? = null

    @SerializedName("updated_at")
    @Expose
    private var updatedAt: String? = null

    @SerializedName("created_at")
    @Expose
    private var createdAt: String? = null

    @SerializedName("id")
    @Expose
    private var id: Int? = null
}