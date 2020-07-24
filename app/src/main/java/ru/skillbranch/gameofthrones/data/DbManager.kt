package ru.skillbranch.gameofthrones.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.MainActivity
import ru.skillbranch.gameofthrones.data.AppData.Companion.DB_NAME
import ru.skillbranch.gameofthrones.data.AppData.Companion.DB_VER
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.dao.CharactersDao
import ru.skillbranch.gameofthrones.data.local.entities.dao.HouseDao

object DbManager {
    val db = databaseBuilder(
        App.applicationContext,
        AppData::class.java,
        DB_NAME
    ).build()
}

@Database(
    entities = [House::class, Character::class],
    version = DB_VER,
    exportSchema =  true,
    views = [CharacterItem::class, CharacterFull::class]
)
@TypeConverters(Converter::class)
abstract class AppData : RoomDatabase() {
    companion object {
        const val DB_NAME = "got.db"
        const val DB_VER = 1;
    }

    abstract fun houseDao() : HouseDao
    abstract fun characterDao(): CharactersDao
}