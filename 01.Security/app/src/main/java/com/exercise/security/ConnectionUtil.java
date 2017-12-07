package com.exercise.security;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author Philip
 * @since 2017-12-06
 */

public class ConnectionUtil {

    Context mContext;

    private static ConnectionUtil instance;

    public static void init(Context context) {
        if (instance == null) {
            instance = new ConnectionUtil(context);
        }
    }

    public static ConnectionUtil getInstance() {
        return instance;
    }

    private ConnectionUtil(Context context) {
        mContext = context.getApplicationContext();
    }


    @WorkerThread
    public String badCert() {
        try {
            URL url = new URL("https://superfish.badssl.com/");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            in.read();
            in.close();

            return "Success";
        } catch (Exception e) {
            Log.d("ConnectionUtil", e.toString(), e);
            return "Error: " + e.getMessage();
        }
    }

    @WorkerThread
    public String badCertDebug() {
        debugTrustAll();

        try {
            URL url = new URL("https://superfish.badssl.com/");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            in.read();
            in.close();

            return "Success";
        } catch (Exception e) {
            Log.d("ConnectionUtil", e.toString(), e);
            return "Error: " + e.getMessage();
        }
    }

    @WorkerThread
    public String goodCert() {

        try {
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = mContext.getAssets().open("ca-superfish.crt");
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL("https://superfish.badssl.com/");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            InputStream in = urlConnection.getInputStream();
            in.read();
            in.close();

            return "Success";

        } catch (Exception e) {
            Log.d("ConnectionUtil", e.toString(), e);
            return "Error: " + e.getMessage();
        }
    }


    @WorkerThread
    public String verifyReCaptcha(String token) {

        String secretKey = mContext.getString(R.string.SECRET_KEY);

        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "secret=" + secretKey + "&response=" + token;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            return "Success: " + response.toString();

        } catch (Exception e) {
            Log.d("ConnectionUtil", e.toString(), e);
            return "Error: " + e.getMessage();
        }

    }


    /**
     * Trust all certificates.
     * WARNING! This should be used for dev purposes only!
     */
    private void debugTrustAll() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }
}
