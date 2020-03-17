package com.dangerfield.gitjob.ui.jobs

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.util.setHideKeyBoardOnPressAway
import kotlinx.android.synthetic.main.fragment_location_change.*
import android.widget.TextView
import androidx.lifecycle.Observer
import com.dangerfield.gitjob.util.goneIf
import com.dangerfield.gitjob.util.onTextChanged
import org.koin.android.viewmodel.ext.android.viewModel

class LocationChangeFragment : DialogFragment() {

    lateinit var mFilterSetter: FilterSetter
    val locationChangeViewModel: LocationChangeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_location_change, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
            dialog?.window?.setWindowAnimations(R.style.AppTheme_ModalAnimation)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        locationChangeViewModel.userState.observe(viewLifecycleOwner, Observer {
            ib_clear_text.goneIf(it != UserState.TYPING)
            when(it) {
                UserState.TYPING -> enterTypingState()
                UserState.SEARCHED -> enterSearchedState()
                UserState.INITAL -> enterInitialState()
            }
        })

        locationChangeViewModel.userState.value = UserState.INITAL

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ib_close.setOnClickListener { this.dismiss() }
        input_location.setHideKeyBoardOnPressAway()

        input_location.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionID: Int, event: KeyEvent?): Boolean {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    locationChangeViewModel.userState.value =
                        UserState.SEARCHED
                    return true
                }
                return false
            }

        })

        input_location.onTextChanged {
            if(it.isNotEmpty()) locationChangeViewModel.userState.value =
                UserState.TYPING
            else locationChangeViewModel.userState.value =
                UserState.INITAL
        }
        btn_done.setOnClickListener {
            mFilterSetter.onSetCity(input_location.text.toString().trim())
            this.dismiss()
        }
        ib_clear_text.setOnClickListener { (input_location as TextView).text = "" }
    }

    fun enterInitialState() {
        ib_close.background = resources.getDrawable(R.drawable.ic_close, null)
        ib_close.setOnClickListener {
            this.dismiss()
        }

    }

    fun enterSearchedState() {

    }

    fun enterTypingState() {
        ib_close.setOnClickListener {
            locationChangeViewModel.userState.value =
                UserState.INITAL
        }

        ib_close.background = resources.getDrawable(R.drawable.ic_arrow_back, null)

    }

    companion object {
        fun newInstance(filterSetter: FilterSetter): LocationChangeFragment {
            val fragment = LocationChangeFragment()
            fragment.mFilterSetter = filterSetter
            return fragment
        }
    }

}
