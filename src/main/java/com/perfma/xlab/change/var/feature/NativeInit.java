package com.perfma.xlab.change.var.feature;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: ZQF
 * @date: 2021-01-08
 * @description: desc
 */
public class NativeInit {
    private static volatile boolean isInit = false;

    public synchronized static void nativeInit() {
        if(!isInit){
            String os = System.getProperty("os.name");
            os = os.toLowerCase();
            String postfix = (os.indexOf("win") != -1) ? ".dll" : (os.indexOf("mac") != -1) ? ".dylib" : ".so";
            try {
                URL url = NativeInit.class.getProtectionDomain().getCodeSource().getLocation();
                url = new URL("jar:file:" + url.getPath() + "!/");
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                int i = 1;
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entityName = jarEntry.getName();
                    if (entityName.endsWith(postfix) && !entityName.contains("jnilib.dSYM")) {
                        if(os.contains("aix")){
                            // aix so package will contains 'aix'
                            if(entityName.contains("aix")){
                                loadJarNative(jarEntry);
                            }
                        } else {
                            // 只处理结尾为 so，且不带 aix 的动态库
                            if(!entityName.contains("aix")){
                                loadJarNative(jarEntry);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getTmpDir() {
        String tmpDir = System.getenv("APP_HOME");
        if(tmpDir == null){
            tmpDir = System.getProperty("app.path");
        }

        if(tmpDir == null){
            tmpDir = System.getProperty("java.io.tmpdir");
        }

        tmpDir = tmpDir == null ? "_temp_" : tmpDir + File.separator + "_temp_";
        return tmpDir;
    }

    private static void loadJarNative(JarEntry jarEntry) {
        File tempDir = new File(getTmpDir());
        String entityName = jarEntry.getName();
        File tempFile = new File(tempDir.getAbsolutePath(), entityName);
        if(!tempFile.getParentFile().exists()){
            tempFile.getParentFile().mkdirs();
        }

        if (tempFile.exists()) {
            tempFile.delete();
        }
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        BufferedInputStream reader = null;
        FileOutputStream writer = null;
        try {
            InputStream in = NativeInit.class.getResourceAsStream(entityName);
            if (in == null) {
                in = NativeInit.class.getResourceAsStream("/" + entityName);
                if (null == in) {
                    return;
                }
            }
            reader = new BufferedInputStream(in);
            writer = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            while (reader.read(buffer) > 0) {
                writer.write(buffer);
                buffer = new byte[1024];
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }

        try {
            System.load(tempFile.getPath());
            tempFile.delete();
            tempDir.delete();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }
}
