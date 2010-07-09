/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.KeyStore;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ public class HttpClient
/*     */   implements X509TrustManager, HostnameVerifier, Runnable
/*     */ {
/*  62 */   private static String HTTP_PROXYSET = "http.proxySet";
/*  63 */   private static String HTTP_PROXYHOST = "http.proxyHost";
/*  64 */   private static String HTTP_PROXYPORT = "http.proxyPort";
/*  65 */   private static String HTTPS_PROXYSET = "https.proxySet";
/*  66 */   private static String HTTPS_PROXYHOST = "https.proxyHost";
/*  67 */   private static String HTTPS_PROXYPORT = "https.proxyPort";
/*     */ 
/*  69 */   private static String TRUE = "true";
/*  70 */   private static String FALSE = "false";
/*  71 */   private static String VIDE = "";
/*     */   private String oldHttpProxySet;
/*     */   private String oldHttpProxyHost;
/*     */   private String oldHttpProxyPort;
/*     */   private String oldHttpsProxySet;
/*     */   private String oldHttpsProxyHost;
/*     */   private String oldHttpsProxyPort;
/*     */   private boolean sslv3Auth;
/*     */   private boolean pwdAuth;
/*     */   private String login;
/*     */   private boolean hasTimedOut;
/*     */   private String operation;
/*     */   private String[] params;
/*     */   private String result;
/*     */   private String requestMethod;
/*     */   private String contentType;
/*     */   private String encoding;
/*     */   private SSLContext ctx;
/*     */   private KeyManagerFactory kmf;
/*     */   private TrustManager[] myTM;
/*     */   private KeyStore clientKS;
/*     */   private KeyStore serverKS;
/*     */   private URLConnection myConnection;
/*     */   private SSLSocketFactory myFactory;
/*     */ 
/*     */   public HttpClient()
/*     */     throws Exception
/*     */   {
/* 106 */     this.ctx = null;
/* 107 */     this.kmf = null;
/* 108 */     this.clientKS = null;
/* 109 */     this.myConnection = null;
/* 110 */     this.operation = null;
/* 111 */     this.params = null;
/* 112 */     init();
/*     */   }
/*     */ 
/*     */   public void init()
/*     */     throws Exception
/*     */   {
/* 119 */     this.sslv3Auth = false;
/* 120 */     this.pwdAuth = false;
/* 121 */     this.requestMethod = "POST";
/* 122 */     this.contentType = "application/xml; charset=utf-8";
/* 123 */     this.encoding = "iso-8859-1";
/* 124 */     initTrustManager();
/* 125 */     this.ctx = SSLContext.getInstance("SSL");
/* 126 */     this.ctx.init(null, this.myTM, null);
/* 127 */     this.myFactory = this.ctx.getSocketFactory();
/*     */   }
/*     */ 
/*     */   public void setProxy(String host, String port)
/*     */   {
/* 140 */     this.oldHttpProxySet = System.getProperty(HTTP_PROXYSET);
/* 141 */     this.oldHttpProxyHost = System.getProperty(HTTP_PROXYHOST);
/* 142 */     this.oldHttpProxyPort = System.getProperty(HTTP_PROXYPORT);
/* 143 */     this.oldHttpsProxySet = System.getProperty(HTTPS_PROXYSET);
/* 144 */     this.oldHttpsProxyHost = System.getProperty(HTTPS_PROXYHOST);
/* 145 */     this.oldHttpsProxyPort = System.getProperty(HTTPS_PROXYPORT);
/* 146 */     if ((host == null) || (port == null)) {
/* 147 */       System.setProperty(HTTP_PROXYSET, FALSE);
/* 148 */       System.setProperty(HTTP_PROXYHOST, VIDE);
/* 149 */       System.setProperty(HTTP_PROXYPORT, VIDE);
/* 150 */       System.setProperty(HTTPS_PROXYSET, FALSE);
/* 151 */       System.setProperty(HTTPS_PROXYHOST, VIDE);
/* 152 */       System.setProperty(HTTPS_PROXYPORT, VIDE);
/*     */     } else {
/* 154 */       System.setProperty(HTTP_PROXYSET, TRUE);
/* 155 */       System.setProperty(HTTP_PROXYHOST, host);
/* 156 */       System.setProperty(HTTP_PROXYPORT, port);
/* 157 */       System.setProperty(HTTPS_PROXYSET, TRUE);
/* 158 */       System.setProperty(HTTPS_PROXYHOST, host);
/* 159 */       System.setProperty(HTTPS_PROXYPORT, port);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetProxy()
/*     */   {
/* 167 */     if (this.oldHttpProxySet != null) {
/* 168 */       System.setProperty(HTTP_PROXYSET, this.oldHttpProxySet);
/* 169 */       System.setProperty(HTTP_PROXYHOST, this.oldHttpProxyHost);
/* 170 */       System.setProperty(HTTP_PROXYPORT, this.oldHttpProxyPort);
/*     */     }
/* 172 */     if (this.oldHttpsProxySet != null) {
/* 173 */       System.setProperty(HTTPS_PROXYSET, this.oldHttpsProxySet);
/* 174 */       System.setProperty(HTTPS_PROXYHOST, this.oldHttpsProxyHost);
/* 175 */       System.setProperty(HTTPS_PROXYPORT, this.oldHttpsProxyPort);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setRequestMethod(String requestMethod)
/*     */   {
/* 185 */     this.requestMethod = requestMethod;
/*     */   }
/*     */ 
/*     */   public void setContentType(String contentType)
/*     */   {
/* 194 */     this.contentType = contentType;
/*     */   }
/*     */ 
/*     */   public void setEncoding(String encoding)
/*     */   {
/* 203 */     this.encoding = encoding;
/*     */   }
/*     */ 
/*     */   public void loadClientCertificate(String filename, String passphrase)
/*     */     throws Exception
/*     */   {
/* 214 */     char[] password = passphrase.toCharArray();
/* 215 */     this.kmf = KeyManagerFactory.getInstance("SunX509");
/* 216 */     this.clientKS = KeyStore.getInstance("PKCS12");
/*     */ 
/* 218 */     FileInputStream fis = new FileInputStream(filename);
/* 219 */     this.clientKS.load(fis, password);
/* 220 */     this.kmf.init(this.clientKS, password);
/*     */   }
/*     */ 
/*     */   public void loadServerCertificate(String filename)
/*     */     throws Exception
/*     */   {
/* 229 */     this.serverKS = KeyStore.getInstance("JKS");
/* 230 */     this.serverKS.load(null, null);
/* 231 */     CertificateFactory cf = 
/* 232 */       CertificateFactory.getInstance("X.509");
/* 233 */     Certificate serverCert = cf.generateCertificate(
/* 234 */       new FileInputStream(filename));
/* 235 */     this.serverKS.setCertificateEntry("server", serverCert);
/*     */   }
/*     */ 
/*     */   public void setSSLv3Auth()
/*     */     throws Exception
/*     */   {
/* 242 */     this.sslv3Auth = true;
/*     */ 
/* 245 */     this.ctx = SSLContext.getInstance("SSLv3");
/*     */ 
/* 248 */     initTrustManager();
/*     */ 
/* 253 */     this.ctx.init(this.kmf.getKeyManagers(), this.myTM, null);
/*     */ 
/* 255 */     this.myFactory = this.ctx.getSocketFactory();
/*     */   }
/*     */ 
/*     */   private void initTrustManager()
/*     */   {
/* 263 */     this.myTM = new TrustManager[] { this };
/*     */   }
/*     */ 
/*     */   private void initTrustManager(KeyStore ks) throws Exception {
/* 267 */     TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
/* 268 */     tmf.init(ks);
/* 269 */     this.myTM = tmf.getTrustManagers();
/*     */   }
/*     */ 
/*     */   public void setPwdAuth(String user, String pwd)
/*     */   {
/* 279 */     this.pwdAuth = true;
/* 280 */     this.login = (user + ":" + pwd);
/* 281 */     this.login = Base64.encodeBuffer(this.login.getBytes());
/*     */   }
/*     */ 
/*     */   public void openConnection(String url)
/*     */     throws Exception
/*     */   {
/* 291 */     URL myURL = new URL(url);
/* 292 */     this.myConnection = myURL.openConnection();
/*     */ 
/* 294 */     if ((myURL.getProtocol().equalsIgnoreCase("https")) || (this.sslv3Auth))
/*     */     {
/* 296 */       ((HttpsURLConnection)this.myConnection).setSSLSocketFactory(this.myFactory);
/* 297 */       ((HttpsURLConnection)this.myConnection).setHostnameVerifier(this);
/*     */     }
/*     */ 
/* 300 */     if (this.pwdAuth) {
/* 301 */       this.myConnection.setRequestProperty("Authorization", "Basic " + this.login);
/*     */     }
/*     */ 
/* 304 */     this.myConnection.setRequestProperty("Content-encoding", this.encoding);
/* 305 */     this.myConnection.setRequestProperty("Content-type", this.contentType);
/*     */     try
/*     */     {
/* 308 */       ((HttpURLConnection)this.myConnection).setRequestMethod(this.requestMethod);
/*     */     } catch (Exception localException) {
/*     */     }
/* 311 */     this.myConnection.setDoOutput(true);
/* 312 */     this.myConnection.setDoInput(true);
/*     */   }
/*     */ 
/*     */   public void openConnection(String url, long millis)
/*     */     throws Exception
/*     */   {
/* 322 */     String[] parameters = { url };
/* 323 */     setTimeOut("openConnection", parameters, millis);
/*     */   }
/*     */ 
/*     */   public String getHttpResponse()
/*     */     throws Exception
/*     */   {
/* 332 */     StringWriter sw = new StringWriter();
/* 333 */     BufferedReader in = new BufferedReader(new InputStreamReader(this.myConnection.getInputStream(), "UTF-8"));
/*     */ 
/* 335 */     String line = null;
/* 336 */     while ((line = in.readLine()) != null)
/*     */     {
/* 338 */       sw.write(line + "\n");
/* 339 */       sw.flush();
/*     */     }
/*     */     try
/*     */     {
/* 343 */       ((HttpURLConnection)this.myConnection).disconnect();
/*     */     } catch (Exception localException) {
/*     */     }
/* 346 */     return sw.toString();
/*     */   }
/*     */ 
/*     */   public String getHttpResponse(long millis)
/*     */     throws Exception
/*     */   {
/* 357 */     String response = setTimeOut("getHttpResponse", null, millis);
/* 358 */     return response;
/*     */   }
/*     */ 
/*     */   public OutputStreamWriter getOutputStreamWriter()
/*     */     throws Exception
/*     */   {
/* 367 */     return new OutputStreamWriter(
/* 368 */       this.myConnection.getOutputStream(), this.encoding);
/*     */   }
/*     */ 
/*     */   private String setTimeOut(String operation, String[] params, long millis)
/*     */     throws Exception
/*     */   {
/* 380 */     this.operation = operation;
/* 381 */     this.params = params;
/* 382 */     this.hasTimedOut = true;
/*     */ 
/* 385 */     Thread t = new Thread(this);
/* 386 */     t.setDaemon(true);
/* 387 */     t.start();
/*     */     try
/*     */     {
/* 392 */       t.join(millis);
/*     */     } catch (InterruptedException ie) {
/* 394 */       t.interrupt();
/*     */     }
/* 396 */     if (this.hasTimedOut) {
/* 397 */       throw new Exception(operation + " has timed out.");
/*     */     }
/* 399 */     return this.result;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 407 */       if (this.operation.equals("openConnection"))
/* 408 */         openConnection(this.params[0]);
/* 409 */       else if (this.operation.equals("getHttpResponse"))
/* 410 */         this.result = getHttpResponse();
/*     */     }
/*     */     catch (Exception e) {
/* 413 */       throw new RuntimeException(e);
/*     */     }
/* 415 */     this.hasTimedOut = false;
/*     */   }
/*     */ 
/*     */   public boolean verify(String p0, SSLSession s)
/*     */   {
/* 422 */     return true;
/*     */   }
/*     */ 
/*     */   public void checkClientTrusted(X509Certificate[] p0, String authType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void checkServerTrusted(X509Certificate[] p0, String authType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public X509Certificate[] getAcceptedIssuers()
/*     */   {
/* 441 */     return new X509Certificate[0];
/*     */   }
/*     */ 
/*     */   private static void test_1()
/*     */     throws Exception
/*     */   {
/* 447 */     HttpClient client = new HttpClient();
/* 448 */     String response = null;
/*     */ 
/* 450 */     System.out.println("Step 1");
/* 451 */     client.openConnection(
/* 452 */       "file:///C:/users/lst/dev/2004/mpi/var/Valid_VERes_01.xml");
/*     */ 
/* 454 */     System.out.println("Step 2");
/*     */     try {
/* 456 */       response = client.getHttpResponse(3000L);
/*     */     } catch (Exception e) {
/* 458 */       throw new Exception("Cannot get response from connection :" + e);
/*     */     }
/*     */ 
/* 461 */     System.out.println("Step 3");
/* 462 */     System.out.println(client.result);
/* 463 */     if (client.hasTimedOut)
/* 464 */       System.out.println("Connection timed out");
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 472 */     test_1();
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.HttpClient
 * JD-Core Version:    0.5.4
 */