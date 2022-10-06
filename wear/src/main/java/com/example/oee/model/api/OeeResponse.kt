package com.example.oee.model.api

import com.google.gson.annotations.SerializedName

class OeeResponse {
    @SerializedName("currentOrderProductionResume")
    var currentOrder: OrderResponse? = OrderResponse()

    @SerializedName("product")
    var currentProduct: ProductResponse? = ProductResponse()

    @SerializedName("devices")
    var devices: ArrayList<Device> = ArrayList<Device>()

    class OrderResponse {
        @SerializedName("good")
        var goodProductionCounter: Int = 0

        @SerializedName("bad")
        var badProductionCounter: Int = 0

        @SerializedName("expected")
        var expectedProductionCounter: Int = 0

        @SerializedName("currentOrder")
        var details= OrderDetailsResponse()

    }

    class OrderDetailsResponse {
        @SerializedName("id")
        var id: String = ""

        @SerializedName("order_number")
        var label: String = ""

        @SerializedName("cell")
        var cell: CellResponse = CellResponse()
    }

    class CellResponse {
        @SerializedName("id")
        var id: String = ""

        @SerializedName("name")
        var label: String = ""
    }

    class ProductResponse {
        @SerializedName("id")
        var id: String = ""

        @SerializedName("label")
        var label: String = ""
    }

    class Device {
        @SerializedName("stopped")
        var stopped = true

        @SerializedName("isMachineUsedInThisOrder")
        var isUsedInOrder: Boolean = false

        @SerializedName("quality")
        var quality: Double = 0.0

        @SerializedName("efficiency")
        var availability: Double = 0.0

        @SerializedName("productivity")
        var performance: Double = 0.0

        @SerializedName("total_production")
        var totalProductionCounter: Int = 0

        @SerializedName("expectedProducts")
        var expectedProductionCounter: Int = 0

        @SerializedName("badProducts")
        var badProductionCounters: BadProductionResponse = BadProductionResponse()

        @SerializedName("machine")
        var machine: MachineResponse = MachineResponse()

    }
    class BadProductionResponse {
        @SerializedName("manualBadProducts")
        var manualProductionCounter: Int = 0

        @SerializedName("autoBadProducts")
        var autoProductionCounter: Int = 0
    }

    class MachineResponse {
        @SerializedName("id")
        var id: String = ""

        @SerializedName("name")
        var name: String = ""

        @SerializedName("help")
        var isHelpRequested: Boolean = false
    }
}