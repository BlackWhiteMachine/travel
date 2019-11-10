package com.example.travel.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Resource {

    @SerializedName("__type")
    @Expose
    var type: String? = null

    @SerializedName("elevations")
    @Expose
    var elevations: List<Int>? = null

    @SerializedName("zoomLevel")
    @Expose
    var zoomLevel: Int? = null

}
