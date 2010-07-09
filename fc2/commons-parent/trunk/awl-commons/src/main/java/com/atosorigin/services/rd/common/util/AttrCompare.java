/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import org.w3c.dom.Attr;
/*     */ import sun.misc.Compare;
/*     */ 
/*     */ public class AttrCompare
/*     */   implements Comparator, Compare
/*     */ {
/*     */   public int doCompare(Object obj0, Object obj1)
/*     */   {
/*  54 */     return compare(obj0, obj1);
/*     */   }
/*     */ 
/*     */   public int compare(Object obj0, Object obj1)
/*     */   {
/*  78 */     Attr attr0 = (Attr)obj0;
/*  79 */     Attr attr1 = (Attr)obj1;
/*     */ 
/*  81 */     attr0.normalize();
/*  82 */     attr1.normalize();
/*     */ 
/*  84 */     String name0 = attr0.getName();
/*  85 */     String name1 = attr1.getName();
/*  86 */     String prefix0 = attr0.getPrefix();
/*  87 */     String prefix1 = attr1.getPrefix();
/*  88 */     String localName0 = attr0.getLocalName();
/*  89 */     String localName1 = attr1.getLocalName();
/*  90 */     String namespaceURI0 = attr0.getNamespaceURI();
/*  91 */     String namespaceURI1 = attr1.getNamespaceURI();
/*  92 */     boolean definesNS0 = false;
/*  93 */     boolean definesNS1 = false;
/*  94 */     boolean definesDefaultNS0 = false;
/*  95 */     boolean definesDefaultNS1 = false;
/*     */ 
/* 109 */     if (name0.equals("xmlns"))
/*     */     {
/* 112 */       localName0 = "";
/* 113 */       prefix0 = "xmlns";
/* 114 */       definesNS0 = true;
/* 115 */       definesDefaultNS0 = true;
/*     */ 
/* 117 */       if (namespaceURI0 == null) {
/* 118 */         namespaceURI0 = "http://www.w3.org/2000/xmlns/";
/*     */       }
/*     */     }
/*     */ 
/* 122 */     if (name1.equals("xmlns"))
/*     */     {
/* 125 */       localName1 = "";
/* 126 */       prefix1 = "xmlns";
/* 127 */       definesNS1 = true;
/* 128 */       definesDefaultNS1 = true;
/*     */ 
/* 130 */       if (namespaceURI1 == null) {
/* 131 */         namespaceURI1 = "http://www.w3.org/2000/xmlns/";
/*     */       }
/*     */     }
/*     */ 
/* 135 */     if (name0.startsWith("xmlns:")) {
/* 136 */       prefix0 = "xmlns";
/* 137 */       localName0 = name0.substring(name0.indexOf("xmlns:") + 
/* 138 */         "xmlns:".length());
/* 139 */       definesNS0 = true;
/*     */ 
/* 141 */       if (namespaceURI0 == null) {
/* 142 */         namespaceURI0 = "http://www.w3.org/2000/xmlns/";
/*     */       }
/*     */     }
/*     */ 
/* 146 */     if (name1.startsWith("xmlns:")) {
/* 147 */       prefix1 = "xmlns";
/* 148 */       localName1 = name1.substring(name1.indexOf("xmlns:") + 
/* 149 */         "xmlns:".length());
/* 150 */       definesNS1 = true;
/*     */ 
/* 152 */       if (namespaceURI1 == null) {
/* 153 */         namespaceURI1 = "http://www.w3.org/2000/xmlns/";
/*     */       }
/*     */     }
/*     */ 
/* 157 */     if (namespaceURI0 == null) {
/* 158 */       namespaceURI0 = "";
/* 159 */       localName0 = name0;
/*     */     }
/*     */ 
/* 162 */     if (namespaceURI1 == null) {
/* 163 */       namespaceURI1 = "";
/* 164 */       localName1 = name1;
/*     */     }
/*     */ 
/* 167 */     if (attr0 == null) {
/* 168 */       return 0;
/*     */     }
/*     */ 
/* 171 */     if (attr1 == null) {
/* 172 */       return 0;
/*     */     }
/*     */ 
/* 175 */     if ((localName0 == null) && (name0 == null)) {
/* 176 */       return 0;
/*     */     }
/*     */ 
/* 179 */     if ((localName1 == null) && (name1 == null)) {
/* 180 */       return 0;
/*     */     }
/*     */ 
/* 193 */     if ((definesNS0) && (definesNS1))
/*     */     {
/* 201 */       int result = signum(localName0.compareTo(localName1));
/*     */ 
/* 203 */       return result;
/* 204 */     }if ((!definesNS0) && (!definesNS1))
/*     */     {
/* 212 */       int NScomparisonResult = 
/* 213 */         signum(namespaceURI0.compareTo(namespaceURI1));
/*     */ 
/* 215 */       if (NScomparisonResult != 0) {
/* 216 */         return NScomparisonResult;
/*     */       }
/*     */ 
/* 219 */       int result = signum(localName0.compareTo(localName1));
/*     */ 
/* 221 */       return result;
/* 222 */     }if ((definesNS0) && (!definesNS1))
/*     */     {
/* 229 */       return -1;
/* 230 */     }if ((!definesNS0) && (definesNS1))
/*     */     {
/* 237 */       return 1;
/*     */     }
/*     */ 
/* 240 */     return 0;
/*     */   }
/*     */ 
/*     */   private static int signum(int input)
/*     */   {
/* 251 */     if (input == 0)
/* 252 */       return 0;
/* 253 */     if (input < 0) {
/* 254 */       return -1;
/*     */     }
/* 256 */     return 1;
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.AttrCompare
 * JD-Core Version:    0.5.4
 */