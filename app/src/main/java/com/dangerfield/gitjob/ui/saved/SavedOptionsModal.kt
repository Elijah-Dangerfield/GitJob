package com.dangerfield.gitjob.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.dangerfield.gitjob.R
import com.dangerfield.gitjob.model.SavedJob
import com.dangerfield.gitjob.util.goneIf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.modal_saved_options.*

enum class ModalOption {
    DELETE, SHARE
}

class SavedOptionsModal : BottomSheetDialogFragment() {

    lateinit var optionsHandler: OptionsHandler
    lateinit var currentSavedJob: SavedJob
    var mOptions: List<ModalOption>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_saved_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_cancel.setOnClickListener { this.dismiss() }
        tv_delete.setOnClickListener {
            optionsHandler.onDelete(currentSavedJob)
            this.dismiss()
        }

        tv_share.setOnClickListener { optionsHandler.onShare(currentSavedJob) }
    }

    override fun onResume() {
        super.onResume()
        tv_share.goneIf(currentSavedJob.url == null)

        if(mOptions != null) {
            applyOptions()
        } else {
            tv_delete.visibility = View.VISIBLE
            tv_share.visibility = View.VISIBLE
        }
    }

    private fun applyOptions() {
        tv_share.goneIf(!mOptions!!.contains(ModalOption.SHARE))
        tv_delete.goneIf(!mOptions!!.contains(ModalOption.DELETE))
    }

    companion object {
        fun newInstance(optionsHandler: OptionsHandler): SavedOptionsModal {
            val fragment = SavedOptionsModal()
            fragment.optionsHandler = optionsHandler
            return fragment
        }
    }

    fun show(fragmentManager: FragmentManager, savedJob: SavedJob, options: List<ModalOption>? = null){
        this.show(fragmentManager, "optionsModal")
        currentSavedJob = savedJob
        mOptions = options
    }
}