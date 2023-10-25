package ch.ost.gartenzwergli.model.dto.openfarm

data class OpenFarmAttributesDto(
    val name: String,
    val slug: String,
    val binomial_name: String,
    val common_names: List<String>,
    val description: String?,
    val sun_requirements: String,
    val sowing_method: String,
    val spread: Int,
    val row_spacing: Int,
    val height: Int,
    val processing_pictures: Int,
    val guides_count: Int,
    val main_image_path: String,
    val taxon: String?,
    val tags_array: List<String>,
    val growing_degree_days: Int?,
    val svg_icon: String?
)