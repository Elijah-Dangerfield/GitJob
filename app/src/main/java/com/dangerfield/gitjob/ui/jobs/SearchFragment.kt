package com.dangerfield.gitjob.ui.jobs


import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.util.goneIf
import com.dangerfield.gitjob.util.hideKeyBoardOnPressAway
import com.dangerfield.gitjob.util.onTextChanged
import com.dangerfield.gitjob.util.openKeyboard
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment(R.layout.fragment_search) {

    lateinit var mFilterSetter: FilterSetter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ib_back.setOnClickListener { parentFragmentManager.popBackStackImmediate()}

        etv_search.openKeyboard()
        etv_search.hideKeyBoardOnPressAway()

        etv_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionID: Int, event: KeyEvent?): Boolean {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    mFilterSetter.onSetSearchTerm(etv_search.text.toString())
                    parentFragmentManager.popBackStackImmediate()
                    return true
                }
                return false
            }

        })

        etv_search.onTextChanged { ib_clear_text.goneIf(it.isEmpty()) }
        ib_clear_text.setOnClickListener { (etv_search as TextView).text = "" }
    }

    companion object {
        fun newInstance(filterSetter: FilterSetter): SearchFragment {
            val fragment = SearchFragment()
            fragment.mFilterSetter = filterSetter
            return fragment
        }
    }
}
