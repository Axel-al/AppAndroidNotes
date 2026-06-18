package fr.eseo.notes.ui.notelist

import fr.eseo.notes.data.FakeNoteDao
import fr.eseo.notes.data.local.NoteEntity
import fr.eseo.notes.data.repository.NoteRepository
import fr.eseo.notes.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModelTest {

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun `togglePin updates the persisted note`() = runTest {
    val note = NoteEntity(id = 1, title = "À faire", content = "Réviser", updatedAt = 1)
    val dao = FakeNoteDao(listOf(note))
    val viewModel = NoteListViewModel(NoteRepository(dao))

    viewModel.togglePin(note)
    advanceUntilIdle()

    assertTrue(requireNotNull(dao.getNoteById(1)).isPinned)
  }

  @Test
  fun `delete failure is exposed in UI state`() = runTest {
    val note = NoteEntity(id = 1, title = "À faire", content = "Réviser")
    val dao = FakeNoteDao(listOf(note)).apply { deleteFailure = IllegalStateException("Échec simulé") }
    val viewModel = NoteListViewModel(NoteRepository(dao))
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

    viewModel.deleteNote(note)
    advanceUntilIdle()

    assertEquals("Échec simulé", viewModel.uiState.value.errorMessage)
    assertFalse(viewModel.uiState.value.isLoading)
    assertEquals(listOf(note), viewModel.uiState.value.notes)
  }
}
