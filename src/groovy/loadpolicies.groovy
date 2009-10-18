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
    
    /*
    add commands from http://docs.sun.com/app/docs/doc/820-3886/ssoadm?a=view
    must go to ssoadm.jsp pages to see actual commands
    
    */
def addAgentToGrp(realm,agentgroupname,agentnames) { 
    def query = [cmd: 'add-agent-to-grp', 'realm': realm, 'agentname': agentname, 'agentgroupname': agentgroupname ,submit: ""]
    agentnames.each {agentname -> query.addQueryParam 'agentnames', agentname}  
    println opensso.post(path: "ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
}
    
//atts: list of 'key=value' strings. They are passed in unencoded and the function encodes them prior to posting    
def createAgent(realm,agentname,agenttype,atts) { 
    def query = [cmd: "create-agent", realm: realm, agentname: agentname, agentype: agenttype, submit: ""]
    atts.each {att -> query.addQueryParam 'attributevalues', URLEncoder.encode(att) }  
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
    def query = [cmd: "delete-policies", realm: realm, policynames: policy , submit: ""] 
    policies.each {policy -> query.addQueryParam 'policynames', policy }   
    println opensso.post(path: "ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
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

def execQuery(query) {
	println opensso.post(path: "ssoadm.jsp", query: query, headers:[cookie: "${cookieName}=${token}"]) 
}

/*

Command Templates

def addAgentToGrp() {
   def query = [cmd: 'add-agent-to-grp', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addAmsdkIdrepoPlugin() {
   def query = [cmd: 'add-amsdk-idrepo-plugin', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addAttrDefs() {
   def query = [cmd: 'add-attr-defs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addAttrs() {
   def query = [cmd: 'add-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addAuthCfgEntr() {
   def query = [cmd: 'add-auth-cfg-entr', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addCotMember() {
   def query = [cmd: 'add-cot-member', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addMember() {
   def query = [cmd: 'add-member', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addPluginInterface() {
   def query = [cmd: 'add-plugin-interface', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addPrivileges() {
   def query = [cmd: 'add-privileges', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addResBundle() {
   def query = [cmd: 'add-res-bundle', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addSiteMembers() {
   def query = [cmd: 'add-site-members', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addSiteSecUrls() {
   def query = [cmd: 'add-site-sec-urls', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addSubSchema() {
   def query = [cmd: 'add-sub-schema', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addSvcAttrs() {
   def query = [cmd: 'add-svc-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addSvcIdentity() {
   def query = [cmd: 'add-svc-identity', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def addSvcRealm() {
   def query = [cmd: 'add-svc-realm', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def agentRemoveProps() {
   def query = [cmd: 'agent-remove-props', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def cloneServer() {
   def query = [cmd: 'clone-server', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createAgent() {
   def query = [cmd: 'create-agent', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createAgentGrp() {
   def query = [cmd: 'create-agent-grp', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createAuthCfg() {
   def query = [cmd: 'create-auth-cfg', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createAuthInstance() {
   def query = [cmd: 'create-auth-instance', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createCot() {
   def query = [cmd: 'create-cot', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createDatastore() {
   def query = [cmd: 'create-datastore', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createIdentity() {
   def query = [cmd: 'create-identity', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createMetadataTempl() {
   def query = [cmd: 'create-metadata-templ', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createPolicies() {
   def query = [cmd: 'create-policies', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createRealm() {
   def query = [cmd: 'create-realm', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createServer() {
   def query = [cmd: 'create-server', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createSite() {
   def query = [cmd: 'create-site', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createSubCfg() {
   def query = [cmd: 'create-sub-cfg', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def createSvc() {
   def query = [cmd: 'create-svc', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteAgentGrps() {
   def query = [cmd: 'delete-agent-grps', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteAgents() {
   def query = [cmd: 'delete-agents', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteAttr() {
   def query = [cmd: 'delete-attr', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteAttrDefValues() {
   def query = [cmd: 'delete-attr-def-values', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteAuthCfgs() {
   def query = [cmd: 'delete-auth-cfgs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteAuthInstances() {
   def query = [cmd: 'delete-auth-instances', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteCot() {
   def query = [cmd: 'delete-cot', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteDatastores() {
   def query = [cmd: 'delete-datastores', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteEntity() {
   def query = [cmd: 'delete-entity', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteIdentities() {
   def query = [cmd: 'delete-identities', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deletePolicies() {
   def query = [cmd: 'delete-policies', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteRealm() {
   def query = [cmd: 'delete-realm', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteRealmAttr() {
   def query = [cmd: 'delete-realm-attr', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteServer() {
   def query = [cmd: 'delete-server', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteSite() {
   def query = [cmd: 'delete-site', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteSubCfg() {
   def query = [cmd: 'delete-sub-cfg', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def deleteSvc() {
   def query = [cmd: 'delete-svc', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def doBatch() {
   def query = [cmd: 'do-batch', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def doMigration70() {
   def query = [cmd: 'do-migration70', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def embeddedStatus() {
   def query = [cmd: 'embedded-status', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def exportEntity() {
   def query = [cmd: 'export-entity', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def exportServer() {
   def query = [cmd: 'export-server', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getAttrDefs() {
   def query = [cmd: 'get-attr-defs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getAuthCfgEntr() {
   def query = [cmd: 'get-auth-cfg-entr', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getAuthInstance() {
   def query = [cmd: 'get-auth-instance', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getIdentity() {
   def query = [cmd: 'get-identity', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getIdentitySvcs() {
   def query = [cmd: 'get-identity-svcs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getRealm() {
   def query = [cmd: 'get-realm', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getRealmSvcAttrs() {
   def query = [cmd: 'get-realm-svc-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getRevisionNumber() {
   def query = [cmd: 'get-revision-number', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def getSvrcfgXml() {
   def query = [cmd: 'get-svrcfg-xml', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def importEntity() {
   def query = [cmd: 'import-entity', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def importServer() {
   def query = [cmd: 'import-server', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listAgentGrpMembers() {
   def query = [cmd: 'list-agent-grp-members', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listAgentGrps() {
   def query = [cmd: 'list-agent-grps', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listAgents() {
   def query = [cmd: 'list-agents', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listAuthCfgs() {
   def query = [cmd: 'list-auth-cfgs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listAuthInstances() {
   def query = [cmd: 'list-auth-instances', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listCotMembers() {
   def query = [cmd: 'list-cot-members', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listCots() {
   def query = [cmd: 'list-cots', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listDatastoreTypes() {
   def query = [cmd: 'list-datastore-types', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listDatastores() {
   def query = [cmd: 'list-datastores', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listEntities() {
   def query = [cmd: 'list-entities', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listIdentities() {
   def query = [cmd: 'list-identities', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listIdentityAssignableSvcs() {
   def query = [cmd: 'list-identity-assignable-svcs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listPolicies() {
   def query = [cmd: 'list-policies', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listRealmAssignableSvcs() {
   def query = [cmd: 'list-realm-assignable-svcs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listRealms() {
   def query = [cmd: 'list-realms', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listResBundle() {
   def query = [cmd: 'list-res-bundle', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listServerCfg() {
   def query = [cmd: 'list-server-cfg', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listServers() {
   def query = [cmd: 'list-servers', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def listSites() {
   def query = [cmd: 'list-sites', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def registerAuthModule() {
   def query = [cmd: 'register-auth-module', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeAgentFromGrp() {
   def query = [cmd: 'remove-agent-from-grp', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeAttrChoicevals() {
   def query = [cmd: 'remove-attr-choicevals', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeAttrDefs() {
   def query = [cmd: 'remove-attr-defs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeCotMember() {
   def query = [cmd: 'remove-cot-member', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeMember() {
   def query = [cmd: 'remove-member', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removePrivileges() {
   def query = [cmd: 'remove-privileges', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeResBundle() {
   def query = [cmd: 'remove-res-bundle', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeServerCfg() {
   def query = [cmd: 'remove-server-cfg', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeSiteMembers() {
   def query = [cmd: 'remove-site-members', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeSiteSecUrls() {
   def query = [cmd: 'remove-site-sec-urls', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeSubSchema() {
   def query = [cmd: 'remove-sub-schema', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeSvcAttrs() {
   def query = [cmd: 'remove-svc-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeSvcIdentity() {
   def query = [cmd: 'remove-svc-identity', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def removeSvcRealm() {
   def query = [cmd: 'remove-svc-realm', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrAny() {
   def query = [cmd: 'set-attr-any', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrBoolValues() {
   def query = [cmd: 'set-attr-bool-values', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrChoicevals() {
   def query = [cmd: 'set-attr-choicevals', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrDefs() {
   def query = [cmd: 'set-attr-defs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrEndRange() {
   def query = [cmd: 'set-attr-end-range', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrI18nKey() {
   def query = [cmd: 'set-attr-i18n-key', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrStartRange() {
   def query = [cmd: 'set-attr-start-range', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrSyntax() {
   def query = [cmd: 'set-attr-syntax', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrType() {
   def query = [cmd: 'set-attr-type', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrUiType() {
   def query = [cmd: 'set-attr-ui-type', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrValidator() {
   def query = [cmd: 'set-attr-validator', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setAttrViewBeanUrl() {
   def query = [cmd: 'set-attr-view-bean-url', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setIdentityAttrs() {
   def query = [cmd: 'set-identity-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setIdentitySvcAttrs() {
   def query = [cmd: 'set-identity-svc-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setInheritance() {
   def query = [cmd: 'set-inheritance', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setPluginViewbeanUrl() {
   def query = [cmd: 'set-plugin-viewbean-url', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setRealmAttrs() {
   def query = [cmd: 'set-realm-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setRealmSvcAttrs() {
   def query = [cmd: 'set-realm-svc-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setRevisionNumber() {
   def query = [cmd: 'set-revision-number', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setSitePriUrl() {
   def query = [cmd: 'set-site-pri-url', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setSiteSecUrls() {
   def query = [cmd: 'set-site-sec-urls', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setSubCfg() {
   def query = [cmd: 'set-sub-cfg', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setSvcAttrs() {
   def query = [cmd: 'set-svc-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setSvcI18nKey() {
   def query = [cmd: 'set-svc-i18n-key', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setSvcViewBeanUrl() {
   def query = [cmd: 'set-svc-view-bean-url', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def setSvrcfgXml() {
   def query = [cmd: 'set-svrcfg-xml', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showAgent() {
   def query = [cmd: 'show-agent', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showAgentGrp() {
   def query = [cmd: 'show-agent-grp', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showAgentMembership() {
   def query = [cmd: 'show-agent-membership', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showAgentTypes() {
   def query = [cmd: 'show-agent-types', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showAuthModules() {
   def query = [cmd: 'show-auth-modules', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showDataTypes() {
   def query = [cmd: 'show-data-types', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showDatastore() {
   def query = [cmd: 'show-datastore', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showIdentityOps() {
   def query = [cmd: 'show-identity-ops', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showIdentitySvcAttrs() {
   def query = [cmd: 'show-identity-svc-attrs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showIdentityTypes() {
   def query = [cmd: 'show-identity-types', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showMembers() {
   def query = [cmd: 'show-members', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showMemberships() {
   def query = [cmd: 'show-memberships', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showPrivileges() {
   def query = [cmd: 'show-privileges', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showRealmSvcs() {
   def query = [cmd: 'show-realm-svcs', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showSite() {
   def query = [cmd: 'show-site', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def showSiteMembers() {
   def query = [cmd: 'show-site-members', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def unregisterAuthModule() {
   def query = [cmd: 'unregister-auth-module', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateAgent() {
   def query = [cmd: 'update-agent', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateAgentGrp() {
   def query = [cmd: 'update-agent-grp', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateAuthCfgEntr() {
   def query = [cmd: 'update-auth-cfg-entr', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateAuthInstance() {
   def query = [cmd: 'update-auth-instance', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateDatastore() {
   def query = [cmd: 'update-datastore', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateEntityKeyinfo() {
   def query = [cmd: 'update-entity-keyinfo', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateServerCfg() {
   def query = [cmd: 'update-server-cfg', submit: ''] 
   //begin command customization 
   execQuery(query)
}

def updateSvc() {
   def query = [cmd: 'update-svc', submit: ''] 
   //begin command customization 
   execQuery(query)
}

*/

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