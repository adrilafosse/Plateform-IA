package fr.isen.francoisyatta.projectv2
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


public class chartCarbone: AppCompatActivity() {
    var cafetiere: TextView? = null
    var lavelinge:TextView? = null
    var lavevaisselle:TextView? = null
    var four:TextView? = null
    var pieChart: PieChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// Link those objects with their
// respective id's that
// we have given in .XML file
        cafetiere = findViewById<TextView>(R.id.fou)
        lavelinge = findViewById<TextView>(R.id.lave)
        lavevaisselle = findViewById<TextView>(R.id.linge)
        four = findViewById<TextView>(R.id.ref)
        pieChart = findViewById<PieChart>(R.id.piechart)

// Creating a method setData()
// to set the text in text view and pie chart
        setData()
    }
    private fun setData() {

// Set the percentage of language used
        cafetiere!!.text = Integer.toString(40)
        lavelinge!!.text = Integer.toString(30)
        lavevaisselle!!.text = Integer.toString(5)
        four!!.text = Integer.toString(25)

// Set the data and color to the pie chart
        pieChart!!.addPieSlice(
            PieModel(
                "R", cafetiere!!.text.toString().toInt().toFloat(),
                Color.parseColor("#FFA726")
            )
        )
        pieChart!!.addPieSlice(
            PieModel(
                "Python", lavelinge!!.text.toString().toInt().toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        pieChart!!.addPieSlice(
            PieModel(
                "C++", lavevaisselle!!.text.toString().toInt().toFloat(),
                Color.parseColor("#EF5350")
            )
        )
        pieChart!!.addPieSlice(
            PieModel(
                "Java", four!!.text.toString().toInt().toFloat(),
                Color.parseColor("#29B6F6")
            )
        )

// To animate the pie chart
        pieChart!!.startAnimation()
    }


}