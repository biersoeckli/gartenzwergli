package ch.ost.gartenzwergli.ui.home

data class CropEvent(val title: String, val cropName: String, val time: String) {

    override fun toString(): String {
        return "CropEvent(title='$title', cropName='$cropName', time='$time')"
    }
}
