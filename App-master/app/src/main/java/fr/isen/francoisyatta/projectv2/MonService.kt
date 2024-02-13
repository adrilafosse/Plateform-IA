package fr.isen.francoisyatta.projectv2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MonService : Service() {
    private val TAG = "MonService"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service en arrière-plan démarré.")
        // Votre code pour effectuer des opérations en arrière-plan

        return START_STICKY
    }
}