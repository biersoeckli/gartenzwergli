package ch.ost.gartenzwergli.model.dto.growstuff

data class GrowstuffCropDetailDto(
    val id: Int,
    val name: String,
    val en_wikipedia_url: String,
    val created_at: String,
    val updated_at: String,
    val slug: String,
    val parent_id: Int?,
    val plantings_count: Int,
    val creator_id: Int,
    val requester_id: Int?,
    val approval_status: String,
    val reason_for_rejection: String?,
    val request_notes: String?,
    val rejection_notes: String?,
    val perennial: Boolean,
    val median_lifespan: Int?,
    val median_days_to_first_harvest: Int,
    val median_days_to_last_harvest: Int?,
    val openfarm_data: Any?,
    val harvests_count: Int,
    val photo_associations_count: Int,
    val plantings: List<GrowstuffCropDetailPlantingDto>?,
    val scientific_names: List<GrowstuffCropDetailScientificNameDto>?,
)

data class GrowstuffCropDetailOpenFarmDataDto(
    val id: String,
    val type: String,
    val links: GrowstuffCropDetailLinksDto,
    val attributes: GrowstuffCropDetailOpenFarmAttributesDto
)

data class GrowstuffCropDetailLinksDto(
    val self: GrowstuffCropDetailLinkDto,
    val website: GrowstuffCropDetailLinkDto
)

data class GrowstuffCropDetailLinkDto(
    val api: String,
    val website: String
)

data class GrowstuffCropDetailOpenFarmAttributesDto(
    val name: String,
    val slug: String,
    val taxon: String,
    val height: Int,
    val spread: Int,
    val svg_icon: String?,
    val tags_array: List<String>,
    val description: String,
    val row_spacing: Int,
    val common_names: List<String>,
    val guides_count: Int,
    val binomial_name: String,
    val sowing_method: String,
    val main_image_path: String,
    val sun_requirements: String,
    val growing_degree_days: Int?,
    val processing_pictures: Int
)

data class GrowstuffCropDetailPlantingDto(
    val id: Int,
    val garden_id: Int,
    val crop_id: Int,
    val planted_at: String,
    val quantity: Int?,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val slug: String,
    val sunniness: String,
    val planted_from: String?,
    val owner_id: Int,
    val finished: Boolean,
    val finished_at: String?,
    val lifespan: Int?,
    val days_to_first_harvest: Int?,
    val days_to_last_harvest: Int?,
    val parent_seed_id: Int?,
    val harvests_count: Int,
    val owner: GrowstuffCropDetailOwnerDto
)

data class GrowstuffCropDetailOwnerDto(
    val id: Int,
    val login_name: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?
)

data class GrowstuffCropDetailScientificNameDto(
    val name: String
)
