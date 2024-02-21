package fr.isen.francoisyatta.projectv3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        // Ajoutez un log pour vérifier que la tâche se lance correctement
        Log.d("WorkerClass", "La tâche WorkManager se lance maintenant !")
        recupérationDonnées()
        notif()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notif(){
        val profilRef = uid?.let { db.collection("id").document(it).collection("profil") }

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
                                Log.d(
                                    "donnée de firebase moyenne conso par jour",
                                    "1: $dataConsoParJour"
                                )
                                profilRef?.get()?.addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        var seuil = document.getDouble("seuil") ?: 0.0
                                        Log.d(
                                            "seuil 1",
                                            "1: $seuil"
                                        )
                                        val dataList = dataConsoParJour?.toList()
                                        if (dataList != null) {
                                            for (pair in dataList) {
                                                val date = pair.first
                                                val consommation = pair.second?.toString()?.toDoubleOrNull()
                                                Log.d("Données de consommation", "Date: $date, Consommation: $consommation")
                                                if (consommation != null) {
                                                    var dateDuJour = LocalDate.now()
                                                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                                    var dateFormatee = dateDuJour.format(formatter)

                                                    if(consommation > seuil && date == dateFormatee){
                                                        //envoyer une notif
                                                        Log.d("notif", "Date: $date, Consommation: $consommation, seuil: $seuil")
                                                        afficherNotification(applicationContext,date,consommation,seuil)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }

                            }

                        }
                    }
            }
        }
    }
    fun afficherNotification(context: Context, date: String, consommation: Double, seuil: Double) {
        val channelId = "id"

        // Créer un gestionnaire de notifications
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Créer un canal de notification (nécessaire pour les versions Android Oreo et supérieures)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertes de consommation",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construire la notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.warning)
            .setContentTitle("Alerte de consommation")
            .setContentText("La consommation pour la date $date dépasse le seuil. Consommation: $consommation kWh, Seuil: $seuil kWh")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(123, builder.build())
    }
}

