package ch.ost.gartenzwergli

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import ch.ost.gartenzwergli.databinding.ActivityGardenCameraPreviewBinding
import ch.ost.gartenzwergli.utils.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class GardenCameraPreviewActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var viewBinding: ActivityGardenCameraPreviewBinding
    private var extraImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGardenCameraPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        launch {
            setupPreviewImage()
        }

        viewBinding.use.setOnClickListener {
            onClickUseImage()
        }

        viewBinding.cancel.setOnClickListener {
            onClickRetakeImage()
        }
    }

    private fun setupPreviewImage() {
        val previewImage: ImageView = viewBinding.previewImage
        extraImageUri = intent.getStringExtra(EXTRA_IMAGE_PATH)

        Log.d(TAG, "extraImageUri: $extraImageUri")

        val bitmap = BitmapUtils().getCapturedImage(this.contentResolver, Uri.parse(extraImageUri))
        previewImage.setImageBitmap(bitmap)
    }

    private fun onClickUseImage() {
        val sharedPref = applicationContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.garden_background_image_key), extraImageUri.toString())
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openFragment", "GardenFragment")
        navigateUpTo(intent)
        finish()
    }

    private fun onClickRetakeImage() {
        // go back to camera activity
        finish()
        super.onBackPressed()
    }

    companion object {
        val EXTRA_IMAGE_PATH: String = "EXTRA_IMAGE_PATH"
        val TAG: String = "GardenCameraPreviewActivity"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}