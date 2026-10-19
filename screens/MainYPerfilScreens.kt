package cl.duoc.secatufruta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.secatufruta.viewmodel.OylViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(vm: OylViewModel, onLogout: () -> Unit) {
    var tabActual by remember { mutableStateOf("catalogo") }
    val user = vm.usuarioActual
    val esAdmin = user?.rol == "ADMIN"
    val carrito by vm.obtenerCarritoActual().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Column {
                        Text(
                            "¡Hola, ${user?.nombre?.split(" ")?.firstOrNull() ?: ""}!",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = VerdeOyl
                        )
                        Text(
                            if (esAdmin) "Panel Administrador • Sucursal OYL" else "Qué bueno verte de nuevo",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    if (!esAdmin) {
                        BadgedBox(
                            badge = {
                                if (carrito.isNotEmpty()) {
                                    Badge(containerColor = Color.Red) { Text("${carrito.size}") }
                                }
                            },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = VerdePastel,
                                modifier = Modifier.size(42.dp)
                            ) {
                                IconButton(onClick = { tabActual = "carrito" }) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = VerdeOyl)
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = tabActual == "catalogo",
                    onClick = { tabActual = "catalogo" },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VerdeOyl,
                        indicatorColor = VerdeOyl
                    )
                )
                NavigationBarItem(
                    selected = tabActual == "mapa",
                    onClick = { tabActual = "mapa" },
                    icon = { Icon(Icons.Default.LocationOn, null) },
                    label = { Text("Mapa") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VerdeOyl,
                        indicatorColor = VerdeOyl
                    )
                )
                NavigationBarItem(
                    selected = tabActual == "qr",
                    onClick = { tabActual = "qr" },
                    icon = { Icon(Icons.Default.QrCodeScanner, null) },
                    label = { Text("Escanear") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VerdeOyl,
                        indicatorColor = VerdeOyl
                    )
                )
                NavigationBarItem(
                    selected = tabActual == "pedidos",
                    onClick = { tabActual = "pedidos" },
                    icon = { Icon(Icons.Default.ReceiptLong, null) },
                    label = { Text("Pedidos") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VerdeOyl,
                        indicatorColor = VerdeOyl
                    )
                )
                NavigationBarItem(
                    selected = tabActual == "perfil",
                    onClick = { tabActual = "perfil" },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VerdeOyl,
                        indicatorColor = VerdeOyl
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (tabActual) {
                "catalogo" -> CatalogoYAdminScreen(vm, esAdmin)
                "mapa" -> MapaTiendasScreen()
                "qr" -> EscanerQrYCamaraScreen(vm, esAdmin)
                "carrito" -> CarritoScreen(vm, onIrAPedidos = { tabActual = "pedidos" })
                "pedidos" -> HistorialPedidosScreen(vm, esAdmin)
                "perfil" -> PerfilScreen(vm, onLogout)
            }
        }
    }
}

@Composable
fun MapaTiendasScreen() {
    val sucursales = listOf(
        Triple("Tienda OYL - Santiago Centro", "Av. Libertador B. O'Higgins 1234", "Abierta • 08:00 - 19:00"),
        Triple("Punto de Retiro Providencia", "Av. Providencia 2150, Local 4", "Abierto • 09:30 - 18:30"),
        Triple("Punto de Retiro Maipú", "Av. Pajaritos 3100", "Abierto • 10:00 - 19:00")
    )
    var seleccionada by remember { mutableStateOf(sucursales.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(16.dp)
    ) {
        Text("Tiendas y Puntos de Retiro", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = VerdeOyl)
        Spacer(modifier = Modifier.height(12.dp))

        // Lienzo de mapa estilizado estilo Mockup 5
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFE6EFE6)),
            contentAlignment = Alignment.Center
        ) {
            // Marcadores simulados en el mapa
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(34.dp).align(Alignment.TopStart).offset(x = 50.dp, y = 40.dp)
            )
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(34.dp).align(Alignment.BottomEnd).offset(x = (-60).dp, y = (-40).dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = VerdeOyl,
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Storefront, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(seleccionada.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(seleccionada.second, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Sucursales habilitadas:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(sucursales) { suc ->
                val esActiva = seleccionada == suc
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { seleccionada = suc },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (esActiva) VerdePastel else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (esActiva) VerdeOyl else Color(0xFFEEEEEE),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Store,
                                    contentDescription = null,
                                    tint = if (esActiva) Color.White else Color.DarkGray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(suc.first, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(suc.second, fontSize = 13.sp, color = Color.DarkGray)
                            Text(suc.third, fontSize = 12.sp, color = VerdeOyl, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerfilScreen(vm: OylViewModel, onLogout: () -> Unit) {
    val user = vm.usuarioActual ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = VerdePastel,
            modifier = Modifier.size(92.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(if (user.rol == "ADMIN") "👨🏻‍💼" else "👩🏻", fontSize = 46.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(user.nombre, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(user.correo, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            color = VerdePastel,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                "Perfil: ${user.rol}",
                color = VerdeOyl,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                FilaPerfilItem(Icons.Default.Phone, "Teléfono de contacto", user.telefono)
                HorizontalDivider()
                FilaPerfilItem(Icons.Default.LocationOn, "Dirección registrada", user.direccion)
                HorizontalDivider()
                FilaPerfilItem(Icons.Default.VerifiedUser, "Permisos Nativos", "Cámara QR y Notificaciones Activos ✓")
                HorizontalDivider()
                FilaPerfilItem(Icons.Default.Storage, "Persistencia Local", "Room SQLite Sincronizado ✓")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { vm.cerrarSesion(); onLogout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Default.ExitToApp, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun FilaPerfilItem(icono: androidx.compose.ui.graphics.vector.ImageVector, titulo: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = VerdePastel,
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icono, null, tint = VerdeOyl, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(titulo, fontSize = 12.sp, color = Color.Gray)
            Text(valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}