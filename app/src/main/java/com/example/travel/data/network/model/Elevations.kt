package com.example.travel.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Elevations {

    @SerializedName("authenticationResultCode")
    @Expose
    var authenticationResultCode: String? = null

    @SerializedName("brandLogoUri")
    @Expose
    var brandLogoUri: String? = null

    @SerializedName("copyright")
    @Expose
    var copyright: String? = null

    @SerializedName("resourceSets")
    @Expose
    var resourceSets: List<ResourceSet>? = null

    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null

    @SerializedName("statusDescription")
    @Expose
    var statusDescription: String? = null

    @SerializedName("traceId")
    @Expose
    var traceId: String? = null

}
