package cl.duoc.secatufruta.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val correo: String,
    val nombre: String,
    val password: String,
    val telefono: String,
    val direccion: String,
    val rol: String
)

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val formato: String = "250 gr",
    val codigoQr: String,
    val imagenUri: String = ""
)

@Entity(tableName = "carrito")
data class CarritoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteCorreo: String,
    val productoId: Int,
    val nombreProducto: String,
    val precioUnitario: Int,
    val cantidad: Int
)

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteCorreo: String,
    val fecha: String,
    val detalle: String,
    val total: Int,
    val estado: String
)