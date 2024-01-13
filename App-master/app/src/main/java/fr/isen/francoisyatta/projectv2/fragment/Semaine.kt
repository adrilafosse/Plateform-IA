package fr.isen.francoisyatta.projectv2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.isen.francoisyatta.projectv2.R
import fr.isen.francoisyatta.projectv2.databinding.FragmentMoisBinding
import fr.isen.francoisyatta.projectv2.databinding.FragmentSemaineBinding


class Semaine : Fragment() {

    private lateinit var binding: FragmentSemaineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSemaineBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getFragmentSemaineBinding(): FragmentSemaineBinding {
        return binding
    }
}


