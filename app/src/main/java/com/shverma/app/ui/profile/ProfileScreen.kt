package com.shverma.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shverma.androidstarter.R
import com.shverma.app.ui.JournButton
import com.shverma.app.ui.MoodSummaryCard
import com.shverma.app.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToChangePassword: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JournAIBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(JournAILightPeach),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.avatarUrl != null) {
                    // If there's an avatar URL, load it
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual image loading
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder with initials
                    Text(
                        text = uiState.userName.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = JournAIBrown,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = uiState.userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = JournAIBrown
            )

            // Email
            Text(
                text = uiState.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Streak/Mood Summary
            if (uiState.streak > 0) {
                MoodSummaryCard(
                    streakText = "${uiState.streak}-day streak!",
                    mood = uiState.mood
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Profile Actions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
//                    ProfileActionItem(
//                        icon = Icons.Outlined.Edit,
//                        title = "Edit Profile",
//                        onClick = { /* Handle edit profile */ }
//                    )
//
//                    HorizontalDivider(
//                        modifier = Modifier.padding(vertical = 8.dp),
//                        thickness = DividerDefaults.Thickness, color = JournAILightPeach
//                    )

                    ProfileActionItem(
                        icon = Icons.Outlined.Password,
                        title = stringResource(R.string.change_password),
                        onClick = onNavigateToChangePassword
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = DividerDefaults.Thickness, color = JournAILightPeach
                    )
//
//                    ProfileActionItem(
//                        icon = Icons.Outlined.Notifications,
//                        title = "Notification Settings",
//                        onClick = { /* Handle notification settings */ }
//                    )
//
//                    HorizontalDivider(
//                        modifier = Modifier.padding(vertical = 8.dp),
//                        thickness = DividerDefaults.Thickness, color = JournAILightPeach
//                    )

                    ProfileActionItem(
                        icon = Icons.Outlined.Security,
                        title = "Privacy & Security",
                        onClick = { /* Handle privacy & security */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Button
            JournButton(
                text = stringResource(R.string.sign_out),
                backgroundColor = JournAILightPeach,
                contentColor = JournAIBrown,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.signOut()
                    onNavigateToLogin()
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = JournAIBrown,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = JournAIBrown
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = JournAIBrown.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
