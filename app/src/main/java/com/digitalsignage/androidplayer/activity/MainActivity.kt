package com.digitalsignage.androidplayer.activity

import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.digitalsignage.androidplayer.BR
import com.digitalsignage.androidplayer.DownloadBroadcastReceiver
import com.digitalsignage.androidplayer.OnDownloadListener
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.adapter.TokenCounterAdapter
import com.digitalsignage.androidplayer.base.BaseActivity
import com.digitalsignage.androidplayer.databinding.ActivityMainBinding
import com.digitalsignage.androidplayer.fileutils.*
import com.digitalsignage.androidplayer.fileutils.DialogHelper.showCustmizeDialog
import com.digitalsignage.androidplayer.model.TokenCounter
import com.digitalsignage.androidplayer.model.response.AssetDownloadScheduleResponse
import com.digitalsignage.androidplayer.model.response.GetTemplatesResponse
import com.digitalsignage.androidplayer.model.response.ScheduleData
import com.digitalsignage.androidplayer.model.response.TemplateAsset
import com.digitalsignage.androidplayer.utils.*
import com.digitalsignage.androidplayer.viewmodel.HomeViewModel
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.*
import java.io.File


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MainActivity : BaseActivity<ActivityMainBinding, HomeViewModel>(),CustDialogListerner,
    TokenCounterAdapter.ItemLongPressListener, OnDownloadListener, OnUdpListerner, TokenCounterAdapter.RepeatTokenCallback, SettingDialogListener {
    private val TAG = MainActivity::class.java.simpleName

    companion object {
        var resumeAt: Long = 0L
    }
    //private lateinit var tokenCounterList: ArrayList<TokenCounter>
    private var tokenCounterList = ArrayList<TokenCounter>()
    private var udpTokenList = ArrayList<String>()
    private var udpListIndex = 0

    private var fileName: String = ""
    private var assetUrl: String = ""

    private var savedResScheduleId: Int? = null

    private var videoFiles = ArrayList<File>()
    private var templateLayoutId: Int = 0
    private lateinit var assetDownloadScheduleResponse: AssetDownloadScheduleResponse
    private var scheduleList = ArrayList<ScheduleData>()
    private var startTime: String? = null
    private var endTime: String? = null


    private var savedZoneId = ""
    private var savedCityId = ""
    private var savedBranchId = ""
    private var savedDeviceGrpId = ""
    private var savedDeviceId = ""

    //private lateinit var mViewStub: ViewStub

    private var downloadAllStatus = false

    private var imgLogo: ImageView? = null
    private var imgBoxOne: ImageView? = null
    private var imgBoxTwo: ImageView? = null
    private var imgBoxThree: ImageView? = null
    private var imgBoxFour: ImageView? = null

    private var videoBoxOne: PlayerView? = null
    private var videoBoxTwo: PlayerView? = null
    private var videoBoxThree: PlayerView? = null
    private var videoBoxFour: PlayerView? = null

    private var tickerText: MarqueeNoFocus? = null
    private var tokenRecyclerView: RecyclerView? = null
    private var llGreenView: LinearLayout? = null
    private var llTickerView: LinearLayout? = null
    private var tokenCounterView: ConstraintLayout? = null
    private var txtTicket: TextView? = null
    private var txtCounter: TextView? = null

    private var mCounter: TextView? = null
    private var mToken: TextView? = null
    private var rtToken: CardView? = null
    private var rtCounter: CardView? = null

    private var inflated: View? = null
    private lateinit var includedVideoView: View

    var isNextTokenCalled = false
    private var oldToken: String? = null
    private var oldCounter: String? = null
    private var oldTokenCounterObj: TokenCounter? = null
    private var blinkDuration: String = "5"
    private var soundDuration: String = "2"
    var selectedSoundName = ""

    lateinit var uri: Uri
    private var offlineVideoUrl1: Uri? = null
    private var offlineVideoUrl2: Uri? = null
    private var offlineVideoUrl3: Uri? = null
    private var offlineVideoUrl4: Uri? = null

    private var imgUrl: String = ""
    private var offlineImgUrl1: Uri? = null
    private var offlineImgUrl2: Uri? = null
    private var offlineImgUrl3: Uri? = null
    private var offlineImgUrl4: Uri? = null

    //Variables for ExoPlayer
    private var mPlayer1: SimpleExoPlayer? = null
    private lateinit var dataSourceFactory1: DataSource.Factory
    private lateinit var trackSelector1: DefaultTrackSelector
    private lateinit var trackSelectorParameters1: DefaultTrackSelector.Parameters

    private var mPlayer2: SimpleExoPlayer? = null
    private lateinit var dataSourceFactory2: DataSource.Factory
    private lateinit var trackSelector2: DefaultTrackSelector
    private lateinit var trackSelectorParameters2: DefaultTrackSelector.Parameters

    private var mPlayer3: SimpleExoPlayer? = null
    private lateinit var dataSourceFactory3: DataSource.Factory
    private lateinit var trackSelector3: DefaultTrackSelector
    private lateinit var trackSelectorParameters3: DefaultTrackSelector.Parameters

    private var mPlayer4: SimpleExoPlayer? = null
    private lateinit var dataSourceFactory4: DataSource.Factory
    private lateinit var trackSelector4: DefaultTrackSelector
    private lateinit var trackSelectorParameters4: DefaultTrackSelector.Parameters

    //variables for intent info
    private lateinit var recordingId: String
    private lateinit var recordIdentifier: String
    private lateinit var offset: Number
    private var isPlaybackEnded = false
    private var playbackPosition: Long = 0

    //Track selection dialog
    private var isShowingTrackSelectionDialog: Boolean = false

    private lateinit var adapterTokenCounter: TokenCounterAdapter
    private var getTemplatesResponse: GetTemplatesResponse? = null

    //arraylists to store offline data
    private var videoUrlOfflineList = ArrayList<Uri>()
    private var imageUrlOfflineList = ArrayList<Uri>()
    private var logoImageOfflineUrl: Uri? = null

    private var templateAssetVideoCount = 0
    private var templateAssetImageCount = 0
    private var templateAssetLogoImageCount = 0
    private var templateAssetTickerTextCount = 0

    private lateinit var udpSocketReceiver: UdpBroadcastReceiver

    override fun getViewModel(): HomeViewModel = HomeViewModel()

    override fun getContentLayout(): Int = R.layout.activity_main

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: called")
        initBinding()

        //mViewModel?.callAssetDownloadRepeat()
        //mViewStub = findViewById(R.id.layout_stub)
        initData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val decorView = this.window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        decorView.systemUiVisibility = uiOptions
            //val actionBar: ActionBar = this.actionBar!!
            //actionBar.hide()

        initBinding()
        //mViewStub = findViewById(R.id.layout_stub)

        DownloadBroadcastReceiver.setDownloadListener(this)
        UdpBroadcastReceiver.setUdpListener(this)
        mViewModel?.callAssetDownloadRepeat()

        //setUpLoader(mBinding!!.layoutLoader)
        initData()
        udpSocketReceiver = UdpBroadcastReceiver()
        val intentFilter = IntentFilter("MESSAGE_RECEIVED")
        registerReceiver(udpSocketReceiver, intentFilter)
    }



    private fun initData() {
        //tokenCounterList = FileUtility.getTokenList()
        //udpTokenList = PreferanceRepository.getUdpTokenList("UdpTokenList")?.toMutableList() as ArrayList<String>
        //call to udp listener
        mViewModel?.getUdpFromViewmodel(this)

        videoFiles = ExternalFileHandling.getStorageVideoFiles()

        savedZoneId = PreferanceRepository.getInt(Constants.ZONE_ID).toString()
        savedCityId = PreferanceRepository.getInt(Constants.CITY_ID).toString()
        savedBranchId = PreferanceRepository.getInt(Constants.BRANCH_ID).toString()
        savedDeviceGrpId = PreferanceRepository.getInt(Constants.DEVICEGRP_ID).toString()
        savedDeviceId = PreferanceRepository.getString(Constants.DEVICE_ID)

        //get shared pref stored response
        assetDownloadScheduleResponse = getTemplateAssetStoredResponse()!!

        scheduleList = assetDownloadScheduleResponse.data as ArrayList<ScheduleData>

        //scheduleList.forEach { scheduleData ->
        for (scheduleData in scheduleList)
        {
            val deviceTemplateData = scheduleData.deviceTemplateData
            if (savedDeviceId == scheduleData.deviceId && savedZoneId == scheduleData.zoneId &&
                    savedCityId == scheduleData.cityId && savedBranchId == scheduleData.branchId &&
                    savedDeviceGrpId == scheduleData.deviceGroupId) {
                startTime = scheduleData.startTime
                endTime = scheduleData.endTime

                val timeBetween = checkCurrentTime(startTime!!, endTime!!)
                Log.d(TAG, "onCreate: checkCurrentTime $timeBetween")

                if (checkIfStartTimeAfterCurrentTime(startTime!!))
                {
                  break
                }
                templateLayoutId = deviceTemplateData?.templateId?.toInt() ?: 0
                PreferanceRepository.setInt(Constants.Current_Device_Layout_ID, templateLayoutId)

                AppLog.d(TAG, "template id: $templateLayoutId")
                if (videoFiles.isEmpty()) {
                    AppLog.d(TAG, "videofiles empty")
                } else {
                    AppLog.d(TAG, "videofiles not empty: ${videoFiles.size}")
                    getDeviceIdTemplateSpecificFiles(savedDeviceId,savedZoneId,savedCityId,savedBranchId,savedDeviceGrpId,templateLayoutId)
                }
                if (timeBetween) {
                    break
                }
            }
        }

        if (getLayout(templateLayoutId) != 0)
        {
            mBinding?.layoutStub?.viewStub?.layoutResource = getLayout(templateLayoutId)
        }
        inflated = mBinding?.layoutStub?.viewStub?.inflate()
        setLayoutViews(templateLayoutId)

        mViewModel?.assetDownloadScheduleData?.observe(this, { dataList ->
            Log.d(TAG, "onCreate: observe")
            dataList.data?.forEach { scheduleDataList ->

                if (PreferanceRepository.getString(Constants.DEVICE_ID)
                                .equals(scheduleDataList.deviceId)
                ) {
                    startTime = scheduleDataList.startTime
                    endTime = scheduleDataList.endTime
                    if (checkCurrentTime(startTime!!, endTime!!)) {

                        scheduleDataList.deviceTemplateData?.let { deviceTempData ->
                            //PreferanceRepository.setInt(Constants.DEVICE_Layout_ID, it.templateId?.toInt()?: -1)
                            //this is for same device template or asset change
                            if (PreferanceRepository.getInt(Constants.Current_Device_Layout_ID) != deviceTempData.templateId?.toInt()) {
                                PreferanceRepository.setInt(
                                        Constants.Current_Device_Layout_ID,
                                        deviceTempData.templateId?.toInt()
                                                ?: 0
                                )
                                //download new files of template
                                downloadAllDataFromApi(scheduleDataList)
                                //saved in sharedpref
                                saveTemplateAssetOfflineData(dataList)
                            }
                            if (PreferanceRepository.getInt(Constants.Current_Device_Layout_ID) == deviceTempData.templateId?.toInt()) {
                                //files diff chk
                                checkApiFileDiff(scheduleDataList)
                            }
                        }
                    } else {
                        //check if there is next schedule for same device after current time &
                        //update UI at that time with downloading assets
                        //startActivity(Intent(this,MainActivity::class.java))
                    }
                }
            }
        })
    }

    private fun checkApiFileDiff(scheduleDataList: ScheduleData) {
        scheduleDataList?.let { fileDiffData ->

            //get shared pref stored response
            assetDownloadScheduleResponse = getTemplateAssetStoredResponse()!!

            scheduleList = assetDownloadScheduleResponse.data as ArrayList<ScheduleData>

            scheduleList.forEach { savedTempData ->
                savedDeviceId = PreferanceRepository.getString(Constants.DEVICE_ID)

                if (savedDeviceId.equals(savedTempData.deviceId) &&
                        fileDiffData.deviceTemplateData?.templateId.equals(savedTempData.deviceTemplateData?.templateId))
                {
                    fileDiffData.deviceTemplateData?.templateId?.toInt()?.let {
                        getPrefStoredSpecificTemplateDataCount(it)
                    }

                    //check logo image diff
                    if (templateAssetLogoImageCount == 1)
                    {
                        if (fileDiffData.deviceTemplateData?.logo.equals(savedTempData.deviceTemplateData?.logo))
                        {
                            Log.d(TAG, "checkApiFileDiff: same image")
                        }else
                        {

                            Log.d(TAG, "img diff: ${scheduleDataList.deviceTemplateData?.logo} / \n" +
                                    "${savedTempData.deviceTemplateData?.logo}")
                            downlodLogoImage(scheduleDataList)
                        }
                    }//if end

                    //check ticker text diff
                    if (templateAssetTickerTextCount == 1)
                    {
                        if (fileDiffData.deviceTemplateData?.tickerText.equals(savedTempData.deviceTemplateData?.tickerText))
                        {
                            Log.d(TAG, "checkApiFileDiff: same tikcer")
                        }else
                        {
                            Log.d(TAG, "ticker diff: ${fileDiffData.deviceTemplateData?.tickerText} / \n" +
                                    "${savedTempData.deviceTemplateData?.tickerText}")
                        }
                    }//if end

                    //check asset box img/video difference
                    fileDiffData.deviceTemplateData?.templateAssets?.let { apitempAssetData ->
                        //api data
                        savedTempData.deviceTemplateData?.templateAssets?.let { savedTempAssestData ->
                            if (apitempAssetData.isNotEmpty() && savedTempAssestData.isNotEmpty()){

                                /*apitempAssetData.forEach { apiData ->
                                    // 0 pos - abc.mp4     a.mp4,b.mp4,c.mp4,abc.mp4 4 4
                                    var found = false
                                    savedTempAssestData.forEach { savedData ->
                                         if (apiData.assetUrl.equals(savedData.assetUrl))
                                        {
                                            Log.d(TAG, "asset url same")
                                        }else{
                                            Log.d(TAG, "assset url diff: ${savedData.assetUrl} / \n" +
                                                    "${apiData.assetUrl}")
                                            downloadOtherFiles(scheduleDataList, apiData)
                                        }
                                    }
                                }*/

                                if (apitempAssetData.size == savedTempAssestData.size)
                                {
                                  apitempAssetData.forEachIndexed { index, templateAsset ->
                                      if (!templateAsset.assetUrl.equals(savedTempAssestData[index].assetUrl))
                                      {
                                          Log.e(TAG, "assset url diff: ${templateAsset.assetUrl} / \n" +
                                                  "${savedTempAssestData[index].assetUrl}")
                                          downloadOtherFiles(scheduleDataList, templateAsset)
                                      }else
                                      {
                                          Log.e(TAG, "checkApiFileDiff: same asset url")
                                      }
                                  }

                                }
                            }
                        }
                    }//asset
                }//temp end
            }
        }

    }

    private fun downloadAllDataFromApi(data: ScheduleData) {
        data.let { scheduleData ->
            val tempData = scheduleData.deviceTemplateData
            val templateAssets = tempData?.templateAssets
//if file already downloaded
//if file not present
            downlodLogoImage(scheduleData)

            templateAssets?.forEach { assetData ->
                downloadOtherFiles(scheduleData, assetData)
            }

            if (downloadAllStatus)
            {
                downloadAllStatus = false
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun downlodLogoImage(scheduleData: ScheduleData) {
        //get logo image if present
        if (scheduleData.deviceTemplateData?.logo != null && scheduleData.deviceTemplateData?.logo!!.isNotEmpty())
        {
            assetUrl = Constants.Video_Base_URL+ scheduleData.deviceTemplateData!!.logo
            fileName = getFileName(assetUrl!!)
            val logoTag = "logo"
            //logo image syntax - scheduleid_deviceid_logo_templateid_imagename.jpeg
            fileName =
                    "${scheduleData.id}_${scheduleData.zoneId}_${scheduleData.cityId}_${scheduleData.branchId}_" +
                            "${scheduleData.deviceGroupId}_${scheduleData.deviceId}_${logoTag}_${scheduleData.deviceTemplateData?.templateId}_$fileName"

            val ifPresent = ExternalFileHandling.isFileExists(fileName!!)
                //checkIfFileAlreadyPresent(this, getFileNameWithoutExtn(fileName!!))
            if (ifPresent)
            {
                downloadAllStatus = true
                Log.d(TAG, "checkCreatedFolder: $fileName not needed to download again")
            }else
            {
                Log.d(TAG, "checkCreatedFolder:$fileName need to download file")
                ExternalFileHandling.downloadFileFromManager(this, assetUrl!!, fileName!!)
            }
        }
    }

    private fun downloadOtherFiles(scheduleData: ScheduleData, assetData: TemplateAsset) {
        assetUrl = Constants.Video_Base_URL + assetData.assetUrl
        fileName = getFileName(assetUrl)
        fileName =
                "${scheduleData.id}_${scheduleData.zoneId}_${scheduleData.cityId}_${scheduleData.branchId}_" +
                        "${scheduleData.deviceGroupId}_${scheduleData.deviceId}_${assetData.boxNo}_${scheduleData.deviceTemplateData?.templateId}_$fileName"
        //It should check before downloading. If file exists, it should return
        // or it should delete and then try to download the new file.
        val ifVideoPresent = ExternalFileHandling.isFileExists(fileName)
            //checkIfFileAlreadyPresent(this, getFileNameWithoutExtn(fileName))
        if (ifVideoPresent)
        {
            downloadAllStatus = true
            Log.d(TAG, "checkCreatedFolder: $fileName not needed to download again")
        }else
        {
            Log.d(TAG, "checkCreatedFolder:$fileName need to download")
            ExternalFileHandling.downloadFileFromManager(this, assetUrl!!, fileName!!)
        }
    }

    /*
     * NOTE: we initialize the player either in onStart or onResume according to API level
     * API level 24 introduced support for multiple windows to run side-by-side. So it's safe to initialize our player in onStart
     * more on Multi-Window Support here https://developer.android.com/guide/topics/ui/multi-window.html
     * Before API level 24, we wait as long as onResume (to grab system resources) before initializing player
    */
    override fun onStart() {
        super.onStart()
        //initFourVideosPlayer()
        initializeVideoPlayerInTemplate()
    }

    override fun onResume() {
        super.onResume()
        //initFourVideosPlayer()
        initializeVideoPlayerInTemplate()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
        if (udpSocketReceiver != null)
        {
            unregisterReceiver(udpSocketReceiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        //AppLog.d(TAG, "onDestroy: called")
        releasePlayer()
        //PreferanceRepository.setBoolean(Constants.Lanuch_MainScreen,false)
        DownloadBroadcastReceiver.setDownloadListener(null)
        mViewModel?.clearViewmodel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mPlayer1?.seekTo(0)
        mPlayer2?.seekTo(0)
        mPlayer3?.seekTo(0)
        mPlayer4?.seekTo(0)
        releasePlayer()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //Log.d(TAG, "onConfigurationChanged: called")
        //tickerText.setMovingText("Ticker Text here")
        tickerAfterOrientationChange(templateLayoutId)
    }


    private fun tickerAfterOrientationChange(templateLayoutId: Int) {
        when (templateLayoutId) {
            1, 2, 4, 5, 6, 8, 9, 10, 12, 13, 14, 16, 17, 18, 20, 21, 22, 24, 25, 26, 28, 29, 30, 32 -> {
                setTickerText(tickerText)
            }
        }
    }

    private fun setLayoutViews(layoutId: Int) {
        when (layoutId) {
            1 -> {
                initLayoutForTemplateOne()
            }
            2 -> {
                initLayoutForTemplateTwo()
            }
            3 -> {
                initLayoutForTemplateThree()
            }
            4 -> {
                initLayoutForTemplateFour()
            }
            5 -> {
                initLayoutForTemplateFive()
            }
            6 -> {
                initLayoutForTemplateSix()
            }
            7 -> {
                initLayoutForTemplateSeven()
            }
            8 -> {
                initLayoutForTemplateEight()
            }
            9 -> {
                initLayoutForTemplateNine()
            }
            10 -> {
                initLayoutForTemplateTen()
            }
            11 -> {
                initLayoutForTemplateEleven()
            }
            12 -> {
                initLayoutForTemplateTwelve()
            }
            13 -> {
                initLayoutForTemplateThirteen()
            }
            14 -> {
                initLayoutForTemplateFourteen()
            }
            15 -> {
                initLayoutForTemplateFifteen()
            }
            16 -> {
                initLayoutForTemplateSixteen()
            }
            17 -> {
                initLayoutForTemplateSeventeen()
            }
            18 -> {
                initLayoutForTemplateEighteen()
            }
            19 -> {
                initLayoutForTemplateNinteen()
            }
            20 -> {
                initLayoutForTemplateTwenty()
            }
            21 -> {
                initLayoutForTemplateTwentyOne()
            }
            22 -> {
                initLayoutForTemplateTwentyTwo()
            }
            23 -> {
                initLayoutForTemplateTwentyThree()
            }
            24 -> {
                initLayoutForTemplateTwentyFour()
            }
            25 -> {
                initLayoutForTemplateTwentyFive()
            }
            26 -> {
                initLayoutForTemplateTwentySix()
            }
            27 -> {
                initLayoutForTemplateTwentySeven()
            }
            28 -> {
                initLayoutForTemplateTwentyEight()
            }
            29 -> {
                initLayoutForTemplateTwentyNine()
            }
            30 -> {
                initLayoutForTemplateThirty()
            }
            31 -> {
                initLayoutForTemplateThirtyOne()
            }
            32 -> {
                initLayoutForTemplateThirtyTwo()
            }

            33 -> {
                initLayoutForTemplateThirtyThree()
            }
            34 -> {
                initLayoutForTemplateTwentyNineCopy()
            }
            35 -> {
                initLayoutForTemplateTwentyNineSliderCopy()
            }
        }

        setSavedData()
    }

    private fun initLayoutForTemplateThirtyThree() {
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        //imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        //videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        mToken = inflated?.findViewById<TextView>(R.id.txt_token)!!
        mCounter = inflated?.findViewById<TextView>(R.id.txt_counter)!!
        rtToken = inflated?.findViewById<CardView>(R.id.card_rttoken_number)
        rtCounter = inflated?.findViewById<CardView>(R.id.card_rtcounter_no)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)

        if (templateAssetVideoCount > 0) {

            if (videoUrlOfflineList.size > 0) {
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateThirtyTwo: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)

                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }

        /* initSingleVideosPlayer()

         if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
             playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
         }*/
        showCustomizeDialog()
        //setTwoImages(32)
    }

    private fun initLayoutForTemplateThirtyTwo() {
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!

        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)

        if (templateAssetVideoCount > 0) {

            if (videoUrlOfflineList.size > 0) {
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateThirtyTwo: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)

                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }

        initSingleVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        showCustomizeDialog()
        setTwoImages(32)
    }

    private fun initLayoutForTemplateThirtyOne() {
        Log.d(TAG, "initLayoutForTemplateThirtyOne: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        initTokenCounterUI()
        setAdapter()

        if (templateAssetVideoCount > 0) {
            Log.d(
                    TAG,
                    "initLayoutForTemplateThirtyOne: videoUrlOfflineList size ${videoUrlOfflineList.size}"
            )
            if (videoUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateThirtyOne: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)

                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }

        initSingleVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }

        setTwoImages(31)
        showCustomizeDialog()
        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateThirty() {
        Log.d(TAG, "initLayoutForTemplateThirty: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!

        setTickerText(tickerText)
        if (templateAssetVideoCount > 0) {
            Log.d(
                    TAG,
                    "initLayoutForTemplateThirty: videoUrlOfflineList size ${videoUrlOfflineList.size}"
            )
            if (videoUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateThirty: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        AppLog.d(TAG, "30 internal video file name: $fileName")
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)
                        arrayData.forEach {
                            println(it)
                        }
                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                       /* AppLog.d(
                                TAG, "30 video spec info: scheduleId: $scheduleId \n\t" +
                                "deviceId: $deviceId \n\t" +
                                "boxNumber: $boxNumber \n\t" +
                                "templateId: $templateId \n\t" + "originalFileName: $originalVideoName"
                        )*/
                        if (boxNumber.equals("3")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }

        initSingleVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }

        setTwoImages(30)

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateTwentyNine() {
        Log.d(TAG, "initLayoutForTemplateTwentyNine: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()

        setTickerText(tickerText)

        if (templateAssetVideoCount > 0) {
            Log.d(
                    TAG,
                    "initLayoutForTemplateTwentyNine: videoUrlOfflineList size ${videoUrlOfflineList.size}"
            )
            if (videoUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateTwentyNine: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)

                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }

        initSingleVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }

        setTwoImages(29)
        showCustomizeDialog()

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }
//copy

    private fun initLayoutForTemplateTwentyNineCopy() {
        Log.d(TAG, "initLayoutForTemplateTwentyNineCopy: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
       // imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()

        setTickerText(tickerText)

        if (templateAssetVideoCount > 0) {
            Log.d(
                TAG,
                "initLayoutForTemplateTwentyNine: videoUrlOfflineList size ${videoUrlOfflineList.size}"
            )
            if (videoUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateTwentyNine: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)

                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }

        initSingleVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }

        setTwoImages(29)
        showCustomizeDialog()

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }
    ///end copy
//slider copy
    private fun initLayoutForTemplateTwentyNineSliderCopy() {
        Log.d(TAG, "initLayoutForTemplateTwentyNineSliderCopy: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
//        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()

        setTickerText(tickerText)

        if (templateAssetVideoCount > 0) {
            Log.d(
                TAG,
                "initLayoutForTemplateTwentyNine: videoUrlOfflineList size ${videoUrlOfflineList.size}"
            )
            if (videoUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateTwentyNine: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)

                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }

        initSingleVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }

        setTwoImages(29)
        showCustomizeDialog()

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }
//slider copy end

    private fun initLayoutForTemplateTwentyEight() {
        Log.d(TAG, "initLayoutForTemplateTwentyEight: called")
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()

        setTickerText(tickerText)
        showCustomizeDialog()
        setTwoVideosData(28)
        initTwoVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }

        if (templateAssetImageCount > 0) {
            Log.d(TAG, "initLayoutForTemplateTwentyEight: img size ${imageUrlOfflineList.size}")
            if (imageUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetImageCount == 1) {
                    imageUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateTwentyEight: img name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]


                        if (boxNumber.equals("3")) {
                            offlineImgUrl3 = uriData
                            loadImageFromUri(this, offlineImgUrl3!!, imgBoxOne)
                        }
                    }
                }
            }
        }
    }

    private fun initLayoutForTemplateTwentySeven() {
        Log.d(TAG, "initLayoutForTemplateTwentySeven: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        initTokenCounterUI()
        setAdapter()

        setTwoVideosData(27)
        initTwoVideosPlayer()
        showCustomizeDialog()
        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }

        if (templateAssetImageCount > 0) {
            Log.d(TAG, "initLayoutForTemplateTwentySeven: img size ${imageUrlOfflineList.size}")
            if (imageUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetImageCount == 1) {
                    imageUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateTwentySeven: img name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineImgUrl3 = uriData
                            loadImageFromUri(this, offlineImgUrl3!!, imgBoxOne)
                        }
                    }
                }
            }
        }

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateTwentySix() {
        Log.d(TAG, "initLayoutForTemplateTwentySix: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!

        setTickerText(tickerText)
        setTwoVideosData(26)
        initTwoVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }

        if (templateAssetImageCount > 0) {
            Log.d(TAG, "initLayoutForTemplateTwentySix: img size ${imageUrlOfflineList.size}")
            if (imageUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetImageCount == 1) {
                    imageUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateTwentySix: img name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineImgUrl3 = uriData
                            loadImageFromUri(this, offlineImgUrl3!!, imgBoxOne)
                        }
                    }
                }
            }
        }

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateTwentyFive() {
        Log.d(TAG, "initLayoutForTemplateTwentyFive: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        setTwoVideosData(25)
        initTwoVideosPlayer()
        showCustomizeDialog()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }

        if (templateAssetImageCount > 0) {
            Log.d(TAG, "initLayoutForTemplateTwentyFive: img size ${imageUrlOfflineList.size}")
            if (imageUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetImageCount == 1) {
                    imageUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "initLayoutForTemplateTwentyFive: img name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("3")) {
                            offlineImgUrl3 = uriData
                            loadImageFromUri(this, offlineImgUrl3!!, imgBoxOne)
                        }
                    }
                }
            }
        }

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }

    }

    private fun initLayoutForTemplateTwentyFour() {
        initTokenCounterUI()
        initFourImgUI()
        setAdapter()
        setFourImages(24)
        showCustomizeDialog()
    }

    private fun initLayoutForTemplateTwentyThree() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        initTokenCounterUI()
        initFourImgUI()
        setAdapter()
        setFourImages(23)
        showCustomizeDialog()

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateTwentyTwo() {
        Log.d(TAG, "initLayoutForTemplateTwentyTwo: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initFourImgUI()

        setTickerText(tickerText)
        setFourImages(22)
        showCustomizeDialog()

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateTwentyOne() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initFourImgUI()
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        setFourImages(21)
        showCustomizeDialog()
        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateTwenty() {
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initFourVideosUI()
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        showCustomizeDialog()
    }

    private fun initLayoutForTemplateNinteen() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        initTokenCounterUI()
        initFourVideosUI()
        //get shared pref stored response
        //assetDownloadScheduleResponse = getTemplateStoredResponse()!!

        //val scheduleList =  assetDownloadScheduleResponse.data
       setAdapter()

        if (scheduleList.isNotEmpty()) {
            scheduleList.forEach { scheduleData ->
                val deviceTemplateData = scheduleData.deviceTemplateData
                if (savedDeviceId.equals(scheduleData.deviceId)) {
                    if (!deviceTemplateData?.logo.isNullOrEmpty()) {
                        val imageUrl = deviceTemplateData?.logo
                        loadImage(this, imageName = imageUrl!!, imgLogo!!)
                    }
                }
            }
        }
        initFourVideosPlayer()
        setFourVideos(19)
        showCustomizeDialog()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }
        if (offlineVideoUrl3 != null && offlineVideoUrl3.toString().isNotEmpty()) {
            playVideoThree(offlineVideoUrl3!!, videoBoxThree, "dataSourceFactory3", mPlayer3)
        }
        if (offlineVideoUrl4 != null && offlineVideoUrl4.toString().isNotEmpty()) {
            playVideoFour(offlineVideoUrl4!!, videoBoxFour, "dataSourceFactory4", mPlayer4)
        }

    }

    private fun initLayoutForTemplateEighteen() {
        Log.d(TAG, "initLayoutForTemplateEighteen: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initFourVideosUI()

        setTickerText(tickerText)
        setTickerText(tickerText)
        initFourVideosPlayer()
        setFourVideos(18)

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }
        if (offlineVideoUrl3 != null && offlineVideoUrl3.toString().isNotEmpty()) {
            playVideoThree(offlineVideoUrl3!!, videoBoxThree, "dataSourceFactory3", mPlayer3)
        }
        if (offlineVideoUrl4 != null && offlineVideoUrl4.toString().isNotEmpty()) {
            playVideoFour(offlineVideoUrl4!!, videoBoxFour, "dataSourceFactory4", mPlayer4)
        }
        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateSeventeen() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initFourVideosUI()
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        initFourVideosPlayer()
        setFourVideos(17)
        showCustomizeDialog()
        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }
        if (offlineVideoUrl3 != null && offlineVideoUrl3.toString().isNotEmpty()) {
            playVideoThree(offlineVideoUrl3!!, videoBoxThree, "dataSourceFactory3", mPlayer3)
        }
        if (offlineVideoUrl4 != null && offlineVideoUrl4.toString().isNotEmpty()) {
            playVideoFour(offlineVideoUrl4!!, videoBoxFour, "dataSourceFactory4", mPlayer4)
        }

    }

    private fun initLayoutForTemplateSixteen() {
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        showCustomizeDialog()
    }

    private fun initLayoutForTemplateFifteen() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        initTokenCounterUI()
        setAdapter()
        setTwoImages(15)
        showCustomizeDialog()

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateFourteen() {
        Log.d(TAG, "initLayoutForTemplateFourteen: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!

        setTickerText(tickerText)
        setTwoImages(14)

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateThirteen() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        setTwoImages(13)
        showCustomizeDialog()

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
    }

    private fun initLayoutForTemplateTwelve() {
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        initTwoVideosPlayer()
        setTwoVideosData(12)
        showCustomizeDialog()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }
    }

    private fun initLayoutForTemplateEleven() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        initTokenCounterUI()
        setAdapter()
        initTwoVideosPlayer()
        setTwoVideosData(11)
        showCustomizeDialog()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }
    }

    private fun initLayoutForTemplateTen() {
        Log.d(TAG, "initLayoutForTemplateTen: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!

        setTickerText(tickerText)
        initTwoVideosPlayer()
        setTwoVideosData(10)

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }
    }

    private fun initLayoutForTemplateNine() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        initTwoVideosPlayer()
        setTwoVideosData(9)
        showCustomizeDialog()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        if (offlineVideoUrl2 != null && offlineVideoUrl2.toString().isNotEmpty()) {
            playVideoTwo(offlineVideoUrl2!!, videoBoxTwo, "dataSourceFactory2", mPlayer2)
        }
    }

    private fun initLayoutForTemplateEight() {
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        setSingImage(8)
        showCustomizeDialog()
    }

    private fun initLayoutForTemplateSeven() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        initTokenCounterUI()
        setAdapter()
        setSingImage(7)

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
        showCustomizeDialog()
    }

    private fun initLayoutForTemplateSix() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!

        setSingImage(6)

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
        setTickerText(tickerText)
    }

    private fun initLayoutForTemplateFive() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        setSingImage(5)

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }
        showCustomizeDialog()
    }

    private fun initLayoutForTemplateFour() {
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.templateVideoPlayer_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        setSingleVideo(4)
        initSingleVideosPlayer()

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
        showCustomizeDialog()
    }

    private fun initLayoutForTemplateTwo() {
        AppLog.d(TAG, "initLayoutForTemplateTwo: called")
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.templateVideoPlayer_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!

        setTickerText(tickerText)

        initSingleVideosPlayer()
        setSingleVideo(2)

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
    }

    private fun initLayoutForTemplateThree() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.templateVideoPlayer_one)!!
        initTokenCounterUI()
        setAdapter()
        showCustomizeDialog()
        initSingleVideosPlayer()
        setSingleVideo(3)

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }
    }

    private fun initLayoutForTemplateOne() {
        imgLogo = inflated?.findViewById<ImageView>(R.id.img_logo)!!
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.templateVideoPlayer_one)!!
        tickerText = inflated?.findViewById(R.id.txt_ticker)!!
        llTickerView = inflated?.findViewById<LinearLayout>(R.id.ll_ticker)!!
        initTokenCounterUI()
        setAdapter()
        setTickerText(tickerText)
        initSingleVideosPlayer()
        setSingleVideo(1)

        if (offlineVideoUrl1 != null && offlineVideoUrl1.toString().isNotEmpty()) {
            playVideoOne(offlineVideoUrl1!!, videoBoxOne, "dataSourceFactory1", mPlayer1)
        }

        if (logoImageOfflineUrl != null && logoImageOfflineUrl.toString().isNotEmpty()) {
            loadImageFromUri(this, logoImageOfflineUrl!!, imgLogo)
        }

        showCustomizeDialog()
    }

    private fun showCustomizeDialog()
    {
        llGreenView?.setOnLongClickListener {

            //showCustmizeDialog(this, layoutInflater, templateLayoutId, this)
            showPasswordDialog(this,layoutInflater,this)
            return@setOnLongClickListener true
        }
    }

    private fun getDeviceIdTemplateSpecificFiles(savedDeviceId: String, savedZoneId: String,savedCityId: String,savedBranchId: String,
                                                 savedDeviceGrpId: String,templateLayoutId: Int) {
         imageUrlOfflineList.clear()
         videoUrlOfflineList.clear()
         logoImageOfflineUrl = null

        getPrefStoredSpecificTemplateDataCount(templateLayoutId)

        if (videoFiles.isNotEmpty()) {
            videoFiles.forEach { file ->
                val extension = getFileExtension(file)
                val fileName: String = getFileNameWithoutExtn(file.path)
                //logo image syntax - scheduleid_deviceid_logo_templateid_imagename.jpeg
                //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
    //scheduleid_zoneid_cityid_branchid_devicegrpid_deviceid_logo_templateid_imagename.jpeg
                //44_3_2_14_71435_1610972260.mp4
                val arrayData: List<String> = fileName.split("_", limit = 9)

                val scheduleId = arrayData[0]
                val zoneId = arrayData[1]
                val cityId = arrayData[2]
                val branchId = arrayData[3]
                val devicegrpId = arrayData[4]
                val deviceId = arrayData[5]
                val boxNumber = arrayData[6]
                val templateId = arrayData[7]
                val originalVideoName = arrayData[8]

                if (extension.isEmpty()) {
                    AppLog.d("file", "No file format found")
                    return
                }

                if (extension == ".jpg" || extension == ".jpeg" || extension == ".png") {
                    try {

                        val uri = FileProvider.getUriForFile(
                                this,
                                this.applicationContext.packageName + ".provider",
                                file
                        )
                        AppLog.d(TAG, "image uri : $uri")
                        //logo image syntax - scheduleid_deviceid_logo_templateid_imagename.jpeg
                        scheduleList.forEach { scheduleData ->
                            val deviceTemplateData = scheduleData.deviceTemplateData
                            if (deviceTemplateData != null) {
                                val templateAssetData = deviceTemplateData.templateAssets

                                if (savedDeviceId.equals(scheduleData.deviceId) &&
                                        savedZoneId.equals(scheduleData.zoneId) &&
                                        savedCityId.equals(scheduleData.cityId) &&
                                        savedBranchId.equals(scheduleData.branchId) &&
                                        savedDeviceGrpId.equals(scheduleData.deviceGroupId)) {
                                    savedResScheduleId = scheduleData.id
                                    AppLog.d(TAG, "savedResScheduleId image: $savedResScheduleId")
                                    if (deviceId.equals(savedDeviceId) && boxNumber.equals("logo") &&
                                            scheduleId.equals(scheduleData.id.toString()) &&
                                            zoneId.equals(savedZoneId) &&
                                            cityId.equals(savedCityId) &&
                                            branchId.equals(savedBranchId) &&
                                            devicegrpId.equals(savedDeviceGrpId) &&
                                            templateId.equals(templateLayoutId.toString()) &&
                                            originalVideoName.equals(
                                                    getFileNameWithoutExtn(
                                                            deviceTemplateData.logo!!
                                                    )
                                            )
                                    ) {
                                        logoImageOfflineUrl = uri
                                    }

                                    templateAssetData?.forEach { tempImgAsset ->
                                         if (deviceId.equals(savedDeviceId) &&
                                             boxNumber.equals(tempImgAsset.boxNo) &&
                                             scheduleId.equals(scheduleData.id.toString()) &&
                                                 zoneId.equals(savedZoneId) &&
                                                 cityId.equals(savedCityId) &&
                                                 branchId.equals(savedBranchId) &&
                                                 devicegrpId.equals(savedDeviceGrpId) &&
                                                 templateId.equals(templateLayoutId.toString())
                                            && originalVideoName.equals(
                                                        getFileNameWithoutExtn(
                                                                tempImgAsset.assetUrl!!
                                                        )
                                                )
                                        ) {
                                            imageUrlOfflineList.add(uri)
                                        }
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        AppLog.e("file", "image ${e.toString()}")
                    }
                } else if (extension == ".mp4") {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            uri = FileProvider.getUriForFile(
                                    this,
                                    "${this.packageName}.provider", file
                            )
                            //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_1_18_35551_1611578015.mp4
                            AppLog.d(TAG, "uri above M: $uri")

                            scheduleList.forEach { scheduleData ->
                                val deviceTemplateData = scheduleData.deviceTemplateData
                                if (deviceTemplateData != null) {
                                    val templateAssetData = deviceTemplateData.templateAssets

                                    if (savedDeviceId.equals(scheduleData.deviceId) &&
                                             savedZoneId.equals(scheduleData.zoneId) &&
                                            savedCityId.equals(scheduleData.cityId) &&
                                            savedBranchId.equals(scheduleData.branchId) &&
                                            savedDeviceGrpId.equals(scheduleData.deviceGroupId)) {
                                        savedResScheduleId = scheduleData.id
                                        AppLog.d(TAG, "savedResScheduleId : $savedResScheduleId")

                                        templateAssetData?.forEach { tempAsset ->

                                            if (deviceId.equals(savedDeviceId) &&
                                                    boxNumber.equals(tempAsset.boxNo) &&
                                                 scheduleId.equals(scheduleData.id.toString()) &&
                                                    zoneId.equals(savedZoneId) &&
                                                    cityId.equals(savedCityId) &&
                                                    branchId.equals(savedBranchId) &&
                                                    devicegrpId.equals(savedDeviceGrpId) &&
                                                 templateId.equals(templateLayoutId.toString()) &&
                                                    originalVideoName.equals(
                                                            getFileNameWithoutExtn(
                                                                    tempAsset.assetUrl!!
                                                            )
                                                    )
                                            ) {
                                                videoUrlOfflineList.add(uri)
                                            }
                                        }
                                    }
                                }
                            }
                            Log.d(
                                    TAG,
                                    "getDeviceIdTemplateSpecificFiles: video size ${videoUrlOfflineList.size}"
                            )
                        } else {
                            uri = Uri.fromFile(file)
                            AppLog.d(TAG, "uri below M: $uri")
                            scheduleList.forEach { scheduleData ->
                                val deviceTemplateData = scheduleData.deviceTemplateData
                                if (deviceTemplateData != null) {
                                    val templateAssetData = deviceTemplateData.templateAssets

                                    if (savedDeviceId.equals(scheduleData.deviceId) &&
                                            savedZoneId.equals(scheduleData.zoneId) &&
                                            savedCityId.equals(scheduleData.cityId) &&
                                            savedBranchId.equals(scheduleData.branchId) &&
                                            savedDeviceGrpId.equals(scheduleData.deviceGroupId)) {
                                        savedResScheduleId = scheduleData.id
                                        AppLog.d(TAG, "savedResScheduleId : $savedResScheduleId")

                                        templateAssetData?.forEach { tempAsset ->

                                            if (deviceId.equals(savedDeviceId)
                                                && boxNumber.equals(tempAsset.boxNo)
                                                && scheduleId.equals(scheduleData.id.toString())
                                                    &&  zoneId.equals(savedZoneId)
                                                    &&    cityId.equals(savedCityId)
                                                    && branchId.equals(savedBranchId)
                                                    &&  devicegrpId.equals(savedDeviceGrpId)
                                                    && templateId.equals(templateLayoutId.toString())
                                                && originalVideoName.equals(
                                                            getFileNameWithoutExtn(
                                                                    tempAsset.assetUrl!!
                                                            )
                                                    )
                                            ) {
                                                videoUrlOfflineList.add(uri)
                                            }
                                        }
                                    }
                                }
                            }
                            Log.d(
                                    TAG,
                                    "getDeviceIdTemplateSpecificFiles: video size ${videoUrlOfflineList.size}"
                            )
                        }
                    } catch (e: Exception) {
                        AppLog.e("file", "video ${e.toString()}")
                    }
                }
            }
        }
    }

    private fun getPrefStoredSpecificTemplateDataCount(templateID: Int) {
        //get template asset count
        getTemplatesResponse = getAllTemplateDesignResponse()
        if (getTemplatesResponse != null) {
            if (getTemplatesResponse?.data != null && getTemplatesResponse?.data!!.isNotEmpty()) {
                getTemplatesResponse?.data!!.forEach { templateAssetCountData ->
                    if (templateAssetCountData?.id!! == templateID) {
                        templateAssetImageCount =
                                templateAssetCountData.imagesRequired!!.toInt()
                        templateAssetVideoCount =
                                templateAssetCountData.videosRequired!!.toInt()
                        templateAssetLogoImageCount =
                                templateAssetCountData.logoRequired!!.toInt()
                        templateAssetTickerTextCount =
                                templateAssetCountData.tickerTextRequired!!.toInt()
                    }
                }
            }
        }
    }

    private fun getFileExtension(file: File): String {
        val fileName = file.name
        return if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            fileName.substring(fileName.lastIndexOf("."))
        else
            ""
    }


    private fun initializeVideoPlayerInTemplate() {
        getTemplatesResponse = getAllTemplateDesignResponse()
        if (getTemplatesResponse != null) {
            if (getTemplatesResponse?.data != null && getTemplatesResponse?.data!!.isNotEmpty()) {
                getTemplatesResponse?.data!!.forEach { templateAssetCountData ->
                    if (templateAssetCountData?.id!!.equals(templateLayoutId)) {
                        templateAssetVideoCount = templateAssetCountData.videosRequired!!.toInt()
                        if (templateAssetVideoCount == 1) {
                            initSingleVideosPlayer()
                        } else if (templateAssetVideoCount == 2) {
                            initTwoVideosPlayer()
                        } else if (templateAssetVideoCount == 4) {
                            initFourVideosPlayer()
                        }
                    }
                }
            }
        }
    }

    private fun initFourVideosPlayer() {

        try {
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val trackSelector: TrackSelector = DefaultTrackSelector(
                    AdaptiveTrackSelection.Factory(
                            bandwidthMeter
                    )
            )
            mPlayer1 = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
            mPlayer2 = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
            mPlayer3 = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
            mPlayer4 = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    private fun initTwoVideosPlayer() {
        try {
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val trackSelector: TrackSelector = DefaultTrackSelector(
                    AdaptiveTrackSelection.Factory(
                            bandwidthMeter
                    )
            )
            mPlayer1 = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
            mPlayer2 = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    private fun initSingleVideosPlayer() {
        try {
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val trackSelector: TrackSelector = DefaultTrackSelector(
                    AdaptiveTrackSelection.Factory(
                            bandwidthMeter
                    )
            )
            mPlayer1 = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    private fun playVideoOne(
            url: Uri,
            exoPlayerView: PlayerView?,
            dataSourceFactoryText: String,
            exoPlayer: SimpleExoPlayer?
    ) {
        val videoUri: Uri = url
        //val videoUri: Uri = Uri.parse(url)
        val dataSourceFactory = DefaultDataSourceFactory(
                this, Util.getUserAgent(
                this,
                dataSourceFactoryText
        )
        )
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        val mediaSource: MediaSource = ExtractorMediaSource(
                videoUri,
                dataSourceFactory,
                extractorsFactory,
                null,
                null
        )
        /* val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).
         createMediaSource(Uri.fromFile(File(filename)))*/
        // Getting media from sdcard storage
        // Getting media from sdcard storage
        /* val secondSource: MediaSource =
             ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(
                 Uri.parse(Directory.toString() + File.separator + "landscape2")
             )
         Directory = Environment.getExternalStorageDirectory() + File.separator + "EXOPLAYER-SAMPLE";*/
        //exoPlayer?.prepare(mediaSource)
        //looping
        val loopingSource = LoopingMediaSource(mediaSource)
        exoPlayer?.prepare(loopingSource)
        exoPlayerView?.player = exoPlayer
        exoPlayer?.playWhenReady = true
    }

    private fun playVideoTwo(
            url: Uri,
            exoPlayerView: PlayerView?,
            dataSourceFactoryText: String,
            exoPlayer: SimpleExoPlayer?
    ) {
        //val videoUri: Uri = Uri.parse(url)
        val videoUri: Uri = url
        val dataSourceFactory = DefaultDataSourceFactory(
                this, Util.getUserAgent(
                this,
                dataSourceFactoryText
        )
        )
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        val mediaSource: MediaSource = ExtractorMediaSource(
                videoUri,
                dataSourceFactory,
                extractorsFactory,
                null,
                null
        )
        //exoPlayer?.prepare(mediaSource)
        //looping
        val loopingSource = LoopingMediaSource(mediaSource)
        exoPlayer?.prepare(loopingSource)
        exoPlayerView?.player = exoPlayer
        exoPlayer?.playWhenReady = true
    }

    private fun playVideoThree(
            url: Uri,
            exoPlayerView: PlayerView?,
            dataSourceFactoryText: String,
            exoPlayer: SimpleExoPlayer?
    ) {
        //val videoUri: Uri = Uri.parse(url)
        val videoUri: Uri = url
        val dataSourceFactory = DefaultDataSourceFactory(
                this, Util.getUserAgent(
                this,
                dataSourceFactoryText
        )
        )
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        val mediaSource: MediaSource = ExtractorMediaSource(
                videoUri,
                dataSourceFactory,
                extractorsFactory,
                null,
                null
        )
        //exoPlayer?.prepare(mediaSource)
//looping
        val loopingSource = LoopingMediaSource(mediaSource)
        exoPlayer?.prepare(loopingSource)
        exoPlayerView?.player = exoPlayer

        exoPlayer?.playWhenReady = true
    }

    private fun playVideoFour(
            url: Uri,
            exoPlayerView: PlayerView?,
            dataSourceFactoryText: String,
            exoPlayer: SimpleExoPlayer?
    ) {
        //val videoUri: Uri = Uri.parse(url)
        val videoUri: Uri = url
        val dataSourceFactory = DefaultDataSourceFactory(
                this, Util.getUserAgent(
                this,
                dataSourceFactoryText
        )
        )
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        val mediaSource: MediaSource = ExtractorMediaSource(
                videoUri,
                dataSourceFactory,
                extractorsFactory,
                null,
                null
        )
        //exoPlayer?.prepare(mediaSource)
        //looping
        val loopingSource = LoopingMediaSource(mediaSource)
        exoPlayer?.prepare(loopingSource)
        exoPlayerView?.player = exoPlayer

        exoPlayer?.playWhenReady = true
    }

    private fun setTickerText(tickerView: MarqueeNoFocus?) {
        if (scheduleList.isNotEmpty()) {
            scheduleList.forEach { scheduleData ->
                val deviceTemplateData = scheduleData.deviceTemplateData
                if (savedDeviceId.equals(scheduleData.deviceId)) {
                    if (deviceTemplateData?.tickerText != null && deviceTemplateData.tickerText!!.isNotEmpty()) {
                        tickerView?.text = deviceTemplateData.tickerText
//                        tickerText.isSelected = true
                        tickerView?.setMovingText(this,deviceTemplateData.tickerText!!)
                    }
                }
            }
        }
    }

    private fun releasePlayer() {
        if (mPlayer1 != null) {
            // save the player state before releasing its resources
            playbackPosition = mPlayer1?.currentPosition!!
            //currentWindow = mPlayer1.getCurrentWindowIndex()
            //autoPlay = mPlayer1.getPlayWhenReady()
            mPlayer1?.playWhenReady = false
            mPlayer1?.stop()
            mPlayer1?.release()
            mPlayer1?.clearVideoSurface()
            videoBoxOne?.player?.release()
            mPlayer1 = null
        }
        if (mPlayer2 != null) {
            // save the player state before releasing its resources
            playbackPosition = mPlayer2?.currentPosition!!
            //currentWindow = mPlayer1.getCurrentWindowIndex()
            //autoPlay = mPlayer1.getPlayWhenReady()
            mPlayer2?.playWhenReady = false
            mPlayer2?.stop()
            mPlayer2?.release()
            mPlayer2?.clearVideoSurface()
            videoBoxTwo?.player?.release()
            mPlayer2 = null
        }
        if (mPlayer3 != null) {
            // save the player state before releasing its resources
            playbackPosition = mPlayer3?.currentPosition!!
            //currentWindow = mPlayer1.getCurrentWindowIndex()
            //autoPlay = mPlayer1.getPlayWhenReady()
            mPlayer3?.playWhenReady = false
            mPlayer3?.stop()
            mPlayer3?.release()
            mPlayer3?.clearVideoSurface()
            videoBoxThree?.player?.release()
            mPlayer3 = null
        }
        if (mPlayer4 != null) {
            // save the player state before releasing its resources
            playbackPosition = mPlayer4?.currentPosition!!
            //currentWindow = mPlayer1.getCurrentWindowIndex()
            //autoPlay = mPlayer1.getPlayWhenReady()
            mPlayer4?.playWhenReady = false
            mPlayer4?.stop()
            mPlayer4?.release()
            mPlayer4?.clearVideoSurface()
            videoBoxFour?.player?.release()
            mPlayer4 = null
        }
    }

    private fun initFourVideosUI(){
        videoBoxOne = inflated?.findViewById<PlayerView>(R.id.video_one)!!
        videoBoxTwo = inflated?.findViewById<PlayerView>(R.id.video_two)!!
        videoBoxThree = inflated?.findViewById<PlayerView>(R.id.video_three)!!
        videoBoxFour = inflated?.findViewById<PlayerView>(R.id.video_four)!!
    }

    private fun initFourImgUI(){
        imgBoxOne = inflated?.findViewById<ImageView>(R.id.img_one)!!
        imgBoxTwo = inflated?.findViewById<ImageView>(R.id.img_two)!!
        imgBoxThree = inflated?.findViewById<ImageView>(R.id.img_three)!!
        imgBoxFour = inflated?.findViewById<ImageView>(R.id.img_four)!!
    }

    private fun initTokenCounterUI(){
        tokenRecyclerView = inflated?.findViewById<RecyclerView>(R.id.recycler_token_counter)!!
        tokenCounterView = inflated?.findViewById<ConstraintLayout>(R.id.const_bg)!!
        llGreenView = inflated?.findViewById<LinearLayout>(R.id.ll_recycler)!!
        txtTicket = inflated?.findViewById<TextView>(R.id.txt_token_number)!!
        txtCounter = inflated?.findViewById<TextView>(R.id.txt_counter_number)!!
    }

    private fun setSingleVideo(templateNo: Int) {
        if (templateAssetVideoCount > 0) {
            Log.d(TAG, "$templateNo: videoUrlOfflineList size ${videoUrlOfflineList.size}")
            if (videoUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 1) {
                    videoUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "$templateNo: video name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        AppLog.d(TAG, "$templateNo internal video file name: $fileName")
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)
                        arrayData.forEach {
                            println(it)
                        }
                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("1")) {
                            offlineVideoUrl1 = uriData
                        }
                    }
                }
            }
        }
    }

    private fun setTwoVideosData(templateNo: Int) {
        Log.d(
                TAG,
                "initLayoutForTemplate $templateNo: videoUrlOfflineList size ${videoUrlOfflineList.size}"
        )
        if (templateAssetVideoCount > 0) {
            if (videoUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 2) {
                    videoUrlOfflineList.forEach { uriData ->
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("1")) {
                            offlineVideoUrl1 = uriData
                        } else if (boxNumber.equals("2")) {
                            offlineVideoUrl2 = uriData
                        }
                    }
                }
            }
        }
    }

    private fun setFourVideos(templateNo: Int) {
        Log.d(
                TAG,
                "initLayoutForTemplate $templateNo: videoUrlOfflineList size ${videoUrlOfflineList.size}"
        )
        if (videoUrlOfflineList.size > 0) {
            if (templateAssetVideoCount > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetVideoCount == 4) {
                    videoUrlOfflineList.forEach { uriData ->
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        //video name syntax - scheduleid_deviceid_boxno_templateid_videoname.mp4
                        //44_3_2_14_71435_1610972260.mp4
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]


                        if (boxNumber.equals("1")) {
                            offlineVideoUrl1 = uriData
                        } else if (boxNumber.equals("2")) {
                            offlineVideoUrl2 = uriData
                        } else if (boxNumber.equals("3")) {
                            offlineVideoUrl3 = uriData
                        } else if (boxNumber.equals("4")) {
                            offlineVideoUrl4 = uriData
                        }
                    }
                }
            }
        }
    }

    private fun setSingImage(templateNo: Int) {
        if (templateAssetImageCount > 0) {
            Log.d(TAG, "$templateNo: img size ${imageUrlOfflineList.size}")
            if (imageUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetImageCount == 1) {
                    imageUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "$templateNo: img name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("1")) {
                            offlineImgUrl1 = uriData
                            loadImageFromUri(this, offlineImgUrl1!!, imgBoxOne)
                        }
                    }
                }
            }
        }
    }

    private fun setFourImages(templateNo: Int) {
        if (templateAssetImageCount > 0) {
            Log.d(TAG, "$templateNo: img size ${imageUrlOfflineList.size}")
            if (imageUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetImageCount == 4) {
                    imageUrlOfflineList.forEach { uriData ->
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("1")) {
                            offlineImgUrl1 = uriData
                            loadImageFromUri(this, offlineImgUrl1!!, imgBoxOne)
                        } else if (boxNumber.equals("2")) {
                            offlineImgUrl2 = uriData
                            loadImageFromUri(this, offlineImgUrl2!!, imgBoxTwo)
                        } else if (boxNumber.equals("3")) {
                            offlineImgUrl3 = uriData
                            loadImageFromUri(this, offlineImgUrl3!!, imgBoxTwo)
                        } else if (boxNumber.equals("4")) {
                            offlineImgUrl4 = uriData
                            loadImageFromUri(this, offlineImgUrl4!!, imgBoxTwo)
                        }
                    }
                }
            }
        }
    }

    private fun setTwoImages(templateNo: Int) {
        if (templateAssetImageCount > 0) {
            Log.d(TAG, "$templateNo: img size ${imageUrlOfflineList.size}")
            if (imageUrlOfflineList.size > 0) {
                //content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_2_18_80795_1611578015.mp4
                if (templateAssetImageCount == 2) {
                    imageUrlOfflineList.forEach { uriData ->
                        Log.d(TAG, "$templateNo: img name $uriData")
                        val fileName: String = getFileNameWithoutExtn(uriData.path!!)
                        val arrayData: List<String> = fileName.split("_", limit = 9)

                        val scheduleId = arrayData[0]
                        val zoneId = arrayData[1]
                        val cityId = arrayData[2]
                        val branchId = arrayData[3]
                        val devicegrpId = arrayData[4]
                        val deviceId = arrayData[5]
                        val boxNumber = arrayData[6]
                        val templateId = arrayData[7]
                        val originalVideoName = arrayData[8]

                        if (boxNumber.equals("1")) {
                            offlineImgUrl1 = uriData
                            loadImageFromUri(this, offlineImgUrl1!!, imgBoxOne)
                        } else if (boxNumber.equals("2")) {
                            offlineImgUrl2 = uriData
                            loadImageFromUri(this, offlineImgUrl2!!, imgBoxTwo)
                        }
                    }
                }
            }
        }
    }


    fun inflateViewStub(view: View?) {
        if (!mBinding?.layoutStub?.isInflated!!) {
            mBinding?.layoutStub?.viewStub?.inflate()
        }
    }

    override fun onFilerValuesSet(templatedId: Int, headerTxtcolorCode: String, headerBgColor: String,
                                  headerFont: String, headerFontSize: Int, headerTxtAlign: String,
                                  headerFontStye: String,headerLableType: String,
                                  queueTxtcolorCode: String, queueBgColor: String,
                                  queueFont: String, queueFontSize: Int, queueTxtAlign: String,
                                  queueFontStye: String,
                                  rowsCount: Int, columnsCount: Int,
                                  tickerTxtcolor: String, tickerBgColor: String,
                                  tickerFont: String, tickerFontSize: Int, tickerDirection: String,
                                  tickerFontStye: String, tickerSpeed: String,
                                  queueBlinkSpeed: String, queueSoundSpeed: String,sound: String,
                                  headerReset: Boolean,queueReset:Boolean,
                                  tickerReset: Boolean,queueLineColor: String,
                                  rtQueueTxtcolor: String, rtQueueBgColor: String,
                                  rtQueueFont: String, rtQueueFontSize: Int, rtQueueAlign: String,
                                  rtQueueFontStyle: String) {
       when(templatedId){
           1, 3, 4, 5, 7, 8, 9, 11, 12, 13, 15, 16, 17, 19, 20, 21, 23, 24, 25, 27, 28, 29, 31, 32,
           2,6,10,14,18,22,26,30-> {
               //Integer.toHexString(lastSelectedColor).substring(2)
//myView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.myCustomGreen));
               if (tokenRecyclerView != null && tokenCounterView != null &&
                       txtTicket != null && txtCounter != null)
               {
                   if (headerReset) {
                       FileUtility.resetHeaderArea(this,tokenCounterView!!,txtTicket!!,txtCounter!!)
                       //txtTicket?.getPaintFlags()?.and(Paint.UNDERLINE_TEXT_FLAG.inv())?.let { txtTicket?.setPaintFlags(it) }
                   }
                   if (headerBgColor.isNotEmpty()) {
                       tokenCounterView?.setBackgroundColor(Color.parseColor("#$headerBgColor"))
                   }
                   if (headerTxtcolorCode.isNotEmpty()) {
                       txtTicket?.setTextColor(Color.parseColor("#$headerTxtcolorCode"))
                       txtCounter?.setTextColor(Color.parseColor("#$headerTxtcolorCode"))
                   }
                   if (headerFontSize != 0) {
                       FileUtility.setFontSize(txtTicket!!, this, headerFontSize)
                       FileUtility.setFontSize(txtCounter!!, this, headerFontSize)
                   }
                   if (headerFont.isNotEmpty()) {
                       FileUtility.setTextFont(txtTicket!!, this, headerFont)
                       FileUtility.setTextFont(txtCounter!!, this, headerFont)
                   }
                   if (headerFontStye.isNotEmpty()) {
                       FileUtility.setStyle(txtTicket!!, this, headerFontStye)
                       FileUtility.setStyle(txtCounter!!, this, headerFontStye)
                   } else {
                       //textView.paintFlags = textView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                   }

                   if (headerTxtAlign.isNotEmpty()) {
                       FileUtility.setTextGravity(txtTicket!!, headerTxtAlign)
                       FileUtility.setTextGravity(txtCounter!!, headerTxtAlign)
                   }
                   if (headerLableType.isNotEmpty())
                   {
                       FileUtility.setTextType(this,txtTicket!!,txtCounter!!,headerLableType)
                   }

                   if (queueBgColor.isNotEmpty()) {
                       tokenRecyclerView?.setBackgroundColor(Color.parseColor("#$queueBgColor"))
                       llGreenView?.setBackgroundColor(Color.parseColor("#$queueBgColor"))
                   }
                   selectedSoundName = sound
                   soundDuration = queueSoundSpeed
                   selectedSoundName = sound
                   soundDuration = queueSoundSpeed
                   blinkDuration = queueBlinkSpeed
                   adapterTokenCounter.setViewsColors(tickerReset, rowsCount, queueTxtcolorCode, queueFont, queueBgColor,
                           queueFontSize, queueFontStye, queueTxtAlign, queueBlinkSpeed, queueSoundSpeed,
                       sound,queueLineColor)
               }

               if (mToken !=  null && mCounter != null && rtToken != null && rtCounter != null)
               {
                   if (rtQueueTxtcolor.isNotEmpty()) {
                       mToken?.setTextColor(Color.parseColor("#$rtQueueTxtcolor"))
                       mCounter?.setTextColor(Color.parseColor("#$rtQueueTxtcolor"))
                   }
                   if (rtQueueBgColor.isNotEmpty()){
                       rtCounter?.setCardBackgroundColor(Color.parseColor("#$rtQueueBgColor"))
                       rtToken?.setCardBackgroundColor(Color.parseColor("#$rtQueueBgColor"))
                   }
                   if (rtQueueFontSize != 0) {
                       FileUtility.setFontSize(mToken!!, this, rtQueueFontSize)
                       FileUtility.setFontSize(mCounter!!, this, rtQueueFontSize)
                   }
                   if (rtQueueFont.isNotEmpty()) {
                       FileUtility.setTextFont(mToken!!, this, rtQueueFont)
                       FileUtility.setTextFont(mCounter!!, this, rtQueueFont)
                   }
                   if (rtQueueFontStyle.isNotEmpty()) {
                       FileUtility.setStyle(mToken!!, this, rtQueueFontStyle)
                       FileUtility.setStyle(mCounter!!, this, rtQueueFontStyle)
                   } else {
                       //textView.paintFlags = textView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                   }

                   if (rtQueueAlign.isNotEmpty()) {
                       FileUtility.setTextGravity(mToken!!,rtQueueAlign)
                       FileUtility.setTextGravity(mCounter!!,rtQueueAlign)
                   }
               }

               if (tickerText != null)
               {
                   if (!tickerSpeed.isNullOrEmpty())
                   {
                       FileUtility.setTickerSpeed(tickerText!!,tickerSpeed)
                   }
                   if (tickerReset)
                   {
                       //FileUtility.resetTickerArea(this,tickerText!!)
                   }

                  /* if (tickerBgColor.isNotEmpty()) {
                       tickerText?.setBackgroundColor(Color.parseColor("#$tickerBgColor"))
                   }*/
                   if (llTickerView != null)
                   {
                       if (tickerBgColor.isNotEmpty()) {
                           llTickerView?.setBackgroundColor(Color.parseColor("#$tickerBgColor"))
                       }
                   }
                   if (tickerTxtcolor.isNotEmpty()) {
                      tickerText?.setTextColor(Color.parseColor("#$tickerTxtcolor"))
                      tickerText?.init(this.windowManager)
                   }
                   if (tickerFontSize != 0) {
                       FileUtility.setFontSize(tickerText!!, this, tickerFontSize)
                   }
                   if (tickerFont.isNotEmpty()) {
                       FileUtility.setTextFont(tickerText!!, this, tickerFont)
                       tickerText?.init(this.windowManager)
                   }
                   if (tickerFontStye.isNotEmpty()) {
                       FileUtility.setStyle(tickerText!!, this, tickerFontStye)
                       tickerText?.init(this.windowManager)
                   }

                   if (tickerDirection.isNotEmpty()) {
                       FileUtility.setTickerDirection(tickerText!!, tickerDirection)
                   }

               }
           }
       }//when end
    }

    override fun onLongClickPerform(position: Int) {
        showPasswordDialog(this,layoutInflater,this)
        //showCustmizeDialog(this, layoutInflater, PreferanceRepository.getInt(Constants.Current_Device_Layout_ID), this)
    }

    override fun onDownloadComplete() {
            val i = Intent(this, MainActivity::class.java)
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
    }

    private fun setSavedData(){
        val data_save = "SAVED_THEME"

        if (tokenCounterView != null)
        {
            if(!PreferanceRepository.getString(data_save+"_Header_bg_txt").isNullOrEmpty()){
                tokenCounterView?.setBackgroundColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Header_bg_txt")))
            }
        }

        if (txtTicket != null && txtCounter != null)
        {
            if(!PreferanceRepository.getString(data_save+"_Header_txt").isNullOrEmpty()){
                txtTicket?.setTextColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Header_txt")))
                txtCounter?.setTextColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Header_txt")))
            }
            if(PreferanceRepository.getInt(data_save+"_Spinner_header_font_size")!=0){
                FileUtility.setFontSize(txtTicket!!, this, PreferanceRepository.getInt(data_save+"_Spinner_header_font_size"))
                FileUtility.setFontSize(txtCounter!!, this, PreferanceRepository.getInt(data_save+"_Spinner_header_font_size"))
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_header_font_family").isNullOrEmpty()){
                FileUtility.setTextFont(txtTicket!!, this, PreferanceRepository.getString(data_save+"_Spinner_header_font_family"))
                FileUtility.setTextFont(txtCounter!!, this, PreferanceRepository.getString(data_save+"_Spinner_header_font_family"))
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_header_font_style").isNullOrEmpty()){
                FileUtility.setStyle(txtTicket!!, this, PreferanceRepository.getString(data_save+"_Spinner_header_font_style"))
                FileUtility.setStyle(txtCounter!!, this, PreferanceRepository.getString(data_save+"_Spinner_header_font_style"))
            } else {
                //textView.paintFlags = textView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_header_text_align").isNullOrEmpty()){
                FileUtility.setTextGravity(txtTicket!!, PreferanceRepository.getString(data_save+"_Spinner_header_text_align"))
                FileUtility.setTextGravity(txtCounter!!, PreferanceRepository.getString(data_save+"_Spinner_header_text_align"))
            }
            if(!PreferanceRepository.getString(data_save+"_HeaderLable_type").isNullOrEmpty()){
                FileUtility.setTextType(this,txtTicket!!, txtCounter!!,PreferanceRepository.getString(data_save+"_HeaderLable_type"))
            }
        }

        if (tickerText != null)
        {
            if (llTickerView != null)
            {
                if(!PreferanceRepository.getString(data_save+"_Ticker_bg_txt").isNullOrEmpty()){
                    llTickerView?.setBackgroundColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Ticker_bg_txt")))
                }
            }
           /* if(!PreferanceRepository.getString(data_save+"_Ticker_bg_txt").isNullOrEmpty()){
                tickerText?.setBackgroundColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Ticker_bg_txt")))
            }*/
            if(!PreferanceRepository.getString(data_save+"_Ticker_txt").isNullOrEmpty()){
                tickerText?.setTextColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Ticker_txt")))
            }
            if(PreferanceRepository.getInt(data_save+"_Spinner_ticker_font_size")!=0){
                FileUtility.setFontSize(tickerText!!, this, PreferanceRepository.getInt(data_save+"_Spinner_ticker_font_size"))
            }
            if (!PreferanceRepository.getString(data_save+"_Spinner_ticker_font_family").isNullOrEmpty()) {
                FileUtility.setTextFont(tickerText!!, this, PreferanceRepository.getString(data_save+"_Spinner_ticker_font_family"))
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_ticker_font_style").isNullOrEmpty()){
                FileUtility.setStyle(tickerText!!, this, PreferanceRepository.getString(data_save+"_Spinner_ticker_font_style"))
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_ticker_direction").isNullOrEmpty()){
                FileUtility.setTickerDirection(tickerText!!, PreferanceRepository.getString(data_save+"_Spinner_ticker_direction"))
            }
            if(!PreferanceRepository.getString(data_save+"_Ticker_speed").isNullOrEmpty()){
                FileUtility.setTickerSpeed(tickerText!!, PreferanceRepository.getString(data_save+"_Ticker_speed"))
            }
        }

        if (mToken != null && mCounter != null)
        {
            if(!PreferanceRepository.getString(data_save+"_RtQueue_txtColor").isNullOrEmpty()){
                mToken?.setTextColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_RtQueue_txtColor")))
                mCounter?.setTextColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_RtQueue_txtColor")))
            }
            if(!PreferanceRepository.getString(data_save+"_RtQueue_bgColor").isNullOrEmpty()){
                rtCounter?.setCardBackgroundColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_RtQueue_bgColor")))
                rtToken?.setCardBackgroundColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_RtQueue_bgColor")))
            }
            if(PreferanceRepository.getInt(data_save+"_RtQueue_fontsize")!=0){
                FileUtility.setFontSize(mToken!!, this, PreferanceRepository.getInt(data_save+"_RtQueue_fontsize"))
                FileUtility.setFontSize(mCounter!!, this, PreferanceRepository.getInt(data_save+"_RtQueue_fontsize"))
            }
            if(!PreferanceRepository.getString(data_save+"_rtqueue_font_family").isNullOrEmpty()){
                FileUtility.setTextFont(mToken!!, this, PreferanceRepository.getString(data_save+"_rtqueue_font_family"))
                FileUtility.setTextFont(mCounter!!, this, PreferanceRepository.getString(data_save+"_rtqueue_font_family"))
            }
            if(!PreferanceRepository.getString(data_save+"_RtQueue_fontStyle").isNullOrEmpty()){
                FileUtility.setStyle(mToken!!, this, PreferanceRepository.getString(data_save+"_RtQueue_fontStyle"))
                FileUtility.setStyle(mCounter!!, this, PreferanceRepository.getString(data_save+"_RtQueue_fontStyle"))
            } else {
                //textView.paintFlags = textView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }
            if(!PreferanceRepository.getString(data_save+"queue_text_align").isNullOrEmpty()){
                FileUtility.setTextGravity(mToken!!, PreferanceRepository.getString(data_save+"queue_text_align"))
                FileUtility.setTextGravity(mCounter!!, PreferanceRepository.getString(data_save+"queue_text_align"))
            }
        }

        if (tokenRecyclerView != null && llGreenView != null)
        {
            if(!PreferanceRepository.getString(data_save+"_Queue_bg_txt").isNullOrEmpty()){
                tokenRecyclerView?.setBackgroundColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Queue_bg_txt")))
                llGreenView?.setBackgroundColor(Color.parseColor("#"+PreferanceRepository.getString(data_save+"_Queue_bg_txt")))
            }
            var rowsCount: Int = 0
            var queueTxtcolorCode:String = ""
            var queueFont:String = ""
            var queueBgColor:String = ""
            var queueFontSize:Int = 30
            var queueFontStye:String = ""
            var queueTxtAlign:String= ""
            var queueSoundDuration:String= "2"
            var queueBlinkDuration:String= "5"
            var queueLineColor = ""

            if(PreferanceRepository.getInt(data_save+"_Spinner_no_of_row_txt")!= 0) {
                rowsCount = PreferanceRepository.getInt(data_save+"_Spinner_no_of_row_txt")
            }
            if(!PreferanceRepository.getString(data_save+"_Queue_txt").toString().isNullOrEmpty()) {
                queueTxtcolorCode = PreferanceRepository.getString(data_save+"_Queue_txt").toString()
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_queue_font_family").toString().isNullOrEmpty()) {
                queueFont = PreferanceRepository.getString(data_save+"_Spinner_queue_font_family")
            }
            if(!PreferanceRepository.getString(data_save+"_Queue_bg_txt").isNullOrEmpty()) {
                queueBgColor = PreferanceRepository.getString(data_save+"_Queue_bg_txt")
            }
            if(PreferanceRepository.getInt(data_save+"_Spinner_queue_font_size")!= 0) {
                queueFontSize = PreferanceRepository.getInt(data_save+"_Spinner_queue_font_size")
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_queue_font_style").isNullOrEmpty()) {
                queueFontStye = PreferanceRepository.getString(data_save+"_Spinner_queue_font_style")
            }
            if(!PreferanceRepository.getString(data_save+"_Spinner_queue_text_align").isNullOrEmpty()) {
                queueTxtAlign = PreferanceRepository.getString(data_save+"_Spinner_queue_text_align")
            }

            if(!PreferanceRepository.getString(data_save+"_Queue_blinkspeed").isNullOrEmpty()) {
                queueBlinkDuration = PreferanceRepository.getString(data_save+"_Queue_blinkspeed")
                blinkDuration = PreferanceRepository.getString(data_save+"_Queue_blinkspeed")
            }

            if(!PreferanceRepository.getString(data_save+"_Queue_soundspeed").isNullOrEmpty()) {
                queueSoundDuration = PreferanceRepository.getString(data_save+"_Queue_soundspeed")
                soundDuration = PreferanceRepository.getString(data_save+"_Queue_soundspeed")
            }

            if(!PreferanceRepository.getString(data_save+"_Queue_sound").isNullOrEmpty()) {
                selectedSoundName = PreferanceRepository.getString(data_save+"_Queue_sound")
            }

            if(!PreferanceRepository.getString(data_save+"_Queue_Line").isNullOrEmpty()) {
                queueLineColor = PreferanceRepository.getString(data_save+"_Queue_Line")
            }

            adapterTokenCounter.setViewsColors(false,rowsCount, queueTxtcolorCode, queueFont, queueBgColor,
                    queueFontSize, queueFontStye, queueTxtAlign,queueBlinkDuration,
                queueSoundDuration,selectedSoundName,queueLineColor)
        }

    }//method end


    private fun setDelay(udpMsg: String?){
        udpMsg.let {
            val splitUdp = udpMsg?.split("*")
            val data = splitUdp?.get(1)
            val splitTokenCounter = data?.split(",")
            val tokenNumber = splitTokenCounter?.get(0)
            val counterNumber = splitTokenCounter?.get(1)
            //tokenCounterList.add(TokenCounter(tokenNumber,counterNumber))
               /* oldTokenCounterObj?.let {
                    adapterTokenCounter.setTokenCounter(it)
                }
                oldTokenCounterObj = TokenCounter(tokenNumber, counterNumber)*/

            if (mToken != null && mCounter != null)
            {
                mToken?.text = tokenNumber
                mCounter?.text = counterNumber

                val blink_anim = AnimationUtils.loadAnimation(this, R.anim.blink)
                mToken?.startAnimation(blink_anim)
                mCounter?.startAnimation(blink_anim)

                Handler().postDelayed({
                    mToken?.clearAnimation()
                    mCounter?.clearAnimation()
                }, getSoundTime(blinkDuration).toLong())

                playRingtone(TokenCounter(tokenNumber, counterNumber),soundDuration,selectedSoundName)
            }else
            {
              adapterTokenCounter.setTokenCounter(TokenCounter(tokenNumber,counterNumber),false)
            }
            //adapterTokenCounter.setTokenCounter(TokenCounter(tokenNumber,counterNumber))
        }
    }
    override fun onUdpReceived(udpMsg: String?) {
        println("udp called")
        udpMsg.let {
            udpTokenList.add(0,it!!)

            if(PreferanceRepository.getInt("SAVED_THEME"+"_Spinner_no_of_row_txt") != 0) {
                val rowsCount = PreferanceRepository.getInt("SAVED_THEME"+"_Spinner_no_of_row_txt")
                if (udpTokenList.size ==  rowsCount + 1)
                {
                  udpTokenList.removeLast()
                }
            }
        }
//1,2,3
        //PreferanceRepository.setUdpTokenList(udpTokenList)
        if (udpTokenList.size > 1)
        {
            for (itemIndex in udpTokenList.size - 2 downTo 0 )
            {
               Handler(Looper.getMainLooper()).postDelayed({
                      setDelay(udpTokenList[itemIndex])
               },soundDuration.toLong())

            }
        }else
        {
          setDelay(udpMsg)
          //udpListIndex++
        }
    }

    private fun setAdapter(){
        adapterTokenCounter = TokenCounterAdapter(this, this, tokenCounterList, viewModel = mViewModel!!,this)
        /*tokenRecyclerView?.addItemDecoration(
                DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                )
        )*/
        tokenRecyclerView?.adapter = adapterTokenCounter
        adapterTokenCounter.notifyDataSetChanged()
        /*if (tokenRecyclerView?.childCount == 0)
        {
          udpTokenList = PreferanceRepository.getUdpTokenList("UdpTokenList")?.toMutableList() as ArrayList<String>

          val demoUdpToken = udpTokenList.toMutableList()
            demoUdpToken.forEach {
             setDelay(it)
          }
        }*/
    }

    private fun playRingtone(tokenCounter: TokenCounter, soundSpeed: String,soundName: String){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("pppp", "playRingtone: $selectedSoundName ${getSound(soundName)} ")
            val ringtonePlayer = RingtonePlayer(this@MainActivity, getSound(soundName))
            ringtonePlayer.play(true,soundSpeed.toInt())
            //delay(getSoundTime(soundSpeed).toLong())
            if (ringtonePlayer.mediaPlayerObj != null) {
                try {
                    delay((ringtonePlayer.mediaPlayerObj.duration * soundSpeed.toInt()).toLong())
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
                ringtonePlayer.stop()
            }
        }
    }

    override fun onRepeatToken(tokenCounter: TokenCounter,soundSpeed: String,soundName: String) {
        playRingtone(tokenCounter,soundSpeed,soundName)
    }

    override fun showSettingsDialog() {
        showCustmizeDialog(this, layoutInflater, templateLayoutId, this)
    }
}