package com.pipishou.aiuitest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.iflytek.aiui.AIUIAgent
import com.iflytek.aiui.AIUIConstant
import com.iflytek.aiui.AIUIListener
import com.iflytek.aiui.AIUIMessage
import com.iflytek.cloud.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    companion object {
        //录音权限
        private val permissions =
            arrayOf(Manifest.permission.RECORD_AUDIO)
        private const val TAG = "MainActivity"
    }

    private var mAIUIState: Int = 0
    private val mAIUIListener: AIUIListener = AIUIListener { event ->
        when (event.eventType) {
            AIUIConstant.EVENT_WAKEUP -> {
                //唤醒事件
                Log.i(TAG, "on event: " + event.eventType)
            }
            AIUIConstant.EVENT_RESULT -> {
                //结果解析事件

                //结果解析事件
                try {
                    val bizParamJson = JSONObject(event.info)
                    val data = bizParamJson.getJSONArray("data").getJSONObject(0)
                    val params = data.getJSONObject("params")
                    val content = data.getJSONArray("content").getJSONObject(0)
                    if (content.has("cnt_id")) {
                        val cnt_id = content.getString("cnt_id")
                        val cntJson =
                            JSONObject(
                                String(
                                    event.data.getByteArray(cnt_id)!!,
                                    Charset.defaultCharset()
                                )
                            )
                        val sub = params.optString("sub")
                        if ("nlp" == sub) {
                            // 解析得到语义结果
                            val resultStr = cntJson.optString("intent")
                            Log.i(TAG, resultStr)
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            AIUIConstant.EVENT_VAD -> {
                if (AIUIConstant.VAD_BOS == event.arg1) {
                    //语音前端点

                    Log.e(TAG, "AIUIConstant.VAD_BOS")
                } else if (AIUIConstant.VAD_EOS == event.arg1) {
                    //语音后端点
                    Log.e(TAG, "AIUIConstant.VAD_EOS")

                }
            }
            AIUIConstant.EVENT_STOP_RECORD -> {
                Log.i(TAG, "on event: EVENT_STOP_RECORD" + event.eventType);
            }
            AIUIConstant.EVENT_START_RECORD -> {
                Log.i(TAG, "on event: EVENT_START_RECORD" + event.eventType);

            }
            AIUIConstant.EVENT_SLEEP -> {

            }
            AIUIConstant.EVENT_STATE -> {
                mAIUIState = event.arg1
                if (AIUIConstant.STATE_IDLE == mAIUIState) {
                    // 闲置状态，AIUI未开启
                } else if (AIUIConstant.STATE_READY == mAIUIState) {
                    // AIUI已就绪，等待唤醒
                } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                    // AIUI工作中，可进行交互
                }
            }

            AIUIConstant.EVENT_ERROR -> {
                //错误事件
                Log.i(TAG, "on event: " + event.eventType);
                Log.e(TAG, "错误: " + event.arg1 + "\n" + event.info);
            }
        }
    }
    private var mAIUIAgent: AIUIAgent? = null
//    private val mWakeuperListener: WakeuperListener =
//        object : WakeuperListener {
//            override fun onResult(result: WakeuperResult) {
//                //唤醒成功
//                try {
//                    val text = result.resultString
//                    val `object`: JSONObject
//                    `object` = JSONObject(text)
//                    val buffer = StringBuffer()
//                    buffer.append("【RAW】 $text")
//                    buffer.append("\n")
//                    buffer.append("【操作类型】" + `object`.optString("sst"))
//                    buffer.append("\n")
//                    buffer.append("【唤醒词id】" + `object`.optString("id"))
//                    buffer.append("\n")
//                    buffer.append("【得分】" + `object`.optString("score"))
//                    buffer.append("\n")
//                    buffer.append("【前端点】" + `object`.optString("bos"))
//                    buffer.append("\n")
//                    buffer.append("【尾端点】" + `object`.optString("eos"))
//                    val resultString = buffer.toString()
//                    Log.d(TAG,resultString)
//                } catch (e: JSONException) {
////                    resultString = "结果解析出错"
////
////                    awakeEventListener?.onFailed("唤醒结果解析出错")
//
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onError(error: SpeechError) {
//                Log.e(TAG, "onError: ")
//                if (10119 == error.errorCode) {
//                    //识别部分内容为空，但是唤醒是成功的
////                    awakeEventListener?.onResult(null)
//                } else {
////                    awakeEventListener?.onFailed(error.message)
//                }
//
//                //            showTip(error.getPlainDescription(true));
//            }
//
//            override fun onBeginOfSpeech() {
////            showTip("开始说话");
//                Log.i(TAG, "onBeginOfSpeech: ")
////                awakeEventListener?.onInitSuccess()
//            }
//
//            override fun onEvent(eventType: Int, isLast: Int, arg2: Int, obj: Bundle) {
////            Log.d(TAG, "eventType:"+eventType+ "arg1:"+isLast + "arg2:" + arg2);
//                // 识别结果
////                if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
////                    val result =
////                        obj[SpeechEvent.KEY_EVENT_IVW_RESULT] as RecognizerResult?
////                    val arrayData: List<String>? =
////                        JsonParser.parseEventContent(result!!.resultString)
////                    if (null != arrayData && arrayData.isNotEmpty()) {
////                        recoString = arrayData[0]
////                        //发送识别结果
////                        awakeEventListener?.onResult(recoString)
////
////                    } else {
////                        awakeEventListener?.onResult(null)
////                    }
////                }
//                Log.i(TAG, "onEvent: ")
//            }
//
//            override fun onVolumeChanged(volume: Int) {
//                // TODO Auto-generated method stub
//            }
//        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), mAIUIListener)
//        mIvw = VoiceWakeuper.createWakeuper(this, null)
//        mIvw!!.setParameter( SpeechConstant.IVW_SST,"wakeup" )
//        mIvw!!.startListening( mWakeuperListener )

//        Handler().postDelayed({
//            val params = "data_type=text"
//            val msg = AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, "抛硬币".toByteArray())
//            mAIUIAgent?.sendMessage(msg)
//            Log.e(TAG, "抛硬币")
//        }, 2000)
//        Handler().postDelayed({
//            val params = "data_type=text"
//            val msg = AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, "附近酒店".toByteArray())
//            mAIUIAgent.sendMessage(msg)
//            Log.e(TAG,"附近酒店")
//        },6000)

        if (AIUIConstant.STATE_WORKING != mAIUIState) {
//            val sleepMsg =
//                AIUIMessage(AIUIConstant.CMD_RESET_WAKEUP, 0, 0, "", null)
//            mAIUIAgent?.sendMessage(sleepMsg)
//            Log.d(TAG,"wakeupMsg")
//            val wakeupMsg =
//                AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null)
//            mAIUIAgent?.sendMessage(wakeupMsg)
//            Log.d(TAG,"wakeupMsg")

            val writeMsg =
                AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null)
            mAIUIAgent?.sendMessage(writeMsg)
            Log.d(TAG,"writeMsg")
        } else {
            // 打开AIUI内部录音机，开始录音
            val writeMsg =
                AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null)
            mAIUIAgent?.sendMessage(writeMsg)
            Log.d(TAG,"writeMsg")

        }
        activity_main_btn.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (AIUIConstant.STATE_WORKING != mAIUIState) {
                    val wakeupMsg =
                        AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null)
                    mAIUIAgent?.sendMessage(wakeupMsg)
                } else {
                    // 打开AIUI内部录音机，开始录音
                    val params1 = "sample_rate=16000,data_type=audio"
                    val writeMsg =
                        AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params1, null)
                    mAIUIAgent?.sendMessage(writeMsg)
                }
            }
            if (event.action == MotionEvent.ACTION_UP) {
                val params1 = "sample_rate=16000,data_type=audio"
                val wakeupMsg =
                    AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, params1, null)
                mAIUIAgent?.sendMessage(wakeupMsg)
            }
            false
        })

    }


    private fun aiuiStart() {
        if (AIUIConstant.STATE_WORKING != mAIUIState) {
            val wakeupMsg =
                AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null)
            mAIUIAgent?.sendMessage(wakeupMsg)
        } else {
            // 打开AIUI内部录音机，开始录音
            val params1 = "sample_rate=16000,data_type=audio"
            val writeMsg =
                AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params1, null)
            mAIUIAgent?.sendMessage(writeMsg)
        }
    }

    private fun aiuiPause() {
        val params1 = "sample_rate=16000,data_type=audio"
        val wakeupMsg =
            AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, params1, null)
        mAIUIAgent?.sendMessage(wakeupMsg)
    }

    private fun getAIUIParams(): String? {
        var params: String? = ""
        val assetManager = resources.assets
        try {
            val ins: InputStream = assetManager.open("cfg/aiui_phone.cfg")
            val buffer = ByteArray(ins.available())
            ins.read(buffer)
            ins.close()
            params = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return params
    }

    //申请录音权限
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val i: Int = ContextCompat.checkSelfPermission(this, permissions.get(0))
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 321)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 321) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                }
            }
        }
    }
}