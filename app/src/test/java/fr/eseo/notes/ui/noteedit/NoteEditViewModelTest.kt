package fr.eseo.notes.ui.noteedit

import androidx.lifecycle.SavedStateHandle
import fr.eseo.notes.data.FakeNoteDao
import fr.eseo.notes.data.local.NoteEntity
import fr.eseo.notes.data.repository.NoteRepository
import fr.eseo.notes.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditViewModelTest {

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun `save creates a trimmed note`() = runTest {
    val dao = FakeNoteDao()
    val viewModel =
      NoteEditViewModel(
        repository = NoteRepository(dao),
        savedStateHandle = SavedStateHandle(mapOf("noteId" to NoteEditViewModel.NEW_NOTE_ID)),
      )
    var saved = false

    viewModel.onTitleChange("  Nouvelle note  ")
    viewModel.onContentChange("  Contenu  ")
    viewModel.save { saved = true }
    advanceUntilIdle()

    val note = requireNotNull(dao.getNoteById(1))
    assertEquals("Nouvelle note", note.title)
    assertEquals("Contenu", note.content)
    assertTrue(saved)
    assertFalse(viewModel.isSaving)
  }

  @Test
  fun `editing loads and updates the existing note`() = runTest {
    val existing = NoteEntity(id = 7, title = "Avant", content = "Ancien contenu")
    val dao = FakeNoteDao(listOf(existing))
    val viewModel =
      NoteEditViewModel(
        repository = NoteRepository(dao),
        savedStateHandle = SavedStateHandle(mapOf("noteId" to 7L)),
      )
    advanceUntilIdle()

    assertTrue(viewModel.isEditing)
    assertEquals("Avant", viewModel.title)

    viewModel.onTitleChange("Après")
    viewModel.save {}
    advanceUntilIdle()

    assertEquals("Après", dao.getNoteById(7)?.title)
  }
}
