package com.notes.writenotes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.notes.writenotes.model.Note
import com.notes.writenotes.databinding.ActivityAddNoteBinding
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddNote : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note: Note

    private lateinit var old_note: Note
    var isUpdate = false
    var isdelete = false
    private val TAG = "AddNote"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            old_note = intent.getSerializableExtra("current_note") as Note
            binding.etTitle.setText(old_note.title)
            binding.etNote.setText(old_note.note)
            isUpdate = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val noteDescription = binding.etNote.text.toString()
            if (title.isNotEmpty() || noteDescription.isNotEmpty()) {

                val formatter = SimpleDateFormat("EEE, d MM yyyy HH:mm:ss")
                note = if (isUpdate) {
                    Note(
                        old_note.id, title, noteDescription, formatter.format(Date())
                    )
                } else {
                    Note(
                        null, title, noteDescription, formatter.format(Date())
                    )
                }
                val intent = Intent()
                intent.putExtra("note", note as Serializable)
                intent.putExtra("isUpdate", isUpdate)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(
                    this@AddNote,
                    "Kuch to Daal de ya Yha bhi Bhikhari Bnega",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}