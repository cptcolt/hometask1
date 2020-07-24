package ru.skillbranch.gameofthrones.data.local.entities.dao

import androidx.room.Dao
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

@Dao
interface HouseDao: BaseDao<House> {
    @Query("SELECT count(*) FROM houses")
    suspend fun recordsCount(): Int

    @Transaction
    fun upsert(objList: List<House>) {
        insert(objList)
            .mapIndexed{index, l -> if (l == -1L) objList[index] else null}
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it)}
    }

    @Query("SELECT id, name, region, coatOfArms, words, titles, seats, currentLord, heir, overlord, founded, founder, diedOut, ancestralWeapons FROM houses")
    fun getHousesList(): List<House>

}