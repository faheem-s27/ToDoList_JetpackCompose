package com.faheemsaleem.todolistapplication

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faheemsaleem.todolistapplication.ui.theme.Note
import com.faheemsaleem.todolistapplication.ui.theme.Notes
import com.faheemsaleem.todolistapplication.ui.theme.ToDoListApplicationTheme
import com.faheemsaleem.todolistapplication.ui.theme.ToDoNoteActions
import com.faheemsaleem.todolistapplication.ui.theme.ToDoNoteViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListApplicationTheme {
                val viewModel = viewModel<ToDoNoteViewModel>()
                val state = viewModel.state
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "MainScreen") {
                    composable("MainScreen") {
                        ToDoApplication(state = state, navController = navController)
                    }
                    composable("AddNote") {
                        AddingNoteScreen(navController = navController, onAction = viewModel::onAction)
                    }
                }

                //ToDoApplication(state = state)
            }
        }
    }
}

@Composable
fun ToDoNote(text: String, checked: Boolean = false, date: String = "") {
    var checked by remember {
      mutableStateOf(checked)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
        )

        if (checked) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(8.dp),
                textAlign = TextAlign.Start,

            )
        } else {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (date.isNotBlank()) {
            Text(
                text = date,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(
    showBackground = true,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewAddingNoteScreen() {
    ToDoListApplicationTheme {
        AddingNoteScreen()
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddingNoteScreen(
    navController: NavController? = null,
    onAction: (ToDoNoteActions) -> Unit = { }
)
{
    val timeDialogState = rememberTimePickerState(initialHour = LocalTime.now().hour, initialMinute = LocalTime.now().minute,
        is24Hour = true
    )
    var noteText by remember {
        mutableStateOf("")
    }
    var showDate by remember {
        mutableStateOf(false)
    }
    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("EEEE, MMM d, yyyy")
                .format(pickedDate)
        }
    }

    var showErrorDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val dateDialogState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Add Note",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        )

        Text(
            text = "Note",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        )
        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Text(
            text = "Date",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = formattedDate, color = MaterialTheme.colorScheme.onSurface)
            ElevatedButton(
                onClick = { showDate = true },
            ) {
                Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.surfaceTint)
            }
        }
        TimePicker(state = timeDialogState, modifier = Modifier
            .padding(8.dp)
            .align(Alignment.CenterHorizontally))


        if (showDate) {
            val confirmEnabled = remember {
                derivedStateOf { dateDialogState.selectedDateMillis != null }
            }
            DatePickerDialog(
                onDismissRequest = {
                    showDate = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDate = Instant.ofEpochMilli(dateDialogState.selectedDateMillis!!)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            if (selectedDate.isBefore(LocalDate.now())) {
                                showErrorDialog = true
                            } else {
                                pickedDate = selectedDate
                                showDate = false
                            }
                        },
                        enabled = confirmEnabled.value
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDate = false }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = dateDialogState)
            }
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text("The selected date is before the current date.") },
                confirmButton = {
                    TextButton(
                        onClick = { showErrorDialog = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = {
                    onAction(ToDoNoteActions.AddNote(
                        Note(
                            text = noteText,
                            date = getFullDateTime(timeDialogState, pickedDate)
                        )
                    ))
                    navController?.navigate("MainScreen") },
                modifier = Modifier
                    .padding(8.dp)
            )
            {
                Text(
                    text = "Add",
                    modifier = Modifier
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            ElevatedButton(
                onClick = { navController?.navigate("MainScreen") },
                modifier = Modifier
                    .padding(8.dp)
            )
            {
                Text(
                    text = "Cancel",
                    modifier = Modifier
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun getFullDateTime(state: TimePickerState, date: LocalDate): Long
{
    val time = LocalTime.of(state.hour, state.minute)
    val dateTime = date.atTime(time)
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}


@Preview(
    showBackground = true,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewToDoApplication() {
    val dummyNotes = Notes(
        list = listOf(
            Note(text = "Buy Milk", checked = true),
            Note(text = "Buy Eggs", date = Date().time, checked = true),
            Note(text = "Buy Bread"),
            Note(text = "Thank you for watching :)", checked = true)
        )
    )
    ToDoListApplicationTheme {
        ToDoApplication(state = dummyNotes)
    }
}

@Composable
fun convertLongToTime(time: Long?): String {
    // return "" if time is 0
    if (time == 0L || time == null) {
        return ""
    }

    // return in format of Today, Tomorrow or Yesterday if time is within 24 hours
    val now = Date().time
    val diff = now - time
    val diffInDays = diff / (24 * 60 * 60 * 1000)

    when (diffInDays) {
        0L -> {
            // return time in format of 1 hour ago, 2 hours ago, 3 hours ago etc
            val diffInHours = diff / (60 * 60 * 1000)
            return if (diffInHours > 0) {
                "$diffInHours hours ago"
            } else {
                // return time in format of 1 minute ago, 2 minutes ago, 3 minutes ago etc
                val diffInMinutes = diff / (60 * 1000)
                if (diffInMinutes > 0) {
                    "$diffInMinutes minutes ago"
                } else {
                    // return time in format of 1 second ago, 2 seconds ago, 3 seconds ago etc
                    val diffInSeconds = diff / 1000
                    if (diffInSeconds > 1) {
                        "$diffInSeconds seconds ago"
                    } else {
                        "Just now"
                    }
                }
            }
        }
        -1L -> {
            return "Yesterday"
        }
        -2L -> {
            return "Day Before Yesterday"
        }
        1L -> {
            return "Tomorrow"
        }
        2L -> {
            return "Day After Tomorrow"
        }
        // return date in string format if time is more than 24 hours
        // return in format 2 days ago, 3 days ago, 4 days ago etc
        else -> {
            val diffInDaysAbs = abs(diffInDays)
            return if (diffInDays > 0) {
                "$diffInDaysAbs days ago"
            } else {
                "In $diffInDaysAbs days"
            }
        }
    }
}

@Composable
fun ToDoApplication(
    state: Notes,
    navController: NavController? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,

    ) {
        Text(
            text = "To Do List",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        )

        if (state.list.isEmpty()) {
            Text(
                text = "No Items have been added yet",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
        }
        else{
            Text(
                text = "Items",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )

            LazyColumn(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            )
            {
                items(state.list.size) { index ->
                    val note = state.list[index]
                    ToDoNote(
                        text = note.text,
                        checked = note.checked,
                        date = convertLongToTime(note.date)
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { navController?.navigate("AddNote") },
            icon = { Icon(Icons.Filled.Add, null) },
            text = { Text(text = "Add") },
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)
        )
    }
}
