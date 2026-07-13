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
        const val DATABASE_VERSION = 2

        const val TASK_TABLE = "taskTable"

        private var INSTANCE: DBHandler? = null

        private val MIGRATION_1_2 = object : Migration(DATABASE_VERSION - 1, DATABASE_VERSION) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE $TASK_TABLE
                    ADD COLUMN priority TEXT NOT NULL DEFAULT 'کم'
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): DBHandler {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    DBHandler::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
            }

            return INSTANCE!!
        }

    }
}