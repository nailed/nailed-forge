package jk_5.nailed.map.script.api;

import com.google.common.collect.Maps;
import jk_5.nailed.map.script.*;

import java.io.IOException;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class FileSystemApi implements ILuaAPI {

    private IAPIEnvironment environment;
    private FileSystem fileSystem;

    public FileSystemApi(IAPIEnvironment environment){
        this.environment = environment;
        this.fileSystem = null;
    }

    @Override
    public String[] getNames(){
        return new String[]{"fs"};
    }

    @Override
    public void startup(){
        this.fileSystem = this.environment.getFileSystem();
    }

    @Override
    public void advance(double _dt){
    }

    @Override
    public void shutdown(){
        this.fileSystem = null;
    }

    @Override
    public String[] getMethodNames(){
        return new String[]{
                "list",
                "combine",
                "getName",
                "getSize",
                "exists",
                "isDir",
                "isReadOnly",
                "makeDir",
                "move",
                "copy",
                "delete",
                "open",
                "getDrive",
                "getFreeSpace",
                "find"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] args) throws Exception{
        switch(method){
            case 0: //list
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path = (String) args[0];
                try{
                    String[] results = this.fileSystem.list(path);
                    Map<Integer, String> table = Maps.newHashMap();
                    for(int i = 0; i < results.length; i++){
                        table.put(i + 1, results[i]);
                    }
                    return new Object[]{table};
                }catch(FileSystemException e){
                    throw new Exception(e.getMessage());
                }
            case 1:
                if(args.length != 2 || args[0] == null || !(args[0] instanceof String) || args[1] == null || !(args[1] instanceof String)){
                    throw new Exception("Expected 2 string arguments");
                }
                String pathA = (String) args[0];
                String pathB = (String) args[1];
                return new Object[]{this.fileSystem.combine(pathA, pathB)};
            case 2:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path1 = (String) args[0];
                return new Object[]{FileSystem.getName(path1)};
            case 3:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path2 = (String) args[0];
                try{
                    return new Object[]{this.fileSystem.getSize(path2)};
                }catch(FileSystemException e){
                    throw new Exception(e.getMessage());
                }
            case 4:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path3 = (String) args[0];
                try{
                    return new Object[]{this.fileSystem.exists(path3)};
                }catch(FileSystemException e){
                    return new Object[]{false};
                }
            case 5:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path4 = (String) args[0];
                try{
                    return new Object[]{this.fileSystem.isDir(path4)};
                }catch(FileSystemException e){
                    return new Object[]{false};
                }
            case 6:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path5 = (String) args[0];
                try{
                    return new Object[]{this.fileSystem.isReadOnly(path5)};
                }catch(FileSystemException e){
                    return new Object[]{false};
                }
            case 7:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path6 = (String) args[0];
                try{
                    this.fileSystem.makeDir(path6);
                    return null;
                }catch(FileSystemException e){
                    throw new Exception(e.getMessage());
                }
            case 8:
                if(args.length != 2 || args[0] == null || !(args[0] instanceof String) || args[1] == null || !(args[1] instanceof String)){
                    throw new Exception("Expected 2 string arguments");
                }
                String path7 = (String) args[0];
                String dest = (String) args[1];
                try{
                    this.fileSystem.move(path7, dest);
                    return null;
                }catch(Exception e){
                    throw new Exception(e.getMessage());
                }
            case 9:
                if(args.length != 2 || args[0] == null || !(args[0] instanceof String) || args[1] == null || !(args[1] instanceof String)){
                    throw new Exception("Expected 2 string arguments");
                }
                String path8 = (String) args[0];
                String dest1 = (String) args[1];
                try{
                    this.fileSystem.copy(path8, dest1);
                    return null;
                }catch(Exception e){
                    throw new Exception(e.getMessage());
                }
            case 10:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path9 = (String) args[0];
                try{
                    this.fileSystem.delete(path9);
                    return null;
                }catch(FileSystemException e){
                    throw new Exception(e.getMessage());
                }
            case 11:
                if(args.length != 2 || args[0] == null || !(args[0] instanceof String) || args[1] == null || !(args[1] instanceof String)){
                    throw new Exception("Expected 2 string arguments");
                }
                String path10 = (String) args[0];
                String mode = (String) args[1];
                try{
                    if(mode.equals("r")){
                        IMountedFileNormal reader = this.fileSystem.openForRead(path10);
                        return wrapBufferedReader(reader);
                    }else if(mode.equals("w")){
                        IMountedFileNormal writer = this.fileSystem.openForWrite(path10, false);
                        return wrapBufferedWriter(writer);
                    }else if(mode.equals("a")){
                        IMountedFileNormal writer = this.fileSystem.openForWrite(path10, true);
                        return wrapBufferedWriter(writer);
                    }else if(mode.equals("rb")){
                        IMountedFileBinary reader = this.fileSystem.openForBinaryRead(path10);
                        return wrapInputStream(reader);
                    }else if(mode.equals("wb")){
                        IMountedFileBinary writer = this.fileSystem.openForBinaryWrite(path10, false);
                        return wrapOutputStream(writer);
                    }else if(mode.equals("ab")){
                        IMountedFileBinary writer = this.fileSystem.openForBinaryWrite(path10, true);
                        return wrapOutputStream(writer);
                    }
                    throw new Exception("Unsupported mode");
                }catch(FileSystemException e){
                    return null;
                }
            case 12:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path11 = (String) args[0];
                try{
                    if(!this.fileSystem.exists(path11)){
                        return null;
                    }
                    return new Object[]{this.fileSystem.getMountLabel(path11)};
                }catch(FileSystemException e){
                    throw new Exception(e.getMessage());
                }
            case 13:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path12 = (String) args[0];
                try{
                    long freeSpace = this.fileSystem.getFreeSpace(path12);
                    if(freeSpace >= 0L){
                        return new Object[]{freeSpace};
                    }
                    return new Object[]{"unlimited"};
                }catch(FileSystemException e){
                    throw new Exception(e.getMessage());
                }
            case 14:
                if(args.length != 1 || args[0] == null || !(args[0] instanceof String)){
                    throw new Exception("Expected 1 string argument");
                }
                String path13 = (String) args[0];
                try{
                    String[] results = this.fileSystem.find(path13);
                    Map<Integer, String> table = Maps.newHashMap();
                    for(int i = 0; i < results.length; i++){
                        table.put(i + 1, results[i]);
                    }
                    return new Object[]{table};
                }catch(FileSystemException e){
                    throw new Exception(e.getMessage());
                }
        }
        return null;
    }

    private static Object[] wrapBufferedReader(final IMountedFileNormal reader){
        return new Object[]{new ILuaObject() {

            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "readLine",
                        "readAll",
                        "close"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] args) throws Exception{
                switch(method){
                    case 0:
                        try{
                            String line = reader.readLine();
                            if(line != null){
                                return new Object[]{line};
                            }
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                    case 1:
                        try{
                            StringBuilder result = new StringBuilder("");
                            String line = reader.readLine();
                            while(line != null){
                                result.append(line);
                                line = reader.readLine();
                                if(line != null){
                                    result.append("\n");
                                }
                            }
                            return new Object[]{result.toString()};
                        }catch(IOException e){
                            return null;
                        }
                    case 2:
                        try{
                            reader.close();
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                }
                return null;
            }
        }
        };
    }

    private static Object[] wrapBufferedWriter(final IMountedFileNormal writer){
        return new Object[]{new ILuaObject() {

            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "write",
                        "writeLine",
                        "close",
                        "flush"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] args) throws Exception{
                switch(method){
                    case 0:
                        String text;
                        if(args.length > 0 && args[0] != null){
                            text = args[0].toString();
                        }else{
                            text = "";
                        }
                        try{
                            writer.write(text, 0, text.length(), false);
                            return null;
                        }catch(IOException e){
                            throw new Exception(e.getMessage());
                        }
                    case 1:
                        String text1;
                        if(args.length > 0 && args[0] != null){
                            text1 = args[0].toString();
                        }else{
                            text1 = "";
                        }
                        try{
                            writer.write(text1, 0, text1.length(), true);
                            return null;
                        }catch(IOException e){
                            throw new Exception(e.getMessage());
                        }
                    case 2:
                        try{
                            writer.close();
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                    case 3:
                        try{
                            writer.flush();
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                }
                return null;
            }
        }
        };
    }

    private static Object[] wrapInputStream(final IMountedFileBinary reader){
        return new Object[]{new ILuaObject() {

            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "read","close"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] args) throws Exception {
                switch(method){
                    case 0:
                        try{
                            int b = reader.read();
                            if(b != -1){
                                return new Object[]{b};
                            }
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                    case 1:
                        try{
                            reader.close();
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                }
                return null;
            }
        }};
    }

    private static Object[] wrapOutputStream(final IMountedFileBinary writer){
        return new Object[]{new ILuaObject() {

            @Override
            public String[] getMethodNames(){
                return new String[]{"write", "close", "flush"};
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] args) throws Exception {
                switch(method){
                    case 0:
                        try{
                            if(args.length > 0 && args[0] instanceof Double){
                                int number = ((Double) args[0]).intValue();
                                writer.write(number);
                            }
                            return null;
                        }catch(IOException e){
                            throw new Exception(e.getMessage());
                        }
                    case 1:
                        try{
                            writer.close();
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                    case 2:
                        try{
                            writer.flush();
                            return null;
                        }catch(IOException e){
                            return null;
                        }
                }
                return null;
            }
        }};
    }
}
