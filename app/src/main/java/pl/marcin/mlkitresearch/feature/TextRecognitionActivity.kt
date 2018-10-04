package pl.marcin.mlkitresearch.feature

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_text_recognition.*
import pl.marcin.mlkitresearch.R


class TextRecognitionActivity : BaseCameraActivity() {

    override val layoutRes = R.layout.activity_text_recognition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase(camera_view, progress_bar)
        local_analyze.setOnClickListener { getPhoto(this::runTextRecognitionOnDevice) }
        internet_analyze.setOnClickListener { getPhoto(this::runTextRecognitionCloud) }
    }

    private fun runTextRecognitionOnDevice(bitmap: Bitmap) {
        passed_image.setImageBitmap(bitmap)
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val labelDetector = FirebaseVision.getInstance().onDeviceTextRecognizer
        labelDetector.processImage(image)
                .addOnSuccessListener {
                    processFirebaseVisionText("Device", it)
                    progressBar.visibility = View.INVISIBLE
                }.addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    Log.e("TextRecog", it.toString())
                    progressBar.visibility = View.INVISIBLE
                }
    }

    private fun runTextRecognitionCloud(bitmap: Bitmap) {
        passed_image.setImageBitmap(bitmap)
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val labelDetector = FirebaseVision.getInstance().cloudTextRecognizer
        labelDetector
                .processImage(image).addOnSuccessListener {
                processFirebaseVisionText("Cloud", it)
                    progressBar.visibility = View.INVISIBLE
                }
                .addOnFailureListener {
                    Log.e("TextRecog", it.toString())
                    progressBar.visibility = View.INVISIBLE
                }
    }

    private fun processFirebaseVisionText(title: String, labels : FirebaseVisionText){
        showAlert(title, labels.text)
    }
}
