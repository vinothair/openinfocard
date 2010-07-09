/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public class CharEncodingConverter
/*     */ {
/*  34 */   public static String SEP = ";";
/*  35 */   public static String DEF = "42";
/*     */   Properties props;
/*     */ 
/*     */   public CharEncodingConverter(Properties data)
/*     */   {
/*  46 */     this.props = new Properties();
/*  47 */     Enumeration en = data.keys();
/*  48 */     while (en.hasMoreElements()) {
/*  49 */       String k = (String)en.nextElement();
/*  50 */       String[] values = data.getProperty(k).split(SEP);
/*  51 */       for (int i = 0; i < values.length; ++i)
/*  52 */         this.props.setProperty(values[i], k);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String convert(String txt)
/*     */   {
/*  65 */     StringBuffer sb = new StringBuffer();
/*  66 */     for (int i = 0; i < txt.length(); ++i) {
/*  67 */       int c = txt.charAt(i);
/*  68 */       if (c > 127) {
/*  69 */         c = Integer.parseInt(this.props.getProperty(Integer.toString(c), DEF));
/*     */       }
/*  71 */       sb.append((char)c);
/*     */     }
/*     */ 
/*  74 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  84 */     ResourceBundle rb = ResourceBundle.getBundle(
/*  85 */       "com.atosorigin.services.rd.common.util.ISO8859-1");
/*  86 */     Properties data = new Properties();
/*  87 */     Enumeration en = rb.getKeys();
/*  88 */     while (en.hasMoreElements()) {
/*  89 */       String k = (String)en.nextElement();
/*  90 */       data.setProperty(k, rb.getString(k));
/*     */     }
/*     */ 
/*  93 */     CharEncodingConverter enc = new CharEncodingConverter(data);
/*  94 */     String txt = "Laurent Simonnet";
/*  95 */     System.out.println(txt + "  -->  " + enc.convert(txt));
/*     */ 
/*  97 */     txt = "ÀÂÄÃÅ àâäáå";
/*  98 */     System.out.println(txt + "  -->  " + enc.convert(txt));
/*     */ 
/* 100 */     txt = "ÈÉÊË èéêë";
/* 101 */     System.out.println(txt + "  -->  " + enc.convert(txt));
/*     */ 
/* 103 */     txt = "ÌÍÎÏ ìíîï";
/* 104 */     System.out.println(txt + "  -->  " + enc.convert(txt));
/*     */ 
/* 106 */     txt = "Ññ Ýý";
/* 107 */     System.out.println(txt + "  -->  " + enc.convert(txt));
/*     */ 
/* 109 */     txt = "ÒÓÔÕÖ òóôõö";
/* 110 */     System.out.println(txt + "  -->  " + enc.convert(txt));
/*     */ 
/* 112 */     txt = "ÙÚÛÜ ùúûü";
/* 113 */     System.out.println(txt + "  -->  " + enc.convert(txt));
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.CharEncodingConverter
 * JD-Core Version:    0.5.4
 */