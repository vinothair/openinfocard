package org.xmldap.ws.trust;

import junit.framework.TestCase;
import org.xmldap.ws.WSConstants;
import org.xmldap.ws.soap.headers.security.token.UsernameToken;

/**
 * RequestSecurityToken Tester.
 *
 * @author <Authors name>
 * @since <pre>03/18/2006</pre>
 * @version 1.0
 */
public class RequestSecurityTokenTest extends TestCase {

    /*

    <wsp:AppliesTo xmlns:wsp="http://schemas.xmlsoap.org/ws/2002/12/policy">
        <wsa:EndpointReference xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/03/addressing">
        <wsa:Address>http://appliesto</wsa:Address>
        </wsa:EndpointReference>
    </wsp:AppliesTo>

    <wst:Claims xmlns:claim="http://pingidentity.com/schema/sts/claim">
        <claim:claim name="name2">val2</claim:claim>
        <claim:claim name="name1">val1</claim:claim>
    </wst:Claims>
    */

    RequestSecurityToken rst;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testToXML() throws Exception{

        rst = new RequestSecurityToken(WSConstants.RST_ISSUE_TYPE_04_04);
        rst.setTokenType(WSConstants.SAML11_NAMESPACE);
        UsernameToken token = new UsernameToken("username","password");
        rst.setSecurityTokenReference(token.getSecurityTokenReference());

        //assertEquals("<wst:RequestSecurityToken xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"><wst:RequestType>" + RequestSecurityToken.ISSUE_TYPE_04_04 + "</wst:RequestType><wst:TokenType>" + RequestSecurityToken.SAML11_NAMESPACE + "</wst:TokenType>" + token.getSecurityTokenReference() + "</wst:RequestSecurityToken>", rst.toXML());



    }
}
