package fr.eseo.notes.ui.notelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.notes.data.local.NoteEntity
import fr.eseo.notes.data.repository.NoteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NoteListViewModel
@Inject
constructor(
  private val repository: NoteRepository,
) : ViewModel() {

  private val errorMessage = MutableStateFlow<String?>(null)

  val uiState: StateFlow<NoteListUiState> =
    repository
      .getAllNotes()
      .combine(errorMessage) { notes, error ->
        NoteListUiState(notes = notes, isLoading = false, errorMessage = error)
      }
      .catch { exception ->
        emit(
          NoteListUiState(
            isLoading = false,
            errorMessage = exception.localizedMessage,
          )
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteListUiState(),
      )

  fun deleteNote(note: NoteEntity) {
    viewModelScope.launch {
      try {
        repository.deleteNote(note)
      } catch (exception: Exception) {
        errorMessage.value = exception.localizedMessage
      }
    }
  }

  fun togglePin(note: NoteEntity) {
    viewModelScope.launch {
      try {
        repository.updateNote(
          note.copy(
            isPinned = !note.isPinned,
            updatedAt = System.currentTimeMillis(),
          )
        )
      } catch (exception: Exception) {
        errorMessage.value = exception.localizedMessage
      }
    }
  }

  fun clearError() {
    errorMessage.value = null
  }
}
