package ch.ost.gartenzwergli

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ch.ost.gartenzwergli.databinding.ActivityGardenCameraPreviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class GardenCameraPreviewActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var viewBinding: ActivityGardenCameraPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGardenCameraPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        launch {
            setupPreviewImage(viewBinding.root.context)
        }
    }

    private fun setupPreviewImage(context: Context) {
        val previewImage: ImageView = viewBinding.previewImage
        val extraImageUri = intent.getStringExtra(EXTRA_IMAGE_PATH)

        Log.d(TAG, "extraImageUri: $extraImageUri")

        val bitmap = getCapturedImage(Uri.parse(extraImageUri))
        previewImage.setImageBitmap(bitmap)
    }

    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                selectedPhotoUri
            )

            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }

    companion object {
        val EXTRA_IMAGE_PATH: String = "EXTRA_IMAGE_PATH"
        val TAG: String = "GardenCameraPreviewActivity"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}