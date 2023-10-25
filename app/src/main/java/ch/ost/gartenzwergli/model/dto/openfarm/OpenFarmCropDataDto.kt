package ch.ost.gartenzwergli.model.dto.openfarm


data class OpenFarmCropDataDto(
    val type: String,
    val id: String,
    val attributes: OpenFarmAttributesDto,
    val links: OpenFarmLinksDto,
    val relationships: OpenFarmRelationshipsDto
)