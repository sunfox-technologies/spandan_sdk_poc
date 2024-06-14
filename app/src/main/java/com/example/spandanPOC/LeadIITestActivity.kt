package com.example.spandanPOC

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.spandanPOC.databinding.ActivityLeadIitestBinding
import `in`.sunfox.healthcare.commons.android.spandan_sdk.SpandanSDK
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTest
import `in`.sunfox.healthcare.commons.android.spandan_sdk.collection.EcgTestCallback
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.DeviceInfo
import `in`.sunfox.healthcare.commons.android.spandan_sdk.connection.OnDeviceConnectionStateChangeListener
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgPosition
import `in`.sunfox.healthcare.commons.android.spandan_sdk.enums.EcgTestType
import `in`.sunfox.healthcare.commons.android.spandan_sdk.listener.PDFReportGenerationCallback
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.PatientData
import `in`.sunfox.healthcare.commons.android.spandan_sdk.retrofit_helper.ReportGenerationResult

class LeadIITestActivity : AppCompatActivity(), EcgTestCallback,
    OnDeviceConnectionStateChangeListener {
    private lateinit var binding: ActivityLeadIitestBinding
    private lateinit var spandanSDK: SpandanSDK
    private var ecgPoints: HashMap<EcgPosition, ArrayList<Double>> = hashMapOf()
    private var ecgApiResult: ReportGenerationResult? = null
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
            } catch (e: Exception) {
                binding.createNewTest.visibility = View.VISIBLE
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }

            binding.createNewTest.setOnClickListener {
//                finish()
//                startActivity(intent)
                ecgTest = spandanSDK.createTest(EcgTestType.LEAD_TWO, this)
                binding.progressBar8.progress = 0
                binding.pdfLinkUrl.text = ""
                binding.result.text = ""
            }


            binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
                if (SpandanSDK.getInstance().isDeviceConnected()) Color.GREEN else Color.RED
            )


            binding.completeTheTest.setOnClickListener {
                try {
                    ecgTest.completeTest()
                    binding.result.text = ""
                    binding.activityMainBtnGenerateReport.visibility = View.VISIBLE
                } catch (e: Exception) {
                    binding.result.text = e.toString()
                }
            }

            binding.progressBar8.setOnClickListener {}

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
                    else {
                        Log.w("TEST_TAG", "onCreate: ${ecgTest.hashCode()}")
                        ecgTest.start(ecgPosition)
                        binding.result.text = ""
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    binding.result.text = e.toString()
                }
            }


            /**
             * step :-5
             * generate ecg report*/
            binding.activityMainBtnGenerateReport.setOnClickListener {
                Log.d("SdkImpl.TAG", "onCreate: impl $ecgPoints")
                showProgressDialog()
                try {
                    spandanSDK.generatePdfReport(
                        ecgTest = ecgTest,
                        patientData =
                        PatientData(
                            age = "134",
                            firstName = "first",
                            lastName = "last",
                            gender = "Male",
                            height = "300",
                            weight = "60"
                        ),
                        pdfReportGenerationCallback = object : PDFReportGenerationCallback {
                            override fun onReportGenerationSuccess(reportGenerationResult: ReportGenerationResult) {
                                this@LeadIITestActivity.ecgApiResult = reportGenerationResult
                                runOnUiThread {
                                    binding.activityMainBtnShowConclusion.visibility = View.VISIBLE
                                    binding.createNewTest.visibility = View.VISIBLE
                                    binding.pdfLinkUrl.text = reportGenerationResult.pdfReportUrl
                                    hideProgressDialog()
                                }
                            }

                            override fun onReportGenerationFailed(errorMsg: String) {
                                runOnUiThread {
                                    hideProgressDialog()
                                    Toast.makeText(
                                        this@LeadIITestActivity,
                                        errorMsg,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    binding.result.text = errorMsg
                                }
                            }
                        })
                } catch (e: Exception) {
                    hideProgressDialog()
                    binding.result.text = e.toString()
                }
            }


            binding.activityMainBtnShowConclusion.setOnClickListener {
                ecgApiResult?.let {
                    val conclusion = (it?.conclusions)
                    val characteristics = ecgApiResult?.characteristics
                    binding.result.text = "$conclusion $characteristics"
                }
            }
            binding.result.text = ""
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

    override fun onConnectionTimedOut() {

        binding.activityMainTextviewTestStatus.text = "timeout"
    }

    override fun onDeviceAttached() {

        binding.activityMainTextviewTestStatus.text = "attach"
    }

    override fun onDeviceConnected(deviceInfo: DeviceInfo) {

        binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
            Color.GREEN
        )
        binding.result.text = "$deviceInfo"
    }

    override fun onDeviceDisconnected() {

        binding.activityMainLayoutDeviceConnectionStatus.setBackgroundColor(
            Color.RED
        )
        binding.activityMainTextviewTestStatus.text = "disconnect"
    }

    override fun onUsbPermissionDenied() {

        binding.activityMainTextviewTestStatus.text = "denied"
    }

    override fun onTestFailed(statusCode: Int) {
        Toast.makeText(this@LeadIITestActivity, "onTestFailed $statusCode", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onTestStarted(ecgPosition: EcgPosition) {
        Toast.makeText(this@LeadIITestActivity, "test started", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onEcgTestCompleted(hashMap: HashMap<EcgPosition, ArrayList<Double>>) {
        Toast.makeText(this@LeadIITestActivity, "test completed...", Toast.LENGTH_SHORT).show()
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

    override fun onPositionRecordingCompleted(
        ecgPosition: EcgPosition,
        ecgPoints: ArrayList<Double>?,
    ) {
        this@LeadIITestActivity.ecgPoints[ecgPosition] = ecgPoints!!
        Toast.makeText(this@LeadIITestActivity, "${ecgPoints.size}", Toast.LENGTH_SHORT).show()
        this@LeadIITestActivity.ecgPoints[ecgPosition] = ecgPoints
        binding.completeTheTest.visibility = View.VISIBLE
    }

}