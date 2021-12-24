package com.digitalsignage.androidplayer.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CitiesResponse{

    @SerializedName("data")
    @Expose
     var data: List<CityData?>? = null

    @SerializedName("status")
    @Expose
     var status: Boolean? = null
}

class CityData{
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
     var deletedAt: Any? = null

    @SerializedName("created_at")
    @Expose
     var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
     var updatedAt: String? = null

    @SerializedName("branches")
    @Expose
     var branches: List<Branch?>? = null
}

class Branch {
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
}

