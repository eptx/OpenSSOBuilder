 import javax.net.ssl.*;   
 import java.security.cert.X509Certificate;  
    
 class SSLCertValidation { 
 
     public static disable() {
     	 // implement X509TrustManager interface
      	 def X509TrustManagerImpl = [
 		 	getAcceptedIssuers: {return null},
 		 	checkClientTrusted: {certs, authType -> },
 			checkServerTrusted: {certs, authType -> } 	
 		 ]	
 		 // create instance of X509TrustManager implementation
     	 def x509tm = X509TrustManagerImpl as X509TrustManager
     	 // add X509TrustManager implementation instance to an array of TrustManager
         def trustAllCerts = [x509tm] as TrustManager[]
        
         // Install the all-trusting trust manager  
         def sc = SSLContext.getInstance("SSL") as SSLContext 
         sc.init(null, trustAllCerts, new java.security.SecureRandom())  
         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
         
         // implement HostnameVerifier interface
         def HostnameVerifierImpl = [verify: {hostname, session -> true}]
         // create instance of HostnameVerifier implementation
         def allHostsValid = HostnameVerifierImpl as HostnameVerifier
         
         // Install the all-trusting host verifier  
         HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
     }
 }