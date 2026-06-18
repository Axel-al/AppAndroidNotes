package fr.eseo.notes.data

import fr.eseo.notes.data.local.NoteDao
import fr.eseo.notes.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNoteDao(initialNotes: List<NoteEntity> = emptyList()) : NoteDao {
  private val notes = MutableStateFlow(initialNotes)
  private var nextId = (initialNotes.maxOfOrNull(NoteEntity::id) ?: 0L) + 1

  var deleteFailure: Exception? = null

  override fun getAllNotes(): Flow<List<NoteEntity>> =
    notes.map { values ->
      values.sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenByDescending { it.updatedAt })
    }

  override suspend fun getNoteById(noteId: Long): NoteEntity? =
    notes.value.firstOrNull { it.id == noteId }

  override fun searchNotes(query: String): Flow<List<NoteEntity>> =
    getAllNotes().map { values -> values.filter { it.title.contains(query, ignoreCase = true) } }

  override suspend fun insertNote(note: NoteEntity): Long {
    val id = if (note.id == 0L) nextId++ else note.id
    notes.value = notes.value.filterNot { it.id == id } + note.copy(id = id)
    return id
  }

  override suspend fun updateNote(note: NoteEntity) {
    notes.value = notes.value.map { current -> if (current.id == note.id) note else current }
  }

  override suspend fun deleteNote(note: NoteEntity) {
    deleteFailure?.let { throw it }
    notes.value = notes.value.filterNot { it.id == note.id }
  }
}
