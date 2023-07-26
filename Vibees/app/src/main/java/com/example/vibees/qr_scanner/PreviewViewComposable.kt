package com.example.vibees.qr_scanner

import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.vibees.Api.VibeesApi
import com.example.vibees.utils.decodeValue
import com.example.vibees.utils.extractLastTwoUUIDs
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@Composable
fun PreviewViewComposable(
    navController: NavHostController,
    party_id: String,
) {

    var api = VibeesApi()

    AndroidView({ context ->
        val cameraExecutor = Executors.newSingleThreadExecutor()
        val previewView = PreviewView(context).also {
            it.scaleType = PreviewView.ScaleType.FILL_CENTER
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyser(callback = {qr_endpoint ->
                        var ids = extractLastTwoUUIDs(qr_endpoint!!)

                        fun partyFailed(error: Throwable) {
                            Toast.makeText(context, "${error.message}", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        Log.d("TAG party QR", "${ids}")
                        Log.d("TAG party QR host", "${party_id}")

                        if (ids[0] != party_id) {
                            partyFailed(Throwable("Invalid party attendee! Please try another party"))
                        } else {
                            api.checkQrAttendee(
                                decodeValue(qr_endpoint!!),
                                successfn = { response ->
                                    Toast.makeText(
                                        context,
                                        "${response.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                },
                                failurefn = { error -> partyFailed(error) }
                            )
                        }

                    }))
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    context as ComponentActivity, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e("DEBUG", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
        previewView
    },
        modifier = Modifier
            .size(width = 250.dp, height = 250.dp))
}