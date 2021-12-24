package com.digitalsignage.androidplayer.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetTemplatesResponse {
    @SerializedName("data")
    @Expose
    var data: List<GetTemplatesData?>? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}

class GetTemplatesData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("quebox_required")
    @Expose
    var queboxRequired: String? = null

    @SerializedName("logo_required")
    @Expose
    var logoRequired: String? = null

    @SerializedName("ticker_text_required")
    @Expose
    var tickerTextRequired: String? = null

    @SerializedName("images_required")
    @Expose
    var imagesRequired: String? = null

    @SerializedName("videos_required")
    @Expose
    var videosRequired: String? = null

    @SerializedName("ppt_required")
    @Expose
    var pptRequired: String? = null

    @SerializedName("template_images")
    @Expose
    var templateImages: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}