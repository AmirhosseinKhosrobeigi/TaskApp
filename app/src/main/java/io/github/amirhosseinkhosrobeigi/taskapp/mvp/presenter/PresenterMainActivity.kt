package io.github.amirhosseinkhosrobeigi.taskapp.mvp.presenter

import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.BaseLifecycle
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.OnBindData
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.model.ModelMainActivity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.view.ViewMainActivity

class PresenterMainActivity(
    private val view: ViewMainActivity,
    private val model: ModelMainActivity
) : BaseLifecycle {

    override fun onCreate() {
        setNewTask()
        setDataInitRecycler()
        dataHandler()
        setupEditClick()
    }

    private fun setNewTask() {
        view.showDialog(
            object : OnBindData {
                override fun saveData(taskEntity: TaskEntity) {
                    model.setData(taskEntity)
                }
            }
        )
    }

    private fun setDataInitRecycler() {
        view.initRecycler(
            object : OnBindData {
                override fun editData(taskEntity: TaskEntity) {
                    model.editData(taskEntity)
                }

                override fun deleteData(taskEntity: TaskEntity) {
                    model.deletetData(taskEntity)
                }

                override fun restoreData(taskEntity: TaskEntity) {
                    model.restoreTask(taskEntity)
                }

                override fun suspendData(taskEntity: TaskEntity) {
                    model.suspendTask(taskEntity)
                }
            }
        )
    }

    private fun dataHandler() {
        view.setData(
            object : OnBindData {
                override fun requestData(state: Boolean) {
                    model.getTasks(
                        state,
                        object : OnBindData {
                            override fun getData(taskEntity: List<TaskEntity>) {
                                view.showTask(taskEntity)
                            }
                        })
                }

                override fun requestSuspendedData() {
                    model.getSuspendedTasks(
                        object : OnBindData {
                            override fun getSuspendedData(taskEntity: List<TaskEntity>) {
                                view.showSuspendedTasks(taskEntity)
                            }
                        })
                }
            }
        )
    }

    private fun setupEditClick() {
        view.setOnEditClickListener { task ->
            view.showEditDialog(task, object : OnBindData {
                override fun editData(taskEntity: TaskEntity) {
                    model.editData(taskEntity)
                }
            })
        }
    }

    override fun onDestroy() {
        model.closeDatabase()
    }
}