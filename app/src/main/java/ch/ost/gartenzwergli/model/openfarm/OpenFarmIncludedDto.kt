package ch.ost.gartenzwergli.model.openfarm

data class OpenFarmIncludedDto(
    val type: String,
    val id: String,
    val attributes: OpenFarmIncludedAttributesDto
)

