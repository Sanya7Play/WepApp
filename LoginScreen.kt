@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp) // Общие отступы для всего экрана
    ) {
        // TopAppBar сверху
        TopAppBar(
            title = {
                Text(
                    text = "Войти",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            actions = {
                Image(
                    painter = painterResource(id = R.drawable.images), // замените на ваше изображение
                    contentDescription = "Login Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter) // Закрепляем сверху
        )

        // Основной контент по центру
        Column(
            modifier = Modifier
                .align(Alignment.Center) // Центрируем элементы
                .fillMaxWidth()
        ) {
            // Поле ввода для имени пользователя
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Отступ снизу
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Username Icon")
                },
                singleLine = true
            )

            // Поле ввода для пароля
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp), // Отступ снизу
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon")
                },
                singleLine = true
            )

            // Кнопка для входа
            Button(
                onClick = {
                    if (userViewModel.validateUser(username, password)) {
                        // Устанавливаем текущего пользователя в ViewModel
                        userViewModel.setCurrentUser(userViewModel.currentUser)
                        // Навигация на экран профиля
                        navController.navigate("profile")
                    } else {
                        // Если невалидный логин или пароль, показываем ошибку
                        loginError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Отступы по бокам
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }

            // Сообщение об ошибке
            if (loginError) {
                Spacer(modifier = Modifier.height(16.dp)) // Отступ сверху перед ошибкой
                Text(
                    text = "Неверный логин или пароль",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private var _currentUser: User? = null
    val currentUser: User?
        get() = _currentUser

    // Функция для получения текущего пользователя
    fun setCurrentUser(user: User?) {
        _currentUser = user
    }

    // Функция для валидации пользователя
    fun validateUser(username: String, password: String): Boolean {
        val isValid = userRepository.validateUser(username, password)
        if (isValid) {
            // Устанавливаем текущего пользователя в UserViewModel
            val user = userRepository.getCurrentUser()
            _currentUser = user
            Log.d("UserViewModel", "Текущий пользователь: ${user?.name}")
        }
        return isValid
    }

    // Получить пользователя по ID
    fun getUserById(id: Int?): User? {
        return id?.let {
            userRepository.getUserById(it)
        }
    }
}

class UserRepository {
    private val users = listOf(
        User(1, "user1", "password1", "Александр Копылов", "kopylow2004@gmail.com", "+79523469728",R.drawable.profile_image_user1),
        User(2, "user2", "password2", "Арсений", "popovaa@mail.ru", "+79103454546",R.drawable.profile_image_user2)
    )

    private var currentUser: User? = null

    // Проверить логин и пароль
    fun validateUser(username: String, password: String): Boolean {
        currentUser = users.find { it.username == username && it.password == password }
        return currentUser != null
    }

    // Получить пользователя по ID
    fun getUserById(id: Int): User? {
        return users.find { it.id == id } // Возвращает User? или null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val name: String,
    val email: String,
    val phone: String,
    val profileImageResId: Int // Ресурс изображения для профиля
)


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val userViewModel: UserViewModel = viewModel() // создаем ViewModel
    LoginScreen(navController = rememberNavController(), userViewModel = userViewModel)
}
