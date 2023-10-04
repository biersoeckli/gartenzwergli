package ch.ost.gartenzwergli.model.openfarm


data class OpenFarmCropDataDto(
    val type: String,
    val id: String,
    val attributes: OpenFarmAttributesDto,
    val links: OpenFarmLinksDto,
    val relationships: OpenFarmRelationshipsDto
)