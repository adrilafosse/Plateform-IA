package fr.isen.francoisyatta.projectv3

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

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

    private fun decryptAES(encrypted: String, key: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")

        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT))
        return String(decryptedBytes)
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
                                        val key = "603DEB1015CA71BE2B73AEF0857D7781"
                                        Log.d("value", "1: $value")
                                        if (value != null) {
                                            val decryptedValue = decryptAES(value, key)
                                            Log.d("valeur decryptée", "1: $decryptedValue")
                                            monTableau.add(decryptedValue)
                                            Log.d("valeur du tableau", "1: ${monTableau[i]}")
                                        }
                                    }
                                    val tableaustring = monTableau.toTypedArray()
                                    //on decoupe de string en 3 parties
                                    tableaufinal = tableaustring.map { chaine ->
                                        val conso = charArrayOf(chaine[12],chaine[13], chaine[14], chaine[15],chaine[16],chaine[17],chaine[18],chaine[19],chaine[20])
                                        val heureChar = charArrayOf(chaine[6], chaine[7])
                                        val minutesChar = charArrayOf(chaine[8], chaine[9])
                                        val annee = charArrayOf(chaine[0], chaine[1], chaine[2],chaine[3],'2','0',chaine[4],chaine[5])
                                        val anneeTotal = charArrayOf(chaine[0], chaine[1],'/', chaine[2],chaine[3],'/','2','0',chaine[4],chaine[5])
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
                                    donneeJour()
                                }
                            }
                        }
                    }
            }
        }
    }
    private fun donneeJour() {
        val consoParJour = mutableMapOf<String, Int>()
        val prixParJour = mutableMapOf<String, Double>()
        val prixParJourHP_HC = mutableMapOf<String, Double>()

        for (ligne in tableaufinal) {
            val date = ligne[3].joinToString("")
            val consoString = ligne[0].joinToString("").trim()
            val conso = consoString.toIntOrNull() ?: continue

            if (consoParJour.containsKey(date)) {
                consoParJour[date] = consoParJour[date]!! + conso
            } else {
                consoParJour[date] = conso
            }
        }

        for ((date, consoTotalJour) in consoParJour) {
            Log.d("consoTotalJour", "consoTotalJour: $consoTotalJour, date: $date")
        }
        val moyenneRef = uid?.let { db.collection("id").document(it).collection("moyenne") }
        moyenneRef?.document("consoParJour")?.set(consoParJour)?.addOnSuccessListener {
            Log.d(
                "Firestore",
                "Données consoParJour ajoutées avec succès à la sous-collection moyenne"
            )
        }?.addOnFailureListener { e ->
            Log.e(
                "Firestore",
                "Erreur lors de l'ajout des données consoParJour à la sous-collection moyenne",
                e
            )
        }

        val profilRef = uid?.let { db.collection("id").document(it).collection("profil") }
        var option: String
        var prix: Double
        var prixHP : Double
        var prixHC : Double

        profilRef?.get()?.addOnSuccessListener { documents ->
            for (document in documents) {
                option = document.getString("option").toString()
                prix = document.getDouble("prix") ?: 0.0
                prixHP  = document.getDouble("prixHP") ?: 0.0
                prixHC =  document.getDouble("prixHC") ?: 0.0
                Log.d("option/firebase/prix", "option: $option, prix : $prix, prixHP: $prixHP, prixHC: $prixHC")

                for (ligne in tableaufinal) {
                    val heureChar = ligne[1].joinToString("")
                    Log.d("heureChar debile", "heureChar: $heureChar")
                    val heure = heureChar.replace(",", ".").toFloatOrNull() ?: 0.0f

                    Log.d("heure debile", "heure: $heure")
                    val date = ligne[3].joinToString("")
                    val consoString = ligne[0].joinToString("").trim()
                    val conso = consoString.toIntOrNull() ?: continue
                    Log.d("conso debile", "conso: $conso")

                    //prix constant
                    val prixFinal = (conso * prix) / 1000.0
                    Log.d("prixFinal debile", "prixFinal: $prixFinal")
                    // val df = DecimalFormat("#.###")
                    // df.roundingMode = RoundingMode.HALF_UP
                    // val prixFinalFormatted = df.format(prixFinal).toDouble()
                    // Log.d("prixFinalFormatted debile", "prixFinalFormatted: $prixFinalFormatted")

                    if (prixParJour.containsKey(date)) {
                         val prixExistant = prixParJour[date]!!
                         val nouveauPrix = prixFinal + prixExistant
                         //prixParJour[date] = DecimalFormat("#.###").format(nouveauPrix).toDouble()
                        prixParJour[date] = nouveauPrix
                    } else {
                         prixParJour[date] = prixFinal
                    }

                    var prixFinalFormattedHP_HC = 0.0
                    //prix HP/HC
                    if (heure != null) {
                        if(heure > 6 && heure < 22) {
                            prixFinalFormattedHP_HC = (conso * prixHP) / 1000.0
                            // val df = DecimalFormat("#.###")
                            // df.roundingMode = RoundingMode.HALF_UP
                            //prixFinalFormattedHP_HC = df.format(prixFinalHP).toDouble()
                            Log.d("prixFinalFormattedHP_HC", "prixFinalFormattedHP_HC: $prixFinalFormattedHP_HC")
                        }
                        else{
                            prixFinalFormattedHP_HC = (conso * prixHC) / 1000.0
                            //val df = DecimalFormat("#.###")
                            //df.roundingMode = RoundingMode.HALF_UP
                            //rixFinalFormattedHP_HC = df.format(prixFinalHC).toDouble()
                            Log.d("prixFinalFormattedHP_HC", "prixFinalFormattedHP_HC: $prixFinalFormattedHP_HC")
                        }

                    }
                    Log.d("prixFinalFormattedHP_HC", "prixFinalFormattedHP_HC: $prixFinalFormattedHP_HC")
                    if (prixParJourHP_HC.containsKey(date)) {
                        val prixExistant = prixParJourHP_HC[date]!!
                        val nouveauPrix = prixFinalFormattedHP_HC + prixExistant
                        prixParJourHP_HC[date] = nouveauPrix
                    } else {
                        prixParJourHP_HC[date] = prixFinalFormattedHP_HC
                    }
                }

                val moyenneRef = uid?.let { db.collection("id").document(it).collection("moyenne") }
                moyenneRef?.document("prixParJour")?.set(prixParJour)?.addOnSuccessListener {
                    Log.d(
                        "Firestore",
                        "Données prixParJour ajoutées avec succès à la sous-collection moyenne"
                    )
                }?.addOnFailureListener { e ->
                    Log.e(
                        "Firestore",
                        "Erreur lors de l'ajout des données prixParJour à la sous-collection moyenne",
                        e
                    )
                }
                moyenneRef?.document("prixParJourHP_HC")?.set(prixParJourHP_HC)?.addOnSuccessListener {
                    Log.d(
                        "Firestore",
                        "Données prixParJourHP_HC ajoutées avec succès à la sous-collection moyenne"
                    )
                }?.addOnFailureListener { e ->
                    Log.e(
                        "Firestore",
                        "Erreur lors de l'ajout des données prixParJourHP_HC à la sous-collection moyenne",
                        e
                    )
                }
            }
        }
    }
}

