package fr.eseo.notes.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.eseo.notes.data.local.NoteDao
import fr.eseo.notes.data.local.NoteDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase =
    Room.databaseBuilder(context, NoteDatabase::class.java, "notes.db").build()

  @Provides fun provideNoteDao(database: NoteDatabase): NoteDao = database.noteDao()
}
