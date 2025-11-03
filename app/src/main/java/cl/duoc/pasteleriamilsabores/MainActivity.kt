package cl.duoc.pasteleriamilsabores

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.pasteleriamilsabores.data.AuthUiState
import cl.duoc.pasteleriamilsabores.data.CartItem
import cl.duoc.pasteleriamilsabores.data.Product
import cl.duoc.pasteleriamilsabores.data.ProductRepository
import cl.duoc.pasteleriamilsabores.ui.theme.Blanco
import cl.duoc.pasteleriamilsabores.ui.theme.Chocolate
import cl.duoc.pasteleriamilsabores.ui.theme.CremaPastel
import cl.duoc.pasteleriamilsabores.ui.theme.PasteleriaMilSaboresTheme
import cl.duoc.pasteleriamilsabores.ui.theme.RosaSuave
import cl.duoc.pasteleriamilsabores.viewmodel.AuthViewModel
import cl.duoc.pasteleriamilsabores.viewmodel.CartViewModel
import cl.duoc.pasteleriamilsabores.viewmodel.CartUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasteleriaMilSaboresTheme {

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val cartViewModel: CartViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val cartUiState by cartViewModel.uiState.collectAsState()
                val authUiState by authViewModel.uiState.collectAsState()

                LaunchedEffect(authUiState) {
                    cartViewModel.updateAuthState(authUiState)
                }

                Scaffold(
                    topBar = {
                        MainTopBar(
                            cartItemCount = cartUiState.items.size,
                            isUserAuthenticated = authUiState.isAuthenticated,
                            onCartClick = {
                                navController.navigate("pantallaCarrito")
                            },
                            onProfileClick = {
                                navController.navigate("profile")
                            }
                        )
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "listaDeProductos",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("listaDeProductos") {
                            ProductListScreen(
                                onProductClick = { productId ->
                                    navController.navigate("detalleDeProducto/$productId")
                                }
                            )
                        }
                        composable(
                            route = "detalleDeProducto/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")
                            ProductDetailScreen(
                                productId = productId,
                                onProductAdded = { product, message ->
                                    cartViewModel.addToCart(product, message)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡'${product.name}' añadido al carrito!")
                                    }
                                }
                            )
                        }

                        composable("pantallaCarrito") {
                            ShoppingCartScreen(
                                cartUiState = cartUiState,
                                onCheckoutClick = {
                                    if (authUiState.isAuthenticated) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Procesando pago...")
                                            delay(2000)
                                            navController.navigate("boleta")
                                        }
                                    } else {
                                        navController.navigate("pantallaLogin")
                                    }
                                }
                            )
                        }

                        composable("boleta") {
                            BoletaScreen(
                                cartUiState = cartUiState,
                                onFinishClick = {
                                    cartViewModel.clearCart()
                                    navController.navigate("listaDeProductos") {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }

                        composable("pantallaLogin") {
                            LoginScreen(
                                authUiState = authUiState,
                                onLogin = { email, password ->
                                    authViewModel.login(email, password)
                                },
                                onRegister = { name, email, birthDate, password, discountCode ->
                                    authViewModel.register(name, email, birthDate, password, discountCode)
                                },
                                onClearError = {
                                    authViewModel.clearError()
                                }
                            )

                            LaunchedEffect(authUiState.isAuthenticated) {
                                if (authUiState.isAuthenticated) {
                                    navController.popBackStack()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Inicio de sesión exitoso! Descuento aplicado.")
                                    }
                                }
                            }
                        }

                        composable("profile") {
                            ProfileScreen(
                                authUiState = authUiState,
                                onUpdateProfile = { currentPassword, newName, newEmail, newPassword ->
                                    authViewModel.updateProfile(currentPassword, newName, newEmail, newPassword)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Perfil actualizado correctamente")
                                    }
                                },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("listaDeProductos") {
                                        popUpTo(0)
                                    }
                                },
                                onClearError = {
                                    authViewModel.clearError()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(cartItemCount: Int, isUserAuthenticated: Boolean, onCartClick: () -> Unit, onProfileClick: () -> Unit) {
    TopAppBar(
        title = { Text("Pastelería Mil Sabores", style = MaterialTheme.typography.headlineLarge, fontSize = 24.sp, color = Blanco) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Chocolate,
            titleContentColor = Blanco
        ),
        actions = {
            IconButton(onClick = onCartClick) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) { Badge { Text(cartItemCount.toString()) } }
                    }
                ) {
                    Icon(Icons.Filled.ShoppingCart, "Carrito de Compras", tint = Blanco)
                }
            }
            if (isUserAuthenticated) {
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Filled.AccountCircle, "Perfil de Usuario", tint = Blanco)
                }
            }
        }
    )
}

@Composable
fun ProductListScreen(onProductClick: (String) -> Unit) {
    val groupedProducts = ProductRepository.getAllProducts().groupBy { it.category }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        groupedProducts.forEach { (categoryName, productsInCategory) ->
            item {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 26.sp,
                    color = Blanco,
                    modifier = Modifier.fillMaxWidth().background(RosaSuave).padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(productsInCategory) { product ->
                ProductCard(
                    product = product,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column {
            product.image?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.headlineLarge, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.description, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ProductDetailScreen(productId: String?, onProductAdded: (Product, String?) -> Unit) {
    val product = ProductRepository.getAllProducts().find { it.id == productId }
    var specialMessage by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (product != null) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        product.image?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = product.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = product.name, style = MaterialTheme.typography.headlineLarge, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = product.description, style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = formatPrice(product.price),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedTextField(
                                value = specialMessage,
                                onValueChange = { specialMessage = it },
                                label = { Text("Mensaje especial (opcional)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Button(
                    onClick = { onProductAdded(product, specialMessage) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RosaSuave,
                        contentColor = Blanco
                    )
                ) {
                    Text("Agregar al Carrito", fontSize = 16.sp)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Producto no encontrado")
                }
            }
        }
    }
}

@Composable
fun ShoppingCartScreen(
    cartUiState: CartUiState,
    onCheckoutClick: () -> Unit
) {
    val subtotal = cartUiState.subtotal
    val discountAmount = cartUiState.discount
    val finalTotal = cartUiState.total

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Mi Carrito de Compras", style = MaterialTheme.typography.headlineLarge, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(16.dp))
            if (cartUiState.items.isEmpty()) {
                Text("Tu carrito está vacío.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(cartUiState.items) { item ->
                            CartListItem(item = item)
                            HorizontalDivider()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        "Subtotal: ${formatPrice(subtotal)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (discountAmount > 0) {
                        Text(
                            "Descuento: -${formatPrice(discountAmount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        "Total: ${formatPrice(finalTotal)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onCheckoutClick() }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RosaSuave,
                        contentColor = Blanco
                    )
                ) {
                    Text("Ir a Pagar", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun BoletaScreen(cartUiState: CartUiState, onFinishClick: () -> Unit) {
    val subtotal = cartUiState.subtotal
    val discountAmount = cartUiState.discount
    val finalTotal = cartUiState.total

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Boleta Electrónica", style = MaterialTheme.typography.headlineLarge, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(cartUiState.items) { item ->
                        CartListItem(item = item)
                        HorizontalDivider()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Text(
                    "Subtotal: ${formatPrice(subtotal)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (discountAmount > 0) {
                    Text(
                        "Descuento: -${formatPrice(discountAmount)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    "Total: ${formatPrice(finalTotal)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onFinishClick() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosaSuave,
                    contentColor = Blanco
                )
            ) {
                Text("Finalizar", fontSize = 16.sp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    authUiState: AuthUiState,
    onUpdateProfile: (String, String, String, String?) -> Unit,
    onLogout: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf(authUiState.user?.name ?: "") }
    var email by remember { mutableStateOf(authUiState.user?.email ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { onClearError() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Mi Perfil", style = MaterialTheme.typography.headlineLarge, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña Actual") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva Contraseña (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (authUiState.errorMessage != null) {
                Text(
                    text = authUiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = { onUpdateProfile(currentPassword, name, email, newPassword) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosaSuave,
                    contentColor = Blanco
                )
            ) {
                Text("Actualizar Perfil", fontSize = 16.sp, color = Blanco)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onLogout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Blanco
                )
            ) {
                Text("Cerrar Sesión", fontSize = 16.sp, color = Blanco)
            }
        }
    }
}

@Composable
fun CartListItem(item: CartItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            if (item.specialMessage != null) {
                Text(text = "Mensaje: \"${item.specialMessage}\"", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            }
        }
        Text(
            text = formatPrice(item.product.price),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    authUiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String, String?) -> Unit,
    onClearError: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { onClearError() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isLoginMode) "Iniciar Sesión" else "Registrarse",
                style = MaterialTheme.typography.headlineLarge, fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (!isLoginMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("ej: usuario@duoc.cl") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (!isLoginMode) {
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { },
                    label = { Text("Fecha de Nacimiento") },
                    placeholder = { Text("Formato: AAAA-MM-DD") },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    enabled = false,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (!isLoginMode) {
                OutlinedTextField(
                    value = discountCode,
                    onValueChange = { discountCode = it },
                    label = { Text("Código de Descuento (opcional)") },
                    placeholder = { Text("ej: FELICES50") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDate = datePickerState.selectedDateMillis
                                if (selectedDate != null) {
                                    birthDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                                        timeZone = TimeZone.getTimeZone("UTC")
                                    }.format(Date(selectedDate))
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (authUiState.errorMessage != null) {
                Text(
                    text = authUiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Button(
                onClick = {
                    if (isLoginMode) {
                        onLogin(email, password)
                    } else {
                        onRegister(name, email, birthDate, password, discountCode)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosaSuave,
                    contentColor = Blanco
                )
            ) {
                Text(
                    if (isLoginMode) "Ingresar" else "Registrarse",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isLoginMode) "¿No tienes una cuenta? Regístrate" else "¿Ya tienes una cuenta? Inicia Sesión",
                modifier = Modifier.clickable { isLoginMode = !isLoginMode },
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatPrice(price: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(price).replace("CLP", "") + " CLP"
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    PasteleriaMilSaboresTheme {
        ProductCard(
            product = Product(
                "TC001", "Tortas", "Torta de Chocolate", 45000, "Descripción de prueba.", R.drawable.torta_cuadrada_chocolate
            )
        )
    }
}
