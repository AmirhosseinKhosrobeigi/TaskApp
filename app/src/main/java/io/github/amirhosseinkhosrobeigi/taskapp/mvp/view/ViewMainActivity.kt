package io.github.amirhosseinkhosrobeigi.taskapp.mvp.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.amirhosseinkhosrobeigi.taskapp.R
import io.github.amirhosseinkhosrobeigi.taskapp.adapter.RecyclerTaskAdapter
import io.github.amirhosseinkhosrobeigi.taskapp.databinding.ActivityMainBinding
import io.github.amirhosseinkhosrobeigi.taskapp.databinding.CustomDialogBinding
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.OnBindData

class ViewMainActivity(
    private val context: Context
) {

    val binding = ActivityMainBinding.inflate(LayoutInflater.from(context))

    private lateinit var adapter: RecyclerTaskAdapter
    private var onEditClick: ((TaskEntity) -> Unit)? = null

    fun showTask(tasks: List<TaskEntity>) {
        val data = arrayListOf<TaskEntity>()
        tasks.forEach { data.add(it) }
        adapter.dataUpdate(data)
    }

    fun setData(onBindData: OnBindData) {
        onBindData.requestData(false)

        binding.rdbTrue.setOnClickListener {
            onBindData.requestData(true)
        }

        binding.rdbFalse.setOnClickListener {
            onBindData.requestData(false)
        }
    }

    fun setOnEditClickListener(listener: (TaskEntity) -> Unit) {
        this.onEditClick = listener
    }

    fun showDialog(onBindData: OnBindData) {
        binding.fab.setOnClickListener {
            showAddEditDialog(null, onBindData)
        }
    }

    fun showEditDialog(task: TaskEntity, onBindData: OnBindData) {
        showAddEditDialog(task, onBindData)
    }

    private fun showAddEditDialog(task: TaskEntity?, onBindData: OnBindData) {
        val view = CustomDialogBinding.inflate(LayoutInflater.from(context))

        val dialog = Dialog(context)
        dialog.setContentView(view.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.let { window ->
            val params = window.attributes
            params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
            params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = params
        }
        
        dialog.show()

        task?.let {
            view.edtTask.setText(it.title)
            when (it.priority) {
                "زیاد" -> view.radioPriority.check(R.id.radioHigh)
                "متوسط" -> view.radioPriority.check(R.id.radioMedium)
                "کم" -> view.radioPriority.check(R.id.radioLow)
            }
        }

        view.btnCancel.setOnClickListener { dialog.dismiss() }
        view.btnSave.setOnClickListener {
            val text = view.edtTask.text.toString()

            val selectedPriority = when (view.radioPriority.checkedRadioButtonId) {
                R.id.radioHigh -> "زیاد"
                R.id.radioMedium -> "متوسط"
                R.id.radioLow -> "کم"
                else -> "متوسط"
            }

            if (text.isNotEmpty()) {
                if (task == null) {
                    onBindData.saveData(
                        TaskEntity(
                            title = text,
                            state = false,
                            priority = selectedPriority
                        )
                    )
                    Toast.makeText(context, "وظیفه شما با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show()
                } else {
                    onBindData.editData(
                        TaskEntity(
                            id = task.id,
                            title = text,
                            state = task.state,
                            priority = selectedPriority
                        )
                    )
                    Toast.makeText(context, "وظیفه با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            } else {
                Toast.makeText(context, "لطفا وظیفه را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initRecycler(onBindData: OnBindData) {
        adapter = RecyclerTaskAdapter(
            arrayListOf<TaskEntity>(),
            onBindData,
            onItemClick = { task ->
                onEditClick?.invoke(task)
            }
        )
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
}