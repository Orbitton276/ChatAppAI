package com.data.chatappai.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.data.chatappai.R
import com.data.chatappai.data.db.User
import com.data.chatappai.presentation.UiState
import com.data.chatappai.presentation.viewmodel.MainViewModel

@Composable
fun NewConversationScreen(navController: NavHostController, viewModel: MainViewModel) {
    val state by viewModel.uiUsersState.collectAsState()

    when (state) {
        is UiState.DataLoaded<*> -> {
            AddConversationForm(
                state as UiState.DataLoaded<List<User>>, viewModel, navController
            )
        }

        is UiState.Error -> {
            Text("error ${(state as UiState.Error).message}")
        }

        UiState.Loading -> CircularProgressIndicator()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddConversationForm(
    state: UiState.DataLoaded<List<User>>,
    viewModel: MainViewModel,
    navController: NavHostController
) {

    var titleState by rememberSaveable  { mutableStateOf("") }
    var titleErrorState by remember { mutableStateOf(false) }
    var searchUserState by rememberSaveable { mutableStateOf("") }

    var selectedUsers by rememberSaveable { mutableStateOf(setOf<User>()) }



    val filteredList by remember(searchUserState) {
        derivedStateOf {
            if (searchUserState.isBlank()) state.data
            else state.data.filter { user ->
                user.firstName.contains(searchUserState, ignoreCase = true)
            }
        }
    }

    Column(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 35.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = titleState,
            onValueChange = { newText ->
                titleState = newText
                titleErrorState = false
            },
            isError = titleErrorState,
            placeholder = {
                Text(
                    text = "Chat Title",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = searchUserState,
            onValueChange = { newText ->
                searchUserState = newText
            },
            trailingIcon = {
                IconButton(onClick = {
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = "Search Icon"
                    )
                }
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_user),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
        if (selectedUsers.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(16.dp),
            ) {

                selectedUsers.forEach { text ->
                    FilterChip(
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Transparent, // Removes background color
                            disabledContainerColor = Color.Transparent, // Ensures disabled chips stay transparent
                            selectedContainerColor = Color.Transparent // Removes background when selected
                        ),
                        enabled = true,
                        selected = true,
                        modifier = Modifier.padding(horizontal = 2.dp),
                        onClick = { /* Handle click */ },
                        label = { Text(text.firstName) },
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = 2.dp
                        )
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Button(onClick = {
                    if (titleState.isBlank()) {
                        titleErrorState = true
                    } else {

                        viewModel.addConversation(selectedUsers, titleState)
                        selectedUsers = setOf()
                        searchUserState = ""
                        titleState = ""
                        navController.navigate("main") {
                            popUpTo("add_conversation") {
                                inclusive = true
                            }
                        }
                    }

                }) {
                    Text(text = stringResource(R.string.add_conversation))
                }
            }
        }

        LazyColumn {
            // Users list

            items(filteredList) { user ->
                val isSelected = user in selectedUsers
                UserItem(user = user, isSelected = isSelected, onClick = {
                    selectedUsers = if (isSelected) {
                        selectedUsers - user // Deselect
                    } else {
                        selectedUsers + user // Select
                    }
                })
            }
        }
    }
}

@Composable
fun UserItem(user: User, isSelected: Boolean, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
        )

    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = user.avatarUrl, // Directly pass the Uri
                ),
                contentDescription = "Contact Photo",
                modifier = Modifier
                    .size(48.dp) // Adjust size as needed
                    .clip(CircleShape) // Makes it circular
                    .background(Color.Gray) // Optional background
                    .border(2.dp, Color.White, CircleShape), // Optional border
                contentScale = ContentScale.Crop // Crops image to fill the shape
            )
            Text(
                text = user.firstName,
                modifier = Modifier
                    .padding(16.dp)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
            )
        }
    }
}
