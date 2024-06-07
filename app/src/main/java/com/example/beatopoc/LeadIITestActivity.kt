package com.example.beatopoc

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.beatopoc.databinding.ActivityLeadIitestBinding
import `in`.sunfox.healthcare.commons.android.spandan_sdk.SpandanSDK
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTest
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTestCallback
import `in`.sunfox.healthcare.commons.android.spandan_sdk.conclusion.EcgReport
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.DeviceInfo
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.OnDeviceConnectionStateChangeListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.DeviceConnectionState
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgPosition
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgTestType
import `in`.sunfox.healthcare.commons.android.spandan_sdk.PDFReportGenerationCallback
import `in`.sunfox.healthcare.commons.android.spandan_sdk.model.GenerateReportModel
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.GenerateReportResult
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.PatientData
import `in`.sunfox.healthcare.java.commons.ecg_processor.conclusions.conclusion.LeadTwoConclusion

class LeadIITestActivity : AppCompatActivity(), EcgTestCallback,
    OnDeviceConnectionStateChangeListener {
    private lateinit var binding: ActivityLeadIitestBinding
    private lateinit var spandanSDK: SpandanSDK
    private var ecgPoints: HashMap<EcgPosition, ArrayList<Double>> = hashMapOf()
    private var ecgReport: EcgReport? = null
    private var ecgApiResult: GenerateReportResult? = null
    private lateinit var ecgTest: EcgTest
    private lateinit var ecgPosition: EcgPosition
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lead_iitest)

        binding.activityMainTextviewCurrentPosition.text = "Please select the lead"

        progressDialog = ProgressDialog(this)
        try {
            spandanSDK = SpandanSDK.getInstance()

            /**
             * step :-2
             * set callback for device connectivity.*/
            spandanSDK.setOnDeviceConnectionStateChangedListener(this)
            /**
             * step :-3
             * create ecg test..*/
            try {
                ecgTest = spandanSDK.createTest(
                    EcgTestType.LEAD_TWO, this
                )
            }catch (e:Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }

            binding.createNewTest.setOnClickListener {
                try {
                    ecgTest = spandanSDK.createTest(
                        EcgTestType.LEAD_TWO,this
//                    ,(application as ApplicationClass).token!!
                    )
                }catch (e:Exception){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }


            binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
                if (SpandanSDK.getInstance().isDeviceConnected()) Color.GREEN else Color.RED
            )


            binding.validateTest.setOnClickListener {
                ecgTest.completeEcgTest()
                binding.activityMainBtnGenerateReport.visibility = View.VISIBLE
            }

            binding.progressBar8.setOnClickListener {
                //ecgPosition = EcgPosition.LEAD_2
                //binding.activityMainTextviewCurrentPosition.text = EcgPosition.LEAD_2.name
            }

            /***
             * step :-4
             * start ecg test.*/
            binding.activityMainBtnStartTest.setOnClickListener {
                ecgPosition = EcgPosition.LEAD_2
                binding.activityMainTextviewCurrentPosition.text = EcgPosition.LEAD_2.name

                try {
                    if (!SpandanSDK.getInstance().isDeviceConnected())
                        Toast.makeText(this, "Please connect the device first.", Toast.LENGTH_SHORT)
                            .show()
                    else if (!::ecgPosition.isInitialized)
                        Toast.makeText(this, "please select any lead", Toast.LENGTH_SHORT).show()
                    else
                        ecgTest.start(ecgPosition)
                }catch (e:Exception){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    binding.result.text = e.toString()
//                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }


            /**
             * step :-5
             * generate ecg report*/
            binding.activityMainBtnGenerateReport.setOnClickListener {
                Log.d("SdkImpl.TAG", "onCreate: impl $ecgPoints")
                showProgressDialog()
                try{
                    spandanSDK.generateReportWithPdf(
                        ecgTest = ecgTest,
                        inputForGenerateReport =
                        GenerateReportModel(
                            patientData = PatientData(
                                age = "134",
                                firstName = "first",
                                lastName = "last",
                                gender = "Male",
                                height = "147",
                                weight = "60"
                            ),
                            generatePdfReport = true,
                            processorType = "LEAD_TWO"
                        ),
                        pdfReportGenerationCallback = object : PDFReportGenerationCallback
                             {
                            override fun onReportGenerationSuccess(ecgReport: GenerateReportResult) {
                                this@LeadIITestActivity.ecgApiResult = ecgReport
                                runOnUiThread {
                                    binding.pdfLinkUrl.text = ecgReport.data.url
                                    hideProgressDialog()
                                }
                            }

                            override fun onReportGenerationFailed(errorMsg: String) {
                                runOnUiThread {
                                    hideProgressDialog()
                                    binding.pdfLinkUrl.text = errorMsg
                                }
                            }
                        })
                }catch (e:Exception){
                    hideProgressDialog()
                    binding.result.text = e.toString()
                }
            }


            binding.activityMainBtnShowConclusion.setOnClickListener {
                ecgApiResult?.let {
                    val conclusion = (ecgReport?.conclusion as LeadTwoConclusion)
                    val characteristics = ecgApiResult?.data?.characteristics
                    binding.result.text = "$conclusion $characteristics"
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            binding.result.text = e.toString()
        }
    }

    private fun showProgressDialog() {
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Generating report....")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    override fun onTestFailed(statusCode: Int) {
        Toast.makeText(this, "onTestFailed $statusCode", Toast.LENGTH_SHORT).show()
    }

    override fun onTestStarted(ecgPosition: EcgPosition) {
        Toast.makeText(this@LeadIITestActivity, "test started", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onEcgTestComplete(hashMap: HashMap<EcgPosition, ArrayList<Double>>) {
        Toast.makeText(this, "test completed...", Toast.LENGTH_SHORT).show()
    }


    override fun onElapsedTimeChanged(elapsedTime: Long, remainingTime: Long) {
        binding.activityMainProgressbarTestStatus.progress = elapsedTime.toInt()
        binding.progressBar8.progress = elapsedTime.toInt()
    }

    override fun onReceivedData(data: String) {
        runOnUiThread {
            binding.activityMainTextviewTestStatus.text = data
        }
    }
    override fun onPositionRecordingComplete(
        ecgPosition: EcgPosition,
        ecgPoints: ArrayList<Double>?,
    ) {
        this.ecgPoints[ecgPosition] = ecgPoints!!
        Toast.makeText(this, "${ecgPoints.size}", Toast.LENGTH_SHORT).show()
        this@LeadIITestActivity.ecgPoints[ecgPosition] = ecgPoints
    }

    override fun onDeviceConnectionStateChanged(deviceConnectionState: DeviceConnectionState) {
        when (deviceConnectionState) {
            DeviceConnectionState.DISCONNECTED -> {
                binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(Color.RED)
            }
            DeviceConnectionState.CONNECTED -> {}
            DeviceConnectionState.VERIFICATION_TIME_OUT -> {}
            DeviceConnectionState.USB_CONNECTION_PERMISSION_DENIED -> {}
        }
    }

    override fun onDeviceTypeChange(deviceType: String) {

    }

    override fun onDeviceVerified(deviceInfo: DeviceInfo) {
//        binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(if (SeriCom.isDeviceConnected()) Color.GREEN else Color.RED)
    }
}