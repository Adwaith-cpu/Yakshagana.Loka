package com.yakshaganaloka.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yakshaganaloka.app.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val TAG = "AuthViewModel"

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            Log.d(TAG, "AuthStateListener: Authenticated UID: ${user.uid}")
            _authState.value = AuthState.Authenticated
        } else {
            Log.d(TAG, "AuthStateListener: Not Authenticated")
            _authState.value = AuthState.Idle
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun login(email: String, password: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }
        if (password.isEmpty()) {
            _authState.value = AuthState.Error("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.login(email, password)
                if (user == null) {
                    _authState.value = AuthState.Error("Login failed: Incorrect credentials")
                }
                // AuthStateListener will handle the _authState.value = Authenticated
            } catch (e: Exception) {
                Log.e(TAG, "Login Error", e)
                _authState.value = AuthState.Error(e.localizedMessage ?: "Authentication failed")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.signInWithGoogle(idToken)
                if (user == null) {
                    _authState.value = AuthState.Error("Google Sign-In failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In Error", e)
                _authState.value = AuthState.Error(e.localizedMessage ?: "Google Authentication failed")
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.signInAnonymously()
                if (user == null) {
                    _authState.value = AuthState.Error("Guest login failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Guest Login Error", e)
                _authState.value = AuthState.Error(e.localizedMessage ?: "Guest login failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.signUp(email, password)
                if (user == null) {
                    _authState.value = AuthState.Error("Signup failed: Could not create account")
                }
            } catch (e: Exception) {
                Log.e(TAG, "SignUp Error", e)
                _authState.value = AuthState.Error(e.localizedMessage ?: "Account creation failed")
            }
        }
    }

    fun logout() {
        Log.d(TAG, "Logging out user: ${auth.currentUser?.uid}")
        repository.logout()
    }
}
