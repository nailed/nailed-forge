package jk_5.nailed.map.script;

import java.io.*;
import java.util.*;

import jk_5.nailed.api.scripting.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class ReadOnlyMount implements IMount {

    private static int MINIMUM_FILE_SIZE = 512;
    private File rootPath;

    public ReadOnlyMount(File rootPath) {
        this.rootPath = rootPath;
    }

    private File getRealPath(String path) throws IOException {
        return new File(this.rootPath, path);
    }

    private boolean created() {
        return this.rootPath.exists();
    }

    public boolean exists(String path) throws IOException {
        if(!created()){
            return path.length() == 0;
        }

        File file = getRealPath(path);
        return file.exists();
    }

    public boolean isDirectory(String path) throws IOException {
        if(!created()){
            return path.length() == 0;
        }
        File file = getRealPath(path);
        return (file.exists()) && (file.isDirectory());
    }

    public void list(String path, List<String> contents) throws IOException {
        if(!created()){
            if(path.length() != 0){
                throw new IOException("Not a directory");
            }
        }else{
            File file = getRealPath(path);
            if((file.exists()) && (file.isDirectory())){
                String[] paths = file.list();
                for(String subPath : paths){
                    if(new File(file, subPath).exists()){
                        contents.add(subPath);
                    }
                }
            }else{
                throw new IOException("Not a directory");
            }
        }
    }

    public long getSize(String path) throws IOException {
        if(!created()){
            if(path.length() == 0){
                return 0L;
            }
        }else{
            File file = getRealPath(path);
            if(file.exists()){
                if(file.isDirectory()){
                    return 0L;
                }

                return file.length();
            }
        }

        throw new IOException("No such file");
    }

    public InputStream openForRead(String path) throws IOException {
        if(created()){
            File file = getRealPath(path);
            if((file.exists()) && (!file.isDirectory())){
                return new FileInputStream(file);
            }
        }
        throw new IOException("No such file");
    }
}
