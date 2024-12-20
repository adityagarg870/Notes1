package com.notes.writenotes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.notes.writenotes.Adapter.NotesAdapter
import com.notes.writenotes.Database.NoteDatabase
import com.notes.writenotes.model.Note
import com.notes.writenotes.model.NoteViewModel
import com.notes.writenotes.databinding.ActivityMainBinding
import java.io.Serializable

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NotesAdapter.NotesClickListener,
    PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNote: Note
    private val launchAddNote =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val note = result.data?.getSerializableExtra("note") as? Note
                val isUpdate = result.data?.getBooleanExtra("isUpdate", false)
                val isdelete = result.data?.getBooleanExtra("isdelete", false)
                if (note != null) {
                    if (isUpdate!!) {
                        viewModel.updateNote(note)
                    } else {
                        viewModel.insertNote(note)

                    }


                }
            } else {

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]
        viewModel.allnotes.observe(this) { list ->
            list?.let {
                adapter.submitList(list)
            }
        }
        database = NoteDatabase.getDatabase(this)
    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.recyclerView.adapter = adapter
        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this, AddNote::class.java)
            launchAddNote.launch(intent)
        }
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
        })
    }

    override fun onItemClicked(note: Note) {
        val intent = Intent(this@MainActivity, AddNote::class.java)
        intent.putExtra("current_note", note as Serializable)
        launchAddNote.launch(intent)
    }

    override fun onLongItemClicked(note: Note, position: Int) {
//        selectedNote = note
//        popUpDisplay(cardView)
        Log.d(TAG, "onLongItemClicked: "+"99")
        adapter.itemDelete(note, position)
        viewModel.deleteNote(note)
    }


    private fun popUpDisplay(cardView: CardView) {
        val popup = PopupMenu(this, cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate((R.menu.pop_up_menu))
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_note) {
            viewModel.deleteNote(selectedNote)
            return true
        }else {
            return false
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}