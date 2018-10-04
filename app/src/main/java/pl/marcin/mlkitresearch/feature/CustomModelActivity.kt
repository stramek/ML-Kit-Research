package pl.marcin.mlkitresearch.feature

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.custom.*
import com.google.firebase.ml.custom.model.FirebaseCloudModelSource
import com.google.firebase.ml.custom.model.FirebaseLocalModelSource
import com.google.firebase.ml.custom.model.FirebaseModelDownloadConditions
import kotlinx.android.synthetic.main.actvity_barcode_recognition.*
import pl.marcin.mlkitresearch.R
import pl.marcin.mlkitresearch.model.Recognition
import pl.marcin.mlkitresearch.utils.FileUtils
import pl.marcin.mlkitresearch.utils.toByteBuffer
import kotlin.experimental.and

class CustomModelActivity : BaseCameraActivity() {

    override val layoutRes: Int = R.layout.actvity_barcode_recognition

    private lateinit var mDataOptions: FirebaseModelInputOutputOptions
    private var mInterpreter: FirebaseModelInterpreter? = null

    private lateinit var mLabelList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase(camera_view, progress_bar)
        init()
        local_analyze.setOnClickListener { getPhoto(this::runlocalBarcodeDetection) }
    }

    private fun init() {
        mLabelList = FileUtils.loadLabels(assets, LABEL_PATH)
        try {
            mDataOptions = createDataOptions()

            val localModelSource = prepareLocalModel()
            val cloudSource = prepareCloudModel(createDefaultConditions())

            FirebaseModelManager.getInstance().apply {
                registerLocalModelSource(localModelSource)
                registerCloudModelSource(cloudSource)
            }

            val modelOptions = FirebaseModelOptions.Builder()
                .setCloudModelName(HOSTED_MODEL_NAME)
                .setLocalModelName(LOCAL_MODEL_ASSET)
                .build()

            mInterpreter = FirebaseModelInterpreter.getInstance(modelOptions)
        } catch (e: FirebaseMLException) {
            e.printStackTrace()
        }
    }

    private fun createDataOptions(): FirebaseModelInputOutputOptions {
        val inputDims = intArrayOf(NUMBER_OF_PHOTOS, TARGET_INPUT_WIDTH, TARGET_INPUT_HEIGHT, NUMBER_OF_PIXELS)
        val outputDims = intArrayOf(NUMBER_OF_PHOTOS, mLabelList.size)
        return FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.BYTE, inputDims)
            .setOutputFormat(0, FirebaseModelDataType.BYTE, outputDims)
            .build()
    }

    private fun createDefaultConditions() = FirebaseModelDownloadConditions.Builder()
        .requireWifi()
        .build()

    private fun prepareLocalModel() = FirebaseLocalModelSource.Builder(LOCAL_MODEL_ASSET)
        .setAssetFilePath(LOCAL_MODEL_ASSET).build()

    private fun prepareCloudModel(confitions: FirebaseModelDownloadConditions)
            = FirebaseCloudModelSource.Builder(HOSTED_MODEL_NAME)
                .enableModelUpdates(true)
                .setInitialDownloadConditions(confitions)
                .setUpdatesDownloadConditions(confitions)
                .build()

    private fun runlocalBarcodeDetection(bitmap: Bitmap) {
        mInterpreter?.let { interpreter ->
            val imgData = bitmap.toByteBuffer(NUMBER_OF_PHOTOS, NUMBER_OF_PIXELS, TARGET_INPUT_WIDTH, TARGET_INPUT_HEIGHT)
            try {
                val inputs = FirebaseModelInputs.Builder().add(imgData).build()
                interpreter
                        .run(inputs, mDataOptions)
                        .continueWith { task ->
                            val labelProbArray = task.result.getOutput<Array<ByteArray>>(0)
                            getTopLabels(labelProbArray)
                        }
                        .addOnSuccessListener { labels ->
                            showAlert("Result", labels.joinToString(separator = "\n"))
                        }
                        .addOnCompleteListener { progressBar.visibility = View.GONE }
                        .addOnFailureListener { error ->
                            showAlert("Error", error.toString())
                            error.printStackTrace()
                        }
            } catch (e: FirebaseMLException) {
                e.printStackTrace()
            }
        }
    }

    private fun getTopLabels(labelProbArray: Array<ByteArray>): List<Recognition> {
        return labelProbArray[0]
            .mapIndexed { index, byteConfidence ->
                val confidence = (byteConfidence and 255.toByte()) / 255.0f
                Recognition(mLabelList.getOrNull(index) ?: "unknown", confidence)
            }
            .sortedByDescending { it.confidence }
            .take(RESULTS_TO_SHOW)
    }

    companion object {
        private const val NUMBER_OF_PHOTOS = 1
        private const val NUMBER_OF_PIXELS = 3
        private const val TARGET_INPUT_WIDTH = 224
        private const val TARGET_INPUT_HEIGHT = 224

        private const val RESULTS_TO_SHOW = 3

        private const val LABEL_PATH = "labels.txt"
        private const val HOSTED_MODEL_NAME = "mobilenet_v1_224_quant"
        private const val LOCAL_MODEL_ASSET = "mobilenet_v1.0_224_quant.tflite" // name from assets
    }
}