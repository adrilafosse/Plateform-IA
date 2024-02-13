package fr.isen.francoisyatta.projectv2.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import fr.isen.francoisyatta.projectv2.R
import fr.isen.francoisyatta.projectv2.databinding.FragmentJourBinding
import fr.isen.francoisyatta.projectv2.databinding.FragmentMoisBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Mois : Fragment() {

    private lateinit var binding: FragmentMoisBinding
    private lateinit var dateFormatee: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var dateDuJour = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MM")
        dateFormatee = dateDuJour.format(formatter)

        binding = FragmentMoisBinding.inflate(inflater, container, false)

        val button1 = binding.root.findViewById<AppCompatImageButton>(R.id.imageButton1)
        val button2 = binding.root.findViewById<AppCompatImageButton>(R.id.imageButton2)

        var mois : String
        button1.setOnClickListener {
            dateDuJour = dateDuJour.minusMonths(1)
            dateFormatee = dateDuJour.format(formatter)
            // Mettre à jour le titre avec la nouvelle date formatée
            if(dateFormatee == "01"){
                mois = "Janvier"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "02"){
                mois ="Février"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "03"){
                mois ="Mars"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "04"){
                mois ="Avril"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "05"){
                mois ="Mai"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "06"){
                mois ="Juin"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "07"){
                mois ="Juillet"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "08"){
                mois ="Aout"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "09"){
                mois ="Septembre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "10"){
                mois ="Octobre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "11"){
                mois ="Novembre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "12"){
                mois ="Décembre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
        }
        button2.setOnClickListener {
            dateDuJour = dateDuJour.plusMonths(1)
            dateFormatee = dateDuJour.format(formatter)
            // Mettre à jour le titre avec la nouvelle date formatée
            if(dateFormatee == "01"){
                mois = "Janvier"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "02"){
                mois ="Février"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "03"){
                mois ="Mars"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "04"){
                mois ="Avril"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "05"){
                mois ="Mai"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "06"){
                mois ="Juin"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "07"){
                mois ="Juillet"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "08"){
                mois ="Aout"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "09"){
                mois ="Septembre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "10"){
                mois ="Octobre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "11"){
                mois ="Novembre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
            else if(dateFormatee == "12"){
                mois ="Décembre"
                binding.titreGraph.text = "Ma consommation (${mois})"
            }
        }
        if(dateFormatee == "01"){
            mois = "Janvier"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "02"){
            mois ="Février"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "03"){
            mois ="Mars"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "04"){
            mois ="Avril"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "05"){
            mois ="Mai"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "06"){
            mois ="Juin"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "07"){
            mois ="Juillet"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "08"){
            mois ="Aout"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "09"){
            mois ="Septembre"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "10"){
            mois ="Octobre"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "11"){
            mois ="Novembre"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        else if(dateFormatee == "12"){
            mois ="Décembre"
            binding.titreGraph.text = "Ma consommation (${mois})"
        }
        return binding.root
    }

    fun getFragmentMoisBinding(): FragmentMoisBinding {
        return binding
    }
    fun getDateFormatee(): String {
        return dateFormatee
    }
}


