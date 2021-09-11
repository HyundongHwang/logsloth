package com.hhd.logslothsampleand.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;


public class MyUtil {
    private static final String TAG = MyUtil.class.getSimpleName();

    public static ScrollView createScrollViewMpWc(Context context) {
        ScrollView sv = new ScrollView(context);
        ViewGroup.LayoutParams mpwc = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sv.setLayoutParams(mpwc);
        sv.setHorizontalScrollBarEnabled(false);
        sv.setSmoothScrollingEnabled(true);
        sv.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        return sv;
    }

    public static LinearLayout createLinearLayoutMpWc(Context context) {
        LinearLayout ll = new LinearLayout(context);
        ViewGroup.LayoutParams mpwc = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(mpwc);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setHorizontalScrollBarEnabled(false);
        return ll;
    }

    public static FlexboxLayout createFlexboxLayoutMpWc(Context context) {
        FlexboxLayout fl = new FlexboxLayout(context);
        ViewGroup.LayoutParams mpwc = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fl.setLayoutParams(mpwc);
        fl.setFlexWrap(FlexWrap.WRAP);
        fl.setHorizontalScrollBarEnabled(false);
        return fl;
    }

    public static LinearLayout createActivityGatewayLinearLayout(final Activity activity) {
        LinearLayout ll = MyUtil.createLinearLayoutMpWc(activity);

        try {
            PackageManager pm = activity.getPackageManager();
            String pkgName = activity.getPackageName();
            PackageInfo pi = pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);

            for (ActivityInfo ai : pi.activities) {
                final Class<?> cls = Class.forName(ai.name);

                if (activity.getClass() == cls)
                    continue;


                Button btn = new Button(activity);
                btn.setAllCaps(false);
                btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                btn.setText(cls.getSimpleName());
                ll.addView(btn);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity, cls));
                    }
                });
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        return ll;
    }

    public static boolean isScreenPortrait(Context context) {
        WindowManager winMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = winMgr.getDefaultDisplay();

        if (disp.getRotation() == Surface.ROTATION_0 ||
                disp.getRotation() == Surface.ROTATION_180) {
            return true;
        }

        return false;
    }

    private static Gson _gson = null;

    static {
        _gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public static String toJsonStr(Object obj) {
        String res = _gson.toJson(obj);
        return res;
    }

    public static float dp2Px(Context context, float dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        return px;
    }

    public static float sp2Px(Context context, float sp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, dm);
        return px;
    }

    public static int getOrientation(Context context) {
        // Orientation
        WindowManager winMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = winMgr.getDefaultDisplay().getRotation();
        int orientation = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = 90;
                break;
            case Surface.ROTATION_90:
                orientation = 0;
                break;
            case Surface.ROTATION_180:
                orientation = 270;
                break;
            case Surface.ROTATION_270:
                orientation = 180;
                break;
        }

        return orientation;
    }


    private static HashMap<String, Pair<Handler, HandlerThread>> _workerMap = new HashMap<>();

    public static Handler getWorkerHandler(String threadName) {

        if (_workerMap.containsKey(threadName)) {
            Handler handler = _workerMap.get(threadName).first;
            return handler;
        }

        HandlerThread thread = new HandlerThread(threadName);
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        _workerMap.put(threadName, new Pair(handler, thread));
        return handler;
    }

    public static void closeAllWorkerHandler() {
        for (Pair<Handler, HandlerThread> hh :_workerMap.values()) {
            hh.second.quitSafely();
            try {
                hh.second.join();
            } catch (Exception e) {
            }
        }
        _workerMap.clear();
    }
}

