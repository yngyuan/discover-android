import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.phase3.CustomJsonObjectRequest
import com.example.phase3.R
import com.example.phase3.getInfo
import com.example.recycler.Report
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_create.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class CreateFragment : Fragment() {

    private lateinit var currentPhotoPath: String
    private lateinit var currentPhotoUrl: Uri
    private var latitute: String = "37.42"
    private var longitute: String = "-122.08"
    private lateinit var myView: View
    private val REQUEST_IMAGE_CAPTURE = 1
    //this url is my firebase storage, it is now public, anyone can upload to it.
    private val myFirebaseStorageUrl: String = "gs://aptphase3-74124.appspot.com"
    private var storage = FirebaseStorage.getInstance()
    private var storgaRef = storage.getReferenceFromUrl(myFirebaseStorageUrl)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_create, container, false)
        val btn_camera = myView.findViewById<Button>(R.id.button_camera)
        btn_camera.setOnClickListener { myView ->
            onClickCamera()
        }
        val btn_create = myView.findViewById<Button>(R.id.button_create)
        btn_create.setOnClickListener { myView ->
            onClickCreate()
        }
//        val sharedPreferences: SharedPreferences =
//            activity!!.getSharedPreferences("Phase3", Context.MODE_PRIVATE)
        //seems like location collection is slow at start
        longitute = getInfo(activity?.applicationContext,"longitude")!!
        latitute = getInfo(activity?.applicationContext,"latitude")!!
        val locView = myView.findViewById<TextView>(R.id.textView_current_location_for_creation)
        locView.text =
            "(Latitute: " + latitute.toString() + ", Longitute:" + longitute.toString() + ")"
        return myView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> displayImg()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun onClickCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity!!,
                        "com.example.phase3.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    fun onClickCreate() {
        uploadImg()
    }

    private fun uploadImg() {
        val btn_create = myView.findViewById<Button>(R.id.button_create)
        if (btn_create.text == "Create New Report!" || btn_create.text == "Try again") {
            btn_create.text = "Uploading image..."
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imgRef = storgaRef.child("images/${timeStamp}.jpg")
            val imgFile = Uri.fromFile(File(currentPhotoPath))
            val uploadTask = imgRef.putFile(imgFile)
            val urlTask =
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation imgRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        currentPhotoUrl = task.result!!
                        sendCreateRequest()
                    } else {
                        // Handle failures
                        // ...
                    }
                }
        }
    }

    private fun jsonRequestGener(): JSONObject {
        val editviewReportTitle = myView.findViewById<EditText>(R.id.editText_title)
        val spinnerReportTheme = myView.findViewById<Spinner>(R.id.spinner_theme)
        val editviewReportDescription = myView.findViewById<EditText>(R.id.editText_description)
        val editviewReportTags = myView.findViewById<EditText>(R.id.editText_tags)


        // TODO: uid need here
//        val uidFromCookie = getCookie(activity).toString()
        val reportImgUrl = currentPhotoUrl.toString()
        val reportTitle = editviewReportTitle.text
        val reportTheme = spinnerReportTheme.selectedItem.toString()
        val reportLocation = "$($latitute,$longitute)"
        val reportDescription = editviewReportDescription.text
        val reportTaglist = editviewReportTags.text
        val currUser = getInfo(activity?.applicationContext, "uid")
        Log.d("ManageFragment", "current User${currUser}")

        val dstObj = JSONObject()
        dstObj.put("uid", currUser)
        dstObj.put("r_img_url", reportImgUrl!!)
        dstObj.put("r_title", reportTitle!!)

//        TODO: uid needed here
//        dstObj.put("r_uid", uidFromCookie)
        dstObj.put("r_uid", currUser)
        dstObj.put("r_location", reportLocation!!)
        dstObj.put("r_tname", reportTheme!!)
        dstObj.put("r_description", reportDescription!!)
        dstObj.put("r_tag_list", reportTaglist!!)

        return dstObj
    }

    private fun sendCreateRequest() {
        val btn_create = myView.findViewById<Button>(R.id.button_create)
        btn_create.text = "Creating..."
        val postCreateReportJson: JSONObject = jsonRequestGener()
        val queue = Volley.newRequestQueue(getActivity()?.getApplicationContext())
        val create_report_url =
            "http://${getString(R.string.homepage)}/${getString(R.string.createReport)}"
        var getRequest: JsonObjectRequest = CustomJsonObjectRequest(
            Request.Method.POST, create_report_url, postCreateReportJson,
            Response.Listener<JSONObject> { response ->
                try {
                    val jsonMes = response.getString("mes")

                    if (jsonMes == "success") {
                        btn_create.text = "Done!"
                    }
                    else {
                        btn_create.text = "Try again"
                    }
                } catch (e: Exception) {
                    btn_create.text = "Try again"
                    Log.d("Listener Create report:", "$e")
//                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                btn_create.text = "Try again"
                Log.d("Error Create report:", "$it")
                val toast = Toast.makeText(
                    getActivity()?.getApplicationContext(),
                    "Error $it}",
                    Toast.LENGTH_LONG
                )
                toast.setGravity(
                    Gravity.TOP, 0, 0
                )
                toast.show()
            }, activity?.applicationContext
        )
        // Add the request to the RequestQueue.
        queue.add(getRequest)
    }

    fun displayImg() {
        val displayView = myView.findViewById<ImageView>(R.id.imageView_camera)
        val textDisplayView = myView.findViewById<TextView>(R.id.textView_nopreview)
        textDisplayView.visibility = TextView.INVISIBLE
        Glide.with(activity!!).load(currentPhotoPath).into(displayView)
    }

}
