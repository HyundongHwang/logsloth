package com.hhd.logsloth.valueformatter;

import com.hhd.logsloth.LogSloth;

public class LsByteArrayValueFormatter implements LogSloth.IValueFormatter {

    @Override
    public boolean canFormat(Object value) {
        boolean res = value instanceof byte[];
        return res;
    }

    @Override
    public String format(Object value) {
        byte[] bb = (byte[]) value;
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("[%d] [\n", bb.length));

        try {
            for (int i = 0; i < Math.min(bb.length, 30); i++) {
                sb.append(String.format("%02x ", bb[i]));

                if (i != 0 && i != 29 && i % 10 == 0) {
                    sb.append('\n');
                }
            }
        } catch (Exception ex) {
        }

        if (bb.length > 30) {
            sb.append("...");
        }

        sb.append(']');
        return sb.toString();
    }
}
