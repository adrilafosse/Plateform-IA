package fr.isen.francoisyatta.projectv3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profil.*


class ProfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val Button5: Button = findViewById(R.id.button5)
        val editText_nouveau_prixHC = findViewById<EditText>(R.id.editText_nouveau_prixHC)
        val editText_nouveau_prixHP = findViewById<EditText>(R.id.editText_nouveau_prixHP)
        val editText_nouveau_prix = findViewById<EditText>(R.id.editText_nouveau_prix)
        val editText_puissance_souscrite = findViewById<EditText>(R.id.editText_puissance_souscrite)
        val editText_prix_abonnement = findViewById<EditText>(R.id.editText_prix_abonnement)
        val editText_prix_abonnement_HP_HC = findViewById<EditText>(R.id.editText_prix_abonnement_HP_HC)

        val spinner = findViewById<Spinner>(R.id.spinner_profil)

        val textView_prix_fixeHP = findViewById<TextView>(R.id.textView_prix_fixeHP)
        val textView_prix_fixe = findViewById<TextView>(R.id.textView_prix_fixe)
        val textView_prix_fixeHC = findViewById<TextView>(R.id.textView_prix_fixeHC)
        val textView_puissance_souscrite = findViewById<TextView>(R.id.textView_puissance_souscrite)
        val textView_prix_abonnement= findViewById<TextView>(R.id.textView_prix_abonnement)
        val textView_prix_abonnement_HP_HC= findViewById<TextView>(R.id.textView_prix_abonnement_HP_HC)

        val options = arrayOf("Classique", "Heure pleine/creuse")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_profil.adapter = adapter

        val db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val uid = currentUser?.uid
        val profilRef = uid?.let { db.collection("id").document(it).collection("profil") }

        var option: String
        var prix= 0.0
        var prixHP= 0.0
        var prixHC= 0.0
        var puissance = 0.0
        var abonnement = 0.0
        var abonnement_HP_HC = 0.0
        profilRef?.get()?.addOnSuccessListener { documents ->
            for (document in documents) {
                option = document.getString("option").toString()
                prix = document.getDouble("prix") ?: 0.0
                prixHP = document.getDouble("prixHP") ?: 0.0
                prixHC = document.getDouble("prixHC") ?: 0.0
                puissance = document.getDouble("puissance_souscrite") ?: 0.0
                abonnement = document.getDouble("prix_abonnement") ?: 0.0
                abonnement_HP_HC = document.getDouble("prix_abonnement_HP_HC") ?: 0.0

                Log.d(
                    "option/firebase/prix profil",
                    "option: $option, prix : $prix, prixHP: $prixHP, prixHC: $prixHC, puissance_souscrite : $puissance, abonnement : $abonnement"
                )
                textView_prix_fixe.text="Prix classique : $prix € par kwh"
                textView_prix_fixeHP.text="Prix HP : $prixHP € par kwh"
                textView_prix_fixeHC.text="Prix HC : $prixHC € par kwh"
                textView_prix_abonnement.text="Prix abonnement classique : $abonnement € par mois"
                textView_puissance_souscrite.text="Puissance souscrite : $puissance kVA"
                textView_prix_abonnement_HP_HC.text="Prix abonnement HP/HC : $abonnement_HP_HC € par mois"
            }
        }

        var selectedOption =""
        spinner_profil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedOption = options[position]
                if (selectedOption == "Heure pleine/creuse") {
                        textView_prix_fixeHP.visibility = View.VISIBLE
                        editText_nouveau_prixHP.visibility = View.VISIBLE

                        textView_prix_fixeHC.visibility = View.VISIBLE
                        editText_nouveau_prixHC.visibility = View.VISIBLE

                        textView_prix_fixe.visibility = View.GONE
                        editText_nouveau_prix.visibility = View.GONE

                        textView_prix_abonnement_HP_HC.visibility = View.VISIBLE
                        editText_prix_abonnement_HP_HC.visibility = View.VISIBLE

                        textView_prix_abonnement.visibility = View.GONE
                        editText_prix_abonnement.visibility = View.GONE

                    } else {
                        textView_prix_fixeHP.visibility = View.GONE
                        editText_nouveau_prixHP.visibility = View.GONE

                        textView_prix_fixeHC.visibility = View.GONE
                        editText_nouveau_prixHC.visibility = View.GONE

                        textView_prix_fixe.visibility = View.VISIBLE
                        editText_nouveau_prix.visibility = View.VISIBLE

                        textView_prix_abonnement_HP_HC.visibility = View.GONE
                        editText_prix_abonnement_HP_HC.visibility = View.GONE

                        textView_prix_abonnement.visibility = View.VISIBLE
                        editText_prix_abonnement.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Faire quelque chose si rien n'est sélectionné
                }
        }

        Log.d("Spinner2", "Valeur sélectionnée : $selectedOption")
        Button5.setOnClickListener {
            val editText_nouveau_prixString = editText_nouveau_prix.text.toString()
            val editText_nouveau_prixHPString = editText_nouveau_prixHP.text.toString()
            val editText_nouveau_prixHCString = editText_nouveau_prixHC.text.toString()
            val editText_puissance_souscriteString = editText_puissance_souscrite.text.toString()
            val editText_prix_abonnementString = editText_prix_abonnement.text.toString()
            val editText_prix_abonnementHPHCString = editText_prix_abonnement_HP_HC.text.toString()


            val profilRef = uid?.let { db.collection("id").document(it).collection("profil") }
            profilRef?.get()?.addOnSuccessListener { documents ->
                for (document in documents) {

                    val dataToUpdate = HashMap<String, Any>()

                    if (selectedOption == "Classique") {
                        Log.d("Classique", "Classique")
                        dataToUpdate["option"] = selectedOption
                        dataToUpdate["prix"] = editText_nouveau_prixString.toDoubleOrNull() ?: 0.0
                        dataToUpdate["puissance_souscrite"] = editText_puissance_souscriteString.toDoubleOrNull() ?: 0.0
                        dataToUpdate["prix_abonnement"] = editText_prix_abonnementString.toDoubleOrNull() ?: 0.0

                    } else if (selectedOption == "Heure pleine/creuse") {
                        dataToUpdate["option"] = selectedOption
                        dataToUpdate["prixHP"] = editText_nouveau_prixHPString.toDoubleOrNull() ?: 0.0
                        dataToUpdate["prixHC"] = editText_nouveau_prixHCString.toDoubleOrNull() ?: 0.0
                        dataToUpdate["puissance_souscrite"] = editText_puissance_souscriteString.toDoubleOrNull() ?: 0.0
                        dataToUpdate["prix_abonnement_HP_HC"] = editText_prix_abonnementHPHCString.toDoubleOrNull() ?: 0.0
                    }


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