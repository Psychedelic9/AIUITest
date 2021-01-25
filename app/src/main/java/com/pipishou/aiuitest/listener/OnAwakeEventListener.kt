package com.pipishou.aiuitest.listener

/**
 *@Author: yiqing
 *@CreateDate: 2020/6/11 16:30
 *@UpdateDate: 2020/6/11 16:30
 *@Description:
 *@ClassName: OnAwakeEventListener
 */
interface OnAwakeEventListener {
    fun onInitSuccess()

    fun onAwake()

    fun onResult(data: String?)

    fun onFailed(error: String?)
}