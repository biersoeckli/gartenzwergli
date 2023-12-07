package ch.ost.gartenzwergli

import android.app.Application
import ch.ost.gartenzwergli.repository.CropEventRepository
import ch.ost.gartenzwergli.services.DatabaseService

class GartenzwergliApplication : Application() {

    // Using by lazy so database and the repository are only created when they're needed
    // rather than when the application starts
    val cropEventRepository by lazy { CropEventRepository(DatabaseService.getDb().cropEventDao()) }
}