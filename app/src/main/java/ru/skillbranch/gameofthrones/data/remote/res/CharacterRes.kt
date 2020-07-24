package ru.skillbranch.gameofthrones.data.remote.res

data class CharacterRes(
    val url: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String,
    val mother: String,
    val spouse: String,
    val allegiances: List<String> = listOf(),
    val books: List<String> = listOf(),
    val povBooks: List<Any> = listOf(),
    val tvSeries: List<String> = listOf(),
    val playedBy: List<String> = listOf()
) : IRes {
    lateinit var houseId: String
    override val id: String
        get() = url.lastSegment()
    val fatherId: String
        get() = father.lastSegment()
    val motherId: String
        get() = mother.lastSegment()
}

interface IRes {
    val id: String
    fun String.lastSegment(divider: String = "/") = split(divider).last()

}
