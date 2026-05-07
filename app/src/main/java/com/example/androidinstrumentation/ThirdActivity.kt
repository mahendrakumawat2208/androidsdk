package com.example.androidinstrumentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidinstrumentation.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_META = "extra_meta"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
    }

    private lateinit var binding: ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textSecondMeta.text = intent.getStringExtra(EXTRA_META).orEmpty()
        binding.textSecondTitle.text = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        binding.textSecondBody.text = intent.getStringExtra(EXTRA_BODY).orEmpty()

        binding.buttonPreviousPage.setOnClickListener { finish() }
    }
}
