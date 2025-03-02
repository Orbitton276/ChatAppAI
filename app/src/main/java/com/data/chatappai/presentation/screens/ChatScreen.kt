package com.data.chatappai.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.data.chatappai.R
import com.data.chatappai.data.db.Conversation
import com.data.chatappai.data.db.User
import com.data.chatappai.domain.model.Message
import com.data.chatappai.domain.model.Profile
import com.data.chatappai.presentation.UiState
import com.data.chatappai.presentation.viewmodel.MessageViewModel
import com.data.chatappai.ui.theme.extendedColors
import com.data.chatappai.utils.CommonUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController, messageViewModel: MessageViewModel) {
    val convState by messageViewModel.conversationState.collectAsState()
    val messagesState by messageViewModel.allMessagesState.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        topBar = {
            TopAppBar(
                title = {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            text = (convState as? UiState.DataLoaded<Conversation>)?.data?.title
                                ?: stringResource(R.string.not_fetched)
                        )
                        ShowParticipantsSummary(messageViewModel)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { TypeMessageBottomBar(messageViewModel) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (messagesState) {
                is UiState.DataLoaded<*> -> {
                    MessageList(
                        (messagesState as UiState.DataLoaded<List<Message>>).data,
                        messageViewModel
                    )
                }

                is UiState.Error -> Text(text = stringResource(R.string.error_loading_messages))
                UiState.Loading -> CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun ShowParticipantsSummary(messageViewModel: MessageViewModel) {
    val users by messageViewModel.allUsers.collectAsState()
    Text(
        text = users.values.joinToString { it.firstName },
        modifier = Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.W100,
        fontSize = 12.sp
    )
}

@Composable
fun MessageList(
    state: List<Message>,
    messageViewModel: MessageViewModel
) {
    val listState = rememberLazyListState()
    val usersMap by messageViewModel.allUsers.collectAsState()
    val profileState by messageViewModel.profileState.collectAsState()

    LaunchedEffect(state, profileState) {
        if (state.isNotEmpty()) {
            listState.animateScrollToItem(state.size - 1)
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        items(state) { msg ->
            MessageListItem(msg, usersMap[(msg.senderId.toLongOrNull())], profileState)
        }
    }
}

@Composable
fun MessageListItem(message: Message, user: User?, profile: Profile) {
    Spacer(Modifier.height(10.dp))
    val isMe = message.senderId == "me"
    Row(
        horizontalArrangement = if (isMe)
            Arrangement.End // Align to the end if it's the current user's message
        else
            Arrangement.Start, // Align to the start for others' messages
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (isMe) {
            MessageUserImage(user, profile)
            MessageCard(message, user, profile)
        } else {
            MessageCard(message, user, profile)
            MessageUserImage(user, profile)
        }
    }

    Spacer(Modifier.height(10.dp))
}

@Composable
fun TypeMessageBottomBar(messageViewModel: MessageViewModel) {
    var text by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {

            TextField(
                value = text, onValueChange = { newTxt ->
                    text = newTxt
                }, modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.extendedColors.selfContainer, // Light blue
                    unfocusedContainerColor = MaterialTheme.extendedColors.selfContainer, // Light blue
                )
            )

        }
        IconButton(
            enabled = text.isNotBlank(),
            onClick = {
                messageViewModel.sendMessage(
                    text = text,
                )
                text = ""

                keyboardController?.hide() // Close the keyboard
                focusManager.clearFocus() // Remove focus from the TextField
            }, colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.extendedColors.selfContainer, // Light blue
            ), modifier = Modifier.padding(start = 5.dp, end = 10.dp)
        ) {

            Icon(
                imageVector = if (text.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.AutoMirrored.Outlined.Send,
                contentDescription = null
            )
        }
    }

}

@Composable
fun MessageCard(message: Message, user: User?, profile: Profile) {

    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f),

        colors = CardDefaults
            .cardColors(
                if (message.senderId == "me") MaterialTheme.extendedColors.selfContainer
                else MaterialTheme.extendedColors.otherContainer
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            Text(
                text = user?.firstName ?: profile.name,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(text = message.content, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val date = CommonUtils.Companion.DateUtils.formatTimestamp(message.timestamp)
                Text(text = date, fontSize = 10.sp)
            }
        }
    }
}


@Composable
fun MessageUserImage(user: User?, profileState: Profile) {
    if (user == null) {
        Image(
            painter = rememberAsyncImagePainter(
                model = profileState.avatar
            ),
            contentDescription = "Contact Photo",
            modifier = Modifier
                .size(24.dp)
                .clip(shape = CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
    } else {
        AsyncImage(
            model = user?.avatarUrl ?: profileState.avatar,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(24.dp)
                .clip(shape = CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop,
        )
    }
}