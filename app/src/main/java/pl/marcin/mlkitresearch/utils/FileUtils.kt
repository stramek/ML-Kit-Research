package pl.marcin.mlkitresearch.utils

import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class FileUtils {

    companion object {

        /**
         * Loads text file words to list and returns it
         */
        fun loadLabels(assetManager: AssetManager, filename: String): List<String> {
            val toReturn = mutableListOf<String>()
            try {
                BufferedReader(InputStreamReader(assetManager.open(filename))).apply {
                    forEachLine { toReturn.add(it) }
                }.close()
            } catch (e: IOException) {
                throw RuntimeException("Problem reading label file!", e)
            }
            return toReturn
        }

    }
}