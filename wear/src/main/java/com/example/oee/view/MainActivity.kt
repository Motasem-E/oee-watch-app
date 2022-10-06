package com.example.oee.view

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.oee.R
import com.example.oee.databinding.ActivityMainBinding
import com.example.oee.model.oee.MachineOEE
import com.example.oee.service.WearableSyncService
import com.example.oee.utils.OnSwipeTouchListener
import com.example.oee.viewmodel.DashboardViewModel
import com.example.oee.viewmodel.ViewListener
import com.google.android.gms.wearable.Wearable
import org.koin.android.ext.android.inject

class MainActivity : Activity() {

    private val dashboardVM: DashboardViewModel by inject()
    private val wearableSyncService: WearableSyncService by inject()

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewHolder: ViewHolder

    inner class ViewHolder {
        var machineLabelTV: TextView private set
        var oeeTV: TextView private set
        var goodProductionTV: TextView private set
        var badProductionTV: TextView  private set

        var nextMachineBt: Button  private set
        var previousMachineBt: Button private set
        var refreshBt: Button private set

        init {
            machineLabelTV = this@MainActivity.findViewById(R.id.tv_machine)
            oeeTV = this@MainActivity.findViewById(R.id.tv_oee)
            goodProductionTV = this@MainActivity.findViewById(R.id.tv_good_production)
            badProductionTV = this@MainActivity.findViewById(R.id.tv_bad_production)

            nextMachineBt = this@MainActivity.findViewById(R.id.bt_next)
            previousMachineBt = this@MainActivity.findViewById(R.id.bt_previous)
            refreshBt = this@MainActivity.findViewById(R.id.bt_refresh)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewHolder = ViewHolder()

        registerListener()

        dashboardVM.refreshData()

        configureGesture()

    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(wearableSyncService)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(wearableSyncService)
    }

    private fun registerListener(){
        dashboardVM.registerListener(object: ViewListener{
            override fun onOeeData(data: MachineOEE) {
                viewHolder.machineLabelTV.text = data.label
                viewHolder.oeeTV.text = "${String.format("%.1f", data.oee)}%"
                viewHolder.goodProductionTV.text = "${data.goodProductionCounter}"
                viewHolder.badProductionTV.text = "${data.badProductionCounter}"
            }

            override fun onFailure(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun configureGesture() {
        viewHolder.nextMachineBt.setOnClickListener {
            dashboardVM.incrementSelectedMachineIndex()
            Toast.makeText(this@MainActivity, "Próxima Máquina...", Toast.LENGTH_SHORT).show();
        }

        viewHolder.previousMachineBt.setOnClickListener {
            dashboardVM.decrementSelectedMachineIndex()
            Toast.makeText(this@MainActivity, "Máquina Anterior...", Toast.LENGTH_SHORT).show();
        }

        viewHolder.refreshBt.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeTop() {
                dashboardVM.incrementSelectedCellIndex()
                Toast.makeText(this@MainActivity, "Próxima Celula...", Toast.LENGTH_SHORT).show();
            }
            override fun onSwipeBottom() {
                dashboardVM.decrementSelectedCellIndex()
                Toast.makeText(this@MainActivity, "Celula Anterior...", Toast.LENGTH_SHORT).show();
            }
            override fun onClick(){
                Toast.makeText(this@MainActivity, "Atualizando...", Toast.LENGTH_SHORT).show();
                dashboardVM.refreshData()
            }

        })
    }
}