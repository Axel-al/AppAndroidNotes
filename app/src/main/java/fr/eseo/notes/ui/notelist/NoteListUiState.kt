package fr.eseo.notes.ui.notelist

import fr.eseo.notes.data.local.NoteEntity

data class NoteListUiState(
  val notes: List<NoteEntity> = emptyList(),
  val isLoading: Boolean = true,
  val errorMessage: String? = null,
)
