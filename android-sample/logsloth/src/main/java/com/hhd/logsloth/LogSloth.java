package com.hhd.logsloth;

import android.annotation.SuppressLint;
import android.util.Log;

import com.hhd.logsloth.valueformatter.LsBufferInfoValueFormatter;
import com.hhd.logsloth.valueformatter.LsByteArrayValueFormatter;
import com.hhd.logsloth.valueformatter.LsByteBufferValueFormatter;
import com.hhd.logsloth.valueformatter.LsMediaFormatValueFormatter;


public class LogSloth {

    private static final String TAG = "LOGSLOTH";
    private static final String[] _INTERNAL_FILE_LIST = {"VMStack.java", "Thread.java", "LogSloth.java", "LogSloth.kt", "FlavorCommon.kt"};
    private static final IValueFormatter[] _VALUE_FORMATTER_ARRAY = new IValueFormatter[]{
            new LsBufferInfoValueFormatter(),
            new LsByteArrayValueFormatter(),
            new LsByteBufferValueFormatter(),
            new LsMediaFormatValueFormatter(),
    };

    private static boolean _showFileInfo = true;
    private static boolean _showLineInfo = true;
    private static boolean _showFuncInfo = true;
    private static boolean _showThreadName = true;
    private static boolean _showStackTraceInfo = true;
    private static int _logLevel = Log.VERBOSE;

    public static void init(
            boolean showFileInfo,
            boolean showLineInfo,
            boolean showFuncInfo,
            boolean showThreadName,
            boolean showStackTraceInfo,
            int logLevel
    ) {
        _showFileInfo = showFileInfo;
        _showLineInfo = showLineInfo;
        _showFuncInfo = showFuncInfo;
        _showThreadName = showThreadName;
        _showStackTraceInfo = showStackTraceInfo;
        _logLevel = logLevel;
    }

    public static void enter() {
        LogSloth.printLog(Log.DEBUG, null, "↘↘↘");
    }

    public static void leave() {
        LogSloth.printLog(Log.DEBUG, null, "↗↗↗");
    }

    public static void value(String valueDesc, Object value) {

        String valueStr = "";

        if (value == null) {
            valueStr = "null";
        } else if (value instanceof Integer ||
                value instanceof Float ||
                value instanceof Double ||
                value instanceof Boolean ||
                value instanceof Long ||
                value instanceof Short ||
                value instanceof Character ||
                value instanceof CharSequence) {

            valueStr = value.toString();
        } else {

            boolean canFormat = false;

            for (IValueFormatter formatter : _VALUE_FORMATTER_ARRAY) {
                if (formatter.canFormat(value)) {
                    canFormat = true;
                    valueStr = formatter.format(value);
                    break;
                }
            }

            if (!canFormat) {
                try {
                    valueStr = LsUtil.toJsonStr(value);
                } catch (Exception ex) {
                }
            }
        }

        String logStr = String.format("%s:::%s", valueDesc, valueStr);
        LogSloth.printLog(Log.DEBUG, null, logStr);
    }

    public static void caller(Object callParam) {
        String callParamStr;

        if (callParam == null) {
            callParamStr = "null";
        } else if (callParam instanceof Integer ||
                callParam instanceof Float ||
                callParam instanceof Double ||
                callParam instanceof Boolean ||
                callParam instanceof Long ||
                callParam instanceof Short ||
                callParam instanceof Character ||
                callParam instanceof CharSequence) {
            callParamStr = callParam.toString();
        } else {
            try {
                callParamStr = LsUtil.toJsonStr(callParam);
            } catch (Exception ex) {
                callParamStr = String.format("%s %s", callParam.getClass().getSimpleName(), ex.getClass().getSimpleName());
            }
        }

        String logStr = String.format("→→→%s→→→", callParamStr);
        LogSloth.printLog(Log.DEBUG, null, logStr);
    }

    public static void callee(Object callParam) {
        String callParamStr;

        if (callParam == null) {
            callParamStr = "null";
        } else if (callParam instanceof Integer ||
                callParam instanceof Float ||
                callParam instanceof Double ||
                callParam instanceof Boolean ||
                callParam instanceof Long ||
                callParam instanceof Short ||
                callParam instanceof Character ||
                callParam instanceof CharSequence) {
            callParamStr = callParam.toString();
        } else {
            try {
                callParamStr = LsUtil.toJsonStr(callParam);
            } catch (Exception ex) {
                callParamStr = String.format("%s %s", callParam.getClass().getSimpleName(), ex.getClass().getSimpleName());
            }
        }

        String logStr = String.format("←←←%s←←←", callParamStr);
        LogSloth.printLog(Log.DEBUG, null, logStr);
    }

    public static void d(String strFormat, Object... args) {
        LogSloth.printLog(Log.DEBUG, null, strFormat, args);
    }

    public static void w(String strFormat, Object... args) {
        LogSloth.printLog(Log.WARN, null, strFormat, args);
    }

    public static void e(String strFormat, Object... args) {
        LogSloth.printLog(Log.ERROR, null, strFormat, args);
    }

    public static void i(String strFormat, Object... args) {
        LogSloth.printLog(Log.INFO, null, strFormat, args);
    }

    public static void v(String strFormat, Object... args) {
        LogSloth.printLog(Log.VERBOSE, null, strFormat, args);
    }

    public static void printLog(
            int level,
            Throwable tr,
            String strFormat,
            Object... args) {

        if (level < _logLevel)
            return;

        StackTraceElement[] stList = Thread.currentThread().getStackTrace();
        StackTraceElement st = null;

        for (StackTraceElement item : stList) {
            boolean isInFile = false;
            for (String inFileName : _INTERNAL_FILE_LIST) {
                if (inFileName.equals(item.getFileName())) {
                    isInFile = true;
                    break;
                }
            }
            if (!isInFile) {
                st = item;
                break;
            }
        }

        String methodName = "M";
        String fileName = "F";
        int lineNum = -1;

        if (st != null) {
            if (_showFuncInfo) {
                methodName = st.getMethodName();
                if ("invoke".equals(st.getMethodName())) {
                    String[] tokenList = st.getClassName().split("\\$");
                    if (tokenList.length > 1) {
                        methodName = st.getClassName().split("\\$")[1];
                    }
                }
            }

            if (_showFileInfo) {
                fileName = st.getFileName();
            }

            if (_showLineInfo) {
                lineNum = st.getLineNumber();
            }
        }

        @SuppressLint("DefaultLocale")
        String threadName = _showThreadName ? Thread.currentThread().getName() : String.format("T_%d", Thread.currentThread().getId());

        printLog(level,
                fileName,
                lineNum,
                methodName,
                threadName,
                tr,
                strFormat,
                args);
    }

    @SuppressLint("DefaultLocale")
    private static void printLog(
            int level,
            String strFileName,
            int nLineNum,
            String strFuncName,
            String threadName,
            Throwable tr,
            String strFormat,
            Object... args) {

        String strLog = "";

        if (args != null && args.length > 0) {
            strLog += String.format(strFormat, args);
        } else {
            strLog += strFormat == null ? "" : strFormat;
        }

        if (tr != null) {
            strLog += "\n";
            strLog += _showStackTraceInfo ? Log.getStackTraceString(tr) : tr.toString();
        }

//        strLog = strLog.replace("\n", "↓↓↓");

        String strFormatedLog = String.format("%s:%d:%s %s %s",
                strFileName,
                nLineNum,
                strFuncName,
                threadName,
                strLog);

        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, strFormatedLog);
                break;
            case Log.DEBUG:
                Log.d(TAG, strFormatedLog);
                break;
            case Log.INFO:
                Log.i(TAG, strFormatedLog);
                break;
            case Log.WARN:
                Log.w(TAG, strFormatedLog);
                break;
            case Log.ERROR:
                Log.e(TAG, strFormatedLog);
                break;
            case Log.ASSERT:
                Log.e(TAG, strFormatedLog);
                break;
            default:
                Log.d(TAG, strFormatedLog);
                break;
        }
    }


    public interface IValueFormatter {
        boolean canFormat(Object value);

        String format(Object value);
    }
}
