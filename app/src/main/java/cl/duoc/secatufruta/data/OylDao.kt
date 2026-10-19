package cl.duoc.secatufruta.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OylDao {
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :pass LIMIT 1")
    suspend fun login(correo: String, pass: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM productos")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE codigoQr = :qr LIMIT 1")
    suspend fun buscarPorQr(qr: String): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarProducto(producto: ProductoEntity)

    @Query("UPDATE productos SET stock = :nuevoStock, precio = :nuevoPrecio WHERE id = :id")
    suspend fun actualizarStockPrecio(id: Int, nuevoStock: Int, nuevoPrecio: Int)

    @Query("SELECT * FROM carrito WHERE clienteCorreo = :correo")
    fun obtenerCarrito(correo: String): Flow<List<CarritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarAlCarrito(item: CarritoEntity)

    @Query("DELETE FROM carrito WHERE id = :id")
    suspend fun eliminarItemCarrito(id: Int)

    @Query("DELETE FROM carrito WHERE clienteCorreo = :correo")
    suspend fun vaciarCarrito(correo: String)

    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun obtenerTodosLosPedidos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE clienteCorreo = :correo ORDER BY id DESC")
    fun obtenerPedidosCliente(correo: String): Flow<List<PedidoEntity>>

    @Insert
    suspend fun crearPedido(pedido: PedidoEntity)

    @Query("UPDATE pedidos SET estado = :nuevoEstado WHERE id = :pedidoId")
    suspend fun cambiarEstadoPedido(pedidoId: Int, nuevoEstado: String)
}