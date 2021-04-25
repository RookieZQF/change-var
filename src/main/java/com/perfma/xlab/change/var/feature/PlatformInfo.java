package com.perfma.xlab.change.var.feature;


/**
 * @author: ZQF
 * @date: 2021-04-23
 * @description: desc
 */
public class PlatformInfo {
    public static String getOS() {
        String os = System.getProperty("os.name");
        if (os.equals("SunOS")) {
            return "solaris";
        } else if (os.equals("Linux")) {
            return "linux";
        } else if (os.equals("FreeBSD")) {
            return "bsd";
        } else if (os.equals("NetBSD")) {
            return "bsd";
        } else if (os.equals("OpenBSD")) {
            return "bsd";
        } else if (os.contains("Darwin") || os.contains("OS X")) {
            return "darwin";
        } else if (os.startsWith("Windows")) {
            return "win32";
        } else {
            throw new RuntimeException("Operating system " + os + " not yet supported");
        }
    }

    /* Returns "sparc" for SPARC based platforms and "x86" for x86 based
       platforms. Otherwise returns the value of os.arch.  If the value
       is not recognized as supported, an exception is thrown instead. */
    public static String getCPU() {
        String cpu = System.getProperty("os.arch");
        if (cpu.equals("i386") || cpu.equals("x86")) {
            return "x86";
        } else if (cpu.equals("sparc") || cpu.equals("sparcv9")) {
            return "sparc";
        } else if (cpu.equals("ia64") || cpu.equals("amd64") || cpu.equals("x86_64")) {
            return cpu;
        } else {
            throw new RuntimeException("CPU type " + cpu + " not yet supported");
        }
    }

    public static String getBit(){
        return System.getProperty("sun.arch.data.model");
    }

    public static boolean isSupport(){
        String os = getOS();
        if(!"linux".equals(os)){
            return false;
        }
        String cpu = getCPU();
        if("x86_64".equals(cpu) || "amd64".equals(cpu)){
            return true;
        }
        return false;
    }
}
