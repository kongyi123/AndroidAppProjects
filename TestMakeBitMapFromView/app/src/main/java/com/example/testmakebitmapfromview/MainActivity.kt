package com.example.testmakebitmapfromview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mv = findViewById<TextView>(R.id.myView)
        val iv = findViewById<TextView>(R.id.imageView)
        // make view to background of view. (it must be done after when corresponding view is attached.)
        // so That's why implemented code is like below.
        mv.post {
            val bitmap = getBitmapFromView(mv)
            val bd = BitmapDrawable(resources, bitmap)
            iv.setBackgroundDrawable(bd)
        }

        // make bitmap file to background of view.
//        val bitmapFromFile = BitmapFactory.decodeResource(resources, R.drawable.aaa)
//        val bd = BitmapDrawable(resources, bitmapFromFile)
//        iv.setBackgroundDrawable(bd)
    }

    fun getBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888);
        val c = Canvas(b);
        v.draw(c);
        return b;
    }
}