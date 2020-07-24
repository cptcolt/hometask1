package ru.skillbranch.gameofthrones

import androidx.lifecycle.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.lang.IllegalArgumentException

class CharacterViewModel(private val id: String) : ViewModel() {
    private val rep = RootRepository

    fun getCharacter() : LiveData<CharacterFull> {
        val charter  = rep.findCharacter(id)
        return charter
    }

}

class CharacterViewModelFactory(private val charId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            return CharacterViewModel(charId) as T
        }
        throw IllegalArgumentException("Error ViewModel class")
    }
}
