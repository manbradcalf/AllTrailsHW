package com.benmedcalf.alltrailshomework

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.benmedcalf.alltrailshomework.databinding.ActivityMainBinding
import com.benmedcalf.alltrailshomework.viewmodel.MapViewModel
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mapViewModel: MapViewModel

    //region LifeCycle Callbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        binding.toggleViewFab.setOnClickListener {
            //TODO("How to make this toggle button aware which fragment is active")

        }
    }
    //endregion
}
