package com.example.spandanPOC

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import androidx.databinding.DataBindingUtil
import com.example.spandanPOC.databinding.ActivityMainBinding
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.twelveLeadTest.setOnClickListener {
//            if ((application as ApplicationClass).token == null) {
//                Toast.makeText(this, "token not initialized", Toast.LENGTH_SHORT).show()
//            } else
                startActivity(Intent(this, TwelveLeadTestActivity::class.java))
        }

        binding.leadIITest.setOnClickListener {
//            if ((application as ApplicationClass).token == null) {
//                Toast.makeText(this, "token not initialized", Toast.LENGTH_SHORT).show()
//            } else
                startActivity(Intent(this, LeadIITestActivity::class.java))
        }
    }

    private fun arrayListToBase64(arrayList: ArrayList<Int>): String {
        // Convert ArrayList to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(arrayList.toIntArray())
        objectOutputStream.close()
        val byteArray = byteArrayOutputStream.toByteArray()

        // Encode byte array to Base64 string
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToArrayList(base64String: String): ArrayList<Int> {
        // Decode Base64 string to byte array
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)

        // Convert byte array back to ArrayList
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        val doubleArray = objectInputStream.readObject() as IntArray
        val arrayList = ArrayList(doubleArray.toList())
        objectInputStream.close()

        return arrayList
    }
}