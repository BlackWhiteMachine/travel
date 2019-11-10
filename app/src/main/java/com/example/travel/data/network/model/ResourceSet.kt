package com.example.travel.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResourceSet {

    @SerializedName("estimatedTotal")
    @Expose
    var estimatedTotal: Int? = null

    @SerializedName("resources")
    @Expose
    var resources: List<Resource>? = null

}
