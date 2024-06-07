package com.example.spandanPOC

data class Conclusion(
    val baseline_wandering: Boolean,
    val detection: String,
    val ecg_type: String,
    val p_wave_type: String,
    val power_line_interference: Boolean,
    val qrs_type: String
)