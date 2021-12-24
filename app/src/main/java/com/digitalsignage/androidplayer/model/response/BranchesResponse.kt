package com.digitalsignage.androidplayer.model.response

import android.bluetooth.BluetoothClass.Device
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class BranchesResponse {
    @SerializedName("data")
    @Expose
    var data: List<BranchData>? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}

class BranchData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("city_id")
    @Expose
    var cityId: String? = null

    @SerializedName("branch_name")
    @Expose
    var branchName: String? = null

    @SerializedName("branch_code")
    @Expose
    var branchCode: String? = null

    @SerializedName("branch_manager_name")
    @Expose
    var branchManagerName: String? = null

    @SerializedName("branch_contact_no")
    @Expose
    var branchContactNo: String? = null

    @SerializedName("branch_it_support_name")
    @Expose
    var branchItSupportName: String? = null

    @SerializedName("branch_it_support_no")
    @Expose
    var branchItSupportNo: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("device_groups")
    @Expose
    var deviceGroups: List<DeviceGroup>? = null
}

class DeviceGroup {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("branch_id")
    @Expose
    var branchId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("slug")
    @Expose
    var slug: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("devices")
    @Expose
    var devices: List<Device>? = null
}

class Device {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("zone_id")
    @Expose
    var zoneId: String? = null

    @SerializedName("city_id")
    @Expose
    var cityId: String? = null

    @SerializedName("branch_id")
    @Expose
    var branchId: String? = null

    @SerializedName("device_group_id")
    @Expose
    var deviceGroupId: String? = null

    @SerializedName("device_orientation")
    @Expose
    var deviceOrientation: String? = null

    @SerializedName("device_name")
    @Expose
    var deviceName: String? = null

    @SerializedName("device_uid")
    @Expose
    var deviceUid: String? = null

    @SerializedName("device_no")
    @Expose
    var deviceNo: String? = null

    @SerializedName("device_ip")
    @Expose
    var deviceIp: String? = null

    @SerializedName("device_model")
    @Expose
    var deviceModel: String? = null

    @SerializedName("device_description")
    @Expose
    var deviceDescription: String? = null

    @SerializedName("device_screen_height")
    @Expose
    var deviceScreenHeight: String? = null

    @SerializedName("device_screen_width")
    @Expose
    var deviceScreenWidth: String? = null

    @SerializedName("device_storage_memory")
    @Expose
    var deviceStorageMemory: String? = null

    @SerializedName("screen_resolution")
    @Expose
    var screenResolution: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}