package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PhoneUiState(
    val bitmaps: List<Bitmap?> = List(3){null as Bitmap?},
    val videoUri: Uri? = null,
)
class PhoneActivityViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(PhoneUiState());
    val uiState = _uiState.asStateFlow();

    fun setBitmap(context: Context, index: Int, uri: Uri){
        _uiState.update {
            val bitmaps = it.bitmaps.toMutableList();
            bitmaps[index] = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            it.copy(bitmaps = bitmaps.toList());
        }
    }
}