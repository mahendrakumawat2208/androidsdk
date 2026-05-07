package com.example.androidinstrumentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidinstrumentation.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLoadFirstApi.setOnClickListener {
            viewModel.loadFirstApiAndNavigate()
        }
        binding.buttonCrash.setOnClickListener {
            throw RuntimeException("Intentional crash triggered by Crash button")
        }

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { render(it) }
                }
                launch {
                    viewModel.navigation.collectLatest { card ->
                        val intent = Intent(this@MainActivity, SecondActivity::class.java)
                            .putExtra(SecondActivity.EXTRA_META, card.meta)
                            .putExtra(SecondActivity.EXTRA_TITLE, card.title)
                            .putExtra(SecondActivity.EXTRA_BODY, card.body)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun render(state: MainViewModel.UiState) {
        val isLoading = state is MainViewModel.UiState.Loading
        binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLoadFirstApi.isEnabled = !isLoading

        val errorMessage = (state as? MainViewModel.UiState.Error)?.message
        binding.textError.visibility = if (errorMessage == null) View.GONE else View.VISIBLE
        binding.textError.text = errorMessage
    }
}
