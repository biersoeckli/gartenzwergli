package ch.ost.gartenzwergli.model.converters

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    companion object {
        const val STRING_SEPARATOR = "||"
    }

    @TypeConverter
    fun List<String>.toStringData() = this.joinToString(STRING_SEPARATOR)

    @TypeConverter
    fun String.toStringList() = this.split(STRING_SEPARATOR)
}