package com.example.kotlincoroutines

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.kotlincoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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
            binding.progress.isVisible = true
            binding.buttonLoad.isEnabled = false
            val deferredCity : Deferred<String> = lifecycleScope.async {
                val city = loadCity()
                binding.tvLocation.text = city
                city
            }
            //
            val deferredTemp : Deferred<Int> = lifecycleScope.async {
                val temp = loadTemperature()
                binding.tvTemperature.text = temp.toString()
                temp
            }
            lifecycleScope.launch {
                // метод join - остановит корутину до тех пор, пока эта работа не будет выполнена
                // метод await - сделает тоже самое, что и join,
                // но при этом он вернет объект, который лежит в корутине
                val city = deferredCity.await()
                val temp = deferredTemp.await()
                Toast.makeText(this@MainActivity,"City: $city, Temp: $temp", Toast.LENGTH_SHORT).show()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
            }

        }
    }


    // Чтобы этот метод можно было приостановить и не заблокировать основной поток,
    // нужно добавить слово suspend
    private suspend fun loadCity(): String {
        // А Thread.sleep заменяем -> на delay(Число в формате Int - 1000 == Одна секунда)
        delay(5000)
        return "Moscow"
    }

    private suspend fun loadTemperature(): Int {
        delay(5000)
        return 17
    }



    private fun loadWithoutCoroutine(step: Int = 0, obj: Any? = null) {
        when (step) {
            0 -> {
                Log.d("MainActivity", "Load started: $this")
                binding.progress.isVisible = true
                binding.buttonLoad.isEnabled = false
                loadCityWithoutCoroutine {
                    loadWithoutCoroutine(1, it)
                }
            }
            1 -> {
                val city = obj as String
                binding.tvLocation.text = city
                loadTemperatureWithoutCoroutine(city) {
                    loadWithoutCoroutine(2, it)
                }
            }
            2 -> {
                val temp = obj as Int
                binding.tvTemperature.text = temp.toString()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
                Log.d("MainActivity", "Load finished: $this")
            }
        }
    }


    private fun loadCityWithoutCoroutine(callback: (String) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke("Moscow")
        }, 5000)
    }


    // Чтобы не возникала ошибка в основном методе, необходимо также добавить suspend
    private suspend fun loadData() {
        binding.progress.isVisible = true
        binding.buttonLoad.isEnabled = false
        val city = loadCity()
        binding.tvLocation.text = city
        val temp = loadTemperature()
        binding.tvTemperature.text = temp.toString()
        binding.progress.isVisible = false
        binding.buttonLoad.isEnabled = true
    }


    private fun loadTemperatureWithoutCoroutine(city: String, callback: (Int) -> Unit) {
        Toast.makeText(
            this,
            getString(R.string.loading_temperature_toast, city),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke(17)
        }, 5000)
    }


}