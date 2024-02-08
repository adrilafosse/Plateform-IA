package fr.isen.francoisyatta.projectv2.fragment

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import fr.isen.francoisyatta.projectv2.databinding.FragmentAnneeBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Annee : Fragment() {

    private lateinit var binding: FragmentAnneeBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val dateFormatee = dateDuJour.format(formatter)
        binding = FragmentAnneeBinding.inflate(inflater, container, false)
        binding.titreGraph.text = "Ma consommation (${dateFormatee})"
        return binding.root
    }
    fun getFragmentAnneeBinding(): FragmentAnneeBinding {
        return binding
    }
}

