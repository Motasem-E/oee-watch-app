package com.example.oee.service

import android.content.Context
import com.example.oee.constants.Constants
import com.example.oee.repository.AppConfigurationsRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*

interface OnDataChanged {
    fun dataChanged()
}

class WearableSyncService (private val configurationsRepository: AppConfigurationsRepository,
                           private val apiService: ApiService,
                           private val context: Context): DataClient.OnDataChangedListener {

    private val dataClient by lazy { Wearable.getDataClient(context) }

    private var listener: OnDataChanged? = null;

    // flag para quando atualizar as celulas, atualizar as máquinas depois da chamada do API
    var requireSync = false


    fun registerListener(listener: OnDataChanged) {
        this.listener = listener
    }

    fun sendToMobile() {
        val putDataReq: PutDataRequest = PutDataMapRequest.create(Constants.CONFIGURATIONS_PATH).setUrgent().run {
            dataMap.putString(Constants.CONFIGURATIONS_METHOD_KEY, configurationsRepository.getMethod())
            dataMap.putString(Constants.CONFIGURATIONS_IP_KEY, configurationsRepository.getIp())
            dataMap.putString(Constants.CONFIGURATIONS_PORT_KEY, configurationsRepository.getPort())

            dataMap.putStringArrayList(Constants.CONFIGURATIONS_CELLS_KEY, ArrayList(configurationsRepository.getCellsLabelsList()))
            dataMap.putStringArrayList(Constants.CONFIGURATIONS_MACHINES_KEY, ArrayList(configurationsRepository.getMachinesLabelsList()))

            dataMap.putInt(Constants.CONFIGURATIONS_SELECTED_CELL_KEY, configurationsRepository.getSelectedCellIndex())
            dataMap.putInt(Constants.CONFIGURATIONS_SELECTED_MACHINE_KEY, configurationsRepository.getSelectedMachineIndex())

            // Always update message
            dataMap.putString("timestamp", System.currentTimeMillis().toString())

            asPutDataRequest()
        }
        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)

        putDataTask.addOnFailureListener(context.mainExecutor, OnFailureListener {
            System.err.println(it.message + "###################################")
            //Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
        })
    }

    private fun updateConfigurations(item: DataItem) {
        DataMapItem.fromDataItem(item).dataMap.apply {
            val receivedCellIndex = getInt(Constants.CONFIGURATIONS_SELECTED_CELL_KEY)!!
            val currentCellIndex = configurationsRepository.getSelectedCellIndex()

            configurationsRepository.setMethod(getString(Constants.CONFIGURATIONS_METHOD_KEY)!!)
            configurationsRepository.setIp(getString(Constants.CONFIGURATIONS_IP_KEY)!!)
            configurationsRepository.setPort(getString(Constants.CONFIGURATIONS_PORT_KEY)!!)

            configurationsRepository.setSelectedCellIndex(receivedCellIndex)
            configurationsRepository.setSelectedMachineIndex(getInt(Constants.CONFIGURATIONS_SELECTED_MACHINE_KEY)!!)

            if (receivedCellIndex != currentCellIndex) {
                requireSync = true
            }

            this@WearableSyncService.apiService.updateInstance()

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
                        // println("Configuration Data Received ####################")
                        this.updateConfigurations(item)
                    } else if (item.uri.path?.compareTo(Constants.SYNC_PATH) == 0) {
                        // println("SYNC REQUEST ##############################")
                        this.sendToMobile()
                    }
                }
            }
        }
    }

}