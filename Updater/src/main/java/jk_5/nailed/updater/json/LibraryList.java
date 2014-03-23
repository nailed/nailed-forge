package jk_5.nailed.updater.json;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jk_5.nailed.updater.UpdatingTweaker;
import jk_5.nailed.updater.json.serialization.EnumAdapterFactory;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class LibraryList {

    public static Gson serializer = new GsonBuilder().registerTypeAdapterFactory(new EnumAdapterFactory()).setPrettyPrinting().create();

    public String versionName;
    public List<Library> libraries = Lists.newArrayList();

    public void writeToFile(File file){
        Writer writer = null;
        try{
            writer = new FileWriter(file);
            serializer.toJson(this, writer);
        }catch(Exception e){
            //NOOP
        }finally{
            IOUtils.closeQuietly(writer);
        }
    }

    public static LibraryList readFromFile(File file){
        Reader reader = null;
        LibraryList ret;
        try{
            if(file.exists()){
                reader = new FileReader(file);
                ret = serializer.fromJson(reader, LibraryList.class);
            }else{
                ret = new LibraryList();
            }
        }catch(Exception e){
            UpdatingTweaker.logger.error("Exception while reading local version data", e);
            ret = new LibraryList();
        }finally{
            IOUtils.closeQuietly(reader);
        }
        return ret;
    }

    public static LibraryList readFromUrl(String url){
        Reader reader = null;
        LibraryList ret;
        try{
            URL u = new URL(url);
            reader = new InputStreamReader(u.openStream());
            ret = serializer.fromJson(reader, LibraryList.class);
        }catch(Exception e){
            UpdatingTweaker.logger.error("Exception while reading remote version data", e);
            ret = new LibraryList();
        }finally{
            IOUtils.closeQuietly(reader);
        }
        return ret;
    }
}
