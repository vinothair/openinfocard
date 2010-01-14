
var TokenIssuer = {
		tokenIssuer : null,

		_trace : function(msg) {
			Components.classes["@mozilla.org/consoleservice;1"]
			 .getService(Components.interfaces.nsIConsoleService)
	          .logStringMessage(msg);
		},
		
		_fail : function(e) {
			var msg;
		    if (e.getMessage) {
		        msg = e + ": " + e.getMessage() + "\n";
		        while (e.getCause() != null) {
		            e = e.getCause();
		            msg += "caused by " + e + ": " + e.getMessage() + "\n";
		        }
		    } else {
		        msg = e;
		    }
		    alert(msg);	
		},
		
		_getExtensionPath : function(extensionName) {
		    var chromeRegistry =
		        Components.classes["@mozilla.org/chrome/chrome-registry;1"]
		            .getService(Components.interfaces.nsIChromeRegistry);
		            
		    var uri =
		        Components.classes["@mozilla.org/network/standard-url;1"]
		            .createInstance(Components.interfaces.nsIURI);
		    
		    uri.spec = "chrome://" + extensionName + "/content/";
		    
		    var path = chromeRegistry.convertChromeURL(uri);
		    if (typeof(path) == "object") {
		        path = path.spec;
		    }
		    
		    path = path.substring(0, path.indexOf("/chrome/") + 1);
		    
		    return path;
		},
		
		initialize : function() {
			var extensionPath = this._getExtensionPath("infocard");
			var libPath = extensionPath + "components/lib/";
			var xmldapUrl = new java.net.URL(libPath+"xmldap.jar");
			var cl = new java.net.URLClassLoader( [ xmldapUrl ]  );
			if (cl === null) {
				this._fail("class loader is null");
				return null;
			}
			
			var aClassName = "org.xmldap.firefox.URLSetPolicy";
			// xmldapPolicy gives us read/write permission on all files that are named cardDB.xml
			var aClass = java.lang.Class.forName(aClassName, true, cl);
			if (aClass === null) {
				this._trace("could not get class for " + aClassName);
				return null;
			}
			var anInstance = aClass.newInstance();
			if (anInstance === null) {
				this._trace("could not create instance of " + aClassName);
				return null;
			}
			anInstance.setOuterPolicy(java.security.Policy.getPolicy());
			java.security.Policy.setPolicy(anInstance);
//			var keystoreURL = new java.net.URL(extensionPath+"components/lib/firefox.jks");
			anInstance.addFilePermission(extensionPath+"components/lib/firefox.jks", "read,write");
//			var cardDbURL = new java.net.URL(extensionPath+"cardDb.xml");
			anInstance.addFilePermission(extensionPath+"cardDb.xml", "read,write");
			
			anInstance.addPermission(new java.security.SecurityPermission("putProviderProperty.BC"));
			anInstance.addPermission(new java.security.SecurityPermission("insertProvider.BC"));
			anInstance.addPermission(new java.util.PropertyPermission("org.bouncycastle.*", "read"));
			anInstance.addPermission(new java.util.PropertyPermission("jsr105Provider", "read"));
			
			// anInstance.addPermission(new javax.smartcardio.CardPermission("*", "*")); // all terminals, all action
			var cpClasz = java.lang.Class.forName("javax.smartcardio.CardPermission", true, cl);
			if (cpClasz === null) {
				this._trace("javax.smartcardio.CardPermission is not available");
			} else {
				var cpConstructor = cpClasz.getConstructor([java.lang.Class.forName("java.lang.String"),java.lang.Class.forName("java.lang.String")]);
				if (cpConstructor === null) {
					this._trace("javax.smartcardio.CardPermission(String, String) is not available");
				} else {
					var cardPermission = cpConstructor.newInstance(["*", "*"]);// all terminals, all action
					if (cardPermission === null) {
						this._trace("javax.smartcardio.CardPermission(String, String) failed");
					} else {
						anInstance.addPermission(cardPermission);
					}
				}
			}
			
//			anInstance.addPermission(new java.security.AllPermission());
			anInstance.addURL(xmldapUrl);

			// reload the classloader
			cl = new java.net.URLClassLoader( [ xmldapUrl ]  );
			if (cl === null) {
				this._trace("class loader is NULL");
				return null;
			}

			var tiClass = java.lang.Class.forName("org.xmldap.firefox.TokenIssuer", true, cl);
			if (tiClass === null) {
				this._trace("tiClass is null");
				return null;
			}
			var tiConstructor = tiClass.getConstructor([java.lang.Class.forName("java.lang.String")]);
			if (tiConstructor === null) {
				this._trace("tiConstructor is null");
				return null;
			}
			
			this.tokenIssuer = tiConstructor.newInstance( [extensionPath] );
			return (this.tokenIssuer !== null);
		},
		
		getToken : function(serializedPolicy) {
			return this.tokenIssuer.getToken(serializedPolicy);
		},
		
		importManagedCard : function(importedCardJSONStr, cardFileJSONStr) {
			try {
				var result = this.tokenIssuer.importManagedCard(importedCardJSONStr, cardFileJSONStr);
				return result;
			} catch (e) {
				this._fail(e);
		    }
		    return null;
		},
		
		getIssuerLogoURL : function(cert) {
			try {
				var result = this.tokenIssuer.getIssuerLogoURL(cert);
				return result;
			} catch (e) {
				this._fail(e);
		    }
		    return null;
    },
    
    getWalletException : function() {
      this._trace("getWalletException");
      try {
        var result = this.tokenIssuer.getWalletException();
        return result;
      } catch(e) {
        this._fail(e);
        throw e;
      }
    },
    
    resetWalletException : function() {
      this._trace("resetWalletException");
      try {
        this.tokenIssuer.resetWalletException();
      } catch(e) {
        this._fail(e);
        throw e;
      }
    },
    
    isPhoneAvailable : function() {
			this._trace("isPhoneAvailable");
			try {
				var result = this.tokenIssuer.isPhoneAvailable();
				return result;
			} catch(e) {
				this._fail("isPhoneAvailable" + e);
				throw e;
			}
		},
		
		endCardSelection : function() {
			this._trace("endCardSelection");
			try {
				this.tokenIssuer.endCardSelection();
			} catch(e) {
				this._fail("endCardSelection" + e);
			}
		},
		
		beginCardSelection : function() {
			this._trace("beginCardSelection");
			try {
				var result = this.tokenIssuer.beginCardSelection();
				return result;
			} catch(e) {
				this._fail(e);
				throw e;
			}
		},
		
		getSelectedCard : function() {
			this._trace("getSelectedCard");
			try {
				var result = this.tokenIssuer.getSelectedCard();
				return result;
			} catch(e) {
				this._fail(e);
				throw e;
			}
		},
		
		phoneFini : function() {
			this._trace("phoneFini");
			try {
			  this.tokenIssuer.phoneFini();
			} catch(e) {
				this._fail(e);
			}
		},
		
		finalize : function() {}

};

