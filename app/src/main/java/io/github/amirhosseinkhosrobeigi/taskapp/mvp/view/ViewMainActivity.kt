package io.github.amirhosseinkhosrobeigi.taskapp.mvp.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.amirhosseinkhosrobeigi.taskapp.R
import io.github.amirhosseinkhosrobeigi.taskapp.adapter.CalendarAdapter
import io.github.amirhosseinkhosrobeigi.taskapp.adapter.RecyclerTaskAdapter
import io.github.amirhosseinkhosrobeigi.taskapp.databinding.ActivityMainBinding
import io.github.amirhosseinkhosrobeigi.taskapp.databinding.CustomDialogBinding
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.TaskEntity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.ext.OnBindData
import io.github.amirhosseinkhosrobeigi.taskapp.utils.ShamsiCalendarUtils

class ViewMainActivity(
    private val context: Context
) {
    val binding = ActivityMainBinding.inflate(LayoutInflater.from(context))
    private lateinit var adapter: RecyclerTaskAdapter
    private var onEditClick: ((TaskEntity) -> Unit)? = null

    fun showTask(tasks: List<TaskEntity>) {
        val data = arrayListOf<TaskEntity>()
        tasks.forEach {
            data.add(it)
        }
        adapter.dataUpdate(data)
    }

    fun showSuspendedTasks(tasks: List<TaskEntity>) {
        val data = arrayListOf<TaskEntity>()
        tasks.forEach {
            data.add(it)
        }
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
            // onBindData.requestSuspendedData()
        }
    }

    fun setOnEditClickListener(listener: (TaskEntity) -> Unit) {
        this.onEditClick = listener
    }

    fun showDialog(onBindData: OnBindData) {
        binding.fab.setOnClickListener {
            showAddEditDialog(
                task = null,
                onBindData = onBindData
            )
        }
    }

    fun showEditDialog(task: TaskEntity, onBindData: OnBindData) {
        showAddEditDialog(
            task = task,
            onBindData = onBindData
        )
    }

    private fun showAddEditDialog(
        task: TaskEntity?,
        onBindData: OnBindData
    ) {
        val view = CustomDialogBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)

        dialog.setContentView(view.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.let { window ->
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = params
        }

        dialog.show()

        view.edtExpiryDate.setOnClickListener {
            showShamsiDatePicker(
                currentDate = view.edtExpiryDate.text.toString(),
                onDateSelected = { selectedDate ->
                    view.edtExpiryDate.setText(selectedDate)
                }
            )
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

        view.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

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
                            expiryDate = if (expiryDate.isNotEmpty()) {
                                expiryDate
                            } else {
                                null
                            }
                        )
                    )

                    Toast.makeText(
                        context,
                        "وظیفه شما با موفقیت ایجاد شد",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onBindData.editData(
                        TaskEntity(
                            id = task.id,
                            title = text,
                            state = task.state,
                            priority = selectedPriority,
                            expiryDate = if (expiryDate.isNotEmpty()) {
                                expiryDate
                            } else {
                                null
                            }
                        )
                    )

                    Toast.makeText(
                        context,
                        "وظیفه با موفقیت ویرایش شد",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                dialog.dismiss()
            } else {
                Toast.makeText(
                    context,
                    "لطفا وظیفه را وارد کنید",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showShamsiDatePicker(
        currentDate: String,
        onDateSelected: (String) -> Unit
    ) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("انتخاب تاریخ شمسی")
            .setPositiveButton("تایید", null)
            .setNegativeButton("انصراف", null)
            .create()

        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_shamsi_material_calendar, null)

        dialog.setView(view)

        val calendarGrid = view.findViewById<RecyclerView>(R.id.calendarGrid)
        val txtMonthYear = view.findViewById<TextView>(R.id.txtMonthYear)
        val btnPrevMonth = view.findViewById<View>(R.id.btnPrevMonth)
        val btnNextMonth = view.findViewById<View>(R.id.btnNextMonth)

        val today = ShamsiCalendarUtils.getCurrentShamsiDate()

        var selectedYear = today.year
        var selectedMonth = today.month
        var selectedDay: Int? = null
        var initialSelectedDay: Int? = null

        if (currentDate.isNotEmpty()) {
            try {
                val parsedDate = ShamsiCalendarUtils.toEnglishNumbers(currentDate)
                val dateParts = parsedDate.split("/")
                if (dateParts.size == 3) {
                    selectedYear = dateParts[0].toInt()
                    selectedMonth = dateParts[1].toInt()
                    selectedDay = dateParts[2].toInt()
                    initialSelectedDay = selectedDay
                }
            } catch (e: Exception) {
                selectedDay = null
                initialSelectedDay = null
            }
        }

        val calendarAdapter = CalendarAdapter { day ->
            selectedDay = day
        }

        calendarGrid.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }

        fun updateMonthTitle() {
            val monthName = ShamsiCalendarUtils.getPersianMonthName(selectedMonth)

            txtMonthYear.text = "$monthName ${
                ShamsiCalendarUtils.toPersianNumbers(
                    selectedYear.toString()
                )
            }"
        }

        fun updateCalendar() {
            val days = ShamsiCalendarUtils.generateCalendarDays(
                selectedYear,
                selectedMonth,
                initialSelectedDay
            )

            calendarAdapter.setData(days)
        }

        btnPrevMonth.setOnClickListener {
            selectedMonth--

            if (selectedMonth < 1) {
                selectedMonth = 12
                selectedYear--
            }

            selectedDay = null
            initialSelectedDay = null
            updateMonthTitle()
            updateCalendar()
        }

        btnNextMonth.setOnClickListener {
            selectedMonth++

            if (selectedMonth > 12) {
                selectedMonth = 1
                selectedYear++
            }

            selectedDay = null
            initialSelectedDay = null
            updateMonthTitle()
            updateCalendar()
        }

        updateMonthTitle()
        updateCalendar()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (selectedDay != null) {
                val selectedDate = "$selectedYear/$selectedMonth/$selectedDay"

                onDateSelected(
                    ShamsiCalendarUtils.toPersianNumbers(selectedDate)
                )

                dialog.dismiss()
            } else if (initialSelectedDay != null) {
                val selectedDate = "$selectedYear/$selectedMonth/$initialSelectedDay"
                onDateSelected(
                    ShamsiCalendarUtils.toPersianNumbers(selectedDate)
                )
                dialog.dismiss()
            } else {
                Toast.makeText(
                    context,
                    "لطفاً یک تاریخ انتخاب کنید",
                    Toast.LENGTH_SHORT
                ).show()
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

        binding.recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        binding.recyclerView.adapter = adapter
    }
}