package jk_5.nailed.updater;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * No description given
 *
 * @author jk-5
 */
public class FileUtils {
    private static final int EOF = -1;

    public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout) throws IOException{
        URLConnection connection = source.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        InputStream input = connection.getInputStream();
        copyInputStreamToFile(input, destination);
    }

    public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        try {
            FileOutputStream output = openOutputStream(destination, false);
            try {
                copy(source, output);
                output.close(); // don't swallow close Exception if copy completes normally
            } finally {
                close(output);
            }
        } finally {
            close(source);
        }
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static void close(Closeable cl){
        if(cl == null) return;
        try{
            cl.close();
        }catch(IOException e){
            //Ignore it
        }
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
