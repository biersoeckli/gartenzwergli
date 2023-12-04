package ch.ost.gartenzwergli.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.FragmentNewEventDialogBinding
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [NewEventDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewEventDialog : DialogFragment() {

    private var _toolbar: Toolbar? = null
    private var _binding: FragmentNewEventDialogBinding? = null

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
        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth ->
                // set day of month , month and year value in the edit text
                binding.newEventCalendarText.setText("" + dayOfMonth + "." + (month + 1) + "." + year)
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