package com.pipishou.aiuitest.application

import android.app.Application
import com.iflytek.cloud.SpeechUtility

/**
 *@Author: yiqing
 *@CreateDate: 2021/1/25 18:17
 *@UpdateDate: 2021/1/25 18:17
 *@Description:
 *@ClassName: MyApplication
 */
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        SpeechUtility.createUtility(this, String.format("engine_start=ivw,delay_init=0,appid=%s","6006c77d"))
    }
}