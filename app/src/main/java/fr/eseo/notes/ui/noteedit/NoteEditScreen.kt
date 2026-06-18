package fr.eseo.notes.ui.noteedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.eseo.notes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
  onBack: () -> Unit,
  viewModel: NoteEditViewModel = hiltViewModel(),
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val genericError = stringResource(R.string.generic_error)

  LaunchedEffect(viewModel.errorMessage) {
    viewModel.errorMessage?.let { message ->
      snackbarHostState.showSnackbar(message.ifBlank { genericError })
      viewModel.clearError()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            stringResource(
              if (viewModel.isEditing) R.string.edit_note_title else R.string.new_note_title
            )
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
            )
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.save(onSaved = onBack) },
            enabled = viewModel.title.isNotBlank() && !viewModel.isLoading && !viewModel.isSaving,
          ) {
            if (viewModel.isSaving) {
              CircularProgressIndicator(modifier = Modifier.padding(10.dp))
            } else {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.save_note),
              )
            }
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { padding ->
    if (viewModel.isLoading) {
      Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center,
      ) {
        CircularProgressIndicator()
      }
    } else {
      Column(
        modifier =
          Modifier.fillMaxSize()
            .padding(padding)
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top,
      ) {
        OutlinedTextField(
          value = viewModel.title,
          onValueChange = viewModel::onTitleChange,
          label = { Text(stringResource(R.string.note_title_label)) },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
          value = viewModel.content,
          onValueChange = viewModel::onContentChange,
          label = { Text(stringResource(R.string.note_content_label)) },
          modifier = Modifier.fillMaxWidth().weight(1f),
        )
      }
    }
  }
}
