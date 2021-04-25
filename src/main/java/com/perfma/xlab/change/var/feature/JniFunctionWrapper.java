package com.perfma.xlab.change.var.feature;

/**
 * @author: ZQF
 * @date: 2021-04-23
 * @description: desc
 */
public class JniFunctionWrapper {
    public static native void detach0();

    public static native int attach0(int pid);

    public static native long getSymbolAddr(String symbol);

    public static native int readAddrValue(long addr, byte[] bytes, int length);

    public static native int writeAddrValue(long addr, byte[] bytes, int length);
}
