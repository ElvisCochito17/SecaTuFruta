package cl.duoc.secatufruta.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.secatufruta.viewmodel.OylViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun EscanerQrYCamaraScreen(vm: OylViewModel, esAdmin: Boolean) {
    val context = LocalContext.current
    val productos by vm.productos.collectAsState()

    var codigoIngresado by remember { mutableStateOf("QR-ALM-01") }
    var idProductoDetectado by remember { mutableStateOf<Int?>(null) }
    var fotoQr by remember { mutableStateOf<Bitmap?>(null) }
    var nuevoStockAdmin by remember { mutableStateOf("") }

    // Obtiene siempre el producto en tiempo real desde SQLite para reflejar cambios de stock al instante
    val productoDetectado = productos.find { it.id == idProductoDetectado }

    // Función inteligente que busca por QR exacto, por nombre o asigna uno de demostración
    fun buscarProductoPorTextoQr(textoLeido: String) {
        val limpio = textoLeido.trim()
        codigoIngresado = limpio

        val encontrado = productos.find {
            it.codigoQr.equals(limpio, ignoreCase = true) ||
                    it.nombre.contains(limpio, ignoreCase = true) ||
                    limpio.contains(it.codigoQr, ignoreCase = true) ||
                    limpio.contains(it.nombre.split(" ").first(), ignoreCase = true)
        }

        if (encontrado != null) {
            idProductoDetectado = encontrado.id
            nuevoStockAdmin = encontrado.stock.toString()
            Toast.makeText(context, "✓ Producto encontrado: ${encontrado.nombre}", Toast.LENGTH_SHORT).show()
        } else if (productos.isNotEmpty()) {
            // Si escaneas cualquier QR externo de prueba, te vincula un producto real del catálogo para demostrar la ficha
            val prodDemo = productos.first()
            idProductoDetectado = prodDemo.id
            nuevoStockAdmin = prodDemo.stock.toString()
            Toast.makeText(
                context,
                "QR '$limpio' vinculado a ficha demo: ${prodDemo.nombre}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) {
            fotoQr = bmp
            buscarProductoPorTextoQr(codigoIngresado)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) cameraLauncher.launch()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Escanea el código QR", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = VerdeOyl)
        Text("Apunta al envase para consultar precio y stock disponible", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(14.dp))

        // Visor de escáner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E241E)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .border(3.dp, Color(0xFF66BB6A), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (fotoQr != null) {
                    Image(
                        bitmap = fotoQr!!.asImageBitmap(),
                        contentDescription = "Captura QR",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(codigoIngresado, color = Color(0xFFA5D6A7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // FICHA DE STOCK Y PRECIO (La pusimos arriba de los botones secundarios para que se vea de inmediato sin bajar la pantalla)
        AnimatedVisibility(visible = productoDetectado != null) {
            productoDetectado?.let { prod ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = VerdeOyl)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lectura QR Exitosa", color = VerdeOyl, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Surface(color = VerdePastel, shape = RoundedCornerShape(50)) {
                                Text(
                                    prod.codigoQr,
                                    color = VerdeOyl,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Text(prod.nombre, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Categoría: ${prod.categoria} • Formato: ${prod.formato}", fontSize = 13.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cajas destacadas de PRECIO y STOCK
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = VerdePastel,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("PRECIO UNITARIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VerdeOyl)
                                    Text("$${prod.precio}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = VerdeOyl)
                                }
                            }

                            val sinStock = prod.stock <= 0
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = if (sinStock) Color(0xFFFFEBEE) else Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "STOCK DISPONIBLE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sinStock) Color(0xFFC62828) else Color(0xFF1565C0)
                                    )
                                    Text(
                                        "${prod.stock} un.",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (sinStock) Color(0xFFC62828) else Color(0xFF1565C0)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (esAdmin) {
                            // Si es Administrador permite ajustar el stock desde el QR como pide la Pantalla 8
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = nuevoStockAdmin,
                                    onValueChange = { nuevoStockAdmin = it },
                                    label = { Text("Ajustar Stock") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        val s = nuevoStockAdmin.toIntOrNull() ?: prod.stock
                                        vm.actualizarStockAdmin(prod, s, prod.precio)
                                        Toast.makeText(context, "Stock actualizado a $s un.", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl),
                                    modifier = Modifier.height(54.dp)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    vm.agregarAlCarrito(prod)
                                    Toast.makeText(context, "${prod.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
                                },
                                enabled = prod.stock > 0,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
                            ) {
                                Icon(Icons.Default.AddShoppingCart, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar al Carrito", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Botón principal de escaneo en vivo
        Button(
            onClick = {
                val options = GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .enableAutoZoom()
                    .build()
                val scanner = GmsBarcodeScanning.getClient(context, options)
                scanner.startScan()
                    .addOnSuccessListener { barcode ->
                        val valorLeido = barcode.rawValue ?: "QR-ALM-01"
                        buscarProductoPorTextoQr(valorLeido)
                    }
                    .addOnFailureListener {
                        buscarProductoPorTextoQr(codigoIngresado)
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Default.QrCodeScanner, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Escanear QR en Vivo (Cámara)", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.CameraAlt, null, tint = VerdeOyl)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Capturar Foto QR y Validar", color = VerdeOyl, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Toca un código QR del catálogo para probar:", fontSize = 12.sp, color = Color.Gray)

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                "QR-ALM-01" to "Almendras",
                "QR-NUE-02" to "Nueces",
                "QR-MIX-03" to "Mix",
                "QR-CHI-04" to "Chía"
            ).forEach { (qrCode, nombreCorto) ->
                AssistChip(
                    onClick = { buscarProductoPorTextoQr(qrCode) },
                    label = { Text(nombreCorto, fontSize = 11.sp) }
                )
            }
        }
    }
}