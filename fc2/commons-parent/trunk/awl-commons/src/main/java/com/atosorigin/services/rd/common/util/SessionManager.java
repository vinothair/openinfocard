/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.URLEncoder;
/*     */ import java.rmi.server.UID;
/*     */ import java.util.Enumeration;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Hashtable;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Vector;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public final class SessionManager
/*     */   implements Runnable
/*     */ {
/*  50 */   private static String SESSION_BUNDLE = "com.atosorigin.services.rd.virtualcard.session.SessionConfiguration";
/*  51 */   private static String SESSION_FLUSHING = "session_flushing";
/*  52 */   private static int DEFAULT_SESSION_FLUSHING = 180;
/*  53 */   private static String SESSION_MAX_NUMBER = "session_max_number";
/*  54 */   private static int DEFAULT_SESSION_MAX_NUMBER = 100;
/*  55 */   private static String SESSION_CLASS = "session_class";
/*  56 */   private static String DEFAULT_SESSION_CLASS = "com.atosorigin.services.rd.common.util.SessionImpl";
/*  57 */   private static String UTF8 = "UTF-8";
/*     */ 
/*  62 */   private static Hashtable managerTable = new Hashtable();
/*     */   private Hashtable sessionTable;
/*     */   private Vector listenerVector;
/*     */   private String id;
/*     */   private int sessionCounter;
/*     */   private int sessionMaxCounter;
/*     */   private int flushingDelay;
/*     */   private String sessionClass;
/*     */   private Logger logger;
/*     */   protected Thread sessionCleaner;
/*     */ 
/*     */   public static String generateSessionId()
/*     */   {
/*     */     try
/*     */     {
/*  85 */       return URLEncoder.encode(new UID().toString(), UTF8); } catch (Exception e) {
/*     */     }
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */   public static SessionManager getInstance()
/*     */   {
/*  96 */     String id = "";
/*     */     try {
/*  98 */       id = URLEncoder.encode(new UID().toString(), UTF8);
/*     */     } catch (Exception localException) {
/*     */     }
/* 101 */     return getNamedInstance(id);
/*     */   }
/*     */ 
/*     */   public static SessionManager getNamedInstance(String id)
/*     */   {
/* 106 */     if (managerTable.containsKey(id)) {
/* 107 */       return (SessionManager)managerTable.get(id);
/*     */     }
/*     */ 
/* 110 */     SessionManager manager = new SessionManager(id);
/* 111 */     managerTable.put(id, manager);
/* 112 */     return manager;
/*     */   }
/*     */ 
/*     */   private SessionManager(String id)
/*     */   {
/* 124 */     this.id = id;
/* 125 */     this.sessionTable = new Hashtable();
/* 126 */     this.sessionCounter = 0;
/* 127 */     this.flushingDelay = DEFAULT_SESSION_FLUSHING;
/* 128 */     this.sessionMaxCounter = DEFAULT_SESSION_MAX_NUMBER;
/* 129 */     this.sessionClass = DEFAULT_SESSION_CLASS;
/* 130 */     this.logger = Logger.getAnonymousLogger();
/*     */ 
/* 132 */     this.listenerVector = new Vector();
/*     */     try
/*     */     {
/* 136 */       ResourceBundle rb = ResourceBundle.getBundle(SESSION_BUNDLE);
/*     */ 
/* 138 */       this.flushingDelay = 
/* 139 */         Integer.valueOf(rb.getString(SESSION_FLUSHING)).intValue();
/*     */ 
/* 141 */       this.sessionMaxCounter = 
/* 142 */         Integer.valueOf(rb.getString(SESSION_MAX_NUMBER)).intValue();
/*     */ 
/* 144 */       this.sessionClass = rb.getString(SESSION_CLASS);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 149 */     if (this.flushingDelay > 0) {
/* 150 */       this.sessionCleaner = new Thread(this);
/* 151 */       this.sessionCleaner.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getID()
/*     */   {
/* 159 */     return this.id;
/*     */   }
/*     */ 
/*     */   public int getSessionNumber()
/*     */   {
/* 165 */     return this.sessionCounter;
/*     */   }
/*     */ 
/*     */   public int getSessionRatio()
/*     */   {
/* 172 */     return 100 * (this.sessionCounter / this.sessionMaxCounter);
/*     */   }
/*     */ 
/*     */   public void setSessionMaxNumber(int max)
/*     */   {
/* 181 */     this.sessionMaxCounter = max;
/*     */   }
/*     */ 
/*     */   public void setFlushingDelay(int delay)
/*     */   {
/* 187 */     this.logger.fine("Set flushing delay to " + String.valueOf(delay));
/*     */ 
/* 189 */     if (delay == 0) {
/* 190 */       this.sessionCleaner.interrupt();
/*     */     } else {
/* 192 */       this.flushingDelay = delay;
/* 193 */       if (Thread.interrupted())
/* 194 */         this.sessionCleaner.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLogger(Logger logger)
/*     */   {
/* 204 */     if (logger != null)
/* 205 */       this.logger = logger;
/*     */   }
/*     */ 
/*     */   public Session getSession(String Id)
/*     */   {
/* 218 */     Session session = (Session)this.sessionTable.get(Id);
/*     */ 
/* 221 */     if (session != null) {
/* 222 */       session.setLastAccessedTime(new GregorianCalendar());
/*     */     }
/*     */ 
/* 226 */     return session;
/*     */   }
/*     */ 
/*     */   public Session newNamedSession()
/*     */     throws Exception
/*     */   {
/* 234 */     String sessionId = null;
/* 235 */     String id = generateSessionId();
/* 236 */     Session session = newSession(generateSessionId());
/* 237 */     if (this.logger != null) {
/* 238 */       this.logger.fine("Creating session : " + id);
/*     */     }
/* 240 */     return session;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Session newSession()
/*     */     throws Exception
/*     */   {
/* 249 */     String sessionId = null;
/* 250 */     return newSession(sessionId);
/*     */   }
/*     */ 
/*     */   public Session newSession(String sessionId)
/*     */     throws Exception
/*     */   {
/* 260 */     this.logger.fine("Create a new Session object with ID : " + sessionId);
/*     */ 
/* 263 */     Session newSession = null;
/*     */     try
/*     */     {
/* 267 */       this.logger.finer("Session object class is : " + this.sessionClass);
/* 268 */       Class c = Class.forName(this.sessionClass);
/*     */ 
/* 270 */       if (sessionId == null)
/*     */       {
/* 273 */         Class[] paramType = new Class[1];
/* 274 */         paramType[0] = super.getClass();
/*     */ 
/* 276 */         Constructor sessionConstructor = c.getConstructor(paramType);
/*     */ 
/* 279 */         newSession = (Session)sessionConstructor.newInstance(new Object[] { this });
/*     */       }
/*     */       else
/*     */       {
/* 284 */         Class[] paramType = new Class[2];
/* 285 */         paramType[0] = super.getClass();
/* 286 */         paramType[1] = sessionId.getClass();
/*     */ 
/* 288 */         Constructor sessionConstructor = c.getConstructor(paramType);
/*     */ 
/* 291 */         newSession = (Session)sessionConstructor.newInstance(
/* 292 */           new Object[] { this, sessionId });
/*     */       }
/*     */ 
/* 296 */       if (this.sessionCounter > this.sessionMaxCounter) {
/* 297 */         throw new Exception("Max session number reached");
/*     */       }
/*     */ 
/* 300 */       if (!this.sessionTable.containsKey(newSession.getId())) {
/* 301 */         this.sessionCounter += 1;
/*     */       }
/*     */ 
/* 305 */       this.sessionTable.put(newSession.getId(), newSession);
/*     */     }
/*     */     catch (Exception e) {
/* 308 */       throw new Exception("Cannot instanciate " + this.sessionClass + 
/* 309 */         " (" + e + ")");
/*     */     }
/*     */ 
/* 313 */     SessionEvent ev = new SessionEvent(this, 
/* 314 */       newSession.getId(), 
/* 315 */       SessionEvent.LISTENERADDED_, 
/* 316 */       SessionEvent.WARNING_);
/*     */ 
/* 318 */     fireEvent(ev);
/*     */ 
/* 321 */     return newSession;
/*     */   }
/*     */ 
/*     */   public Enumeration getSessionIds()
/*     */   {
/* 330 */     return this.sessionTable.keys();
/*     */   }
/*     */ 
/*     */   public synchronized void removeSession(String sessionId)
/*     */   {
/* 337 */     removeSession(this, sessionId);
/*     */   }
/*     */ 
/*     */   public synchronized void removeSession(Object source, String sessionId)
/*     */   {
/* 345 */     this.logger.fine("Remove session '" + sessionId + "' from the session table.");
/*     */ 
/* 348 */     if (!this.sessionTable.containsKey(sessionId)) return;
/*     */ 
/* 350 */     Session session = (Session)this.sessionTable.get(sessionId);
/* 351 */     this.sessionTable.remove(sessionId);
/* 352 */     this.sessionCounter -= 1;
/* 353 */     session = null;
/*     */ 
/* 355 */     SessionEvent ev = new SessionEvent(
/* 356 */       source, 
/* 357 */       sessionId, 
/* 358 */       SessionEvent.SESSIONREMOVED_, 
/* 359 */       SessionEvent.WARNING_);
/*     */ 
/* 361 */     fireEvent(ev);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 370 */     this.logger.finer("Cleaner started.");
/*     */     while (true)
/*     */     {
/*     */       try
/*     */       {
/* 376 */         this.logger.finest("Clean expired sessions...");
/*     */         try
/*     */         {
/* 379 */           Thread.sleep(this.flushingDelay * 1000);
/*     */         }
/*     */         catch (Exception localException1) {
/*     */         }
/* 383 */         GregorianCalendar current = 
/* 384 */           (GregorianCalendar)GregorianCalendar.getInstance();
/*     */         Enumeration sessions;
/* 388 */         synchronized (this.sessionTable) {
/* 389 */           sessions = this.sessionTable.elements();
/*     */         }
/*     */        // Enumeration sessions;
/* 393 */         while (sessions.hasMoreElements())
/*     */         {
/* 395 */           Session session = (Session)sessions.nextElement();
/*     */ 
/* 397 */           int sessionDelay = session.getFlushingDelay();
/*     */ 
/* 399 */           if (sessionDelay > 0)
/*     */             continue;
/* 401 */           GregorianCalendar sessionLastAccess = 
/* 402 */             (GregorianCalendar)session.getLastAccessedTime().clone();
/*     */ 
/* 404 */           sessionLastAccess.add(13, this.flushingDelay);
/*     */ 
/* 406 */           if (sessionLastAccess.before(current)) {
/* 407 */             String sid = session.getId();
/* 408 */             this.logger.finest("Session '" + sid + "' has expired.");
/* 409 */             removeSession(sid);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception er)
/*     */       {
/* 415 */         System.out.println("er : " + er);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addListener(SessionListener listener)
/*     */   {
/* 424 */     this.listenerVector.add(listener);
/*     */   }
/*     */ 
/*     */   public void removeListener(SessionListener listener)
/*     */   {
/* 432 */     this.listenerVector.remove(listener);
/* 433 */     listener.removingFromListenerList();
/*     */     try
/*     */     {
/* 436 */       SessionEvent ev = new SessionEvent(this, SessionEvent.UNKNOWN_, 
/* 437 */         SessionEvent.LISTENERREMOVED_, SessionEvent.WARNING_);
/* 438 */       listener.listen(ev);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAllListener() {
/* 446 */     Enumeration en = this.listenerVector.elements();
/* 447 */     while (en.hasMoreElements()) {
/* 448 */       SessionListener sl = (SessionListener)en.nextElement();
/* 449 */       removeListener(sl);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireEvent(SessionEvent event)
/*     */   {
/* 455 */     Enumeration en = this.listenerVector.elements();
/* 456 */     while (en.hasMoreElements()) {
/* 457 */       SessionListener sl = (SessionListener)en.nextElement();
/*     */       try { sl.listen(event); }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.SessionManager
 * JD-Core Version:    0.5.4
 */