package com.lediya.infosys.view

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.lediya.infosys.R
import com.lediya.infosys.databinding.ActivityListScreenBinding
import com.lediya.infosys.view.viewModel.ListScreenViewModel

class ListScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListScreenBinding
    private lateinit var viewModel: ListScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_screen)
        viewModel =
            ViewModelProviders.of(this).get(ListScreenViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}