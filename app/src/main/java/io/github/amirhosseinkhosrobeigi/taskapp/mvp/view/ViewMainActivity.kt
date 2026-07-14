package io.github.amirhosseinkhosrobeigi.taskapp.mvp.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private var dateSelectionCallback: ((String) -> Unit)? = null

    fun showTask(tasks: List<TaskEntity>) {
        val data = arrayListOf<TaskEntity>()
        tasks.forEach { data.add(it) }
        adapter.dataUpdate(data)
    }

    fun showSuspendedTasks(tasks: List<TaskEntity>) {
        val data = arrayListOf<TaskEntity>()
        tasks.forEach { data.add(it) }
        adapter.dataUpdateSuspended(data)
    }

    fun setData(onBindData: OnBindData) {
        onBindData.requestData(false)

        binding.rdbTrue.setOnClickListener {
            onBindData.requestData(true)
        }

        binding.rdbFalse.setOnClickListener {
            onBindData.requestData(false)
        }

        binding.rdbSuspended.setOnClickListener {
            //onBindData.requestSuspendedData()
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

        view.edtExpiryDate.setOnClickListener {
            showShamsiDatePicker { selectedDate ->
                view.edtExpiryDate.setText(selectedDate)
            }
        }

        task?.let {
            view.edtTask.setText(it.title)
            when (it.priority) {
                "زیاد" -> view.radioPriority.check(R.id.radioHigh)
                "متوسط" -> view.radioPriority.check(R.id.radioMedium)
                "کم" -> view.radioPriority.check(R.id.radioLow)
            }
            if (!it.expiryDate.isNullOrEmpty()) {
                view.edtExpiryDate.setText(it.expiryDate)
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

            val expiryDate = view.edtExpiryDate.text.toString()

            if (text.isNotEmpty()) {
                if (task == null) {
                    onBindData.saveData(
                        TaskEntity(
                            title = text,
                            state = false,
                            priority = selectedPriority,
                            expiryDate = if (expiryDate.isNotEmpty()) expiryDate else null
                        )
                    )
                    Toast.makeText(context, "وظیفه شما با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show()
                } else {
                    onBindData.editData(
                        TaskEntity(
                            id = task.id,
                            title = text,
                            state = task.state,
                            priority = selectedPriority,
                            expiryDate = if (expiryDate.isNotEmpty()) expiryDate else null
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

    private fun showShamsiDatePicker(onDateSelected: (String) -> Unit) {
        // Persian months
        val persianMonths = listOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )

        // Current Shamsi date (approximate - you may want to calculate this properly)
        val currentYear = 1403
        val currentMonth = 5  // Ordibehesht
        val currentDay = 15

        // Create year selection dialog
        val years = (1390..1420).toList().map { it.toString() }
        val yearAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, years)

        val monthAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, persianMonths)

        // Create a custom view for date selection
        val datePickerView = LayoutInflater.from(context).inflate(R.layout.dialog_shamsi_date_picker, null)

        val yearSpinner = datePickerView.findViewById<android.widget.Spinner>(R.id.spinnerYear)
        val monthSpinner = datePickerView.findViewById<android.widget.Spinner>(R.id.spinnerMonth)
        val daySpinner = datePickerView.findViewById<android.widget.Spinner>(R.id.spinnerDay)

        yearSpinner.adapter = yearAdapter
        monthSpinner.adapter = monthAdapter

        yearSpinner.setSelection(years.indexOf(currentYear.toString()))
        monthSpinner.setSelection(currentMonth - 1)

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateDaySpinner(daySpinner, yearSpinner.selectedItem.toString().toInt(), position + 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        updateDaySpinner(daySpinner, currentYear, currentMonth)
        daySpinner.setSelection(currentDay - 1)

        AlertDialog.Builder(context)
            .setTitle("انتخاب تاریخ شمسی")
            .setView(datePickerView)
            .setPositiveButton("تایید") { _, _ ->
                val year = yearSpinner.selectedItem.toString()
                val month = monthSpinner.selectedItemPosition + 1
                val day = daySpinner.selectedItemPosition + 1
                val dateString = "$year/$month/$day"
                onDateSelected(dateString)
            }
            .setNegativeButton("انصراف", null)
            .show()
    }

    private fun updateDaySpinner(daySpinner: android.widget.Spinner, year: Int, month: Int) {
        val daysInMonth = when (month) {
            12 -> if (isPersianLeapYear(year)) 30 else 29
            in 1..6 -> 31
            else -> 30
        }
        val days = (1..daysInMonth).map { it.toString() }
        val dayAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, days)
        daySpinner.adapter = dayAdapter
    }

    private fun isPersianLeapYear(year: Int): Boolean {
        return ((year % 33) % 4) == 1
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
