package com.notes.writenotes.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.notes.writenotes.model.Note
import com.notes.writenotes.R
import kotlin.random.Random

class NotesAdapter(
    private val context: Context,
    private val listener: NotesClickListener
    ):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() , Filterable {
    private var list: MutableList<Note> = mutableListOf()
    private var notesFilteredList: MutableList<Note> = mutableListOf()
    private val TAG = "NotesAdapter"

    inner class ItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        var notesLayout: CardView = view.findViewById(R.id.card_layout)
        var title: TextView = view.findViewById(R.id.tv_title)
        private val noteTv: TextView  = view.findViewById(R.id.tv_note)
        private val date: TextView = view.findViewById(R.id.tv_date)

        @SuppressLint("UseCompatLoadingForColorStateLists")
        fun onBind(item: Note, position: Int) {
            title.text = item.title
            noteTv.text = item.note
            date.text =  item.date
            notesLayout.setCardBackgroundColor(randomColor(context))

            notesLayout.setOnClickListener {
                Log.d(TAG, "onBind: "+"42")
                listener.onItemClicked(item)
            }

            notesLayout.setOnLongClickListener {
                // Do something when the view is long-clicked
                listener.onLongItemClicked(note = item, position = position)
                Log.d(TAG, "onLongClick: " + "48")
                true // return true to indicate that the long click has been handled
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = notesFilteredList[position]
        if (holder is NotesAdapter.ItemViewHolder) {
            holder.onBind(item, position)
        }
    }
    fun itemDelete(note: Note, position: Int){
        notesFilteredList.removeAt(position)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        if (notesFilteredList.size != 0) {
            return notesFilteredList.size
        }
        return 0
    }
    fun randomColor(context: Context):Int{
        val list = listOf(
            R.color.color1, R.color.color2, R.color.color3, R.color.color4,
            R.color.color5, R.color.color6, R.color.color7, R.color.color8,
            R.color.color9, R.color.color10, R.color.color11, R.color.color12,
            R.color.color13, R.color.color14, R.color.color15
        )
        val randomIndex = Random.nextInt(list.size)
        return ContextCompat.getColor(context, list[randomIndex])
        }

    fun submitList(data: List<Note>) {
        list.clear()
        notesFilteredList.clear()
        list.addAll(data)
        notesFilteredList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charString = constraint?.toString() ?: ""

                if(charString.isEmpty()){
                    notesFilteredList.clear()
                    notesFilteredList.addAll(list)
                } else{

                    var filteredList:  MutableList<Note> = mutableListOf()

                    list.filter {
                        (it.title!!.lowercase().startsWith(constraint.toString().lowercase().trim()))
                    }.forEach{ filteredList.add(it)}
                    notesFilteredList = filteredList
                }

                return FilterResults().apply { values = notesFilteredList }

            }
            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results!!.values != null) {
                    notesFilteredList = results.values as MutableList<Note>
                    notifyDataSetChanged()
                }

            }
        }
    }

    interface NotesClickListener{
        fun onItemClicked(note: Note)
        fun onLongItemClicked(note: Note, position: Int)
    }
}