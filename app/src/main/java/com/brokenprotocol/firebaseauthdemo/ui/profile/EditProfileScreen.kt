package com.brokenprotocol.firebaseauthdemo.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.R
import com.brokenprotocol.firebaseauthdemo.ui.auth.AuthViewModel
import com.brokenprotocol.firebaseauthdemo.ui.components.*
import com.brokenprotocol.firebaseauthdemo.network.UserProfileData
import com.brokenprotocol.firebaseauthdemo.ui.auth.AuthState

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userProfileData by viewModel.userProfileData.collectAsState()
    val authState by viewModel.authState.collectAsState()
    
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scrollState = rememberScrollState()
    
    // Fetch full user profile data when screen loads
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            viewModel.getUserProfile(user.firebaseUid)
        }
    }
    
    // Update form fields when user profile data is loaded
    LaunchedEffect(userProfileData) {
        userProfileData?.let { profile ->
            username = profile.username ?: ""
            firstName = profile.firstName ?: ""
            lastName = profile.lastName ?: ""
            phoneNumber = profile.phoneNumber ?: ""
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AuthHeader(
            title = stringResource(R.string.auth_edit_profile_title),
            onBackClick = { navController.popBackStack() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Username field
        AuthTextField(
            value = username,
            onValueChange = { username = it },
            label = stringResource(R.string.label_username),
            keyboardOptions = AuthKeyboardOptions.Text,
            isError = errorMessage != null
        )
        
        // First Name field
        AuthTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = stringResource(R.string.label_first_name),
            keyboardOptions = AuthKeyboardOptions.Text
        )
        
        // Last Name field
        AuthTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = stringResource(R.string.label_last_name),
            keyboardOptions = AuthKeyboardOptions.Text
        )
        
        // Phone Number field
        AuthTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = stringResource(R.string.label_phone_number),
            keyboardOptions = AuthKeyboardOptions.Text
        )
        
        // Error message
        errorMessage?.let { error ->
            ErrorMessage(
                message = error,
                onDismiss = { errorMessage = null }
            )
        }
        
        // Success message
        successMessage?.let { success ->
            SuccessMessage(message = success)
        }
        
        // Loading indicator
        if (isLoading) {
            LoadingSpinner(message = stringResource(R.string.message_updating_profile))
        }
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cancel button
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.button_cancel))
            }
            
            // Save button
            PrimaryButton(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    successMessage = null

                    currentUser?.let { user ->
                        viewModel.updateProfile(
                            firebaseUid = user.firebaseUid,
                            username = username,
                            firstName = firstName,
                            lastName = lastName,
                            phoneNumber = phoneNumber
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading && username.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank(),
                text = stringResource(R.string.button_save),
                loading = false
            )
        }
    }
    
    // Handle auth state changes (for profile updates)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                isLoading = false
                if ((authState as AuthState.Success).message?.contains("updated") == true) {
                    successMessage = (authState as AuthState.Success).message
                    // Refresh the profile data
                    currentUser?.let { user ->
                        viewModel.getUserProfile(user.firebaseUid)
                    }
                    // Navigate back to ProfileScreen after successful update
                    navController.popBackStack()
                }
            }
            is AuthState.Error -> {
                isLoading = false
                errorMessage = (authState as AuthState.Error).message
            }
            is AuthState.Loading -> {
                isLoading = true
                errorMessage = null
                successMessage = null
            }
            else -> {
                isLoading = false
            }
        }
    }
}
