 SSLCertValidation.disable()      
 def ossourl = args[0]
 def threadcount = 10
 def accesscount = 100
 
 for (t in 1..threadcount){
 	Thread.start {
 		for (a in 1..accesscount) {
 			print ossourl.toURL().text  
 		}
 	}
 }

println "${threadcount * accesscount} requests."
           
             
