package uk.ac.tees.mad.w9642974.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9642974.R
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination
import uk.ac.tees.mad.w9642974.presentation.auth.viewmodels.AuthViewModel

object SignupDestination : NavigationDestination {
    override val route = "signup"
}

@Composable
fun SignUpScreen(
    onLoginClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val signUpstate = viewModel.signUpState.collectAsState(initial = null)
    val signUpUiState = viewModel.signUpUiState.collectAsState().value
    val focusManager = LocalFocusManager.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onNavigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
                Text(text = "Back", fontSize = 18.sp, color = Color.Gray)
            }
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
        Column {
            Text(text = "Sign Up", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Create an GroupFlow account & \nManage your projects.",
                fontSize = 16.sp,
                lineHeight = 20.sp,
                color = Color.Gray,
                letterSpacing = 0.8.sp
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = signUpUiState.name,
            onValueChange = {
                viewModel.updateSignUpState(signUpUiState.copy(name = it))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Full Name")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Person2, contentDescription = "")
            },
            maxLines = 1,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            value = signUpUiState.email,
            onValueChange = {
                viewModel.updateSignUpState(signUpUiState.copy(email = it))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Email")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Email, contentDescription = "")
            },
            maxLines = 1,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = signUpUiState.password,
            onValueChange = {
                viewModel.updateSignUpState(signUpUiState.copy(password = it))
            },

            modifier = Modifier.fillMaxWidth(),

            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Lock, contentDescription = "")
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        description,
                    )
                }
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
        HorizontalDivider()

        Spacer(modifier = Modifier.height(80.dp))

        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .height(65.dp)
                    .clickable {
                        viewModel.registerUser(
                            username = signUpUiState.name,
                            email = signUpUiState.email,
                            password = signUpUiState.password,
                        )
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (signUpstate.value?.isLoading == true) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.background)
                } else {
                    Text(text = "SIGN UP", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        BorderStroke(0.7.dp, Color.Gray.copy(0.5f)), RoundedCornerShape(16.dp)
                    )
                    .height(65.dp)
                    .clickable {
                        onLoginClick()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? LOGIN", fontWeight = FontWeight.SemiBold
                )
            }

        }
    }
    LaunchedEffect(key1 = signUpstate.value?.isSuccess) {
        scope.launch {
            if (signUpstate.value?.isSuccess?.isNotEmpty() == true) {
                onSignUpSuccess()
            }
        }
    }

    LaunchedEffect(key1 = signUpstate.value?.isError) {
        scope.launch {
            if (signUpstate.value?.isError?.isNotEmpty() == true) {
                val error = signUpstate.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}