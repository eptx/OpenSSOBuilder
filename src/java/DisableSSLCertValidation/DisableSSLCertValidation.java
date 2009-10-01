/*
This class is used to disable SSL Cert Validation. It is based on http://www.nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/ (Svetln Nakov).
Would like to convert this to groovy but not sure how to deal with inner classes yet.

Usage:

import DisableSSLCertValidation.*;

DisableSSLCertValidation.disable()

def ssourl = 'https://sso-server.somewhere/opensso/identity/authenticate?username=amadmin&password=mypassword'
print ssourl.toURL().text



*/

package DisableSSLCertValidation;

import  java.security.cert.*;
import javax.net.ssl.*;

public class DisableSSLCertValidation {
	
	public static void disable(){
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
            }
            public void checkClientTrusted(
														 java.security.cert.X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(
														 java.security.cert.X509Certificate[] certs, String authType) {
            }
			}
		};
		
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			System.out.println("All certs will be ignored!");
		} catch (Exception e) { 
			System.out.println(e.getMessage());
		}
		
		// Create all-trusting host name verifier  
		HostnameVerifier allHostsValid = new HostnameVerifier() {  
			public boolean verify(String hostname, SSLSession session) {  
				return true;  
			}  
		};  
		
		// Install the all-trusting host verifier  
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);  
	}
}
