package pl.marcin.mlkitresearch.feature

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import kotlinx.android.synthetic.main.activity_labeling.*
import pl.marcin.mlkitresearch.R


class LabelingActivity : BaseCameraActivity() {

    override val layoutRes = R.layout.activity_labeling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase(camera_view, progress_bar)
        local_analyze.setOnClickListener { getPhoto(this::runImageLabelingOnDevice) }
        internet_analyze.setOnClickListener { getPhoto(this::runImageLabelingCloud) }
    }

    private fun runImageLabelingOnDevice(bitmap: Bitmap) {
        passed_image.setImageBitmap(bitmap)
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionLabelDetectorOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()

        val labelDetector = FirebaseVision.getInstance().getVisionLabelDetector(options)

        labelDetector.detectInImage(image)
                .addOnSuccessListener {
                    processImageLabelingFromDevice(it)
                    progressBar.visibility = View.INVISIBLE
                }.addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    Log.e("LABELFAILURE", it.toString())
                    progressBar.visibility = View.INVISIBLE
                }
    }

    private fun runImageLabelingCloud(bitmap: Bitmap) {
        passed_image.setImageBitmap(bitmap)
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionCloudDetectorOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .setMaxResults(15)
            .build()
        val labelDetector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options)
        labelDetector
                .detectInImage(image).addOnSuccessListener {
                    processImageLabelingFromCloud(it)
                    progressBar.visibility = View.INVISIBLE
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    Log.e("LABELFAILURE", it.toString())
                    progressBar.visibility = View.INVISIBLE
                }
    }

    private fun processImageLabelingFromDevice(labels : MutableList<FirebaseVisionLabel>){
        val labelsSb = StringBuilder()
        labels.forEach {
            Log.d("IMAGELABELING",it.label)
            labelsSb.append("${it.label}: ${it.confidence}").appendln()
        }

        showAlert("Labels from device", labelsSb.toString())
    }

    fun processImageLabelingFromCloud(labels : MutableList<FirebaseVisionCloudLabel>){
        val labelsSb = StringBuilder()
        labels.forEach {
            Log.d("IMAGELABELING",it.label)
            labelsSb.append("${it.label}: ${it.confidence}").appendln()
        }

        showAlert("Labels from cloud", labelsSb.toString())
    }
}
