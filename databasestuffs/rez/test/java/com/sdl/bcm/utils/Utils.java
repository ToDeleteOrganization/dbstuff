package com.sdl.bcm.utils;

import java.io.*;
import java.nio.charset.Charset;

public class Utils {

    public static String readFile(String fileName) {
        File file = new File(fileName);
        return new String(readLargeFileBytes(file), Charset.forName("UTF-8"));
    }

    public static String readFile(File file) {
        return new String(readLargeFileBytes(file), Charset.forName("UTF-8"));
    }

    public static byte[] readLargeFileBytes(File file) {
        try {
            InputStream in = new FileInputStream(file);
            long length = file.length();

            if (length > Integer.MAX_VALUE) {
                throw new IOException("File is too large!");
            }
            byte[] bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            in.close();
            return bytes;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static void writeFile(String fileName, String content) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
