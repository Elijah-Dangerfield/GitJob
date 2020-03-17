package com.dangerfield.gitjob.ui.jobs.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class UserState {
    TYPING, SEARCHED, INITAL
}
class LocationChangeViewModel: ViewModel() {
    var userState = MutableLiveData<UserState>()
}