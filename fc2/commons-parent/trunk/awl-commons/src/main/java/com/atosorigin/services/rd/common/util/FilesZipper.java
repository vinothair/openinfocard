/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ 
/*     */ public class FilesZipper
/*     */ {
/*     */   static final int BUFFER = 2048;
/*     */ 
/*     */   public static final void copyInputStream(InputStream in, OutputStream out)
/*     */     throws IOException
/*     */   {
/*  55 */     byte[] buffer = new byte[2048];
/*     */     int len;
/*  58 */     while ((len = in.read(buffer)) >= 0)
/*     */     {
/*     */      // int len;
/*  59 */       out.write(buffer, 0, len);
/*     */     }
/*  61 */     out.flush();
/*     */   }
/*     */ 
/*     */   public void ZipFiles(String[] filesToZip, String sourcedir, String zipFile)
/*     */     throws Exception
/*     */   {
/*  76 */     BufferedInputStream source = null;
/*  77 */     FileOutputStream dest = new FileOutputStream(zipFile);
/*  78 */     ZipOutputStream zipped = new ZipOutputStream(new BufferedOutputStream(dest));
/*     */ 
/*  80 */     for (int i = 0; i < filesToZip.length; ++i)
/*     */     {
/*  82 */       String oneFileToZip = filesToZip[i];
/*     */ 
/*  85 */       FileInputStream inputFileToZip = new FileInputStream(sourcedir + oneFileToZip);
/*  86 */       source = new BufferedInputStream(inputFileToZip, 2048);
/*  87 */       ZipEntry entry = new ZipEntry(oneFileToZip);
/*     */ 
/*  89 */       zipped.putNextEntry(entry);
/*     */ 
/*  91 */       copyInputStream(source, zipped);
/*     */ 
/*  93 */       source.close();
/*     */     }
/*  95 */     zipped.close();
/*     */   }
/*     */ 
/*     */   public int UnzipFiles(String filesToUnzip, String destdir)
/*     */     throws Exception
/*     */   {
/* 110 */     ZipFile zipFile = new ZipFile(filesToUnzip);
/* 111 */     Enumeration entries = zipFile.entries();
/* 112 */     int nbfiles = 0;
/*     */ 
/* 114 */     while (entries.hasMoreElements())
/*     */     {
/* 116 */       ZipEntry entry = (ZipEntry)entries.nextElement();
/*     */ 
/* 118 */       if (entry.isDirectory()) {
/* 119 */         new File(entry.getName()).mkdir();
/*     */       }
/*     */       else {
/* 122 */         ++nbfiles;
/* 123 */         InputStream zippedSource = zipFile.getInputStream(entry);
/* 124 */         BufferedOutputStream unzippedDest = new BufferedOutputStream(new FileOutputStream(destdir + entry.getName()));
/*     */ 
/* 126 */         copyInputStream(zippedSource, unzippedDest);
/*     */ 
/* 128 */         unzippedDest.close();
/*     */       }
/*     */     }
/* 130 */     zipFile.close();
/* 131 */     return nbfiles;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 142 */     String[] toZip = new String[3];
/* 143 */     toZip[0] = "1528.crl";
/* 144 */     toZip[1] = "1530.crl";
/* 145 */     toZip[2] = "RLite_W2K_README.pdf";
/* 146 */     String zipFile = "C:/tracelogger/crl.zip";
/* 147 */     String sourceDir = "C:/tracelogger/";
/* 148 */     String destDir = "C:/tracelogger/test/";
/* 149 */     FilesZipper zip = new FilesZipper();
/*     */     try
/*     */     {
/* 152 */       zip.ZipFiles(toZip, sourceDir, zipFile);
/*     */ 
/* 154 */       System.out.println("total unzipped files : " + zip.UnzipFiles(zipFile, destDir));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 158 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.FilesZipper
 * JD-Core Version:    0.5.4
 */