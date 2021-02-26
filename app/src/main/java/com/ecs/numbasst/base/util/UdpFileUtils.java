package com.ecs.numbasst.base.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zw
 * @time 2021/1/26
 * @description
 */
public class UdpFileUtils {

    private OutputStream out;
    File file;
    byte[] buff = new byte[1024];
    String filePath = "";
    public UdpFileUtils(String fileName) {
        if (fileName != null && !fileName.equals("")) {

            String name = fileName.trim();
            String dirs = DataKeeper.downloadPath + name + "/";

            Log.d("zwcc","创建文件夹 dirs = " +dirs);
            file = new File(DataKeeper.downloadPath);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(dirs);
            if (!file.exists()) {
                file.mkdir();
            }

            String path = dirs + name + ".dat";
            filePath = path;
            file = new File(path);
            Log.d("zwcc","创建文件 path = " +path);
            try {
                if (file.exists()) {
                    file.delete();
                }
                if (file.createNewFile()) {
                    out = new FileOutputStream(file);
                } else {
                    out = new FileOutputStream(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String getPath(){
        return filePath;
    }


    public void writeBytesToFile(byte[] content) throws IOException {
        InputStream is = new ByteArrayInputStream(content);
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        is.close();
    }

    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
