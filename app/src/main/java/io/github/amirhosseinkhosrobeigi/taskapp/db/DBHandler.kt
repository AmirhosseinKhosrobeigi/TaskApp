package io.github.amirhosseinkhosrobeigi.taskapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.amirhosseinkhosrobeigi.taskapp.db.dao.TaskDAO
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = DBHandler.DATABASE_VERSION
)
abstract class DBHandler : RoomDatabase() {

    abstract fun taskDao(): TaskDAO

    companion object {

        private const val DATABASE_NAME = "task_database"
        const val DATABASE_VERSION = 5

        const val TASK_TABLE = "taskTable"

        private var INSTANCE: DBHandler? = null


        fun getDatabase(context: Context): DBHandler {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    DBHandler::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .build()
            }

            return INSTANCE!!
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE taskTable ADD COLUMN expiryDate TEXT")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE taskTable ADD COLUMN suspended INTEGER NOT NULL DEFAULT 0")
            }
        }

    }
}