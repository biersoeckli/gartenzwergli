package ch.ost.gartenzwergli.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.FragmentNewEventDialogBinding
import ch.ost.gartenzwergli.model.dbo.DUMMY_CROP_ID
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import ch.ost.gartenzwergli.services.DatabaseService
import java.util.Calendar


/**
 * A simple [Fragment] subclass.
 * Use the [NewEventDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewEventDialog : DialogFragment() {

    private var _toolbar: Toolbar? = null
    private var _binding: FragmentNewEventDialogBinding? = null
    val onSuccessfullySavedAction = MutableLiveData<String>()

    private val binding get() = _binding!!

    private val calendar: Calendar = Calendar.getInstance()
    private val dateFormat = "dd.MM.yyyy"
    private val simpleDateFormat = java.text.SimpleDateFormat(dateFormat, java.util.Locale.GERMANY)

    var toolbar: Toolbar?
        get() = _toolbar
        set(value) {
            _toolbar = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentNewEventDialogBinding.inflate(inflater, container, false)
        val root = binding.root
        toolbar = root.findViewById(R.id.toolbar)

        binding.newEventCalendarText.text = Editable.Factory
            .getInstance().newEditable(simpleDateFormat.format(calendar.time))
        binding.newEventCalendarText.setOnClickListener {
            showDatePickerDialog()
        }

        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.setNavigationOnClickListener(View.OnClickListener {
            dismiss()
        })
        toolbar?.setTitle("Neuer Event")
        toolbar?.inflateMenu(R.menu.new_event_dialog_menu)

        toolbar?.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_new_event -> {

                    val dateString = binding.newEventCalendarText.text.toString()
                    val date = simpleDateFormat.parse(dateString)
                    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.GERMANY)
                        .format(date!!)

                    DatabaseService.getDb().cropEventDao().insertAll(
                        CropEventDbo(
                            title = binding.newEventPlantText.text.toString(),
                            description = binding.newEventPlantDescription.text.toString(),
                            dateTime = dateFormat,
                            plantedTime = dateFormat,
                            cropId = DUMMY_CROP_ID
                        )
                    )
                    onSuccessfullySavedAction.setValue("saved")
                    dismiss()
                    return@OnMenuItemClickListener true
                }
            }
            return@OnMenuItemClickListener false
        })
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = getDialog()

        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.getWindow()?.setLayout(width, height)
            dialog.getWindow()?.setWindowAnimations(R.style.Theme_Gartenzwergli)
        }
    }

    private fun showDatePickerDialog() {
        // get current date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // create date picker dialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth ->
                // show leading zero for single digit days
                var dayOfMonthString = dayOfMonth.toString()
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0${dayOfMonth}"
                }
                var monthString = (month + 1).toString()
                if (month < 10) {
                    monthString = "0${month}"
                }
                binding.newEventCalendarText.setText("${dayOfMonthString}.${monthString}.${year}")
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}