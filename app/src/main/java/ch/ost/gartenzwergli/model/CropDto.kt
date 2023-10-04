package ch.ost.gartenzwergli.model

import com.google.gson.annotations.SerializedName


data class CropDto(
    val _index: String,
    val _type: String,
    val _id: String,
    val _score: Double,
    val name: String,
    val description: String,
    val slug: String,
    val alternate_names: List<String>,
    val scientific_names: List<String>,
    val photos_count: Int,
    val plantings_count: Int,
    val harvests_count: Int,
    val planters_ids: List<Int>,
    val has_photos: Boolean,
    val thumbnail_url: String,
    val scientific_name: String,
    val created_at: Long,
    val id: String

)