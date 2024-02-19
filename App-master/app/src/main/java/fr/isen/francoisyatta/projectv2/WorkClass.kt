package fr.isen.francoisyatta.projectv2

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class WorkClass(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    private var taille: Int = 0
    private var tableaufinal: MutableList<Array<CharArray>> = mutableListOf()
    val db = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val uid = currentUser?.uid

    override fun doWork(): Result {
        // Ajoutez un log pour vérifier que la tâche se lance correctement
        Log.d("WorkerClass", "La tâche WorkManager se lance maintenant !")
        recupérationDonnées()
        // Ajoutez un log pour indiquer que la tâche est terminée
        Log.d("WorkerClass", "La tâche WorkManager est terminée !")

        return Result.success()
    }

    private fun recupérationDonnées() {

        if (currentUser != null) {
            // on récupère les données de la collection id qui a pour id l'uid de l'utilisateur
            if (uid != null) {
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
                                    Log.d("taille2", "1: $taille")
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
                                        val heureChar = charArrayOf(chaine[5], chaine[6])
                                        val minutesChar = charArrayOf(chaine[8], chaine[9])
                                        val annee = charArrayOf(
                                            chaine[11],
                                            chaine[12],
                                            chaine[14],
                                            chaine[15],
                                            chaine[17],
                                            chaine[18],
                                            chaine[19],
                                            chaine[20]
                                        )
                                        val anneeTotal = charArrayOf(
                                            chaine[11],
                                            chaine[12],
                                            chaine[13],
                                            chaine[14],
                                            chaine[15],
                                            chaine[16],
                                            chaine[17],
                                            chaine[18],
                                            chaine[19],
                                            chaine[20]
                                        )
                                        val minutesString = minutesChar.joinToString("")
                                        val minutesFloat = minutesString.toFloatOrNull() ?: 0.0f
                                        val minuteBase10 = String.format("%.2f", minutesFloat / 60)

                                        val minuteBase10Char = minuteBase10.toCharArray()
                                        val deuxDernierChiffres = charArrayOf(minuteBase10Char[1],minuteBase10Char[2],minuteBase10Char[3])
                                        val heureMinute = heureChar + deuxDernierChiffres
                                        arrayOf(conso, heureMinute, annee, anneeTotal)

                                    }.toMutableList()
                                    for (i in 0 until taille) {
                                        Log.d("Tableaufinal", "Conso: ${tableaufinal[i][0].joinToString("")}, heureMinute: ${tableaufinal[i][1].joinToString("")}, annee: ${tableaufinal[i][2].joinToString("")}, anneeTotal: ${tableaufinal[i][3].joinToString("")}")
                                    }
                                    Log.d("taille7", "1: $taille")
                                    donneeJour()
                                }
                            }
                        }
                    }
            }
        }
    }
    private fun donneeJour()
    {
        val consoParJour = mutableMapOf<String, Int>()

        // Parcourir le tableau des données
        for (ligne in tableaufinal) {
            // Récupérer la date et la consommation de la ligne actuelle
            val date = ligne[3].joinToString("") // Index 2 pour la date
            val consoString = ligne[0].joinToString("").trim() // Supprimer les espaces blancs
            val conso = consoString.toIntOrNull() ?: continue // Convertir en entier, en évitant les valeurs non valides
            // Si la date est déjà présente dans la map, ajouter la consommation à la consommation existante
            if (consoParJour.containsKey(date)) {
                consoParJour[date] = consoParJour[date]!! + conso
            } else {
                // Sinon, ajouter une nouvelle entrée dans la map avec la consommation actuelle
                consoParJour[date] = conso
            }
        }

        // Afficher les résultats dans les logs
        for ((date, consoTotalJour) in consoParJour) {
            Log.d("consoTotalJour", "consoTotalJour: $consoTotalJour, date: $date")
        }
        val moyenneRef = uid?.let { db.collection("id").document(it).collection("moyenne") }
        if (moyenneRef != null) {
            moyenneRef.document("consoParJour").set(consoParJour)
                .addOnSuccessListener {
                    Log.d("Firestore", "Données consoParJour ajoutées avec succès à la sous-collection moyenne")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Erreur lors de l'ajout des données consoParJour à la sous-collection moyenne", e)
                }
        }
    }


}

