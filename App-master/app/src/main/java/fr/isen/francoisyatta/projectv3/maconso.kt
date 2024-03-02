package fr.isen.francoisyatta.projectv3

import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.francoisyatta.projectv3.databinding.ActivityMaconsoBinding
import fr.isen.francoisyatta.projectv3.fragment.Annee
import fr.isen.francoisyatta.projectv3.fragment.Jour
import fr.isen.francoisyatta.projectv3.fragment.Mois
import fr.isen.francoisyatta.projectv3.fragment.Semaine
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class maconso : AppCompatActivity(){

    private lateinit var binding: ActivityMaconsoBinding
    private lateinit var fragmentJour: Jour
    private lateinit var fragmentSemaine: Semaine
    private lateinit var fragmentMois: Mois
    private lateinit var fragmentAnnee: Annee
    private val consoHeure = ArrayList<Pair<Float, Float>>()
    private var tableaufinal: List<Array<CharArray>> = listOf()
    var taille: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maconso)

        binding = ActivityMaconsoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val Button1: Button = findViewById(R.id.button1)
        val Button2: Button = findViewById(R.id.button2)
        val Button3: Button = findViewById(R.id.button3)
        val Button4: Button = findViewById(R.id.button4)

        fragmentJour = Jour()
        fragmentSemaine = Semaine()
        fragmentMois = Mois()
        fragmentAnnee = Annee()


        Button1.setOnClickListener {
            recupérationDonnées()
            val bouton = 1
            consoHeure.clear()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentJour)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            donneeJour()
            initializeScreen(fragmentJour.getFragmentJourBinding().consoGraph,bouton)
        }
        Button2.setOnClickListener {
            recupérationDonnées()
            val bouton =2
            consoHeure.clear()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentSemaine)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            donneeSemaine()
            initializeScreen(fragmentSemaine.getFragmentSemaineBinding().consoGraph, bouton)
        }

        Button3.setOnClickListener {
            recupérationDonnées()
            consoHeure.clear()
            val bouton =3
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentMois)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            donneeMois()
            initializeScreen(fragmentMois.getFragmentMoisBinding().consoGraph, bouton)
        }

        Button4.setOnClickListener {
            val bouton =4
            recupérationDonnées()
            consoHeure.clear()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentAnnee)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            donneeAnnee()
            initializeScreen(fragmentAnnee.getFragmentAnneeBinding().consoGraph,bouton)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun donneeJour(){
        val dateDuFragment = fragmentJour.getDateFormatee()
        Log.d("date du fragment", "$dateDuFragment")

        // Analyser la date du fragment
        val formatterFragment = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateFragment = LocalDate.parse(dateDuFragment, formatterFragment)

        //Log.d("dateDuJour", "$dateDuJour")
        val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
        //val dateFormatee = dateDuJour.format(formatter)
        val dateDuFragment2 = dateFragment.format(formatter)

        Log.d("date du fragment2", "$dateDuFragment2")
        //Log.d("dateFormatee", "1: $dateFormatee")

        for (i in 0 until taille) {
            val jour = tableaufinal[i][2].joinToString("")
            if(jour == dateDuFragment2){
                Log.d("jour", "1: $jour")
                val consoString = tableaufinal[i][0].joinToString("")
                val heureString = tableaufinal[i][1].joinToString("")
                Log.d("heureString", "1: $heureString")

                val conso = consoString.toFloatOrNull() ?: 0.0f
                val heure = heureString.replace(",", ".").toFloatOrNull() ?: 0.0f

                Log.d("consoFinal", "1: $conso")
                Log.d("heureFinal", "1: $heure")
                consoHeure.add(Pair(conso, heure))
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun donneeSemaine(){

        val dateDuFragment = fragmentSemaine.getDateFormatee()
        Log.d("date du fragment semaine", "$dateDuFragment")

        val formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateDuFragmentLocalDate = LocalDate.parse(dateDuFragment, formatter1)

        //recupere le jour en anglais de la semaine
        val jourDeLaSemaine = dateDuFragmentLocalDate.dayOfWeek

        //recupere le jour en chiffre du jour
        val formatter = DateTimeFormatter.ofPattern("dd")
        val dateFormateeStr = dateDuFragmentLocalDate.format(formatter)

        //recupere mois et annee
        val formatter2= DateTimeFormatter.ofPattern("MMyyyy")
        val moisAnneeToday = dateDuFragmentLocalDate.format(formatter2)
        Log.d("moisAnneeToday", "1: $moisAnneeToday")

        // Convertir la date formatée en un entier
        val dateFormatee = dateFormateeStr.toInt()

        //transforme le jour en anglais en francais
        val jourDeLaSemaineEnString = jourDeLaSemaine.getDisplayName(TextStyle.FULL, Locale.FRENCH)
        Log.d("jourDeLaSemaineEnString", "1: $jourDeLaSemaineEnString")

        //cree des map par jour de la semaine
        val consommationsParJoursLundi = mutableMapOf<String, MutableList<Float>>()
        val consommationsParJoursMardi = mutableMapOf<String, MutableList<Float>>()
        val consommationsParJoursMercredi = mutableMapOf<String, MutableList<Float>>()
        val consommationsParJoursJeudi = mutableMapOf<String, MutableList<Float>>()
        val consommationsParJoursVendredi = mutableMapOf<String, MutableList<Float>>()
        val consommationsParJoursSamedi = mutableMapOf<String, MutableList<Float>>()
        val consommationsParJoursDimanche = mutableMapOf<String, MutableList<Float>>()

        for (i in 0 until taille) {

            val date = tableaufinal[i][2].joinToString("")

            val jourSemaineChar = charArrayOf(date[0],date[1])
            val jours = date
                .substring(0,2 ) // Extrait le jour de la date
            val moisAnnee = date
                .substring(2,8) // extrait mois annee
            Log.d("moisAnnee", "1: $moisAnnee")
            //recupere conso
            val consoString = tableaufinal[i][0].joinToString("")
            val conso = consoString.toFloatOrNull() ?: 0.0f

            //transforme je jour en INT
            val premierChiffre = Character.getNumericValue(jourSemaineChar[0])
            val deuxiemeChiffre = Character.getNumericValue(jourSemaineChar[1])

            val jourSemaineInt = premierChiffre*10 + deuxiemeChiffre
            Log.d("jourSemaineInt", "1: $jourSemaineInt")

            if( moisAnnee == moisAnneeToday){
                if(jourDeLaSemaineEnString == "lundi"){
                    if ( jourSemaineInt == dateFormatee || jourSemaineInt == dateFormatee+1 ||
                        jourSemaineInt == dateFormatee+2 || jourSemaineInt == dateFormatee+3 ||
                        jourSemaineInt == dateFormatee+4 || jourSemaineInt == dateFormatee+5 ||
                        jourSemaineInt == dateFormatee+6){

                        if (jours in consommationsParJoursLundi) {
                            consommationsParJoursLundi[jours]!!.add(conso)
                        } else {
                            consommationsParJoursLundi[jours] = mutableListOf(conso)
                        }
                    }
                }
                if(jourDeLaSemaineEnString == "mardi"){
                    if ( jourSemaineInt == dateFormatee || jourSemaineInt == dateFormatee+1 ||
                        jourSemaineInt == dateFormatee+2 || jourSemaineInt == dateFormatee+3 ||
                        jourSemaineInt == dateFormatee+4 || jourSemaineInt == dateFormatee+5
                        || jourSemaineInt == dateFormatee-1){

                        if (jours in consommationsParJoursMardi) {
                            consommationsParJoursMardi[jours]!!.add(conso)
                        } else {
                            consommationsParJoursMardi[jours] = mutableListOf(conso)
                        }
                    }
                }
                if(jourDeLaSemaineEnString == "mercredi"){
                    if ( jourSemaineInt == dateFormatee || jourSemaineInt == dateFormatee+1 ||
                        jourSemaineInt == dateFormatee+2 || jourSemaineInt == dateFormatee+3 ||
                        jourSemaineInt == dateFormatee+4 || jourSemaineInt == dateFormatee-2 ||
                        jourSemaineInt == dateFormatee-1){

                        if (jours in consommationsParJoursMercredi) {
                            consommationsParJoursMercredi[jours]!!.add(conso)
                        } else {
                            consommationsParJoursMercredi[jours] = mutableListOf(conso)
                        }
                    }
                }
                if(jourDeLaSemaineEnString == "jeudi"){
                    if ( jourSemaineInt == dateFormatee || jourSemaineInt == dateFormatee+1 ||
                        jourSemaineInt == dateFormatee+2 || jourSemaineInt == dateFormatee+3 ||
                        jourSemaineInt == dateFormatee-3 || jourSemaineInt == dateFormatee-2 ||
                        jourSemaineInt == dateFormatee-1){

                        if (jours in consommationsParJoursJeudi) {
                            consommationsParJoursJeudi[jours]!!.add(conso)
                        } else {
                            consommationsParJoursJeudi[jours] = mutableListOf(conso)
                        }
                    }
                }
                if(jourDeLaSemaineEnString == "vendredi"){
                    if ( jourSemaineInt == dateFormatee || jourSemaineInt == dateFormatee+1 ||
                        jourSemaineInt == dateFormatee+2 || jourSemaineInt == dateFormatee-4 ||
                        jourSemaineInt == dateFormatee-3 || jourSemaineInt == dateFormatee-2 ||
                        jourSemaineInt == dateFormatee-1){

                        if (jours in consommationsParJoursVendredi) {
                            consommationsParJoursVendredi[jours]!!.add(conso)
                        } else {
                            consommationsParJoursVendredi[jours] = mutableListOf(conso)
                        }
                    }
                }
                if(jourDeLaSemaineEnString == "samedi"){
                    if ( jourSemaineInt == dateFormatee || jourSemaineInt == dateFormatee+1 ||
                        jourSemaineInt == dateFormatee-5 || jourSemaineInt == dateFormatee-4 ||
                        jourSemaineInt == dateFormatee-3 || jourSemaineInt == dateFormatee-2 ||
                        jourSemaineInt == dateFormatee-1){

                        if (jours in consommationsParJoursSamedi) {
                            consommationsParJoursSamedi[jours]!!.add(conso)
                        } else {
                            consommationsParJoursSamedi[jours] = mutableListOf(conso)
                        }
                    }
                }
                if(jourDeLaSemaineEnString == "dimanche"){
                    if ( jourSemaineInt == dateFormatee || jourSemaineInt == dateFormatee-6 ||
                        jourSemaineInt == dateFormatee-5 || jourSemaineInt == dateFormatee-4 ||
                        jourSemaineInt == dateFormatee-3 || jourSemaineInt == dateFormatee-2 ||
                        jourSemaineInt == dateFormatee-1){

                        if (jours in consommationsParJoursDimanche) {
                            consommationsParJoursDimanche[jours]!!.add(conso)
                        } else {
                            consommationsParJoursDimanche[jours] = mutableListOf(conso)
                        }
                    }
                }
            }

            Log.d("consommationsParJoursLundi", "$consommationsParJoursLundi")
            Log.d("consommationsParJoursMardi", "$consommationsParJoursMardi")
            Log.d("consommationsParJoursMercredi", "$consommationsParJoursMercredi")
            Log.d("consommationsParJoursJeudi", "$consommationsParJoursJeudi")
            Log.d("consommationsParJoursVendredi", "$consommationsParJoursVendredi")
            Log.d("consommationsParJoursSamedi", "$consommationsParJoursSamedi")
            Log.d("consommationsParJoursDimanche", "$consommationsParJoursDimanche")
        }
        // Calculez la moyenne de la somme pour chaque jours
        if(consommationsParJoursLundi.isNotEmpty()){
            for ((jours, consommations) in consommationsParJoursLundi) {
                val moyenne = consommations.sum()
                Log.d("somme conso pour lundi $jours", "$moyenne")
                val joursInt = jours.toIntOrNull()
                Log.d("dateFormatee ", "$dateFormatee")
                Log.d("joursInt ", "$joursInt")
                var jourSemaine : Int =0
                if(joursInt == dateFormatee){
                    jourSemaine = 1
                }
                if(joursInt == dateFormatee +1){
                    jourSemaine = 2
                }
                if(joursInt == dateFormatee +2){
                    jourSemaine = 3
                }
                if(joursInt == dateFormatee +3){
                    jourSemaine = 4
                }
                if(joursInt == dateFormatee +4){
                    jourSemaine = 5
                }
                if(joursInt == dateFormatee +5){
                    jourSemaine = 6
                }
                if(joursInt == dateFormatee +6){
                    jourSemaine = 7
                }
                consoHeure.add(Pair(moyenne, jourSemaine.toFloat()))
            }
        }
        // Calculez la moyenne de la somme pour chaque jours
        if(consommationsParJoursMardi.isNotEmpty()){
            for ((jours, consommations) in consommationsParJoursMardi) {
                val moyenne = consommations.sum()
                Log.d("somme conso pour mardi $jours", "$moyenne")
                val joursInt = jours.toIntOrNull()
                Log.d("dateFormatee ", "$dateFormatee")
                Log.d("joursInt ", "$joursInt")
                var jourSemaine : Int =0
                if(joursInt == dateFormatee){
                    jourSemaine = 2
                }
                if(joursInt == dateFormatee +1){
                    jourSemaine = 3
                }
                if(joursInt == dateFormatee +2){
                    jourSemaine = 4
                }
                if(joursInt == dateFormatee +3){
                    jourSemaine = 5
                }
                if(joursInt == dateFormatee -1){
                    jourSemaine = 1
                }
                if(joursInt == dateFormatee +4){
                    jourSemaine = 6
                }
                if(joursInt == dateFormatee +5){
                    jourSemaine = 7
                }
                consoHeure.add(Pair(moyenne, jourSemaine.toFloat()))
            }
        }
        // Calculez la moyenne de la somme pour chaque jours
        if(consommationsParJoursMercredi.isNotEmpty()){
            for ((jours, consommations) in consommationsParJoursMercredi) {
                val moyenne = consommations.sum()
                Log.d("somme conso pour mercredi $jours", "$moyenne")
                val joursInt = jours.toIntOrNull()
                Log.d("dateFormatee ", "$dateFormatee")
                Log.d("joursInt ", "$joursInt")
                var jourSemaine : Int =0
                if(joursInt == dateFormatee){
                    jourSemaine = 3
                }
                if(joursInt == dateFormatee +1){
                    jourSemaine = 4
                }
                if(joursInt == dateFormatee +2){
                    jourSemaine = 5
                }
                if(joursInt == dateFormatee +3){
                    jourSemaine = 6
                }
                if(joursInt == dateFormatee -1){
                    jourSemaine = 2
                }
                if(joursInt == dateFormatee -2){
                    jourSemaine = 1
                }
                if(joursInt == dateFormatee +4){
                    jourSemaine = 7
                }
                consoHeure.add(Pair(moyenne, jourSemaine.toFloat()))
            }
        }
        // Calculez la moyenne de la somme pour chaque jours
        if(consommationsParJoursJeudi.isNotEmpty()){
            for ((jours, consommations) in consommationsParJoursJeudi) {
                val moyenne = consommations.sum()
                Log.d("somme conso pour jeudi $jours", "$moyenne")
                val joursInt = jours.toIntOrNull()
                Log.d("dateFormatee ", "$dateFormatee")
                Log.d("joursInt ", "$joursInt")
                var jourSemaine : Int =0
                if(joursInt == dateFormatee){
                    jourSemaine = 4
                }
                if(joursInt == dateFormatee +1){
                    jourSemaine = 5
                }
                if(joursInt == dateFormatee +2){
                    jourSemaine = 6
                }
                if(joursInt == dateFormatee +3){
                    jourSemaine = 7
                }
                if(joursInt == dateFormatee -1){
                    jourSemaine = 3
                }
                if(joursInt == dateFormatee -2){
                    jourSemaine = 2
                }
                if(joursInt == dateFormatee -3){
                    jourSemaine = 1
                }
                consoHeure.add(Pair(moyenne, jourSemaine.toFloat()))
            }
        }
        // Calculez la moyenne de la somme pour chaque jours
        if(consommationsParJoursVendredi.isNotEmpty()){
            for ((jours, consommations) in consommationsParJoursVendredi) {
                val moyenne = consommations.sum()
                Log.d("somme conso pour vendredi $jours", "$moyenne")
                val joursInt = jours.toIntOrNull()
                Log.d("dateFormatee ", "$dateFormatee")
                Log.d("joursInt ", "$joursInt")
                var jourSemaine : Int =0
                if(joursInt == dateFormatee){
                    jourSemaine = 5
                }
                if(joursInt == dateFormatee +1){
                    jourSemaine = 6
                }
                if(joursInt == dateFormatee +2){
                    jourSemaine = 7
                }
                if(joursInt == dateFormatee -4){
                    jourSemaine = 1
                }
                if(joursInt == dateFormatee -1){
                    jourSemaine = 4
                }
                if(joursInt == dateFormatee -2){
                    jourSemaine = 3
                }
                if(joursInt == dateFormatee -3){
                    jourSemaine = 2
                }
                consoHeure.add(Pair(moyenne, jourSemaine.toFloat()))
            }
        }
        // Calculez la moyenne de la somme pour chaque jours
        if(consommationsParJoursSamedi.isNotEmpty()){
            for ((jours, consommations) in consommationsParJoursSamedi) {
                val moyenne = consommations.sum()
                Log.d("somme conso pour samedi $jours", "$moyenne")
                val joursInt = jours.toIntOrNull()
                Log.d("dateFormatee ", "$dateFormatee")
                Log.d("joursInt ", "$joursInt")
                var jourSemaine : Int =0
                if(joursInt == dateFormatee){
                    jourSemaine = 6
                }
                if(joursInt == dateFormatee +1){
                    jourSemaine = 7
                }
                if(joursInt == dateFormatee -4){
                    jourSemaine = 2
                }
                if(joursInt == dateFormatee -5){
                    jourSemaine = 1
                }
                if(joursInt == dateFormatee -1){
                    jourSemaine = 5
                }
                if(joursInt == dateFormatee -2){
                    jourSemaine = 4
                }
                if(joursInt == dateFormatee -3){
                    jourSemaine = 3
                }
                consoHeure.add(Pair(moyenne, jourSemaine.toFloat()))
            }
        }
        // Calculez la moyenne de la somme pour chaque jours
        if(consommationsParJoursDimanche.isNotEmpty()){
            for ((jours, consommations) in consommationsParJoursDimanche) {
                val moyenne = consommations.sum()
                Log.d("somme conso pour jeudi $jours", "$moyenne")
                val joursInt = jours.toIntOrNull()
                Log.d("dateFormatee ", "$dateFormatee")
                Log.d("joursInt ", "$joursInt")
                var jourSemaine : Int =0
                if(joursInt == dateFormatee){
                    jourSemaine = 7
                }
                if(joursInt == dateFormatee -4){
                    jourSemaine = 3
                }
                if(joursInt == dateFormatee -5){
                    jourSemaine = 2
                }
                if(joursInt == dateFormatee -6){
                    jourSemaine = 1
                }
                if(joursInt == dateFormatee -1){
                    jourSemaine = 6
                }
                if(joursInt == dateFormatee -2){
                    jourSemaine = 5
                }
                if(joursInt == dateFormatee -3){
                    jourSemaine = 4
                }
                consoHeure.add(Pair(moyenne, jourSemaine.toFloat()))
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun donneeMois() {
        val dateDuFragment = fragmentMois.getDateFormatee()
        Log.d("date du fragment mois", "$dateDuFragment")

        val dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val annee = dateDuJour.format(formatter)
        Log.d("annee mois", "$annee")

        val consommationsParJours = mutableMapOf<String, MutableList<Float>>()

        // Parcourez les données et stockez les consommations associées à chaque jours
        for (i in 0 until taille) {
            val anneeString = tableaufinal[i][2].joinToString("")
            Log.d("anneeString", "$anneeString")
            val moisDonnee = anneeString.substring(2, 4)
            Log.d("moisDonnee", "$moisDonnee")
            val anneeDonnee = anneeString.substring(4, 8)

            // Vérifiez si le mois correspond à notre mois
            if (moisDonnee == dateDuFragment && anneeDonnee == annee ) {
                val consoString = tableaufinal[i][0].joinToString("")
                val dateString = tableaufinal[i][2].joinToString("")
                val jours = dateString.substring(0,2 ) // Extrait le jour de la date
                Log.d("joursMois $jours", " anneeDonnee $anneeDonnee" )

                val consoFinal = consoString.toFloatOrNull() ?: 0.0f

                if (jours in consommationsParJours) {
                    consommationsParJours[jours]!!.add(consoFinal)
                } else {
                    consommationsParJours[jours] = mutableListOf(consoFinal)
                }
                Log.d("consommationsParJours", "$consommationsParJours")
            }
        }
        // Calculez la moyenne de la somme pour chaque jours
        for ((jours, consommations) in consommationsParJours) {
            val moyenne = consommations.sum()
            Log.d("Moyenne pour le jour $jours", "$moyenne")
            val moyenneFloat = moyenne.toFloat()
            val joursFloat = jours.toFloat()
            Log.d("moyenneFloat", "$moyenneFloat")
            Log.d("joursFloat", "$joursFloat")
            consoHeure.add(Pair(moyenne.toFloat(), jours.toFloat()))
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun donneeAnnee(){
        val dateDuFragment = fragmentAnnee.getDateFormatee()
        Log.d("date du fragment annee", "$dateDuFragment")

        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val dateDuFragment2 = dateDuFragment.format(formatter)
        Log.d("dateDuFragment2", "$dateDuFragment2")
        val consommationsParMois = mutableMapOf<String, MutableList<Float>>()

        // Parcourez les données et stockez les consommations associées à chaque mois
        for (i in 0 until taille) {
            val anneeString = tableaufinal[i][2].joinToString("")
            val anneeDonnee = anneeString.substring(4, 8)

            // Vérifiez si l'année correspond à notre année
            if (anneeDonnee == dateDuFragment2) {
                val consoString = tableaufinal[i][0].joinToString("")
                val dateString = tableaufinal[i][2].joinToString("")
                val mois = dateString.substring(2, 4) // Extrait le mois de la date

                val consoFinal = consoString.toFloatOrNull() ?: 0.0f
                Log.d("consoFinal", "1: $consoFinal")

                // Ajoutez la consommation à la liste associée au mois correspondant
                if (mois in consommationsParMois) {
                    consommationsParMois[mois]!!.add(consoFinal)
                } else {
                    consommationsParMois[mois] = mutableListOf(consoFinal)
                }
                Log.d("consommationsParMois", "$consommationsParMois")
            }
        }

        // Calculez la somme de la consommation pour chaque mois
        for ((mois, consommations) in consommationsParMois) {
            val moyenne = consommations.sum()
            Log.d("Moyenne pour le mois $mois", "$moyenne")
            val moyenneFloat = moyenne.toFloat()
            val moisFloat = mois.toFloat()
            Log.d("moyenneFloat", "$moyenneFloat")
            Log.d("moisFloat", "$moisFloat")
            consoHeure.add(Pair(moyenne.toFloat(), mois.toFloat()))
        }

    }
    private fun decryptAES(encrypted: String, key: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")

        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT))
        return String(decryptedBytes)
    }

    private fun recupérationDonnées() {
        val db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            // on recupère l'uid de l'utilisateur
            val uid = currentUser.uid
            Log.d("conso uid", "1: $uid")

            // on récupère les données de la collection id qui a pour id l'uid de l'utilisateur
            db.collection("id").document(uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            //on recupere les données
                            val data = document.data
                            Log.d("donnée de firebase", "1: $data")

                            if(data != null){
                                taille = data.size
                                Log.d("taille du tableau", "1: $taille")

                                val monTableau = mutableListOf<String>()
                                //Create a table with the data
                                for (i in 0 until taille) {

                                    val value = data.entries.elementAtOrNull(i)?.value as? String
                                    Log.d("valeur", "1: $value")

                                    val key = "603DEB1015CA71BE2B73AEF0857D7781" // Should be 16/32 bytes for AES-128/256

                                    if (value != null) {
                                        //Decrypt the value
                                        val decryptedValue = decryptAES(value, key)
                                        Log.d("valeur decryptée", "1: $decryptedValue")

                                        //Add the decrypted value to the array
                                        monTableau.add(decryptedValue)
                                    }
                                }

                                val tableaustring = monTableau.toTypedArray()

                                //Separate the string into multiple parts
                                tableaufinal = tableaustring.map { chaine ->
                                    val conso = charArrayOf(chaine[12],chaine[13], chaine[14], chaine[15],chaine[16],chaine[17],chaine[18],chaine[19],chaine[20])
                                    val hour = charArrayOf(chaine[6], chaine[7])
                                    val minute = charArrayOf(chaine[8],chaine[9])
                                    val year = charArrayOf(chaine[0], chaine[1], chaine[2],chaine[3],'2','0',chaine[4],chaine[5])
                                    val yearBackwards = charArrayOf('2','0', chaine[4],chaine[5],chaine[2],chaine[3],chaine[0],chaine[1])
                                    val yearFormatted = charArrayOf('2', '0',chaine[4],chaine[5],'-',chaine[2],chaine[3],'-',chaine[0],chaine[1])

                                    // Modification minute en base 10
                                    val minutesString = minute.joinToString("")
                                    val minutesFloat = minutesString.toFloatOrNull() ?: 0.0f
                                    val minuteBase10 = String.format("%.2f", minutesFloat / 60)

                                    val minuteBase10Char = minuteBase10.toCharArray()
                                    Log.d("minuteBase10Char", "1: ${minuteBase10Char.joinToString("")}")
                                    val deuxDernierChiffres = charArrayOf(minuteBase10Char[1],minuteBase10Char[2],minuteBase10Char[3])
                                    Log.d("deuxDernierChiffres", "1: ${deuxDernierChiffres.joinToString("")}")
                                    // Concaténation des heures et des minutes
                                    val heureMinute = hour + deuxDernierChiffres
                                    Log.d("heureMinute", "1: ${heureMinute.joinToString("")}")

                                    val anneeALenversHeure = yearBackwards+heureMinute
                                    Log.d("anneeALenversHeure", "1: ${anneeALenversHeure.joinToString("")}")
                                    arrayOf(conso, heureMinute, year, anneeALenversHeure,yearFormatted)
                                }
                                //trie le tableau par rapport a l'annee
                                tableaufinal = tableaufinal.sortedBy { ligne ->
                                    val anneeALenversHeure = String(ligne[3])
                                    anneeALenversHeure
                                }
                                for (ligne in tableaufinal) {
                                    val conso = String(ligne[0])
                                    val hour = String(ligne[1])
                                    val year = String(ligne[2])
                                    val yearFormattedHour = String(ligne[3])
                                    val yearFormatted = String(ligne[4])

                                    Log.d("Tableaufinal2", "Conso: $conso, Heure: $hour, Année: $year, anneeALenversHeure : $yearFormattedHour,anneeALenversFormater : $yearFormatted")
                                }


                            }
                        } else {
                            Log.e("Activite", "Erreur: Document nul ou inexistant")
                        }
                    } else {
                        Log.e("Activite", "Erreur lors de la récupération des données de 'id'", task.exception)
                    }
                }
        }
    }
    private fun initializeScreen(chart: LineChart, btn : Int) {
        val consommation = setLineChartData(evolution_consommation(), R.color.bleusavee)
        val graphLignes = listOf<LineDataSet>(consommation)

        drawChart(graphLignes, chart, btn)
    }



    private fun setLineChartData(lineValues: ArrayList<Entry>, color: Int): LineDataSet {
        val lineDataset = LineDataSet(lineValues, "Consommation en W")
        //We add features to our chart
        lineDataset.color = resources.getColor(color)
        lineDataset.lineWidth = 3f
        lineDataset.circleRadius = 2f
        lineDataset.setCircleColors(color)
        lineDataset.valueTextSize = 0f
        lineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER

        return lineDataset
    }

    //fonction pour effectuer le tracé de la courbe de consommation
    private fun drawChart(linesDataSet: List<LineDataSet>, chart: LineChart, btn : Int) {
        val data = LineData(linesDataSet)
        chart.data = data
        chart.animateXY(1500, 1500, Easing.EaseInCubic)

        chart.setTouchEnabled(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)

        val x: XAxis = chart.xAxis
        x.position = XAxis.XAxisPosition.BOTTOM

        x.setDrawAxisLine(true)
        x.setDrawGridLines(false)
        x.textColor = getColor(R.color.black)
        x.axisLineColor = getColor(R.color.black)

        if( btn ==1){
            // Définir les valeurs minimale et maximale de l'axe X
            x.granularity = 1f // affichage des valeurs entières
            x.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.0f", value)
                }
            }
            x.labelCount = 12
            x.granularity = 1f
            x.axisMinimum = 0f
            x.axisMaximum = 24f
            chart.description.text = "H"
            chart.description.textSize = 12f
        }
        if (btn == 2) {
            // Définir les valeurs minimale et maximale de l'axe X
            x.granularity = 1f // affichage des valeurs entières
            x.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value) {
                        1f -> "Lu"
                        2f -> "Ma"
                        3f -> "Mer"
                        4f -> "Je"
                        5f -> "Ven"
                        6f -> "Sa"
                        7f -> "Di"
                        else -> ""
                    }
                }
            }
            x.labelCount = 7 // Nombre total de libellés sur l'axe X
            x.axisMinimum = 0.5f // Ajuster légèrement les valeurs minimale et maximale pour centrer les libellés
            x.axisMaximum = 7.5f
            chart.description.text = "Jours"
            chart.description.textSize = 12f
        }

        if( btn ==3){
            // Définir les valeurs minimale et maximale de l'axe X
            x.granularity = 1f // affichage des valeurs entières
            x.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.0f", value)
                }
            }
            x.labelCount = 12
            x.granularity = 2f
            x.axisMinimum = 0f
            x.axisMaximum = 31f
            chart.description.text = "Jours"
            chart.description.textSize = 12f
        }
        if( btn == 4){
            // Définir les valeurs minimale et maximale de l'axe X
            x.granularity = 1f // affichage des valeurs entières
            x.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value) {
                        1f -> "J"
                        2f -> "F"
                        3f -> "M"
                        4f -> "A"
                        5f -> "M"
                        6f -> "J"
                        7f -> "J"
                        8f -> "A"
                        9f -> "S"
                        10f -> "O"
                        11f -> "N"
                        12f -> "D"
                        else -> ""
                    }
                }
            }
            x.axisMinimum = 0.5f
            x.labelCount = 12
            x.granularity = 1f
            x.axisMinimum = 0f
            x.axisMaximum = 12f
            chart.description.text = "Mois"
            chart.description.textSize = 12f
        }

        val y: YAxis = chart.axisLeft

        y.setDrawZeroLine(true)
        y.setDrawGridLines(false)
        y.textColor = getColor(R.color.black)
        y.axisLineColor = getColor(R.color.black)

        chart.axisRight.isEnabled = false
    }
    private fun evolution_consommation(): ArrayList<Entry> {
        val lineValues = ArrayList<Entry>()

        for ((index, pair) in consoHeure.withIndex()) {
            Log.d("conso data 3", "index: $index")
            Log.d("conso data 4", "pair $pair")

            lineValues.add(Entry(pair.second , pair.first))
        }

        return lineValues
    }


}