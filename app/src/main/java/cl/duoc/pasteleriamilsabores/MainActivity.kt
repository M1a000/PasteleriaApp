package cl.duoc.pasteleriamilsabores

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import cl.duoc.pasteleriamilsabores.viewmodel.WishlistViewModel
import coil.compose.AsyncImage
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
                val wishlistViewModel: WishlistViewModel = viewModel()
                val cartUiState by cartViewModel.uiState.collectAsState()
                val authUiState by authViewModel.uiState.collectAsState()
                val wishlistUiState by wishlistViewModel.uiState.collectAsState()

                var showLoginDialog by remember { mutableStateOf(false) }

                if (showLoginDialog) {
                    AlertDialog(
                        onDismissRequest = { showLoginDialog = false },
                        title = { Text("Iniciar Sesión Requerido") },
                        text = { Text("Debes iniciar sesión para añadir productos a tu lista de deseados.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showLoginDialog = false
                                    navController.navigate("pantallaLogin")
                                }
                            ) {
                                Text("Iniciar Sesión")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLoginDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

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
                            },
                            onWishlistClick = {
                                navController.navigate("wishlist")
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
                                authUiState = authUiState,
                                onProductClick = { productId ->
                                    navController.navigate("detalleDeProducto/$productId")
                                },
                                wishlistViewModel = wishlistViewModel,
                                onToggleWishlist = {
                                    if (authUiState.isAuthenticated) {
                                        wishlistViewModel.toggleWishlist(it)
                                        scope.launch {
                                            val message = if (wishlistViewModel.isWishlisted(it)) {
                                                "Añadido a la lista de deseados"
                                            } else {
                                                "Eliminado de la lista de deseados"
                                            }
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    } else {
                                        showLoginDialog = true
                                    }
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
                                wishlistViewModel = wishlistViewModel,
                                onProductAdded = { product, message ->
                                    cartViewModel.addToCart(product, message)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡'${product.name}' añadido al carrito!")
                                    }
                                },
                                onToggleWishlist = {
                                    if (authUiState.isAuthenticated) {
                                        wishlistViewModel.toggleWishlist(it)
                                        scope.launch {
                                            val message = if (wishlistViewModel.isWishlisted(it)) {
                                                "Añadido a la lista de deseados"
                                            } else {
                                                "Eliminado de la lista de deseados"
                                            }
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    } else {
                                        showLoginDialog = true
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

                        composable("wishlist") {
                            WishlistScreen(
                                wishlistUiState = wishlistUiState,
                                onProductClick = { productId ->
                                    navController.navigate("detalleDeProducto/$productId")
                                },
                                onToggleWishlist = { 
                                    wishlistViewModel.toggleWishlist(it)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Eliminado de la lista de deseados")
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
                                onRegister = { name, email, birthDate, password, confirmPassword, discountCode, profilePictureUri ->
                                    authViewModel.register(name, email, birthDate, password, confirmPassword, discountCode, profilePictureUri)
                                },
                                onClearError = {
                                    authViewModel.clearError()
                                }
                            )

                            LaunchedEffect(authUiState.isAuthenticated) {
                                if (authUiState.isAuthenticated) {
                                    if (authUiState.isAdmin) {
                                        navController.navigate("adminPanel") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                        }
                                    } else {
                                        navController.popBackStack()
                                    }
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Inicio de sesión exitoso!")
                                    }
                                }
                            }
                        }

                        composable("profile") {
                            ProfileScreen(
                                authUiState = authUiState,
                                onUpdateProfile = { currentPassword, newName, newEmail, newPassword, newProfilePictureUri ->
                                    val success = authViewModel.updateProfile(currentPassword, newName, newEmail, newPassword, newProfilePictureUri)
                                    if (success) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Perfil actualizado correctamente")
                                        }
                                    }
                                    success
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
                        
                        composable("adminPanel") {
                            AdminScreen(
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("listaDeProductos") {
                                        popUpTo(0)
                                    }
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
fun MainTopBar(cartItemCount: Int, isUserAuthenticated: Boolean, onCartClick: () -> Unit, onProfileClick: () -> Unit, onWishlistClick: () -> Unit) {
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
                IconButton(onClick = onWishlistClick) {
                    Icon(Icons.Filled.Favorite, "Lista de Deseados", tint = Blanco)
                }
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Filled.AccountCircle, "Perfil de Usuario", tint = Blanco)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    authUiState: AuthUiState,
    onProductClick: (String) -> Unit,
    wishlistViewModel: WishlistViewModel,
    onToggleWishlist: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = remember { listOf("Todas") + ProductRepository.getAllProducts().map { it.category }.distinct() }
    var selectedCategory by remember { mutableStateOf("Todas") }
    val wishlistUiState by wishlistViewModel.uiState.collectAsState()

    val filteredProducts = remember(searchQuery, selectedCategory) {
        val allProducts = ProductRepository.getAllProducts()

        val productsByCategory = if (selectedCategory == "Todas") {
            allProducts
        } else {
            allProducts.filter { it.category == selectedCategory }
        }

        if (searchQuery.isBlank()) {
            productsByCategory
        } else {
            productsByCategory.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar producto...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Chocolate) },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Chocolate,
                        labelColor = Blanco,
                        selectedContainerColor = RosaSuave,
                        selectedLabelColor = Blanco
                    )
                )
            }
        }

        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontraron coincidencias.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlistUiState.wishlistedProductIds.contains(product.id),
                        onToggleWishlist = { onToggleWishlist(product.id) },
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                product.image?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Añadir a deseados",
                        tint = if (isWishlisted) Color.Red else Color.Gray
                    )
                }
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
fun WishlistScreen(
    wishlistUiState: cl.duoc.pasteleriamilsabores.viewmodel.WishlistUiState,
    onProductClick: (String) -> Unit,
    onToggleWishlist: (String) -> Unit
) {
    val wishlistedProducts = ProductRepository.getAllProducts().filter { wishlistUiState.wishlistedProductIds.contains(it.id) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mi Lista de Deseados", style = MaterialTheme.typography.headlineLarge, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (wishlistedProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No has añadido nada a tu lista de deseados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(wishlistedProducts) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = true,
                        onToggleWishlist = { onToggleWishlist(product.id) },
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(
    productId: String?,
    wishlistViewModel: WishlistViewModel,
    onProductAdded: (Product, String?) -> Unit,
    onToggleWishlist: (String) -> Unit
) {
    val product = ProductRepository.getAllProducts().find { it.id == productId }
    var specialMessage by remember { mutableStateOf("") }
    val wishlistUiState by wishlistViewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (product != null) {
                val isWishlisted = wishlistUiState.wishlistedProductIds.contains(product.id)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            product.image?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            IconButton(
                                onClick = { onToggleWishlist(product.id) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Añadir a deseados",
                                    tint = if (isWishlisted) Color.Red else Color.Gray
                                )
                            }
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
    onUpdateProfile: (String, String, String, String?, String?) -> Boolean,
    onLogout: () -> Unit,
    onClearError: () -> Unit
) {
    var editingField by remember { mutableStateOf<String?>(null) }
    var newValue by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    
    var profilePictureUri by remember { mutableStateOf(authUiState.user?.profilePictureUri?.let { Uri.parse(it) }) }
    var showPhotoDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            profilePictureUri = uri
        }
    )

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Cambiar foto de perfil") },
            text = { Text("¿Deseas seleccionar una nueva foto de tu galería?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPhotoDialog = false
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    if (editingField != null) {
        val user = authUiState.user!!
        AlertDialog(
            onDismissRequest = { editingField = null },
            title = { Text("Editar ${editingField}") },
            text = {
                Column {
                    if (editingField == "Contraseña") {
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Nueva contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = { confirmNewPassword = it },
                            label = { Text("Confirmar nueva contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = newValue,
                            onValueChange = { newValue = it },
                            label = { Text("Nuevo ${editingField}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Contraseña actual para confirmar") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (authUiState.errorMessage != null) {
                        Text(
                            text = authUiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val success = when (editingField) {
                            "Nombre" -> onUpdateProfile(currentPassword, newValue, user.email, null, profilePictureUri?.toString())
                            "Correo" -> onUpdateProfile(currentPassword, user.name, newValue, null, profilePictureUri?.toString())
                            "Contraseña" -> {
                                if (newPassword == confirmNewPassword) {
                                    onUpdateProfile(currentPassword, user.name, user.email, newPassword, profilePictureUri?.toString())
                                } else {
                                    // Idealmente, mostrar un error al usuario aquí
                                    false
                                }
                            }
                            else -> false
                        }
                        if (success) {
                            editingField = null
                            newValue = ""
                            currentPassword = ""
                            newPassword = ""
                            confirmNewPassword = ""
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingField = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(authUiState) {
        if (authUiState.user != null) {
            profilePictureUri = authUiState.user.profilePictureUri?.let { Uri.parse(it) }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            if (profilePictureUri != null) {
                AsyncImage(
                    model = profilePictureUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(150.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Foto de perfil por defecto", modifier = Modifier.size(150.dp))
            }
            Text(
                text = "Cambiar foto de perfil",
                modifier = Modifier.clickable { showPhotoDialog = true }.padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileInfoRow(
                label = "Nombre:",
                value = authUiState.user?.name ?: "",
                onEditClick = { 
                    newValue = authUiState.user?.name ?: ""
                    editingField = "Nombre" 
                }
            )
            ProfileInfoRow(
                label = "Correo:",
                value = authUiState.user?.email ?: "",
                onEditClick = { 
                    newValue = authUiState.user?.email ?: ""
                    editingField = "Correo" 
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { editingField = "Contraseña" }) {
                Text("Cambiar contraseña")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión", color = Blanco)
            }
        }
    }
}

@Composable
fun AdminScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Panel de Administrador", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Text("¡Bienvenido, dueño! Aquí podrás gestionar tu pastelería.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión", color = Blanco)
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label $value", style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Editar",
            modifier = Modifier.clickable(onClick = onEditClick),
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
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
    onRegister: (String, String, String, String, String, String?, String?) -> Unit,
    onClearError: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            profilePictureUri = uri
        }
    )

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { onClearError() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoginMode) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }) {
                    if (profilePictureUri != null) {
                        AsyncImage(
                            model = profilePictureUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(150.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Añadir foto de perfil", modifier = Modifier.size(150.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Image(
                    painter = painterResource(id = R.drawable.login_icon),
                    contentDescription = "Login Icon",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                        onRegister(name, email, birthDate, password, confirmPassword, discountCode, profilePictureUri?.toString())
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
            ),
            isWishlisted = false,
            onToggleWishlist = {}
        )
    }
}
