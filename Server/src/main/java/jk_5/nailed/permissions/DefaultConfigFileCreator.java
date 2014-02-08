package jk_5.nailed.permissions;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultConfigFileCreator {

    public static void createGroupConfig(File file){
        PrintWriter writer = null;
        try{
            writer = new PrintWriter(new FileWriter(file));
            writer.println("#Group permissions");
            writer.println("#Note: Inherited group specified last will override values from the previous groups,");
            writer.println("#      so this is the highest priority. Lowest priority up.");
            writer.println();
            writer.println("Admin {");
            writer.println("    Prefix = \"@\"");
            writer.println("    Suffix = \"\"");
            writer.println("    Default = false");
            writer.println("    Permissions {");
            writer.println("        ");
            writer.println("    }");
            writer.println("}");
        }catch(Exception e){

        }finally{
            IOUtils.closeQuietly(writer);
        }
    }
}
