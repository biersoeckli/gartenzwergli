package ch.ost.gartenzwergli.model.dto.openfarm

data class OpenFarmIncludedAttributesDto(
    val id: String,
    val photographic_id: String,
    val image_url: String,
    val small_url: String,
    val thumbnail_url: String,
    val medium_url: String,
    val large_url: String,
    val canopy_url: String
)
