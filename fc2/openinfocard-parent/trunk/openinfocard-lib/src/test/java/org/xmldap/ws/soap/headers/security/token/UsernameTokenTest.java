package org.xmldap.ws.soap.headers.security.token;

import junit.framework.TestCase;

/**
 * UsernameToken Tester.
 *
 * @author <Authors name>
 * @since <pre>03/16/2006</pre>
 * @version 1.0
 */
public class UsernameTokenTest extends TestCase {

    UsernameToken token;

    public void setUp() throws Exception {
        super.setUp();
        token = new UsernameToken("username","password");
        token.createNonce();
        token.addCreated();
    }

    public void testSetGetUsername() throws Exception {
        assertEquals("username", token.getUsername());
    }

    public void testSetGetPassword() throws Exception {
        assertEquals("password", token.getPassword());
    }

    public void testToXML() throws Exception {

        System.out.println(token.toXML());
        assertEquals("<wsse:UsernameToken xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"" + token.getId() + "\"><wsse:Username>username</wsse:Username><wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">password</wsse:Password><wsse:Nonce>" + token.getNonce() + "</wsse:Nonce><wsu:Created>" + token.getCreated() + "</wsu:Created></wsse:UsernameToken>", token.toXML());

    }
}
