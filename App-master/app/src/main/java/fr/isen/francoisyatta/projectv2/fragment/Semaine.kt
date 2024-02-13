package fr.isen.francoisyatta.projectv2.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import fr.isen.francoisyatta.projectv2.R
import fr.isen.francoisyatta.projectv2.databinding.FragmentMoisBinding
import fr.isen.francoisyatta.projectv2.databinding.FragmentSemaineBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Semaine : Fragment() {

    private lateinit var binding: FragmentSemaineBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateFormatee = dateDuJour.format(formatter)

        binding = FragmentSemaineBinding.inflate(inflater, container, false)
        binding.titreGraph.text = "Semaine du (${dateFormatee})"
        return binding.root
    }

    fun getFragmentSemaineBinding(): FragmentSemaineBinding {
        return binding
    }
}


