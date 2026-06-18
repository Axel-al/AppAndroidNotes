package fr.eseo.notes.ui.navigation

import kotlinx.serialization.Serializable

@Serializable data object NoteListRoute

@Serializable data class NoteEditRoute(val noteId: Long = -1L)
