package com.shverma.journai.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.shverma.journai.R
import com.shverma.journai.auth.GoogleSignInManager
import com.shverma.journai.ui.JournButton
import com.shverma.journai.ui.theme.AppTypography
import com.shverma.journai.ui.theme.JournAIBrown
import com.shverma.journai.ui.theme.JournAILightPeach
import com.shverma.journai.ui.theme.dimensions

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val googleManager = remember {
        GoogleSignInManager(context)
    }


    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val task =
                GoogleSignIn.getSignedInAccountFromIntent(
                    result.data
                )

            try {
                val account =
                    task.getResult(ApiException::class.java)
                account.idToken?.let {
                    viewModel.loginWithGoogle(it) {
                        onLoginSuccess()
                    }
                }

            } catch (e: Exception) {
                viewModel.showError(message = e.printStackTrace().toString())
            }
        }


    val dims = dimensions()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dims.spacingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ---------- Header ----------

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .size(
                            dims.buttonHeight +
                                    dims.spacingXLarge
                        )
                        .clip(CircleShape)
                        .background(JournAILightPeach),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(
                            R.drawable.ic_journal_book
                        ),
                        contentDescription = null,
                        tint = JournAIBrown,
                        modifier = Modifier.size(
                            dims.iconSizeLarge +
                                    dims.iconSizeMedium
                        )
                    )
                }

                Spacer(Modifier.height(dims.spacingXXLarge))

                Text(
                    text = stringResource(
                        R.string.welcome_to_journai
                    ),
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = JournAIBrown
                )

                Spacer(Modifier.height(dims.spacingSmall))

                Text(
                    text = stringResource(
                        R.string.app_subtitle
                    ),
                    style = AppTypography.bodyLarge,
                    color = JournAIBrown.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }


            // ---------- Button ----------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dims.spacingXXLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                JournButton(
                    text = "Continue with Google",
                    iconResId = R.drawable.ic_google,
                    backgroundColor = Color.White,
                    contentColor = JournAIBrown,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.isLoading
                ) {

                    launcher.launch(
                        googleManager.client.signInIntent
                    )
                }


                uiState.error?.let {

                    Spacer(Modifier.height(dims.spacingSmall))

                    Text(
                        text = it,
                        color = MaterialTheme
                            .colorScheme
                            .error,
                        style = AppTypography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }


            // ---------- Footer ----------

            Text(
                text = stringResource(
                    R.string.motivational_quote
                ),
                style = AppTypography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = JournAIBrown.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    bottom = dims.spacingRegular
                )
            )
        }
    }
}

