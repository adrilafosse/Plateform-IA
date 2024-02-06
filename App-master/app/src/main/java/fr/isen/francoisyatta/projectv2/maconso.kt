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
            consoHeure.clear()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentJour)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            doneeJour()
            initializeScreen(fragmentJour.getFragmentJourBinding().consoGraph)
        }
        Button2.setOnClickListener {
            val bouton =2
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentSemaine)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            initializeScreen(fragmentSemaine.getFragmentSemaineBinding().consoGraph)
            //fetchDataAndFillList(bouton)
        }

        Button3.setOnClickListener {
            val bouton =3
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentMois)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            initializeScreen(fragmentMois.getFragmentMoisBinding().consoGraph)
            //fetchDataAndFillList(bouton)
        }

        Button4.setOnClickListener {
            val bouton =4
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentAnnee)
            transaction.commitNow()

            supportFragmentManager.executePendingTransactions()
            initializeScreen(fragmentAnnee.getFragmentAnneeBinding().consoGraph)
            //fetchDataAndFillList(bouton)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun doneeJour(){
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
                consoHeure.add(Pair(conso, heure))
            }
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

                                //on decoupe de string en 3 parties
                                tableaufinal = tableaustring.map { chaine ->
                                    val conso = charArrayOf(chaine[0], chaine[1], chaine[2])
                                    val heure = charArrayOf(chaine[5], chaine[6], chaine[8],chaine[9])
                                    val minutesChar = charArrayOf(chaine[8],chaine[9])
                                    val annee = charArrayOf(chaine[11], chaine[12], chaine[14],chaine[15],chaine[17],chaine[18],chaine[19],chaine[20])

                                    // Modification minute en base 10
                                    val minutesString = minutesChar.joinToString("")
                                    val minutesFloat = minutesString.toFloatOrNull() ?: 0.0f
                                    Log.d("minutesFloat", "1: $minutesFloat")

                                    val minuteBase10 = (minutesFloat / 60).toString()
                                    Log.d("minuteBase10", "1: $minuteBase10")

                                    // Concaténation des heures et des minutes
                                    val heureMinute = heure + minuteBase10.toCharArray()

                                    arrayOf(conso, heureMinute, annee)
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
    private fun initializeScreen(chart: LineChart) {
        val consommation = setLineChartData(evolution_consommation(), R.color.bleusavee)
        val graphLignes = listOf<LineDataSet>(consommation)

        drawChart(graphLignes, chart)
    }



    private fun setLineChartData(lineValues: ArrayList<Entry>, color: Int): LineDataSet {
        val lineDataset = LineDataSet(lineValues, "Consommation")
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
    private fun drawChart(linesDataSet: List<LineDataSet>, chart: LineChart) {
        val data = LineData(linesDataSet)
        chart.data = data
        chart.animateXY(2000, 2000, Easing.EaseInCubic)

        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        chart.setTouchEnabled(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)

        val x: XAxis = chart.xAxis
        x.position = XAxis.XAxisPosition.BOTTOM

        x.setDrawAxisLine(true)
        x.setDrawGridLines(false)
        x.textColor = getColor(R.color.black)
        x.axisLineColor = getColor(R.color.black)

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