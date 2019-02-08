package br.com.murilomoro.ocrsamples

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_GALLERY = 0
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_PERMISSIONS = 2
    }

    private var cameraImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_gallery.setOnClickListener {
            tv_ocr.text = ""
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        bt_camera.setOnClickListener {
            requestCameraPermission { openCamera() }
        }

    }

    private fun openCamera() {
        tv_ocr.text = ""

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "ocr-image")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        cameraImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun analyze(imageUri: Uri) {
        try {
            val image = FirebaseVisionImage.fromFilePath(this, imageUri)

            progress.visibility = View.VISIBLE

            FirebaseVision.getInstance()
                .onDeviceTextRecognizer
                .processImage(image)
                .addOnSuccessListener {
                    val builder = StringBuilder()
                    val texts = it.textBlocks.map { it.text }
                    texts.forEach { txt -> builder.appendln(txt) }

                    progress.visibility = View.GONE
                    tv_ocr.text = builder.toString()

                    val ocrAnalyzer = OCRAnalyzer(texts)
                    ocrAnalyzer.identifyCPF()
                        ?.let {
                            AlertDialog.Builder(this)
                                .setMessage("CPF IDENTIFICADO: $it")
                                .setNeutralButton("Ok") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                        }
                }
                .addOnFailureListener {
                    progress.visibility = View.GONE
                    Toast.makeText(this, "Process failure", Toast.LENGTH_LONG).show()
                    it.printStackTrace()
                }
        } catch (e: IOException) {
            Toast.makeText(this, "Image not found", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GALLERY -> {
                data?.data?.let { analyze(it) }
            }

            REQUEST_CAMERA -> {
                cameraImageUri?.let { analyze(it) }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                var count = 0
                grantResults.forEach {
                    if (it == PackageManager.PERMISSION_GRANTED)
                        count++
                }

                if (count == permissions.count()) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permissões necessárias para continuar", Toast.LENGTH_LONG).show()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun requestCameraPermission(alreadyGranted: () -> Unit) {
        val permissions = mutableListOf<String>()

        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_PERMISSIONS
            )
        } else {
            alreadyGranted.invoke()
        }
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}
