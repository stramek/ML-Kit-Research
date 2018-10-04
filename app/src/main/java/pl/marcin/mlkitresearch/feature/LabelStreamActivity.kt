package pl.marcin.mlkitresearch.feature

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.actvity_label_stream.*
import pl.marcin.mlkitresearch.R



class LabelStreamActivity : AppCompatActivity() {

    companion object {
        private const val PREFERRED_CAMERA_PREVIEW_AREA = 640 * 480
    }

    private lateinit var camera: Fotoapparat

    private val cameraConfiguration = CameraConfiguration(
        previewResolution = standardRatio(
            firstAvailable(selectBestResolutionWithMaxAreaOf(PREFERRED_CAMERA_PREVIEW_AREA), lowestResolution())
        ),
        previewFpsRange = highestFps(),
        focusMode = firstAvailable(continuousFocusPicture(), autoFocus()),
        frameProcessor = { frameToProcess -> processFrame(frameToProcess) }
    )

    private val detector = FirebaseVision.getInstance().visionLabelDetector
    private var isProcessing = false

    /**
     * Filter available resolutions with bigger area then [maxPreviewArea] and then picks highest.
     */
    private fun selectBestResolutionWithMaxAreaOf(maxPreviewArea: Int): Iterable<Resolution>.() -> Resolution? = {
        toList().asSequence().filter { it.area <= maxPreviewArea }.maxBy { it.area }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actvity_label_stream)
        camera = Fotoapparat(this, camera_view, cameraConfiguration = cameraConfiguration)
    }

    private fun processFrame(frame: Frame) {
        if (isProcessing) return
        isProcessing = true

        val rotation = when(frame.rotation) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw RuntimeException("WRONG ROTATION!")
        }

        val firebaseImage = FirebaseVisionImage.fromByteArray(
            frame.image,
            FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(rotation)
                .setWidth(frame.size.width)
                .setHeight(frame.size.height)
                .build()
        )

        detector.detectInImage(firebaseImage)
            .addOnSuccessListener { updateLabels(it) }
            .addOnFailureListener { Log.e("TAG", "Error processing barcodes", it) }
            .addOnCompleteListener { isProcessing = false }
    }

    private fun updateLabels(labels: List<FirebaseVisionLabel>) {
        val output = labels.asSequence().take(3).joinToString(separator = "\n") { "${it.label}: ${it.confidence}" }
        resultTv.text = output
    }

    override fun onStart() {
        super.onStart()
        camera.start()
    }

    override fun onStop() {
        camera.stop()
        super.onStop()
    }
}
