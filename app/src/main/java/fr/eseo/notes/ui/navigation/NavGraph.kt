package fr.eseo.notes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.eseo.notes.ui.noteedit.NoteEditScreen
import fr.eseo.notes.ui.notelist.NoteListScreen

@Composable
fun NotesApp() {
  val navController = rememberNavController()

  NavHost(
    navController = navController,
    startDestination = NoteListRoute,
  ) {
    composable<NoteListRoute> {
      NoteListScreen(
        onNoteClick = { noteId -> navController.navigate(NoteEditRoute(noteId)) },
        onAddClick = { navController.navigate(NoteEditRoute()) },
      )
    }
    composable<NoteEditRoute> {
      NoteEditScreen(onBack = { navController.popBackStack() })
    }
  }
}
