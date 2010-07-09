/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.util.EventObject;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SessionEvent extends EventObject
/*     */ {
/*  34 */   public static String SESSIONREMOVED_ = "SESSION_REMOVED";
/*  35 */   public static String SESSIONADDED_ = "SESSION_ADDED";
/*  36 */   public static String SESSIONUPDATED_ = "SESSION_UPDATED";
/*  37 */   public static String LISTENERREMOVED_ = "LISTENER_REMOVED";
/*  38 */   public static String LISTENERADDED_ = "LISTENER_ADDED";
/*     */ 
/*  40 */   public static String CRITICAL_ = "CRITICAL";
/*  41 */   public static String SEVERE_ = "SEVERE";
/*  42 */   public static String WARNING_ = "WARNING";
/*  43 */   public static String UNKNOWN_ = "UNKNOWN";
/*     */ 
/*  46 */   private static String NAME = "com.atosorigin.services.rd.common.util.SessionEvent";
/*     */   protected String sessionID;
/*     */   protected GregorianCalendar eventUTCDate;
/*     */   protected String eventType;
/*     */   protected String eventCategory;
/*     */ 
/*     */   public SessionEvent(Object source, String sessionID, String eventType, String eventCategory)
/*     */   {
/*  55 */     super(source);
/*  56 */     this.sessionID = sessionID;
/*  57 */     this.eventUTCDate = new GregorianCalendar();
/*  58 */     this.eventType = eventType;
/*  59 */     this.eventCategory = eventCategory;
/*     */   }
/*     */ 
/*     */   public SessionEvent(Object source)
/*     */   {
/*  66 */     this(source, UNKNOWN_, UNKNOWN_, UNKNOWN_);
/*     */   }
/*     */ 
/*     */   public SessionEvent(Object source, String sessionID)
/*     */   {
/*  73 */     this(source, sessionID, UNKNOWN_, UNKNOWN_);
/*     */   }
/*     */ 
/*     */   public Vector getInfo()
/*     */   {
/*  81 */     Vector info = new Vector();
/*  82 */     info.add(NAME);
/*  83 */     info.add(this.sessionID);
/*  84 */     info.add(this.eventUTCDate);
/*  85 */     info.add(this.eventType);
/*  86 */     info.add(this.eventCategory);
/*  87 */     return info;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  94 */     return NAME;
/*     */   }
/*     */ 
/*     */   public String getSessionID()
/*     */   {
/*  99 */     return this.sessionID;
/*     */   }
/*     */ 
/*     */   public GregorianCalendar getDate()
/*     */   {
/* 104 */     return this.eventUTCDate;
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.SessionEvent
 * JD-Core Version:    0.5.4
 */