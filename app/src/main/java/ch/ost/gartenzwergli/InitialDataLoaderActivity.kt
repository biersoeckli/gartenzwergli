package ch.ost.gartenzwergli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.ost.gartenzwergli.services.DataStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class InitialDataLoaderActivity : AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_data_loader)

        launch {
            val dataStorage = DataStorage()
            dataStorage.runInitial(applicationContext)
            finish()
        }
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}