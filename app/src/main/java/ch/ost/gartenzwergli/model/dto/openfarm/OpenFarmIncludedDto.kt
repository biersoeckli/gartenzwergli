package ch.ost.gartenzwergli.model.dto.openfarm

data class OpenFarmIncludedDto(
    val type: String,
    val id: String,
    val attributes: OpenFarmIncludedAttributesDto
)

