package jk_5.nailed.crashreporter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLModDisabledEvent;

import java.io.File;
import java.net.URISyntaxException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class ReporterModContainer extends DummyModContainer {

    public ReporterModContainer(){
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "CrashReporter";
        meta.name = "CrashReporter";
        meta.version = "0.1";
        meta.authorList = Arrays.asList("jk-5");
    }

    @Override
    public Certificate getSigningCertificate(){
        Certificate[] certs = this.getClass().getProtectionDomain().getCodeSource().getCertificates();
        return certs != null ? certs[0] : null;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller){
        bus.register(this);
        return true;
    }

    @Subscribe
    public void onDisable(FMLModDisabledEvent event){
        CrashReporter.getLogger().info("-----");
        CrashReporter.getLogger().info("Disabling CrashReporter...");
        CrashReporter.getLogger().info("NOTE: This is not supported yet");
        CrashReporter.getLogger().info("-----");
    }

    @Override
    public Disableable canBeDisabled(){
        return Disableable.YES;
    }

    @Override
    public List<String> getOwnedPackages(){
        return Arrays.asList("jk_5.nailed.crashreporter", "jk_5.nailed.crashreporter.notificationhandlers", "jk_5.nailed.crashreporter.pasteproviders", "jk_5.nailed.crashreporter.transformer", "jk_5.nailed.crashreporter.transformer.asm");
    }

    @Override
    public File getSource(){
        if(Loader.coremodLocation == null){
            File f;
            try{
                f = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            }catch(URISyntaxException e){
                f = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            }
            f = f.getParentFile().getParentFile().getParentFile().getParentFile();
            return f;
        }else{
            return Loader.coremodLocation;
        }
    }

    @Override
    public Class<?> getCustomResourcePackClass(){
        if(this.getSource() == null){
            return FMLFolderResourcePack.class;
        }else{
            if(this.getSource().isDirectory()){
                return FMLFolderResourcePack.class;
            }else{
                return FMLFileResourcePack.class;
            }
        }
    }
}
