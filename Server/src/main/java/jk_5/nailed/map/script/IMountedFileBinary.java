package jk_5.nailed.map.script;

import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMountedFileBinary extends IMountedFile {

    public int read() throws IOException;
    public void write(int data) throws IOException;
    public void close() throws IOException;
    public void flush() throws IOException;
}
