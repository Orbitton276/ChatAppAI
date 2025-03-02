package com.data.chatappai

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.rememberAsyncImagePainter
import com.data.chatappai.presentation.screens.ChatScreen
import com.data.chatappai.presentation.screens.MainConversationScreen
import com.data.chatappai.presentation.screens.NewConversationScreen
import com.data.chatappai.presentation.screens.ProfileScreen
import com.data.chatappai.presentation.viewmodel.MainViewModel
import com.data.chatappai.presentation.viewmodel.MessageViewModel
import com.data.chatappai.presentation.viewmodel.ProfileViewModel
import com.data.chatappai.ui.theme.ChatAppAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("Permission", "Contacts permission granted")
                val viewModel: MainViewModel by viewModels()
                viewModel.getAllUsers()
            } else {
                Log.d("Permission", "Contacts permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatAppAITheme {

                println(BuildConfig.OPENAI_API_KEY)
                MainContent()

            }
        }

        checkContactsPermission()
//        signIn()
    }

    private fun checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
//            fetchContacts(applicationContext)
        } else {
            requestContactsPermission.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val viewModel = hiltViewModel<MainViewModel>()

    // State for initial profile check
    var startDest by remember { mutableStateOf("profile/{isIntro}") }

    // Perform the profile check only once during app startup
    var hasProfile by remember { mutableStateOf<Boolean?>(null) }

    // LaunchedEffect runs once and collects the flow
    LaunchedEffect(profileViewModel.hasProfile) {
        profileViewModel.hasProfile.collect { profileExists ->
            if (profileExists != null && hasProfile == null) {
                hasProfile = profileExists
            }
        }
    }
    if (hasProfile == null) {
        CircularProgressIndicator()
    } else {

        startDest = if (hasProfile == true) "main" else "profile/{isIntro}"
        // Start navigation with a fixed destination based on the profile state at startup
        NavHost(navController = navController, startDestination = startDest) {
            composable(route = "main") {
                MainConversationScreen(navController, viewModel)
            }
            composable(route = "new_conversation") {
                NewConversationScreen(navController, viewModel)
            }
            composable(
                route = "profile/{isIntro}",
                arguments = listOf(navArgument("isIntro") { type = NavType.BoolType })
            ) { backStack ->
                val isIntro = if(backStack.arguments?.containsKey("isIntro") == true)backStack.arguments?.getBoolean("isIntro") ?: true else true
                ProfileScreen(navController, profileViewModel, isIntro)
            }
            composable(route = "chat/{convId}", arguments = listOf(navArgument("convId") { type = NavType.IntType })) { stack ->
                val messageViewModel = hiltViewModel<MessageViewModel>(stack)
                ChatScreen(navController, messageViewModel)
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildTopBar(navController: NavController, viewModel: MainViewModel) {
    TopAppBar(
        title = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chatting",
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                val navigateToProfile : () -> Unit = {
                    navController.navigate("profile/false")
                }
                TopBarProfileImage(navigateToProfile, viewModel)
            }
        },
    )
}

@Composable
fun TopBarProfileImage(navigateToProfile: () -> Unit, viewModel: MainViewModel) {
    val profileState by viewModel.profileState.collectAsState()
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .clickable { navigateToProfile() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = profileState.avatar
            ),
            contentDescription = "Top Bar Profile",
            modifier = Modifier.fillMaxSize() // Ensures the image takes up the full size
        )
    }
}










