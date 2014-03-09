package jk_5.nailed.updater;

import java.security.MessageDigest;
import java.security.cert.Certificate;

/**
 * No description given
 *
 * @author jk-5
 */
public class CertificateHelper {

    private static final String HEXES = "0123456789abcdef";

    public static String getFingerprint(Certificate certificate){
        if(certificate == null){
            return "NO VALID CERTIFICATE FOUND";
        }
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] der = certificate.getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            return hexify(digest);
        }catch(Exception e){
            return null;
        }
    }

    private static String hexify(byte[] chksum){
        final StringBuilder hex = new StringBuilder(2 * chksum.length);
        for(final byte b : chksum){
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
}
