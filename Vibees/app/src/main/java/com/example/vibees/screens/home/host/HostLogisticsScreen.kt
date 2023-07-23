package com.example.vibees.screens.home.host

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vibees.GlobalAppState
import me.naingaungluu.formconductor.FieldResult
import me.naingaungluu.formconductor.FormResult
import me.naingaungluu.formconductor.composeui.field
import me.naingaungluu.formconductor.composeui.form
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HostLogisticsScreen(
    onClick: () -> Unit,
) {
    var unitStreet by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    val partystore by GlobalAppState::PartyStore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 10.dp, top = 25.dp, end = 10.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        form(HostLogistics::class) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Host a Party",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "The boring stuff first...",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(20.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Start
                )

                val focusManager = LocalFocusManager.current

                field(HostLogistics::unitandstreet) {
                    OutlinedTextField(
                        value = state.value?.value.orEmpty(),
                        onValueChange = {
                            unitStreet = it
                            setField(it)
                        } ,
                        isError = resultState.value is FieldResult.Error,
                        label = { Text("Unit and Street")},
                        placeholder = { Text("Unit and Street*", color = Color.Gray)},
                        enabled = true,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,//Show next as IME button
                            keyboardType = KeyboardType.Text, //Plain text keyboard
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .height(64.dp)
                            .fillMaxWidth()
                    )
                }
                field(HostLogistics::city) {
                    OutlinedTextField(
                        value = state.value?.value.orEmpty(),
                        onValueChange = {
                            city = it
                            setField(it)
                        },
                        isError = resultState.value is FieldResult.Error,
                        label = { Text("City")},
                        placeholder = { Text("City*", color = Color.Gray)},
                        enabled = true,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,//Show next as IME button
                            keyboardType = KeyboardType.Text, //Plain text keyboard
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .height(64.dp)
                            .fillMaxWidth()
                    )
                }
                field(HostLogistics::postalcode) {
                    OutlinedTextField(
                        value = state.value?.value.orEmpty(),
                        onValueChange = {
                            postalCode = it
                            setField(it)
                        },
                        isError = resultState.value is FieldResult.Error,
                        label = { Text("Postal Code")},
                        placeholder = { Text("Postal Code*", color = Color.Gray)},
                        supportingText = {
                            if (resultState.value is FieldResult.Error) Text("6 characters only")
                            else Text("")
                        },
                        enabled = true,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,//Show next as IME button
                            keyboardType = KeyboardType.Text, //Plain text keyboard
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .height(84.dp)
                            .fillMaxWidth()
                    )
                }
                field(HostLogistics::province) {
                    province = provinceDropdownMenu()
                    setField(province)
                }
                field(HostLogistics::date) {
                    date = datePickerMenu()
                    setField(date)
                }
                field(HostLogistics::time) {
                    time = timePickerMenu()
                    setField(time)
                }
            }

            TextButton(
                onClick = {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy,hh:mm a")
                    partystore?.street = unitStreet
                    partystore?.city = city
                    partystore?.postal_code = postalCode
                    partystore?.prov = province
                    partystore?.date_time = LocalDateTime.parse("$date,$time", formatter)
                    Log.d("STORE", partystore.toString())
                    onClick()
                          },
                modifier = Modifier.align(Alignment.End),
                enabled = this.formState.value is FormResult.Success
            ) {
                Text(
                    "Next",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = if (formState.value is FormResult.Success) Color.Blue else Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Arrow right"
                )
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun provinceDropdownMenu(): String {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Alberta", "British Columbia", "Manitoba", "New Brunswick",
        "Newfoundland and Labrador", "Nova Scotia", "Ontario", "Prince Edward Island",
        "Quebec", "Saskatchewan")
    var selectedIndex by remember { mutableIntStateOf(0) }
    Box(modifier = Modifier
        .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = items[selectedIndex],
                onValueChange = {},
                label = { Text("Province") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .padding(horizontal = 4.dp)
                    .height(64.dp)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = s,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        onClick = {
                            selectedIndex = index
                            expanded = false
                        }
                    )
                }
            }
        }
    }
    return items[selectedIndex]
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePickerMenu(): String {
    var selectedDate by remember { mutableStateOf("") }
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }

    OutlinedTextField(
        value = getDate(selectedDate),
        onValueChange = {},
        label = { Text("Date") },
        readOnly = true,
        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "date") },
        modifier = Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        openDialog = true
                    }
                }
            }
            .padding(4.dp)
            .height(64.dp)
            .fillMaxWidth()
    )

    if (openDialog) {

        DatePickerDialog(
            onDismissRequest = {
                openDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = (datePickerState.selectedDateMillis?.plus(5 * 3600000))
                            .toString()
                        openDialog = false

                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = dateValidator()
            )
        }
    }
    return getDate(selectedDate)
}

fun dateValidator(): (Long) -> Boolean {
    return {
            timeInMillis ->
        val calenderDate = Calendar.getInstance()
        calenderDate.timeInMillis = timeInMillis
        timeInMillis > Calendar.getInstance().timeInMillis
    }
}

@SuppressLint("SimpleDateFormat")
private fun getDate(s: String): String {
    return if (s != "") {
        SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            .format(Date(s.toLong())).toString()
    } else {
        ""
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun timePickerMenu(): String {
    var selectedTime by remember { mutableStateOf("") }
    var openDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val formatter = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    OutlinedTextField(
        value = selectedTime,
        onValueChange = {},
        label = { Text("Time") },
        readOnly = true,
        trailingIcon = { Icon(
            nestClockFarsightAnalog(),
            "clock",
            modifier = Modifier.scale(0.7F)
        ) },
        modifier = Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        openDialog = true
                    }
                }
            }
            .padding(4.dp)
            .height(64.dp)
            .fillMaxWidth()
    )

    if (openDialog) {
        TimePickerDialog(
            onCancel = { openDialog = false },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                cal.set(Calendar.MINUTE, timePickerState.minute)
                cal.isLenient = false
                selectedTime = formatter.format(cal.time)
                openDialog = false
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }
    return selectedTime
}

// need this since Google apparently has documentation that works only in the future :/
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}

@Composable
fun nestClockFarsightAnalog(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "nest_clock_farsight_analog",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(21.333f, 20.417f)
                lineToRelative(4.125f, 4.166f)
                quadToRelative(0.417f, 0.375f, 0.396f, 0.917f)
                quadToRelative(-0.021f, 0.542f, -0.396f, 0.917f)
                quadToRelative(-0.416f, 0.416f, -0.958f, 0.416f)
                reflectiveQuadToRelative(-0.917f, -0.416f)
                lineToRelative(-3.958f, -3.959f)
                quadToRelative(-0.5f, -0.458f, -0.708f, -1.041f)
                quadToRelative(-0.209f, -0.584f, -0.209f, -1.209f)
                verticalLineToRelative(-5.5f)
                quadToRelative(0f, -0.541f, 0.375f, -0.937f)
                reflectiveQuadToRelative(0.917f, -0.396f)
                quadToRelative(0.542f, 0f, 0.938f, 0.396f)
                quadToRelative(0.395f, 0.396f, 0.395f, 0.937f)
                close()
                moveTo(20f, 6.208f)
                quadToRelative(0.542f, 0f, 0.938f, 0.396f)
                quadToRelative(0.395f, 0.396f, 0.395f, 0.938f)
                verticalLineToRelative(0.833f)
                quadToRelative(0f, 0.542f, -0.395f, 0.938f)
                quadToRelative(-0.396f, 0.395f, -0.938f, 0.395f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.395f)
                quadToRelative(-0.375f, -0.396f, -0.375f, -0.938f)
                verticalLineToRelative(-0.833f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                close()
                moveTo(33.792f, 20f)
                quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                reflectiveQuadToRelative(-0.938f, 0.375f)
                horizontalLineToRelative(-0.833f)
                quadToRelative(-0.542f, 0f, -0.937f, -0.375f)
                quadToRelative(-0.396f, -0.375f, -0.396f, -0.917f)
                reflectiveQuadToRelative(0.396f, -0.938f)
                quadToRelative(0.395f, -0.395f, 0.937f, -0.395f)
                horizontalLineToRelative(0.833f)
                quadToRelative(0.542f, 0f, 0.938f, 0.395f)
                quadToRelative(0.396f, 0.396f, 0.396f, 0.938f)
                close()
                moveTo(20f, 30.292f)
                quadToRelative(0.542f, 0f, 0.938f, 0.396f)
                quadToRelative(0.395f, 0.395f, 0.395f, 0.937f)
                verticalLineToRelative(0.833f)
                quadToRelative(0f, 0.542f, -0.395f, 0.938f)
                quadToRelative(-0.396f, 0.396f, -0.938f, 0.396f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.396f)
                reflectiveQuadToRelative(-0.375f, -0.938f)
                verticalLineToRelative(-0.833f)
                quadToRelative(0f, -0.542f, 0.375f, -0.937f)
                quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                close()
                moveTo(9.708f, 20f)
                quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                quadToRelative(-0.395f, 0.375f, -0.937f, 0.375f)
                horizontalLineToRelative(-0.833f)
                quadToRelative(-0.542f, 0f, -0.938f, -0.375f)
                quadToRelative(-0.396f, -0.375f, -0.396f, -0.917f)
                reflectiveQuadToRelative(0.396f, -0.938f)
                quadToRelative(0.396f, -0.395f, 0.938f, -0.395f)
                horizontalLineToRelative(0.833f)
                quadToRelative(0.542f, 0f, 0.937f, 0.395f)
                quadToRelative(0.396f, 0.396f, 0.396f, 0.938f)
                close()
                moveTo(20f, 36.375f)
                quadToRelative(-3.375f, 0f, -6.375f, -1.292f)
                quadToRelative(-3f, -1.291f, -5.208f, -3.521f)
                quadToRelative(-2.209f, -2.229f, -3.5f, -5.208f)
                quadTo(3.625f, 23.375f, 3.625f, 20f)
                quadToRelative(0f, -3.417f, 1.292f, -6.396f)
                quadToRelative(1.291f, -2.979f, 3.521f, -5.208f)
                quadToRelative(2.229f, -2.229f, 5.208f, -3.5f)
                reflectiveQuadTo(20f, 3.625f)
                quadToRelative(3.417f, 0f, 6.396f, 1.292f)
                quadToRelative(2.979f, 1.291f, 5.208f, 3.5f)
                quadToRelative(2.229f, 2.208f, 3.5f, 5.187f)
                reflectiveQuadTo(36.375f, 20f)
                quadToRelative(0f, 3.375f, -1.292f, 6.375f)
                quadToRelative(-1.291f, 3f, -3.5f, 5.208f)
                quadToRelative(-2.208f, 2.209f, -5.187f, 3.5f)
                quadToRelative(-2.979f, 1.292f, -6.396f, 1.292f)
                close()
                moveToRelative(0f, -2.625f)
                quadToRelative(5.75f, 0f, 9.75f, -4.021f)
                reflectiveQuadToRelative(4f, -9.729f)
                quadToRelative(0f, -5.75f, -4f, -9.75f)
                reflectiveQuadToRelative(-9.75f, -4f)
                quadToRelative(-5.708f, 0f, -9.729f, 4f)
                quadToRelative(-4.021f, 4f, -4.021f, 9.75f)
                quadToRelative(0f, 5.708f, 4.021f, 9.729f)
                quadTo(14.292f, 33.75f, 20f, 33.75f)
                close()
                moveTo(20f, 20f)
                close()
            }
        }.build()
    }
}
