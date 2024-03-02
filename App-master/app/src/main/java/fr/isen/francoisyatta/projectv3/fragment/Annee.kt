package fr.isen.francoisyatta.projectv3.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import fr.isen.francoisyatta.projectv3.R
import fr.isen.francoisyatta.projectv3.databinding.FragmentAnneeBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Annee : Fragment() {

    private lateinit var binding: FragmentAnneeBinding
    private lateinit var dateFormatee : String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy")

        dateFormatee = dateDuJour.format(formatter)

        binding = FragmentAnneeBinding.inflate(inflater, container, false)
        binding.titreGraph.text = "$dateFormatee"

        val button1 = binding.root.findViewById<AppCompatImageButton>(R.id.imageButton1)
        val button2 = binding.root.findViewById<AppCompatImageButton>(R.id.imageButton2)

        button1.setOnClickListener {
            dateDuJour = dateDuJour.minusYears(1)
            dateFormatee = dateDuJour.format(formatter)
            // Mettre à jour le titre avec la nouvelle date formatée
            binding.titreGraph.text = "$dateFormatee"
        }
        button2.setOnClickListener {
            dateDuJour = dateDuJour.plusYears(1)
            dateFormatee = dateDuJour.format(formatter)
            // Mettre à jour le titre avec la nouvelle date formatée
            binding.titreGraph.text = "$dateFormatee"
        }
        return binding.root
    }
    fun getFragmentAnneeBinding(): FragmentAnneeBinding {
        return binding
    }
    fun getDateFormatee(): String {
        return dateFormatee
    }
}

