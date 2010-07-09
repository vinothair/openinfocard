package com.utils;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmldap.exceptions.CryptoException;

import com.awl.rd.fc2.data.connectors.DataConnector;
import com.utils.impl.STSConfiguration_Wallet;

/**
 * Servlet implementation class CreateAccountServlet
 */
public class CreateAccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateAccountServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		
		if (request.getParameter("username") == null || request.getParameter("username").equals("")) out.write("Saisissez un username SVP"); 
		
		else if (request.getParameter("password") == null || request.getParameter("password").equals("")) out.write("Saisissez un mot de passe SVP");
		
		else {
			STSConfiguration_Wallet wallet = new STSConfiguration_Wallet();
			wallet.configure();
			
			try {
				wallet.createAccountForUserId(request.getParameter("username"), request.getParameter("password"));				
			} catch (CryptoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			DataConnector.getInstance().save();
						
			out.write("done.");
		}		
				
	}

}
