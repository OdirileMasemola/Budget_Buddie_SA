package com.example.budget_buddie_sa

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

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
                ivFullImage.setImageURI(Uri.parse(imageUriString))
            } catch (e: Exception) {0
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
