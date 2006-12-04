var return_to = "http://127.0.0.1:7055/";

var verified_id;
var openid_email;
var openid_nickname;
var openid_fullname;

function openid(server){

    debug("Starting openid server");
    startServer();


    var openid_url;
    if (server.indexOf('http') != 0) {
        openid_url = "http://" + server;
    } else {
        openid_url = server;
    }

    debug("Checking openid_url: " + openid_url);

    var req = new XMLHttpRequest();
    req.open('GET', openid_url, false);
    req.setRequestHeader("User-Agent", "xmldap openid stack");
    req.send(null);
    if(req.status == 200) {

        var resp = req.responseText;
        var serverIndex = resp.indexOf("openid.server");
        var next = resp.substring(serverIndex);
        var hrefIndex = next.indexOf("href=");
        hrefIndex += 6;
        var subStr = next.substring(hrefIndex);
        var endAddr = subStr.indexOf('"');
        var server = subStr.substring(0,endAddr);
        debug("Openid: " + server);
        try{

            var url = server + "?openid.identity=" + openid_url + "&openid.return_to=" + return_to + "&openid.mode=checkid_immediate&openid.sreg.required=email,fullname,nickname";
            debug('Performing check_immediate: ' + url);
            checkimmediate(url);

        } catch (ex) {
            alert("Error loading remote iframe: " + ex);
        }

    }


}


function checkimmediate(uri) {

    var iframe = document.getElementById('openid_iframe');
    var window = iframe.contentWindow;
    iframe.setAttribute('src', uri);

}

function handleOpenIdResponse(url) {

    var user_setup_url = getParam(url, "openid.user_setup_url");
    var identity = getParam(url, "openid.identity");

    debug("openid.user_setup_url: " + user_setup_url);
    debug("openid.identity: " + identity);
    if ( identity != null ) {

        verified_id = urlDecode(identity);
        openid_email = urlDecode(getParam(url, "openid.sreg.email"));
        openid_nickname = urlDecode(getParam(url, "openid.sreg.nickname"));
        openid_fullname = urlDecode(getParam(url, "openid.sreg.fullname"));
        debug('Done processing openid');

        finalizeOpenId();

    } else if ( user_setup_url != null ) {


        var se = document.createElement("iframe");
        se.setAttribute('id','openid_iframe2');
        se.setAttribute('style','min-width:795px; max-width:795px;min-height:445px;max-height:445px;');
        se.style.border = '0';
        var instructarea = document.getElementById('openID');
        instructarea.appendChild(se);
        se.contentWindow.location = urlDecode(user_setup_url) + "&openid.sreg.required=email,fullname,nickname";


    } else {

        alert('openid error');

    }


}



var serverStarted = false;
var serverSocket;
var request;

var async_listener = {

    onStopListening : function(serverSocket, status){},

    onSocketAccepted : function(serverSocket, transport) {

        debug('Got Server Connection');
        var inputstream = transport.openInputStream(0,0,0);
        var input = Components.classes["@mozilla.org/scriptableinputstream;1"].createInstance(Components.interfaces.nsIScriptableInputStream);
        input.init(inputstream);

        var output = transport.openOutputStream(0,0,0);
        var page = "HTTP/1.0 200 OK\nContent-type: text/html\n\n<body bgcolor='#333333'></body>";
        output.write(page, page.length);
        output.close();

        var listener = {

            requestData : "",

            onStartRequest: function(request, context){},

            onStopRequest: function(request, context, status) {

                input.close();
                output.close();

            },

            onDataAvailable: function(request, context, stream, offset, count) {

                this.requestData += input.read(count);
                debug("bytes read: " + count);
                var queryStart = this.requestData.indexOf("/?");
                queryStart += 2;
                var queryString = this.requestData.substring(queryStart, this.requestData.indexOf(" HTTP/1."));
                handleOpenIdResponse(queryString);

            }

        };


        var pump = Components.classes["@mozilla.org/network/input-stream-pump;1"].createInstance(Components.interfaces.nsIInputStreamPump);
        pump.init(inputstream, -1, -1, 0, 0, false);
        pump.asyncRead(listener,null);


    }

};




function startServer(){

    if ( ! serverStarted )  {
        serverSocket = Components.classes["@mozilla.org/network/server-socket;1"].createInstance(Components.interfaces.nsIServerSocket);
        serverSocket.init(7055,true,-1);
        serverSocket.asyncListen(async_listener);
        serverStarted = true;
    }

}

function stopServer(){

    if ( serverStarted )  {
        serverSocket.close();
    }

}

function getParam(query, param) {

    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if (pair[0] == param) {
            return pair[1];
        }
    }
    return null;
}

function urlDecode(string){

    var utftext = unescape(string);

    var string = "";
    var i = 0;
    var c = c1 = c2 = 0;

    while ( i < utftext.length ) {

        c = utftext.charCodeAt(i);

        if (c < 128) {
            string += String.fromCharCode(c);
            i++;
        }
        else if((c > 191) && (c < 224)) {
            c2 = utftext.charCodeAt(i+1);
            string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
            i += 2;
        }
        else {
            c2 = utftext.charCodeAt(i+1);
            c3 = utftext.charCodeAt(i+2);
            string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
            i += 3;
        }

    }

    return string;
}
