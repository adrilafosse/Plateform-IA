package fr.isen.francoisyatta.projectv3

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RapportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rapport)
        val cardViewJours5max: CardView = findViewById(R.id.Jours5max)
        val cardViewComparaison: CardView = findViewById(R.id.comparaison)

        val Button: Button = findViewById(R.id.button5)
        var jour1: TextView = cardViewJours5max.findViewById(R.id.textView1)
        val jour2: TextView = cardViewJours5max.findViewById(R.id.textView2)
        val jour3: TextView = cardViewJours5max.findViewById(R.id.textView3)
        val jour4: TextView = cardViewJours5max.findViewById(R.id.textView4)
        val jour5: TextView = cardViewJours5max.findViewById(R.id.textView5)

        val classique: TextView = cardViewComparaison.findViewById(R.id.prix_classique)
        val HP_HC: TextView = cardViewComparaison.findViewById(R.id.prix_HP_HC)
        val vainqueur: TextView = cardViewComparaison.findViewById(R.id.vainqueur_Forfait)

        val db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val uid = currentUser?.uid

        if (currentUser != null) {
            // on récupère les données de la collection id qui a pour id l'uid de l'utilisateur
            if (uid != null) {
                db.collection("id").document(uid).collection("moyenne").document("consoParJour")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document != null && document.exists()) {
                                //on recupere les données
                                val dataConsoParJour = document.data
                                Log.d("donnée de firebase moyenne conso par jour", "1: $dataConsoParJour")
                                if(dataConsoParJour != null){

                                    val dataList = dataConsoParJour.toList()

                                    val sortedDataList = dataList.sortedByDescending { it.second as Long }

                                    val topFiveData = sortedDataList.take(5)
                                    jour1.text ="${topFiveData[0].first} : ${topFiveData[0].second}"
                                    jour2.text ="${topFiveData[1].first} : ${topFiveData[1].second}"
                                    jour3.text ="${topFiveData[2].first} : ${topFiveData[2].second}"
                                    jour4.text ="${topFiveData[3].first} : ${topFiveData[3].second}"
                                    jour5.text ="${topFiveData[4].first} : ${topFiveData[4].second}"
                                    for ((date, consommation) in topFiveData) {
                                        Log.d("Top 5 consommations", "Date: $date, Consommation: $consommation")
                                    }
                                }
                            }

                        }
                    }
                var prixTotal = 0.0
                db.collection("id").document(uid).collection("moyenne").document("prixParJour")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document != null && document.exists()) {
                                // On récupère les données
                                val dataPrixParJour = document.data
                                Log.d("donnée de firebase moyenne prix par jour", "1: $dataPrixParJour")

                                if(dataPrixParJour != null){
                                    // Parcour les données et additionne chaque prix
                                    for ((cle, valeur) in dataPrixParJour) {
                                        if (valeur is Number) {
                                            prixTotal += valeur.toDouble()
                                        }
                                    }
                                    classique.text = "Prix total forfait classique : ${String.format("%.2f", prixTotal)}"
                                    Log.d("Prix total", prixTotal.toString())
                                }
                            }
                        }
                        var prixTotal_HP_HC = 0.0
                        db.collection("id").document(uid).collection("moyenne").document("prixParJourHP_HC")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document != null && document.exists()) {
                                        // On récupère les données
                                        val dataPrixParJour = document.data
                                        Log.d("donnée de firebase moyenne prix par jour HP_HC", "1: $dataPrixParJour")

                                        if(dataPrixParJour != null){

                                            // Parcour les données et additionne chaque prix
                                            for ((cle, valeur) in dataPrixParJour) {
                                                if (valeur is Number) {
                                                    prixTotal_HP_HC += valeur.toDouble()
                                                }
                                            }
                                            HP_HC.text = "Prix total forfait HP_HC 1: ${String.format("%.2f", prixTotal_HP_HC)}"
                                        }
                                    }
                                }
                                if(prixTotal_HP_HC > prixTotal ){
                                    vainqueur.text="Forfait classique est plus avantageux"
                                }
                                else{
                                    vainqueur.text="Forfait HP/HC est plus avantageux"
                                }

                            }
                    }

            }

        }
        Button.setOnClickListener {
            val prix_seuil = findViewById<EditText>(R.id.nouveauprixseuil).text.toString()

            val profilRef = uid?.let { db.collection("id").document(it).collection("profil") }
            profilRef?.get()?.addOnSuccessListener { documents ->
                for (document in documents) {
                    val dataToUpdate = HashMap<String, Any>()
                    dataToUpdate["seuil"] = prix_seuil.toDoubleOrNull() ?: 0.0
                    document.reference.update(dataToUpdate)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Document mis à jour avec succès")
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Succès")
                            builder.setMessage("Les données ont été enregistrées avec succès!")
                            builder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Erreur lors de la mise à jour du document", e)
                        }
                }
            }
        }

    }
}