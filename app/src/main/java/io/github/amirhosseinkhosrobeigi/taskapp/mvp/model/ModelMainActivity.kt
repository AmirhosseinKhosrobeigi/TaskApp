package io.github.amirhosseinkhosrobeigi.taskapp.mvp.model

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.amirhosseinkhosrobeigi.taskapp.db.DBHandler
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.OnBindData
import io.github.amirhosseinkhosrobeigi.taskapp.utils.ShamsiCalendarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModelMainActivity(private val activity: AppCompatActivity) {

    private val db = DBHandler.getDatabase(activity)

    fun setData(taskEntity: TaskEntity) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.taskDao().insertTask(taskEntity)
            }
        }
    }

    fun editData(taskEntity: TaskEntity) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.taskDao().updateTasks(taskEntity)
            }
        }
    }

    fun deletetData(taskEntity: TaskEntity) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.taskDao().deleteTasks(taskEntity)
            }
        }
    }

    fun getTasks(state: Boolean, onBindData: OnBindData) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val tasks = db.taskDao().getTasksByColumn(state)
                withContext(Dispatchers.Main) {
                    tasks.collect {
                        onBindData.getData(it)
                    }
                }
            }
        }
    }

    fun getSuspendedTasks(onBindData: OnBindData) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val tasks = db.taskDao().getAllTasks()
                withContext(Dispatchers.Main) {
                    tasks.collect { allTasks ->
                        val suspendedTasks = allTasks.filter { task ->
                            task.suspended || (task.expiryDate?.let { ShamsiCalendarUtils.isDateExpired(it) } ?: false)
                        }
                        onBindData.getSuspendedData(suspendedTasks)
                    }
                }
            }
        }
    }

    fun setTaskSuspended(taskId: Int, suspended: Boolean) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.taskDao().setTaskSuspended(taskId, suspended)
            }
        }
    }

    fun restoreTask(taskEntity: TaskEntity) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.taskDao().setTaskSuspended(taskEntity.id, false)
            }
        }
    }

    fun suspendTask(taskEntity: TaskEntity) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.taskDao().setTaskSuspended(taskEntity.id, true)
            }
        }
    }

    fun closeDatabase() {
        db.close()
    }
}