package fr.isen.francoisyatta.projectv2.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.isen.francoisyatta.projectv2.databinding.FragmentAnneeBinding


class Annee : Fragment() {

    private lateinit var binding: FragmentAnneeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnneeBinding.inflate(inflater, container, false)
        return binding.root
    }
    fun getFragmentAnneeBinding(): FragmentAnneeBinding {
        return binding
    }
}

