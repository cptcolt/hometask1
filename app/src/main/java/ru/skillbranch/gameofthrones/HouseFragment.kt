package ru.skillbranch.gameofthrones

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.house_fragment.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem

class HouseFragment : Fragment() {

    companion object {
        fun newInstance(houseName: String) = HouseFragment().apply {
            arguments = bundleOf("HOUSE" to houseName)
        }
    }

    private lateinit var viewModel: HouseViewModel
    private lateinit var houseName: String
    private lateinit var characterAdapter: CharacterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        with (arguments) {
            houseName = this?.getString("HOUSE", "") ?: HouseType.STARK.title
        }
        characterAdapter = CharacterAdapter {
            val action = ChartersListScreenDirections.actionChartersListScreenToCharacterScreen2(it.id, it.house.title, it.name)
            findNavController().navigate(action)
        }
        val vmFactory = HouseViewModelFactory(houseName)
        viewModel = ViewModelProviders.of(this, vmFactory).get(HouseViewModel::class.java)
        viewModel.getCharacters().observe(this, Observer<List<CharacterItem>> {
            characterAdapter.updateItems(it)
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.house_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(house_recycle) {
            layoutManager = LinearLayoutManager(context)
            adapter = characterAdapter
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        with(menu.findItem(R.id.app_bar_search).actionView as SearchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.searchQuery(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.searchQuery(newText)
                    return true
                }
            })
        }
        super.onPrepareOptionsMenu(menu)
    }
}