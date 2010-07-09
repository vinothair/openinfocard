/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.StringTokenizer;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class Error
/*     */ {
/*  15 */   private static String PACKAGE = "com.atosorigin.services.rd.common.util";
/*     */ 
/*     */   public static String getErrorCode(String context, Exception except)
/*     */   {
/*  26 */     return getErrorCode(null, 
/*  27 */       PACKAGE + ".errorCode", 
/*  28 */       context, 
/*  29 */       except, 
/*  30 */       except.getMessage());
/*     */   }
/*     */ 
/*     */   public static String getErrorCode(String context, Exception except, String exceptionMessage)
/*     */   {
/*  47 */     return getErrorCode(null, 
/*  48 */       PACKAGE + ".errorCode", 
/*  49 */       context, 
/*  50 */       except, 
/*  51 */       exceptionMessage);
/*     */   }
/*     */ 
/*     */   public static String getErrorCode(ClassLoader cl, String context, Exception except)
/*     */   {
/*  66 */     return getErrorCode(cl, 
/*  67 */       PACKAGE + ".errorCode", 
/*  68 */       context, 
/*  69 */       except, 
/*  70 */       except.getMessage());
/*     */   }
/*     */ 
/*     */   public static String getErrorCode(ClassLoader cl, String context, Exception except, String exceptionMessage)
/*     */   {
/*  89 */     return getErrorCode(cl, 
/*  90 */       PACKAGE + ".errorCode", 
/*  91 */       context, 
/*  92 */       except, 
/*  93 */       exceptionMessage);
/*     */   }
/*     */ 
/*     */   public static String getErrorCode(String configBundle, String context, Exception except)
/*     */   {
/* 108 */     return getErrorCode(null, 
/* 109 */       configBundle, 
/* 110 */       context, 
/* 111 */       except, 
/* 112 */       except.getMessage());
/*     */   }
/*     */ 
/*     */   public static String getErrorCode(String configBundle, String context, Exception except, String exceptionMessage)
/*     */   {
/* 131 */     return getErrorCode(null, 
/* 132 */       configBundle, 
/* 133 */       context, 
/* 134 */       except, 
/* 135 */       exceptionMessage);
/*     */   }
/*     */ 
/*     */   public static String getErrorCode(ClassLoader cl, String configBundle, String context, Exception except)
/*     */   {
/* 151 */     return getErrorCode(cl, 
/* 152 */       configBundle, 
/* 153 */       context, 
/* 154 */       except, 
/* 155 */       except.getMessage());
/*     */   }
/*     */ 
/*     */   public static String getErrorCode(ClassLoader cl, String configBundle, String context, Exception except, String exceptionMessage)
/*     */   {
	 		  boolean label259=false;
/* 175 */     String errorCode = "";
/* 176 */     XMLResourceBundle xrb = null;
/* 177 */     int index = 0;
/* 178 */     int indexMax = 0;
/* 179 */     Element exceptionElement = null;
/* 180 */     NodeList keyWordsNodeList = null;
/* 181 */     NodeList returnMessage = null;
/*     */     try
/*     */     {
/* 185 */       xrb = XMLResourceBundle.getBundle(cl, configBundle);
/*     */     } catch (Exception e) {
/* 187 */       errorCode = "ERROR/UNEXPECTED/CHECK-ERROR-CONFIG(" + e.getMessage() + ")";
/* 188 */       return errorCode;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 194 */       indexMax = xrb.getNode("Error." + context).getChildNodes().getLength();
/*     */     }
/*     */     catch (MissingResourceException mre) {
/* 197 */       indexMax = 0;
/*     */     }
/*     */ 
/* 201 */     for (index = 0; index < indexMax; ++index)
/*     */     {
/*     */       try {
/* 204 */         exceptionElement = (Element)xrb.getNode("Error." + context + ".Exception", 
/* 205 */           "classID", 
/* 206 */           except.getClass().getName(), 
/* 207 */           index);
/*     */       }
/*     */       catch (MissingResourceException mre) {
/* 210 */         index = indexMax;
				  label259 = true;
/* 211 */         break;
/*     */       }
/*     */ 
/* 214 */       keyWordsNodeList = exceptionElement.getElementsByTagName("KeyWordList");
/*     */ 
/* 217 */       if (keyWordsNodeList.item(0) != null)
/*     */       {
/* 219 */         String keyWordsList = getFirstChildValue(keyWordsNodeList.item(0));
/* 220 */         StringTokenizer keyWords = new StringTokenizer(keyWordsList, ",", false);
/*     */ 
/* 223 */         if (areKeyWordsInException(exceptionMessage, keyWords)) {
/* 224 */           errorCode = getErrorCode(exceptionElement, context);
/* 225 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 232 */         errorCode = getErrorCode(exceptionElement, context);
/* 233 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 241 */     if (index == indexMax || label259) {
/*     */       try
/*     */       {
/* 244 */          errorCode = xrb.getString("Error." + context + ".Default.Return_message");
/*     */       } catch (Exception e1) {
/*     */         try {
/* 247 */           errorCode = xrb.getString("Error.Default.Return_message");
/*     */         } catch (Exception e2) {
/* 249 */           errorCode = "ERROR/UNEXPECTED/CHECK-ERROR-CONFIG(" + e2.getMessage() + ")";
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 255 */     return errorCode;
/*     */   }
/*     */ 
/*     */   private static boolean areKeyWordsInException(String exceptionMessage, StringTokenizer keyWords)
/*     */   {
/* 267 */     boolean response = true;
/*     */ 
/* 269 */     while (keyWords.hasMoreTokens())
/*     */     {
/* 271 */       if (exceptionMessage.indexOf(keyWords.nextToken()) != -1)
/*     */         continue;
/* 273 */       response = false;
/* 274 */       break;
/*     */     }
/*     */ 
/* 280 */     return response;
/*     */   }
/*     */ 
/*     */   private static String getErrorCode(Element exceptionElement, String context)
/*     */   {
/* 292 */     String errorCode = "";
/* 293 */     NodeList returnMessage = exceptionElement.getElementsByTagName("Return_message");
/*     */     try
/*     */     {
/* 296 */       errorCode = getFirstChildValue(returnMessage.item(0));
/*     */     } catch (Exception e) {
/* 298 */       errorCode = context + "/UNEXPECTED/CHECK-ERROR-CONFIG(" + e.getMessage() + ")";
/*     */     }
/*     */ 
/* 301 */     return errorCode;
/*     */   }
/*     */ 
/*     */   private static String getFirstChildValue(Node node)
/*     */   {
/* 314 */     int indexMax = node.getChildNodes().getLength();
/* 315 */     String value = "";
/* 316 */     String tempValue = "";
/*     */ 
/* 318 */     for (int i = 0; i < indexMax; ++i) {
/* 319 */       tempValue = node.getChildNodes().item(i).getNodeValue();
/* 320 */       if (!value.equals("#TEXT")) {
/* 321 */         value = tempValue;
/* 322 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 326 */     return value;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 338 */     Exception testClass = new Exception("test");
/* 339 */     System.out.println(testClass.getClass().getName());
/* 340 */     System.out.println(testClass.getMessage());
/*     */ 
/* 342 */     System.out.println(getErrorCode("API-Component", testClass));
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.Error
 * JD-Core Version:    0.5.4
 */