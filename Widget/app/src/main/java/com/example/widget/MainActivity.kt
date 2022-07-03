package com.example.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mv = findViewById<TextView>(R.id.myView)

        findViewById<Button>(R.id.updateButton).setOnClickListener {
            val bitmap = getBitmapFromView(mv)
            val bd = BitmapDrawable(resources, bitmap)
            val wp = WidgetProvider()
            wp.update(this, bd.bitmap)
        }

    }

    private fun getBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888);
        val c = Canvas(b);
        v.draw(c);
        return b;
    }
}