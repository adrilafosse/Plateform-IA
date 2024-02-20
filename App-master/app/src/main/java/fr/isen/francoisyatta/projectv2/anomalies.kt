package fr.isen.francoisyatta.projectv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_anomalies.*



class anomalies : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anomalies)

        val options = arrayOf("Classique", "Heure pleine/creuse")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_profil.adapter = adapter

        spinner_profil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedOption = options[position]
                if (selectedOption == "Heure pleine/creuse") {
                        textView_prix_fixeHP.visibility = View.VISIBLE
                        editText_nouveau_prixHP.visibility = View.VISIBLE

                        textView_prix_fixeHC.visibility = View.VISIBLE
                        editText_nouveau_prixHC.visibility = View.VISIBLE

                        textView_prix_fixe.visibility = View.GONE
                        editText_nouveau_prix.visibility = View.GONE
                    } else {
                        textView_prix_fixeHP.visibility = View.GONE
                        editText_nouveau_prixHP.visibility = View.GONE

                        textView_prix_fixeHC.visibility = View.GONE
                        editText_nouveau_prixHC.visibility = View.GONE

                        textView_prix_fixe.visibility = View.VISIBLE
                        editText_nouveau_prix.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Faire quelque chose si rien n'est sélectionné
                }
            }
    }
}