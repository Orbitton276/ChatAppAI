package com.data.chatappai.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.data.chatappai.BuildTopBar

import com.data.chatappai.data.db.Conversation
import com.data.chatappai.presentation.UiState
import com.data.chatappai.presentation.viewmodel.MainViewModel

@Composable

fun MainConversationScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.uiConversationState.collectAsState()

    Scaffold(
        topBar = {
            BuildTopBar(navController, viewModel)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("new_conversation")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }, content = { paddingValues ->
            Box(modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()) {
                when (state) {
                    is UiState.Error -> {
                        Text("error ${(state as UiState.Error).message}")
                    }

                    UiState.Loading -> CircularProgressIndicator()

                    is UiState.DataLoaded<*> -> {
                        ConversationListScreen(
                            state as UiState.DataLoaded<List<Conversation>>,
                            navController
                        )
                    }

                }
            }

        }, modifier = Modifier.fillMaxSize()
    )

}


@Composable
fun ConversationListScreen(
    state: UiState.DataLoaded<List<Conversation>>,
    navController: NavController
) {
    Spacer(Modifier.height(20.dp))
    if (state.data.isEmpty()) {
        Text(text = "No Conversations Yet", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp))
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            items(state.data) { conv ->
                val click: () -> Unit = {
                    navController.navigate("chat/${conv.id}")
                }
                ConversationItem(conv, click)
            }
        }
    }

}

@Composable
fun ConversationItem(conversation: Conversation, click: () -> Unit) {
    Column(Modifier
        .padding(10.dp)
        .fillMaxWidth()
        .clickable {
            click()
        }) {
        Text(text = conversation.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Text(
            text = conversation.preview.takeIf { it.isNotBlank() } ?: "No Messages Yet",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(thickness = 1.dp)

    }
}