/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.net.URLEncoder;
/*     */ import java.rmi.server.UID;
/*     */ import java.util.Enumeration;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class SessionImpl
/*     */   implements Session, Runnable
/*     */ {
/*     */   protected String id;
/*     */   protected Hashtable attributeTable;
/*     */   protected GregorianCalendar creationTime;
/*     */   protected GregorianCalendar lastAccessedTime;
/*     */   protected SessionManager sessionManager;
/*     */   protected int flushingDelay;
/*     */   protected Thread sessionCleaner;
/*     */ 
/*     */   public static String generateSessionId()
/*     */   {
/*     */     try
/*     */     {
/*  55 */       return URLEncoder.encode(new UID().toString(), "UTF-8"); } catch (Exception ex) {
/*     */     }
/*  57 */     return String.valueOf(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   public SessionImpl()
/*     */   {
/*  63 */     this(null, generateSessionId());
/*     */   }
/*     */ 
/*     */   public SessionImpl(SessionManager mgr)
/*     */   {
/*  68 */     this(mgr, generateSessionId());
/*     */   }
/*     */ 
/*     */   public SessionImpl(String id)
/*     */   {
/*  73 */     this(null, id);
/*     */   }
/*     */ 
/*     */   public SessionImpl(SessionManager mgr, String id)
/*     */   {
/*  79 */     this.sessionManager = mgr;
/*  80 */     this.id = id;
/*  81 */     this.attributeTable = new Hashtable();
/*  82 */     this.creationTime = new GregorianCalendar();
/*  83 */     this.lastAccessedTime = new GregorianCalendar();
/*  84 */     this.lastAccessedTime.add(14, -100);
/*     */   }
/*     */ 
/*     */   public String getId() {
/*  88 */     return this.id;
/*     */   }
/*     */ 
/*     */   public GregorianCalendar getCreationTime()
/*     */   {
/*  93 */     return this.creationTime;
/*     */   }
/*     */ 
/*     */   public GregorianCalendar getLastAccessedTime() {
/*  97 */     return this.lastAccessedTime;
/*     */   }
/*     */ 
/*     */   public synchronized void setLastAccessedTime(GregorianCalendar time)
/*     */   {
/* 103 */     if (time.after(this.lastAccessedTime))
/* 104 */       this.lastAccessedTime = time;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String name)
/*     */   {
/* 110 */     setLastAccessedTime(new GregorianCalendar());
/* 111 */     return this.attributeTable.get(name);
/*     */   }
/*     */ 
/*     */   public Enumeration getAttributeNames()
/*     */   {
/* 116 */     return this.attributeTable.keys();
/*     */   }
/*     */ 
/*     */   public synchronized void setAttribute(String name, Object value)
/*     */   {
/* 122 */     if (value != null) {
/* 123 */       this.attributeTable.put(name, value);
/* 124 */       setLastAccessedTime(new GregorianCalendar());
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void removeAttribute(String name)
/*     */   {
/* 130 */     this.attributeTable.remove(name);
/* 131 */     setLastAccessedTime(new GregorianCalendar());
/*     */   }
/*     */ 
/*     */   public void setFlushingDelay(int delay)
/*     */   {
/* 137 */     if (delay > 0) {
/* 138 */       this.flushingDelay = delay;
/*     */ 
/* 140 */       if (this.sessionCleaner == null) {
/* 141 */         this.sessionCleaner = new Thread(this);
/* 142 */         this.sessionCleaner.start();
/*     */       }
/* 144 */       else if (Thread.interrupted()) {
/* 145 */         this.sessionCleaner.start();
/*     */       }
/*     */     }
/*     */     else {
/* 149 */       this.sessionCleaner.interrupt();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getFlushingDelay()
/*     */   {
/* 155 */     return this.flushingDelay;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/* 169 */         label0: Thread.sleep(this.flushingDelay * 1000);
/*     */       }
/*     */       catch (Exception localException) {
/*     */       }
/* 173 */       GregorianCalendar current = 
/* 174 */         (GregorianCalendar)GregorianCalendar.getInstance();
/*     */ 
/* 176 */       GregorianCalendar sessionLastAccess = 
/* 177 */         (GregorianCalendar)getLastAccessedTime().clone();
/*     */ 
/* 179 */       sessionLastAccess.add(13, this.flushingDelay);
/*     */ 
/* 181 */       if ((sessionLastAccess.before(current)) && 
/* 182 */         (this.sessionManager != null));
/* 183 */       this.sessionManager.removeSession(this, getId());
/* 184 */       return;
/*     */ 
///* 166 */       break label0;
/*     */     }
/*     */     catch (Exception localException1)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.SessionImpl
 * JD-Core Version:    0.5.4
 */