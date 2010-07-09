package com.utils.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmldap.util.PropertiesManager;

import com.awl.rd.fc2.CreateInfoCard;
import com.awl.rd.fc2.claims.CompositeSupportedClaims;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.storage.FactoryCardStorage;

/**
 * Servlet implementation class GetCRD
 */
public class GetCRD extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCRD() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userName = request.getParameter("USERNAME");
		String password = request.getParameter("PASSWORD");
		String method = request.getParameter("AUTH");
		if(method==null){
			System.out.println("METHOD NULL, PUT PWD");
			method = CreateInfoCard.METHOD_PWD;
		}else{
			System.out.println("METHOD : " + method);
		}
		System.out.println("U["+userName+"]-PWD["+password+"]");
		PropertiesManager properties = PropertiesManager.getInstance();
		 String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
         if (supportedClaimsClass == null) {
         	throw new ServletException("supportedClaimsClass is null");
         }
         //SupportedClaims supportedClaimsImpl;
		try {
			//response.getWriter().print("U["+userName+"]-PWD["+password+"]");
			//supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
			if(FactoryCardStorage.getCardStorage(new CompositeSupportedClaims()).authenticate(userName, password)){
				//response.getWriter().print("authentication succeed");				
				response.setContentType("application/x-informationcard; charset=utf-8");
				response.getOutputStream().write(CreateInfoCard.getCRD(userName,0,method).getBytes());
				return;
			}else{
				response.getWriter().print("<H2>Authentication FAILED</H2>");
			}
			
		} catch (CardNotFoundExecption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
