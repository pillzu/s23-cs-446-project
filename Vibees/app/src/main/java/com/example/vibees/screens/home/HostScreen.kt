package com.example.vibees.screens.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibees.Api.APIInterface
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Normalizer.Form
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


//val url = "http://127.0.0.1:5000"
//data class ResponseClass (val response: String)
//data class RequestModel (
//    val user_id: Int,
//    val party_name: String,
//    val date_time: Date?,
//    val street: String,
//    val city: String,
//    val province: String,
//    val postal_code: String,
//    val type: String,
//    val max_capacity: Int,
//    val entry_fees: Double,
//    val desc: String,
//    val thumbnail: Image?
//    )
//
//interface APIInterface {
//    @POST("/parties/host")
//    fun requestParty(@Body requestModel: RequestModel): Call<ResponseClass>
//}

//object serviceBuilder {
//    private val client = OkHttpClient.Builder().build()
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(url)
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
//        .build()
//
//    fun<T> buildService(service: Class<T>): T{
//        return retrofit.create(service)
//    }
//}

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    onChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    isEnabled: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = text,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        textStyle = TextStyle(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyBoardActions,
        enabled = isEnabled,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            disabledTextColor = Color.Black
        ),
        placeholder = {
            Text(text = placeholder, style = TextStyle(fontSize = 18.sp, color = Color.LightGray))
        }
    )
}

@Composable
fun FlexibleTextField(
    modifier: Modifier = Modifier.padding(10.dp),
    text: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    onChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    isEnabled: Boolean = true,
    width: Int,
    height: Int
) {
    OutlinedTextField(
        modifier = modifier.size(width.dp, height.dp),
        value = text,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        textStyle = TextStyle(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyBoardActions,
        enabled = isEnabled,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            disabledTextColor = Color.Black
        ),
        placeholder = {
            Text(text = placeholder, style = TextStyle(fontSize = 18.sp, color = Color.LightGray))
        }
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
fun HostScreen(name: String, onClick: () -> Unit) {

    val apiService = APIInterface()
    var userID by GlobalAppState::UserID

    var partyName by remember { mutableStateOf("") }
    val partyDate = remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    val partyTime = remember { mutableStateOf<LocalTime>(LocalTime.now()) }
    var partyType by remember { mutableStateOf("") }
    var maxCapacity by remember { mutableStateOf("") }
    var entryFee by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap =  remember { mutableStateOf<Bitmap?>(null) }
    val thumbnail: Image? = null
    var partyDateTimeStr: SimpleDateFormat? = null
    var partyDateTime: Date? = null

    var unitStreet by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    val partyContext = LocalContext.current
    val partyCalendar = Calendar.getInstance()
    val partyYear = partyCalendar.get(Calendar.YEAR)
    val partyMonth = partyCalendar.get(Calendar.MONTH)
    val partyDay = partyCalendar.get(Calendar.DAY_OF_MONTH)
    partyCalendar.time = Date()
    val partyDatePickerDialog = DatePickerDialog(
        partyContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            partyDate.value = LocalDate.of(mYear, mMonth, mDayOfMonth)
        }, partyYear, partyMonth, partyDay
    )

    val partyHour = partyCalendar.get(Calendar.HOUR_OF_DAY)
    val partyMinute = partyCalendar.get(Calendar.MINUTE)
    val partyTimePickerDialog = TimePickerDialog(
        partyContext,
        { _: TimePicker, hour: Int, minute: Int ->
            partyTime.value = LocalTime.of(hour, minute)
        }, partyHour, partyMinute, false
    )

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }


//    val retrofit = serviceBuilder.buildService(APIInterface::class.java)
//    val obj = RequestModel(1, partyName, partyDateTime, unitStreet, city, province, postalCode,
//                           partyType, maxCapacity, entryFee, description, thumbnail)
//
//    retrofit.requestParty(obj).enqueue(
//        object:Callback<ResponseClass> {
//            override fun onResponse(
//                call: Call<ResponseClass>,
//                response: Response<ResponseClass>
//            ) {
//                Log.d("TAG", "${response.body()?.response}")
//                Toast.makeText(partyContext, "${response.body()?.response}", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        }
//    )

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 60.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {

        val focusManager = LocalFocusManager.current

        Spacer(modifier = Modifier.padding(6.dp))
        Row {
            androidx.compose.material3.Text(
                modifier = Modifier.clickable { onClick() },
                text = "Host Party!",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color =Color.Black,
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))

        AppTextField(
            text = partyName,
            placeholder = "Party Name*",
            onChange = {
                partyName = it
            },
            imeAction = ImeAction.Next,//Show next as IME button
            keyboardType = KeyboardType.Text, //Plain text keyboard
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        // Party Date Input
        Row {
            Button(modifier = Modifier
                .size(200.dp, 48.dp),
                onClick = {
                    partyDatePickerDialog.show()
                }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)) {
                Text(text = "Schedule Date*", color = MaterialTheme.colorScheme.primary, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
            }
            androidx.compose.material3.Text(
                modifier = Modifier.padding(10.dp),
                text = "${partyDate.value}",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
//                Text("${partyDate.value}", textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.padding(8.dp))
//        Column {

//        }
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party Time Input
//        Row {
//            Button(modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//                onClick = {
//                    partyTimePickerDialog.show()
//                }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)) {
//                Text(text = "Schedule Time*", color = MaterialTheme.colorScheme.primary)
//            }
//            androidx.compose.material3.Text(
//                modifier = Modifier.padding(10.dp),
//                text = "${partyTime.value}",
//                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//            )
////                Text("${partyDate.value}", textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
//        }

        Row {
            Button(modifier = Modifier
                .size(200.dp, 48.dp),
                onClick = {
                    partyTimePickerDialog.show()
                }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)) {
                Text(text = "Schedule Time*", color = MaterialTheme.colorScheme.primary)
            }
            androidx.compose.material3.Text(
                modifier = Modifier.padding(10.dp),
                text = "${partyTime.value}",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
        }

//        Column {
//            Button(modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//                onClick = {
//                    partyTimePickerDialog.show()
//                }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)) {
//                Text(text = "Schedule Time*", color = MaterialTheme.colorScheme.primary)
//            }
//            Text("Selected Time: ${partyTime.value}", textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
//        }
//        Spacer(modifier = Modifier.padding(8.dp))

        AppTextField(
            text = unitStreet,
            placeholder = "Unit and Street Number*",
            onChange = {
                unitStreet = it
            },
            imeAction = ImeAction.Next,//Show next as IME button
            keyboardType = KeyboardType.Text, //Plain text keyboard
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Row {
                FlexibleTextField (
                    text = city,
                    placeholder = "City*",
                    onChange = {
                        city = it
                    },
                    imeAction = ImeAction.Next,//Show next as IME button
                    keyboardType = KeyboardType.Text, //Plain text keyboard
                    keyBoardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    width = 200,
                    height = 56
                )
                FlexibleTextField(
                    text = province,
                    placeholder = "Province*",
                    onChange = {
                        province = it
                    },
                    imeAction = ImeAction.Next,//Show next as IME button
                    keyboardType = KeyboardType.Text, //Plain text keyboard
                    keyBoardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    width = 200,
                    height = 56
                )
        }

        AppTextField(
            text = postalCode,
            placeholder = "Postal Code*",
            onChange = {
                postalCode = it
            },
            imeAction = ImeAction.Next,//Show next as IME button
            keyboardType = KeyboardType.Text, //Plain text keyboard
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        AppTextField(
            text = partyType,
            placeholder = "Party Type*",
            onChange = {
                partyType = it
            },
            imeAction = ImeAction.Next,//Show next as IME button
            keyboardType = KeyboardType.Text, //Plain text keyboard
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Row {
            FlexibleTextField (
                text = maxCapacity,
                placeholder = "Max Capacity",
                onChange = {
                    maxCapacity = it
                },
                imeAction = ImeAction.Next,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
                keyBoardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                width = 200,
                height = 56
            )
            FlexibleTextField (
                text = entryFee,
                placeholder = "Fees",
                onChange = {
                    entryFee = it
                },
                imeAction = ImeAction.Next,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
                keyBoardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                width = 200,
                height = 56
            )
        }

        AppTextField(
            text = description,
            placeholder = "Party Description",
            onChange = {
                description = it
            },
            imeAction = ImeAction.Next,//Show next as IME button
            keyboardType = KeyboardType.Text, //Plain text keyboard
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        // Party Name Input
//        Text("Party Name*")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = partyName,
//            onValueChange = { partyName = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party Street Addr Input
//        Text("Enter Address (Unit/Apt/Suite and Street Number)*")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = unitStreet,
//            onValueChange = { unitStreet = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party City Input
//        Text("Enter City*")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = city,
//            onValueChange = { city = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party Province Input
//        Text("Enter Province*")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = province,
//            onValueChange = { province = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party Postal Code Input
//        Text("Enter Postal Code*")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = postalCode,
//            onValueChange = { postalCode = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party Type Input
//        Text("Party Type*")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = partyType,
//            onValueChange = { partyType = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        //Party Capacity Input
//        Text("Enter Maximum Capacity")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = maxCapacity,
//            onValueChange = { maxCapacity = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party Entry Fee Input
//        Text("Enter Entry Fees")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = entryFee,
//            onValueChange = { entryFee = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

        // Party Description Input
//        Text(text = "Description")
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            value = description,
//            onValueChange = { description = it },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//            )
//        )
//        Spacer(modifier = Modifier.padding(8.dp))

//        Button(onClick = { launcher.launch("image/*") }, modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp),
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)) {
//            Text(text = "Pick Thumbnail", color = MaterialTheme.colorScheme.primary)
//        }
//

        // Submit Button
        Button(
            onClick = {
                var host_date = partyDate.value.atTime(partyTime.value)
                Log.d("TAG", host_date.toString())
                var dateString = host_date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                val obj = Party(userID, partyName, dateString, partyType,  maxCapacity.toInt(),
                    entryFee.toDouble(), description, unitStreet, city, province, postalCode, "", "", "")
                Log.d("TAG", obj.toString())

                // call endpoint /parties/host to create a party
                val callResponse = apiService.createParty(obj)
                val response = callResponse.enqueue(
                    object: Callback<ResponseMessage> {
                        override fun onResponse(
                            call: Call<ResponseMessage>,
                            response: Response<ResponseMessage>
                        ) {
                            Log.d("TAG", "${response.body()?.message}")
                            Toast.makeText(partyContext, "${response.body()?.message}", Toast.LENGTH_LONG).show()
                        }

                        override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                            Log.d("TAG", "FAILURE")
                            Log.d("TAG", t.message.toString())
                        }
                    }
                )
            },
            modifier = Modifier.padding(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)) {
            Text(text = "Create Party!", color = Color.Black)
        }
    }
}
