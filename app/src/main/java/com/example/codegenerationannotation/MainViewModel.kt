package com.example.codegenerationannotation

import androidx.lifecycle.ViewModel
import com.example.anotation.SampleMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    @SampleMethod
    fun doSomething() {
        mainRepository.doSomething()
    }
}