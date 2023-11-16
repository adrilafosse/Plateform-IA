package fr.isen.francoisyatta.projectv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar

class aide : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aide)  //liaison du code .kotlin à l'affichage layout .xml

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