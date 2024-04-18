package uk.ac.tees.mad.w9642974.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    navigateUp: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userDetailsState = viewModel.currentUserData.collectAsState(initial = null)
    val uiState = viewModel.uiState.collectAsState()
    var isProfileClicked by remember {
        mutableStateOf(false)
    }
    val members = viewModel.allMembers

    LaunchedEffect(userDetailsState.value?.data) {
        if (userDetailsState.value?.data != null) {
            userDetailsState.value?.data.let {
                viewModel.updateUiState(
                    UserResponse.CurrentUser(
                        name = it?.item?.name!!,
                        email = it.item.email,
                        profileImage = it.item.profileImage,
                    )
                )
            }
        }
    }
    if (isProfileClicked) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isProfileClicked = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Members", fontSize = 22.sp, fontWeight = FontWeight.Medium)
            }
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(members) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(8.dp)) {
                            Text(
                                text = it.username,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = it.email,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    } else {

        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Profile", fontSize = 22.sp, fontWeight = FontWeight.Medium)
            }


            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, Color.Black), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.value.profileImage.isEmpty()) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = "Profile photo",
                        tint = Color.Gray
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                            .data(uiState.value.profileImage).build(),
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = uiState.value.name, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = uiState.value.email, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(40.dp))
            Column(
                Modifier
                    .background(Color.LightGray.copy(0.2f))
                    .weight(1f)
                    .padding(24.dp)
                    .fillMaxWidth()

            ) {
                Text(text = "Explore", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(30.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        onClick = {
                            isProfileClicked = true
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonSearch,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Members")
                        }
                    }
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White),
                        onClick = onLogout
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Log out")
                        }
                    }
                }
            }
        }
    }
}


object ProfileDestination : NavigationDestination {
    override val route = "profile"
}