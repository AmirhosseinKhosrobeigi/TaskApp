package io.github.amirhosseinkhosrobeigi.taskapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.github.amirhosseinkhosrobeigi.taskapp.R
import io.github.amirhosseinkhosrobeigi.taskapp.databinding.RecyclerItemBinding
import io.github.amirhosseinkhosrobeigi.taskapp.databinding.RecyclerItemSuspendedBinding
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.OnBindData

class RecyclerTaskAdapter(
    private val tasks: ArrayList<TaskEntity>,
    private val onBindData: OnBindData,
    private val onItemClick: (TaskEntity) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_SUSPENDED = 1
    }

    private var isSuspendedMode = false

    inner class TaskViewHolder(
        private val binding: RecyclerItemBinding
    ) : ViewHolder(binding.root) {

        fun setData(data: TaskEntity) {
            binding.txtTitle.text = data.title
            binding.txtPriority.text = data.priority
            binding.checkBox.isChecked = data.state

            val priorityColor = when (data.priority) {
                "زیاد" -> R.color.priority_high
                "متوسط" -> R.color.priority_medium
                "کم" -> R.color.priority_low
                else -> R.color.accent_purple
            }
            binding.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, priorityColor)
            )

            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked)
                    onBindData.editData(
                        TaskEntity(
                            data.id,
                            data.title,
                            true,
                            data.priority,
                            data.expiryDate
                        )
                    )
                else
                    onBindData.editData(
                        TaskEntity(
                            data.id,
                            data.title,
                            false,
                            data.priority,
                            data.expiryDate
                        )
                    )
            }

            binding.imgDelete.setOnClickListener {
                onBindData.deleteData((data))
            }

            binding.imgSuspend.setOnClickListener {
                onBindData.suspendData(data)
            }

            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }

    }

    inner class SuspendedTaskViewHolder(
        private val binding: RecyclerItemSuspendedBinding
    ) : ViewHolder(binding.root) {

        fun setData(data: TaskEntity, onBindData: OnBindData) {
            binding.txtTaskTitleSuspended.text = data.title
            binding.txtPrioritySuspended.text = data.priority

            val priorityColor = when (data.priority) {
                "زیاد" -> R.color.priority_high
                "متوسط" -> R.color.priority_medium
                "کم" -> R.color.priority_low
                else -> R.color.text_primary_dark
            }
            binding.txtPrioritySuspended.setTextColor(
                ContextCompat.getColor(binding.root.context, priorityColor)
            )

            // Display expiry date if available
            binding.txtExpiryDate.text = if (!data.expiryDate.isNullOrEmpty()) {
                "انقضا: ${data.expiryDate}"
            } else {
                "انقضا: تاریخ نامشخص"
            }

            if (data.suspended) {
                binding.txtSuspendedLabel.text = "معلق (دستی)"
            } else {
                binding.txtSuspendedLabel.text = "منقضی شده"
                binding.txtSuspendedLabel.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.status_warning)
                )
            }

            binding.btnRestore.setOnClickListener {
                onBindData.restoreData(data)
            }

            binding.btnDeleteSuspended.setOnClickListener {
                onBindData.deleteData(data)
            }

            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (isSuspendedMode) VIEW_TYPE_SUSPENDED else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SUSPENDED) {
            SuspendedTaskViewHolder(
                RecyclerItemSuspendedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            TaskViewHolder(
                RecyclerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = tasks[position]
        if (isSuspendedMode) {
            (holder as SuspendedTaskViewHolder).setData(task, onBindData)
        } else {
            (holder as TaskViewHolder).setData(task)
        }
    }

    fun dataUpdate(newList: ArrayList<TaskEntity>) {
        isSuspendedMode = false
        val diffCallback = RecyclerDiffUtils(tasks, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tasks.clear()
        tasks.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    fun dataUpdateSuspended(newList: ArrayList<TaskEntity>) {
        isSuspendedMode = true
        val diffCallback = RecyclerDiffUtils(tasks, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tasks.clear()
        tasks.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }
}
