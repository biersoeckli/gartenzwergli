package ch.ost.gartenzwergli.ui.home

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import ch.ost.gartenzwergli.R

/**
 * A simple [Fragment] subclass.
 * Use the [NewEventDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewEventDialog : DialogFragment() {

    private var _toolbar: Toolbar? = null
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
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_new_event_dialog, container, false)
        toolbar = root.findViewById(R.id.toolbar)

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
}