package com.brokenprotocol.firebaseauthdemo.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.ui.auth.AuthViewModel
import com.brokenprotocol.firebaseauthdemo.ui.components.PrimaryButton
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
        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            isError = errorMessage != null
        )
        
        // First Name field
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        
        // Last Name field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        
        // Phone Number field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            )
        )
        
        // Error message
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Success message
        successMessage?.let { success ->
            Text(
                text = success,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator()
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
                Text("Cancel")
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
                text = "Save",
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
