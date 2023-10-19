package ch.ost.gartenzwergli.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val _cropEventList = MutableLiveData<List<String>>().apply {
        value = ArrayList<String>()
    }

    val text: LiveData<String> = _text
    val cropEventList: LiveData<List<String>> = _cropEventList
}