package com.example.oee.repository

import com.example.oee.service.ApiService
import com.example.oee.model.api.CellResponse
import com.example.oee.model.api.OeeResponse
import com.example.oee.model.oee.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepository(private val apiService: ApiService, private val configurationsRepository: AppConfigurationsRepository) {

    fun getCells(onCellsResponse: OnCellsResponse) {
        apiService.instance.getCells().enqueue(object : Callback<ArrayList<CellResponse>> {
            override fun onResponse(call : Call<ArrayList<CellResponse>>, response: Response<ArrayList<CellResponse>>){
                if(response.code() == 200) {
                    val oeeResponse = response.body()!!
                    val cells = oeeResponse.map { cell ->
                        val machines = cell.machines.map { machine ->
                            Machine(machine.id, machine.label)
                        }
                        Cell(cell.id, cell.label, ArrayList(machines))
                    }
                    onCellsResponse.onSuccess(ArrayList(cells))
                } else {
                    onCellsResponse.onFailure("Corpo vazio da requisição")
                }
            }

            override fun onFailure(call: Call<ArrayList<CellResponse>>, t: Throwable){
                onCellsResponse.onFailure(t.message ?: "Erro ao realizar requisição")
            }
        })
    }

    fun getOEE(cellId: String, onOeeResponse: OnOEEResponse) {
        apiService.instance.getOEE(cellId).enqueue(object : Callback<OeeResponse> {
            override fun onResponse(call : Call<OeeResponse>, response: Response<OeeResponse>){
                if(response.code() == 200){
                    val oeeResponse = response.body()!!

                    var product: Product? = null
                    oeeResponse.currentProduct?.let {
                        product = Product(oeeResponse.currentProduct!!.id,
                                          oeeResponse.currentProduct!!.label)
                    }

                    var order: Order? = null
                    oeeResponse.currentOrder?.let {
                        order = Order(
                            oeeResponse.currentProduct!!.id,
                            oeeResponse.currentProduct!!.label
                        )
                    }

                    val machines = ArrayList<MachineOEE>();
                    oeeResponse.devices.forEach {
                        val device = it

                        if(device.isUsedInOrder) {
                            val machine = MachineOEE(
                                id = device.machine.id,
                                label = device.machine.name,
                                status = if (device.stopped) "Parada" else "Produzindo",
                                goodProductionCounter = device.totalProductionCounter - device.badProductionCounters.autoProductionCounter - device.badProductionCounters.manualProductionCounter,
                                badProductionCounter = device.badProductionCounters.autoProductionCounter + device.badProductionCounters.manualProductionCounter,
                                expectedProductionCounter = device.expectedProductionCounter,
                                quality = device.quality,
                                availability = device.availability,
                                performance = device.performance,
                                oee = device.quality * device.availability * device.performance * 100,
                                isHelpRequested = device.machine.isHelpRequested
                            )

                            machines.add(machine)
                        }
                    }

                    var cell: CellOEE? = null
                    oeeResponse.currentOrder?.details?.cell?.let { cellResponse ->
                        cell = CellOEE(
                            id = cellResponse.id,
                            label = cellResponse.label,
                            goodProductionCounter = machines.sumOf { machine -> machine.goodProductionCounter },
                            badProductionCounter = machines.sumOf { machine -> machine.badProductionCounter },
                            expectedProductionCounter = machines.sumOf { machine -> machine.expectedProductionCounter },

                            // TODO CALCULATE ############################
                            quality = 0.0,
                            availability = 0.0,
                            performance =0.0,
                            oee = 0.0,

                            order = order,
                            machines = machines,
                            product = product,
                        )
                    }



                    onOeeResponse.onSuccess(cell)
                } else {
                    onOeeResponse.onFailure("Corpo vazio da requisição")
                }
            }

            override fun onFailure(call: Call<OeeResponse>, t: Throwable){
                onOeeResponse.onFailure(t.message ?: "Erro ao realizar requisição")
            }
        })
    }

    interface OnOEEResponse {
        fun onSuccess(data: CellOEE?)
        fun onFailure(message: String)
    }

    interface OnCellsResponse {
        fun onSuccess(data: ArrayList<Cell>)
        fun onFailure(message: String)
    }
}