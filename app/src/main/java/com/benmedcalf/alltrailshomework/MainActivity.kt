package com.benmedcalf.alltrailshomework

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.benmedcalf.alltrailshomework.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.detailFragment) {
                binding.searchFragmentContainerView.visibility = View.GONE
            } else {
                // TODO("avoid explicitly setting VISIBLE if not necessary")
                binding.searchFragmentContainerView.visibility = View.VISIBLE
            }
        }

    }
}
