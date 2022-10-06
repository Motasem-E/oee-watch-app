package com.example.oee.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.oee.R
import com.example.oee.service.WearableSyncService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val syncService: WearableSyncService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, AppConfigurationsFragment())
            .commit()

       findViewById<FloatingActionButton>(R.id.bt_sync).setOnClickListener{
           syncService.requestSyncNow();
       }
    }
}