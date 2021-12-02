package com.hhd.logsloth;

import android.annotation.SuppressLint;
import android.util.Log;

import com.hhd.logsloth.valueformatter.LsBufferInfoValueFormatter;
import com.hhd.logsloth.valueformatter.LsByteArrayValueFormatter;
import com.hhd.logsloth.valueformatter.LsByteBufferValueFormatter;
import com.hhd.logsloth.valueformatter.LsMediaFormatValueFormatter;


public class LogSloth {

    private static final String TAG = "LOGSLOTH";
    private static final String[] _INTERNAL_FILE_LIST = {"VMStack.java", "Thread.java", "LogSloth.java", "LogSloth.kt"};
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
        LogSloth.writeLog(Log.DEBUG, "↘↘↘");
    }

    public static void leave() {
        LogSloth.writeLog(Log.DEBUG, "↗↗↗");
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
        LogSloth.writeLog(Log.DEBUG, logStr);
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
        LogSloth.writeLog(Log.DEBUG, logStr);
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
        LogSloth.writeLog(Log.DEBUG, logStr);
    }

    public static void d(String strFormat, Object... args) {
        LogSloth.writeLog(Log.DEBUG, strFormat, args);
    }

    public static void w(String strFormat, Object... args) {
        LogSloth.writeLog(Log.WARN, strFormat, args);
    }

    public static void e(String strFormat, Object... args) {
        LogSloth.writeLog(Log.ERROR, strFormat, args);
    }

    public static void i(String strFormat, Object... args) {
        LogSloth.writeLog(Log.INFO, strFormat, args);
    }

    public static void v(String strFormat, Object... args) {
        LogSloth.writeLog(Log.VERBOSE, strFormat, args);
    }

    public static void exception(Exception ex) {
        String log = _showStackTraceInfo ? Log.getStackTraceString(ex) : ex.toString();
        LogSloth.writeLog(Log.ERROR, log);
    }

    public static void empty() {
        LogSloth.writeLog(Log.DEBUG, "");
    }

    public static void writeLog(
            int level,
            String strFormat,
            Object... args) {

        if (level < _logLevel)
            return;


        StackTraceElement[] stList = Thread.currentThread().getStackTrace();
        StackTraceElement st = null;

        for (StackTraceElement item : stList) {
            boolean isInFile = false;
            for (String inFileName : _INTERNAL_FILE_LIST) {
                if (item.getFileName().equals(inFileName)) {
                    isInFile = true;
                    break;
                }
            }
            if (!isInFile) {
                st = item;
                break;
            }
        }

        if (st == null)
            return;

        String methodName = null;

        if (_showFuncInfo) {
            methodName = st.getMethodName();
            if (st.getMethodName().equals("invoke")) {
                String[] tokenList = st.getClassName().split("\\$");
                if (tokenList.length > 1) {
                    methodName = st.getClassName().split("\\$")[1];
                }
            }
        } else {
            methodName = "M";
        }

        String fileName = _showFileInfo ? st.getFileName() : "F";
        int lineNum = _showLineInfo ? st.getLineNumber() : -1;
        @SuppressLint("DefaultLocale")
        String threadName = _showThreadName ? Thread.currentThread().getName() : String.format("T_%d", Thread.currentThread().getId());

        writeLog(level,
                fileName,
                lineNum,
                methodName,
                threadName,
                strFormat,
                args);
    }

    @SuppressLint("DefaultLocale")
    private static void writeLog(
            int level,
            String strFileName,
            int nLineNum,
            String strFuncName,
            String threadName,
            String strFormat,
            Object... args) {

        String strLog = "";

        if (args != null && args.length > 0) {
            strLog = String.format(strFormat, args);
        } else {
            strLog = strFormat == null ? "" : strFormat;
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
