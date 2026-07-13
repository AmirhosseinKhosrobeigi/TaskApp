package io.github.amirhosseinkhosrobeigi.taskapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.github.amirhosseinkhosrobeigi.taskapp.R
import io.github.amirhosseinkhosrobeigi.taskapp.databinding.RecyclerItemBinding
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.OnBindData

class RecyclerTaskAdapter(
    private val tasks: ArrayList<TaskEntity>,
    private val onBindData: OnBindData,
    private val onItemClick: (TaskEntity) -> Unit = {}
) : RecyclerView.Adapter<RecyclerTaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(
        private val binding: RecyclerItemBinding
    ) : ViewHolder(binding.root) {

        fun setData(data: TaskEntity) {
            binding.txtTitle.text = data.title
            binding.txtPriority.text = data.priority
            binding.checkBox.isChecked = data.state

            val priorityColor = when (data.priority) {
                "زیاد" -> R.color.accent_red
                "متوسط" -> R.color.accent_blue
                "کم" -> R.color.accent_green
                else -> R.color.accent_purple
            }
            binding.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, priorityColor)
            )

            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked)
                    onBindData.editData(TaskEntity(data.id, data.title, true, data.priority))
                else
                    onBindData.editData(TaskEntity(data.id, data.title, false, data.priority))
            }

            binding.imgDelete.setOnClickListener {
                onBindData.deleteData((data))
            }

            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TaskViewHolder(
            RecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.setData(tasks[position])
    }

    fun dataUpdate(newList: ArrayList<TaskEntity>) {
        val diffCallback = RecyclerDiffUtils(tasks, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tasks.clear()
        tasks.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }
}