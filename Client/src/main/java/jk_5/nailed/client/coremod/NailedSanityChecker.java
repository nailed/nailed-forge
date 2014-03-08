package jk_5.nailed.client.coremod;

import cpw.mods.fml.common.CertificateHelper;
import cpw.mods.fml.relauncher.IFMLCallHook;
import jk_5.nailed.NailedLog;

import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedSanityChecker implements IFMLCallHook {

    private static final String FINGERPRINT = "87401ecb3314a1a18fb267281b2432975a7e2e84";

    @Override
    public void injectData(Map<String, Object> data){

    }

    @Override
    public Void call() throws Exception{
        CodeSource source = this.getClass().getProtectionDomain().getCodeSource();
        if(source.getLocation().getProtocol().equals("jar")){
            Certificate[] certs = source.getCertificates();
            if(certs == null){
                NailedLog.fatal("No fingerprint was found for Nailed");
                throw new RuntimeException("Nailed is not signed! Please get a new one, or contact jk-5");
            }
            for(Certificate cert : certs){
                String fingerprint = CertificateHelper.getFingerprint(cert);
                if(fingerprint.equals(FINGERPRINT)){
                    NailedLog.fatal("Found valid fingerprint for Nailed. Certificate fingerprint " + fingerprint);
                }else{
                    NailedLog.fatal("Found invalid fingerprint for Nailed. Certificate fingerprint " + fingerprint);
                    throw new RuntimeException("Nailed is not signed! Please get a new one, or contact jk-5");
                }
            }
        }
        return null;
    }
}
