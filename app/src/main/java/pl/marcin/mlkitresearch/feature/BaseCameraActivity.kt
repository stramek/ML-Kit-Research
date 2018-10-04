package pl.marcin.mlkitresearch.feature

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.autoFocus
import io.fotoapparat.selector.continuousFocusPicture
import io.fotoapparat.selector.firstAvailable
import io.fotoapparat.view.CameraView
import pl.marcin.mlkitresearch.utils.rotateBy

abstract class BaseCameraActivity : AppCompatActivity() {

    abstract val layoutRes: Int

    lateinit var progressBar: ProgressBar

    private val cameraConfiguration = CameraConfiguration(
        focusMode = firstAvailable(continuousFocusPicture(), autoFocus())
    )

    private lateinit var aparat: Fotoapparat

    fun initBase(cameraView: CameraView, progressBar: ProgressBar) {
        this.progressBar = progressBar
        aparat = Fotoapparat(
            context = this,
            view = cameraView,
            cameraConfiguration = cameraConfiguration
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
    }

    fun getPhoto(function: (Bitmap) -> Unit) {
        progressBar.visibility = View.VISIBLE

        val photoResult = aparat
            .autoFocus()
            .takePicture()

        photoResult
            .toBitmap(scaled(scaleFactor = 0.5f))
            .whenAvailable { photo ->
                val rotatedPhoto = photo!!.bitmap.rotateBy(-photo.rotationDegrees.toFloat())
                function(rotatedPhoto)
            }
    }

    override fun onStart() {
        super.onStart()
        aparat.start()
    }

    override fun onStop() {
        aparat.stop()
        super.onStop()
    }

    fun showAlert(title: String, text: String) {
        progressBar.visibility = View.GONE
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(text)
            .create()
            .show()
    }
}