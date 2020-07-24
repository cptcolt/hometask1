package ru.skillbranch.gameofthrones

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.house_item.view.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem

class CharacterAdapter(private val listener: (CharacterItem) -> Unit):
    RecyclerView.Adapter<CharacterAdapter.CharacterVH>() {
    var items: List<CharacterItem> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterVH {
        val containerView: View = from(parent.context).inflate(R.layout.house_item, parent, false)

        return CharacterVH(containerView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CharacterVH, position: Int) {
        holder.bind(items[position], listener)
    }

    fun updateItems(data: List<CharacterItem>) {
        val diffCalback = object: DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].id == data[newItemPosition].id

            override fun getOldListSize(): Int {
                return items.size
            }

            override fun getNewListSize(): Int {
                return data.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition] == data[newItemPosition]

        }
        val diffResult = DiffUtil.calculateDiff(diffCalback, true)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    class CharacterVH (override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(
            item: CharacterItem,
            listener: (CharacterItem) -> Unit
        ) {
            with(containerView) {
                item.name.also {
                    title.text = if (it.isBlank()) "None" else it
                }
                item.titles
                    .plus(item.aliases)
                    .filter { it.isNotBlank() }
                    .also {
                        description.text = if (it.isEmpty()) "None" else it.joinToString(" * ")
                    }
                icon.setImageResource(item.house.icon)
                itemView.setOnClickListener{listener(item)}
            }
        }
    }

}
