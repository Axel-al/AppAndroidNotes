package fr.eseo.notes.data.repository

import fr.eseo.notes.data.local.NoteDao
import fr.eseo.notes.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NoteRepositoryTest {

  private val dao = FakeNoteDao()
  private val repository = NoteRepository(dao)

  @Test
  fun `repository delegates the complete CRUD lifecycle`() = runTest {
    val id = repository.insertNote(NoteEntity(title = "Cours", content = "Room et Hilt"))

    assertEquals(listOf("Cours"), repository.getAllNotes().first().map(NoteEntity::title))
    assertEquals("Room et Hilt", repository.getNoteById(id)?.content)

    val updated =
      requireNotNull(repository.getNoteById(id)).copy(
        title = "Cours Android",
        updatedAt = 42,
      )
    repository.updateNote(updated)

    assertEquals("Cours Android", repository.searchNotes("android").first().single().title)

    repository.deleteNote(updated)

    assertNull(repository.getNoteById(id))
    assertEquals(emptyList<NoteEntity>(), repository.getAllNotes().first())
  }
}

private class FakeNoteDao : NoteDao {
  private val notes = MutableStateFlow<List<NoteEntity>>(emptyList())
  private var nextId = 1L

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
    notes.value = notes.value.filterNot { it.id == note.id }
  }
}
