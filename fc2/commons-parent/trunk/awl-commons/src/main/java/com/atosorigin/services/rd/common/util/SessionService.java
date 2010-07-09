/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class SessionService
/*     */ {
/*     */   public String setSession(String managerName, String sessionID, Hashtable data)
/*     */   {
/*  48 */     SessionManager mng = SessionManager.getNamedInstance(managerName);
/*  49 */     Session session = mng.getSession(sessionID);
/*  50 */     if (session == null)
/*     */       try {
/*  52 */         session = mng.newSession(sessionID);
/*     */       }
/*     */       catch (Exception localException) {
/*     */       }
/*  56 */     Enumeration en = data.keys();
/*  57 */     while (en.hasMoreElements()) {
/*  58 */       String k = (String)en.nextElement();
/*  59 */       session.setAttribute(k, (String)data.get(k));
/*     */     }
/*     */ 
/*  62 */     return session.getId();
/*     */   }
/*     */ 
/*     */   public boolean removeSession(String managerName, String sessionID)
/*     */   {
/*     */     try
/*     */     {
/*  75 */       SessionManager mng = SessionManager.getNamedInstance(managerName);
/*  76 */       mng.removeSession(sessionID);
/*  77 */       return true; } catch (Exception ex_) {
/*     */     }
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   public Hashtable get(String managerName, String sessionID)
/*     */   {
/*  92 */     SessionManager mng = SessionManager.getNamedInstance(managerName);
/*  93 */     Session session = mng.getSession(sessionID);
/*     */ 
/*  95 */     Hashtable result = new Hashtable();
/*     */ 
/*  97 */     Enumeration en = session.getAttributeNames();
/*  98 */     while (en.hasMoreElements()) {
/*  99 */       String k = (String)en.nextElement();
/* 100 */       result.put(k, (String)session.getAttribute(k));
/*     */     }
/*     */ 
/* 103 */     return result;
/*     */   }
/*     */ 
/*     */   public void resetManager(String managerName)
/*     */   {
/* 114 */     SessionManager mng = SessionManager.getNamedInstance(managerName);
/* 115 */     Enumeration en = mng.getSessionIds();
/* 116 */     while (en.hasMoreElements()) {
/* 117 */       String id = (String)en.nextElement();
/* 118 */       mng.removeSession(id);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.SessionService
 * JD-Core Version:    0.5.4
 */