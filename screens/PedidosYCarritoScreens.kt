package cl.duoc.secatufruta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
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

@Composable
fun CarritoScreen(vm: OylViewModel, onIrAPedidos: () -> Unit) {
    val items by vm.obtenerCarritoActual().collectAsState(initial = emptyList())
    var pedidoConfirmado by remember { mutableStateOf(false) }

    if (pedidoConfirmado) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoCrema)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = VerdePastel,
                modifier = Modifier.size(110.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("📦", fontSize = 54.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("¡Pedido confirmado!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = VerdeOyl)
            Text("Tu pedido ha sido recibido y el stock fue reservado.", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Estado inicial:", color = Color.Gray)
                        Text("Pendiente de Validación", fontWeight = FontWeight.Bold, color = VerdeOyl)
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Modalidad:", color = Color.Gray)
                        Text("Retiro en tienda / Despacho", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrAPedidos,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
            ) {
                Text("Ver mis pedidos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        return
    }

    val subtotal = items.sumOf { it.precioUnitario * it.cantidad }
    val despacho = if (items.isNotEmpty()) 2000 else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(16.dp)
    ) {
        Text("Mi Carrito (${items.size})", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = VerdeOyl)
        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tu carrito está vacío", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Agrega frutos secos desde el catálogo", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = VerdePastel,
                                    modifier = Modifier.size(50.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🌰", fontSize = 24.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.nombreProducto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("250 gr • Cant: ${item.cantidad}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Text(
                                "$${item.precioUnitario * item.cantidad}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = VerdeOyl
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Resumen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Subtotal", color = Color.Gray)
                        Text("$$subtotal", fontWeight = FontWeight.SemiBold)
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Despacho", color = Color.Gray)
                        Text("$$despacho", fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("$${subtotal + despacho}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = VerdeOyl)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { vm.confirmarPedido(items) { pedidoConfirmado = true } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
                    ) {
                        Text("Continuar compra", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialPedidosScreen(vm: OylViewModel, esAdmin: Boolean) {
    val todos by vm.todosLosPedidos.collectAsState()
    val misPedidos = if (esAdmin) todos else todos.filter { it.clienteCorreo == vm.usuarioActual?.correo }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(16.dp)
    ) {
        Text(
            if (esAdmin) "Control de Pedidos (Admin)" else "Mis Pedidos",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = VerdeOyl
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (misPedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no hay pedidos registrados.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(misPedidos) { ped ->
                    val (bgEstado, txtEstado) = when (ped.estado) {
                        "Pendiente de Validación" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                        "Confirmado" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                        "Preparado" -> Color(0xFFF3E5F5) to Color(0xFF6A1B9A)
                        else -> Color(0xFFE8F5E9) to VerdeOyl
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = VerdePastel,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.ShoppingBag, null, tint = VerdeOyl)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Pedido #00${ped.id}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                        Text(ped.fecha, fontSize = 12.sp, color = Color.Gray)
                                    }
                                }

                                Surface(
                                    color = bgEstado,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        ped.estado,
                                        color = txtEstado,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Artículos: ${ped.detalle}", fontSize = 14.sp)
                            if (esAdmin) {
                                Text("Cliente: ${ped.clienteCorreo}", fontSize = 12.sp, color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total: $${ped.total}", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = VerdeOyl)

                                if (esAdmin && ped.estado != "Entregado") {
                                    Button(
                                        onClick = { vm.avanzarEstadoPedido(ped) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOyl)
                                    ) {
                                        Icon(Icons.Default.LocalShipping, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Avanzar Estado", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}