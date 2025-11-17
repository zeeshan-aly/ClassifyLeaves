package com.example.classifyleaves
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

data class PredictionResult(val label: String, val confidence: Float)

class LeafClassifier(context: Context) {

    private val TAG = "LeafClassifier"
    private val interpreter: Interpreter
    private val classLabels: List<String>

    init {
        Log.d(TAG, "Loading TFLite model and class labels...")
        val modelBuffer = loadModelFile(context, "leaf_classifier.tflite")
        interpreter = Interpreter(modelBuffer)
        Log.d(TAG, "Model loaded successfully.")
        classLabels = context.assets.open("classes.txt")
            .bufferedReader().readLines()
        Log.d(TAG, "Loaded ${classLabels.size} class labels.")
    }

    fun classify(bitmap: Bitmap): PredictionResult {
        Log.d(TAG, "Preprocessing input image...")
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resized)
        val inputBuffer = tensorImage.buffer
        Log.d(TAG, "Input image preprocessed. Running inference...")
        val output = Array(1) { FloatArray(classLabels.size) }
        interpreter.run(inputBuffer, output)
        val confidences = output[0]
        val maxIdx = confidences.indices.maxByOrNull { confidences[it] } ?: -1
        val label = classLabels.getOrElse(maxIdx) { "Unknown" }
        val confidence = if (maxIdx != -1) confidences[maxIdx] else 0f
        Log.d(TAG, "Inference complete. Top label: $label, confidence: $confidence")
        return PredictionResult(label, confidence)
    }
}

fun loadModelFile(context: Context, modelFileName: String): ByteBuffer {
    val fileDescriptor = context.assets.openFd(modelFileName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

class LeafClassifierViewModelFactory(
    private val app: android.app.Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeafClassifierViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeafClassifierViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
