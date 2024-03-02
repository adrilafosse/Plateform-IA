package fr.isen.francoisyatta.projectv3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit;



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  //liaison du code .kotlin à l'affichage layout .xml

        val arrayList = ArrayList<Model>()

        // affichage des différents éléments du menu
        arrayList.add(Model("Ma consommation", "Afficher votre consommation électrique", R.drawable.maconso))
        arrayList.add(Model("Rapport d'activité", "Conseil pour mieux gérer votre consommation", R.drawable.eclair))
        arrayList.add(Model("Profil", "Modifier votre profil", R.drawable.img))
        // arrayList.add(Model("Maintenance prédictive", "Afficher les améliorations possibles", R.drawable.maintenance))
        arrayList.add(Model("Aide", "Un problème ? Contactez nous", R.drawable.aide))
        //arrayList.add(Model("BLE", "Connexion BLE", R.drawable.bluetooth))

        val myAdapter = MyAdapter(arrayList, this)

        recyclerView.layoutManager = LinearLayoutManager (this)
        recyclerView.adapter = myAdapter

        // Créer une contrainte pour exécuter la tâche uniquement lorsque le réseau est disponible
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Crée une tâche périodique avec WorkManager
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            WorkClass::class.java,
            60, // Répéter toutes les 60 minutes
            TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        // Planifier la tâche périodique avec WorkManager
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }
}