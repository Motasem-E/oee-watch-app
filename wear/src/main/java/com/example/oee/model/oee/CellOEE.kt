package com.example.oee.model.oee

data class CellOEE (var id: String,
                    var label: String,
                    var goodProductionCounter: Int,
                    var badProductionCounter: Int,
                    var expectedProductionCounter: Int,
                    var quality: Double,
                    var availability: Double,
                    var performance: Double,
                    var oee: Double,
                    var machines: ArrayList<MachineOEE>,
                    var product: Product?,
                    var order: Order?)