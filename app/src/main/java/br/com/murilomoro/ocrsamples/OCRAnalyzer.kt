package br.com.murilomoro.ocrsamples

/**
 * Created by Murilo Moro on 08/02/19.
 */
class OCRAnalyzer(private val textBlocks: List<String>) {

    fun identifyCPF(): String? {
        return textBlocks
            .flatMap { it.lines() }
            .mapNotNull {
                val numbers = it.onlyNumbers()
                if (numbers.length >= 11) {
                    numbers.substring(0, 11)
                } else {
                    null
                }
            }
            .find { it.isCPF() }
    }

}