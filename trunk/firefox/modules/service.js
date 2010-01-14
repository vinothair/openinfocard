
const EXPORTED_SYMBOLS = ['CardstoreManager'];

const Cc = Components.classes;
const Ci = Components.interfaces;
const Cr = Components.results;
const Cu = Components.utils;

Cu.import("resource://gre/modules/XPCOMUtils.jsm");
//Cu.import("resource://infocard/ext/Sync.js");
//Cu.import("resource://infocard/log4moz.js");
//Cu.import("resource://infocard/constants.js");
//Cu.import("resource://infocard/util.js");
//Cu.import("resource://infocard/auth.js");
//Cu.import("resource://infocard/resource.js");
//Cu.import("resource://infocard/base_records/wbo.js");
//Cu.import("resource://infocard/base_records/crypto.js");
//Cu.import("resource://infocard/base_records/keys.js");
//Cu.import("resource://infocard/engines.js");
//Cu.import("resource://infocard/identity.js");
//Cu.import("resource://infocard/status.js");
//Cu.import("resource://infocard/engines/clientData.js");

//Cu.import("resource://infocard/constants.js", CardstoreManager);
//Cu.import("resource://infocard/util.js", CardstoreManager);
//Cu.import("resource://infocard/auth.js", CardstoreManager);
//Cu.import("resource://infocard/resource.js", CardstoreManager);
//Cu.import("resource://infocard/base_records/keys.js", CardstoreManager);
//Cu.import("resource://infocard/notifications.js", CardstoreManager);
//Cu.import("resource://infocard/identity.js", CardstoreManager);
//Cu.import("resource://infocard/status.js", CardstoreManager);
//Cu.import("resource://infocard/stores.js", CardstoreManager);
//Cu.import("resource://infocard/engines.js", CardstoreManager);
//
//Cu.import("resource://infocard/engines/bookmarks.js", CardstoreManager);
//Cu.import("resource://infocard/engines/clientData.js", CardstoreManager);
//Cu.import("resource://infocard/engines/forms.js", CardstoreManager);
//Cu.import("resource://infocard/engines/history.js", CardstoreManager);
//Cu.import("resource://infocard/engines/prefs.js", CardstoreManager);
//Cu.import("resource://infocard/engines/passwords.js", CardstoreManager);
//Cu.import("resource://infocard/engines/tabs.js", CardstoreManager);

//Utils.lazy(CardstoreManager, 'Service', CardstsoreManagerSvc);

/*
 * Service singleton
 * Main entry point into CardstoreManager's sync framework
 */

function CardstoreManagerSvc() {
	this.wrappedJSObject = this;
//  this._notify = Utils.notify("cardstoremanager:service:");
}
CardstoreManagerSvc.prototype = {

//  _lock: Utils.lock,
//  _catch: Utils.catch,
  _isQuitting: false,
  _loggedIn: false,
  _syncInProgress: false,
  _keyGenEnabled: true,

  db: "cardDb.xml",

  get locked() { return this._locked; },
  lock: function Svc_lock() {
    if (this._locked)
      return false;
    this._locked = true;
    return true;
  },
  unlock: function Svc_unlock() {
    this._locked = false;
  },

  log : function log(msg) {
	  var debug = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
	  debug.logStringMessage("cardstoreManager: " + msg);
  },

  /**
   * Prepare to initialize the rest of CardstoreManager after waiting a little bit
   */
  onStartup: function onStartup() {
	  this.log("start");
	  
	  var cardfile = this.readCardStore();
	  
//    Status.service = STATUS_DELAYED;
//
//    // Figure out how many seconds to delay loading CardstoreManager based on the app
//    let wait = 0;
//    switch (Svc.AppInfo.ID) {
//      case FIREFOX_ID:
//        // Add one second delay for each tab in every window
//        let enum = Svc.WinMediator.getEnumerator("navigator:browser");
//        while (enum.hasMoreElements())
//          wait += enum.getNext().gBrowser.mTabs.length;
//    }
//
//    // Make sure we wait a little but but not too long in the worst case
//    wait = Math.ceil(Math.max(5, Math.min(20, wait)));
//
//    this._initLogs();
//    this._log.info("Loading CardstoreManager " + WEAVE_VERSION + " in " + wait + " sec.");
//    Utils.delay(this._onStartup, wait * 1000, this, "_startupTimer");
  },

  // one-time initialization like setting up observers and the like
  // xxx we might need to split some of this out into something we can call
  //     again when username/server/etc changes
//  _onStartup: function _onStartup() {
//    Status.service = STATUS_OK;
//    this.enabled = true;
//
//    this._registerEngines();
//
//    // Reset our sync id if we're upgrading, so sync knows to reset local data
//    if (WEAVE_VERSION != Svc.Prefs.get("lastversion")) {
//      this._log.info("Resetting client syncID from _onStartup.");
//      Clients.resetSyncID();
//    }
//
//    let ua = Cc["@mozilla.org/network/protocol;1?name=http"].
//      getService(Ci.nsIHttpProtocolHandler).userAgent;
//    this._log.info(ua);
//
//    if (!this._checkCrypto()) {
//      this.enabled = false;
//      this._log.error("Could not load the CardstoreManager crypto component. Disabling " +
//                      "CardstoreManager, since it will not work correctly.");
//    }
//
//    Svc.Observer.addObserver(this, "network:offline-status-changed", true);
//    Svc.Observer.addObserver(this, "private-browsing", true);
//    Svc.Observer.addObserver(this, "quit-application", true);
//    Svc.Observer.addObserver(this, "cardstoremanager:service:sync:finish", true);
//    Svc.Observer.addObserver(this, "cardstoremanager:service:sync:error", true);
//    Svc.Observer.addObserver(this, "cardstoremanager:service:backoff:interval", true);
//    Svc.Observer.addObserver(this, "cardstoremanager:engine:score:updated", true);
//
//    if (!this.enabled)
//      this._log.info("CardstoreManager Sync disabled");
//
//    // Create CardstoreManager identities (for logging in, and for encryption)
//    ID.set('CardstoreManagerID', new Identity('Mozilla Services Password', this.username));
//    Auth.defaultAuthenticator = new BasicAuthenticator(ID.get('CardstoreManagerID'));
//
//    ID.set('CardstoreManagerCryptoID',
//           new Identity('Mozilla Services Encryption Passphrase', this.username));
//
//    this._updateCachedURLs();
//
//    if (Svc.Prefs.get("autoconnect"))
//      this._autoConnect();
//  },

  QueryInterface: XPCOMUtils.generateQI([Ci.nsIObserver,
                                         Ci.nsISupportsWeakReference]),

  // nsIObserver

//  observe: function CardstoreManagerSvc__observe(subject, topic, data) {
//    switch (topic) {
//      case "network:offline-status-changed":
//        // Whether online or offline, we'll reschedule syncs
//        this._log.trace("Network offline status change: " + data);
//        this._checkSyncStatus();
//        break;
//      case "private-browsing":
//        // Entering or exiting private browsing? Reschedule syncs
//        this._log.trace("Private browsing change: " + data);
//        this._checkSyncStatus();
//        break;
//      case "quit-application":
//        this._onQuitApplication();
//        break;
//      case "cardstoremanager:service:sync:error":
//        this._handleSyncError();
//        break;
//      case "cardstoremanager:service:sync:finish":
//        this._scheduleNextSync();
//        this._syncErrors = 0;
//        break;
//      case "cardstoremanager:service:backoff:interval":
//        let interval = data + Math.random() * data * 0.25; // required backoff + up to 25%
//        Status.backoffInterval = interval;
//        Status.minimumNextSync = Date.now() + data;
//        break;
//      case "cardstoremanager:engine:score:updated":
//        this._handleScoreUpdate();
//        break;
//      case "idle":
//        this._log.trace("Idle time hit, trying to sync");
//        Svc.Idle.removeIdleObserver(this, IDLE_TIME);
//        Utils.delay(function() this.sync(false), 0, this);
//        break;
//    }
//  },

//  _handleScoreUpdate: function CardstoreManagerSvc__handleScoreUpdate() {
//    const SCORE_UPDATE_DELAY = 3000;
//    Utils.delay(this._calculateScore, SCORE_UPDATE_DELAY, this, "_scoreTimer");
//  },
//
//  _calculateScore: function CardstoreManagerSvc_calculateScoreAndDoStuff() {
//    var engines = Engines.getEnabled();
//    for (let i = 0;i < engines.length;i++) {
//      this._log.trace(engines[i].name + ": score: " + engines[i].score);
//      this.globalScore += engines[i].score;
//      engines[i]._tracker.resetScore();
//    }
//
//    this._log.trace("Global score updated: " + this.globalScore);
//
//    if (this.globalScore > this.syncThreshold) {
//      this._log.debug("Global Score threshold hit, triggering sync.");
//      this.syncOnIdle();
//    }
//    else if (!this._syncTimer) // start the clock if it isn't already
//      this._scheduleNextSync();
//  },

  // These are global (for all engines)

  // gets cluster from central LDAP server and returns it, or null on error

  /**
   * Call sync() on an idle timer
   *
   */
//  syncOnIdle: function CardstoreManagerSvc_syncOnIdle() {
//    this._log.debug("Idle timer created for sync, will sync after " +
//                    IDLE_TIME + " seconds of inactivity.");
//    Svc.Idle.addIdleObserver(this, IDLE_TIME);
//  },

};

//for export
let CardstoreManager = {
		Service : new CardstoreManagerSvc()
};
