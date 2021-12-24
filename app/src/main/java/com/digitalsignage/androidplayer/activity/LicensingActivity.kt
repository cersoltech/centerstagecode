package com.digitalsignage.androidplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.util.Log
import android.util.Xml
import android.view.OrientationEventListener
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.digitalsignage.androidplayer.BR
import com.digitalsignage.androidplayer.BuildConfig
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.base.BaseActivity
import com.digitalsignage.androidplayer.databinding.ActivityLicensingBinding
import com.digitalsignage.androidplayer.fileutils.FileUtility.Companion.decryptText
import com.digitalsignage.androidplayer.fileutils.FileUtility.Companion.getDeviceId
import com.digitalsignage.androidplayer.utils.*
import com.digitalsignage.androidplayer.viewmodel.LicensingViewmodel
import com.digitalsignage.androidplayer.viewmodel.LoginViewModel
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import kotlinx.android.synthetic.main.activity_licensing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xmlpull.v1.XmlSerializer
import java.io.*
import java.lang.Boolean
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class LicensingActivity : BaseActivity<ActivityLicensingBinding, LoginViewModel>() {

    private val destFileName: String = "license.txt"
    private val TAG = LicensingActivity::class.java.simpleName
    private val PERMS_REQUEST_CODE = 1202
    private val PICK_FILE = 1203
    private var expiryDate: String = ""
    private var fileUri: Uri? = null
    private var filePath: String? = null
    val zipTypes = arrayOf("txt")

    private var isOnlineReg = false

    override fun getViewModel(): LoginViewModel = LoginViewModel()
    override fun getContentLayout(): Int = R.layout.activity_licensing
    override fun getBindingVariable(): Int = BR.licenseviewmodel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()

        getDeviceId(this)?.let {
            val deviceudid = resources.getString(R.string.device_udid)
            mBinding!!.txtDeviceudid.text = "$deviceudid $it"
        }

        mBinding!!.radioGroupRegister.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = findViewById<RadioButton>(checkedId)
            when(checkedId)
            {
                R.id.rdBtn_onlinereg -> {
                    ll_offlinereg.visibility = View.GONE
                    ll_onlinereg.visibility = View.VISIBLE
                    isOnlineReg = true
                }
                R.id.rdBtn_offlinereg -> {
                    ll_offlinereg.visibility = View.VISIBLE
                    ll_onlinereg.visibility = View.GONE
                    isOnlineReg = false
                }
            }
        }

        mBinding!!.edtAttachfile.setOnClickListener {
            checkStoragePermission()
        }
        Log.d(TAG, "onCreate: ${getFileStreamPath("applicen.txt")}")
        Log.d("deviceid", "onCreate: ${getDeviceId(this)}")
        //readXmlFile()
        // /data/user/0/com.digitalsignage.androidplayer/files/applicen.txt

        mBinding!!.btnSubmit.setOnClickListener {
            if (mBinding!!.radioGroupRegister.checkedRadioButtonId == -1)
            {
                // no radio buttons are checked
                Toast.makeText(this, "Please select license register type", Toast.LENGTH_SHORT).show()
            }else
            {
                PreferanceRepository.setBoolean(Constants.islicenseOnlineRegister,isOnlineReg)
                if (isOnlineReg)
                {
                    setupObservers()
                }else{
                    mBinding!!.layoutLoader.pbLoader.visibility = View.VISIBLE
                    if (filePath.toString() != null && filePath?.isNotEmpty() == true)
                    {
                        val file = File(filePath)

                        // val selectedFilePath: String = FilePath.getPath(this, fileUri)
                        /* val file = File(fileUri?.path?.replace("document/primary:",
                             Environment.getExternalStorageDirectory().toString() + "/"))*/
                        Log.d(TAG, "onCreate src path: ${file.path}")
                        ///document/primary:WhatsApp/Media/WhatsApp Documents/Executive03082021.pdf
                        //val dst = File(getPath(this)+"license.pdf")
                        /* val uri = FileProvider.getUriForFile(
                                 this,
                                 this.applicationContext.packageName + ".provider",
                                 file
                         )
                         val fileUri = File(uri.path)*/
                        //Log.e(TAG, "onCreate: ${fileUri.name}")
                        //val srcfile = File(uri?.path)
                        val dst = File(this.filesDir.toString() + File.separator + "player/" + "license.txt")
                        copyFile(file, dst, destFileName)
                    }else{
                        mBinding!!.layoutLoader.pbLoader.visibility = View.GONE
                        Toast.makeText(this, "Please submit valid license", Toast.LENGTH_SHORT).show()
                    }
                }
                /*PreferanceRepository.setBoolean(Constants.isLicenseActive, true)
                if (!expiryDate.isNullOrEmpty() && expiryDate != "")
                {
                    PreferanceRepository.setString(Constants.expiryDate, expiryDate)
                }*/
                //startActivity(Intent(this, DeviceInfoActivity::class.java))
                //finish()
            }
        }
    }

    private fun setupObservers() {
        getDeviceId(this)?.let { mViewModel?.getLicenseApiData(it) }
        Log.d("imei", "imei: ${getDeviceId(this)}")
        mViewModel!!.getLicenseData.observe(this, {
            when (it) {
                is State.Loading -> {
                    AppLog.i(TAG,"license loading")
                }

                is State.Error -> {
                    AppLog.i(TAG,"license error")
                }
                is State.Success -> {
                    AppLog.d(tag = TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.status) {
                            if (it.data.data != null) {
                                PreferanceRepository.setBoolean(Constants.isLicenseActive, true)
                                Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, DeviceInfoActivity::class.java))
                                finish()
                            }
                        }else
                        {
                            Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun createTextFile() {
        val dir = File(this.filesDir, "player")
        //Environment.getExternalStorageDirectory();
        if (!dir.exists()) {
            dir.mkdir()
        }
        /*try {
            val gpxfile = File(dir, "/applicen.xml")
            val writer = FileWriter(gpxfile)
            writer.append("test")
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
        val filename = "applicen.txt"

        if (File(this.filesDir.toString() + File.separator + "player/" + filename).exists())
        {
            Log.d(TAG, "createTextFile: file alreday present")
        }else
        {
            val file = File(this.filesDir.toString() + File.separator + "player/" + filename)
            //val fos: FileOutputStream = openFileOutput(filename, Context.MODE_APPEND)
            val fos: FileOutputStream = FileOutputStream(file, true)

            val serializer: XmlSerializer = Xml.newSerializer()
            serializer.setOutput(fos, "UTF-8")
            serializer.startDocument(null, Boolean.valueOf(true))
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)

            Log.d("deviceid", "onCreate: ${getDeviceId(this)}")

            val date = Date()
            val c = Calendar.getInstance()
            println("Current time => $c")
            val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val currentDate =  df.format(date)
            // now add 30 day in Calendar instance
            c.add(Calendar.DAY_OF_YEAR, 30)
            val resultDate: Date = c.time
            expiryDate = df.format(resultDate)
            //Log.d(TAG, "createTextFile: currentdate - $currentDate / $expiryDate")

            //get encrypted softwarekey
            val cryptolib = Cryptolib()
            val encryptedDate = cryptolib.encrypt(
                    currentDate,
                    BuildConfig.LOGIN_KEY,
                    BuildConfig.LOGIN_IV
            )
            //decryptedOtp = _crypt.decrypt(it.body()!!.data.oTP, passkey, "0000000000000000")

            serializer.startTag(null, "root")
            serializer.startTag(null, "softwarekey")
            serializer.text(encryptedDate)
            serializer.endTag(null, "softwarekey")
            serializer.startTag(null, "imeinumner")
            serializer.text(getDeviceId(this))
            serializer.endTag(null, "imeinumner")
            serializer.startTag(null, "dateofexpiry")
            serializer.text(expiryDate)
            serializer.endTag(null, "dateofexpiry")

            serializer.endDocument()
            serializer.flush()

            fos.close()
        }

    }

    private fun readXmlFile(){
        try {
            //getFileStreamPath("applicen.txt")
            //val fis: InputStream = assets.open("file.xml")
            //val file = getFileStreamPath("applicen.txt")
            val file = File(this.filesDir.toString() + File.separator + "player/" + destFileName)
            val fis: InputStream = FileInputStream(file.path)
            val dbFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val dBuilder: DocumentBuilder = dbFactory.newDocumentBuilder()
            val doc: Document = dBuilder.parse(fis)
            val element: Element = doc.documentElement
            element.normalize()
            val nList: NodeList = doc.getElementsByTagName("root")
            for (i in 0 until nList.length) {
                val node: Node = nList.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val element2: Element = node as Element
                    Log.d("xmldata", "readXmlFile: " + getValue("softwarekey", element2) + "\n")
                    Log.d("xmldata", "readXmlFile1: " + getValue("imeinumber", element2) + "\n")
                    Log.d("xmldata", "readXmlFile2: " + getValue("dateofexpiry", element2))

                    if (getValue("softwarekey", element2) != null)
                    {
                     /* val cryptolib = Cryptolib()
                      val decryptedKey = cryptolib.decrypt(getValue("softwarekey", element2), BuildConfig.LOGIN_KEY, "0000000000000000")
                        Log.d(TAG, "decryptedKey: $decryptedKey")*/

                        var decryptedKey = decryptText(getValue("softwarekey", element2).toString())
                        decryptedKey = decryptedKey.replace("<","2").replace(">","4")
                        Log.d(TAG, "readXmlFile: decryppt text - $decryptedKey")
                        val splitData = decryptedKey?.split("_")
                        val expiryDateOfLicense = splitData?.get(0)
                        val imeiNo = splitData?.get(1)
                        expiryDate = expiryDateOfLicense
                        Log.d(TAG, "readXmlFile: $expiryDate $imeiNo")

                        getDeviceId(this)?.let {
                            deviceId ->
                            if (!imeiNo.isNullOrBlank())
                            {
                               if (imeiNo != deviceId){
                                   mBinding!!.layoutLoader.pbLoader.visibility = View.GONE
                                   Toast.makeText(this, "Please submit valid license", Toast.LENGTH_SHORT).show()
                               }else
                               {
                                   if (isLicenseExpired(expiryDateOfLicense!!))
                                   {
                                       mBinding!!.layoutLoader.pbLoader.visibility = View.GONE
                                       Toast.makeText(this, "Please submit valid license", Toast.LENGTH_SHORT).show()
                                   }else
                                   {
                                       mBinding!!.layoutLoader.pbLoader.visibility = View.GONE
                                       expiryDate = expiryDateOfLicense
                                       PreferanceRepository.setBoolean(Constants.isLicenseActive, true)
                                       if (!expiryDate.isNullOrEmpty() && expiryDate != "")
                                       {
                                           PreferanceRepository.setString(Constants.expiryDate, expiryDate)
                                       }
                                       startActivity(Intent(this, DeviceInfoActivity::class.java))
                                       finish()
                                   }
                               }
                            }
                        }
                    }else{
                        Log.d(TAG, "readXmlFile: softwarekey else")
                    }
                    if (getValue("dateofexpiry", element2) != null)
                    {
                       /* if (isLicenseExpired(getValue("dateofexpiry", element2)!!))
                        {
                            mBinding!!.layoutLoader.pbLoader.visibility = View.GONE
                            Toast.makeText(this, "Please submit valid license", Toast.LENGTH_SHORT).show()
                        }else
                        {
                            mBinding!!.layoutLoader.pbLoader.visibility = View.GONE
                            expiryDate = getValue("dateofexpiry", element2)!!
                            PreferanceRepository.setBoolean(Constants.isLicenseActive, true)
                            if (!expiryDate.isNullOrEmpty() && expiryDate != "")
                            {
                                PreferanceRepository.setString(Constants.expiryDate, expiryDate)
                            }
                            startActivity(Intent(this, DeviceInfoActivity::class.java))
                            finish()
                        }*/
                    }else
                    {
                        mBinding!!.layoutLoader.pbLoader.visibility = View.GONE
                        Toast.makeText(this, "Please submit valid license", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Please submit valid file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun getValue(tag: String, element: Element): String? {
        val nodeList = element.getElementsByTagName(tag).item(0).childNodes
        val node = nodeList.item(0)
        return node.nodeValue
    }

    private fun checkStoragePermission() {
        if (hasPermissions(this)) {
            // our app has permissions.
            //createTextFile()
            //openDocumentDialog()
            openFilePicker()
        } else {
            //our app doesn't have permissions, So requesting permissions.
            requestPerms()
        }
    }

    private fun openFilePicker(){
        FilePickerBuilder.instance
                .setMaxCount(1) //optional
                .enableVideoPicker(false)
                .enableCameraSupport(false)
                //.enableDocSupport(false)
                .enableSelectAll(false)
                .sortDocumentsBy(SortingTypes.NAME)
                //.withOrientation(Orientation.PORTRAIT_ONLY)
                .addFileSupport("Files", zipTypes, R.drawable.icon_file_doc)
                //.setSelectedFiles(filePaths) //optional
                //.setActivityTheme(R.style.LibAppTheme) //optional
                .pickFile(this)
    }

    private fun openDocumentDialog() {
        val intent = Intent()
        val mimetypes = arrayOf("text/plain", "application/*")
        intent.type = "application/*"
        //intent.type = "text/plain"
        //intent.type = "text/plain|application/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerConst.REQUEST_CODE_DOC && resultCode == Activity.RESULT_OK) {
            val dataList: ArrayList<Uri> =
                    data?.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)!!
            if (dataList.size != 0)
            {
                val extension = getFileExtension(File(getRealPathFromURI(dataList[0])))
                if(extension == ".txt"){
                    filePath = getRealPathFromURI(dataList[0])

                    if (!filePath.isNullOrBlank())
                    {
                        val fileName = getFileName(filePath!!)
                        mBinding!!.txtFilename.visibility = View.VISIBLE
                        mBinding!!.txtFilename.text = fileName
                    }
                } else {
                    //(extension == ".zip" || extension == ".pdf" || extension == ".ppt" || extension == ".docx" )
                    Toast.makeText(this, "Please select valid file", Toast.LENGTH_SHORT).show()
                }
            }else
            {
                mBinding!!.txtFilename.visibility = View.GONE
            }

            Log.e("docType", contentResolver.getType(dataList[0])!!)
            Log.e(TAG, "onActivityResult: ${getRealPathFromURI(dataList[0])}", )
        }
       /* if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null)
        {
            fileUri = data.data

            //check extension and allowed txt,xml file only
           *//* val srcPath = fileUri?.let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                getPathFromUri(this, it)
            } else {
                //handle lower version
            }
            }*//*

            //val selectedFilePath = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)
            Log.d(TAG, "onActivityResult: $fileUri \n ${getRealPathFromURI(fileUri!!)}" )
        }*/
    }


    private fun getRealPathFromURI(contentURI: Uri) : String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        @SuppressWarnings("deprecation")
        val cursor = contentResolver.query(
                contentURI, projection, null,
                null, null
        )
                ?: return ""
        val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        if (cursor.moveToFirst()) {
            val s = cursor.getString(column_index)
            if (s != null) {
                // cursor.close();
                return s
            }else{
                return ""
            }
        }
        // cursor.close();
        return ""
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allowed = true

        when (requestCode) {
            PERMS_REQUEST_CODE ->
                for (res in grantResults) {
                    allowed = allowed && res == PackageManager.PERMISSION_GRANTED
                }
            else ->
                allowed = false
        }
        if (allowed) {
            //createTextFile()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun requestPerms() {
        val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMS_REQUEST_CODE)
        }
    }



    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File, fileName: String) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        val expFile =
            File(destFile.path + File.separator.toString() + fileName)
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
            //source.transferTo(0, source.size(), destination)
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
        readXmlFile()
    }



    fun getPath(context: Context): String {
        val path = File(context.filesDir, "player")
        return path.absolutePath
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getPathFromUri(context: Context, uri: Uri): String? {

// check here to KITKAT or new version
        val  isKitKat: kotlin.Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

// DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val  docId: String = DocumentsContract.getDocumentId(uri);
                val split = docId.split(":")
                val  type:String = split[0];

                if ("primary".equals(type, true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                val  id: String = DocumentsContract.getDocumentId(uri);
                /*val  contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        id.toLong());*/

                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }

                val contentUriPrefixesToTry = arrayOf(
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads",
                        "content://downloads/all_downloads"
                )

                var contentUri: Uri;
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), java.lang.Long.valueOf(id))
                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (path != null) {
                            return path
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }
                //return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                var  docId: String = DocumentsContract.getDocumentId(uri);
                var split = docId.split(":");
                var  type: String = split[0];

                 var contentUri: Uri? = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                var  selection: String = "_id=?";
                var selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
// MediaStore (and general)
        else if ("content".equals(uri.getScheme(), true)) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
// File
        else if ("file".equals(uri.getScheme(), true)) {
            return uri.getPath();
        }

        return null;
    }
    private fun getDataColumn(context: Context, uri: Uri?,
                              selection: String?, selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val  column: String = "_data"
        val projection = arrayOf(column)

        try {
            uri?.let {
                cursor = context.getContentResolver().query(uri, projection,
                        selection, selectionArgs, null)
                }
            if (cursor != null && cursor!!.moveToFirst()) {
                val index = cursor!!.getColumnIndexOrThrow(column);
                return cursor!!.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor!!.close();
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): kotlin.Boolean {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority())
    }
    fun isDownloadsDocument(uri: Uri): kotlin.Boolean {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority())
    }
    fun isMediaDocument(uri: Uri) : kotlin.Boolean {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority())
    }

       fun isGooglePhotosUri(uri: Uri) : kotlin.Boolean{
            return "com.google.android.apps.photos.content".equals(uri
                    .getAuthority())
        }
}