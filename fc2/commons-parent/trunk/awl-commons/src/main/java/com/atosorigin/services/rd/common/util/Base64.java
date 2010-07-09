/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Base64
/*     */ {
/*  40 */   private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
/*     */ 
/*  43 */   private static byte[] codes = new byte[256];
/*     */ 
/*     */   static {
/*  46 */     for (int i = 0; i < 256; ++i) {
/*  47 */       codes[i] = -1;
/*     */     }
/*  49 */     for (int i = 65; i <= 90; ++i) {
/*  50 */       codes[i] = (byte)(i - 65);
/*     */     }
/*  52 */     for (int i = 97; i <= 122; ++i) {
/*  53 */       codes[i] = (byte)(26 + i - 97);
/*     */     }
/*  55 */     for (int i = 48; i <= 57; ++i) {
/*  56 */       codes[i] = (byte)(52 + i - 48);
/*     */     }
/*  58 */     codes[43] = 62;
/*  59 */     codes[47] = 63;
/*     */   }
/*     */ 
/*     */   public static String encode(String data)
/*     */   {
/*  71 */     return new String(encode(c2b(data.toCharArray())));
/*     */   }
/*     */ 
/*     */   public static String encodeBuffer(byte[] data)
/*     */   {
/*  83 */     return new String(encode(data));
/*     */   }
/*     */ 
/*     */   public static String decode(String data)
/*     */   {
/*  95 */     return new String(decode(c2b(data.toCharArray())));
/*     */   }
/*     */ 
/*     */   public static byte[] decodeBuffer(String data)
/*     */   {
/* 102 */     StringTokenizer tok = new StringTokenizer(data, " \n\r\t", false);
/* 103 */     StringBuffer buffer = new StringBuffer(data.length());
/*     */ 
/* 105 */     while (tok.hasMoreElements()) {
/* 106 */       buffer.append(tok.nextToken());
/*     */     }
/*     */ 
/* 109 */     data = buffer.toString();
/*     */ 
/* 111 */     return decode(data.getBytes());
/*     */   }
/*     */ 
/*     */   public static char[] encode(byte[] data)
/*     */   {
/* 124 */     char[] out = new char[(data.length + 2) / 3 * 4];
/*     */ 
/* 130 */     int i = 0; for (int index = 0; i < data.length; index += 4) {
/* 131 */       boolean quad = false;
/* 132 */       boolean trip = false;
/* 133 */       int val = 0xFF & data[i];
/*     */ 
/* 135 */       val <<= 8;
/* 136 */       if (i + 1 < data.length) {
/* 137 */         val |= 0xFF & data[(i + 1)];
/* 138 */         trip = true;
/*     */       }
/* 140 */       val <<= 8;
/* 141 */       if (i + 2 < data.length) {
/* 142 */         val |= 0xFF & data[(i + 2)];
/* 143 */         quad = true;
/*     */       }
/*     */ 
/* 146 */       out[(index + 3)] = alphabet[64];
/* 147 */       val >>= 6;
/* 148 */       out[(index + 2)] = alphabet[64];
/* 149 */       val >>= 6;
/* 150 */       out[(index + 1)] = alphabet[(val & 0x3F)];
/* 151 */       val >>= 6;
/* 152 */       out[(index + 0)] = alphabet[(val & 0x3F)];
/*     */ 
/* 130 */       i += 3;
/*     */     }
/*     */ 
/* 155 */     return out;
/*     */   }
/*     */ 
/*     */   public static byte[] decode(byte[] data)
/*     */   {
/* 167 */     int len = (data.length + 3) / 4 * 3;
/* 168 */     if ((data.length > 0) && (data[(data.length - 1)] == 61))
/* 169 */       --len;
/* 170 */     if ((data.length > 1) && (data[(data.length - 2)] == 61)) {
/* 171 */       --len;
/*     */     }
/* 173 */     byte[] out = new byte[len];
/*     */ 
/* 175 */     int shift = 0;
/* 176 */     int accum = 0;
/* 177 */     int index = 0;
/*     */ 
/* 179 */     for (int ix = 0; ix < data.length; ++ix)
/*     */     {
/* 181 */       int value = codes[(data[ix] & 0xFF)];
/* 182 */       if (value < 0)
/*     */         continue;
/* 184 */       accum <<= 6;
/* 185 */       shift += 6;
/* 186 */       accum |= value;
/* 187 */       if (shift < 8)
/*     */         continue;
/* 189 */       shift -= 8;
/*     */ 
/* 191 */       out[(index++)] = (byte)(accum >> shift & 0xFF);
/*     */     }
/*     */ 
/* 196 */     if (index != out.length) {
/* 197 */       throw new RuntimeException("Error decoding BASE64 element: miscalculated data length!");
/*     */     }
/* 199 */     return out;
/*     */   }
/*     */ 
/*     */   private static byte[] c2b(char[] c)
/*     */   {
/* 226 */     byte[] b = new byte[c.length];
/* 227 */     for (int i = 0; i < c.length; ++i) {
/* 228 */       b[i] = (byte)c[i];
/*     */     }
/* 230 */     return b;
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.Base64
 * JD-Core Version:    0.5.4
 */