package cl.duoc.secatufruta.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.secatufruta.data.ProductoEntity
import cl.duoc.secatufruta.viewmodel.OylViewModel

@Composable
fun CatalogoYAdminScreen(vm: OylViewModel, esAdmin: Boolean) {
    val productos by vm.productos.collectAsState()
    var categoriaFiltro by remember { mutableStateOf("Todos") }
    var busqueda by remember { mutableStateOf("") }
    var mostrarModalNuevo by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<ProductoEntity?>(null) }

    val categorias = listOf("Todos", "Frutos secos", "Semillas", "Deshidratados")
    val filtrados = productos.filter {
        (categoriaFiltro == "Todos" || it.categoria == categoriaFiltro) &&
                it.nombre.contains(busqueda, ignoreCase = true)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Banner promocional superior estilo Mockup 4
        item(span = { GridItemSpan(2) }) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(VerdeOyl, Color(0xFF43A047))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    if (esAdmin) "GESTIÓN EN TERRENO" else "100% NATURAL",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                if (esAdmin) "Control de Stock e Inventario" else "Compra saludable,\nvida mejor",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 24.sp
                            )
                        }
                        Text("🥜", fontSize = 48.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Buscador estilizado
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar almendras, nueces, semillas...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = VerdeOyl) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Chips de categorías
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categorias) { cat ->
                        val activo = categoriaFiltro == cat
                        FilterChip(
                            selected = activo,
                            onClick = { categoriaFiltro = cat },
                            label = {
                                Text(
                                    cat,
                                    fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VerdeOyl,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }

                if (esAdmin) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { mostrarModalNuevo = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
                    ) {
                        Icon(Icons.Default.AddAPhoto, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nuevo Producto (Capturar con Cámara)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tarjetas de productos en grilla de 2 columnas (Mockup 6)
        items(filtrados) { prod ->
            ProductoGridCard(
                prod = prod,
                esAdmin = esAdmin,
                onAddCart = { vm.agregarAlCarrito(prod) },
                onEditAdmin = { productoAEditar = prod }
            )
        }
    }

    // Modal Admin: Crear Producto
    if (mostrarModalNuevo) {
        var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
            fotoBitmap = bmp
        }
        val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) cameraLauncher.launch()
        }

        AlertDialog(
            onDismissRequest = { mostrarModalNuevo = false },
            title = { Text("Registrar Nuevo Producto", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = vm.prodNombre,
                        onValueChange = { vm.prodNombre = it; vm.errorProdNombre = null },
                        label = { Text("Nombre comercial") },
                        isError = vm.errorProdNombre != null,
                        supportingText = { vm.errorProdNombre?.let { Text(it) } }
                    )
                    OutlinedTextField(
                        value = vm.prodPrecio,
                        onValueChange = { vm.prodPrecio = it; vm.errorProdPrecio = null },
                        label = { Text("Precio unitario ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = vm.errorProdPrecio != null,
                        supportingText = { vm.errorProdPrecio?.let { Text(it) } }
                    )
                    OutlinedTextField(
                        value = vm.prodStock,
                        onValueChange = { vm.prodStock = it; vm.errorProdStock = null },
                        label = { Text("Stock disponible") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = vm.errorProdStock != null,
                        supportingText = { vm.errorProdStock?.let { Text(it) } }
                    )
                    OutlinedButton(
                        onClick = { permLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (fotoBitmap != null) "Fotografía lista ✓" else "Fotografiar Producto")
                    }
                    fotoBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp))
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { vm.validarYGuardarProducto(fotoBitmap != null) { mostrarModalNuevo = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalNuevo = false }) { Text("Cancelar") }
            }
        )
    }

    // Modal Admin: Ajustar Stock
    productoAEditar?.let { prod ->
        var nuevoStock by remember { mutableStateOf(prod.stock.toString()) }
        var nuevoPrecio by remember { mutableStateOf(prod.precio.toString()) }
        AlertDialog(
            onDismissRequest = { productoAEditar = null },
            title = { Text("Ajustar Stock: ${prod.nombre}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nuevoStock,
                        onValueChange = { nuevoStock = it },
                        label = { Text("Existencias reales en estante") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = nuevoPrecio,
                        onValueChange = { nuevoPrecio = it },
                        label = { Text("Precio unitario ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = nuevoStock.toIntOrNull() ?: prod.stock
                        val p = nuevoPrecio.toIntOrNull() ?: prod.precio
                        vm.actualizarStockAdmin(prod, s, p)
                        productoAEditar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
                ) { Text("Confirmar Ajuste") }
            },
            dismissButton = {
                TextButton(onClick = { productoAEditar = null }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun ProductoGridCard(
    prod: ProductoEntity,
    esAdmin: Boolean,
    onAddCart: () -> Unit,
    onEditAdmin: () -> Unit
) {
    val (emoji, colorFondo) = when {
        prod.nombre.contains("Almendra", true) -> "🌰" to Color(0xFFFFF3E0)
        prod.nombre.contains("Nuez", true) || prod.nombre.contains("Nueces", true) -> "🥜" to Color(0xFFEFEBE9)
        prod.nombre.contains("Mix", true) -> "🥣" to Color(0xFFFFF8E1)
        prod.nombre.contains("Chía", true) || prod.categoria.contains("Semilla", true) -> "🌱" to Color(0xFFE8F5E9)
        prod.nombre.contains("Cranberr", true) || prod.categoria.contains("Deshidratado", true) -> "🍒" to Color(0xFFFCE4EC)
        else -> "🥜" to Color(0xFFF1F8E9)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Cabecera visual del fruto seco
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorFondo),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 46.sp)

                // Etiqueta de formato arriba a la derecha
                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        prod.formato,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOyl,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = prod.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = prod.categoria,
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${prod.precio}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = VerdeOyl
                    )
                    val stockBajo = prod.stock <= 5
                    Text(
                        text = "Stock: ${prod.stock} un.",
                        fontSize = 11.sp,
                        color = if (stockBajo) Color(0xFFD32F2F) else Color(0xFF388E3C),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Botón circular verde igual al mockup 6
                IconButton(
                    onClick = if (esAdmin) onEditAdmin else onAddCart,
                    enabled = esAdmin || prod.stock > 0,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (esAdmin || prod.stock > 0) VerdeOyl else Color.LightGray)
                ) {
                    Icon(
                        imageVector = if (esAdmin) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}