package com.example.guoyiwei.dk.providers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class HttpsProvider {




    public static String httpsGet(String urlStr) {
        URL url;

        try {
            url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }
            };
            TrustManager[] tm = {xtm};

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tm, null);

            con.setSSLSocketFactory(ctx.getSocketFactory());
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession session) {

                    return true;
                }
            };
            con.setHostnameVerifier(hv);

            BufferedReader read = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String temp;
            StringBuilder re = new StringBuilder();
            while ((temp = read.readLine()) != null) {
                re.append(temp);
            }
            return re.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
