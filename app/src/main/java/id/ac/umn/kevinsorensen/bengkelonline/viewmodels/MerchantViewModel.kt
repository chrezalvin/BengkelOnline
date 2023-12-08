package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.ComplaintController
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Complaint
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MerchantUiState(
    val user: User? = null,
    val complaints: List<Complaint> = listOf(),
    val error: String = "",
    val titleError: String = "",
    val descriptionError: String = "",
)
class MerchantViewModel(): ViewModel(){
    private val database = Firebase;
    private val complaintController = ComplaintController(database);
    private val userController = UserController(database);

    private val _uiState = MutableStateFlow(MerchantUiState());
    val uiState = _uiState.asStateFlow();

    var userLocation by mutableStateOf(LatLng(0.0, 0.0))
        private set;

    fun updateUserLocation(lat: Double, long: Double){
        userLocation = LatLng(lat, long);
    }

    fun initializeHome(userId: String){
        userController.getUserById(userId){user ->
            if(user != null) {
                _uiState.value = _uiState.value.copy(user = user);
            }
        }
    }

    fun getNearbyComplaints(){
        complaintController.getNearbyComplaint(
            userLocation.latitude.toFloat(),
            userLocation.longitude.toFloat(),
            onSuccess = { complaints ->
                Log.d("MerchantViewModel", "getNearbyComplaints: $complaints");
                _uiState.value = _uiState.value.copy(complaints = complaints);
            },
            onFailure = { error ->
                Log.d("MerchantViewModel", "getNearbyComplaints: $error");
                _uiState.value = _uiState.value.copy(error = error);
            }
        )
    }
}