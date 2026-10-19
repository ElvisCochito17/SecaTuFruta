package cl.duoc.secatufruta.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.secatufruta.data.*
import cl.duoc.secatufruta.utils.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OylViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = OylDatabase.getDatabase(application).dao()

    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)
        private set

    var nombreInput by mutableStateOf("")
    var correoInput by mutableStateOf("")
    var passInput by mutableStateOf("")
    var telefonoInput by mutableStateOf("+56 9 ")
    var direccionInput by mutableStateOf("Santiago, RM")

    var errorNombre by mutableStateOf<String?>(null)
    var errorCorreo by mutableStateOf<String?>(null)
    var errorPass by mutableStateOf<String?>(null)
    var errorTelefono by mutableStateOf<String?>(null)
    var mensajeGlobal by mutableStateOf<String?>(null)

    var prodNombre by mutableStateOf("")
    var prodCategoria by mutableStateOf("Frutos secos")
    var prodPrecio by mutableStateOf("")
    var prodStock by mutableStateOf("")
    var errorProdNombre by mutableStateOf<String?>(null)
    var errorProdPrecio by mutableStateOf<String?>(null)
    var errorProdStock by mutableStateOf<String?>(null)

    val productos: StateFlow<List<ProductoEntity>> = dao.obtenerProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todosLosPedidos: StateFlow<List<PedidoEntity>> = dao.obtenerTodosLosPedidos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        precargarDatosDePrueba()
    }

    private fun precargarDatosDePrueba() {
        viewModelScope.launch {
            val adminExistente = dao.login("admin@admin.cl", "admin123")
            if (adminExistente == null) {
                dao.registrarUsuario(
                    UsuarioEntity(
                        correo = "admin@admin.cl",
                        nombre = "Administrador",
                        password = "admin123",
                        telefono = "+56 9 1234 1234",
                        direccion = "Santiago",
                        rol = "ADMIN"
                    )
                )
                dao.registrarUsuario(
                    UsuarioEntity(
                        correo = "ana@correo.cl",
                        nombre = "Ana Torres",
                        password = "123456",
                        telefono = "+56 9 1234 5678",
                        direccion = "Santiago, RM",
                        rol = "CLIENTE"
                    )
                )
            }

            if (productos.first().isEmpty()) {
                dao.guardarProducto(ProductoEntity(nombre = "Almendras Naturales", categoria = "Frutos secos", precio = 2990, stock = 25, codigoQr = "QR-ALM-01"))
                dao.guardarProducto(ProductoEntity(nombre = "Nueces Mariposa", categoria = "Frutos secos", precio = 3490, stock = 18, codigoQr = "QR-NUE-02"))
                dao.guardarProducto(ProductoEntity(nombre = "Mix Premium", categoria = "Frutos secos", precio = 3990, stock = 40, codigoQr = "QR-MIX-03"))
                dao.guardarProducto(ProductoEntity(nombre = "Semillas de Chía", categoria = "Semillas", precio = 2290, stock = 32, codigoQr = "QR-CHI-04"))
                dao.guardarProducto(ProductoEntity(nombre = "Cranberries Deshidratados", categoria = "Deshidratados", precio = 2890, stock = 16, codigoQr = "QR-CRA-05"))
            }
        }
    }

    fun validarYLogin(onExito: () -> Unit) {
        errorCorreo = if (!correoInput.contains("@")) "Ingresa un correo válido (ej: usuario@correo.cl)" else null
        errorPass = if (passInput.length < 4) "La contraseña debe tener al menos 4 caracteres" else null

        if (errorCorreo == null && errorPass == null) {
            viewModelScope.launch {
                val user = dao.login(correoInput.trim(), passInput.trim())
                if (user != null) {
                    usuarioActual = user
                    mensajeGlobal = null
                    onExito()
                } else {
                    mensajeGlobal = "Credenciales incorrectas. Prueba ana@correo.cl (123456) o admin@admin.cl (admin123)"
                }
            }
        }
    }

    fun validarYRegistrar(onExito: () -> Unit) {
        errorNombre = if (nombreInput.isBlank()) "El nombre completo es obligatorio" else null
        errorCorreo = if (!correoInput.contains("@") || !correoInput.contains(".")) "Formato de correo inválido" else null
        errorPass = if (passInput.length < 6) "Mínimo 6 caracteres por seguridad" else null
        errorTelefono = if (telefonoInput.length < 8) "Teléfono de contacto incompleto" else null

        if (errorNombre == null && errorCorreo == null && errorPass == null && errorTelefono == null) {
            viewModelScope.launch {
                val nuevo = UsuarioEntity(
                    correo = correoInput.trim(),
                    nombre = nombreInput.trim(),
                    password = passInput.trim(),
                    telefono = telefonoInput.trim(),
                    direccion = direccionInput.trim(),
                    rol = "CLIENTE"
                )
                dao.registrarUsuario(nuevo)
                usuarioActual = nuevo
                onExito()
            }
        }
    }

    fun validarYGuardarProducto(imagenCapturada: Boolean, onExito: () -> Unit) {
        errorProdNombre = if (prodNombre.isBlank()) "Ingresa el nombre comercial" else null
        val precioNum = prodPrecio.toIntOrNull()
        errorProdPrecio = if (precioNum == null || precioNum <= 0) "Precio debe ser mayor a $0" else null
        val stockNum = prodStock.toIntOrNull()
        errorProdStock = if (stockNum == null || stockNum < 0) "Stock inválido" else null

        if (errorProdNombre == null && errorProdPrecio == null && errorProdStock == null) {
            viewModelScope.launch {
                dao.guardarProducto(
                    ProductoEntity(
                        nombre = prodNombre.trim(),
                        categoria = prodCategoria,
                        precio = precioNum!!,
                        stock = stockNum!!,
                        codigoQr = "QR-${prodNombre.take(3).uppercase()}-${(10..99).random()}",
                        imagenUri = if (imagenCapturada) "FOTO_CAPTURADA_CAMARA" else ""
                    )
                )
                prodNombre = ""; prodPrecio = ""; prodStock = ""
                onExito()
            }
        }
    }

    fun obtenerCarritoActual(): Flow<List<CarritoEntity>> {
        val correo = usuarioActual?.correo ?: ""
        return dao.obtenerCarrito(correo)
    }

    fun agregarAlCarrito(producto: ProductoEntity) {
        val correo = usuarioActual?.correo ?: return
        if (producto.stock <= 0) return
        viewModelScope.launch {
            dao.agregarAlCarrito(
                CarritoEntity(
                    clienteCorreo = correo,
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    precioUnitario = producto.precio,
                    cantidad = 1
                )
            )
        }
    }

    fun confirmarPedido(items: List<CarritoEntity>, onConfirmado: () -> Unit) {
        val correo = usuarioActual?.correo ?: return
        if (items.isEmpty()) return
        viewModelScope.launch {
            val subtotal = items.sumOf { it.precioUnitario * it.cantidad }
            val totalConDespacho = subtotal + 2000
            val resumen = items.joinToString(", ") { "${it.cantidad}x ${it.nombreProducto}" }
            val fechaHoy = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            items.forEach { item ->
                val prod = productos.value.find { it.id == item.productoId }
                if (prod != null && prod.stock >= item.cantidad) {
                    dao.actualizarStockPrecio(prod.id, prod.stock - item.cantidad, prod.precio)
                }
            }

            dao.crearPedido(
                PedidoEntity(
                    clienteCorreo = correo,
                    fecha = fechaHoy,
                    detalle = resumen,
                    total = totalConDespacho,
                    estado = "Pendiente de Validación"
                )
            )
            dao.vaciarCarrito(correo)

            NotificationHelper.mostrarNotificacion(
                getApplication(),
                "¡Pedido Confirmado en OYL!",
                "Tu compra por $$totalConDespacho está Pendiente de Validación."
            )
            onConfirmado()
        }
    }

    fun actualizarStockAdmin(producto: ProductoEntity, nuevoStock: Int, nuevoPrecio: Int) {
        viewModelScope.launch {
            dao.actualizarStockPrecio(producto.id, nuevoStock, nuevoPrecio)
        }
    }

    fun avanzarEstadoPedido(pedido: PedidoEntity) {
        val siguiente = when (pedido.estado) {
            "Pendiente de Validación" -> "Confirmado"
            "Confirmado" -> "Preparado"
            "Preparado" -> "Entregado"
            else -> "Entregado"
        }
        viewModelScope.launch {
            dao.cambiarEstadoPedido(pedido.id, siguiente)
            NotificationHelper.mostrarNotificacion(
                getApplication(),
                "Actualización de Pedido #${pedido.id}",
                "El estado cambió a: $siguiente"
            )
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        correoInput = ""
        passInput = ""
    }
}