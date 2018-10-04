package pl.marcin.mlkitresearch.feature

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.actvity_face_recognition.*
import pl.marcin.mlkitresearch.R

class BarcodeActivity : BaseCameraActivity() {

    override val layoutRes: Int = R.layout.actvity_barcode_recognition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase(camera_view, progress_bar)
        local_analyze.setOnClickListener { getPhoto(this::runlocalBarcodeDetection) }
    }

    private fun runlocalBarcodeDetection(bitmap: Bitmap) {
        val options = FirebaseVisionBarcodeDetectorOptions.Builder().build()
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
        passed_image.setImageBitmap(bitmap)

        detector.detectInImage(firebaseImage)
            .addOnSuccessListener {
                processBarcodes(it)
                progress_bar.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e("Barcodes", "Error processing barcodes", it)
                progress_bar.visibility = View.GONE
            }
    }

    private fun processBarcodes(barcodes: List<FirebaseVisionBarcode>) {
        barcodes.firstOrNull()!!
        val sb = StringBuilder()
        barcodes.forEach {
            sb.append(it.rawValue).append("\n")
        }
        showAlert("Barcodes", "Count: ${barcodes.size}\n$sb")
    }
}