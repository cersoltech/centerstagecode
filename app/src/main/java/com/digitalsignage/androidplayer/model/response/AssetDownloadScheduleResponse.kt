package com.digitalsignage.androidplayer.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AssetDownloadScheduleResponse {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: List<ScheduleData>? = null
}

class ScheduleData {
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

    @SerializedName("device_id")
    @Expose
    var deviceId: String? = null

    @SerializedName("device_template_data_id")
    @Expose
    var deviceTemplateDataId: String? = null

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null

    @SerializedName("assets_download_time")
    @Expose
    var assetsDownloadTime: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("device_template_data")
    @Expose
    var deviceTemplateData: DeviceTemplateData? = null
}

class DeviceTemplateData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("logo")
    @Expose
    var logo: String? = null

    @SerializedName("ticker_text")
    @Expose
    var tickerText: String? = null

    @SerializedName("organization_id")
    @Expose
    var organizationId: Any? = null

    @SerializedName("template_id")
    @Expose
    var templateId: String? = null

    @SerializedName("is_deleted")
    @Expose
    var isDeleted: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("template_assets")
    @Expose
    var templateAssets: List<TemplateAsset>? = null
}

class TemplateAsset {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("template_data_id")
    @Expose
    var templateDataId: String? = null

    @SerializedName("box_no")
    @Expose
    var boxNo: String? = null

    @SerializedName("asset_url")
    @Expose
    var assetUrl: String? = null

    @SerializedName("asset_type")
    @Expose
    var assetType: String? = null

    @SerializedName("asset_box_number")
    @Expose
    var assetBoxNumber: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}