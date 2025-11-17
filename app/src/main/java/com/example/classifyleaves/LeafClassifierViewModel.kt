package com.example.classifyleaves

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LeafClassifierViewModel(app: Application) : AndroidViewModel(app), KoinComponent {
    private val classifier: LeafClassifier by inject()

    private val _selectedImage = MutableStateFlow<Bitmap?>(null)
    val selectedImage: StateFlow<Bitmap?> = _selectedImage

    private val _prediction = MutableStateFlow<PredictionResult?>(null)
    val prediction: StateFlow<PredictionResult?> = _prediction

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun setImage(bitmap: Bitmap) {
        _selectedImage.value = bitmap
        classify(bitmap)
    }

    private fun classify(bitmap: Bitmap) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = classifier.classify(bitmap)
                _prediction.value = result
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clear() {
        _selectedImage.value = null
        _prediction.value = null
        _error.value = null
    }
} 