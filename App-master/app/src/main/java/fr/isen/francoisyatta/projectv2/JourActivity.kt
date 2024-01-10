package fr.isen.francoisyatta.projectv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.francoisyatta.projectv2.fragment.Jour

class JourActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jour)
        val fragmentJour = Jour()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragmentJour)
            .commit()
    }
}