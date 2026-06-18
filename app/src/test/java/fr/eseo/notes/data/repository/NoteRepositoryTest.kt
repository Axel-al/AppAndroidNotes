package fr.eseo.notes.data.repository

import fr.eseo.notes.data.FakeNoteDao
import fr.eseo.notes.data.local.NoteEntity
import kotlinx.coroutines.flow.first
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
