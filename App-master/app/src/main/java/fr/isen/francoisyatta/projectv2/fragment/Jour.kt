package fr.isen.francoisyatta.projectv2.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.isen.francoisyatta.projectv2.R

import fr.isen.francoisyatta.projectv2.databinding.FragmentJourBinding

class Jour : Fragment() {

    private lateinit var binding: FragmentJourBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJourBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getFragmentJourBinding(): FragmentJourBinding {
        return binding
    }
}

