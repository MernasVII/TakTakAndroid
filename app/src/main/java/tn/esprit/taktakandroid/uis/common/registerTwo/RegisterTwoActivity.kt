package tn.esprit.taktakandroid.uis.common.registerTwo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.databinding.ActivityRegisterTwoBinding
import tn.esprit.taktakandroid.uis.common.BaseActivity

class RegisterTwoActivity : BaseActivity() {
    private lateinit var mainView: ActivityRegisterTwoBinding
    private var imageBitmap: Bitmap? = null
    private lateinit var recognizer: TextRecognizer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityRegisterTwoBinding.inflate(layoutInflater)
        setContentView(mainView.root)

        recognizer = TextRecognizer.Builder(this).build()


        mainView.btnRegSp.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        mainView.etCin.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        ImagePicker.with(this)
                            .compress(1024)
                            .crop()
                         .createIntent {
                             startForImageResult.launch(it)
                         }
                    }
                }
        }


    }



    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri = data?.data!!

                val bitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    uri
                )

                imageBitmap = bitmap
                processImage(bitmap)


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }

    private fun processImage(bitmap: Bitmap?) {
        val frame = Frame.Builder().setBitmap(bitmap!!).build()
        val tb = recognizer.detect(frame) as SparseArray<TextBlock>
        val s = java.lang.StringBuilder();
        for (i in 0 until tb.size()) {
            if (tb.valueAt(i).value.toString().trim().length == 8) {
                s.append(tb.valueAt(i).value)
            }
            else{
                Toast.makeText(this, "Operation Failed", Toast.LENGTH_SHORT).show()

            }
        }
        mainView.etCin.setText(s.toString())

    }

}