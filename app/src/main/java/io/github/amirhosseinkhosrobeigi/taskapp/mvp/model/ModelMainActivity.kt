package io.github.amirhosseinkhosrobeigi.taskapp.mvp.model

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.amirhosseinkhosrobeigi.taskapp.db.DBHandler
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.OnBindData
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

    fun closeDatabase() {
        db.close()
    }
}