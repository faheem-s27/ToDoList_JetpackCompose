package com.faheemsaleem.todolistapplication.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.lifecycle.ViewModel
import java.util.Date

data class Note(val text: String, val checked: Boolean = false, val date: Long = 0)
data class Notes(val list: List<Note>)

sealed class ToDoNoteActions
{
    data class AddNote(val note: Note) : ToDoNoteActions()
    data class DeleteNote(val note: Note) : ToDoNoteActions()
    data class EditNote(
        val note: Note,
        val text: String = note.text,
        val checked: Boolean = note.checked,
        val date: Long = note.date
    ) : ToDoNoteActions()
}

class ToDoNoteViewModel: ViewModel()
{
    private var dummyList = listOf<Note>(
        Note(text = "Buy Milk", checked = true),
        Note(text = "Buy Eggs", date = Date().time, checked = true),
        Note(text = "Buy Bread"),
        Note(text = "Thank you for watching :)", checked = true)
    )

    fun onAction(action: ToDoNoteActions)
    {
        when(action)
        {
            is ToDoNoteActions.AddNote -> AddItem(text = action.note.text, date = action.note.date)
            is ToDoNoteActions.DeleteNote -> {}
            is ToDoNoteActions.EditNote ->  {}
        }
    }

    private var list by mutableStateOf(listOf<Note>())
    //private var list by mutableStateOf(dummyList)

    var state by mutableStateOf(Notes(list))
        private set

    private fun AddItem(text: String, checked: Boolean = false, date: Long = 0)
    {
        val note = Note(
            text = text,
            checked = checked,
            date = date
        )
        list = list + note
        state = state.copy(list = list)
    }
}
