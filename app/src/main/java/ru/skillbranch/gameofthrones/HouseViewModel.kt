package ru.skillbranch.gameofthrones

import androidx.lifecycle.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.lang.IllegalArgumentException

class HouseViewModel(private val houseName: String) : ViewModel() {
    private val rep = RootRepository
    val queryStr = MutableLiveData("")

    fun getCharacters() : LiveData<List<CharacterItem>> {
        val charters: LiveData<List<CharacterItem>> = rep.findCharacters(houseName)
        return charters.combineAndCompute(queryStr) {list, query ->
            if (query.isEmpty()) list
            else list.filter{it.name.contains(query, true)}
        }
    }

    fun searchQuery(str: String) {
        queryStr.value = str
    }

    fun <T, A, B> LiveData<A>.combineAndCompute(other: LiveData<B>, onChange: (A, B) -> T): MediatorLiveData<T> {

        var source1emitted = false
        var source2emitted = false

        val result = MediatorLiveData<T>()

        val mergeF = {
            val source1Value = this.value
            val source2Value = other.value

            if (source1emitted && source2emitted) {
                result.value = onChange.invoke(source1Value!!, source2Value!! )
            }
        }

        result.addSource(this) { source1emitted = true; mergeF.invoke() }
        result.addSource(other) { source2emitted = true; mergeF.invoke() }

        return result
    }
}

class HouseViewModelFactory(private val houseName: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseViewModel::class.java)) {
            return HouseViewModel(houseName) as T
        }
        throw IllegalArgumentException("Error ViewModel class")
    }
}