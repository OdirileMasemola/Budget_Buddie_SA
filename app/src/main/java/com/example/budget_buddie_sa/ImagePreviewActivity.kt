package com.example.budget_buddie_sa

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
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

        val imageUrl = intent.getStringExtra("imageUrl")
        Log.d("ImagePreviewActivity", "Received imageUrl: $imageUrl")
        
        if (!imageUrl.isNullOrEmpty()) {
            try {
                Glide.with(this)
                    .load(imageUrl)
                    .fitCenter()
                    .into(ivFullImage)
            } catch (e: Exception) {
                Log.e("ImagePreviewActivity", "Error loading image: ${e.message}")
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Log.e("ImagePreviewActivity", "imageUrl is null or empty")
            Toast.makeText(this, "Image path is missing", Toast.LENGTH_SHORT).show()
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
