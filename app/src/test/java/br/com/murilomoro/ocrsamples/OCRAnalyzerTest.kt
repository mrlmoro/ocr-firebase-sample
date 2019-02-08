package br.com.murilomoro.ocrsamples

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Murilo Moro on 08/02/19.
 */
class OCRAnalyzerTest {

    lateinit var ocrAnalyzer: OCRAnalyzer

    @Before
    fun `Setup test`() {
        ocrAnalyzer = OCRAnalyzer(getListMock())
    }

    @Test
    fun `Test replace regex`() {
        val expected = "81954529902"
        val mock = "819.545.299-02"
        val result = mock.replace(Regex("[^0-9]"), "")
        assertEquals(expected, result)
    }

    @Test
    fun `Test identify CPF`() {
        val expected = "81954529902"
        val result = ocrAnalyzer.identifyCPF()
        assertEquals(expected, result)
    }

    private fun getListMock(): List<String> {
        return Gson()
            .fromJson<List<String>>(
                getJsonMock(),
                object : TypeToken<List<String>>() {}.type
            )
    }

    private fun getJsonMock() =
        "[\"A\", \"t\", \"CDATA NASCIMENTO O\", \"L\", \"rCAT, HAB.\", \"19 HABILITAÇAo\", \"NO\", \"21/04/2025l 05/02/2015\", \"01134549321\", \"REPUBLICA GAFEDERATIYA D\", \"BRASI\", \"CTDADES\", \"NS\\nDEARILTAS\", \"JOSÉ DA SILVA JUNIOR\", \"-DOC. IDENTIDADE/ÓRG. EMISSOR/ UF\", \"42035678 sSP/SP\\n819.545.299-02 10/01/1990\\nJOSÉ DA SILVA\\nMARIA DA SILVA\", \"MiNISTERIG\\nRIAMENTONAGIONALDET\", \"ARERANACIONA\"]"
}