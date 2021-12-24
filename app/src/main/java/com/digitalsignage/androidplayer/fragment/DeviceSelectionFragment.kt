package com.digitalsignage.androidplayer.fragment

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.digitalsignage.androidplayer.BR
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.activity.MainActivity
import com.digitalsignage.androidplayer.activity.VideoActivity
import com.digitalsignage.androidplayer.base.BaseFragment
import com.digitalsignage.androidplayer.databinding.DesignLayout3Binding
import com.digitalsignage.androidplayer.fileutils.ExternalFileHandling
import com.digitalsignage.androidplayer.fileutils.FileUtility
import com.digitalsignage.androidplayer.model.response.DeviceTemplateData
import com.digitalsignage.androidplayer.utils.*
import com.digitalsignage.androidplayer.viewmodel.LoginViewModel
import kotlinx.coroutines.*
import java.io.File

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DeviceSelectionFragment : BaseFragment<DesignLayout3Binding, LoginViewModel>() {

    private lateinit var mContext: Context
    private val TAG = DeviceSelectionFragment::class.java.simpleName

    private var selectedDeviceGroupId: String = ""
    private var fileName: String? = ""
    val demoList = arrayListOf("Select", "Zone 1", "Zone 2", "Zone 3")

    private val PERMS_REQUEST_CODE = 1201

    private val zoneNameList = ArrayList<String>()
    private val zoneIDList = ArrayList<Int>()
    private val cityNameList = ArrayList<String>()
    private val cityIDList = ArrayList<Int>()
    private val branchNameList = ArrayList<String>()
    private val branchIDList = ArrayList<Int>()
    private val deviceGrpNameList = ArrayList<String>()
    private val deviceGrpIDList = ArrayList<Int>()
    private val deviceNameList = ArrayList<String>()
    private val deviceIDList = ArrayList<Int>()

    lateinit var spnrZoneAdapter: ArrayAdapter<String>
    lateinit var spnrCityAdapter: ArrayAdapter<String>
    lateinit var spnrBranchAdapter: ArrayAdapter<String>
    lateinit var spnrDeviceGroupAdapter: ArrayAdapter<String>
    lateinit var spnrDeviceAdapter: ArrayAdapter<String>

    var assetUrl: String? = ""

    var zoneName = ""
    var cityName = ""
    var branchName = ""
    var deviceGroupName = ""
    var deviceName = ""

    //private var mEventsMap = LinkedHashMap<String, List<ScheduleData>>()
    private var mEventsMap = LinkedHashMap<String, DeviceTemplateData>()

    private var downloadedFiles = ArrayList<File>()
    private var downloadedFilesNameList = ArrayList<String>()
    private var downloadedFileName: String = ""

    private var checkVideos = ArrayList<String>()

    //Create a cancellable job
    private var job: Job? = null

    var isUserClickedOnUI: Boolean = false

    override fun getViewModel(): LoginViewModel = LoginViewModel()

    override fun getFragmentLayout(): Int = R.layout.design_layout3

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = initBinding(inflater, container)
        setUpLoader(mBinding!!.layoutLoader)

        //return inflater.inflate(R.layout.fragment_device_selection, container, false)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        mBinding!!.btnSubmit.setOnClickListener {
            PreferanceRepository.setString(Constants.Zone_Name,zoneName)
            PreferanceRepository.setString(Constants.City_Name,cityName)
            PreferanceRepository.setString(Constants.Branch_Name,branchName)
            PreferanceRepository.setString(Constants.Device_Group_Name,deviceGroupName)
            PreferanceRepository.setString(Constants.Device_Name,deviceName)

            if (validateData())
            {
                if (isUserClickedOnUI)
                {
                    val intent = Intent(activity, MainActivity::class.java)
                    //val intent = Intent(activity, VideoActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        //if data is already filled then only navigate
        //callMainActivity()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        callMainActivity()
    }

    override fun onDestroyView() {
        job?.cancel()
        super.onDestroyView()
    }
    
    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun callMainActivity() {
        job?.cancel()
        job = GlobalScope.launch {
            delay(5000)
            //if (validateData())
            if (PreferanceRepository.getString(Constants.Zone_Name).isNotEmpty() &&
                PreferanceRepository.getString(Constants.City_Name).isNotEmpty() &&
            PreferanceRepository.getString(Constants.Branch_Name).isNotEmpty() &&
            PreferanceRepository.getString(Constants.Device_Group_Name).isNotEmpty() &&
            PreferanceRepository.getString(Constants.Device_Name).isNotEmpty())
            {
                if (!isUserClickedOnUI)
                {
                    val intent = Intent(activity, MainActivity::class.java)
                    //val intent = Intent(activity, VideoActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateData(): Boolean{
     return when{
            zoneName.isBlank() -> {
                Toast.makeText(this.requireActivity(), "Please select zone", Toast.LENGTH_SHORT).show()
                false
            }
         cityName.isBlank() -> {
             Toast.makeText(this.requireActivity(), "Please select city", Toast.LENGTH_SHORT).show()
             false
         }
         branchName.isBlank() -> {
             Toast.makeText(this.requireActivity(), "Please select branch", Toast.LENGTH_SHORT).show()
             false
         }
         deviceGroupName.isBlank() -> {
             Toast.makeText(this.requireActivity(), "Please select device group", Toast.LENGTH_SHORT).show()
             false
         }
         deviceName.isBlank() -> {
             Toast.makeText(this.requireActivity(), "Please select device", Toast.LENGTH_SHORT).show()
             false
         }
            else -> true
       }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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
            checkCreatedFolder()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    Toast.makeText(activity!!, "Storage Permissions denied.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeviceSelectionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeviceSelectionFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private fun setupObservers() {

        mViewModel?.getAssetDownloadScheduleData(getCurrentdate("dd-MM-yyyy"))
        mViewModel?.getZonesData()

        spnrZoneAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item, zoneNameList)
        mBinding!!.spinnerZone.adapter = spnrZoneAdapter

        spnrCityAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item, cityNameList)
        mBinding!!.spinnerCity.adapter = spnrCityAdapter

        spnrBranchAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item, branchNameList)
        mBinding!!.spinnerBranch.adapter = spnrBranchAdapter

        spnrDeviceGroupAdapter = ArrayAdapter<String>(
            activity!!,
            R.layout.spinner_item,
            deviceGrpNameList
        )
        mBinding!!.spinnerDeviceGroup.adapter = spnrDeviceGroupAdapter

        spnrDeviceAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner_item, deviceNameList)
        mBinding!!.spinnerDevice.adapter = spnrDeviceAdapter

        mViewModel!!.assetDownloadScheduleData.observe(viewLifecycleOwner, {
            when (it) {
                is State.Success -> {
                    AppLog.d(TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.data != null && it.data.data!!.isNotEmpty()) {
                            val data = it.data.data
                            if (data?.size!! > 0) {
                                //saved in sharedpref
                                saveTemplateAssetOfflineData(it.data)

                                checkStoragePermission()
                            }
                        }
                    }
                }
            }
        })

        mViewModel!!.zoneData.observe(viewLifecycleOwner, {
            zoneNameList.clear()
            zoneIDList.clear()
            when (it) {
                is State.Success -> {
                    AppLog.d(TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.data!!.isNotEmpty()) {
                            //zoneList.addAll(it.data.data!!)
                            zoneNameList.add("Select Zone")
                            zoneIDList.add(0)

                            it.data.data!!.forEach { i ->
                                zoneNameList.add(i.name!!)
                                zoneIDList.add(i.id!!)
                            }

                            spnrZoneAdapter.notifyDataSetChanged()

                            if (PreferanceRepository.getString(Constants.Zone_Name).isNotEmpty())
                            {
                                zoneNameList.forEachIndexed { index, s ->
                                    if (PreferanceRepository.getString(Constants.Zone_Name) == s)
                                    {
                                        mBinding!!.spinnerZone.setSelection(index, true)
                                    }
                                }
                            }
                        } else {
                            spnrZoneAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })



        mViewModel!!.cityData.observe(viewLifecycleOwner, {
            cityNameList.clear()
            cityIDList.clear()
            when (it) {
                is State.Success -> {
                    AppLog.d(TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.data!!.isNotEmpty()) {
                            //zoneList.addAll(it.data.data!!)
                            cityNameList.add("Select City")
                            cityIDList.add(0)
                            it.data.data!!.forEach { i ->
                                cityNameList.add(i?.name!!)
                                cityIDList.add(i.id!!)
                            }
                            spnrCityAdapter.notifyDataSetChanged()

                            if (PreferanceRepository.getString(Constants.City_Name).isNotEmpty())
                            {
                                cityNameList.forEachIndexed { index, s ->
                                    if (PreferanceRepository.getString(Constants.City_Name) == s)
                                    {
                                        mBinding!!.spinnerCity.setSelection(index, true)
                                    }
                                }
                            }
                        } else {
                            spnrCityAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })


        mViewModel!!.branchData.observe(viewLifecycleOwner, {
            branchNameList.clear()
            branchIDList.clear()
            when (it) {
                is State.Success -> {
                    AppLog.d(TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.data!!.isNotEmpty()) {
                            //zoneList.addAll(it.data.data!!)
                            branchNameList.add("Select Branch")
                            branchIDList.add(0)
                            it.data.data!!.forEach { i ->
                                branchNameList.add(i.branchName!!)
                                branchIDList.add(i.id!!)
                            }
                            spnrBranchAdapter.notifyDataSetChanged()
                            if (PreferanceRepository.getString(Constants.Branch_Name).isNotEmpty())
                            {
                                branchNameList.forEachIndexed { index, s ->
                                    if (PreferanceRepository.getString(Constants.Branch_Name) == s)
                                    {
                                        mBinding!!.spinnerBranch.setSelection(index, true)
                                    }
                                }
                            }
                        } else {
                            spnrBranchAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        mViewModel!!.deviceGroupData.observe(viewLifecycleOwner, {
            deviceGrpNameList.clear()
            deviceGrpIDList.clear()
            when (it) {
                is State.Success -> {
                    AppLog.d(TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.data!!.isNotEmpty()) {
                            //zoneList.addAll(it.data.data!!)
                            deviceGrpNameList.add("Select Device Group")
                            deviceGrpIDList.add(0)
                            it.data.data!!.forEach { i ->
                                deviceGrpNameList.add(i.name!!)
                                deviceGrpIDList.add(i.id!!)
                            }
                            spnrDeviceGroupAdapter.notifyDataSetChanged()

                            if (PreferanceRepository.getString(Constants.Device_Group_Name).isNotEmpty())
                            {
                                deviceGrpNameList.forEachIndexed { index, s ->
                                    if (PreferanceRepository.getString(Constants.Device_Group_Name) == s)
                                    {
                                        mBinding!!.spinnerDeviceGroup.setSelection(index, true)
                                    }
                                }
                            }
                        } else {
                            spnrDeviceGroupAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        mViewModel!!.deviceData.observe(viewLifecycleOwner, {
            deviceNameList.clear()
            deviceIDList.clear()
            when (it) {
                is State.Success -> {
                    AppLog.d(TAG, "Success: ")
                    if (it.code == 200) {
                        //spnrDeviceAdapter.clear()
                        if (it.data.data!!.isNotEmpty()) {
                            //zoneList.addAll(it.data.data!!)
                            deviceNameList.add("Select Device")
                            deviceIDList.add(0)
                            it.data.data!!.forEach { i ->
                                if (i.deviceGroupId != null && i.deviceGroupId!!.isNotEmpty()) {
                                    if (selectedDeviceGroupId.equals(i.deviceGroupId)) {
                                        deviceNameList.add(i.deviceName!!)
                                        deviceIDList.add(i.id!!)
                                    }
                                }
                            }
                            //mBinding?.spinnerDevice?.setSelection(0)
                            spnrDeviceAdapter.notifyDataSetChanged()
                            if (PreferanceRepository.getString(Constants.Device_Name).isNotEmpty())
                            {
                                deviceNameList.forEachIndexed { index, s ->
                                    if (PreferanceRepository.getString(Constants.Device_Name) == s)
                                    {
                                        mBinding!!.spinnerDevice.setSelection(index, true)
                                    }
                                }
                            }

                        } else {
                            spnrDeviceAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })


        mBinding!!.spinnerZone.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        PreferanceRepository.setInt(Constants.ZONE_ID, zoneIDList[position])
                        zoneName = zoneNameList[position]
                    }
                    AppLog.d(TAG, "ZoneID: " + zoneIDList[position].toString())
                    mViewModel?.getCitiesData(zoneId = zoneIDList[position].toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        //mBinding!!.spinnerCity.adapter = ArrayAdapter<String>(this,R.layout.spinner_item,demoList)

        mBinding!!.spinnerCity.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        PreferanceRepository.setInt(Constants.CITY_ID, cityIDList[position])
                        cityName = cityNameList[position]
                    }
                    AppLog.d(TAG, "CityID: " + cityIDList[position].toString())
                    mViewModel?.getBranchData(cityId = cityIDList[position].toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        //mBinding!!.spinnerBranch.adapter = ArrayAdapter<String>(this,R.layout.spinner_item,demoList)

        mBinding!!.spinnerBranch.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        PreferanceRepository.setInt(Constants.BRANCH_ID, branchIDList[position])
                        branchName = branchNameList[position]
                    }
                    AppLog.d(TAG, "BranchID: " + branchIDList[position].toString())
                    mViewModel?.getDeviceGroupData(branchId = branchIDList[position].toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        //mBinding!!.spinnerDeviceGroup.adapter = ArrayAdapter<String>(this,R.layout.spinner_item,demoList)
        mBinding!!.spinnerDeviceGroup.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        PreferanceRepository.setInt(
                            Constants.DEVICEGRP_ID,
                            deviceGrpIDList[position]
                        )
                        deviceGroupName = deviceGrpNameList[position]

                        selectedDeviceGroupId = deviceGrpIDList[position].toString()
                        AppLog.d(TAG, "DeviceGrpID: " + deviceGrpIDList[position].toString())
                        mViewModel?.getDeviceData()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        //mBinding!!.spinnerDevice.adapter = ArrayAdapter<String>(this,R.layout.spinner_item,demoList)
        mBinding!!.spinnerDevice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    AppLog.d(TAG, "DeviceID: " + deviceIDList[position].toString())
                    if (position > 0) {
                        PreferanceRepository.setString(
                            Constants.DEVICE_ID,
                            deviceIDList[position].toString()
                        )
                        deviceName = deviceNameList[position]
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        mBinding!!.spinnerZone.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) isUserClickedOnUI = true
            return@setOnTouchListener false
        }
        mBinding!!.spinnerCity.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) isUserClickedOnUI = true
            return@setOnTouchListener false
        }
        mBinding!!.spinnerBranch.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) isUserClickedOnUI = true
            return@setOnTouchListener false
        }
        mBinding!!.spinnerDeviceGroup.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) isUserClickedOnUI = true
            return@setOnTouchListener false
        }
        mBinding!!.spinnerDevice.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) isUserClickedOnUI = true
            return@setOnTouchListener false
        }
    }

    private fun checkStoragePermission() {
        if (hasPermissions()) {
            // our app has permissions.
           //mBinding?.layoutLoader!!.pbLoader.visibility = View.VISIBLE
            checkCreatedFolder()
        } else {
            //our app doesn't have permissions, So i m requesting permissions.
            requestPerms()
        }
    }

    private fun checkCreatedFolder() {
        //FileUtility.showProgress(mContext)

        val assetPrefData = getTemplateAssetStoredResponse()

        if (assetPrefData != null) {
            val scheduleList = assetPrefData.data
            if (scheduleList?.size!! > 0) {
                scheduleList.forEach { scheduleListData ->
                    val templateData = scheduleListData.deviceTemplateData
                    val templateAssets = templateData?.templateAssets
                    //get logo image if present
                    if (templateData?.logo != null && templateData.logo!!.isNotEmpty()) {
                        assetUrl = Constants.Video_Base_URL + templateData.logo
                        fileName = getFileName(assetUrl!!)
                        val logoTag = "logo"
                        //logo image syntax - scheduleid_zoneid_cityid_branchid_devicegrpid_deviceid_logo_templateid_imagename.jpeg
                        fileName =
    "${scheduleListData.id}_${scheduleListData.zoneId}_${scheduleListData.cityId}_${scheduleListData.branchId}_" +
            "${scheduleListData.deviceGroupId}_${scheduleListData.deviceId}_${logoTag}_${templateData.templateId}_$fileName"
                        AppLog.d(TAG, "Image FileName: $fileName")

                        val ifPresent = /*checkIfFileAlreadyPresent(activity!!, fileName!!)*/ ExternalFileHandling.isFileExists(
                            fileName!!
                        )

                        Log.d(TAG, "checkCreatedFolder: img checkIfFileAlreadyPresent $ifPresent")
                        if (ifPresent) {
                            Log.d(TAG, "checkCreatedFolder: $fileName not needed to download again")
                        } else {
                            Log.d(TAG, "checkCreatedFolder:$fileName need to download file")
                            //ExternalFileHandling.videoDownloader(activity!!, assetUrl!!, fileName!!)
                            ExternalFileHandling.downloadFileFromManager(
                                activity!!,
                                assetUrl!!,
                                fileName!!
                            )
                        }
                    }

                    templateAssets?.forEach { assetData ->
                        assetUrl = Constants.Video_Base_URL + assetData.assetUrl
                        fileName = getFileName(assetUrl!!)
                        //scheduleid_deviceid_boxno_templateid_videoname.mp4
                        fileName =
   "${scheduleListData.id}_${scheduleListData.zoneId}_${scheduleListData.cityId}_${scheduleListData.branchId}_" +
           "${scheduleListData.deviceGroupId}_${scheduleListData.deviceId}_${assetData.boxNo}_${templateData.templateId}_$fileName"
                        AppLog.d(TAG, "FileName: $fileName")
                        //It should check before downloading. If file exists, it should return
                        // or it should delete and then try to download the new file.
                        val ifVideoPresent = /*checkIfFileAlreadyPresent(activity!!, fileName!!)*/ ExternalFileHandling.isFileExists(
                            fileName!!
                        )
                        Log.d(TAG, "checkCreatedFolder: checkIfFileAlreadyPresent $ifVideoPresent")

                        if (ifVideoPresent) {
                            Log.d(TAG, "checkCreatedFolder: $fileName not needed to download again")
                        } else {
                            Log.d(TAG, "checkCreatedFolder:$fileName need to download")
                            Log.d(TAG, "checkCreatedFolder: $assetUrl")
                            //ExternalFileHandling.videoDownloader(activity!!, assetUrl!!, fileName!!)
                            ExternalFileHandling.downloadFileFromManager(
                                activity!!,
                                assetUrl!!,
                                fileName!!
                            )
                        }
                    }

                }
            }
        }
    }

    private fun hasPermissions(): Boolean {
        var res: Int
        //string array of permissions,
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        for (perms in permissions) {
            res = activity!!.checkCallingOrSelfPermission(perms)
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
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

}