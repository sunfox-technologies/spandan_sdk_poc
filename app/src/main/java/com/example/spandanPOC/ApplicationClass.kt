package com.example.spandanPOC


import `in`.sunfox.healthcare.commons.android.spandan_sdk.OnInitializationCompleteListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.SpandanSDK
import android.app.Application
import android.widget.Toast

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