package com.example.beatopoc

import android.app.ProgressDialog
import `in`.sunfox.healthcare.commons.android.spandan_sdk.SpandanSDK
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTest
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTestCallback
import `in`.sunfox.healthcare.commons.android.spandan_sdk.conclusion.EcgReport
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.DeviceConnectionState
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgPosition
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgTestType
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.beatopoc.databinding.ActivityTwelveLeadTestBinding
import `in`.sunfox.healthcare.commons.android.spandan_sdk.OnApiReportGenerationListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.DeviceInfo
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.OnDeviceConnectionStateChangeListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.EcgApiResult
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.EcgReportApiInput

class TwelveLeadTestActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTwelveLeadTestBinding

    private lateinit var spandanSDK: SpandanSDK
    private var ecgPoints : HashMap<EcgPosition,ArrayList<Double>> = hashMapOf()
    private var ecgReport : EcgReport? = null
    private var ecgApiResult:EcgApiResult? = null
    private lateinit var ecgTest:EcgTest
    private lateinit var ecgPosition: EcgPosition
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_twelve_lead_test)

        progressDialog = ProgressDialog(this)

        try{
            binding.activityMainTextviewCurrentPosition.text = "Please select the lead"

            spandanSDK = SpandanSDK.getInstance()

            /**
             * step :-2
             * set callback for device connectivity.*/
            spandanSDK.setOnDeviceConnectionStateChangedListener(object : OnDeviceConnectionStateChangeListener{
                override fun onDeviceConnectionStateChanged(deviceConnectionState: DeviceConnectionState) {
                    when(deviceConnectionState){
                        DeviceConnectionState.DISCONNECTED -> { binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
                            Color.RED) }
                        DeviceConnectionState.CONNECTED -> {  }
                        DeviceConnectionState.VERIFICATION_TIME_OUT -> {}
                        DeviceConnectionState.USB_CONNECTION_PERMISSION_DENIED -> {}
                    }
                }

                override fun onDeviceTypeChange(deviceType: String) {

                }

                override fun onDeviceVerified(deviceInfo: DeviceInfo) {

                }

            })
//            spandanSDK.setOnDeviceConnectionStateChangedListener {
//                when(it){
//                    DeviceConnectionState.DISCONNECTED -> { binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
//                        Color.RED) }
//                    DeviceConnectionState.CONNECTED -> {  }
//                    DeviceConnectionState.VERIFIED -> { binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
//                        Color.GREEN) }
//                    DeviceConnectionState.VERIFICATION_TIME_OUT -> {}
//                    DeviceConnectionState.USB_CONNECTION_PERMISSION_DENIED -> {}
//                }
//            }
            /**
             * step :-3
             * create ecg test..*/
            ecgTest = spandanSDK.createTest(EcgTestType.TWELVE_LEAD,object : EcgTestCallback{
                override fun onTestFailed(statusCode: Int) {

                }

                override fun onTestStarted(ecgPosition: EcgPosition) {

                }

                override fun onElapsedTimeChanged(elapsedTime: Long, remainingTime: Long) {
                    binding.activityMainProgressbarTestStatus.progress = elapsedTime.toInt()
                    when(ecgPosition){
                        EcgPosition.V1 -> {
                            binding.progressBar.progress = elapsedTime.toInt()
                        }
                        EcgPosition.V2 -> {
                            binding.progressBar2.progress = elapsedTime.toInt()
                        }
                        EcgPosition.V3 -> {
                            binding.progressBar3.progress = elapsedTime.toInt()
                        }
                        EcgPosition.V4 -> {
                            binding.progressBar4.progress = elapsedTime.toInt()
                        }
                        EcgPosition.V5 -> {
                            binding.progressBar5.progress = elapsedTime.toInt()
                        }
                        EcgPosition.V6 -> {
                            binding.progressBar6.progress = elapsedTime.toInt()
                        }
                        EcgPosition.LEAD_2 -> {
                            binding.progressBar8.progress = elapsedTime.toInt()
                        }
                        EcgPosition.LEAD_1 -> {
                            binding.progressBar7.progress = elapsedTime.toInt()
                        }
                    }
                }

                override fun onReceivedData(data: String) {
                    runOnUiThread {
                        binding.activityMainTextviewDataRecieved.text = data
                    }
                }

                override fun onPositionRecordingComplete(
                    ecgPosition: EcgPosition,
                    ecgPoints: ArrayList<Double>?
                ) {
                    runOnUiThread {
                        Toast.makeText(this@TwelveLeadTestActivity,"lead completed",Toast.LENGTH_SHORT).show()
                    }
                    if(ecgPoints!=null)
                        this@TwelveLeadTestActivity.ecgPoints[ecgPosition] = ecgPoints
                }

            },(application as ApplicationClass).token!!)

            binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(if(SpandanSDK.getInstance().isDeviceConnected()) Color.GREEN else Color.RED)

            binding.progressBar.setOnClickListener {
                ecgPosition = EcgPosition.V1
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.V1.name
            }

            binding.progressBar2.setOnClickListener {
                ecgPosition = EcgPosition.V2
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.V2.name
            }


            binding.progressBar3.setOnClickListener {
                ecgPosition = EcgPosition.V3
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.V3.name
            }
            binding.progressBar4.setOnClickListener {
                ecgPosition = EcgPosition.V4
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.V4.name
            }
            binding.progressBar5.setOnClickListener {
                ecgPosition = EcgPosition.V5
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.V5.name
            }
            binding.progressBar6.setOnClickListener {
                ecgPosition = EcgPosition.V6
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.V6.name
            }
            binding.progressBar7.setOnClickListener {
                ecgPosition = EcgPosition.LEAD_1
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.LEAD_1.name
            }
            binding.progressBar8.setOnClickListener {
                ecgPosition = EcgPosition.LEAD_2
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.LEAD_2.name
            }

            /***
             * step :-4
             * start ecg test.*/
            binding.activityMainBtnStartTest.setOnClickListener {
                if(!SpandanSDK.getInstance().isDeviceConnected())
                    Toast.makeText(this,"Please connect the device first.", Toast.LENGTH_SHORT).show()
                else if(!::ecgPosition.isInitialized)
                    Toast.makeText(this@TwelveLeadTestActivity,"please select any lead", Toast.LENGTH_SHORT).show()
                else
                    ecgTest.start(ecgPosition)
            }


            /**
             * step :-5
             * generate ecg report*/
            binding.activityMainBtnGenerateReport.setOnClickListener {
                showProgressDialog()
//                spandanSDK.generateReport(12,ecgPoints,(application as BeatoApplication).token!!,object : OnReportGenerationStateListener{
//                    override fun onReportGenerationSuccess(ecgReport: EcgReport,pdfInputStream:InputStream?) {
//                        this@TwelveLeadTestActivity.ecgReport = ecgReport
//                        runOnUiThread {
//                            Toast.makeText(this@TwelveLeadTestActivity,"report generated",Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onReportGenerationFailed(errorCode: Int, errorMsg: String) {
//                        runOnUiThread {
//                            Toast.makeText(this@TwelveLeadTestActivity,errorMsg,Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                })
                spandanSDK.generateReportWithPdf(
                    verifierKey = "enter verifier token", apiKey =  "enter api key",
                    ecgReportApiInput = EcgReportApiInput(
                        age = "34",
                        first_name = "first",
                        last_name = "last",
                        gender = "Male",
                        generate_pdf_report = true,
                        height = "147",
                        weight = "60",
                        lead2_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.LEAD_2]!!),
                        lead1_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.LEAD_1]!!),
                        v1_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.V1]!!),
                        v2_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.V2]!!),
                        v3_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.V3]!!),
                        v4_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.V4]!!),
                        v5_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.V5]!!),
                        v6_data = Helper.convertListToBase64String(ecgPoints[EcgPosition.V6]!!),
                        processor_type = "TWELVE_LEAD"
                    ),
                    onApiReportGenerationListener = object : OnApiReportGenerationListener {
                        override fun onReportGenerationFailed(errorMsg: String) {
                            hideProgressDialog()
                            binding.pdfLinkUrl.text = errorMsg
                        }

                        override fun onReportGenerationSuccess(ecgReport: EcgApiResult) {
                            this@TwelveLeadTestActivity.ecgApiResult = ecgReport
                            runOnUiThread {
                                hideProgressDialog()
                                binding.pdfLinkUrl.text = ecgReport.url
                            }
                        }

                    }
                )
            }

            binding.activityMainBtnShowConclusion.setOnClickListener {
                ecgApiResult?.let {
//                    val conclusion = (ecgReport?.conclusion as LeadTwoConclusion)
//                    val characteristics = ecgReport?.ecgCharacteristics
//                    binding.result.text = "$conclusion $characteristics"
//                    val conclusion = (ecgApiResult?.conclusions as LeadTwoConclusion)
                    val conclusion = (ecgApiResult?.conclusions)
                    val characteristics = ecgApiResult?.characteristics
                    binding.reportConclusion.text = "$conclusion $characteristics"
                }
            }
        }
        catch (e:Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }

    }

    private fun showProgressDialog() {
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Generating report....")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    private fun hideProgressDialog(){
        progressDialog.dismiss()
    }
}