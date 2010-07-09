package com.awl.rd.applications.common.message.types;

import java.util.Arrays;
import java.util.Vector;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Message_CSP implements IMessage {

	public final static String CMD_SELECT_CERTIFICATE = "SELECT";
	public final static String CMD_REQUEST_SIGNATURE_XML = "SIGN_XML";
	public final static String CMD_REQUEST_SIGNATURE_DATA = "SIGN_DATA";
	public final static String CMD_UNKNOWN = "UNKNOWN";
	private String cmd;
	
	public final static int UNKNOWN = -1;
	public final static int CERTIFICATE_OK = 0;
	public final static int CERTIFICATE_KO = 1;
	public final static int SIGNATURE_OK = 2;
	public final static int SIGNATURE_KO = 3;
	
	private int state;
	
	private String issuerDN ="";
	private String userDN="";
	private int[] usages = new int[]{};
	
	private String certificate;
	private String data;
	private String signedData;
	
	public Message_CSP() {
		this.state = UNKNOWN;
		this.cmd = CMD_UNKNOWN;
		
		this.certificate = "";
		this.data = "";
		this.signedData = "";
	}
	
	
	public String toString(){
		return this.getClass().getName();
	}
	
	@Override
	public String toXML() {
		return "<MESSAGE>" +
			"<TYPE>Message_CSP</TYPE>" +
				"<CMD>"+this.cmd+"</CMD>"+
				"<CERTIFICATE_REQUEST>" +
					"<USERDN>"+this.userDN+"</USERDN>" +
					"<ISSUERDN>"+this.issuerDN+"</ISSUERDN>" +
					"<USAGES>"+Arrays.toString(this.usages)+"</USAGES>" +
				"</CERTIFICATE_REQUEST>" +
				"<CERTIFICATE_B64>"+this.certificate+"</CERTIFICATE_B64>" +
				"<DATA>"+this.data+"</DATA>" +
				"<SIGNED_DATA>"+this.signedData+"</SIGNED_DATA>" +
				"<STATE>"+this.state+"</STATE>"+
			"</MESSAGE>";
	}
	
	@Override
	public void constructFromXML(String xml) throws Message_ExceptionUnableToConvertMessage {
		XMLParser parser = new XMLParser(xml);
		try {
			this.userDN = parser.getFirstValue("USERDN");
			this.issuerDN = parser.getFirstValue("ISSUERDN");
			
			int[] tab = new int[]{};
			String a = parser.getFirstValue("USAGES");
			String b = a.substring(a.indexOf("[")+1, a.lastIndexOf("]"));
			if(b.length()>0){
				String[] c = b.split(",");	
				Vector<Integer> tmpValues = new Vector<Integer>();
 				for(int i=0; i<c.length; i++){
 					//this.usages[i] = Integer.parseInt(c[i].trim());
 					try {
						tmpValues.add(Integer.valueOf(c[i].trim()));
					} catch (Exception e) {
						// TODO: handle exception
					}
 				}
 				this.usages = new int[tmpValues.size()];
				for(int i=0; i<tmpValues.size(); i++){
					this.usages[i] = tmpValues.get(i);
					System.out.println(this.usages[i]);
				}
			}
			
//			m_strAPDUInHex = parser.getFirstValue("APDU");
//			m_strSWInHex = parser.getFirstValue("SW");
//			m_strDataInHex = parser.getFirstValue("DATA");
//			m_iCCCode = parser.getFirstValue("CODE");
//			m_strCCControl = parser.getFirstValue("CONTROL");
			this.certificate = parser.getFirstValue("CERTIFICATE_B64");
			this.data = parser.getFirstValue("DATA");
			this.state = Integer.valueOf(parser.getFirstValue("STATE"));
			this.cmd = parser.getFirstValue("CMD");
			this.signedData = parser.getFirstValue("SIGNED_DATA");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Message_ExceptionUnableToConvertMessage(e.getMessage()));
		}
		
	}

	public void setCmd(String cmd){
		this.cmd = cmd;
	}
	public String getCmd(){
		return cmd;
	}
		
	public void setState(int state){
		this.state = state;
	}
	public int getState(){
		return state;
	}
	
	public String getIssuerDN() {
		return issuerDN;
	}
	public void setIssuerDN(String issuerDN) {
		this.issuerDN = issuerDN;
	}

	public String getUserDN() {
		return userDN;
	}
	public void setUserDN(String userDN) {
		this.userDN = userDN;
	}

	public int[] getUsages() {
		return usages;
	}
	public void setUsages(int[] usages) {
		this.usages = usages;
	}
	
	public static void main(String[] args) {
		Message_CSP msg = new Message_CSP();
		msg.setUsages(new int[]{1,3, 55});
		System.out.println(msg.toXML());
		try {
			msg.constructFromXML(msg.toXML());
		} catch (Message_ExceptionUnableToConvertMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	
	
	public String getSignedData() {
		return signedData;
	}
	public void setSignedData(String signedDate) {
		this.signedData = signedDate;
	}


	public void setCertificate(String certificateB64) {
		this.certificate = certificateB64;
	}
	public String getCertificate() {
		return this.certificate;
	}
}
