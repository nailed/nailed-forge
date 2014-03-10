package jk_5.nailed.updater.oldjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jk_5.nailed.updater.Updater2;
import jk_5.nailed.updater.json.serialization.EnumAdapterFactory;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * No description given
 *
 * @author jk-5
 */
public class JsonFactory {

    private static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new EnumAdapterFactory()).registerTypeAdapter(Date.class, new DateAdapter()).registerTypeAdapter(File.class, new FileAdapter()).enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static LocalDependencyFile loadLocalDependencyFile(File file){
        Reader reader = null;
        LocalDependencyFile v = null;
        try{
            reader = new FileReader(file);
            v = gson.fromJson(reader, LocalDependencyFile.class);
        }catch(IOException e){
            Updater2.logger.fatal("IOException while reading local dependency file " + file, e);
        }finally{
            IOUtils.closeQuietly(reader);
        }
        return v;
    }

    public static RemoteDependencyFile loadRemoteDependencyFile(String url){
        Reader reader = null;
        RemoteDependencyFile v = null;
        try{
            reader = new InputStreamReader(new URL(url).openStream());
            v = gson.fromJson(reader, RemoteDependencyFile.class);
        }catch(MalformedURLException e){
            Updater2.logger.fatal("Invalid URL format " + url, e);
        }catch(IOException e){
            Updater2.logger.fatal("IOException while reading remote dependency file " + url, e);
        }finally{
            IOUtils.closeQuietly(reader);
        }
        return v;
    }

    public static void writeLocalDependencyFile(LocalDependencyFile depFile, File file){
        Writer writer = null;
        try{
            writer = new FileWriter(file);
            gson.toJson(depFile, writer);
        }catch(IOException e){
            Updater2.logger.fatal("IOException while writing local dependency file " + file, e);
        }finally{
            IOUtils.closeQuietly(writer);
        }
    }
}
