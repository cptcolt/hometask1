package ru.skillbranch.gameofthrones.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import org.w3c.dom.CharacterData
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.data.AppData
import ru.skillbranch.gameofthrones.data.DbManager
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.dao.HouseDao
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.data.remote.res.NetworkService
import ru.skillbranch.gameofthrones.data.remote.res.RestService

object RootRepository {
    private val api: RestService = NetworkService.api
    private val houseDao: HouseDao = DbManager.db.houseDao()
    private val characterDao = DbManager.db.characterDao()

    private val errorHandler = CoroutineExceptionHandler {_, exception ->
        println("Error $exception")
        exception.printStackTrace()
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + errorHandler)

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(): List<House> = houseDao.getHousesList()

    /**
     * Получение данных о требуемых домах по их полным именам из сети 
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getNeedHouses(vararg houseNames: String): List<HouseRes> {
        return houseNames.fold(mutableListOf()) { acc, title ->
            acc.also {
                it.add(api.houseByName(title).first())
            }
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getNeedHouseWithCharacters(vararg houseNames: String): List<Pair<HouseRes, List<CharacterRes>>> {
        val result = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        val houses = getNeedHouses(*houseNames)

        scope.launch {
            houses.forEach {house ->
                var count = 0;
                println("house ${house.url} scope ${this.coroutineContext}")
                val characters = mutableListOf<CharacterRes>()
                result.add(house to characters)
                house.members.forEach {character ->
                    launch(CoroutineName("character $character")) {
                        api.charter(character)
                            .apply { houseId = house.shortName }
                            .also { characters.add(it) }
                        count++
                    }
                }
            }
        }.join()

        return result
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) {
        var list = mutableListOf<House>()
        houses.forEach {
            list.add(it.toHouse())
        }
        houseDao.upsert(list)
        return complete()
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(Characters : List<CharacterRes>, house: HouseType, complete: () -> Unit) {
        var list = mutableListOf<Character>()
        Characters.forEach {
            list.add(it.toCharacter(house))
        }
        characterDao.upsert(list)
        return complete()
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        //TODO implement me
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name : String): List<CharacterItem> = characterDao.findCharactersList(name)

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id : String): CharacterFull = characterDao.findCharacterFull(id)

    fun findCharacter(id : String): LiveData<CharacterFull> = characterDao.findCharacter(id)

    fun findCharacters(title : String) = characterDao.findCharacters(title)

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    suspend fun isNeedUpdate() = houseDao.recordsCount() == 0

    suspend fun syncData() {
        val pairs = getNeedHouseWithCharacters(*AppConfig.NEED_HOUSES)
        val inits = mutableListOf<House>() to mutableListOf<Character>()
        val lists = pairs.fold(inits) {acc, (houseRes, charactersList) ->
            val house = houseRes.toHouse()
            val chars = charactersList.map { it.toCharacter(house.id) }
           println("House sync $houseRes")
            println("House sync $charactersList")
            acc.also {(hs, ch) ->
                hs.add(house)
                ch.addAll(chars)
            }
        }
        houseDao.upsert(lists.first)
        characterDao.upsert(lists.second)
    }
}

private fun CharacterRes.toCharacter(house: HouseType): Character {
    return Character (
        this.id,
        this.name,
        this.gender,
        this.culture,
        this.born,
        this.died,
        this.titles,
        this.aliases,
        this.father,
        this.mother,
        this.spouse,
        house)
}

private fun HouseRes.toHouse(): House {
    val data = this
    val house = House(
        HouseType.fromString(data.shortName.trim()),
        data.name,
        data.region,
        data.coatOfArms,
        data.words,
        data.titles,
        data.seats,
        data.currentLord,
        data.heir,
        data.overlord,
        data.founded,
        data.founder,
        data.diedOut,
        data.ancestralWeapons
    )
    return house
}
