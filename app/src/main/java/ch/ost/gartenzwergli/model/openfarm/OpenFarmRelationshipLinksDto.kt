package ch.ost.gartenzwergli.model.openfarm

data class OpenFarmRelationshipLinksDto(
    val links: OpenFarmRelatedLinksDto,
    val data: List<OpenFarmRelatedDataDto>
)
