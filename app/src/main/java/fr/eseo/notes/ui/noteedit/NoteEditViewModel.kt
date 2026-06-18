package fr.eseo.notes.ui.noteedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.notes.data.local.NoteEntity
import fr.eseo.notes.data.repository.NoteRepository
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NoteEditViewModel
@Inject
constructor(
  private val repository: NoteRepository,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val noteId: Long = savedStateHandle["noteId"] ?: NEW_NOTE_ID
  val isEditing: Boolean
    get() = noteId != NEW_NOTE_ID

  private var originalNote: NoteEntity? = null

  var title by mutableStateOf("")
    private set

  var content by mutableStateOf("")
    private set

  var isLoading by mutableStateOf(isEditing)
    private set

  var isSaving by mutableStateOf(false)
    private set

  var errorMessage by mutableStateOf<String?>(null)
    private set

  init {
    if (isEditing) {
      viewModelScope.launch {
        try {
          originalNote = repository.getNoteById(noteId)
          originalNote?.let { note ->
            title = note.title
            content = note.content
          }
        } catch (exception: Exception) {
          errorMessage = exception.localizedMessage
        } finally {
          isLoading = false
        }
      }
    }
  }

  fun onTitleChange(value: String) {
    title = value
  }

  fun onContentChange(value: String) {
    content = value
  }

  fun save(onSaved: () -> Unit) {
    if (title.isBlank() || isSaving) return

    viewModelScope.launch {
      isSaving = true
      errorMessage = null
      try {
        val now = System.currentTimeMillis()
        val note = originalNote
        if (note == null) {
          repository.insertNote(
            NoteEntity(
              title = title.trim(),
              content = content.trim(),
              updatedAt = now,
              createdAt = now,
            )
          )
        } else {
          repository.updateNote(
            note.copy(
              title = title.trim(),
              content = content.trim(),
              updatedAt = now,
            )
          )
        }
        onSaved()
      } catch (exception: Exception) {
        errorMessage = exception.localizedMessage
      } finally {
        isSaving = false
      }
    }
  }

  fun clearError() {
    errorMessage = null
  }

  companion object {
    const val NEW_NOTE_ID = -1L
  }
}
