package jk_5.nailed.map.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.annotation.Nonnull;

import jk_5.nailed.api.scripting.IWritableMount;

/**
 * No description given
 *
 * @author jk-5
 */
public class FileMount implements IWritableMount {

    private static int MINIMUM_FILE_SIZE = 512;
    private File rootPath;
    private long capacity;
    private long usedSpace;

    public FileMount(File rootPath, long capacity) {
        this.rootPath = rootPath;
        this.capacity = capacity;
        this.usedSpace = created() ? measureUsedSpace(rootPath) : MINIMUM_FILE_SIZE;
    }

    @Override
    public boolean exists(String path) throws IOException {
        if(!created()){
            return path.length() == 0;
        }
        File file = getRealPath(path);
        return file.exists();
    }

    @Override
    public boolean isDirectory(String path) throws IOException {
        if(!created()){
            return path.length() == 0;
        }
        File file = getRealPath(path);
        return (file.exists()) && (file.isDirectory());
    }

    @Override
    public void list(String path, List<String> contents) throws IOException {
        if(!created()){
            if(path.length() != 0){
                throw new IOException("Not a directory");
            }
        }else{
            File file = getRealPath(path);
            if(file.exists() && file.isDirectory()){
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

    @Override
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

    @Override
    public InputStream openForRead(String path) throws IOException {
        if(created()){
            File file = getRealPath(path);
            if(file.exists() && !file.isDirectory()){
                return new FileInputStream(file);
            }
        }
        throw new IOException("No such file");
    }

    @Override
    public void makeDirectory(String path) throws IOException {
        create();
        File file = getRealPath(path);
        if(file.exists()){
            if(!file.isDirectory()){
                throw new IOException("File exists");
            }
        }else{
            if(getRemainingSpace() < MINIMUM_FILE_SIZE){
                throw new IOException("Out of space");
            }
            boolean success = file.mkdirs();
            if(success){
                this.usedSpace += MINIMUM_FILE_SIZE;
            }else{
                throw new IOException("Access denied");
            }
        }
    }

    @Override
    public void delete(String path) throws IOException {
        if(path.length() == 0){
            throw new IOException("Access denied");
        }
        if(created()){
            File file = getRealPath(path);
            if(file.exists()){
                deleteRecursively(file);
            }
        }
    }

    private void deleteRecursively(File file) throws IOException {
        if(file.isDirectory()){
            String[] children = file.list();
            for(String child : children){
                deleteRecursively(new File(file, child));
            }
        }

        long fileSize = file.isDirectory() ? 0L : file.length();
        boolean success = file.delete();
        if(success){
            this.usedSpace -= Math.max(MINIMUM_FILE_SIZE, fileSize);
        }else{
            throw new IOException("Access denied");
        }
    }

    @Override
    public OutputStream openForWrite(String path) throws IOException {
        create();
        File file = getRealPath(path);
        if(file.exists() && file.isDirectory()){
            throw new IOException("Cannot write to directory");
        }

        if(!file.exists()){
            if(getRemainingSpace() < MINIMUM_FILE_SIZE){
                throw new IOException("Out of space");
            }
            this.usedSpace += MINIMUM_FILE_SIZE;
        }else{
            this.usedSpace -= Math.max(file.length(), MINIMUM_FILE_SIZE);
            this.usedSpace += MINIMUM_FILE_SIZE;
        }
        return new CountingOutputStream(new FileOutputStream(file, false), MINIMUM_FILE_SIZE);
    }

    @Override
    public OutputStream openForAppend(String path) throws IOException {
        if(created()){
            File file = getRealPath(path);
            if(!file.exists()){
                throw new IOException("No such file");
            }
            if(file.isDirectory()){
                throw new IOException("Cannot write to directory");
            }

            return new CountingOutputStream(new FileOutputStream(file, true), Math.max(MINIMUM_FILE_SIZE - file.length(), 0L));
        }
        throw new IOException("No such file");
    }

    @Override
    public long getRemainingSpace() throws IOException {
        return Math.max(this.capacity - this.usedSpace, 0L);
    }

    private File getRealPath(String path) throws IOException {
        return new File(this.rootPath, path);
    }

    private boolean created() {
        return this.rootPath.exists();
    }

    private void create() throws IOException {
        if(!this.rootPath.exists()){
            boolean success = this.rootPath.mkdirs();
            if(!success){
                throw new IOException("Access denied");
            }
        }
    }

    private long measureUsedSpace(File file) {
        if(!file.exists()){
            return 0L;
        }
        if(file.isDirectory()){
            long size = MINIMUM_FILE_SIZE;
            String[] contents = file.list();
            for(String content : contents){
                size += measureUsedSpace(new File(file, content));
            }
            return size;
        }

        return Math.max(file.length(), MINIMUM_FILE_SIZE);
    }

    private class CountingOutputStream extends OutputStream {

        private OutputStream innerStream;
        private long ignoringBytesLeft;

        public CountingOutputStream(OutputStream innerStream, long bytesToIgnore) {
            this.innerStream = innerStream;
            this.ignoringBytesLeft = bytesToIgnore;
        }

        @Override
        public void close() throws IOException {
            this.innerStream.close();
        }

        @Override
        public void flush() throws IOException {
            this.innerStream.flush();
        }

        @Override
        public void write(@Nonnull byte[] b) throws IOException {
            count(b.length);
            this.innerStream.write(b);
        }

        @Override
        public void write(@Nonnull byte[] b, int off, int len) throws IOException {
            count(len);
            this.innerStream.write(b, off, len);
        }

        public void write(int b) throws IOException {
            count(1L);
            this.innerStream.write(b);
        }

        private void count(long n) throws IOException {
            this.ignoringBytesLeft -= n;
            if(this.ignoringBytesLeft < 0L){
                long newBytes = -this.ignoringBytesLeft;
                this.ignoringBytesLeft = 0L;

                long bytesLeft = FileMount.this.capacity - FileMount.this.usedSpace;
                if(newBytes > bytesLeft){
                    throw new IOException("Out of space");
                }

                FileMount.this.usedSpace = newBytes;
            }
        }
    }
}
