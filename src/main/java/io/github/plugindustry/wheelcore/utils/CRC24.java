package io.github.plugindustry.wheelcore.utils;

import javax.annotation.Nonnull;

public class CRC24 {
    private static final int CRC24_INIT = 0x0b704ce;
    private static final int CRC24_POLY = 0x1864cfb;
    private static final CRC24 instance = new CRC24();
    private int crc = CRC24_INIT;

    public static int calculateForString(@Nonnull String str) {
        instance.reset();
        instance.update(str.getBytes());
        return instance.getValue();
    }

    public void update(int b) {
        crc ^= b << 16;
        for (int i = 0; i < 8; i++) {
            crc <<= 1;
            if ((crc & 0x1000000) != 0)
                crc ^= CRC24_POLY;
        }
    }

    public int getValue() {
        return crc;
    }

    public void reset() {
        crc = CRC24_INIT;
    }

    public void update(byte[] data) {
        for (byte b : data)
            update(b);
    }

    public void update(byte[] data, int offset, int length) {
        for (int i = offset; i < offset + length; i++)
            update(data[i]);
    }
}
