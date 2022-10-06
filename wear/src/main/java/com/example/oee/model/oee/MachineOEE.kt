package com.example.oee.model.oee

data class MachineOEE(var id: String,
                      var label: String,
                      var status: String,
                      var goodProductionCounter: Int,
                      var badProductionCounter: Int,
                      var expectedProductionCounter: Int,
                      var quality: Double,
                      var availability: Double,
                      var performance: Double,
                      var oee: Double,
                      var isHelpRequested: Boolean)
