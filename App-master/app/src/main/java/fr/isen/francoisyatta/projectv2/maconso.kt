package fr.isen.francoisyatta.projectv2

import android.os.Bundle
import android.util.Log
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

class maconso : AppCompatActivity() {

    private lateinit var binding: ActivityMaconsoBinding
    private val consoHeure = ArrayList<Pair<Float, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maconso)  //liaison du code .kotlin à l'affichage layout .xml


        binding = ActivityMaconsoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val uid = currentUser.uid
            Log.d("Activite", "1: $uid")
            db.collection("id").document(uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            for (index in 1..4) {
                                val array = document.get("$index") as? ArrayList<*>

                                if (array != null && array.size == 2) {
                                    val numberValue = array[0] as? Number
                                    val stringValue = array[1] as? String

                                    if (numberValue != null && stringValue != null) {
                                        Log.d("Activite", "Valeur du tableau (Number): $numberValue, Valeur du tableau 'String': $stringValue")
                                        consoHeure.add(Pair(numberValue.toFloat(), stringValue))
                                    } else {
                                        Log.e("Activite", "Erreur: Le champ 'number' ou 'string' est nul pour l'index $index")
                                    }
                                } else {
                                    Log.e("Activite", "Erreur: Le tableau à l'index $index est incorrect ou nul")
                                }
                            }
                            initializeScreen()
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
        drawChart(graphLignes, binding.consoGraph)

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
            lineValues.add(Entry(index.toFloat() * 5, pair.first))
        }

        return lineValues
    }

}