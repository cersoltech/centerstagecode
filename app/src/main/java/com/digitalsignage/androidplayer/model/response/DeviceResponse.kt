package com.digitalsignage.androidplayer.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ZoneResponse {
    @SerializedName("data")
    @Expose
    var data: List<Zone>? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}

class Zone {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("organization_id")
    @Expose
    var organizationId: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("slug")
    @Expose
    var slug: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("cities")
    @Expose
    var cities: List<City>? = null
}

class City {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("zone_id")
    @Expose
    var zoneId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("slug")
    @Expose
    var slug: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}

class DeviceResponse {
    @SerializedName("data")
    @Expose
    var data: List<Device>? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}