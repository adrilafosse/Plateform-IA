package fr.isen.francoisyatta.projectv3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class maintenance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)  //liaison du code .kotlin à l'affichage layout .xml

        //val actionBar : ActionBar? = supportActionBar
        //actionBar!!.setDisplayHomeAsUpEnabled(true)
        //actionBar!!.setDisplayShowHomeEnabled(true)

        // prend les données depuis putExtra intent
        //var intent = intent
        //val aTitle = intent.getStringExtra("iTitle")

        //définit le titre dans une autre activité
        //actionBar.setTitle(aTitle)
        //a_title.text = aTitle
    }
}