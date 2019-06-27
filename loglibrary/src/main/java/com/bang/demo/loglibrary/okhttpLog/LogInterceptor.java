package com.bang.demo.loglibrary.okhttpLog;

import com.bang.demo.loglibrary.LogUtil;

public class LogInterceptor implements HttpLoggingInterceptorM.Logger{
    public static String INTERCEPTOR_TAG_STR = "OkHttp";
    public LogInterceptor(String tag) {
        INTERCEPTOR_TAG_STR = tag;
    }
    @Override
    public void log(String message, int type) {
        LogUtil.printLog(false, type, INTERCEPTOR_TAG_STR, message);
    }
}
