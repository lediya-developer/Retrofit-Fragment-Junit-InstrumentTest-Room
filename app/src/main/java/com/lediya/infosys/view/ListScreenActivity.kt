package com.lediya.infosys.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onMenuHomePressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onMenuHomePressed() {
        onBackPressed()
    }
}