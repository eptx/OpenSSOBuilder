import groovyx.net.http.HTTPBuilder

class OpenssoHelper { 
    def opensso 
    def token
    //??? make rest call here to osso to get cookie name
    def cookieName = "iPlanetDirectoryPro"
    def OpenssoHelper(url, username, password) {
    	// disable ssl cert validation by default
    	OpenssoHelper(true, url, username, password)
    }
    
    def OpenssoHelper(sslDisable, url, username, password) {
    	if(sslDisable) {
    		 SSLCertValidation.disable() 
    	}
        this.opensso = new HTTPBuilder(url) 
        login(username, password) 
    }

    def login(username, password) { 
        def rawResults = opensso.get(path:"identity/authenticate", query: [username: username, password:password])
        def results = parseResults(rawResults)
        this.token = results['token.id'] 
    }
    
def createAgent(realm,agentname,agenttype,atts) { 
	//???missing atts [attributevalues: name1=value], attributevalues: name2=value,...]
    def query = [cmd: "create-agent", realm: realm, agentname: agentname, agentype: agenttype, submit: ""] 
    println opensso.post(path: "ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
}

def createRealm(realm) { 
    def query = [cmd: "create-realm", realm: realm, submit: ""] 
    println opensso.post(path: "ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
}

def createPolicy(realm,policy) { 
    //remove existing policies
    def policyNames = parsePolicyNames(policy) 
    //println "Deleting ${policyNames}."
    deletePolicies(realm,policyNames)
    //add new policies, data must be URL encoded
    def policyEnc = URLEncoder.encode(policy)    
    def query = [cmd: "create-policies", realm: realm, xmlfile: policyEnc, submit: ""] 
    println opensso.post(path: "ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
}

def deletePolicies(realm,policies) { 
    //more work here, uribuilder doesn't support multi value params yet (issue filed), so multiple calls to delete policies
    for (policy in policies) {
    try{
        def query = [cmd: "delete-policies", realm: realm, policynames: policy , submit: ""] 
        println opensso.post(path: "ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
    } catch (Error e){}
    }
}

def removeRealm(realm) { 
    def query = [cmd: "delete-realm", realm: realm, submit: ""] 
    println opensso.post(path: "/ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
}

def parsePolicyNames(policyXml){
	///XmlSlurper doesn't seem to handle DOCTYPE tags well so remove it
    def justxml =  policyXml.replace('''<!DOCTYPE Policies PUBLIC "-//Sun Java System Access Manager 7.1 2006Q3 Admin CLI DTD//EN" "jar://com/sun/identity/policy/policyAdmin.dtd">''','')
    def policies = new XmlSlurper().parseText(justxml)
    def policyNames =  policies.Policy.@name
    return  policyNames
}

def parseResults(results) { 
    def properties = new Properties() 
    properties.load results 
    return properties 
} 
} 

///////////////////////////
 def loadPolicies (ossoURL,ossoUser,ossoPwd,policyURL){
    def opensso = new OpenssoHelper(ossoURL, ossoUser,ossoPwd) 
    def policyXml = policyURL.toURL().text
    opensso.createPolicy("/",policyXml)
}

//loadPolicies('http://an-opensso-server:9080/opensso/', 'username','password','http://url-to-policies-provisioned-from-somewhere')

//load policies into server from some URL
loadPolicies(this.args[0],this.args[1],this.args[2],this.args[3])