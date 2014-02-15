package jk_5.nailed.map.script;

import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMountedFileNormal extends IMountedFile {

    public String readLine() throws IOException;
    public void write(String data, int paramInt1, int paramInt2, boolean append) throws IOException;
    public void close() throws IOException;
    public void flush() throws IOException;
}
