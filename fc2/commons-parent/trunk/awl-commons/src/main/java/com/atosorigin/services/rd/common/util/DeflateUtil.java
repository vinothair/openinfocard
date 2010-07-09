/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.Vector;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.DeflaterOutputStream;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterInputStream;
/*     */ 
/*     */ public class DeflateUtil
/*     */ {
/*  40 */   public static int DEFAULT_COMPRESSION = -1;
/*  41 */   public static int NO_COMPRESSION = 0;
/*  42 */   public static int BEST_COMPRESSION = 9;
/*     */ 
/*  44 */   private static Vector levels = new Vector();
/*     */ 
/*  46 */   static { levels.add(new Integer(DEFAULT_COMPRESSION));
/*  47 */     levels.add(new Integer(NO_COMPRESSION));
/*  48 */     levels.add(new Integer(BEST_COMPRESSION)); }
/*     */ 
/*     */ 
/*     */   public static synchronized byte[] deflate(byte[] data)
/*     */     throws Exception
/*     */   {
/*  57 */     return deflate(data, DEFAULT_COMPRESSION);
/*     */   }
/*     */ 
/*     */   public static synchronized byte[] deflate(byte[] data, int compressionLevel)
/*     */     throws Exception
/*     */   {
/*  67 */     if (!levels.contains(new Integer(compressionLevel))) {
/*  68 */       compressionLevel = DEFAULT_COMPRESSION;
/*     */     }
/*     */ 
/*  72 */     Deflater deflater = new Deflater(compressionLevel);
/*     */ 
/*  75 */     ByteArrayOutputStream compressedData = new ByteArrayOutputStream();
/*  76 */     Writer w = new OutputStreamWriter(
/*  77 */       new DeflaterOutputStream(compressedData, deflater), "UTF-8");
/*  78 */     w.write(new String(data, "UTF-8"));
/*     */ 
/*  81 */     w.close();
/*  82 */     compressedData.close();
/*  83 */     deflater.end();
/*     */ 
/*  85 */     return compressedData.toByteArray();
/*     */   }
/*     */ 
/*     */   public static synchronized byte[] inflate(byte[] data)
/*     */     throws Exception
/*     */   {
/*  95 */     Inflater inflater = new Inflater();
/*     */ 
/*  98 */     ByteArrayInputStream bais = new ByteArrayInputStream(data);
/*  99 */     BufferedReader br = new BufferedReader(
/* 100 */       new InputStreamReader(new InflaterInputStream(bais, inflater), "UTF-8"));
/*     */ 
/* 102 */     int r = 0;
/* 103 */     ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
/* 104 */     Writer w = new OutputStreamWriter(decompressedData, "UTF-8");
/* 105 */     while ((r = br.read()) != -1) {
/* 106 */       w.write(r);
/* 107 */       w.flush();
/*     */     }
/*     */ 
/* 111 */     br.close();
/* 112 */     bais.close();
/* 113 */     w.close();
/* 114 */     decompressedData.close();
/* 115 */     inflater.end();
/*     */ 
/* 117 */     return decompressedData.toByteArray();
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.DeflateUtil
 * JD-Core Version:    0.5.4
 */