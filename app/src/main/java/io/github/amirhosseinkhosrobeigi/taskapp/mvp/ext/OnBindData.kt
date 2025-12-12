package io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext

import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity

interface OnBindData {

    fun saveData(taskEntity: TaskEntity) {}

    fun editData(taskEntity: TaskEntity) {}

    fun deleteData(taskEntity: TaskEntity) {}

    fun getData(taskEntity: List<TaskEntity>) {}

    fun requestData(state: Boolean) {}

}