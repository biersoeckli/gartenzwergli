package ch.ost.gartenzwergli.ui.crops

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.ActivitySelectCropBinding
import com.google.android.material.search.SearchBar

class SelectCropActivity : AppCompatActivity() {

    private lateinit var currentFragment: CropsFragment
    private lateinit var viewBinding: ActivitySelectCropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectCropBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        currentFragment =
            intent.getStringExtra("searchBarText")?.let { CropsFragment.newInstance(it,1) }!!

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, currentFragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}