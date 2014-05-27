package jk_5.nailed.ipc.filestore;

import java.io.*;
import java.util.*;

import jk_5.nailed.api.scripting.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class FilestoreMount implements IMount {

    private final MappackFilestore filestore;

    public FilestoreMount(MappackFilestore filestore) {
        this.filestore = filestore;
    }

    @Override
    public boolean exists(String path) throws IOException {
        return path.length() == 0 || filestore.paths.containsKey(path);
    }

    @Override
    public boolean isDirectory(String path) throws IOException {
        if(path.endsWith("/") || path.length() == 0 || path.length() == 1){
            return true;
        }
        for(String p : filestore.paths.keySet()){
            if(p.startsWith(path + "/")){
                return true;
            }
        }
        return false;
    }

    @Override
    public void list(String path, List<String> contents) throws IOException {
        for(MappackFile file : filestore.files){
            if(file.path.startsWith(path)){
                contents.add(file.path);
            }
        }
    }

    @Override
    public long getSize(String path) throws IOException {
        return filestore.paths.containsKey(path) ? filestore.paths.get(path).size : -1;
    }

    @Override
    public InputStream openForRead(String path) throws IOException {
        if(filestore.paths.containsKey(path)){
            MappackFile f = filestore.paths.get(path);
            File file = f.getLocation();
            if(file.exists() && !file.isDirectory()){
                return new FileInputStream(file);
            }
        }
        throw new IOException("No such file");
    }
}
