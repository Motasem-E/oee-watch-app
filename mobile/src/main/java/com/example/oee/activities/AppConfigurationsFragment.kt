package com.example.oee.activities

import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import com.example.oee.R
import com.example.oee.model.AppConfigurations
import com.example.oee.service.SyncListener
import com.example.oee.service.WearableSyncService
import com.google.android.gms.wearable.Wearable
import org.koin.android.ext.android.inject


class AppConfigurationsFragment : PreferenceFragmentCompat() {

    private lateinit var viewHolder: ViewHolder
    private val syncService: WearableSyncService by inject()
    private val configurations: AppConfigurations by inject()

    inner class ViewHolder {
        val httpMethod: SwitchPreference = findPreference("method")!!
        val ip: EditTextPreference = findPreference("ip")!!
        val port: EditTextPreference = findPreference("port")!!

        val selectedCells: ListPreference = findPreference("selected_cell")!!
        val selectedMachines: ListPreference = findPreference("selected_machine")!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.configurations, rootKey)

        viewHolder = ViewHolder()

        syncService.registerListener(object: SyncListener {
            override fun dataChanged() {
                viewHolder.httpMethod.isChecked = configurations.method == "https://"

                viewHolder.ip.text = configurations.ip
                viewHolder.ip.summary = configurations.ip

                viewHolder.port.text = configurations.port
                viewHolder.port.summary = configurations.port

                viewHolder.selectedCells.entries = configurations.cellsLabels.map { cell ->
                    val cellCharSequence: CharSequence = cell
                    cellCharSequence
                }.toTypedArray()

                viewHolder.selectedCells.entryValues = configurations.cellsLabels.mapIndexed { index, _  ->
                    index.toString()
                }.toTypedArray()

                viewHolder.selectedMachines.entries = configurations.machinesLabels.map { machine ->
                    val machineCharSequence: CharSequence = machine
                    machineCharSequence
                }.toTypedArray()

                viewHolder.selectedMachines.entryValues = configurations.machinesLabels.mapIndexed { index, _  ->
                    index.toString()
                }.toTypedArray()

                if(configurations.selectedCellIndex < configurations.cellsLabels.size) {
                    viewHolder.selectedCells.summary = configurations.cellsLabels[configurations.selectedCellIndex]
                }

                if(configurations.selectedMachineIndex < configurations.machinesLabels.size) {
                    viewHolder.selectedMachines.summary = configurations.machinesLabels[configurations.selectedMachineIndex]
                }
            }

            override fun onFailure(message: String) {
                Toast.makeText(this@AppConfigurationsFragment.context, message, Toast.LENGTH_SHORT).show()
            }
        })

        registerPreferencesChangeListeners()
    }

    private fun registerPreferencesChangeListeners() {
        viewHolder.httpMethod.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            configurations.method = if(newValue == true) "https://" else "http://"
            syncService.sendToWearable()
            true
        }
        viewHolder.ip.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            if(newValue != "") {
                configurations.ip = newValue as String
                syncService.sendToWearable()
                true
            } else {
                Toast.makeText(this@AppConfigurationsFragment.activity, "IP inválido", Toast.LENGTH_SHORT).show()
                false
            }
        }
        viewHolder.port.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            if(newValue != "") {
                configurations.port = newValue as String
                syncService.sendToWearable()
                true
            } else {
                Toast.makeText(this@AppConfigurationsFragment.activity, "porta invalida", Toast.LENGTH_SHORT).show()
                false
            }
        }

        viewHolder.selectedCells.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            try {
                val cellIndex = newValue.toString().toInt()
                configurations.selectedCellIndex = cellIndex
                syncService.sendToWearable()
                true
            } catch (_: NumberFormatException ) {
                Toast.makeText(this@AppConfigurationsFragment.activity, "Seleção invalida", Toast.LENGTH_SHORT).show()
                false
            }
        }

        viewHolder.selectedMachines.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            try {
                val machineIndex = newValue.toString().toInt()
                configurations.selectedMachineIndex = machineIndex
                syncService.sendToWearable()
                true
            } catch (_: NumberFormatException ) {
                Toast.makeText(this@AppConfigurationsFragment.activity, "Seleção invalida", Toast.LENGTH_SHORT).show()
                false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this.requireActivity()).addListener(syncService)
        syncService.requestSyncNow()
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this.requireActivity()).removeListener(syncService)
    }


}