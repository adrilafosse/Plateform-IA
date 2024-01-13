package fr.isen.francoisyatta.projectv2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.isen.francoisyatta.projectv2.R
import fr.isen.francoisyatta.projectv2.databinding.FragmentJourBinding
import fr.isen.francoisyatta.projectv2.databinding.FragmentMoisBinding

class Mois : Fragment() {

    private lateinit var binding: FragmentMoisBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoisBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getFragmentMoisBinding(): FragmentMoisBinding {
        return binding
    }

}


