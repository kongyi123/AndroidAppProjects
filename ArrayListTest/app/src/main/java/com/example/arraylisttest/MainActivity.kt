package com.example.arraylisttest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val arrayList = ArrayList<Aaa>()
        arrayList.add(Aaa())
        arrayList.add(Aaa())

        Log.i("kongyi1220", "$arrayList")

    }

    inner class Aaa() {

    }
}
