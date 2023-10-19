package ch.ost.gartenzwergli.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.ost.gartenzwergli.model.dbo.CropDbo

@Dao
interface CropDao {
    @Query("SELECT * FROM crop")
    fun getAll(): List<CropDbo>

    @Query("SELECT * FROM crop WHERE id IN (:cropIds)")
    fun loadAllByIds(cropIds: IntArray): List<CropDbo>

    @Query("SELECT * FROM crop WHERE id = :cropId")
    fun findById(cropId: String): CropDbo

    @Insert
    fun insertAll(vararg users: CropDbo)

    @Update
    fun updateAll(vararg users: CropDbo)

    @Delete
    fun delete(user: CropDbo)
}