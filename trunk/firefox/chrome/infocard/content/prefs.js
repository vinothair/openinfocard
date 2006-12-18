var IdentitySelectorPreferences = {

    getPrefsBranch: function() {

        var prefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
        var branch = prefs.getBranch("identityselector.");
        return branch;

    },

    getActiveSelectorCID: function() {

        return IdentitySelector.getCharPref('contractid');

    },

    setActiveSelectorCID: function(cid) {

        return IdentitySelector.setCharPref('contractid',cid);

    },

    getCharPref: function(name) {

         var branch = IdentitySelector.getPrefsBranch();
         var pref = null;
         if (branch.prefHasUserValue(name)) {
             pref =  branch.getCharPref(name);
         }
         return pref;

    },

    setCharPref: function(name, value) {

         var branch = IdentitySelector.getPrefsBranch();
         branch.setCharPref(name, value);

    }

};
