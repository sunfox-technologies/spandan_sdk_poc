package com.example.spandanPOC

data class ReportResponse(
    val ecgResult: EcgResult,
    val image_url: String,
    val subjectDetails: SubjectDetails
)