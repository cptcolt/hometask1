package ru.skillbranch.gameofthrones.data.remote.res

data class HouseRes(
    val url: String,
    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
    val titles: List<String> = listOf(),
    val seats: List<String> = listOf(),
    val currentLord: String,
    val heir: String,
    val overlord: String,
    val founded: String,
    val founder: String,
    val diedOut: String,
    val ancestralWeapons: List<String> = listOf(),
    val cadetBranches: List<Any> = listOf(),
    val swornMembers: List<String> = listOf()
): IRes {
     override val id:String
         get() = url.lastSegment()

     val shortName: String
         get() = name.dropLastUntil()

     val members: List<String>
         get() = swornMembers.map {it.lastSegment()}

    fun String.dropLastUntil() : String {
        var str = split(" of").first()
        str = str.split(" ").last()
        return str
    }
}