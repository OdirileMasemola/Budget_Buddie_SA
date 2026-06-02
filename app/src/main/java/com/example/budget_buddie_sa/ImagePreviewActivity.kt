package com.example.budget_buddie_sa

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

/**
 * Activity to show a full-screen preview of an image (Category or Receipt).
 */
class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        val ivFullImage = findViewById<ImageView>(R.id.ivFullImage)
        val btnClose = findViewById<View>(R.id.btnClose)

        val imageUriString = intent.getStringExtra("image_uri")
        
        if (!imageUriString.isNullOrEmpty()) {
            try {
                Glide.with(this)
                    .load(Uri.parse(imageUriString))
                    .fitCenter()
                    .into(ivFullImage)
            } catch (e: Exception) {
                e.printStackTrace()
                finish() // Close if image cannot be loaded
            }
        } else {
            finish()
        }

        btnClose.setOnClickListener {
            finish()
        }
        
        // Also allow clicking background to close
        findViewById<View>(R.id.rootLayout).setOnClickListener {
            finish()
        }
    }
}
