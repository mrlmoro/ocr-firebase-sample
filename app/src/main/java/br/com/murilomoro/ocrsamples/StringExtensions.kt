package br.com.murilomoro.ocrsamples

/**
 * Created by Murilo Moro on 08/02/19.
 */

fun String.toChar(index: Int): Char = toCharArray()[index]

fun String.onlyNumbers() = replace(Regex("[^0-9]"), "")