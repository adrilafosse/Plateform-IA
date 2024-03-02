package fr.isen.francoisyatta.projectv3.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton

import fr.isen.francoisyatta.projectv3.R

import fr.isen.francoisyatta.projectv3.databinding.FragmentJourBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Jour : Fragment() {

    private lateinit var binding: FragmentJourBinding
    private lateinit var dateFormatee: String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        dateFormatee = dateDuJour.format(formatter)

        binding = FragmentJourBinding.inflate(inflater, container, false)
        binding.titreGraph.text = "$dateFormatee"

        // Références aux boutons
        val button1 = binding.root.findViewById<AppCompatImageButton>(R.id.imageButton1)
        val button2 = binding.root.findViewById<AppCompatImageButton>(R.id.imageButton2)



        button1.setOnClickListener {
            dateDuJour = dateDuJour.minusDays(1)
            dateFormatee = dateDuJour.format(formatter)
            // Mettre à jour le titre avec la nouvelle date formatée
            binding.titreGraph.text = "$dateFormatee"
        }
        button2.setOnClickListener {
            dateDuJour = dateDuJour.plusDays(1)
            dateFormatee = dateDuJour.format(formatter)
            // Mettre à jour le titre avec la nouvelle date formatée
            binding.titreGraph.text = "$dateFormatee"
        }
        return binding.root
    }

    fun getFragmentJourBinding(): FragmentJourBinding {
        return binding
    }
    fun getDateFormatee(): String {
        return dateFormatee
    }
}


