package com.example.bookreaderapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookreaderapp.viewmodel.AuthState
import com.example.bookreaderapp.viewmodel.AuthViewModel

//@Composable
//fun LoginScreen(
//    navController: NavController,
//    authViewModel: AuthViewModel = viewModel()
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var errorMsg by remember { mutableStateOf<String?>(null) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Login", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        errorMsg?.let {
//            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
//        }
//
//        Button(
//            onClick = {
//                authViewModel.login(email, password) { success, error ->
//                    if (success) {
//                        navController.navigate("home") {
//                            popUpTo("login") { inclusive = true }
//                        }
//                    } else {
//                        errorMsg = error
//                    }
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Login")
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        TextButton(onClick = {
//            navController.navigate("signup")
//        }) {
//            Text("Don't have an account? Sign up")
//        }
//    }
//}



@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState = authViewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BOOKHIVE",
            fontSize = 32.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Tabs: Login / Signup (UI only)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Login",
                fontSize = 20.sp,
                color = Color(0xFFFF5A5F), // Orange (active)
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = "Signup",
                fontSize = 20.sp,
                color = Color.Gray,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("signup") }) {
            Text(
                text = "Don't have an account? Sign up",
                color = Color(0xFFFF5A5F)
            )
        }
    }
}
