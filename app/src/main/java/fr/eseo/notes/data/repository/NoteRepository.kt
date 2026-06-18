package fr.eseo.notes.data.repository

import fr.eseo.notes.data.local.NoteDao
import fr.eseo.notes.data.local.NoteEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class NoteRepository
@Inject
constructor(
  private val noteDao: NoteDao,
) {
  fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

  fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

  suspend fun getNoteById(noteId: Long): NoteEntity? = noteDao.getNoteById(noteId)

  suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

  suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

  suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
