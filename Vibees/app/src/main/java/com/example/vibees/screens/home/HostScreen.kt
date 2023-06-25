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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val url = "http://127.0.0.1:5000"
data class ResponseClass (val response: String)
data class RequestModel (
    val user_id: Int,
    val party_name: String,
    val date_time: Date?,
    val street: String,
    val city: String,
    val province: String,
    val postal_code: String,
    val type: String,
    val max_capacity: Int,
    val entry_fees: Double,
    val desc: String,
    val thumbnail: Image?
    )

interface APIInterface {
    @POST("/parties/host")
    fun requestParty(@Body requestModel: RequestModel): Call<ResponseClass>
}

object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun HostScreen(name: String, onClick: () -> Unit) {

    var partyName by remember { mutableStateOf("") }
    val partyDate = remember { mutableStateOf("") }
    val partyTime = remember { mutableStateOf("") }
    var partyType by remember { mutableStateOf("") }
    var maxCapacity by remember { mutableIntStateOf(0) }
    var entryFee by remember { mutableDoubleStateOf(0.0) }
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
            partyDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, partyYear, partyMonth, partyDay
    )

    val partyHour = partyCalendar.get(Calendar.HOUR_OF_DAY)
    val partyMinute = partyCalendar.get(Calendar.MINUTE)
    val partyTimePickerDialog = TimePickerDialog(
        partyContext,
        { _: TimePicker, hour: Int, minute: Int ->
            partyTime.value = "$hour:$minute"
        }, partyHour, partyMinute, false
    )

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val retrofit = ServiceBuilder.buildService(APIInterface::class.java)
    val obj = RequestModel(1, partyName, partyDateTime, unitStreet, city, province, postalCode,
                           partyType, maxCapacity, entryFee, description, thumbnail)

    retrofit.requestParty(obj).enqueue(
        object:Callback<ResponseClass> {
            override fun onResponse(
                call: Call<ResponseClass>,
                response: Response<ResponseClass>
            ) {
                Log.d("TAG", "${response.body()?.response}")
                Toast.makeText(partyContext, "${response.body()?.response}", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                TODO("Not yet implemented")
            }
        }
    )

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 60.dp)
        .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.padding(6.dp))
        Row {
            androidx.compose.material3.Text(
                modifier = Modifier.clickable { onClick() },
                text = "Host Party!",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))

        // Party Name Input
        Text("Party Name*")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = partyName,
            onValueChange = { partyName = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Date Input
        Column {
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                onClick = {
                partyDatePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
                Text(text = "Schedule Date*", color = MaterialTheme.colorScheme.primary)
            }
            Text("Selected Date: ${partyDate.value}", textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Time Input
        Column {
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                onClick = {
                partyTimePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
                Text(text = "Schedule Time*", color = MaterialTheme.colorScheme.primary)
            }
            Text("Selected Time: ${partyTime.value}", textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Street Addr Input
        Text("Enter Address (Unit/Apt/Suite and Street Number)*")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = unitStreet,
            onValueChange = { unitStreet = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Party City Input
        Text("Enter City*")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = city,
            onValueChange = { city = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Province Input
        Text("Enter Province*")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = province,
            onValueChange = { province = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Postal Code Input
        Text("Enter Postal Code*")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = postalCode,
            onValueChange = { postalCode = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Type Input
        Text("Party Type*")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = partyType,
            onValueChange = { partyType = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        //Party Capacity Input
        Text("Enter Maximum Capacity")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = "$maxCapacity",
            onValueChange = { maxCapacity = it.toInt() },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Entry Fee Input
        Text("Enter Entry Fees")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = "$entryFee",
            onValueChange = { entryFee = it.toDouble() },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Party Description Input
        Text(text = "Description")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = description,
            onValueChange = { description = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))

        Button(onClick = { launcher.launch("image/*") }, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
               colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
            Text(text = "Pick Thumbnail", color = MaterialTheme.colorScheme.primary)
        }

        // Submit Button
        Button(
            onClick = {
                val dateString = "$partyYear-$partyMonth-$partyDay $partyDay:$partyMinute:00"
                partyDateTimeStr = SimpleDateFormat("yyyy-MM-DD HH:MM:SS")
                partyDateTime = partyDateTimeStr!!.parse(dateString)
            },
            modifier = Modifier.padding(10.dp),
               colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)) {
            Text(text = "Create Party!", color = MaterialTheme.colorScheme.primary)
        }
    }
}