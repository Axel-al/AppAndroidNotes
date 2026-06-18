package fr.eseo.notes.ui.notelist

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import fr.eseo.notes.R
import fr.eseo.notes.data.local.NoteEntity
import fr.eseo.notes.ui.theme.NotesTheme
import org.junit.Rule
import org.junit.Test

class NoteListScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun emptyState_isDisplayed() {
    composeTestRule.setContent {
      NotesTheme {
        NoteListContent(
          uiState = NoteListUiState(isLoading = false),
          padding = PaddingValues(),
          onNoteClick = {},
          onPinClick = {},
          onDeleteClick = {},
        )
      }
    }

    composeTestRule
      .onNodeWithText(composeTestRule.activity.getString(R.string.empty_notes))
      .assertExists()
  }

  @Test
  fun deleteButton_opensConfirmationDialog() {
    val note = NoteEntity(id = 1, title = "Cours", content = "Jetpack Compose")
    composeTestRule.setContent {
      NotesTheme {
        NoteListContent(
          uiState = NoteListUiState(notes = listOf(note), isLoading = false),
          padding = PaddingValues(),
          onNoteClick = {},
          onPinClick = {},
          onDeleteClick = {},
        )
      }
    }

    composeTestRule
      .onNodeWithContentDescription(
        composeTestRule.activity.getString(R.string.delete_note)
      )
      .performClick()

    composeTestRule
      .onNodeWithText(composeTestRule.activity.getString(R.string.delete_dialog_message))
      .assertExists()
  }
}
