package ch.ost.gartenzwergli

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ch.ost.gartenzwergli.databinding.ActivityMainBinding
import ch.ost.gartenzwergli.services.DataStorage
import ch.ost.gartenzwergli.services.DatabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // !!! START | THIS HAS TO BE EXECUTED AT APPLICATION START | START !!!
        DatabaseService.setupDbWithContext(application.applicationContext)
        launch {
            val dataStorage = DataStorage()
            if (dataStorage.isInitialRunNecessary()) {
                Intent(this@MainActivity, InitialDataLoaderActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                dataStorage.syncDetailDataFromNewCropDbos()
            }
        }
        // !!! END | THIS HAS TO BE EXECUTED AT APPLICATION START | END !!!

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_crops, R.id.navigation_home, R.id.navigation_beds
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}