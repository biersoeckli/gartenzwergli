package ch.ost.gartenzwergli.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _cropEventList = MutableLiveData<List<String>>().apply {
        value = ArrayList<String>()
    }

    val cropEventList: LiveData<List<String>> = _cropEventList
}