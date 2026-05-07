package com.example.androidinstrumentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidinstrumentation.databinding.ActivitySecondBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_META = "extra_meta"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
    }

    private lateinit var binding: ActivitySecondBinding
    private val viewModel: SecondViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindFirstApiData()
        binding.buttonLoadSecondApi.setOnClickListener { viewModel.loadSecondApi() }
        binding.buttonPreviousPage.setOnClickListener { finish() }

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { render(it) }
                }
                launch {
                    viewModel.navigation.collectLatest { card ->
                        val intent = Intent(this@SecondActivity, ThirdActivity::class.java)
                            .putExtra(ThirdActivity.EXTRA_META, card.meta)
                            .putExtra(ThirdActivity.EXTRA_TITLE, card.title)
                            .putExtra(ThirdActivity.EXTRA_BODY, card.body)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun bindFirstApiData() {
        binding.textFirstMeta.text = intent.getStringExtra(EXTRA_META).orEmpty()
        binding.textFirstTitle.text = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        binding.textFirstBody.text = intent.getStringExtra(EXTRA_BODY).orEmpty()
    }

    private fun render(state: SecondViewModel.UiState) {
        val isLoading = state is SecondViewModel.UiState.Loading
        binding.progressSecondLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLoadSecondApi.isEnabled = !isLoading
        binding.buttonPreviousPage.isEnabled = !isLoading

        val errorMessage = (state as? SecondViewModel.UiState.Error)?.message
        binding.textSecondError.visibility = if (errorMessage == null) View.GONE else View.VISIBLE
        binding.textSecondError.text = errorMessage
    }
}
