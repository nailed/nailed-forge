package jk_5.nailed.client.util.config;

import java.io.*;

public class ConfigFile extends ConfigTagParent {

    public File file;
    private Reader reader;

    private boolean loading = false;
    private boolean readOnly = false;

    public static final byte[] lineend = new byte[]{0xD, 0xA};

    public ConfigFile(Reader reader){
        try{
        this.reader = reader;
        this.file = null;
        this.loadConfig();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ConfigFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try{
            this.reader = new FileReader(file);
            this.file = file;
            newlinemode = 2;
            loadConfig();
        }catch(FileNotFoundException e){
            throw new RuntimeException(e);
        }
    }

    private void loadConfig() {
        loading = true;
        BufferedReader reader;
        try {
            if(!(this.reader instanceof BufferedReader)) this.reader = new BufferedReader(this.reader);
            reader = (BufferedReader) this.reader;
            while (true) {
                reader.mark(2000);
                String line = reader.readLine();
                if (line != null && line.startsWith("#")) {
                    if (comment == null || comment.equals(""))
                        comment = line.substring(1);
                    else
                        comment = comment + "\n" + line.substring(1);
                } else {
                    reader.reset();
                    break;
                }
            }
            loadChildren(reader);
            if(this.file != null) reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loading = false;
    }

    public ConfigFile setReadOnly(){
        this.readOnly = true;
        return this;
    }

    @Override
    public ConfigFile setComment(String header) {
        super.setComment(header);
        return this;
    }

    @Override
    public ConfigFile setSortMode(int mode) {
        super.setSortMode(mode);
        return this;
    }

    @Override
    public String getNameQualifier() {
        return "";
    }

    public static String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null)
            return line.replace("\t", "");
        return line;
    }

    public static String formatLine(String line) {
        line = line.replace("\t", "");
        if (line.startsWith("#")) {
            return line;
        } else if (line.contains("=")) {
            line = line.substring(0, line.indexOf("=")).replace(" ", "") + line.substring(line.indexOf("="));
            return line;
        } else {
            line = line.replace(" ", "");
            return line;
        }
    }

    public static void writeLine(PrintWriter writer, String line, int tabs) {
        for (int i = 0; i < tabs; i++)
            writer.print('\t');

        writer.println(line);
    }

    @Override
    public void saveConfig() {
        if (loading || readOnly)
            return;

        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        writeComment(writer, 0);
        ConfigFile.writeLine(writer, "", 0);
        saveTagTree(writer, 0, "");
        writer.flush();
        writer.close();
    }

    public boolean isLoading() {
        return loading;
    }
}
