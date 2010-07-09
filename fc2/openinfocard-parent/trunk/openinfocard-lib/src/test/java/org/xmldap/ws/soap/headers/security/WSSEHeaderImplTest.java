package org.xmldap.ws.soap.headers.security;

import junit.framework.TestCase;
import org.xmldap.ws.soap.headers.security.token.UsernameToken;


public class WSSEHeaderImplTest extends TestCase {

    WSSEHeaderImpl header;
    UsernameToken token;

    public void setUp() throws Exception {
        super.setUp();
        header = new WSSEHeaderImpl();
        token = new UsernameToken("username", "password");
        token.createNonce();
        token.addCreated();
        header.addToken(token);

    }

    public void testAddToken() throws Exception {
       assertEquals(0, header.tokens.lastIndexOf(token));
    }

    public void testToXML() throws Exception {

        assertEquals("<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"><wsse:UsernameToken xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"" + token.getId() + "\"><wsse:Username>username</wsse:Username><wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">password</wsse:Password><wsse:Nonce>" + token.getNonce() + "</wsse:Nonce><wsu:Created>" + token.getCreated() + "</wsu:Created></wsse:UsernameToken></wsse:Security>", header.toXML());

    }
}
