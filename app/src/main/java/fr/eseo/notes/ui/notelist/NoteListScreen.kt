package fr.eseo.notes.ui.notelist

import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.eseo.notes.R
import fr.eseo.notes.data.local.NoteEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
  onNoteClick: (Long) -> Unit,
  onAddClick: () -> Unit,
  viewModel: NoteListViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val genericError = stringResource(R.string.generic_error)

  LaunchedEffect(uiState.errorMessage) {
    uiState.errorMessage?.let { message ->
      snackbarHostState.showSnackbar(message.ifBlank { genericError })
      viewModel.clearError()
    }
  }

  Scaffold(
    topBar = { TopAppBar(title = { Text(stringResource(R.string.notes_title)) }) },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      FloatingActionButton(onClick = onAddClick) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = stringResource(R.string.add_note),
        )
      }
    },
  ) { padding ->
    NoteListContent(
      uiState = uiState,
      padding = padding,
      onNoteClick = onNoteClick,
      onPinClick = viewModel::togglePin,
      onDeleteClick = viewModel::deleteNote,
    )
  }
}

@Composable
internal fun NoteListContent(
  uiState: NoteListUiState,
  padding: PaddingValues,
  onNoteClick: (Long) -> Unit,
  onPinClick: (NoteEntity) -> Unit,
  onDeleteClick: (NoteEntity) -> Unit,
) {
  when {
    uiState.isLoading -> {
      Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center,
      ) {
        CircularProgressIndicator()
      }
    }

    uiState.notes.isEmpty() -> {
      Box(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = stringResource(R.string.empty_notes),
          style = MaterialTheme.typography.bodyLarge,
        )
      }
    }

    else -> {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        items(uiState.notes, key = NoteEntity::id) { note ->
          NoteCard(
            note = note,
            onClick = { onNoteClick(note.id) },
            onPinClick = { onPinClick(note) },
            onDeleteClick = { onDeleteClick(note) },
          )
        }
      }
    }
  }
}

@Composable
internal fun NoteCard(
  note: NoteEntity,
  onClick: () -> Unit,
  onPinClick: () -> Unit,
  onDeleteClick: () -> Unit,
) {
  var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
  val cardColor =
    remember(note.color) {
      runCatching { Color(parseColor(note.color)) }.getOrElse { Color.Unspecified }
    }

  Card(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    colors =
      if (cardColor == Color.Unspecified) {
        CardDefaults.cardColors()
      } else {
        CardDefaults.cardColors(containerColor = cardColor)
      },
  ) {
    Row(
      modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 4.dp, bottom = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = note.title,
            modifier = Modifier.weight(1f, fill = false),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          if (note.isPinned) {
            Spacer(Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.pinned_badge),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }
        Spacer(Modifier.height(4.dp))
        Text(
          text = note.content.ifBlank { stringResource(R.string.empty_content) },
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        Text(
          text = stringResource(R.string.last_modified, formatDate(note.updatedAt)),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      IconButton(onClick = onPinClick) {
        Icon(
          imageVector = Icons.Default.PushPin,
          contentDescription =
            stringResource(if (note.isPinned) R.string.unpin_note else R.string.pin_note),
          tint =
            if (note.isPinned) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
      }
      IconButton(onClick = { showDeleteDialog = true }) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = stringResource(R.string.delete_note),
        )
      }
    }
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text(stringResource(R.string.delete_dialog_title)) },
      text = { Text(stringResource(R.string.delete_dialog_message)) },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            onDeleteClick()
          }
        ) {
          Text(stringResource(R.string.delete))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text(stringResource(R.string.cancel))
        }
      },
    )
  }
}

@Composable
private fun formatDate(timestamp: Long): String {
  val formatter =
    remember {
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withZone(ZoneId.systemDefault())
    }
  return remember(timestamp, formatter) { formatter.format(Instant.ofEpochMilli(timestamp)) }
}
