package com.example.oee.model

data class AppConfigurations(
    var ip: String = "",
    var port: String = "",
    var method: String = "http://",
    var cellsLabels: ArrayList<String> = ArrayList(),
    var machinesLabels: ArrayList<String> = ArrayList(),
    var selectedCellIndex: Int = 0,
    var selectedMachineIndex: Int = 0
)