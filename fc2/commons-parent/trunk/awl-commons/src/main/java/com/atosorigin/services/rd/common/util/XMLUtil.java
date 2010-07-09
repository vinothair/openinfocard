/*    */ package com.atosorigin.services.rd.common.util;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
/*    */ import javax.xml.parsers.DocumentBuilder;
/*    */ import javax.xml.parsers.DocumentBuilderFactory;
/*    */ import javax.xml.parsers.ParserConfigurationException;
/*    */ import org.w3c.dom.Document;
/*    */ 
/*    */ public class XMLUtil
/*    */ {
/*    */   public static Document getDocument()
/*    */     throws Exception
/*    */   {
/* 17 */     Document document = getDocument((InputStream)null);
/*    */ 
/* 19 */     return document;
/*    */   }
/*    */ 
/*    */   public static Document getDocument(InputStream xmlInputStream)
/*    */     throws Exception
/*    */   {
/* 25 */     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 26 */     DocumentBuilder db = null;
/* 27 */     dbf.setNamespaceAware(true);
/*    */     try
/*    */     {
/* 30 */       db = dbf.newDocumentBuilder();
/*    */     }
/*    */     catch (ParserConfigurationException pce)
/*    */     {
/* 34 */       String msg = "Init document builder : " + pce.getMessage();
/* 35 */       throw new Exception(msg);
/*    */     }
/*    */     Document document;
/* 39 */     if (xmlInputStream == null)
/*    */     {
/* 42 */       document = db.newDocument();
/*    */     }
/*    */     else
/*    */     {
///*    */       Document document;
/*    */       try
/*    */       {
/* 49 */         document = db.parse(xmlInputStream);
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 53 */         String msg = "Creation Document (DOM) failed : " + e.getMessage();
/* 54 */         throw new Exception(msg);
/*    */       }
/*    */     }
///*    */     Document document;
/* 58 */     return document;
/*    */   }
/*    */ 
/*    */   public static Document getDocument(String xmlString)
/*    */     throws Exception
/*    */   {
/*    */     InputStream xmlInputStream;
///*    */     InputStream xmlInputStream;
/* 64 */     if (xmlString == null)
/*    */     {
/* 66 */       xmlInputStream = null;
/*    */     }
/*    */     else
/*    */     {
/* 70 */       xmlInputStream = new ByteArrayInputStream(xmlString.getBytes());
/*    */     }
/* 72 */     Document document = getDocument(xmlInputStream);
/*    */ 
/* 74 */     return document;
/*    */   }
/*    */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.XMLUtil
 * JD-Core Version:    0.5.4
 */