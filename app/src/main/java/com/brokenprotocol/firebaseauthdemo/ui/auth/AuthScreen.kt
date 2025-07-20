package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.brokenprotocol.firebaseauthdemo.network.DjangoApiService
import com.brokenprotocol.firebaseauthdemo.network.FirebaseApiService

@Composable
fun AuthScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { 
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true
                    statusMessage = "Signing in..."
                    
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val firebaseApiService = FirebaseApiService()
                            val authResult = firebaseApiService.signInWithEmailAndPassword(email, password)
                            
                            if (authResult.success && authResult.idToken != null) {
                                statusMessage = "Firebase auth successful! Sending to Django..."
                                
                                val djangoApiService = DjangoApiService()
                                val response = djangoApiService.verifyFirebaseToken(authResult.idToken)
                                
                                if (response.success) {
                                    statusMessage = "Django Response (Code: ${response.responseCode}):\n${response.responseBody}"
                                } else {
                                    statusMessage = "Django Error: ${response.error ?: response.responseBody}"
                                }
                            } else {
                                statusMessage = "Firebase auth failed: ${authResult.error}"
                            }
                        } catch (e: Exception) {
                            statusMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    statusMessage = "Please enter email and password"
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Signing In..." else "Sign In")
        }
        
        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusMessage,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 