<%--

 Copyright (c) 2006, Axel Nennker - nennker.de
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--%>

<html>


<%@page info="Login" language="java"%>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato"%>
<%@taglib uri="/WEB-INF/auth.tld" prefix="auth"%>
<jato:useViewBean className="com.sun.identity.authentication.UI.LoginViewBean">


<%@ page contentType="text/html" %>

<head>
<title><jato:text name="htmlTitle_Login" /></title>

<%
String ServiceURI = (String) viewBean.getDisplayFieldValue(viewBean.SERVICE_URI);
String linkElement = "<link rel=\"xrds.metadata\" href=\"" + request.getServletPath() + "?xmldap_rp.xrds" + "\"/>";
String metaElement = "<meta http-equiv=\"X-XRDS-Location\" content=\"" + request.getServletPath() + "?xmldap_rp.xrds" + "\"/>";

%>
<link rel="stylesheet" href="<%= ServiceURI %>/css/styles.css" type="text/css">
<script language="JavaScript" src="<%= ServiceURI %>/js/browserVersion.js"></script>
<script language="JavaScript" src="<%= ServiceURI %>/js/auth.js"></script>

<script language="JavaScript">

    writeCSS('<%= ServiceURI %>');

<jato:content name="validContent">
    var defaultBtn = 'Submit';
    var elmCount = 0;

    /** submit form with default command button */
    function defaultSubmit() {
	LoginSubmit(defaultBtn);
    }

    /**
     * submit form with given button value
     *
     * @param value of button
     */
    function LoginSubmit(value) {
        aggSubmit();
        var hiddenFrm = document.forms['Login'];

        if (hiddenFrm != null) {
	    hiddenFrm.elements['IDButton'].value = value;
            hiddenFrm.submit();
        }
    }

</jato:content>
</script>
<script type="text/javascript"><!--// Empty script so IE5.0 Windows will draw table and button borders
//-->
</script>

<style type="text/css">
<!--
body {
  background-color: #fff;
}
td {
  background-color: #fff;
}
td.navigation {
  background-color: #d9d9d9;
}
.navigation ul {
  width:150px; 
  padding: 0;
  border-bottom: 1px solid #999;
  margin: 20px 0 0 0; 
}
.navigation li {
  list-style-type: none;
  line-height: 1em;
  border-top: 1px solid #999;
} 
.navigation li a, .navigation li a:visited {
  display: block;
  color: #333;
  font-weight: bold;
  padding: 3px 0;
  text-decoration: none;
  text-indent: 20px;
}
.navigation li a:hover {
  color: #000;
  background: url("<%= ServiceURI %>/images/navi-over.gif") 0 50% no-repeat;
  text-decoration: none;
}
#footer {
  margin: 1em 24px;
  color: #666;
}
-->
</style>
</head>

<body class="LogBdy" onload="placeCursorOnFirstElm();">

  <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" title="">
	<tr>
  	  <td width="172" height="53" valign="top" style="border-bottom: 9px solid #43648D; background-color:#fff;"><a href="http://www.deutsche-telekom-laboratories.de/deutsch/index.html"><img src="<%= ServiceURI %>/images/head_t.gif" alt="Deutsche Telekom Laboratories" width="172" height="53"></a></td>
    <td height="53" align="left" valign="top" style="border-bottom: 9px solid #43648D; background-color:#E20074;"><img src="<%= ServiceURI %>/images/head_logo_mba2.gif" alt="Overarching AAA Ð Multi-method Baes Authentication"></td>
	</tr>
  	<tr>
    	<td width="172" valign="top" class="navigation">
			<ul><li><a href="#">Shop</a></li><li><a href="http://n2v1.e1.i3alab.net/mba/DemoShop/index.html">T-Demoshop</a></li></ul>
		</td>
    	<td valign="top" bordercolor="#993399" style="padding: 40px 24px;">
      		<table title="" cellspacing=0 cellpadding=0>
          		<tr>
            		<td valign="top">
	              		<table border="0" cellspacing="0" cellpadding="0">
                			<tr>
                  				<td colspan="2"></td>
                			</tr>
	<!-- display authentication scheme -->
    <!-- Header display -->
							<tr>
        						<td></td>
        						<td><div class="logTxtSvrNam">                    
	<jato:content name="ContentStaticTextHeader">
	    <jato:getDisplayFieldValue name='StaticTextHeader'
		defaultValue='Authentication' fireDisplayEvents='true'
		escape='false'/>
	</jato:content>        
								</div></td>
							</tr>
	<!-- End of Header display -->      
  
	<jato:content name="validContent">

	<jato:tiledView name="tiledCallbacks"
	    type="com.sun.identity.authentication.UI.CallBackTiledView">

	<script language="javascript">
	    elmCount++;
	</script>

	<jato:content name="textBox">
	<!-- text box display -->
							<tr>
	<form name="frm<jato:text name="txtIndex" />" action="blank"
	    onSubmit="defaultSubmit(); return false;">

								<td nowrap="nowrap"><div class="logLbl">
            <jato:content name="isRequired">
            <img src="<%= ServiceURI %>/images/required.gif" alt="Required Field" 
            title="Required Field" width="7" height="14" />
            </jato:content>
            <span class="LblLev2Txt">
            <label for="IDToken<jato:text name="txtIndex" />">                
                <jato:text name="txtPrompt" defaultValue="User name:" 
                                                        escape="false" />                           
            </label></span></div>
								</td>
								<td><div class="logInp">
	    <input type="text" name="IDToken<jato:text name="txtIndex" />"
                id="IDToken<jato:text name="txtIndex" />"
		value="" class="TxtFld"></div>
								</td>
	</form>
							</tr>	
	<!-- end of textBox -->
	</jato:content>

	<jato:content name="password">
	<!-- password display -->
         <script language="javascript">
            elmCount++;
         </script>


							<tr>
	<form name="frm<jato:text name="txtIndex" />" action="blank"
	    onSubmit="defaultSubmit(); return false;">

								<td nowrap="nowrap"><div class="logLbl">
            <jato:content name="isRequired">
            <img src="<%= ServiceURI %>/images/required.gif" alt="Required Field" 
            title="Required Field" width="7" height="14" />
            </jato:content>
            <span class="LblLev2Txt">
            <label for="IDToken<jato:text name="txtIndex" />">  
                <jato:text name="txtPrompt" defaultValue="Password:" 
                                                        escape="false" />                
            </label></span></div>
								</td>
								<td><div class="logInp">
	    <input type="password" name="IDToken<jato:text name="txtIndex" />"
                id="IDToken<jato:text name="txtIndex" />"
		value="" class="TxtFld"></div>
								</td>
	</form>
							</tr>	
	<!-- end of password -->
	</jato:content>

	<jato:content name="choice">
	<!-- choice value display -->
							<tr>
	<form name="frm<jato:text name="txtIndex" />" action="blank"
	    onSubmit="defaultSubmit(); return false;">

								<td nowrap="nowrap"><div class="logLbl">
            <jato:content name="isRequired">
            <img src="<%= ServiceURI %>/images/required.gif" alt="Required Field" 
            title="Required Field" width="7" height="14" />
            </jato:content>
            <span class="LblLev2Txt">
            <label for="IDToken<jato:text name="txtIndex" />">  
                <jato:text name="txtPrompt" defaultValue="RadioButton:" 
                                                            escape="false" />                
            </label></span></div>
								</td>

								<td><div class="logInp">
	    <jato:tiledView name="tiledChoices"
		type="com.sun.identity.authentication.UI.CallBackChoiceTiledView">

	    <jato:content name="selectedChoice">
	        <input type="radio"
		    name="IDToken<jato:text name="txtParentIndex" />"
                    id="IDToken<jato:text name="txtParentIndex" />"
		    value="<jato:text name="txtIndex" />" class="Rb"
		    checked><jato:text name="txtChoice" /><br>
	    </jato:content>

	    <jato:content name="unselectedChoice">
	        <input type="radio"
		    name="IDToken<jato:text name="txtParentIndex" />"
                    id="IDToken<jato:text name="txtParentIndex" />"
		    value="<jato:text name="txtIndex" />" class="Rb"
		    ><jato:text name="txtChoice" /><br>
	    </jato:content>

	    </jato:tiledView></div>
								</td>
	</form>
							</tr>
							<!--tr></tr-->
	<!-- end of choice -->
	</jato:content>

	<!-- end of tiledCallbacks -->
	</jato:tiledView>

	<!-- end of validContent -->
	</jato:content>


	<jato:content name="ContentStaticTextResult">
	<!-- after login output message -->
	<p><b><jato:getDisplayFieldValue name='StaticTextResult'
	    defaultValue='' fireDisplayEvents='true' escape='false'/></b></p>
	</jato:content>

	<jato:content name="ContentHref">
	<!-- URL back to Login page -->
	    <p><auth:href name="LoginURL"
		    fireDisplayEvents='true'>
		<jato:text
		name="txtGotoLoginAfterFail" /></auth:href></p>
	</jato:content>

	<jato:content name="ContentImage">
	<!-- customized image defined in properties file -->
	    <p><img name="IDImage"
		src="<jato:getDisplayFieldValue name='Image'/>" alt=""></p>
	</jato:content>

	<jato:content name="ContentButtonLogin">
	<!-- Submit button -->

	<jato:content name="hasButton">
	<script language="javascript">
	    defaultBtn = '<jato:text name="defaultBtn" />';
	</script>
	<tr>
	<td><img src="<%= ServiceURI %>/images/dot.gif" 
        width="1" height="15" alt="" /></td>
	<td>
	    <table border=0 cellpadding=0 cellspacing=0>
	    <tr>
	    <td>
	    <jato:tiledView name="tiledButtons"
		type="com.sun.identity.authentication.UI.ButtonTiledView">	    
		<script language="javascript">
		    markupButton(
			'<jato:text name="txtButton" />',
			"javascript:LoginSubmit('<jato:text name="txtButton" />')");
		</script>	    
	    </jato:tiledView>	
        </td>
        </tr>
	    </table>
	</td>
	</tr>
	<!-- end of hasButton -->
	</jato:content>

	<jato:content name="hasNoButton">
	<tr>
	<td><img src="<%= ServiceURI %>/images/dot.gif" 
        width="1" height="15" alt="" /></td>
    <td>
	    <script language="javascript">
		markupButton(
		    '<jato:text name="lblSubmit" />',
		   	"javascript:LoginSubmit('<jato:text name="lblSubmit" />')");
	    </script>
	</td>
	</tr>
	<!-- end of hasNoButton -->
	</jato:content>

	<!-- end of ContentButtonLogin -->
	</jato:content>
	<tr>
         <td nowrap="nowrap">
          <div class="logLbl">
           <span class="LblLev2Txt">
            <label for="IDToken3">
             Please use your InfoCard
            </label>
           </span>
          </div>
	 </td>
	 <td>
<jato:content name="validContent">
<auth:form name="Login" method="post"
    defaultCommandChild="DefaultLoginURL" >
<h2>&nbsp;<img src='<%= ServiceURI %>/images/card.jpg' onClick="defaultSubmit()"/></h2>
<OBJECT type="application/x-informationCard" name="xmlToken">
          <PARAM Name="tokenType" Value="urn:oasis:names:tc:SAML:1.0:assertion">
          <PARAM Name="requiredClaims" 
Value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress">        
          <PARAM Name="optionalClaims"
Value="http://schemas.microsoft.com/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.microsoft.com/ws/2005/05/identity/claims/streetaddress http://schemas.microsoft.com/ws/2005/05/identity/claims/locality http://schemas.microsoft.com/ws/2005/05/identity/claims/stateorprovince http://schemas.microsoft.com/ws/2005/05/identity/claims/postalcode http://schemas.microsoft.com/ws/2005/05/identity/claims/country http://schemas.microsoft.com/ws/2005/05/identity/claims/homephone http://schemas.microsoft.com/ws/2005/05/identity/claims/otherphone http://schemas.microsoft.com/ws/2005/05/identity/claims/mobilephone http://schemas.microsoft.com/ws/2005/05/identity/claims/dateofbirth http://schemas.microsoft.com/ws/2005/05/identity/claims/gender">
</OBJECT>

<!--
-->

<script language="javascript">
    if (elmCount != null) {
	for (var i = 0; i < elmCount-1; i++) {
	    document.write(
		"<input name=\"IDToken" + i + "\" type=\"hidden\">");
	}
    document.write("<input name=\"IDButton"  + "\" type=\"hidden\">");	
    }
</script>
<input type="hidden" name="goto" value="<%= request.getParameter("goto") %>">
</auth:form>
</jato:content>
	 </td>
	</tr>
	<tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
	<tr>
            <td><img src="<%= ServiceURI %>/images/dot.gif" 
            width="1" height="33" alt="" /></td>
	    <td>&nbsp;</td>
	</tr>
        </table>      </td>
    </tr>
    </table>
    </td>
    </tr>
    <tr class="LogBotBnd">
      <td class="navigation">&nbsp;</td>
      <td><div class="logCpy"><span class="logTxtCpy">
        <auth:resBundle bundleName="amAuthUI" resourceKey="copyright.notice" /></span></div>
      </td>
    </tr>
  </table>


</body>

</jato:useViewBean>

</html>
