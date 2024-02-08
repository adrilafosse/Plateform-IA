package fr.isen.francoisyatta.projectv2

import android.os.Build
import android.os.Bundle
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
import fr.isen.francoisyatta.projectv2.databinding.ActivityMaconsoBinding
import fr.isen.francoisyatta.projectv2.fragment.Annee
import fr.isen.francoisyatta.projectv2.fragment.Jour
import fr.isen.francoisyatta.projectv2.fragment.Mois
import fr.isen.francoisyatta.projectv2.fragment.Semaine
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter





class maconso : AppCompatActivity() {

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

        recupérationDonnées()
        Button1.setOnClickListener {
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
            //initializeScreen(fragmentSemaine.getFragmentSemaineBinding().consoGraph)
            //fetchDataAndFillList(bouton)
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
        val dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
        val dateFormatee = dateDuJour.format(formatter)
        Log.d("dateFormatee", "1: $dateFormatee")
        for (i in 0 until taille) {
            val jour = tableaufinal[i][2].joinToString("")
            if(jour == dateFormatee){
                Log.d("jour", "1: $jour")
                val consoString = tableaufinal[i][0].joinToString("")
                val heureString = tableaufinal[i][1].joinToString("")

                val conso = consoString.toFloatOrNull() ?: 0.0f
                val heure = heureString.toFloatOrNull() ?: 0.0f

                Log.d("consoFinal", "1: $conso")
                Log.d("heureFinal", "1: $heure")
                consoHeure.add(Pair(conso, heure))
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun donneeMois() {
        val dateDuJour = LocalDate.now()
        val formatterMois = DateTimeFormatter.ofPattern("MM")
        val moisToday = dateDuJour.format(formatterMois)
        Log.d("moisToday", "1: $moisToday")
        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val annee = dateDuJour.format(formatter)
        val consommationsParJours = mutableMapOf<String, MutableList<Float>>()

        // Parcourez les données et stockez les consommations associées à chaque jours
        for (i in 0 until taille) {
            val anneeString = tableaufinal[i][2].joinToString("")
            Log.d("anneeString", "$anneeString")
            val moisDonnee = anneeString.substring(2, 4)
            Log.d("moisDonnee", "$moisDonnee")
            val anneeDonnee = anneeString.substring(4, 8)

            // Vérifiez si le mois correspond à notre mois
            if (moisDonnee == moisToday && anneeDonnee == annee ) {
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
        // Calculez la moyenne de la consommation pour chaque jours
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
        val dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val annee = dateDuJour.format(formatter)
        Log.d("annee", "1: $annee")
        val consommationsParMois = mutableMapOf<String, MutableList<Float>>()

        // Parcourez les données et stockez les consommations associées à chaque mois
        for (i in 0 until taille) {
            val anneeString = tableaufinal[i][2].joinToString("")
            val anneeDonnee = anneeString.substring(4, 8)

            // Vérifiez si l'année correspond à notre année
            if (anneeDonnee == annee) {
                val consoString = tableaufinal[i][0].joinToString("")
                val dateString = tableaufinal[i][2].joinToString("")
                val mois = dateString.substring(2, 4) // Extrait le mois de la date

                val consoFinal = consoString.toFloatOrNull() ?: 0.0f

                // Ajoutez la consommation à la liste associée au mois correspondant
                if (mois in consommationsParMois) {
                    consommationsParMois[mois]!!.add(consoFinal)
                } else {
                    consommationsParMois[mois] = mutableListOf(consoFinal)
                }
                Log.d("consommationsParMois", "$consommationsParMois")
            }
        }

        // Calculez la moyenne de la consommation pour chaque mois
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
                                //on cree un tableau qui va prendre les valeurs de data
                                for (i in 0 until taille) {
                                    val value = data.entries.elementAtOrNull(i)?.value as? String
                                    Log.d("value", "1: $value")
                                    if (value != null) {
                                        monTableau.add(value)
                                        Log.d("valeur du tableau", "1: ${monTableau[i]}")
                                    }
                                }

                                val tableaustring = monTableau.toTypedArray()
                                var anneeALenversInt : Int =0
                                //on decoupe de string en 3 parties
                                tableaufinal = tableaustring.map { chaine ->
                                    val conso = charArrayOf(chaine[0], chaine[1], chaine[2])
                                    val heure = charArrayOf(chaine[5], chaine[6])
                                    val minutesChar = charArrayOf(chaine[8],chaine[9])
                                    val annee = charArrayOf(chaine[11], chaine[12], chaine[14],chaine[15],chaine[17],chaine[18],chaine[19],chaine[20])
                                    val anneeALenvers = charArrayOf(chaine[17], chaine[18], chaine[19],chaine[20],chaine[14],chaine[15],chaine[11],chaine[12])

                                    // Modification minute en base 10
                                    val minutesString = minutesChar.joinToString("")
                                    val minutesFloat = minutesString.toFloatOrNull() ?: 0.0f
                                    val minuteBase10 = String.format("%.2f", minutesFloat / 60)

                                    val minuteBase10Char = minuteBase10.toCharArray()
                                    Log.d("minuteBase10Char", "1: ${minuteBase10Char.joinToString("")}")
                                    val deuxDernierChiffres = charArrayOf(minuteBase10Char[1],minuteBase10Char[2],minuteBase10Char[3])
                                    Log.d("deuxDernierChiffres", "1: ${deuxDernierChiffres.joinToString("")}")
                                    // Concaténation des heures et des minutes
                                    val heureMinute = heure + deuxDernierChiffres
                                    Log.d("heureMinute", "1: ${heureMinute.joinToString("")}")

                                    val anneeALenversHeure = anneeALenvers+heureMinute

                                    arrayOf(conso, heureMinute, annee, anneeALenversHeure)
                                }
                                //trie le tableau par rapport a l'annee
                                tableaufinal = tableaufinal.sortedBy { ligne ->
                                    val anneeALenversHeure = String(ligne[3])
                                    anneeALenversHeure
                                }
                                for (ligne in tableaufinal) {
                                    val conso = String(ligne[0])
                                    val heure = String(ligne[1])
                                    val annee = String(ligne[2])
                                    val anneeALenversHeure = String(ligne[3])

                                    Log.d("Tableaufinal2", "Conso: $conso, Heure: $heure, Année: $annee, anneeALenversHeure : $anneeALenversHeure")
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
                    return String.format("%.0f", value)
                }
            }
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