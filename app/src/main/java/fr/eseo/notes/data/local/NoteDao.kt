package fr.eseo.notes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

  @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
  fun getAllNotes(): Flow<List<NoteEntity>>

  @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
  suspend fun getNoteById(noteId: Long): NoteEntity?

  @Query(
    """
    SELECT * FROM notes
    WHERE title LIKE '%' || :query || '%' COLLATE NOCASE
    ORDER BY isPinned DESC, updatedAt DESC
    """
  )
  fun searchNotes(query: String): Flow<List<NoteEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: NoteEntity): Long

  @Update
  suspend fun updateNote(note: NoteEntity)

  @Delete
  suspend fun deleteNote(note: NoteEntity)
}
