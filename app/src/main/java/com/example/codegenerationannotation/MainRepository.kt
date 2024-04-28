package com.example.codegenerationannotation

import android.util.Log
import com.example.anotation.GenerateBinding
import javax.inject.Inject

interface MainRepository {
    fun doSomething()
}

@GenerateBinding(type = MainRepository::class)
class MainRepositoryImpl @Inject constructor(): MainRepository {
    override fun doSomething() {
        Log.d("MainRepositoryImpl", "doSomething")
    }
}