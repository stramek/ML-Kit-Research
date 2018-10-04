package pl.marcin.mlkitresearch.feature

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.actvity_landmarks_recognition.*
import pl.marcin.mlkitresearch.R

class LandmarksActivity : BaseCameraActivity() {

    override val layoutRes = R.layout.actvity_landmarks_recognition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase(camera_view, progress_bar)
        internet_analyze.setOnClickListener { getPhoto(this::runNetworkFaceRecognition) }
    }

    private fun runNetworkFaceRecognition(bitmap: Bitmap) {
        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
        passed_image.setImageBitmap(bitmap)

        val options = FirebaseVisionCloudDetectorOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .setMaxResults(15)
            .build()

        val detector = FirebaseVision.getInstance().getVisionCloudLandmarkDetector(options)

        detector.detectInImage(firebaseImage)
            .addOnSuccessListener {
                processFaces(it)
                progress_bar.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e("TAG", "Error processing landmarks", it)
                progress_bar.visibility = View.GONE
            }
    }

    private fun processFaces(landmarks: List<FirebaseVisionCloudLandmark>) {
        val sb = StringBuilder()
        landmarks.forEach {
            sb.append("${it.landmark}: ${it.confidence}").append("\n")
        }
        showAlert("From network landmark", "size: ${landmarks.size}\n$sb")
    }
}
