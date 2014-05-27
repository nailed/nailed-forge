package jk_5.nailed.map.script;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import com.google.common.collect.*;

import jk_5.nailed.api.scripting.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class JarMount implements IMount {

    private ZipFile zipFile;
    private FileInZip root;
    private String rootPath;

    public JarMount(File jarFile, String subPath) throws IOException {
        if(!jarFile.exists() || jarFile.isDirectory()){
            throw new FileNotFoundException();
        }

        try{
            this.zipFile = new ZipFile(jarFile);
        }catch(Exception e){
            throw new IOException("Error loading zip file");
        }

        if(this.zipFile.getEntry(subPath) == null){
            this.zipFile.close();
            throw new IOException("Zip does not contain path");
        }

        Enumeration zipEntries = this.zipFile.entries();
        while(zipEntries.hasMoreElements()){
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();
            String entryName = entry.getName();
            if(entryName.startsWith(subPath)){
                entryName = FileSystem.toLocal(entryName, subPath);
                if(this.root == null){
                    if("".equals(entryName)){
                        this.root = new FileInZip(entryName, entry.isDirectory(), entry.getSize());
                        this.rootPath = subPath;
                        if(!this.root.isDirectory()){
                            break;
                        }

                    }
                }else{
                    FileInZip parent = this.root.getParent(entryName);
                    if(parent != null){
                        parent.insertChild(new FileInZip(entryName, entry.isDirectory(), entry.getSize()));
                    }
                }
            }
        }
    }

    public boolean exists(String path) throws IOException {
        FileInZip file = this.root.getFile(path);
        if(file != null){
            return true;
        }
        return false;
    }

    public boolean isDirectory(String path) throws IOException {
        FileInZip file = this.root.getFile(path);
        if(file != null){
            return file.isDirectory();
        }
        return false;
    }

    public void list(String path, List<String> contents) throws IOException {
        FileInZip file = this.root.getFile(path);
        if((file != null) && (file.isDirectory())){
            file.list(contents);
        }else{
            throw new IOException("Not a directory");
        }
    }

    public long getSize(String path) throws IOException {
        FileInZip file = this.root.getFile(path);
        if(file != null){
            return file.getSize();
        }
        throw new IOException("No such file");
    }

    public InputStream openForRead(String path) throws IOException {
        FileInZip file = this.root.getFile(path);
        if((file != null) && (!file.isDirectory())){
            try{
                String fullPath = this.rootPath;
                if(path.length() > 0){
                    fullPath = fullPath + "/" + path;
                }
                ZipEntry entry = this.zipFile.getEntry(fullPath);
                if(entry != null){
                    return this.zipFile.getInputStream(entry);
                }
            }catch(Exception e){
            }
        }

        throw new IOException("No such file");
    }

    private class FileInZip {

        private String path;
        private boolean directory;
        private long size;
        private Map<String, FileInZip> children = Maps.newLinkedHashMap();

        public FileInZip(String path, boolean directory, long size) {
            this.path = path;
            this.directory = directory;
            this.size = this.directory ? 0L : size;
        }

        public String getPath() {
            return this.path;
        }

        public boolean isDirectory() {
            return this.directory;
        }

        public long getSize() {
            return this.size;
        }

        public void list(List<String> contents) {
            for(String child : this.children.keySet()){
                contents.add(child);
            }
        }

        public void insertChild(FileInZip child) {
            String localPath = FileSystem.toLocal(child.getPath(), this.path);
            this.children.put(localPath, child);
        }

        public FileInZip getFile(String path) {
            if(path.equals(this.path)){
                return this;
            }

            String localPath = FileSystem.toLocal(path, this.path);
            int slash = localPath.indexOf("/");
            if(slash >= 0){
                localPath = localPath.substring(0, slash);
            }

            FileInZip subFile = (FileInZip) this.children.get(localPath);
            if(subFile != null){
                return subFile.getFile(path);
            }

            return null;
        }

        public FileInZip getParent(String path) {
            if(path.length() == 0){
                return null;
            }

            FileInZip file = getFile(FileSystem.getDirectory(path));
            if(file.isDirectory()){
                return file;
            }
            return null;
        }
    }
}
