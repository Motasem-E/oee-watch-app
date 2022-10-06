package com.example.oee.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.oee.utils.ObjectSerializer
import org.json.JSONObject


class AppConfigurationsRepository(context: Context) {
    companion object {
        const val PREF_IP = "ip_key"
        const val PREF_PORT = "port_key"
        const val PREF_METHOD = "method_key"

        const val PREF_SELECTED_CELL_INDEX = "selected_cell_key"
        const val PREF_SELECTED_MACHINE_INDEX = "selected_machine_key"

        const val PREF_CELLS_LABELS = "cells_labels_key"
        const val PREF_MACHINES_LABELS = "machines_labels_key"
    }

    private val mPrefs:SharedPreferences

    init {
        mPrefs = context.getSharedPreferences("com.oee.app", Context.MODE_PRIVATE);
    }

    fun getIp(): String {
        return mPrefs.getString(PREF_IP, "192.168.1.196") ?: "192.168.1.196"
    }

    fun setIp(ip: String) {
        mPrefs.edit().putString(PREF_IP, ip).commit()
    }

    fun getPort(): String {
        return mPrefs.getString(PREF_PORT, "3001") ?: "3001"
    }

    fun setPort(port: String) {
        mPrefs.edit().putString(PREF_PORT, port).commit()
    }

    fun getMethod(): String {
        return mPrefs.getString(PREF_METHOD, "HTTP://") ?: "HTTP://"
    }

    fun setMethod(method: String) {
        mPrefs.edit().putString(PREF_METHOD, method).commit()
    }

    fun getSelectedCellIndex(): Int {
        return mPrefs.getInt(PREF_SELECTED_CELL_INDEX, 0)
    }

    fun setSelectedCellIndex(index: Int) {
        mPrefs.edit().putInt(PREF_SELECTED_CELL_INDEX, index).commit()
    }

    fun getCellsLabelsList(): ArrayList<String> {
        val cellsString =  mPrefs.getString(PREF_CELLS_LABELS, ObjectSerializer.serialize(ArrayList<String>()))
        return ObjectSerializer.deserialize(cellsString) as ArrayList<String>
    }

    fun setCellsLabelsList(cells: ArrayList<String> ) {
        val serializedString = ObjectSerializer.serialize(cells)
        mPrefs.edit().putString(PREF_CELLS_LABELS, serializedString).commit()
    }

    fun getMachinesLabelsList(): ArrayList<String> {
        val machinesString =  mPrefs.getString(PREF_MACHINES_LABELS, ObjectSerializer.serialize(ArrayList<String>()))
        return ObjectSerializer.deserialize(machinesString) as ArrayList<String>
    }

    fun setMachinesLabelsList(machines: ArrayList<String>) {
        val serializedString = ObjectSerializer.serialize(machines)
        mPrefs.edit().putString(PREF_MACHINES_LABELS, serializedString).commit()
    }

    fun getSelectedMachineIndex(): Int {
        return mPrefs.getInt(PREF_SELECTED_MACHINE_INDEX, 0)
    }

    fun setSelectedMachineIndex(index: Int) {
        mPrefs.edit().putInt(PREF_SELECTED_MACHINE_INDEX, index).commit()
    }
}