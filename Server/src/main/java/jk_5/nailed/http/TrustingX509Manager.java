package jk_5.nailed.http;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * No description given
 *
 * @author jk-5
 */
public final class TrustingX509Manager implements X509TrustManager {

    private static final X509TrustManager instance = new TrustingX509Manager();

    private TrustingX509Manager() {

    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public static X509TrustManager getInstance() {
        return instance;
    }
}
