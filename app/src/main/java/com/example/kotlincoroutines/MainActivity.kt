package com.example.kotlincoroutines

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.kotlincoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonLoad.setOnClickListener {
            // Если ты хочешь, чтобы после того как Activity прекращала свою работу,
            // то все запросы в потоке отменялись, то есть чтобы у них был жизненный цикл,
            // для этого надо использовать метод lifecycleScope.launch
            lifecycleScope.launch{
                loadData()
            }
        }
    }


    // Чтобы не возникала ошибка в основном методе, необходимо также добавить suspend
    private suspend fun loadData() {
        binding.progress.isVisible = true
        binding.buttonLoad.isEnabled = false
        val city = loadCity()
        binding.tvLocation.text = city
        val temp = loadTemperature(city)
        binding.tvTemperature.text = temp.toString()
        binding.progress.isVisible = false
        binding.buttonLoad.isEnabled = true
    }


    // Чтобы этот метод можно было приостановить и не заблокировать основной поток,
    // нужно добавить слово suspend
    private suspend fun loadCity(): String {
        // А Thread.sleep заменяем -> на delay(Число в формате Int - 1000 == Одна секунда)
        delay(5000)
        return "Moscow"
    }

    private suspend fun loadTemperature(city: String): Int {
        Toast.makeText(
            this,
            getString(R.string.loading_temperature_toast, city),
            Toast.LENGTH_SHORT
        ).show()
        delay(5000)
        return 17
    }
}