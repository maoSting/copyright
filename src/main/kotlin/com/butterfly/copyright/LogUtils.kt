package com.butterfly.copyright

object LogUtils {
    // 打印信息
    fun print(msg: String){
        println("$msg ");
    }

    // 打印信息（自动换行）
    fun println(msg: String){
        println(msg);
    }

    // 打印错误信息
    fun error(msg: String){
        println(msg);
    }
}