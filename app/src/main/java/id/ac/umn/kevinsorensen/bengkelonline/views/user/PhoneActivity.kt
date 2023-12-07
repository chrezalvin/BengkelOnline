package id.ac.umn.kevinsorensen.bengkelonline.views.user

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
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
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.ui.theme.BengkelOnlineTheme
import java.io.File

class PhoneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BengkelOnlineTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomePhone()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomePhone() {
    var bitmaps by remember { mutableStateOf(List(3) { null as Bitmap? }) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var showChoiceDialog by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(-1) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val mContext = LocalContext.current

    val launchers = List(3) { index ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                bitmaps = bitmaps.toMutableList().apply {
                    this[index] = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                }.toList()
            }
        }
    }

    // Launchers for capturing or selecting images
    val cameraLaunchers = List(3) { index ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) {
            // Update the corresponding bitmap in the list
            it?.let { bitmap ->
                bitmaps = bitmaps.toMutableList().apply { this[index] = bitmap }.toList()
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
        Text(
            text = "Set Your Location",
            fontSize = 15.sp,
            color = Color.Blue,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                // Handle location button click here
            },
            modifier = Modifier
                .height(50.dp)
                .width(200.dp),
            colors = ButtonDefaults.buttonColors(
                Color.Blue
            )
        ) {
            Text("Get Current Location")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Tambahkan Video Kondisi Motor Anda Saat ini (Wajib : 1)",
            fontSize = 15.sp,
            color = Color.Blue,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray)
                .clickable {
                    showDialog = true
                }
        ) {
            if (videoUri != null) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_play_circle_24),
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End // Align items to the end (right)
                    ) {
                        IconButton(
                            onClick = {
                                if (videoUri != null) {
                                    showDeleteDialog = true
                                }
                            },
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                contentDescription = "Trash",
                                tint = Color.White
                            )

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showDeleteDialog = false
                                    },
                                    title = {
                                        Text("Delete")
                                    },
                                    text = {
                                        Text("Are you sure you want to Delete?")
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                videoUri = null
                                                showDeleteDialog = false
                                            }
                                        ) {
                                            Text("Yes")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = {
                                                showDeleteDialog = false
                                            }
                                        ) {
                                            Text("No")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add Video",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                )
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Select Video Source") },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("Record Video")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        videoPickerLauncher.launch("video/*")
                        showDialog = false
                    }) {
                        Text("Choose from Gallery")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Tambahkan Foto Kondisi Motor Anda Saat ini (Wajib : 1, Maks : 3)",
            fontSize = 15.sp,
            color = Color.Blue,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
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
                            .background(Color.Gray)
                            .clickable { showDialog = false }
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_image_24),
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                            .clickable { openImagePicker(index) }
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
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
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Jelaskan Keluhan Anda") }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                // Handle location button click here
            },
            modifier = Modifier
                .height(50.dp)
                .width(200.dp),
            colors = ButtonDefaults.buttonColors(
                Color.Blue
            )
        ) {
            Text("Pesan Sekarang")
        }
    }
}