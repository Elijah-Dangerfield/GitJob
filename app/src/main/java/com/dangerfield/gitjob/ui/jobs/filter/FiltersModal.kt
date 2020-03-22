package com.dangerfield.gitjob.ui.jobs.filter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.ui.jobs.feed.Filter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.modal_jobs_filter.*

class FiltersModal : BottomSheetDialogFragment() {

    var mCallBack: FilterSetter? = null
    var mCurrentFilter: Filter? = null

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
            if(it.isSelected) btn_part_time.isSelected = false
        }

        btn_part_time.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) btn_full_time.isSelected = false
        }

        btn_set_filters.setOnClickListener {
            //if both options are selected there is no filter, otherwise give
            //back the string used as the filter in the api call
            when {
                btn_full_time.isSelected -> mCallBack?.onSetFilter(Filter.FULL_TIME)
                btn_part_time.isSelected -> mCallBack?.onSetFilter(Filter.PART_TIME)
                else ->  mCallBack?.onSetFilter(null)

            }
            this.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        btn_part_time.isSelected = mCurrentFilter == Filter.PART_TIME
        btn_full_time.isSelected = mCurrentFilter == Filter.FULL_TIME
    }

    companion object {
        fun newInstance(callback: FilterSetter, currentFilter: Filter?): FiltersModal {
            val fragment = FiltersModal()
            fragment.mCurrentFilter  = currentFilter
            fragment.mCallBack = callback
            return fragment
        }
    }
}
