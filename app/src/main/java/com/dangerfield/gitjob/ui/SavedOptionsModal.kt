package com.dangerfield.gitjob.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.SavedJob
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.modal_saved_options.*

class SavedOptionsModal : BottomSheetDialogFragment() {

    lateinit var optionsPresenter: OptionsPresenter
    lateinit var currentSavedJob: SavedJob


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.modal_saved_options, container, false)
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_cancel.setOnClickListener { this.dismiss() }
        tv_delete.setOnClickListener {
            optionsPresenter.onDelete(currentSavedJob)
            this.dismiss()
        }
        tv_share.setOnClickListener { optionsPresenter.onShare(currentSavedJob) }

    }

    companion object {
        fun newInstance(optionsPresenter: OptionsPresenter): SavedOptionsModal {
            val fragment = SavedOptionsModal()
            fragment.optionsPresenter = optionsPresenter
            return fragment
        }
    }

}