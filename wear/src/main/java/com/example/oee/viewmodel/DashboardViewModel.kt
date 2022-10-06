package com.example.oee.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oee.model.oee.Cell
import com.example.oee.model.oee.CellOEE
import com.example.oee.model.oee.MachineOEE
import com.example.oee.repository.AppConfigurationsRepository
import com.example.oee.repository.DashboardRepository
import com.example.oee.service.OnDataChanged
import com.example.oee.service.WearableSyncService
import org.koin.core.component.KoinComponent


class DashboardViewModel(
    private val dataRepository: DashboardRepository,
    private val configurationsRepository: AppConfigurationsRepository,
    private var syncService: WearableSyncService
) : ViewModel(), KoinComponent {
    private var totalMachinesCounter = 0
    private var listener: ViewListener? = null
    private var cells: ArrayList<Cell> = ArrayList();

    init {
        syncService.registerListener(object : OnDataChanged {
            override fun dataChanged() {
                refreshData()
            }
        })
    }

    fun registerListener(listener: ViewListener) {
        this.listener = listener
    }

    fun refreshData() {
        getCells()
    }

    private fun getCells() {
        dataRepository.getCells(object : DashboardRepository.OnCellsResponse {
            override fun onSuccess(data: ArrayList<Cell>) {
                cells = data
                // Update configs
                configurationsRepository.setCellsLabelsList(ArrayList(cells.map { cell -> cell.label }))
                if (cells.size == 0) {
                    configurationsRepository.setMachinesLabelsList(ArrayList())
                    // Don't call OEE when no Cells
                    return
                } else {
                    var selectedCellIndex = configurationsRepository.getSelectedCellIndex();
                    // Cannot request without getting the Cells List

                    if (selectedCellIndex > cells.size - 1) {
                        selectedCellIndex = 0
                    }
                    configurationsRepository.setMachinesLabelsList(ArrayList(cells[selectedCellIndex].machines.map { machine -> machine.label }))
                }

                if(syncService.requireSync) {
                    syncService.requireSync = false
                    syncService.sendToMobile()
                }

                getOEE()
            }

            override fun onFailure(message: String) {
                listener?.let {
                    listener!!.onFailure(message)
                }
            }
        })
    }

    private fun getOEE() {
        var selectedCellIndex = configurationsRepository.getSelectedCellIndex();
        // Cannot request without getting the Cells List

        if (selectedCellIndex > cells.size - 1) {
            selectedCellIndex = 0
        }

        dataRepository.getOEE(
            cells[selectedCellIndex].id,
            object : DashboardRepository.OnOEEResponse {
                override fun onSuccess(data: CellOEE?) {
                    val machine =
                        data!!.machines[configurationsRepository.getSelectedMachineIndex()]
                    totalMachinesCounter = data.machines.size

                    listener?.let {
                        listener!!.onOeeData(machine)
                    }
                }

                override fun onFailure(message: String) {
                    listener?.let {
                        listener!!.onFailure(message)
                    }
                }
            })
    }

    fun incrementSelectedCellIndex() {
        configurationsRepository.setSelectedMachineIndex(0);

        var selectedCellIndex = configurationsRepository.getSelectedCellIndex();
        selectedCellIndex += 1;
        if (selectedCellIndex >= cells.size) {
            selectedCellIndex = 0
        }
        configurationsRepository.setSelectedCellIndex(selectedCellIndex)

        syncService.requireSync = true

        refreshData()
    }

    fun decrementSelectedCellIndex() {
        configurationsRepository.setSelectedMachineIndex(0);

        var selectedCellIndex = configurationsRepository.getSelectedCellIndex();
        selectedCellIndex -= 1;
        if (selectedCellIndex < 0) {
            selectedCellIndex =
                if (cells.size - 1 >= 0) cells.size - 1 else 0
        }
        configurationsRepository.setSelectedCellIndex(selectedCellIndex)

        syncService.requireSync = true

        refreshData()
    }

    fun incrementSelectedMachineIndex() {
        var selectedMachineIndex = configurationsRepository.getSelectedMachineIndex()

        selectedMachineIndex += 1;
        if (selectedMachineIndex >= totalMachinesCounter) {
            selectedMachineIndex = 0
        }

        configurationsRepository.setSelectedMachineIndex(selectedMachineIndex)

        syncService.requireSync = true

        refreshData()
    }

    fun decrementSelectedMachineIndex() {
        var selectedMachineIndex = configurationsRepository.getSelectedMachineIndex()

        selectedMachineIndex -= 1;
        if (selectedMachineIndex < 0) {
            selectedMachineIndex =
                if (totalMachinesCounter - 1 >= 0) totalMachinesCounter - 1 else 0
        }

        configurationsRepository.setSelectedMachineIndex(selectedMachineIndex)

        syncService.requireSync = true

        refreshData()
    }
}


interface ViewListener {
    fun onOeeData(data: MachineOEE)
    fun onFailure(message: String)
}
