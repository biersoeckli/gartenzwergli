package ch.ost.gartenzwergli.model.dbo.cropevent

import java.time.LocalDate


data class CropEventHarvestItem(
    val cropEventAndCrop: CropEventAndCrop,
    val harvestDate: LocalDate
)