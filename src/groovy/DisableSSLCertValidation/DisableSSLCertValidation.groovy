 import javax.net.ssl.*;   
 import java.security.cert.X509Certificate;  
             
 class x509tm implements X509TrustManager {
     public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null;} 
     public void checkClientTrusted(X509Certificate[] certs, String authType) {}  
     public void checkServerTrusted(X509Certificate[] certs, String authType) {}
 }  
                    
 def trustAllCerts = [new x509tm()] as TrustManager[]

 // Install the all-trusting trust manager  
 def sc = SSLContext.getInstance("SSL") as SSLContext 
 sc.init(null, trustAllCerts, new java.security.SecureRandom())  
 HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
 
 // Create all-trusting host name verifier
 class hnv implements HostnameVerifier {
       public boolean verify(String hostname, SSLSession session) {  
                 return true;  
       }
 }
 
 def allHostsValid = new hnv()
 
 // Install the all-trusting host verifier  
 HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
 
 
 /////////////////////////        
 def ossourl = args[0]
 print ossourl.toURL().text            
             
