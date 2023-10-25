package ch.ost.gartenzwergli.model.dto.openfarm

data class OpenFarmRelationshipLinksDto(
    val links: OpenFarmRelatedLinksDto,
    val data: List<OpenFarmRelatedDataDto>
)
