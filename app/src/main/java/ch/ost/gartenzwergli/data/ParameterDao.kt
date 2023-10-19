package ch.ost.gartenzwergli.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.ParameterDbo

@Dao
interface ParameterDao {
    @Query("SELECT * FROM parameter WHERE id = :paramKey")
    fun findByKey(paramKey: String): ParameterDbo?

    @Insert
    fun insertAll(vararg users: ParameterDbo)

    @Delete
    fun delete(user: ParameterDbo)
}