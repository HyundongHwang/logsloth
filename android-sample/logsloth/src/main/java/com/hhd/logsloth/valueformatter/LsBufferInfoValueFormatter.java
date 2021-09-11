package com.hhd.logsloth.valueformatter;

import android.media.MediaCodec;

import com.hhd.logsloth.LogSloth;

public class LsBufferInfoValueFormatter implements LogSloth.IValueFormatter {

    @Override
    public boolean canFormat(Object value) {
        boolean res = value instanceof MediaCodec.BufferInfo;
        return res;
    }

    @Override
    public String format(Object value) {
        MediaCodec.BufferInfo bi = (MediaCodec.BufferInfo) value;
        String res = String.format("[%d:%d:%d:%d]", bi.offset, bi.size, bi.presentationTimeUs, bi.flags);
        return res;
    }
}
