package ch.ost.gartenzwergli.utils

import android.content.Context
import ch.ost.gartenzwergli.model.dbo.CropDbo

class CropDboUtils {
    companion object {

        fun getCropImagesPath(ctx: Context): String {
            return ctx.filesDir.absolutePath.plus("/cropImages")
        }

        fun getCropImageFileName(cropDbo: CropDbo): String {
            if (cropDbo.thumbnailUrl == null) {
                throw Exception("ThumbnailUrl of crop is null")
            }
            var fileSuffix = cropDbo.thumbnailUrl.substringAfterLast(".")
            if (fileSuffix.contains("?")) {
                fileSuffix = fileSuffix.substringBefore("?")
            }
            val fileName = cropDbo.id + ".$fileSuffix"
            return fileName
        }
    }
}