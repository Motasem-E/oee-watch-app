package com.example.oee.model.api

import com.google.gson.annotations.SerializedName

class CellResponse {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("name")
    var label: String = ""

    @SerializedName("machines")
    var machines: ArrayList<MachineResponse> = ArrayList()

    class MachineResponse {
        @SerializedName("id")
        var id: String = ""

        @SerializedName("name")
        var label: String = ""
    }
}
