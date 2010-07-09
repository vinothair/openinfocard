/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Comment;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.ProcessingInstruction;
/*     */ 
/*     */ public class Canonicalizer20010315
/*     */ {
/*     */   public static final String ENCODING = "UTF8";
/*     */   public static final String XML_LANG_SPACE_SpecNS = "http://www.w3.org/XML/1998/namespace";
/*     */   public static final String NamespaceSpecNS = "http://www.w3.org/2000/xmlns/";
/*     */   protected static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
/*     */   protected static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
/*     */   protected static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
/*  70 */   protected Document _doc = null;
/*  71 */   protected Element _documentElement = null;
/*  72 */   protected Node _rootNodeOfC14n = null;
/*     */ 
/*  74 */   protected Writer _writer = null;
/*     */ 
/*     */   public byte[] engineCanonicalizeSubTree(Node rootNode)
/*     */     throws Exception
/*     */   {
/*  92 */     this._rootNodeOfC14n = rootNode;
/*  93 */     this._doc = getOwnerDocument(this._rootNodeOfC14n);
/*  94 */     this._documentElement = this._doc.getDocumentElement();
/*     */     try
/*     */     {
/*  98 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  99 */       this._writer = 
/* 100 */         new OutputStreamWriter(baos, 
/* 100 */         "UTF8");
/*     */ 
/* 102 */       Map inscopeNamespaces = getInscopeNamespaces(rootNode);
/* 103 */       Map alreadyVisible = new HashMap();
/*     */ 
/* 105 */       canonicalizeSubTree(rootNode, inscopeNamespaces, alreadyVisible);
/* 106 */       this._writer.close();
/*     */ 
/* 108 */       return baos.toByteArray();
/*     */     }
/*     */     catch (UnsupportedEncodingException ex)
/*     */     {
				throw new Exception(ex.getMessage());
/*     */     }
/*     */     catch (IOException ex) {
				throw new Exception(ex.getMessage());
/*     */     }
/*     */     finally {
/* 116 */       this._rootNodeOfC14n = null;
/* 117 */       this._doc = null;
/* 118 */       this._documentElement = null;
/* 119 */       this._writer = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void canonicalizeSubTree(Node currentNode, Map inscopeNamespaces, Map alreadyVisible)
/*     */     throws Exception, IOException
/*     */   {
/* 135 */     int currentNodeType = currentNode.getNodeType();
/*     */ 
/* 137 */     switch (currentNodeType)
/*     */     {
/*     */     case 5:
/*     */     case 8:
/*     */     case 10:
/*     */     default:
/* 141 */       break;
/*     */     case 2:
/*     */     case 6:
/*     */     case 11:
/*     */     case 12:
/* 149 */       throw new Exception("empty");
/*     */     case 9:
{
/* 151 */       Node currentChild = currentNode.getFirstChild();
/* 152 */       while (currentChild != null)
/*     */       {
/* 154 */         canonicalizeSubTree(currentChild, inscopeNamespaces, alreadyVisible);
/*     */ 
/* 153 */         currentChild = currentChild.getNextSibling();
/*     */       }
/*     */ 
/* 156 */       break;
}
/*     */     case 7:
/* 159 */       int position = getPositionRelativeToDocumentElement(currentNode);
/*     */ 
/* 161 */       if (position == 1) {
/* 162 */         this._writer.write("\n");
/*     */       }
/*     */ 
/* 165 */       outputPItoWriter((ProcessingInstruction)currentNode);
/*     */ 
/* 167 */       if (position != -1) return;
/* 168 */       this._writer.write("\n");
/*     */ 
/* 170 */       break;
/*     */     case 3:
/*     */     case 4:
/* 174 */       outputTextToWriter(currentNode.getNodeValue());
/* 175 */       break;
/*     */     case 1:
{
/* 178 */       Element currentElement = (Element)currentNode;
/*     */ 
/* 180 */       this._writer.write("<");
/* 181 */       this._writer.write(currentElement.getTagName());
/*     */ 
/* 183 */       Object[] attrs = 
/* 184 */         updateInscopeNamespacesAndReturnVisibleAttrs(
/* 185 */         currentElement, inscopeNamespaces, alreadyVisible);
/*     */ 
/* 187 */       attrs = sortAttributes(attrs);
/*     */ 
/* 190 */       for (int i = 0; i < attrs.length; ++i) {
/* 191 */         outputAttrToWriter(((Attr)attrs[i]).getNodeName(), 
/* 192 */           ((Attr)attrs[i]).getNodeValue());
/*     */       }
/*     */ 
/* 195 */       this._writer.write(">");
/*     */ 
/* 198 */       Node currentChild = currentNode.getFirstChild();
/* 199 */       while (currentChild != null)
/*     */       {
/* 201 */         if (currentChild.getNodeType() == 1)
/*     */         {
/* 207 */           canonicalizeSubTree(currentChild, 
/* 208 */             new HashMap(inscopeNamespaces), 
/* 209 */             new HashMap(alreadyVisible));
/*     */         }
/* 211 */         else canonicalizeSubTree(currentChild, 
/* 212 */             inscopeNamespaces, 
/* 213 */             alreadyVisible);
/* 200 */         currentChild = currentChild.getNextSibling();
/*     */       }
/*     */ 
/* 217 */       this._writer.write("</");
/* 218 */       this._writer.write(currentElement.getTagName());
/* 219 */       this._writer.write(">");
}
/*     */     }
/*     */   }
/*     */ 
/*     */   Object[] updateInscopeNamespacesAndReturnVisibleAttrs(Element currentElement, Map inscopeNamespaces, Map alreadyVisible)
/*     */     throws Exception
/*     */   {
/* 238 */     List result = new Vector();
/* 239 */     NamedNodeMap attributes = currentElement.getAttributes();
/* 240 */     int attributesLength = attributes.getLength();
/*     */ 
/* 242 */     for (int i = 0; i < attributesLength; ++i) {
/* 243 */       Attr currentAttr = (Attr)attributes.item(i);
/* 244 */       String name = currentAttr.getNodeName();
/* 245 */       String value = currentAttr.getValue();
/*     */ 
/* 247 */       if ((name.equals("xmlns")) && (value.equals("")))
/*     */       {
/* 250 */         inscopeNamespaces.remove("xmlns");
/* 251 */       } else if ((name.startsWith("xmlns")) && (!value.equals("")))
/*     */       {
/* 254 */         inscopeNamespaces.put(name, value);
/* 255 */       } else if (name.startsWith("xml:"))
/*     */       {
/* 258 */         inscopeNamespaces.put(name, value);
/*     */       }
/*     */       else
/*     */       {
/* 262 */         result.add(currentAttr);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 269 */     if ((alreadyVisible.containsKey("xmlns")) && 
/* 270 */       (!inscopeNamespaces.containsKey("xmlns")))
/*     */     {
/* 273 */       alreadyVisible.remove("xmlns");
/*     */ 
/* 275 */       Attr a = this._doc.createAttributeNS(
/* 276 */         "http://www.w3.org/2000/xmlns/", "xmlns");
/*     */ 
/* 278 */       a.setValue("");
/* 279 */       result.add(a);
/*     */     }
/*     */ 
/* 283 */     boolean isOrphanNode = currentElement == this._rootNodeOfC14n;
/* 284 */     Iterator it = inscopeNamespaces.keySet().iterator();
/*     */ 
/* 286 */     while (it.hasNext()) {
/* 287 */       String name = (String)it.next();
/* 288 */       String inscopeValue = (String)inscopeNamespaces.get(name);
/*     */ 
/* 290 */       if ((name.startsWith("xml:")) && (((isOrphanNode) || 
/* 291 */         (!alreadyVisible.containsKey(name)) || 
/* 292 */         (!alreadyVisible.get(name).equals(inscopeValue)))))
/*     */       {
/* 294 */         alreadyVisible.put(name, inscopeValue);
/* 295 */         Attr a = this._doc.createAttributeNS(
/* 296 */           "http://www.w3.org/XML/1998/namespace", name);
/*     */ 
/* 298 */         a.setValue(inscopeValue);
/* 299 */         result.add(a);
/*     */       } else {
/* 301 */         if ((alreadyVisible.containsKey(name)) && ((
/* 302 */           (!alreadyVisible.containsKey(name)) || 
/* 304 */           (alreadyVisible.get(name)
/* 304 */           .equals(inscopeValue))))) continue;
/* 305 */         if (namespaceIsRelative(inscopeValue)) {
/* 306 */           Object[] exArgs = { currentElement.getTagName(), name, 
/* 307 */             inscopeValue };
/*     */ 
/* 309 */           throw new Exception(
/* 310 */             "c14n.Canonicalizer.RelativeNamespace" + exArgs);
/*     */         }
/*     */ 
/* 313 */         alreadyVisible.put(name, inscopeValue);
/*     */ 
/* 315 */         Attr a = this._doc.createAttributeNS(
/* 316 */           "http://www.w3.org/2000/xmlns/", name);
/* 317 */         a.setValue(inscopeValue);
/* 318 */         result.add(a);
/*     */       }
/*     */     }
/*     */ 
/* 322 */     return result.toArray();
/*     */   }
/*     */ 
/*     */   protected Map getInscopeNamespaces(Node apexNode)
/*     */     throws Exception
/*     */   {
/* 337 */     Map result = new HashMap();
/*     */ 
/* 339 */     if (apexNode.getNodeType() != 1) {
/* 340 */       return result;
/*     */     }
/*     */ 
/* 343 */     Element apexElement = (Element)apexNode;
/*     */ 
/* 345 */     Node parent = apexElement.getParentNode();
/* 346 */     while ((parent != null) && (parent.getNodeType() == 1))
/*     */     {
/* 348 */       NamedNodeMap attributes = parent.getAttributes();
/* 349 */       int nrOfAttrs = attributes.getLength();
/*     */ 
/* 351 */       for (int i = 0; i < nrOfAttrs; ++i) {
/* 352 */         Attr currentAttr = (Attr)attributes.item(i);
/* 353 */         String name = currentAttr.getNodeName();
/* 354 */         String value = currentAttr.getValue();
/*     */ 
/* 356 */         if ((name.equals("xmlns")) && (value.equals("")))
/*     */           continue;
/* 358 */         if (((!name.startsWith("xml:")) && ((
/* 359 */           (!name.startsWith("xmlns")) || (value.equals(""))))) || 
/* 360 */           (result.containsKey(name))) continue;
/* 361 */         result.put(name, value);
/*     */       }
/* 347 */       parent = parent.getParentNode();
/*     */     }
/*     */ 
/* 367 */     return result;
/*     */   }
/*     */ 
/*     */   static int getPositionRelativeToDocumentElement(Node currentNode)
/*     */   {
/* 385 */     if (currentNode == null) {
/* 386 */       return 0;
/*     */     }
/*     */ 
/* 389 */     Document doc = currentNode.getOwnerDocument();
/*     */ 
/* 391 */     if (currentNode.getParentNode() != doc) {
/* 392 */       return 0;
/*     */     }
/*     */ 
/* 395 */     Element documentElement = doc.getDocumentElement();
/*     */ 
/* 397 */     if (documentElement == null) {
/* 398 */       return 0;
/*     */     }
/*     */ 
/* 401 */     if (documentElement == currentNode) {
/* 402 */       return 0;
/*     */     }
/*     */ 
/* 405 */     for (Node x = currentNode; x != null; x = x.getNextSibling()) {
/* 406 */       if (x == documentElement) {
/* 407 */         return -1;
/*     */       }
/*     */     }
/*     */ 
/* 411 */     return 1;
/*     */   }
/*     */ 
/*     */   void outputAttrToWriter(String name, String value)
/*     */     throws IOException
/*     */   {
/* 435 */     this._writer.write(" ");
/* 436 */     this._writer.write(name);
/* 437 */     this._writer.write("=\"");
/*     */ 
/* 439 */     int length = value.length();
/*     */ 
/* 441 */     for (int i = 0; i < length; ++i) {
/* 442 */       char c = value.charAt(i);
/*     */ 
/* 444 */       switch (c)
/*     */       {
/*     */       case '&':
/* 447 */         this._writer.write("&amp;");
/* 448 */         break;
/*     */       case '<':
/* 451 */         this._writer.write("&lt;");
/* 452 */         break;
/*     */       case '"':
/* 455 */         this._writer.write("&quot;");
/* 456 */         break;
/*     */       case '\t':
/* 459 */         this._writer.write("&#x9;");
/* 460 */         break;
/*     */       case '\n':
/* 463 */         this._writer.write("&#xA;");
/* 464 */         break;
/*     */       case '\r':
/* 467 */         this._writer.write("&#xD;");
/* 468 */         break;
/*     */       default:
/* 471 */         this._writer.write(c);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 476 */     this._writer.write("\"");
/*     */   }
/*     */ 
/*     */   void outputPItoWriter(ProcessingInstruction currentPI)
/*     */     throws IOException
/*     */   {
/* 487 */     this._writer.write("<?");
/*     */ 
/* 489 */     String target = currentPI.getTarget();
/* 490 */     int length = target.length();
/*     */ 
/* 492 */     for (int i = 0; i < length; ++i) {
/* 493 */       char c = target.charAt(i);
/*     */ 
/* 495 */       switch (c)
/*     */       {
/*     */       case '\r':
/* 498 */         this._writer.write("&#xD;");
/* 499 */         break;
/*     */       default:
/* 502 */         this._writer.write(c);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 507 */     String data = currentPI.getData();
/*     */ 
/* 509 */     length = data.length();
/*     */ 
/* 511 */     if ((data != null) && (length > 0)) {
/* 512 */       this._writer.write(" ");
/*     */ 
/* 514 */       for (int i = 0; i < length; ++i) {
/* 515 */         char c = data.charAt(i);
/*     */ 
/* 517 */         switch (c)
/*     */         {
/*     */         case '\r':
/* 520 */           this._writer.write("&#xD;");
/* 521 */           break;
/*     */         default:
/* 524 */           this._writer.write(c);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 530 */     this._writer.write("?>");
/*     */   }
/*     */ 
/*     */   void outputCommentToWriter(Comment currentComment)
/*     */     throws IOException
/*     */   {
/* 541 */     this._writer.write("<!--");
/*     */ 
/* 543 */     String data = currentComment.getData();
/* 544 */     int length = data.length();
/*     */ 
/* 546 */     for (int i = 0; i < length; ++i) {
/* 547 */       char c = data.charAt(i);
/*     */ 
/* 549 */       switch (c)
/*     */       {
/*     */       case '\r':
/* 552 */         this._writer.write("&#xD;");
/* 553 */         break;
/*     */       default:
/* 556 */         this._writer.write(c);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 561 */     this._writer.write("-->");
/*     */   }
/*     */ 
/*     */   void outputTextToWriter(String text)
/*     */     throws IOException
/*     */   {
/* 572 */     int length = text.length();
/*     */ 
/* 574 */     for (int i = 0; i < length; ++i) {
/* 575 */       char c = text.charAt(i);
/*     */ 
/* 577 */       switch (c)
/*     */       {
/*     */       case '&':
/* 580 */         this._writer.write("&amp;");
/* 581 */         break;
/*     */       case '<':
/* 584 */         this._writer.write("&lt;");
/* 585 */         break;
/*     */       case '>':
/* 588 */         this._writer.write("&gt;");
/* 589 */         break;
/*     */       case '\r':
/* 592 */         this._writer.write("&#xD;");
/* 593 */         break;
/*     */       default:
/* 596 */         this._writer.write(c);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Document getOwnerDocument(Node node)
/*     */   {
/* 614 */     if (node.getNodeType() == 9) {
/* 615 */       return (Document)node;
/*     */     }
/* 617 */     return node.getOwnerDocument();
/*     */   }
/*     */ 
/*     */   public static final Object[] sortAttributes(Object[] namednodemap)
/*     */   {
/* 628 */     if (namednodemap == null) {
/* 629 */       return new Attr[0];
/*     */     }
/*     */ 
/* 632 */     Arrays.sort(namednodemap, new AttrCompare());
/*     */ 
/* 634 */     return namednodemap;
/*     */   }
/*     */ 
/*     */   public static boolean namespaceIsRelative(String namespaceValue)
/*     */   {
/* 644 */     return !namespaceIsAbsolute(namespaceValue);
/*     */   }
/*     */ 
/*     */   public static boolean namespaceIsAbsolute(String namespaceValue)
/*     */   {
/* 656 */     if (namespaceValue.length() == 0) {
/* 657 */       return true;
/*     */     }
/*     */ 
/* 660 */     boolean foundColon = false;
/* 661 */     int length = namespaceValue.length();
/*     */ 
/* 663 */     for (int i = 0; i < length; ++i) {
/* 664 */       char c = namespaceValue.charAt(i);
/*     */ 
/* 666 */       if (c == ':')
/* 667 */         foundColon = true;
/* 668 */       else if ((!foundColon) && (c == '/')) {
/* 669 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 673 */     return foundColon;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 678 */     String input = "<!DOCTYPE doc [<!ATTLIST e9 attr CDATA \"default\">]>\n<!-- Comment 2 --><doc>\n   <e1   />\n   <e2   ></e2>\n   <e3    name = \"elem3\"   id=\"elem3\"    />\n   <e4    name=\"elem4\"   id=\"elem4\"    ></e4>\n   <e5 a:attr=\"out\" b:attr=\"sorted\" attr2=\"all\" attr=\"I'm\"\n       xmlns:b=\"http://www.ietf.org\"\n       xmlns:a=\"http://www.w3.org\"\n       xmlns=\"http://example.org\"/>\n   <e6 xmlns=\"\" xmlns:a=\"http://www.w3.org\">\n       <e7 xmlns=\"http://www.ietf.org\">\n           <e8 xmlns=\"\" xmlns:a=\"http://www.w3.org\">\n               <e9 xmlns=\"\" xmlns:a=\"http://www.ietf.org\"/>\n               <text>&#169;</text>\n           </e8>\n       </e7>\n   </e6>\n</doc><!-- Comment 3 -->\n";
/*     */ 
/* 700 */     input = "<doc xmlns:x=\"http://w3.org/2\" xmlns:y=\"http://w3.org/1\"><x:e a=\"a\"/><x:e x:a=\"x:a\"/><e x:a=\"x:a\"/><e x:a=\"x:a\" y:a=\"y:a\"/><e x:a=\"x:a\" a=\"a\"/><e x:a=\"x:a\" x:b=\"x:b\"/></doc>";
/*     */ 
/* 710 */     DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
/* 711 */     dfactory.setNamespaceAware(true);
/*     */ 
/* 713 */     DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();
/* 714 */     byte[] inputBytes = input.getBytes();
/* 715 */     Document doc = documentBuilder.parse(new ByteArrayInputStream(inputBytes));
/*     */ 
/* 718 */     Canonicalizer20010315 c14n = new Canonicalizer20010315();
/*     */ 
/* 720 */     byte[] outputBytes = c14n.engineCanonicalizeSubTree(doc);
/* 721 */     System.out.println(new String(outputBytes));
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.Canonicalizer20010315
 * JD-Core Version:    0.5.4
 */