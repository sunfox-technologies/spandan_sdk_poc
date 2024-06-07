package com.example.spandanPOC


data class EcgProcessorInput(
    val age: String,
    val first_name: String,
    val gender: String,
    val generate_pdf_report: Boolean,
    val height: String,
    val last_name: String,
    val lead2_data: String,
    val processor_type: String,
    val weight: String
)