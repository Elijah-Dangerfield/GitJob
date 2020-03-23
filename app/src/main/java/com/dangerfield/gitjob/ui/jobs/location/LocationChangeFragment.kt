package com.dangerfield.gitjob.ui.jobs.location

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.gitjob.model.AddedLocation
import com.dangerfield.gitjob.ui.jobs.filter.FilterSetter
import com.dangerfield.gitjob.ui.jobs.search.AutoCompleteAdapter
import com.dangerfield.gitjob.util.goneIf
import com.dangerfield.gitjob.util.hideKeyboard
import com.dangerfield.gitjob.util.onTextChanged
import org.koin.android.viewmodel.ext.android.viewModel
import org.w3c.dom.Text

class LocationChangeFragment : DialogFragment() {

    lateinit var mFilterSetter: FilterSetter
    var currentDeterminedLocation: String? = null
    var currentSelectedLocation: String? = null
    private val allLocations = "All Locations"

    private val locationChangeViewModel: LocationChangeViewModel by viewModel()

    private val autoCompleteTerms: ArrayList<String> by lazy {
        ArrayList<String>(resources.getStringArray(R.array.location_auto_complete_list).asList())
    }

    private val addedLocationsAdapter: AddedLocationsAdapter by lazy {
        AddedLocationsAdapter(
            context!!,
            locationChangeViewModel,
            currentSelectedLocation = if(currentSelectedLocation.isNullOrEmpty()) null else AddedLocation(currentSelectedLocation!!),
            currentDeterminedLocation = if(currentDeterminedLocation.isNullOrEmpty()) null else AddedLocation((currentDeterminedLocation!!))
        ) { term -> onSelectLocation(term)}
    }

    private val autoCompleteAdapter: AutoCompleteAdapter by lazy {
        AutoCompleteAdapter(
            context!!,
            emptyResultsList
        ) { term ->
            val location =
            if(term.contains("Search")){
                term.substringAfter("\"").substringBeforeLast("\"").toString()
            }else {
                term
            }
           searchFor(location)
        }
    }

    private fun searchFor(term: String) {
        val location =
            if(term.isNullOrEmpty()) AddedLocation(allLocations)
            else AddedLocation(term)
        if(!addedLocationsAdapter.searches.contains(location)){
            addedLocationsAdapter.searches.add(location)
        }
        onSelectLocation(location.location)
    }

    private val emptyResultsList: (() -> List<String>) =  {
         listOf("Search for \"${locationChangeViewModel.searchTerm.value}\"")
    }

    private fun onSelectLocation(location: String) {
        locationChangeViewModel.userState.value = UserState.LOCATION_CHOSEN
        addedLocationsAdapter.selectedLocation = AddedLocation(location)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_location_change, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()

    }

    override fun onResume() {
        super.onResume()
        enterInitialState()
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

    private fun setupObservers() {
        observeUserState()
        observeSearchedLocations()
    }

    private fun observeSearchedLocations() {
        locationChangeViewModel.getSearchedLocations().observe(viewLifecycleOwner, Observer {
            addedLocationsAdapter.searches = it as ArrayList<AddedLocation>
            it.forEach {searched ->
                if(!autoCompleteTerms.contains(searched.location.toLowerCase().trim())) { autoCompleteTerms.add(searched.location.toLowerCase().trim())
                }
            }
        })
    }

    private fun observeUserState() {
        locationChangeViewModel.userState.observe(viewLifecycleOwner, Observer {
            ib_clear_text.goneIf(it != UserState.TYPING)
            btn_done.goneIf(it != UserState.LOCATION_CHOSEN)
            when(it) {
                UserState.TYPING -> enterTypingState()
                UserState.SEARCHED -> enterSearchedState()
                UserState.INITAL -> enterInitialState()
                UserState.LOCATION_CHOSEN -> enterLocationChosenState()
            }
        })
    }

    private fun setupViews() {
        ib_close.setOnClickListener { this.dismiss() }
        input_location.setHideKeyBoardOnPressAway()

        rv_location_change.layoutManager = LinearLayoutManager(context)

        input_location.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionID: Int, event: KeyEvent?): Boolean {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    searchFor(input_location.text.toString().trim())
                    return true
                }
                return false
            }

        })

        input_location.onTextChanged {
            locationChangeViewModel.searchTerm.value = it
            if(it.isNotEmpty()) {
                locationChangeViewModel.userState.value = UserState.TYPING
            }else if (locationChangeViewModel.userState.value != UserState.LOCATION_CHOSEN){
                locationChangeViewModel.userState.value = UserState.INITAL
            }

            rv_location_change.adapter = if(it.isNotEmpty()) autoCompleteAdapter else addedLocationsAdapter
            autoCompleteAdapter.terms = autoCompleteTerms.filter {term ->
                term.toLowerCase().contains(input_location.text.toString().trim().toLowerCase())
            }
        }
        btn_done.setOnClickListener {
            var location = addedLocationsAdapter.selectedLocation?.location
            if(location == currentSelectedLocation || location == null) this.dismiss()
            if(location == addedLocationsAdapter.currentLocationString){
                location = addedLocationsAdapter.currentDeterminedLocation?.location
            }
            locationChangeViewModel.saveSearchedLocation(AddedLocation(location!!))
            if(location == allLocations){ location = null }
            mFilterSetter.onSetCity(location)
            this.dismiss()
        }
        ib_clear_text.setOnClickListener { (input_location as TextView).text = "" }
    }

    private fun enterInitialState() {
        ib_close.background = resources.getDrawable(R.drawable.ic_close, null)
        ib_close.setOnClickListener {
            this.dismiss()
        }
        hideKeyBoard()
        rv_location_change.adapter = addedLocationsAdapter

    }

    private fun enterSearchedState() {
        //prolly show search results adapter or something
    }

    private fun enterLocationChosenState() {
        (input_location as TextView).text = ""
        hideKeyBoard()
        rv_location_change.adapter = addedLocationsAdapter
    }

    fun hideKeyBoard() {
        this.view?.hideKeyboard()
        input_location.clearFocus()
    }

    private fun enterTypingState() {
        ib_close.setOnClickListener {
            locationChangeViewModel.userState.value =
                UserState.INITAL
            hideKeyBoard()
        }
        rv_location_change.adapter = autoCompleteAdapter

        ib_close.background = resources.getDrawable(R.drawable.ic_arrow_back, null)

    }

    companion object {
        fun newInstance(filterSetter: FilterSetter, currentDeterminedLocation: String?,
                        currentSelectedLocation: String?): LocationChangeFragment {

            val fragment = LocationChangeFragment()
            fragment.currentDeterminedLocation = currentDeterminedLocation
            fragment.currentSelectedLocation = currentSelectedLocation
            fragment.mFilterSetter = filterSetter
            return fragment
        }
    }
}
