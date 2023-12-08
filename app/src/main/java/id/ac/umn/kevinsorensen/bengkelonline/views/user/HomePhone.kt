package id.ac.umn.kevinsorensen.bengkelonline.views.user

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.LatLng
import id.ac.umn.kevinsorensen.bengkelonline.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePhone(
    currentLocation: LatLng = LatLng(0.0, 0.0),
    problemDesc: String = "",
    bitmaps: List<Bitmap?> = List(3) { null as Bitmap? },

    onProblemDescChange: (String) -> Unit = {},
    onBitmapUpdate: (index: Int, Bitmap?) -> Unit = { _, _ -> },
    onOrder: () -> Unit = {},

    error: String = "",
    locationError: String = "",
    bitmapError: String = "",
) {
//    var bitmaps by remember { mutableStateOf(List(3) { null as Bitmap? }) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showChoiceDialog by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(-1) }

    val launchers = List(3) { index ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                onBitmapUpdate(index, if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                })
            }
        }
    }

    // Launchers for capturing or selecting images
    val cameraLaunchers = List(3) { index ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) {
            // Update the corresponding bitmap in the list
            onBitmapUpdate(index, it)
        }
    }

    val videoLaunchers = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { isVideoCaptured ->
        if (isVideoCaptured) {
            videoUri?.let { capturedVideoUri ->
                videoUri = capturedVideoUri
            }
        }
    }


    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        videoUri = uri
    }

    fun createTempFileUri(): Uri {
        val tempFile = File.createTempFile("capture_", ".mp4", context.externalCacheDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
    }

    fun openImagePicker(index: Int) {
        currentImageIndex = index
        showChoiceDialog = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
            contentDescription = null,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Ambil Foto Motor Anda",
            fontSize = 15.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            for (index in 0 until 3) {
                if (bitmaps[index] != null) {
                    Image(
                        bitmap = bitmaps[index]!!.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                            .clickable { showDialog = false }
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                            .clickable { openImagePicker(index) }
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
        if(bitmapError != ""){
            Text(
                text = bitmapError,
                fontSize = 15.sp,
                color = Color.Red,
            )
        }
        if (showChoiceDialog) {
            AlertDialog(
                onDismissRequest = { showChoiceDialog = false },
                title = { Text("Pilih Sumber Gambar") },
                confirmButton = {
                    Button(onClick = {
                        cameraLaunchers[currentImageIndex].launch()
                        showChoiceDialog = false
                    }) {
                        Text("Kamera")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        launchers[currentImageIndex].launch("image/*")
                        showChoiceDialog = false
                    }) {
                        Text("Galeri")
                    }
                },
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Set Lokasimu",
            fontSize = 15.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(20.dp)
                .width(400.dp)
                .height(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize() // Make the Row fill the Box
            ) {
                Text(
                    text = "Lat: ${currentLocation.latitude}, Lon: ${currentLocation.longitude}"
                    ,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton (
                    onClick = {
                    }
                ){
                    if(currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0){
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.Black
                        )
                    }
                    else {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Get Location",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
        if (locationError != "") {
            Text(
                text = locationError,
                fontSize = 15.sp,
                color = Color.Red,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Catatan",
            fontSize = 15.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = problemDesc,
            onValueChange = { onProblemDescChange(it) },
            label = { Text("Jelaskan Keluhan Anda") },
            modifier = Modifier
                .width(400.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                // Handle location button click here
                  onOrder()
            },
            modifier = Modifier
                .height(50.dp)
                .width(175.dp),
            colors = ButtonDefaults.buttonColors(
                Color.Red
            )
        ) {
            Text("PESAN SEKARANG")
        }

        if(error != "")
            Text(text = error, color = Color.Red);
    }
}