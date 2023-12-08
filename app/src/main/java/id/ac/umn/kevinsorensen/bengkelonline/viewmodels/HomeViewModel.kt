package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.ac.umn.kevinsorensen.bengkelonline.SettingsStore
import id.ac.umn.kevinsorensen.bengkelonline.api.ComplaintController
import id.ac.umn.kevinsorensen.bengkelonline.api.ProductController
import id.ac.umn.kevinsorensen.bengkelonline.api.ResourceCollector
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class HomeState(
    val user: User? = null,
    val profilePhotoUri: Uri? = null,
    val bitmaps: List<Bitmap?> = List(3) {null as Bitmap?},
    val bitmapError: String = "",
    val locationError: String = "",
    val error: String = "",
);

class HomeViewModel(
    private val settingsStore: SettingsStore,
    database: Firebase = Firebase
): ViewModel()
{
    private val _uiState = MutableStateFlow(HomeState());
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow();

    private val userController = UserController(database);
    private val complaintController = ComplaintController(database);
    private val resourceCollector = ResourceCollector(database.storage);

    var currentLocation by mutableStateOf(LatLng(0.0, 0.0))
        private set;

    var complaintDescription by mutableStateOf("")
        private set;

    fun initializeHome(profileId: String){
        if(_uiState.value.user != null) {
            Log.d("HomeViewModel", "initializeHome: user is not null");
            return;
        }

        Log.d("HomeViewModel", "initializeHome: $profileId");

        userController.getUserById(profileId) { user ->
            if(user != null) {
                Log.d("HomeViewModel", "initializeHome: $user");
                resourceCollector.getProfilePhoto(user.photo ?: "") { uri ->
                    _uiState.update {
                        it.copy(profilePhotoUri = uri);
                    }
                }
                _uiState.update {
                    it.copy(user = user);
                }
            }
            else
                Log.d("HomeViewModel", "initializeHome: user is null")
        }
    }

    fun updateCurrentLocation(lat: Float, long: Float){
        currentLocation = LatLng(lat.toDouble(), long.toDouble());
    }

    fun updateComplaintDescription(description: String){
        complaintDescription = description;
    }

    fun logout(onSuccess: () -> Unit){
        viewModelScope.launch {
            settingsStore.saveText("");
            onSuccess();
        }
    }

    fun imagesChecking(){
        if(_uiState.value.bitmaps.count{ it == null } == _uiState.value.bitmaps.size){
            _uiState.update {
                it.copy(bitmapError = "Please fill out this field");
            }
        }
        else
            _uiState.update {
                it.copy(bitmapError = "");
            }

    }

    fun locationChecking(){
        if(currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0){
            _uiState.update {
                it.copy(locationError = "please agree to the location permission first");
            }
        }
        else
            _uiState.update {
                it.copy(locationError = "");
            }
    }

    fun validateInputs(onSuccess: () -> Unit){
        imagesChecking();
        locationChecking();

        if(
            _uiState.value.bitmapError.isEmpty() &&
            _uiState.value.locationError.isEmpty()
            ){
            onSuccess();
        }
    }

    fun orderComplaint(onSuccess: () -> Unit){
        if(_uiState.value.user == null){
            _uiState.update {
                it.copy(error = "User is not logged in");
            }
            return;
        }

        validateInputs {
            complaintController.submitComplaint(
                _uiState.value.user!!.id,
                currentLocation.longitude.toFloat(),
                currentLocation.latitude.toFloat(),
                complaintDescription,
                List(3) {""},
                onSuccess = {
                    onSuccess();

                    // update user location
                    userController.updateUserLocation(
                        _uiState.value.user!!.id,
                        currentLocation.longitude.toFloat(),
                        currentLocation.latitude.toFloat()
                    ) { success ->
                        if(success)
                            Log.d("HomeViewModel", "orderComplaint: user location updated");
                        else
                            Log.d("HomeViewModel", "orderComplaint: user location failed to update");
                    }
                },
                onFailure = { ex ->
                    _uiState.update {
                        it.copy(error = "Failed to submit complaint: $ex");
                    }
                }
            )
        }
    }

    fun updateBitmaps(index: Int, bitmap: Bitmap?){
        _uiState.update {
            val bitmaps = it.bitmaps.toMutableList();
            bitmaps[index] = bitmap;
            it.copy(bitmaps = bitmaps);
        }
    }

    fun updateBitmaps(){

    }
}