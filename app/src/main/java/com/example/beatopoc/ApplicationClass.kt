package com.example.beatopoc


import `in`.sunfox.healthcare.commons.android.spandan_sdk.OnInitializationCompleteListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.SpandanSDK
import android.app.Application
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class ApplicationClass : Application() {

    /**
     * step :-1
     * sdk initialization*/
    override fun onCreate() {
        super.onCreate()
        SpandanSDK.initialize(this,
            masterKey = BuildConfig.masterKey,
            verifierToken = BuildConfig.verifierToken,
            onInitializationCompleteListener = object : OnInitializationCompleteListener {
                override fun onInitializationSuccess() {
                    Toast.makeText(
                        this@ApplicationClass,
                        "token initialized successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onInitializationFailed(message: String) {
//                    throw Exception(message)
                }
            }
        )
    }
}