package pl.marcin.mlkitresearch.model

data class Recognition(val title: String, val confidence: Float) {

    override fun toString() = "$title: $confidence"
}