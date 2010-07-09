/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.GregorianCalendar;
/*     */ 
/*     */ public class SessionTest
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  30 */     test4();
/*     */   }
/*     */ 
/*     */   private static void test1() throws Exception {
/*  34 */     SessionManager manager = SessionManager.getInstance();
/*  35 */     Session session1 = manager.newSession(SessionManager.generateSessionId());
/*  36 */     System.out.println("id : " + session1.getId());
/*  37 */     System.out.println(session1.getLastAccessedTime().getTime());
/*     */     try { Thread.sleep(2000L); } catch (Exception localException) {
/*  39 */     }session1.setAttribute("key1", "Val1");
/*  40 */     System.out.println(session1.getLastAccessedTime().getTime());
/*  41 */     System.out.println("Date have to be different");
/*     */   }
/*     */ 
/*     */   private static void test2()
/*     */     throws Exception
/*     */   {
/*  48 */     SessionManager manager = SessionManager.getInstance();
/*  49 */     Session session1 = manager.newSession(SessionManager.generateSessionId());
/*  50 */     System.out.println("id : " + session1.getId());
/*  51 */     manager.setFlushingDelay(30);
/*     */ 
/*  53 */     boolean loop = true;
/*  54 */     while (loop)
/*     */     {
/*  56 */       loop = false;
/*  57 */       Enumeration e = manager.getSessionIds();
/*  58 */       while (e.hasMoreElements()) {
/*  59 */         System.out.println((String)e.nextElement());
/*  60 */         loop = true;
/*     */       }
/*     */       try {
/*  63 */         Thread.sleep(10000L);
/*     */       } catch (Exception localException) {
/*     */       }
/*     */     }
/*  67 */     Session session2 = manager.newSession(SessionManager.generateSessionId());
/*  68 */     System.out.println("id : " + session2.getId());
/*  69 */     loop = true;
/*  70 */     while (loop)
/*     */     {
/*  72 */       loop = false;
/*  73 */       Enumeration e = manager.getSessionIds();
/*  74 */       while (e.hasMoreElements()) {
/*  75 */         System.out.println((String)e.nextElement());
/*  76 */         loop = true;
/*     */       }
/*     */       try {
/*  79 */         Thread.sleep(10000L);
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void test3() throws Exception
/*     */   {
/*  89 */     SessionManager manager = SessionManager.getInstance();
/*  90 */     manager.setSessionMaxNumber(2);
/*  91 */     Session session1 = manager.newSession(SessionManager.generateSessionId());
/*  92 */     System.out.println("id : " + session1.getId());
/*  93 */     Session session2 = manager.newSession(SessionManager.generateSessionId());
/*  94 */     System.out.println("id : " + session2.getId());
/*  95 */     Session session3 = manager.newSession(SessionManager.generateSessionId());
/*  96 */     System.out.println("id : " + session3.getId());
/*     */ 
/*  98 */     System.out.println("End");
/*     */   }
/*     */ 
/*     */   private static void test4() throws Exception
/*     */   {
/* 103 */     SessionManager manager = SessionManager.getInstance();
/*     */ 
/* 105 */     Session session1 = manager.newSession("1122334455");
/* 106 */     System.out.println("id : " + session1.getId());
/*     */ 
/* 109 */     System.out.println("End");
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.SessionTest
 * JD-Core Version:    0.5.4
 */