package com.example.oee.model.oee

data class Cell(
    var id: String = "",
    var label: String = "",
    var machines: ArrayList<Machine> = ArrayList()
)