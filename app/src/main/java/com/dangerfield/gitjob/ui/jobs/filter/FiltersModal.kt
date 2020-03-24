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
        return inflater.inflate(R.layout.modal_jobs_filter, container, false)
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
            when {
                btn_full_time.isSelected -> mCallBack?.onSetFilter(Filter.FULL_TIME)
                btn_part_time.isSelected -> mCallBack?.onSetFilter(Filter.PART_TIME)
                else ->  mCallBack?.onSetFilter(null)
            }
            this.dismiss()
        }

        btn_cancel.setOnClickListener { this.dismiss() }
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
