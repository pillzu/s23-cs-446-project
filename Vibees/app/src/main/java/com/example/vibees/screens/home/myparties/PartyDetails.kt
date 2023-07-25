package com.example.vibees.screens.home.myparties

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.example.vibees.Api.APIInterface
import com.example.vibees.GlobalAppState
import com.example.vibees.graphs.HostScreens
import com.example.vibees.qr_scanner.PreviewViewComposable
import com.example.vibees.screens.home.host.PartyStore
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

var URL = "https://www.youtube.com/watch?v=xvFZjo5PgG0&ab_channel=Duran"

@Composable
fun checkAndRequestCameraPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
        // Request a permission
        SideEffect {
            launcher.launch(permission)
        }
    }
}
@androidx.camera.core.ExperimentalGetImage
@Composable
fun PartyDetails(
    navController: NavHostController,
    id: String,
) {
    var userID by GlobalAppState::UserID
    val apiService = APIInterface()
    var partyDetails by GlobalAppState::PartyDetails
    var partystore by GlobalAppState::PartyStore
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current


    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .size(40.dp)
                    .clickable {
                        clipboardManager.setText(AnnotatedString("https://vibees.ca/party/${id}"))
                        Toast
                            .makeText(
                                context,
                                "Invite link copied to clipboard!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
            if (partyDetails?.host_id == userID) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .size(40.dp)
                        .clickable {
                            // populate fields
                            partystore = PartyStore(
                                partyDetails?.street,
                                partyDetails?.city,
                                partyDetails?.prov,
                                partyDetails?.postal_code,
                                partyDetails?.date_time,
                                partyDetails?.name,
                                partyDetails?.type,
                                partyDetails?.entry_fee?.roundToInt(),
                                partyDetails?.desc,
                                partyDetails?.drug,
                                partyDetails?.byob,
                                partyDetails?.tags,
                                partyDetails?.party_avatar_url,
                                partyDetails?.host_id,
                                partyDetails?.max_cap,
                                partyDetails?.party_id,
                                partyDetails?.host_name,
                                partyDetails?.qr_endpoint,
                                isedit = true,
                                partyDetails?.attend_count
                            )
                            navController.navigate(HostScreens.Step1.route)
                        }
                )
            }

        }
        // QR Code section
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(top = 50.dp)
        ) {
            if (partyDetails?.host_id != userID) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .clip(RoundedCornerShape(15.dp))
                ) {

                    Barcode(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 20.dp, vertical = 15.dp)
                            .width(250.dp)
                            .height(250.dp),
                        resolutionFactor = 10, // Optionally, increase the resolution of the generated image
                        type = BarcodeType.QR_CODE, // pick the type of barcode you want to render
                        value = "${URL}${partyDetails?.qr_endpoint}" // The textual representation of this code
                    )
                    Text(
                        text = partyDetails?.name!!,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(15.dp)
                    )
                }
            } else {
                val context = LocalContext.current

                val permission = Manifest.permission.CAMERA
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (!isGranted) {
                        Toast.makeText(
                            context,
                            "Failed to initialize camera...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                checkAndRequestCameraPermission(context, permission, launcher)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                ) {
                    PreviewViewComposable()
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Party Icon",
                modifier = Modifier
                    .size(100.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = partyDetails?.name!!,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Hosted by ${partyDetails?.host_name}",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Date",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        color = Color.Black, text = partyDetails?.date_time!!.format(
                            DateTimeFormatter.ISO_DATE
                        )
                    )
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Location",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = partyDetails?.street!!)
                    Text(
                        color = Color.Black,
                        text = "${partyDetails?.city}, ${partyDetails?.prov}"
                    )
                    Text(color = Color.Black, text = partyDetails?.postal_code!!)
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Maximum Capacity",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = partyDetails?.max_cap.toString())
                }
            }

            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Type",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(color = Color.Black, text = "EDM")
                    Text(color = Color.Black, text = "Alcohol-free")
                    Text("")
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Entry Fees",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(color = Color.Black, text = partyDetails?.entry_fee.toString())
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Description",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(color = Color.Black, text = partyDetails?.desc!!)
            }
        }
    }
}