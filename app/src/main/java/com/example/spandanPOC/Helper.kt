package com.example.spandanPOC

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer
import java.util.Base64

object Helper {
    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun main(args: Array<String>) {
        // Sample ArrayList
        val arrayList = ArrayList<String>()
        arrayList.add("Hello")
        arrayList.add("World")

        // Step 1: Convert ArrayList to byte array
//        val byteArray = convertDoubleArrayListToByteArray(arrayList)

        // Step 2: Encode byte array to Base64
//        val base64String = encodeToBase64(byteArray)
//
//        // Print the result
//        println("Base64 String: $base64String")
    }

    fun convertDoubleArrayListToByteArray(doubleArrayList: ArrayList<Double>): ByteArray {
        // Convert the list of Doubles to a ByteArray using ByteBuffer
        val byteBuffer = ByteBuffer.allocate(doubleArrayList.size * 8)
        for (value in doubleArrayList) {
            byteBuffer.putDouble(value)
        }
        return byteBuffer.array()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeToBase64(byteArray: ByteArray): String {
        return Base64.getEncoder().encodeToString(byteArray)
    }

    fun <T> convertListToBase64String(list: List<T>): String {
        var result = list.joinToString(separator = ",")
        var base64String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getEncoder().encodeToString(result.toByteArray())
        } else {
            android.util.Base64.encodeToString(result.toByteArray(), android.util.Base64.DEFAULT)
        }
        return base64String
    }
}

