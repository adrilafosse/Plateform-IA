package fr.isen.francoisyatta.projectv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import fr.isen.francoisyatta.projectv2.databinding.ActivityBilancarboneBinding

class bilancarbone : AppCompatActivity() {

    private lateinit var binding: ActivityBilancarboneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilancarbone)

        binding = ActivityBilancarboneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, chartCarbone::class.java)
        binding.FirstImage.setOnClickListener {
                startActivity(intent)
            }

            binding.SecondImage.setOnClickListener {
                startActivity(intent)
            }
            binding.ThirdImage.setOnClickListener {
                startActivity(intent)
            }

            binding.FourthImage.setOnClickListener {
                startActivity(intent)
            }

            binding.FiveImage.setOnClickListener {
                startActivity(intent)
            }

            binding.SixImage.setOnClickListener {
                startActivity(intent)
            }


        }
    }
