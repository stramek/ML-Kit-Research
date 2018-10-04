package pl.marcin.mlkitresearch.feature

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.actvity_face_recognition.*
import pl.marcin.mlkitresearch.R

class FaceRecognitionActivity : BaseCameraActivity() {

    override val layoutRes = R.layout.actvity_face_recognition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase(camera_view, progress_bar)
        local_analyze.setOnClickListener { getPhoto(this::runLocalFaceRecognition) }
    }

    private fun runLocalFaceRecognition(bitmap: Bitmap) {

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
            .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .setTrackingEnabled(true)
            .build()

        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
        passed_image.setImageBitmap(bitmap)

        detector.detectInImage(firebaseImage)
            .addOnSuccessListener {
                processFaces(it)
                progress_bar.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e("Faces", "Error processing faces", it)
                progress_bar.visibility = View.GONE
            }
    }

    private fun processFaces(faces: List<FirebaseVisionFace>) {
        val sb = StringBuilder()
        faces.forEach {
            sb.append(it.smilingProbability).append("\n")
        }
        showAlert("Faces", "Count: ${faces.size}\n$sb")
    }

}