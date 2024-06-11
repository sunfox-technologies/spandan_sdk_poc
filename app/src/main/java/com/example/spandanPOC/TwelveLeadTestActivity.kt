package com.example.spandanPOC

import android.app.ProgressDialog
import `in`.sunfox.healthcare.commons.android.spandan_sdk.SpandanSDK
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTest
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTestCallback
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgPosition
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgTestType
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.spandanPOC.databinding.ActivityTwelveLeadTestBinding
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.DeviceInfo
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.OnDeviceConnectionStateChangeListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.listener.PDFReportGenerationCallback
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.PatientData
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.ReportGenerationResult

class TwelveLeadTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTwelveLeadTestBinding

    private lateinit var spandanSDK: SpandanSDK
    private var ecgPoints: HashMap<EcgPosition, ArrayList<Double>> = hashMapOf()
    private var ecgApiResult: ReportGenerationResult? = null
    private lateinit var ecgTest: EcgTest
    private lateinit var ecgPosition: EcgPosition
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_twelve_lead_test)

        progressDialog = ProgressDialog(this)

        try {
            binding.activityMainTextviewCurrentPosition.text = "Please select the lead"

            spandanSDK = SpandanSDK.getInstance()

            /**
             * step :-2
             * set callback for device connectivity.*/
            spandanSDK.setOnDeviceConnectionStateChangedListener(object :
                OnDeviceConnectionStateChangeListener {
                override fun onConnectionTimedOut() {}

                override fun onDeviceAttached() {}

                override fun onDeviceConnected(deviceInfo: DeviceInfo) {}

                override fun onDeviceDisconnected() {}

                override fun onUsbPermissionDenied() {}
            })
            /**
             * step :-3
             * create ecg test..*/
            try {
                createNewEcgTest()
            }catch (e:Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }


            binding.createNewTest.setOnClickListener {
                try {
                    createNewEcgTest()
                }catch (e:Exception){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
                if (SpandanSDK.getInstance().isDeviceConnected()) Color.GREEN else Color.RED
            )

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
                try {
                    if (!SpandanSDK.getInstance().isDeviceConnected())
                        Toast.makeText(this, "Please connect the device first.", Toast.LENGTH_SHORT)
                            .show()
                    else if (!::ecgPosition.isInitialized)
                        Toast.makeText(
                            this@TwelveLeadTestActivity,
                            "please select any lead",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        ecgTest.start(ecgPosition)
                }catch (e:Exception){
                    binding.reportConclusion.text = e.toString()
                }
            }


            binding.validateTest.setOnClickListener {
                ecgTest.completeTest()
                binding.activityMainBtnGenerateReport.visibility = View.VISIBLE
            }
            /**
             * step :-5
             * generate ecg report*/
            binding.activityMainBtnGenerateReport.setOnClickListener {
                try {
                    showProgressDialog()
                    spandanSDK.generatePdfReport(
                        ecgTest = ecgTest,
                            patientData = PatientData(
                                age = "34",
                                firstName = "first",
                                lastName = "last",
                                gender = "Male",
                                height = "147",
                                weight = "60",
                            ),
                        pdfReportGenerationCallback = object : PDFReportGenerationCallback {
                            override fun onReportGenerationFailed(errorMsg: String) {
                                hideProgressDialog()
                                binding.pdfLinkUrl.text = errorMsg
                            }

                            override fun onReportGenerationSuccess(reportGenerationResult: ReportGenerationResult) {
                                this@TwelveLeadTestActivity.ecgApiResult = reportGenerationResult
                                runOnUiThread {
                                    hideProgressDialog()
                                    binding.activityMainBtnShowConclusion.visibility = View.VISIBLE
                                    binding.pdfLinkUrl.text = reportGenerationResult.pdfReportUrl
                                }
                            }

                        }
                    )
                }catch (e:Exception){
                    hideProgressDialog()
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            binding.activityMainBtnShowConclusion.setOnClickListener {
                ecgApiResult?.let {
                    val conclusion = it.conclusions
                    val characteristics = it.characteristics
                    binding.reportConclusion.text = "$characteristics $conclusion"
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun createNewEcgTest() {
        ecgTest = spandanSDK.createTest(EcgTestType.TWELVE_LEAD, object : EcgTestCallback {
            override fun onTestFailed(statusCode: Int) {

            }

            override fun onTestStarted(ecgPosition: EcgPosition) {

            }

            override fun onEcgTestCompleted(hashMap: HashMap<EcgPosition, ArrayList<Double>>) {

            }

            override fun onElapsedTimeChanged(elapsedTime: Long, remainingTime: Long) {
                binding.activityMainProgressbarTestStatus.progress = elapsedTime.toInt()
                when (ecgPosition) {
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

            override fun onPositionRecordingCompleted(
                ecgPosition: EcgPosition,
                ecgPoints: ArrayList<Double>?,
            ) {
                runOnUiThread {
                    Toast.makeText(
                        this@TwelveLeadTestActivity,
                        "lead completed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (ecgPoints != null)
                    this@TwelveLeadTestActivity.ecgPoints[ecgPosition] = ecgPoints
            }

        }
        )
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
}