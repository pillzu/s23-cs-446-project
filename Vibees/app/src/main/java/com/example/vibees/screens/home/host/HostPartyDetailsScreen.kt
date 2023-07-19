package com.example.vibees.screens.home.host

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.naingaungluu.formconductor.FieldResult
import me.naingaungluu.formconductor.FormResult
import me.naingaungluu.formconductor.composeui.field
import me.naingaungluu.formconductor.composeui.form

@Composable
fun HostPartyDetailsScreen(
    onClick: () -> Unit,
) {
    var partyname by remember { mutableStateOf("")}
    var theme by remember { mutableStateOf("")}
    var fee by remember { mutableIntStateOf(0) }
    var description by remember { mutableStateOf("")}
    var drugfriendly by remember { mutableStateOf(false)}
    var byob by remember { mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, top = 25.dp, end = 10.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        form(HostPartyDetails::class) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Host a Party",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "The juicy details",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(20.dp),
                    textAlign = TextAlign.Start
                )

                val focusManager = LocalFocusManager.current

                field(HostPartyDetails::name) {
                    OutlinedTextField(
                        value = state.value?.value.orEmpty(),
                        onValueChange = {
                            partyname = it
                            setField(it)
                        } ,
                        isError = resultState.value is FieldResult.Error,
                        label = { Text("Party Name")},
                        placeholder = { Text("Party Name", color = Color.Gray)},
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
                        modifier = Modifier.padding(4.dp)
                    )
                }
                field(HostPartyDetails::theme) {
                    OutlinedTextField(
                        value = state.value?.value.orEmpty(),
                        onValueChange = {
                            theme = it
                            setField(it)
                        } ,
                        isError = resultState.value is FieldResult.Error,
                        label = { Text("Party Theme")},
                        placeholder = { Text("Party Theme", color = Color.Gray)},
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
                        modifier = Modifier.padding(4.dp)
                    )
                }
                field(HostPartyDetails::description) {
                    OutlinedTextField(
                        value = state.value?.value.orEmpty(),
                        onValueChange = {
                            description = it
                            setField(it)
                        } ,
                        isError = resultState.value is FieldResult.Error,
                        label = { Text("Description")},
                        placeholder = { Text("Description", color = Color.Gray)},
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
                            .height(120.dp)
                            .padding(4.dp)
                    )
                }
                field(HostPartyDetails::fee) {
                    OutlinedTextField(
                        value = state.value?.value.orEmpty(),
                        onValueChange = {
                            try {
                                fee = it.toInt()
                                setField(it)
                            } catch (_: NumberFormatException) {
                                Log.d("Invalid number", "Invalid number entered in fee")
                            }
                        } ,
                        isError = resultState.value is FieldResult.Error,
                        label = { Text("Fee")},
                        placeholder = { Text("Fee (Optional)", color = Color.Gray)},
                        enabled = true,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,//Show next as IME button
                            keyboardType = KeyboardType.Number, //Plain text keyboard
                        ),
                        modifier = Modifier.padding(4.dp)
                    )
                }
                field(HostPartyDetails::drugfriendly) {
                    Text(
                        text = "Does this party allow drug usage?",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Row(Modifier.selectableGroup(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Yes",
                                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            )
                            RadioButton(
                                selected = state.value?.value == "Yes",
                                onClick = {
                                    setField("Yes")
                                    drugfriendly = true
                                          },
                                modifier = Modifier
                                    .semantics { contentDescription = "Yes" }
                                    .padding(end = 50.dp)
                            )

                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No",
                                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            )
                            RadioButton(
                                selected = state.value?.value == "No",
                                onClick = {
                                    setField("No")
                                    drugfriendly = false
                                          },
                                modifier = Modifier
                                    .semantics { contentDescription = "No" }
                                    .padding(end = 50.dp)
                            )
                        }

                    }
                    Text(
                        text = if (resultState.value is FieldResult.Error) "Select an option"
                               else "",
                        color = Color.Red,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
                field(HostPartyDetails::byob) {
                    Text(
                        text = "Bring Your Own Booze/Snacks?",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(Modifier.selectableGroup(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Yes",
                                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            )
                            RadioButton(
                                selected = state.value?.value == "Yes",
                                onClick = {
                                    setField("Yes")
                                    byob = true
                                },
                                modifier = Modifier
                                    .semantics { contentDescription = "Yes" }
                                    .padding(end = 50.dp)
                            )

                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No",
                                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            )
                            RadioButton(
                                selected = state.value?.value == "No",
                                onClick = {
                                    setField("No")
                                    byob = false
                                },
                                modifier = Modifier
                                    .semantics { contentDescription = "No" }
                                    .padding(end = 50.dp)
                            )
                        }

                    }
                    Text(
                        text = if (resultState.value is FieldResult.Error) "Select an option"
                        else "",
                        color = Color.Red,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
            }

            TextButton(
                onClick = {
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
