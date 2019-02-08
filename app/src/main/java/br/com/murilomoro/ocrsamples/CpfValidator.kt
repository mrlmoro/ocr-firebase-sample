package br.com.murilomoro.ocrsamples

/**
 * Created by Murilo Moro on 08/02/19.
 */

fun String.isCPF(): Boolean {
    // considera-se erro CPF's formados por uma sequencia de numeros iguais
    if (equals("00000000000") ||
        equals("11111111111") ||
        equals("22222222222") ||
        equals("33333333333") ||
        equals("44444444444") ||
        equals("55555555555") ||
        equals("66666666666") ||
        equals("77777777777") ||
        equals("88888888888") ||
        equals("99999999999") ||
        (length != 11)
    ) {
        return false
    }

    try {
        // Calculo do 1o. Digito Verificador
        val dig10 = calculateCpfDigit(this, 10)

        // Calculo do 2o. Digito Verificador
        val dig11 = calculateCpfDigit(this, 11)

        // Verifica se os digitos calculados conferem com os digitos informados.
        return (dig10 == toChar(9)) && (dig11 == toChar(10))
    } catch (ex: Exception) {
        ex.printStackTrace()
        return false
    }
}

private fun calculateCpfDigit(cpf: String, pesoDigito: Int): Char {
    var peso = pesoDigito
    var sm = 0
    var num: Int

    for (i in 0 until (pesoDigito - 1)) {
        num = cpf.toChar(i).toInt() - 48
        sm += (num * peso)
        peso -= 1
    }

    val r = 11 - (sm % 11)
    return if (r == 10 || r == 11)
        '0'
    else
        (r + 48).toChar()
}