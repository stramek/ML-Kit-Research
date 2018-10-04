package pl.marcin.mlkitresearch.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun Bitmap.rotateBy(degrees: Float): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees)
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.toByteBuffer(
    batchSize:Int,
    pixelSize: Int,
    imgSizeX: Int,
    imgSizeY: Int
): ByteBuffer {
    val intValues = IntArray(imgSizeX * imgSizeY)
    val imgData = ByteBuffer.allocateDirect(
        batchSize * imgSizeX * imgSizeY * pixelSize
    )
    imgData.order(ByteOrder.nativeOrder())
    val scaledBitmap = Bitmap.createScaledBitmap(this, imgSizeX, imgSizeY, true)
    imgData.rewind()
    scaledBitmap.getPixels(
        intValues, 0, scaledBitmap.width, 0, 0,
        scaledBitmap.width, scaledBitmap.height
    )
    for ((pixel, _) in (0 until imgSizeX * imgSizeY).withIndex()) {
        val value = intValues[pixel]
        imgData.put((value shr 16 and 0xFF).toByte())
        imgData.put((value shr 8 and 0xFF).toByte())
        imgData.put((value and 0xFF).toByte())
    }
    return imgData
}