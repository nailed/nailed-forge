package jk_5.nailed.updater.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jk_5.nailed.updater.FileUtils;
import jk_5.nailed.updater.Updater2;
import jk_5.nailed.updater.json.dependencies.DependencyFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * No description given
 *
 * @author jk-5
 */
public class JsonFactory {

    public static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new EnumAdapterFactory()).registerTypeAdapter(Date.class, new DateAdapter()).registerTypeAdapter(File.class, new FileAdapter()).enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static LocalDependencyFile loadLocalDependencyFile(File file){
        Reader reader = null;
        LocalDependencyFile v = null;
        try{
            reader = new FileReader(file);
            v = gson.fromJson(reader, LocalDependencyFile.class);
        }catch(FileNotFoundException e){
            Updater2.logger.info("Local dependency file does not exist. Creating...");
        }finally{
            FileUtils.close(reader);
        }
        if(v == null){
            v = new LocalDependencyFile();
            v.libraries = new ArrayList<LocalLibrary>();
        }
        return v;
    }

    public static DependencyFile loadRemoteDependencyFile(String url){
        Reader reader = null;
        DependencyFile v = null;
        try{
            reader = new InputStreamReader(new URL(url).openStream());
            v = gson.fromJson(reader, DependencyFile.class);
        }catch(MalformedURLException e){
            Updater2.logger.fatal("Invalid URL format " + url, e);
        }catch(IOException e){
            Updater2.logger.fatal("IOException while reading remote dependency file " + url, e);
        }finally{
            FileUtils.close(reader);
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
            FileUtils.close(writer);
        }
    }
}
