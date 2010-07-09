/*    */ package com.atosorigin.services.rd.common.util;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Hashtable;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class HttpParams
/*    */ {
/*    */   private Hashtable keysValues;
/*    */ 
/*    */   public HttpParams(String string, String delimiter)
/*    */   {
/* 50 */     StringTokenizer st1 = new StringTokenizer(string, delimiter, false);
/*    */ 
/* 52 */     this.keysValues = new Hashtable(st1.countTokens());
/*    */ 
/* 54 */     for (int i = 0; st1.hasMoreTokens(); ++i)
/*    */     {
/* 56 */       StringTokenizer st2 = new StringTokenizer(st1.nextToken(), "=", false);
/* 57 */       if (st2 == null)
/*    */         continue;
/* 59 */       String key = st2.nextToken();
/*    */       String value;
/*    */      // String value;
/* 60 */       if (st2.hasMoreTokens())
/*    */       {
/* 62 */         value = st2.nextToken();
/*    */       }
/*    */       else
/*    */       {
/* 66 */         value = "";
/*    */       }
/* 68 */       this.keysValues.put(key, value);
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getSize()
/*    */   {
/* 75 */     return this.keysValues.size();
/*    */   }
/*    */ 
/*    */   public String getValue(String key)
/*    */   {
/* 80 */     return (String)this.keysValues.get(key);
/*    */   }
/*    */ 
/*    */   public int getValueInt(String key)
/*    */   {
/* 85 */     return Integer.parseInt((String)this.keysValues.get(key));
/*    */   }
/*    */ 
/*    */   public static void main(String[] arg)
/*    */   {
/* 90 */     String test = "&var1=un& &var2=deux&v&var3=trois& ";
/* 91 */     HttpParams httpParams = new HttpParams(test, "&");
/*    */ 
/* 93 */     System.out.println("test = (" + test + ")");
/* 94 */     System.out.println(httpParams.getValue("var1"));
/* 95 */     System.out.println(httpParams.getValue("var2"));
/* 96 */     System.out.println(httpParams.getValue("var3"));
/*    */   }
/*    */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.HttpParams
 * JD-Core Version:    0.5.4
 */