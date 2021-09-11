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
        LogSloth.writeLog(Log.ERROR, Log.getStackTraceString(ex));
    }

    public static void empty() {
        LogSloth.writeLog(Log.DEBUG, "");
    }

    public static void writeLog(
            int level,
            String strFormat,
            Object... args) {

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

        String methodName = st.getMethodName();

        if (st.getMethodName().equals("invoke")) {
            String[] tokenList = st.getClassName().split("\\$");
            if (tokenList.length > 1) {
                methodName = st.getClassName().split("\\$")[1];
            }
        }

        writeLog(level,
                st.getFileName(),
                st.getLineNumber(),
                methodName,
                strFormat,
                args);
    }

    @SuppressLint("DefaultLocale")
    public static void writeLog(
            int level,
            String strFileName,
            int nLineNum,
            String strFuncName,
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
                Thread.currentThread().getName(),
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
