package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*
import ru.skillbranch.gameofthrones.HouseType

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String, //rel
    val mother: String, //rel
    val spouse: String,
    @ColumnInfo(name = "house_id")
    val houseId: HouseType//rel
)

@DatabaseView(
    """
        SELECT house_id as house, id, name, aliases, titles
        FROM characters
        ORDER BY name ASC
        """
)
data class CharacterItem(
    val id: String,
    val house: HouseType, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
)

@DatabaseView(
    """
        SELECT characters.id, characters.name, characters.born, characters.died, characters.titles, characters.aliases, characters.house_id, characters.mother, characters.father, houses.words, mother.name as m_name, mother.id as m_id, mother.house_id as m_house, father.name as f_name, father.id as f_id, father.house_id as f_house 
        FROM characters 
        LEFT JOIN characters as mother ON characters.mother = mother.id 
        LEFT JOIN characters as father ON characters.father = father.id 
        INNER JOIN houses ON characters.house_id = houses.id
        """
)

data class CharacterFull(
    val id: String,
    val name: String,
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    @ColumnInfo(name = "house_id")
    val house:HouseType, //rel
    @Embedded(prefix = "f_")
    val father: RelativeCharter?,
    @Embedded(prefix = "m_")
    val mother: RelativeCharter?
)

data class RelativeCharter(
    val id: String,
    val name: String,
    val house:String //rel
)