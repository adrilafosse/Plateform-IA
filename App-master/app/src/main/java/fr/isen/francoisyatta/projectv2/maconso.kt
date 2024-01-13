package fr.isen.francoisyatta.projectv2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.francoisyatta.projectv2.databinding.ActivityMaconsoBinding
import fr.isen.francoisyatta.projectv2.fragment.Annee
import fr.isen.francoisyatta.projectv2.fragment.Jour
import fr.isen.francoisyatta.projectv2.fragment.Mois
import fr.isen.francoisyatta.projectv2.fragment.Semaine
import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale



class maconso : AppCompatActivity() {

    private lateinit var binding: ActivityMaconsoBinding
    private lateinit var fragmentJour: Jour
    private val consoHeure = ArrayList<Pair<Float, Float>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maconso)

        binding = ActivityMaconsoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val Button1: Button = findViewById(R.id.button1)
        val Button2: Button = findViewById(R.id.button2)
        val Button3: Button = findViewById(R.id.button3)
        val Button4: Button = findViewById(R.id.button4)

        // Utilisez la propriété de classe fragmentJour au lieu d'une variable locale
        fragmentJour = Jour()
        val fragmentSemaine = Semaine()
        val fragmentMois = Mois()
        val fragmentAnnee = Annee()

        Button1.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragmentJour)
            transaction.commitNow() // Use commitNow to immediately execute the transaction

            // Call initializeScreen after the transaction is complete
            supportFragmentManager.executePendingTransactions()
            initializeScreen()
        }
        Button2.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentSemaine)
                .commit()
        }

        Button3.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentMois)
                .commit()
        }

        Button4.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentAnnee)
                .commit()
        }

        //val actionBar : ActionBar? = supportActionBar
        //actionBar!!.setDisplayHomeAsUpEnabled(true)
        //actionBar!!.setDisplayShowHomeEnabled(true)


        // prend les données depuis putExtra intent
        val intent = intent
        val aTitle = intent.getStringExtra("iTitle")

        //définit le titre dans une autre activité
        //actionBar.setTitle(aTitle)
        //aTitle.text = aTitle
        fetchDataAndFillList()
    }

    private fun fetchDataAndFillList() {
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
                            //on parcourt les tableaux en fonction de leur numéros
                            for (index in 1..4) {
                                val array = document.get("$index") as? ArrayList<*>

                                if (array != null && array.size == 3) {
                                    val conso = array[0] as? Number
                                    val heure = array[1] as? Number
                                    val timestamp = array[2] as? Timestamp

                                    //on divise la date et l'heure
                                    val date = timestamp?.toDate()
                                    val date2 = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                    val h2 = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                    val date3 = date?.let { date2.format(it) }
                                    val h3 = date?.let { h2.format(it) }
                                    val h4 = h3?.toCharArray()
                                    if (h4 != null) {

                                        val h5: Int = Integer.parseInt("${h4?.get(0)}${h4?.get(1)}${h4?.get(3)}${h4?.get(4)}${h4?.get(6)}${h4?.get(7)}")
                                        val h6 = h5.toFloat()

                                        if (conso != null && heure != null && date != null) {


                                            Log.d("ma conso date", "date: $date3")
                                            Log.d("ma conso heure 1", "heure: $h3")
                                            Log.d("ma conso heure 2", "heure: $h6")
                                            val h7 = h6/10000

                                            //on ajoute les données récuperé dans consoHeure
                                            consoHeure.add(Pair(conso.toFloat(), h7.toFloat()))
                                            Log.d(
                                                "conso data 2",
                                                "Valeur du tableau (Number): $consoHeure"
                                            )
                                        } else {
                                            Log.e(
                                                "Activite",
                                                "Erreur: Le champ 'number' ou 'string' est nul pour l'index $index"
                                            )
                                        }
                                    }
                                } else {
                                    Log.e("Activite", "Erreur: Le tableau à l'index $index est incorrect ou nul")
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
    private fun initializeScreen() {
        val consommation = setLineChartData(evolution_consommation(), R.color.bleusavee)
        val graphLignes = listOf<LineDataSet>(consommation)

        // Obtenez une référence au graphique du fragment
        val fragmentJourChart = fragmentJour.getFragmentJourBinding().consoGraph

        // Appelez drawChart avec le graphique du fragment
        drawChart(graphLignes, fragmentJourChart)
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
            Log.d("conso data 5", "lineValues: $lineValues")
        }

        return lineValues
    }
}