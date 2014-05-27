package jk_5.nailed.map.script;

import java.io.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMountedFileNormal extends IMountedFile {

    String readLine() throws IOException;
    void write(String data, int paramInt1, int paramInt2, boolean append) throws IOException;
    void close() throws IOException;
    void flush() throws IOException;
}
