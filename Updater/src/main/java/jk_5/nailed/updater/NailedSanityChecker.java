package jk_5.nailed.updater;

import java.security.CodeSource;
import java.security.cert.Certificate;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedSanityChecker {

    private static final String FINGERPRINT = "87401ecb3314a1a18fb267281b2432975a7e2e84";

    public static void check() {
        CodeSource source = NailedSanityChecker.class.getProtectionDomain().getCodeSource();
        if(source.getLocation().getProtocol().equals("jar")){
            Certificate[] certs = source.getCertificates();
            if(certs == null){
                UpdatingTweaker.logger.fatal("No fingerprint was found for Nailed");
                throw new RuntimeException("Nailed is not signed! Please get a new one, or contact jk-5");
            }
            for(Certificate cert : certs){
                String fingerprint = CertificateHelper.getFingerprint(cert);
                if(fingerprint.equals(FINGERPRINT)){
                    UpdatingTweaker.logger.info("Found valid fingerprint for Nailed. Certificate fingerprint " + fingerprint);
                }else{
                    UpdatingTweaker.logger.fatal("Found invalid fingerprint for Nailed. Certificate fingerprint " + fingerprint);
                    throw new RuntimeException("Nailed is not signed! Please get a new one, or contact jk-5");
                }
            }
        }
    }
}
