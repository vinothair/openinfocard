/*
 *                             Sun Public License
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License").  You may not use this file except in compliance with
 * the License.  A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the SLAMD Distributed Load Generation Engine.
 * The Initial Developer of the Original Code is Neil A. Wilson.
 * Portions created by Neil A. Wilson are Copyright (C) 2004-2006.
 * Some preexisting portions Copyright (C) 2002-2006 Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):  Neil A. Wilson
 */
package com.sun.slamd.example;


import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.X509Certificate;


/**
 * This class provides an implementation of an SSL socket factory that will use
 * JSSE to create the SSL socket.  In addition, it will implement a trust
 * mechanism in such a way that it will blindly trust any certificate that the
 * server presents to it, regardless of what we might think is wrong with it.
 *
 * @author Neil A. Wilson
 */
public class BlindTrustSocketFactory extends SSLSocketFactory implements SecureProtocolSocketFactory, X509TrustManager {
    // Indicates whether debug mode will be enabled (will print a message to
    // standard error whenever any method is called).
    boolean debugMode;

    // The SSL context that will be used to manage all things SSL.
    SSLContext sslContext;

    // The SSL socket factory that will actually be used to create the sockets.
    SSLSocketFactory sslSocketFactory;


    /**
     * Creates a new instance of this LDAP socket factory.
     *
     * @throws Exception If a problem occurs while initializing this socket
     *                   factory.
     */
    public BlindTrustSocketFactory() throws Exception {
        this(false);
    }


    /**
     * Creates a new instance of this LDAP socket factory, optionally operating in
     * debug mode.
     *
     * @param debugMode Indicates whether to operate in debug mode.  If this is
     *                  enabled, a message will be printed to standard error
     *                  any time of of the methods of this class is called.
     * @throws Exception If a problem occurs while initializing this socket
     *                   factory.
     */
    public BlindTrustSocketFactory(boolean debugMode) throws Exception {
        this.debugMode = debugMode;

        // Indicate that we will be using JSSE for the SSL-based connections.
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        System.setProperty("java.protocol.handler.pkgs",
                "com.sun.net.ssl.internal.www.protocol");

        // Get the default SSL context.
        try {
            sslContext = SSLContext.getInstance("SSLv3");
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new Exception("Unable to initialize the SSL context:  " + nsae);
        }

        // Initialize the SSL context with our own trust manager (this class) to
        // use when determining whether to trust a client certificate.
        try {
            sslContext.init(null, new TrustManager[]{this}, null);
        }
        catch (KeyManagementException kme) {
            throw new Exception("Unable to regsiter a new trust manager with " +
                    "the SSL context:  " + kme);
        }

        // Get the socket factory to use when creating the certificates.
        sslSocketFactory = sslContext.getSocketFactory();

        // If we are in debug mode, indicate that the socket factory has been
        // created.
        if (debugMode) {
            System.err.println("New JSSEBlindTrustSocketFactory created");
        }
    }


    /**
     * Determines whether the provided client certificate should be trusted.  In
     * this case, the certificate will always be trusted.
     *
     * @param chain    The peer certificate chain.
     * @param authType The authentication type based on the client certificate.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // No implementation required.  If we don't throw an exception, then there
        // is no problem with the cert.
        if (debugMode) {
            System.err.println("checkClientTrusted() invoked");
        }
    }


    /**
     * Determines whether the provided server certificate should be trusted.  In
     * this case, the certificate will always be trusted.
     *
     * @param chain    The peer certificate chain.
     * @param authType The authentication type based on the server certificate.
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        // No implementation required.  If we don't throw an exception, then there
        // is no problem with the cert.
        if (debugMode) {
            System.err.println("checkServerTrusted() invoked");
        }
    }


    /**
     * Retrieves an array of CA certificates that are trusted for authenticating
     * peers.
     *
     * @return An empty array, because we don't care about any list of CAs.
     */
    public X509Certificate[] getAcceptedIssuers() {
        if (debugMode) {
            System.err.println("getAcceptedIssuers() invoked");
        }

        return new X509Certificate[0];
    }


    /**
     * Establishes an SSL socket to the provided host and port that can be used by
     * the LDAP SDK for Java for communicating with an LDAP directory server.
     *
     * @param host The address of the server to which the connection is to be
     *             established.
     * @param port The port number of the server to which the connection is to
     *             be established.
     * @return The SSL socket that may be used for communicating with the
     *         directory server.
     * @throws Exception If a problem occurs while trying to establish the
     *                   connection.
     */
    public Socket makeSocket(String host, int port)
            throws Exception {
        if (debugMode) {
            System.err.println("makeSocket(" + host + "," + port + ") invoked");
        }

        try {
            return sslSocketFactory.createSocket(host, port);
        }
        catch (Exception e) {
            throw new Exception("Unable to establish the SSL connection:  " + e);
        }
    }


    /**
     * Creates a new SSL socket connected to the specified host and port.
     *
     * @param host The address of the system to which the SSL socket should be
     *             connected.
     * @param port The port on the target system to which the SSL socket should
     *             be connected.
     * @throws IOException If a problem occurs while creating the SSL socket.
     */
    public Socket createSocket(String host, int port)
            throws IOException {
        return sslSocketFactory.createSocket(host, port);
    }


    /**
     * Creates a new SSL socket connected to the specified host and port.
     *
     * @param host      The address of the system to which the SSL socket should
     *                  be connected.
     * @param port      The port on the target system to which the SSL socket
     *                  should be connected.
     * @param localHost The address on the local system from which the socket
     *                  should originate.
     * @param localPort The port on the local system from which the socket
     *                  should originate.
     * @throws IOException If a problem occurs while creating the SSL socket.
     */
    public Socket createSocket(String host, int port, InetAddress localHost,
                               int localPort)
            throws IOException {
        return sslSocketFactory.createSocket(host, port, localHost, localPort);
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort, HttpConnectionParams httpConnectionParams) throws IOException, UnknownHostException, ConnectTimeoutException {

        return sslSocketFactory.createSocket(host, port, localHost, localPort);

    }


    /**
     * Creates a new SSL socket connected to the specified host and port.
     *
     * @param host The address of the system to which the SSL socket should be
     *             connected.
     * @param port The port on the target system to which the SSL socket should
     *             be connected.
     * @throws IOException If a problem occurs while creating the SSL socket.
     */
    public Socket createSocket(InetAddress host, int port)
            throws IOException {
        return sslSocketFactory.createSocket(host, port);
    }


    /**
     * Creates a new SSL socket connected to the specified host and port.
     *
     * @param host         The address of the system to which the SSL socket should
     *                     be connected.
     * @param port         The port on the target system to which the SSL socket
     *                     should be connected.
     * @param localAddress The address on the local system from which the socket
     *                     should originate.
     * @param localPort    The port on the local system from which the socket
     *                     should originate.
     * @throws IOException If a problem occurs while creating the SSL socket.
     */
    public Socket createSocket(InetAddress host, int port,
                               InetAddress localAddress, int localPort)
            throws IOException {
        return sslSocketFactory.createSocket(host, port, localAddress, localPort);
    }


    /**
     * Converts the provided socket to an SSL socket using this socket factory.
     *
     * @param socket    The socket to convert to an SSL socket.
     * @param host      The host to which the socket is connected.
     * @param port      The port to which the socket is connected.
     * @param autoClose Indicates whether the underlying socket should be closed
     *                  when the returned SSL socket is closed.
     * @throws IOException If a problem occurs while creating the SSL socket.
     */
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose)
            throws IOException {
        return sslSocketFactory.createSocket(socket, host, port, autoClose);
    }


    /**
     * Retrieves the set of cipher suites that are enabled by default.
     *
     * @return The set of cipher suites that are enabled by default.
     */
    public String[] getDefaultCipherSuites() {
        return sslSocketFactory.getDefaultCipherSuites();
    }


    /**
     * Retrieves the set of cipher suites that can be used to create SSL sockets.
     *
     * @return The set of cipher suites that can be used to create SSL sockets.
     */
    public String[] getSupportedCipherSuites() {
        return sslSocketFactory.getSupportedCipherSuites();
  }
}

