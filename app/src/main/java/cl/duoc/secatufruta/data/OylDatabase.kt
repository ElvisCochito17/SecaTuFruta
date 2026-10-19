package cl.duoc.secatufruta.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UsuarioEntity::class, ProductoEntity::class, CarritoEntity::class, PedidoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OylDatabase : RoomDatabase() {
    abstract fun dao(): OylDao

    companion object {
        @Volatile
        private var INSTANCE: OylDatabase? = null

        fun getDatabase(context: Context): OylDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OylDatabase::class.java,
                    "secatufruta_oyl_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}