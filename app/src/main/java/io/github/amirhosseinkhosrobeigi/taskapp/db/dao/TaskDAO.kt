package io.github.amirhosseinkhosrobeigi.taskapp.db.dao

import androidx.room.*
import io.github.amirhosseinkhosrobeigi.taskapp.db.DBHandler
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {

    @Insert
    fun insertTask(vararg task: TaskEntity)

    @get:Query("SELECT * FROM ${DBHandler.TASK_TABLE}")
    val getTasks: Flow<List<TaskEntity>>

    @Query("SELECT * FROM ${DBHandler.TASK_TABLE} WHERE state = :type")
    fun getTasksByColumn(type: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ${DBHandler.TASK_TABLE} WHERE suspended = 1")
    fun getSuspendedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ${DBHandler.TASK_TABLE}")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Update
    fun updateTasks(vararg tasks: TaskEntity): Int

    @Delete
    fun deleteTasks(vararg tasks: TaskEntity): Int

    @Query("DELETE FROM ${DBHandler.TASK_TABLE}")
    fun deleteAllTasks()

    @Query("UPDATE ${DBHandler.TASK_TABLE} SET suspended = :suspended WHERE id = :taskId")
    fun setTaskSuspended(taskId: Int, suspended: Boolean)
}