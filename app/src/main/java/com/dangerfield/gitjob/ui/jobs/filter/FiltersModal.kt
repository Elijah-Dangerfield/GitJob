package com.dangerfield.gitjob.ui.jobs.filter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dangerfield.gitjob.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.modal_jobs_filter.*

class FiltersModal : BottomSheetDialogFragment() {

    var mCallBack: FilterSetter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.modal_jobs_filter, container, false)
//        view.clipToOutline = true
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_full_time.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        btn_part_time.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        btn_set_filters.setOnClickListener {
            //if both options are selected there is no filters, otherwise give
            //back the string used as the filter in the api call
            if(btn_full_time.isSelected && btn_full_time.isSelected) {
                mCallBack?.onSetFilter()
            }

            if(btn_full_time.isSelected) mCallBack?.onSetFilter("full_time")
        }
    }

    companion object {
        fun newInstance(callback: FilterSetter): FiltersModal {
            val fragment = FiltersModal()
            fragment.mCallBack = callback
            return fragment
        }
    }

}
