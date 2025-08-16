package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.R
import com.brokenprotocol.firebaseauthdemo.navigation.NavRoutes
import com.brokenprotocol.firebaseauthdemo.ui.components.ErrorMessage
import com.brokenprotocol.firebaseauthdemo.ui.components.LoadingSpinner
import com.brokenprotocol.firebaseauthdemo.ui.components.PrimaryButton
import com.brokenprotocol.firebaseauthdemo.ui.components.SuccessMessage

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel
) {
    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentUser != null) {
            // User is authenticated - show Sign Out
            Text(
                text = stringResource(R.string.profile_welcome, currentUser!!.email),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = stringResource(R.string.button_sign_out),
                onClick = { viewModel.signOut() },
                loading = authState is AuthState.Loading
            )
        } else {
            // User is not authenticated - show Sign In and Sign Up
            Text(
                text = stringResource(R.string.auth_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = stringResource(R.string.button_sign_in),
                onClick = { navController.navigate(NavRoutes.SignIn.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = stringResource(R.string.button_sign_up),
                onClick = { navController.navigate(NavRoutes.SignUp.route) }
            )
        }
        
        // Handle auth state for feedback
        when (authState) {
            is AuthState.Loading -> {
                Spacer(modifier = Modifier.height(16.dp))
                LoadingSpinner(
                    message = if (currentUser != null) stringResource(R.string.message_signing_out) else stringResource(R.string.message_signing_in)
                )
            }
            is AuthState.Success -> {
                Spacer(modifier = Modifier.height(16.dp))
                SuccessMessage(
                    message = stringResource(R.string.message_authentication_successful)
                )
            }
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorMessage(
                    message = (authState as AuthState.Error).message,
                    onDismiss = { viewModel.clearError() }
                )
            }
            else -> {
                // Initial state - no message needed
            }
        }
    }
} 