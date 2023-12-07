package ch.ost.gartenzwergli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
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
            val statusTextView = findViewById<TextView>(R.id.initialStateDescriptionTextView);
            val dataStorage = DataStorage()

            statusTextView.text = "Loading crops and images (this may take a while)"
            dataStorage.runInitial(applicationContext)
            statusTextView.text = "Loading crop details (this may take a while)"
            dataStorage.onCropSyncState.observe(this@InitialDataLoaderActivity, {
                statusTextView.text = "Loading crop details ($it)"
            })
            dataStorage.syncDetailDataFromNewCropDbos()

            finish()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}