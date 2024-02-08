package fr.isen.francoisyatta.projectv2.fragment

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi

import fr.isen.francoisyatta.projectv2.R

import fr.isen.francoisyatta.projectv2.databinding.FragmentJourBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Jour : Fragment() {

    private lateinit var binding: FragmentJourBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateFormatee = dateDuJour.format(formatter)
        // Inflate the layout for this fragment
        binding = FragmentJourBinding.inflate(inflater, container, false)
        binding.titreGraph.text = "Ma consommation (${dateFormatee})"
        return binding.root
    }

    fun getFragmentJourBinding(): FragmentJourBinding {
        return binding
    }
}

