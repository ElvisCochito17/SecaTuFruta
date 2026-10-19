package cl.duoc.secatufruta.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.secatufruta.viewmodel.OylViewModel

val VerdeOyl = Color(0xFF1B5E20)
val VerdeClaroOyl = Color(0xFF2E7D32)
val FondoCrema = Color(0xFFFDFBF7)
val VerdePastel = Color(0xFFE8F5E9)

@Composable
fun BienvenidaScreen(onComenzar: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(VerdePastel, FondoCrema, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700)) + expandVertically(tween(700))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(110.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌰", fontSize = 54.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "OYL",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdeOyl,
                        letterSpacing = 2.sp
                    )
                    Text(
                        "FRUTOS SECOS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeClaroOyl,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Productos naturales más cerca de ti",
                        fontSize = 15.sp,
                        color = Color(0xFF555555),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            BeneficioItem(Icons.Default.Inventory2, "Catálogo actualizado en tiempo real")
                            BeneficioItem(Icons.Default.ShoppingBag, "Pedidos online ágiles sin espera")
                            BeneficioItem(Icons.Default.LocalShipping, "Retiros en tienda y despachos")
                            BeneficioItem(Icons.Default.Favorite, "100% productos saludables y frescos")
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onComenzar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Comenzar", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onComenzar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ya tengo una cuenta", color = VerdeOyl, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun BeneficioItem(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = VerdePastel,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = null, tint = VerdeOyl, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF333333))
    }
}

@Composable
fun AuthScreen(vm: OylViewModel, onLoginSuccess: () -> Unit) {
    var esRegistro by remember { mutableStateOf(false) }
    var rolSeleccionadoTab by remember { mutableStateOf("Cliente") }
    var mostrarPass by remember { mutableStateOf(false) }
    var avatarElegido by remember { mutableIntStateOf(1) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Logo superior estilo mockup
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = VerdePastel
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(if (esRegistro) "👩🏻‍🌾" else "🌿", fontSize = 34.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (esRegistro) "Crear Cuenta" else "OYL FRUTOS SECOS",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = VerdeOyl
        )
        Text(
            text = if (esRegistro) "Regístrate para hacer tus pedidos ágiles" else "Bienvenido de vuelta",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Selector estilo Píldora Cliente / Administrador (Mockup 3)
        if (!esRegistro) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFEAEAEA),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    listOf("Cliente", "Administrador").forEach { rol ->
                        val activo = rolSeleccionadoTab == rol
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(if (activo) VerdeOyl else Color.Transparent)
                                .clickable {
                                    rolSeleccionadoTab = rol
                                    if (rol == "Cliente") {
                                        vm.correoInput = "ana@correo.cl"
                                        vm.passInput = "123456"
                                    } else {
                                        vm.correoInput = "admin@admin.cl"
                                        vm.passInput = "admin123"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = rol,
                                color = if (activo) Color.White else Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tarjeta contenedora del formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (esRegistro) {
                    OutlinedTextField(
                        value = vm.nombreInput,
                        onValueChange = { vm.nombreInput = it; vm.errorNombre = null },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = VerdeOyl) },
                        isError = vm.errorNombre != null,
                        supportingText = { vm.errorNombre?.let { Text(it) } },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = vm.correoInput,
                    onValueChange = { vm.correoInput = it; vm.errorCorreo = null },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = VerdeOyl) },
                    isError = vm.errorCorreo != null,
                    supportingText = { vm.errorCorreo?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vm.passInput,
                    onValueChange = { vm.passInput = it; vm.errorPass = null },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = VerdeOyl) },
                    trailingIcon = {
                        IconButton(onClick = { mostrarPass = !mostrarPass }) {
                            Icon(
                                if (mostrarPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (mostrarPass) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = vm.errorPass != null,
                    supportingText = { vm.errorPass?.let { Text(it) } },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (esRegistro) {
                    OutlinedTextField(
                        value = vm.telefonoInput,
                        onValueChange = { vm.telefonoInput = it; vm.errorTelefono = null },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, null, tint = VerdeOyl) },
                        isError = vm.errorTelefono != null,
                        supportingText = { vm.errorTelefono?.let { Text(it) } },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = vm.direccionInput,
                        onValueChange = { vm.direccionInput = it },
                        label = { Text("Dirección (opcional)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = VerdeOyl) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Selecciona un avatar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val avatares = listOf("👩🏻", "🧑🏽", "👩🏼‍🦱")
                        avatares.forEachIndexed { index, emoji ->
                            val seleccionado = avatarElegido == index
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (seleccionado) VerdePastel else Color(0xFFF2F2F2),
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(
                                            width = if (seleccionado) 2.dp else 0.dp,
                                            color = if (seleccionado) VerdeOyl else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { avatarElegido = index }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(emoji, fontSize = 30.sp)
                                    }
                                }
                                if (seleccionado) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = VerdeOyl,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

                vm.mensajeGlobal?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            it,
                            color = Color(0xFFC62828),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (esRegistro) vm.validarYRegistrar(onLoginSuccess)
                        else vm.validarYLogin(onLoginSuccess)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
                ) {
                    Text(
                        if (esRegistro) "Crear Cuenta" else "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        TextButton(onClick = { esRegistro = !esRegistro; vm.mensajeGlobal = null }) {
            Text(
                if (esRegistro) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                color = VerdeOyl,
                fontWeight = FontWeight.Bold
            )
        }
    }
}