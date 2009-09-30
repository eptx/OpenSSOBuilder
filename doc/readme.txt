Found a newsgroup response by Casey Butterworth at http://markmail.org/message/4446fobsydzvnfzs describing an approach to remotely administring an opensso server by wrapping ssoadm.jsp with an OpenSSOBuilder. This is a reasonable appraoch and I have expanded its features. Contributors welcome.

Code is not well tested, so use with caution.
Best,
Erik

Original post from Casey...

Hi Mark,

I'm currently investigating a few options here (e.g. call the same classes that
the CLI uses, implement a groovy builder, etc) but in the meantime, the
following groovy snippet will probably take you some of the way (obviously need
to expand to support the operations that you require):

import groovyx.net.http.HTTPBuilder

class OpenssoHelper { def opensso def token

def OpenssoHelper(url, username, password) { this.opensso = new HTTPBuilder(url) login(username, password) }

def login(username, password) { def results = parseResults(opensso.get(path:
"/opensso/identity/authenticate", query: [username: username, password:
password])) this.token = results['token.id'] }

def createRealm(realm) { def query = [cmd: "create-realm", realm: realm, submit: ""] println opensso.post(path: "/opensso/ssoadm.jsp", query: query, headers:
[cookie: "iPlanetDirectoryPro=${token}"]) }

def removeRealm(realm) { def query = [cmd: "delete-realm", realm: realm, submit: ""] println opensso.post(path: "/opensso/ssoadm.jsp", query: query, headers:
[cookie: "iPlanetDirectoryPro=${token}"]) }

def parseResults(results) { def properties = new Properties() properties.load results return properties } }

This can be used as follows:

def opensso = new OpenssoHelper("http://server.name:port", "username",
"password") opensso.createRealm("test") opensso.removeRealm("test")

Note that this simply calls the ssoadm.jsp page so to add new functions simply
go online, take a look at what's required, and implement here. Obviously the
ssoadm tools themselves are fairly obscure (which attributes to add to a map,
xml config to use, etc) and when I get time I'm hoping to build up a more self
explanatory API.

Tips:

* If you are unsure of property keys or values, use the GUI get-* and
list-* operations on a pre-configured item

* More info available at
http://docs.sun.com/app/docs/doc/820-3886/ssoadm?l=en&a=view&q=ssoadm

Cheers Casey  
