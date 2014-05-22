package jk_5.nailed.map.script;

import java.io.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMountedFileBinary extends IMountedFile {

    int read() throws IOException;
    void write(int data) throws IOException;
    void close() throws IOException;
    void flush() throws IOException;
}
