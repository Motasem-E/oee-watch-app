package com.example.oee.service

import android.content.Context
import com.example.oee.constants.Constants
import com.example.oee.model.AppConfigurations
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*

interface SyncListener {
    fun dataChanged()
    fun onFailure(message: String)
}

class WearableSyncService (configurations: AppConfigurations, context: Context): DataClient.OnDataChangedListener {
    private var configurations: AppConfigurations
    private var context: Context

    private val dataClient by lazy { Wearable.getDataClient(context) }

    private var listener: SyncListener? = null;

    init {
        this.configurations = configurations
        this.context = context
    }

    fun registerListener(listener: SyncListener) {
        this.listener = listener
    }

    fun sendToWearable() {
        val putDataReq: PutDataRequest = PutDataMapRequest.create(Constants.CONFIGURATIONS_PATH).setUrgent().run {
            // Always update message
            dataMap.putString("timestamp", System.currentTimeMillis().toString())

            dataMap.putString(Constants.CONFIGURATIONS_METHOD_KEY, configurations.method)
            dataMap.putString(Constants.CONFIGURATIONS_IP_KEY, configurations.ip)
            dataMap.putString(Constants.CONFIGURATIONS_PORT_KEY, configurations.port)
            dataMap.putInt(Constants.CONFIGURATIONS_SELECTED_CELL_KEY, configurations.selectedCellIndex)
            dataMap.putInt(Constants.CONFIGURATIONS_SELECTED_MACHINE_KEY, configurations.selectedMachineIndex)

            // Campos enviados somente para resolver o bug desses campos não serem sincronizados por falta de uso
            dataMap.putStringArrayList(Constants.CONFIGURATIONS_CELLS_KEY, configurations.cellsLabels)
            dataMap.putStringArrayList(Constants.CONFIGURATIONS_MACHINES_KEY, configurations.machinesLabels)


            asPutDataRequest()
        }
        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)

        putDataTask.addOnFailureListener(context.mainExecutor, OnFailureListener {
            this@WearableSyncService.listener?.let { _ ->
                this@WearableSyncService.listener!!.onFailure(it.message ?: "Error while sending packets to Wearable")
            }
        })
    }

    fun requestSyncNow() {
        val putDataReq: PutDataRequest = PutDataMapRequest.create(Constants.SYNC_PATH).setUrgent().run {
            // Always update message
            dataMap.putString("timestamp", System.currentTimeMillis().toString())
            asPutDataRequest()
        }
        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)

        putDataTask.addOnFailureListener(context.mainExecutor, OnFailureListener {
            this@WearableSyncService.listener?.let { _ ->
                this@WearableSyncService.listener!!.onFailure(it.message ?: "Error while sending packets to Wearable")
            }
        })
    }

    private fun updateConfigurations(item: DataItem) {
        DataMapItem.fromDataItem(item).dataMap.apply {
            val method = getString(Constants.CONFIGURATIONS_METHOD_KEY)
            val ip = getString(Constants.CONFIGURATIONS_IP_KEY)
            val port = getString(Constants.CONFIGURATIONS_PORT_KEY)
            val cellsLabels = getStringArrayList(Constants.CONFIGURATIONS_CELLS_KEY)
            val machineLabels = getStringArrayList(Constants.CONFIGURATIONS_MACHINES_KEY)
            val selectedCellIndex = getInt(Constants.CONFIGURATIONS_SELECTED_CELL_KEY)
            val selectedMachineIndex = getInt(Constants.CONFIGURATIONS_SELECTED_MACHINE_KEY)

            configurations.method = method!!
            configurations.ip = ip!!
            configurations.port = port!!

            configurations.cellsLabels = cellsLabels!!
            configurations.machinesLabels = machineLabels!!
            configurations.selectedCellIndex = selectedCellIndex!!
            configurations.selectedMachineIndex = selectedMachineIndex!!


            /* println(cellsLabels)
            println(machineLabels)
            println(selectedCellIndex)
            println(selectedMachineIndex)
            println("########################################") */


            this@WearableSyncService.listener?.let {
                this@WearableSyncService.listener!!.dataChanged()
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo(Constants.CONFIGURATIONS_PATH) == 0) {
                        updateConfigurations(item)
                    }
                }
            }
        }
    }

}