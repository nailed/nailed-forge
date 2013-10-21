package jk_5.nailed.map;

import com.google.common.collect.Lists;
import jk_5.nailed.NailedLog;
import lombok.Getter;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapLoader {

    private static final MapLoader INSTANCE = new MapLoader();
    @Getter private static final File mappackFolder = new File("mappacks");
    @Getter private static final File mapsFolder = new File("maps");

    private final List<Map> maps = Lists.newArrayList();
    private final List<Mappack> mappacks = Lists.newArrayList();

    public static MapLoader instance(){
        return INSTANCE;
    }

    public void loadMappacks(){
        NailedLog.info("Loading mappacks...");
        this.mappacks.clear();
        if(!mappackFolder.exists()) mappackFolder.mkdirs();
        for(File file : mappackFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mappack");
            }
        })){
            try{
                Mappack pack = Mappack.create(file);
                this.mappacks.add(pack);
            }catch (MappackInitializationException e){
                NailedLog.severe(e, "Error while loading mappack " + file.getName() + ", skipping it!");
            }
        }
        NailedLog.info("Successfully loaded %d mappacks!", this.mappacks.size());
    }

    public File unzipMapFromMapPack(File mapPack, File destDir){
        try{
            ZipFile zipFile = new ZipFile(mapPack);
            Enumeration e = zipFile.entries();
            File worldDir = null;
            while(e.hasMoreElements()){
                ZipEntry entry = (ZipEntry)e.nextElement();
                if(entry.getName().equals("mappack.cfg")) continue;
                if(entry.getName().equals("gameinstructions.cfg")) continue;
                if(entry.getName().contains("##MCEDIT.TEMP##")) continue;
                if(entry.getName().startsWith("__MACOSX/")) continue;
                File destinationFilePath = new File(destDir.getParentFile(), entry.getName());
                destinationFilePath.getParentFile().mkdirs();
                if(!entry.isDirectory()){
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                    int b;
                    byte buffer[] = new byte[1024];
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                }else if(entry.getName().equals("world/")){
                    worldDir = destinationFilePath;
                }
            }
            if(worldDir == null){
                System.err.println("Invalid or corrupt mappack file");
                System.exit(1);
            }
            worldDir.renameTo(destDir);
            return destDir;
        }catch(IOException ioe){

        }
        return null;
    }

    public Mappack getMappack(String internalName){
        for(Mappack pack : this.mappacks){
            if(pack.getInternalName().equals(internalName)){
                return pack;
            }
        }
        return null;
    }

    public void addMap(Map map){
        this.maps.add(map);
    }

    public Map getMap(int id){
        for(Map map : this.maps){
            if(map.getID() == id){
                return map;
            }
        }
        return null;
    }
}
