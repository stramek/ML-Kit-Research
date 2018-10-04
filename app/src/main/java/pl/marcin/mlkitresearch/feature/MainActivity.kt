package pl.marcin.mlkitresearch.feature

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import pl.marcin.mlkitresearch.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupButtons()
    }

    private fun setupButtons() {
        labelingBt.setOnClickListener { startLabelingActivity() }
        textRecognitionBt.setOnClickListener { startTextRecognitionActivity() }
        faceRecogniotionBr.setOnClickListener { startFaceRecognitionActivity() }
        landmarksRecogniotionBr.setOnClickListener { startLandmarksRecognitionActivity() }
        barcodeRecogniotionBr.setOnClickListener { startBarcodeRecognitionActivity() }
        customModel.setOnClickListener { startCustomActivity() }
        labelRecognitionStream.setOnClickListener { startLabelStreamActivity() }
    }

    private fun startLabelingActivity() = runWithPermissions(Manifest.permission.CAMERA) {
        startActivity<LabelingActivity>()
    }

    private fun startTextRecognitionActivity() = runWithPermissions(Manifest.permission.CAMERA) {
        startActivity<TextRecognitionActivity>()
    }

    private fun startFaceRecognitionActivity() = runWithPermissions(Manifest.permission.CAMERA) {
        startActivity<FaceRecognitionActivity>()
    }

    private fun startLandmarksRecognitionActivity() = runWithPermissions(Manifest.permission.CAMERA) {
        startActivity<LandmarksActivity>()
    }

    private fun startBarcodeRecognitionActivity() = runWithPermissions(Manifest.permission.CAMERA) {
        startActivity<BarcodeActivity>()
    }

    private fun startCustomActivity() = runWithPermissions(Manifest.permission.CAMERA) {
        startActivity<CustomModelActivity>()
    }

    private fun startLabelStreamActivity() = runWithPermissions(Manifest.permission.CAMERA) {
        startActivity<LabelStreamActivity>()
    }
}
