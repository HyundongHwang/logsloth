package com.hhd.logsloth.valueformatter;

import com.hhd.logsloth.LogSloth;

import java.nio.ByteBuffer;

public class LsByteBufferValueFormatter implements LogSloth.IValueFormatter {

    @Override
    public boolean canFormat(Object value) {
        boolean res = value instanceof ByteBuffer;
        return res;
    }

    @Override
    public String format(Object value) {
        ByteBuffer bb = (ByteBuffer) value;
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("[%d:%d:%d] [\n", bb.position(), bb.limit(), bb.capacity()));

        try {
            for (int i = 0; i < Math.min(bb.capacity(), 30); i++) {
                sb.append(String.format("%02x ", bb.get(i)));

                if (i != 0 && i != 29 && i % 10 == 0) {
                    sb.append('\n');
                }
            }
        } catch (Exception ex) {
        }

        if (bb.capacity() > 30) {
            sb.append("...");
        }

        sb.append(']');
        return sb.toString();
    }
}
