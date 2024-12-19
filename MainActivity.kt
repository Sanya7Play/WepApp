package com.example.jobapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobapp.ui.theme.JobAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JobAppNavigation()
        }
    }
}

@Composable
fun JobAppNavigation() {
    val navController = rememberNavController()
    val favoriteJobs = remember { mutableStateListOf<Job>() } // Список избранных вакансий
    val jobList = remember {
        mutableStateListOf(
            Job(1, "Грузчик(12ч)", "1200₽", "магазин Магнит", "Воронеж", "+79033555566"),
            Job(2, "Кассир(8ч)", "2000₽", "магазин Пятерочка", "Воронеж", "+791032321354"),
            Job(3, "Фасовщик(5ч)", "1500₽", "магазин Перекресток", "Воронеж", "+79081409538"),
            Job(4, "Кассир(12ч)", "2200₽", "магазин Магнит", "Воронеж", "+7955555555"),
        )
    }
    val phoneNumberToShow = remember { mutableStateOf<String?>(null) } // Состояние для номера телефона
    val bookingStatus = remember { mutableStateOf<String?>(null) }
    val userViewModel: UserViewModel = viewModel() // создаем один ViewModel для всех экранов

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController, userViewModel)
        }
        composable("profile") {
            ProfileScreen(navController, userViewModel)
        }
        composable("search") {
            SearchScreen(navController, jobList, favoriteJobs, phoneNumberToShow, bookingStatus)
        }
        composable("favorites") {
            FavoritesScreen(navController, favoriteJobs, phoneNumberToShow)
        }
        composable("income") {
            IncomeScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Профиль") },
            selected = navController.currentDestination?.route == "profile",
            onClick = { navController.navigate("profile") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            label = { Text("Поиск") },
            selected = navController.currentDestination?.route == "search",
            onClick = { navController.navigate("search") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
            label = { Text("Мои смены") },
            selected = navController.currentDestination?.route == "favorites",
            onClick = { navController.navigate("favorites") }
        )
        // Новая кнопка "Мой доход"
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "My Income") },
            label = { Text("Мой доход") },
            selected = navController.currentDestination?.route == "income",
            onClick = { navController.navigate("income") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val currentUser = userViewModel.currentUser
    val context = LocalContext.current
    val showLogoutDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Профиль",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = { /* Действие для кнопки меню */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Меню"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE)
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentUser != null) {
                // Профильное изображение в круге
                Image(
                    painter = painterResource(id = currentUser.profileImageResId),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp) // Размер изображения
                        .clip(CircleShape) // Обрезка изображения в круг
                        .border(2.dp, Color.Gray, CircleShape) // Опциональная рамка вокруг изображения
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Изменяемые поля профиля
                var name by remember { mutableStateOf(currentUser.name) }
                var email by remember { mutableStateOf(currentUser.email) }
                var phone by remember { mutableStateOf(currentUser.phone) }

                // Отображение и редактирование информации пользователя
                ProfileField(label = "Имя", value = name) { name = it }
                Spacer(modifier = Modifier.height(16.dp))
                ProfileField(label = "Email", value = email) { email = it }
                Spacer(modifier = Modifier.height(16.dp))
                ProfileField(label = "Телефон", value = phone) { phone = it }
                Spacer(modifier = Modifier.height(32.dp))

                // Кнопка для сохранения изменений
                val coroutineScope = rememberCoroutineScope()

                Button(
                    onClick = {
                        // Логика для сохранения изменений
                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "Изменения сохранены",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Обновление данных пользователя в ViewModel или базе данных
                            userViewModel.setCurrentUser(
                                currentUser.copy(name = name, email = email, phone = phone)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить изменения")
                }
            } else {
                // Если пользователь не найден
                Text(
                    text = "Пользователь не найден",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }
    }
    // Диалог для выхода
    if (showLogoutDialog.value) {
        LogoutDialog(onDismiss = { showLogoutDialog.value = false }) {
            // Логика для выхода, например, сброс текущего пользователя в ViewModel
            userViewModel.setCurrentUser(null)
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}
@Composable
fun LogoutDialog(onDismiss: () -> Unit, onLogout: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выход из системы") },
        text = { Text("Вы уверены, что хотите выйти?") },
        confirmButton = {
            TextButton(onClick = onLogout) {
                Text("Выйти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// Функция для отображения изменяемых полей профиля
@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

data class Job(
    val id: Int,
    val title: String,
    val cash: String,
    val company: String,
    val location: String,
    val phone: String,
    var isFavorite: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    jobList: List<Job>,
    favoriteJobs: MutableList<Job>,
    phoneNumberToShow: MutableState<String?>,
    bookingStatus: MutableState<String?> // Для отображения статуса бронирования
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Поиск вакансий",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White, // Цвет текста
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE) // Цвет заливки
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            itemsIndexed(jobList) { _, job ->
                JobItem(
                    job = job,
                    onFavoriteClick = {
                        if (job.isFavorite) {
                            favoriteJobs.remove(job)
                        } else {
                            favoriteJobs.add(job)
                        }
                        job.isFavorite = !job.isFavorite
                    },
                    onCallClick = {
                        phoneNumberToShow.value = job.phone
                    },
                    onBookClick = {
                        bookingStatus.value = "Бронирование для ${job.title} успешно!" // Устанавливаем сообщение
                    }
                )
            }
        }

        // Диалог с номером телефона
        phoneNumberToShow.value?.let { phoneNumber ->
            ShowPhoneDialog(phoneNumber) {
                phoneNumberToShow.value = null
            }
        }

        // Диалог с подтверждением бронирования
        bookingStatus.value?.let { status ->
            ShowBookingDialog(status) {
                bookingStatus.value = null
            }
        }
    }
}

@Composable
fun JobItem(
    job: Job,
    onFavoriteClick: () -> Unit,
    onCallClick: () -> Unit,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = job.title, style = MaterialTheme.typography.titleLarge)
            Text(text = job.cash, style = MaterialTheme.typography.titleMedium)
            Text(text = job.company, style = MaterialTheme.typography.bodyMedium)
            Text(text = job.location, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Иконка добавления в избранное
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (job.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Favorite"
                    )
                }

                // Кнопка "Call"
                Button(
                    onClick = onCallClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Позвонить")
                }

                // Кнопка "Забронировать"
                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Забронировать")
                }
            }
        }
    }
}

@Composable
fun ShowPhoneDialog(phoneNumber: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Номер телефона") },
        text = { Text("Позвонить по номеру $phoneNumber?") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Позвонить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun ShowBookingDialog(status: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Бронирование") },
        text = { Text(status) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavHostController,
    favoriteJobs: MutableList<Job>,
    phoneNumberToShow: MutableState<String?>
) {
    // Состояние для отображения статуса бронирования
    val bookingStatus = remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Избранные вакансии",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White, // Цвет текста
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE) // Цвет заливки
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            itemsIndexed(favoriteJobs) { index, job ->
                JobItem(
                    job = job,
                    onFavoriteClick = {
                        favoriteJobs.removeAt(index)
                        job.isFavorite = false
                    },
                    onCallClick = {
                        phoneNumberToShow.value = job.phone
                    },
                    onBookClick = {
                        // Установка статуса бронирования
                        bookingStatus.value = "Бронирование успешно!"
                    }
                )
            }
        }

        // Отображение диалога для номера телефона
        phoneNumberToShow.value?.let { phoneNumber ->
            ShowPhoneDialog(phoneNumber) {
                phoneNumberToShow.value = null
            }
        }

        // Отображение диалога или сообщения о бронировании
        bookingStatus.value?.let { status ->
            BookingStatusDialog(status) {
                bookingStatus.value = null
            }
        }
    }
}

@Composable
fun BookingStatusDialog(status: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Статус бронирования")
        },
        text = {
            Text(text = status)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "ОК")
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(navController: NavHostController) {
    // Пример данных
    val balance = remember { mutableStateOf(12345.67) } // Текущий баланс
    val transactions = remember {
        listOf(
            Transaction("Зачисление зарплаты", 45000.0, "01.12.2024"),
            Transaction("Возврат налога", 1500.0, "15.11.2024"),
            Transaction("Бонус от компании", 3000.0, "20.10.2024")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Мой доход",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White, // Цвет текста
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE) // Цвет заливки
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp), // Отступы для красоты
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Отображение баланса
            Text(
                text = "Ваш баланс:",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₽${"%,.2f".format(balance.value)}", // Форматирование баланса
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Список зачислений
            Text(
                text = "История зачислений",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}

// Данные о транзакции
data class Transaction(
    val description: String,
    val amount: Double,
    val date: String
)

// Компонент для отображения одной транзакции
@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = transaction.description, style = MaterialTheme.typography.bodyMedium)
                Text(text = transaction.date, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "₽${"%,.2f".format(transaction.amount)}", // Форматирование суммы
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JobAppTheme {
        JobAppNavigation()
    }
}
