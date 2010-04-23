
Components.utils.import("resource://gre/modules/XPCOMUtils.jsm");

function OicCardstoreFile() {
  // If you only need to access your component from Javascript, uncomment the following line:
  this.wrappedJSObject = this;
}

OicCardstoreFile.prototype = {
    // properties required for XPCOM registration:
    classDescription: "The openinfocard cardstore in a local file implementation",
    classID:          Components.ID("{bdc78940-db54-11de-8a39-0800200c9a66}"),
    contractID:       "@openinfocard.org/cardstorefile;1",

    // QueryInterface implementation
    QueryInterface: XPCOMUtils.generateQI([Components.interfaces.IInformationCardStore]),

    db: "cardDb.xml",
    
    errorstring: "",
    errornumber: 0,

    /* returns true on success */
    login : function(in string credentials) {
      return true;
    },
    
    void   logout() {
    },

    boolean loggedIn() {
      return true;
    },


    void   clearCardStore() {
    },

    // the informationCardXml is defined in ISIP 1.5
    void   addCard(in string informationCardXml) {
    },
    void   removeCard(in string cardId) {
    },

    // the roamingStoreXml is defined in ISIP 1.5
    void   addCardsFromRoamingStore(in string roamingStoreXml) {
    },

    // the informationCardXml is defined in ISIP 1.5
    void updateCard(in string informationCardXml, in string cardId) {
    },

    void getAllCardIds(out unsigned long count, [array, size_is(count)] out string cardIds) {
    },

    unsigned long getCardCount() {
      return 0;
    }

    string cardIdIterator() {
      return null;
    }
    string cardidIteratorNext(in string iterator) {
      return null;
    },
    boolean cardIdIteratorHasNext(in string iterator) {
      return true;
    },

    // returns an encrypted card store as defined in ISIP 1.5
    string cardStoreExportAllCards(in wstring password) {
      return null;
    },
    string cardStoreExportCards(in wstring password, in unsigned long count, [array, size_is(count)] in string cardIds) {
      return null;
    },

    // this may return null if this cardStore is not willing to reveal the mastersecret
    string getMasterSecretForCard(in string cardId) {
      return null;
    },
    string getRpIdentifier(in string cardId, in nsIX509Cert relyingPartyCertificate); {
      return null;
    },
    
    string getCardByPPID(in string PPID, in nsIX509Cert relyingPartyCertificate) {
      return null;
    },

    string getCardStoreName() {
      return null;
    },
    string getCardStoreVersion() {
      return "1.0";
    }

    string getToken(in string serializedPolicy) {
      return "not implemented";
    }
};

var components = [OicCardstoreFile];

function NSGetModule(compMgr, fileSpec) {
     return XPCOMUtils.generateModule(components);
}


