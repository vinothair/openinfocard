/*    */ package com.atosorigin.services.rd.common.util;
/*    */ 
/*    */ public class Proxy
/*    */ {
/* 29 */   private static String HTTP_PROXYSET = "http.proxySet";
/* 30 */   private static String HTTP_PROXYHOST = "http.proxyHost";
/* 31 */   private static String HTTP_PROXYPORT = "http.proxyPort";
/* 32 */   private static String HTTPS_PROXYSET = "https.proxySet";
/* 33 */   private static String HTTPS_PROXYHOST = "https.proxyHost";
/* 34 */   private static String HTTPS_PROXYPORT = "https.proxyPort";
/*    */ 
/* 36 */   private static String TRUE = "true";
/* 37 */   private static String FALSE = "false";
/* 38 */   private static String VIDE = "";
/*    */   private String oldHttpProxySet;
/*    */   private String oldHttpProxyHost;
/*    */   private String oldHttpProxyPort;
/*    */   private String oldHttpsProxySet;
/*    */   private String oldHttpsProxyHost;
/*    */   private String oldHttpsProxyPort;
/*    */ 
/*    */   public void setProxy(String host, String port)
/*    */   {
/* 62 */     this.oldHttpProxySet = System.getProperty(HTTP_PROXYSET);
/* 63 */     this.oldHttpProxyHost = System.getProperty(HTTP_PROXYHOST);
/* 64 */     this.oldHttpProxyPort = System.getProperty(HTTP_PROXYPORT);
/* 65 */     this.oldHttpsProxySet = System.getProperty(HTTPS_PROXYSET);
/* 66 */     this.oldHttpsProxyHost = System.getProperty(HTTPS_PROXYHOST);
/* 67 */     this.oldHttpsProxyPort = System.getProperty(HTTPS_PROXYPORT);
/*    */ 
/* 69 */     if ((host == null) || (port == null)) {
/* 70 */       System.setProperty(HTTP_PROXYSET, FALSE);
/* 71 */       System.setProperty(HTTP_PROXYHOST, VIDE);
/* 72 */       System.setProperty(HTTP_PROXYPORT, VIDE);
/* 73 */       System.setProperty(HTTPS_PROXYSET, FALSE);
/* 74 */       System.setProperty(HTTPS_PROXYHOST, VIDE);
/* 75 */       System.setProperty(HTTPS_PROXYPORT, VIDE);
/*    */     } else {
/* 77 */       System.setProperty(HTTP_PROXYSET, TRUE);
/* 78 */       System.setProperty(HTTP_PROXYHOST, host);
/* 79 */       System.setProperty(HTTP_PROXYPORT, port);
/* 80 */       System.setProperty(HTTPS_PROXYSET, TRUE);
/* 81 */       System.setProperty(HTTPS_PROXYHOST, host);
/* 82 */       System.setProperty(HTTPS_PROXYPORT, port);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void resetProxy()
/*    */   {
/* 91 */     if (this.oldHttpProxySet != null) {
/* 92 */       System.setProperty(HTTP_PROXYSET, this.oldHttpProxySet);
/* 93 */       System.setProperty(HTTP_PROXYHOST, this.oldHttpProxyHost);
/* 94 */       System.setProperty(HTTP_PROXYPORT, this.oldHttpProxyPort);
/*    */     }
/* 96 */     if (this.oldHttpsProxySet != null) {
/* 97 */       System.setProperty(HTTPS_PROXYSET, this.oldHttpsProxySet);
/* 98 */       System.setProperty(HTTPS_PROXYHOST, this.oldHttpsProxyHost);
/* 99 */       System.setProperty(HTTPS_PROXYPORT, this.oldHttpsProxyPort);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.Proxy
 * JD-Core Version:    0.5.4
 */