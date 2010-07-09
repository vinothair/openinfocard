/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.Vector;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public abstract class XMLResourceBundle
/*     */ {
/*  90 */   private static String CLASS_NAME = "XMLResourceBundle";
/*  91 */   private static String RESOURCE_NOT_FOUND = "Could not find resource ";
/*  92 */   private static String FILE_EXTENSION = ".xml";
/*     */ 
/*     */   public static XMLResourceBundle getFile(String fileName)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 111 */     return getFile(fileName, false);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getFile(String fileName, boolean validate)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 126 */     FileInputStream bundleFileStream = null;
/*     */     try
/*     */     {
/* 129 */       bundleFileStream = new FileInputStream(fileName);
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 133 */       throw new MissingResourceException(
/* 134 */         RESOURCE_NOT_FOUND + fileName, 
/* 135 */         CLASS_NAME, 
/* 136 */         fileName);
/*     */     }
/*     */ 
/* 139 */     return new XMLResourceBundleImpl(bundleFileStream, null, validate);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getBundle(String bundle)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 153 */     return getBundle(null, bundle);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getBundle(ClassLoader loader, String bundle)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 168 */     return getBundle(loader, bundle, false);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getBundle(String bundle, boolean validate)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 183 */     return getBundle(null, bundle, validate);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getBundle(ClassLoader loader, String bundle, boolean validate)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 201 */     return getBundle(loader, bundle, null, validate);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getBundle(String bundle, Locale locale)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 227 */     return getBundle(null, bundle, locale);
/*     */   }
/*     */ 
/*     */   public abstract Document getDocument();
/*     */ 
/*     */   public static XMLResourceBundle getBundle(ClassLoader loader, String bundle, Locale locale)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 252 */     return getBundle(loader, bundle, locale, false);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getBundle(String bundle, Locale locale, boolean validate)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 269 */     return getBundle(null, bundle, locale, validate);
/*     */   }
/*     */ 
/*     */   public static XMLResourceBundle getBundle(ClassLoader loader, String bundle, Locale locale, boolean validate)
/*     */     throws MissingResourceException, Exception
/*     */   {
/* 288 */     InputStream bundleStream = null;
/*     */ 
/* 290 */     if (locale != null)
/*     */     {
/* 292 */       bundleStream = getResourceAsStream(loader, 
/* 293 */         bundle + "_" + locale.getCountry() + "_" + 
/* 294 */         locale.getLanguage() + "_" + locale.getVariant());
/*     */     }
/*     */     else {
/* 297 */       bundleStream = getResourceAsStream(loader, bundle);
/*     */     }
/*     */ 
/* 300 */     return new XMLResourceBundleImpl(bundleStream, locale, validate);
/*     */   }
/*     */ 
/*     */   private static InputStream getResourceAsStream(ClassLoader loader, String resource)
/*     */     throws MissingResourceException
/*     */   {
/* 372 */     String resourceToFind = new String(resource);
/*     */ 
/* 374 */     InputStream resourceStream = null;
/*     */     try
/*     */     {
/* 378 */       if (loader == null) {
/* 379 */         loader = getClassLoader();
/*     */       }
/*     */ 
/* 382 */       resourceStream = loader.getResourceAsStream(
/* 383 */         resourceToFind.replace('.', '/') + FILE_EXTENSION);
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/* 387 */       int index = resourceToFind.lastIndexOf('_');
/*     */ 
/* 389 */       if (index >= 0) {
/* 390 */         resourceToFind = resourceToFind.substring(0, index);
/* 391 */         resourceStream = getResourceAsStream(loader, resourceToFind);
/*     */       }
/*     */       else
/*     */       {
/* 395 */         throw new MissingResourceException(
/* 396 */           RESOURCE_NOT_FOUND + resourceToFind + FILE_EXTENSION, 
/* 397 */           CLASS_NAME, 
/* 398 */           resourceToFind + FILE_EXTENSION);
/*     */       }
/*     */     }
/*     */ 
/* 402 */     return resourceStream;
/*     */   }
/*     */ 
/*     */   private static ClassLoader getClassLoader()
/*     */   {
/* 413 */     return XMLResourceBundle.class.getClassLoader();
/*     */   }
/*     */ 
/*     */   public abstract Locale getLocale();
/*     */ 
/*     */   public abstract Node getNode(String paramString)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNode(String paramString, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNode(String paramString1, String paramString2, String paramString3)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNode(String paramString1, String paramString2, String paramString3, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNode(String paramString1, String paramString2, String paramString3, String paramString4)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNode(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNode(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNode(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract Node getNamedChild(Node paramNode, String paramString);
/*     */ 
/*     */   public abstract Vector getNamedChildren(Node paramNode, String paramString);
/*     */ 
/*     */   public abstract String getNamedAttribut(Node paramNode, String paramString);
/*     */ 
/*     */   public abstract String getString(String paramString)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract int getNbOfElement(String paramString);
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3, String paramString4)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
/*     */     throws MissingResourceException;
/*     */ 
/*     */   public abstract String getString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt)
/*     */     throws MissingResourceException;
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.XMLResourceBundle
 * JD-Core Version:    0.5.4
 */