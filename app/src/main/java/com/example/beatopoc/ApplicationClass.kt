package com.example.beatopoc


import `in`.sunfox.healthcare.commons.android.spandan_sdk.OnInitializationCompleteListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.SpandanSDK
import android.app.Application
import android.util.Log

class ApplicationClass:Application() {

    var token : String?=null

    /**
     * step :-1
     * sdk initialization*/
    override fun onCreate() {
        super.onCreate()
        SpandanSDK.initialize(this@ApplicationClass,apiKey = "enter api key", verifierToken = "enter verifier token", onInitializationCompleteListener = object : OnInitializationCompleteListener{
            override fun onInitializationSuccess(authenticationToken: String) {

                token = authenticationToken
            }

            override fun onInitializationFailed(message: String) {
                Log.d("BuldToken.TAG", "onInitializationSuccess: $message")
            }

        })
    }
}