package com.data.chatappai.presentation.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.data.chatappai.R
import com.data.chatappai.domain.model.Profile
import com.data.chatappai.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel, isIntro: Boolean) {
    val profileState by viewModel.profileState.collectAsState(initial = Profile())
    var nameInput by remember { mutableStateOf(profileState.name) }
    var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(profileState.avatar)) }
    var isImagePicked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Handle image picker result

    val coroutineScope = rememberCoroutineScope()
    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Copy the selected image to a permanent storage location
                imageUri = it
                isImagePicked = true
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    if (!isIntro) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            Modifier.clickable {
                                navController.popBackStack()
                            })
                    }
                }

            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(paddingValues)
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Box() {
                    LaunchedEffect(profileState.avatar) {
                        imageUri = Uri.parse(profileState.avatar)
                    }
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = imageUri
                        ),
                        contentDescription = "Contact Photo",
                        modifier = Modifier
                            .size(256.dp) // Adjust size as needed
                            .clip(CircleShape) // Makes it circular
                            .background(Color.Gray) // Optional background
                            .border(2.dp, Color.White, CircleShape), // Optional border
                        contentScale = ContentScale.Crop // Crops image to fill the shape
                    )
                    IconButton(
                        onClick = {
                            pickImage.launch("image/*")
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier
                            .padding(20.dp)
                            .align(Alignment.BottomEnd),
                        content = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                            )
                        })
                }

                LaunchedEffect(profileState.name) {
                    nameInput = profileState.name
                }
                // Input for name
                Spacer(Modifier.height(20.dp))
                TextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Enter your name") },
                    shape = RoundedCornerShape(10.dp),
                )

                Button(
                    onClick = { pickImage.launch("image/*") },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Pick Image")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                Button(
                    onClick = {
                        // Save profile with the picked image URI if available
                        viewModel.saveProfileObject(nameInput, imageUri)
                        coroutineScope.launch {
                            Toast.makeText(context,
                                context.getString(R.string.navigating_in_2_seconds), Toast.LENGTH_SHORT).show()
                            delay(2000)

                            if (isIntro) {
                                navController.navigate("main") {
                                    popUpTo("profile/{isIntro}") { inclusive = true } // Remove profile from stack
                                }
                            } else {
                                navController.popBackStack()
                            }

                        }
                    },
                    enabled = nameInput.isNotEmpty()
                ) {
                    Text("Save Profile")
                }
            }
        }
    )
}



