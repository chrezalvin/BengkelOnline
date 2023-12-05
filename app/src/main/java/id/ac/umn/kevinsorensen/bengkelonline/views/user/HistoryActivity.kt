package id.ac.umn.kevinsorensen.bengkelonline.views.user

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import id.ac.umn.kevinsorensen.bengkelonline.api.ResourceCollector
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

data class Affirmation(
    val tanggal: Date,
    val latitude: Double,
    val longitude: Double,
    val namaBengkel: String,
    val status: String
)

class DataSource {
    fun loadAffirmations(): List<Affirmation> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return listOf(
            Affirmation(dateFormat.parse("01-01-2023")!!, -6.175110, 106.865036, "Bengkel A", "Selesai"),
            Affirmation(dateFormat.parse("05-01-2023")!!, -6.208763, 106.845599, "Bengkel B", "Berjalan"),
            Affirmation(dateFormat.parse("10-01-2023")!!, -6.914744, 107.609810, "Bengkel C", "Batal"),
            Affirmation(dateFormat.parse("15-01-2023")!!, -7.795580, 110.369490, "Bengkel D", "Batal"),
            Affirmation(dateFormat.parse("20-01-2023")!!, -6.932444, 107.604738, "Bengkel E", "Berjalan"),
            Affirmation(dateFormat.parse("25-01-2023")!!, -6.917464, 107.619123, "Bengkel F", "Berjalan"),
            Affirmation(dateFormat.parse("01-02-2023")!!, -7.257472, 112.752090, "Bengkel G", "Selesai"),
            Affirmation(dateFormat.parse("05-02-2023")!!, -7.966620, 112.632629, "Bengkel H", "Selesai"),
            Affirmation(dateFormat.parse("10-02-2023")!!, -6.175392, 106.827153, "Bengkel I", "Batal"),
            Affirmation(dateFormat.parse("15-02-2023")!!, -6.402484, 106.794243, "Bengkel J", "Batal")
        )
    }
}

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId")
        Log.d(TAG, "userId: $userId")

        setContent {
            historyPage(this)
        }
    }

    companion object {
        private const val TAG = "HistoryActivity"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun historyPage(
    activity: ComponentActivity,
) {
    val navController = rememberNavController()
    val affirmations = remember { DataSource().loadAffirmations() }

    Scaffold(
        topBar = {
            TopNavigation3(activity)
        },
        content = { p ->
            Column(modifier = Modifier.padding(p)) {
                NavigationTop(navController, affirmations)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigation3(activity: ComponentActivity) {

    TopAppBar(
        title = {
            Text(text = "History")
        },
        navigationIcon = {
            IconButton(onClick = {
                // destroy activity
                activity.finish()
            }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go Back")
            }
        }
    )
}

@Composable
fun NavigationTop(navController: NavController, affirmations: List<Affirmation>) {
    val (selectedTabIndex, setSelectedTabIndex) = remember { mutableStateOf(0) }

    TabRow(selectedTabIndex = selectedTabIndex) {
        Tab(
            text = { Text("Berjalan") },
            selected = selectedTabIndex == 0,
            onClick = { setSelectedTabIndex(0) }
        )
        Tab(
            text = { Text("Selesai") },
            selected = selectedTabIndex == 1,
            onClick = { setSelectedTabIndex(1) }
        )
        Tab(
            text = { Text("Batal") },
            selected = selectedTabIndex == 2,
            onClick = { setSelectedTabIndex(2) }
        )
    }

    displayAffirmations(affirmations) {
        when (selectedTabIndex) {
            0 -> it.status == "Berjalan"
            1 -> it.status == "Selesai"
            2 -> it.status == "Batal"
            else -> false
        }
    }
}

@Composable
fun displayAffirmations(affirmations: List<Affirmation>, filter: (Affirmation) -> Boolean) {
    LazyColumn {
        items(affirmations.filter(filter)) { affirmation ->
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                Text(
                    text = "Tanggal: ${formatDate(affirmation.tanggal)}",
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    color = Color.Black
                )
                Text(
                    text = "Longitude: ${affirmation.longitude}",
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Latitude: ${affirmation.latitude}",
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Bengkel: ${affirmation.namaBengkel}",
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(date)
}
