/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ public class XMLResourceBundleImpl extends XMLResourceBundle
/*     */   implements ErrorHandler
/*     */ {
/*  68 */   private static String CLASS_NAME = "XMLResourceBundle";
/*     */ 
/*  70 */   private static String CONSTRUCTOR_ERROR = "Cannot load XMLResourceBundle ";
/*  71 */   private static String NULL_KEY = "Key cannot be null";
/*  72 */   private static String KEY_NOT_FOUND = "Could not find Node with key ";
/*  73 */   private static String INDEX_NOT_FOUND = ", index ";
/*  74 */   private static String ATTRIBUTE_NOT_FOUND = ", attribute ";
/*  75 */   private static String CHILD_NOT_FOUND = ", child ";
/*     */   static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
/*     */   static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
/*     */   private Locale locale;
/*     */   private Document document;
/*     */ 
/*     */   public XMLResourceBundleImpl(InputStream bundle, Locale locale, boolean validate)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 104 */       this.locale = locale;
/*     */ 
/* 107 */       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/*     */ 
/* 109 */       if (validate) {
/* 110 */         dbf.setNamespaceAware(true);
/* 111 */         dbf.setValidating(true);
/* 112 */         dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
/*     */       }
/*     */ 
/* 115 */       DocumentBuilder db = dbf.newDocumentBuilder();
/*     */ 
/* 117 */       if (validate) {
/* 118 */         db.setErrorHandler(this);
/*     */       }
/*     */ 
/* 121 */       this.document = db.parse(new InputSource(bundle));
/*     */     }
/*     */     catch (Exception e) {
/* 124 */       throw new Exception(CONSTRUCTOR_ERROR + "(" + e + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   public Document getDocument()
/*     */   {
/* 132 */     return this.document;
/*     */   }
/*     */ 
/*     */   public void warning(SAXParseException exception)
/*     */     throws SAXException
/*     */   {
/* 140 */     throw new SAXException(exception);
/*     */   }
/*     */ 
/*     */   public void error(SAXParseException exception)
/*     */     throws SAXException
/*     */   {
/* 149 */     throw new SAXException(exception);
/*     */   }
/*     */ 
/*     */   public void fatalError(SAXParseException exception)
/*     */     throws SAXException
/*     */   {
/* 158 */     throw new SAXException(exception);
/*     */   }
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 167 */     return this.locale;
/*     */   }
/*     */ 
/*     */   public Node getNode(String key)
/*     */     throws MissingResourceException
/*     */   {
/* 176 */     return getNode(key, 0);
/*     */   }
/*     */ 
/*     */   public Node getNode(String key, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 187 */     if (key == null) {
/* 188 */       throw new NullPointerException(NULL_KEY);
/*     */     }
/*     */ 
/* 192 */     Node node = null;
/*     */     try
/*     */     {
/* 196 */       node = (Node)getKeyNodes(key).get(index);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 201 */     if (node == null)
/*     */     {
/* 203 */       String errorMessage = KEY_NOT_FOUND + key;
/*     */ 
/* 205 */       if (index != 0) {
/* 206 */         errorMessage = errorMessage + INDEX_NOT_FOUND + index;
/*     */       }
/*     */ 
/* 209 */       throw new MissingResourceException(errorMessage, CLASS_NAME, key);
/*     */     }
/*     */ 
/* 213 */     return node;
/*     */   }
/*     */ 
/*     */   public Node getNode(String key, String attrName, String attrValue)
/*     */     throws MissingResourceException
/*     */   {
/* 224 */     return getNode(key, attrName, attrValue, 0);
/*     */   }
/*     */ 
/*     */   public Node getNode(String key, String attrName, String attrValue, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 237 */     if (key == null) {
/* 238 */       throw new NullPointerException(NULL_KEY);
/*     */     }
/*     */ 
/* 242 */     Node node = null;
/*     */     try
/*     */     {
/* 246 */       node = (Node)getKeyNodes(key, attrName, attrValue).get(index);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 251 */     if (node == null)
/*     */     {
/* 253 */       String errorMessage = KEY_NOT_FOUND + key + ATTRIBUTE_NOT_FOUND + 
/* 254 */         attrName + "=" + attrValue;
/*     */ 
/* 256 */       if (index != 0) {
/* 257 */         errorMessage = errorMessage + INDEX_NOT_FOUND + index;
/*     */       }
/*     */ 
/* 260 */       throw new MissingResourceException(errorMessage, CLASS_NAME, key);
/*     */     }
/*     */ 
/* 264 */     return node;
/*     */   }
/*     */ 
/*     */   public Node getNode(String key, String attrName, String attrValue, String childKey)
/*     */     throws MissingResourceException
/*     */   {
/* 276 */     return getNode(key, attrName, attrValue, childKey, 0);
/*     */   }
/*     */ 
/*     */   public Node getNode(String key, String attrName, String attrValue, String childKey, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 290 */     if (key == null) {
/* 291 */       throw new NullPointerException(NULL_KEY);
/*     */     }
/*     */ 
/* 295 */     Node node = null;
/*     */     try
/*     */     {
/* 298 */       node = (Node)getKeyNodes(key, attrName, attrValue, childKey).get(index);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/* 304 */     if (node == null)
/*     */     {
/* 306 */       String errorMessage = KEY_NOT_FOUND + key + ATTRIBUTE_NOT_FOUND + 
/* 307 */         attrName + "=" + attrValue + CHILD_NOT_FOUND + 
/* 308 */         childKey;
/*     */ 
/* 310 */       if (index != 0) {
/* 311 */         errorMessage = errorMessage + INDEX_NOT_FOUND + index;
/*     */       }
/*     */ 
/* 314 */       throw new MissingResourceException(errorMessage, CLASS_NAME, key);
/*     */     }
/*     */ 
/* 318 */     return node;
/*     */   }
/*     */ 
/*     */   public Node getNode(String key, String attrName, String attrValue, String childKey, String childAttrName, String childAttrValue)
/*     */     throws MissingResourceException
/*     */   {
/* 332 */     return getNode(key, attrName, attrValue, 
/* 333 */       childKey, childAttrName, childAttrValue, 0);
/*     */   }
/*     */ 
/*     */   public Node getNode(String key, String attrName, String attrValue, String childKey, String childAttrName, String childAttrValue, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 349 */     if (key == null) {
/* 350 */       throw new NullPointerException(NULL_KEY);
/*     */     }
/*     */ 
/* 354 */     Node node = null;
/*     */     try
/*     */     {
/* 357 */       node = 
/* 358 */         (Node)getKeyNodes(key, attrName, attrValue, childKey, 
/* 358 */         childAttrName, childAttrValue).get(index);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/* 364 */     if (node == null)
/*     */     {
/* 366 */       String errorMessage = KEY_NOT_FOUND + key + ATTRIBUTE_NOT_FOUND + 
/* 367 */         attrName + "=" + attrValue + CHILD_NOT_FOUND + 
/* 368 */         childKey + ATTRIBUTE_NOT_FOUND + 
/* 369 */         childAttrName + "=" + childAttrValue;
/*     */ 
/* 371 */       if (index != 0) {
/* 372 */         errorMessage = errorMessage + INDEX_NOT_FOUND + index;
/*     */       }
/*     */ 
/* 375 */       throw new MissingResourceException(errorMessage, CLASS_NAME, key);
/*     */     }
/*     */ 
/* 379 */     return node;
/*     */   }
/*     */ 
/*     */   public Node getNamedChild(Node node, String childName)
/*     */   {
/* 389 */     NodeList nl = node.getChildNodes();
/* 390 */     for (int i = 0; i < nl.getLength(); ++i) {
/* 391 */       if (nl.item(i).getNodeName().equals(childName)) {
/* 392 */         return nl.item(i);
/*     */       }
/*     */     }
/* 395 */     return null;
/*     */   }
/*     */ 
/*     */   public Vector getNamedChildren(Node node, String childName)
/*     */   {
/* 405 */     Vector children = new Vector();
/* 406 */     NodeList nl = node.getChildNodes();
/* 407 */     for (int i = 0; i < nl.getLength(); ++i) {
/* 408 */       Node child = nl.item(i);
/* 409 */       if (child.getNodeName().equals(childName)) {
/* 410 */         children.addElement(child);
/*     */       }
/*     */     }
/* 413 */     return children;
/*     */   }
/*     */ 
/*     */   public String getNamedAttribut(Node node, String attributName)
/*     */   {
/* 424 */     NamedNodeMap map = node.getAttributes();
/* 425 */     return map.getNamedItem(attributName).getNodeValue();
/*     */   }
/*     */ 
/*     */   public String getString(String key)
/*     */     throws MissingResourceException
/*     */   {
/* 433 */     return getString(key, 0);
/*     */   }
/*     */ 
/*     */   public String getString(String key, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 443 */     return getNodeValue(getNode(key, index));
/*     */   }
/*     */ 
/*     */   public int getNbOfElement(String key)
/*     */   {
/* 451 */     String parentName = "";
/* 452 */     String nodeName = "";
/*     */ 
/* 455 */     if (key.indexOf('.') > 0) {
/* 456 */       parentName = key.substring(0, key.lastIndexOf('.'));
/*     */ 
/* 458 */       nodeName = key.substring(key.lastIndexOf('.') + 1);
/*     */     } else {
/* 460 */       parentName = this.document.getDocumentElement().getNodeName();
/* 461 */       if (parentName.equals(key)) {
/* 462 */         return 1;
/*     */       }
/* 464 */       return 0;
/*     */     }
/*     */ 
/* 467 */     Node parentNode = getNode(parentName);
/* 468 */     NodeList nl = parentNode.getChildNodes();
/* 469 */     int count = 0;
/* 470 */     for (int i = 0; i < nl.getLength(); ++i) {
/* 471 */       if (nl.item(i).getNodeName().equals(nodeName)) {
/* 472 */         ++count;
/*     */       }
/*     */     }
/* 475 */     return count;
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName)
/*     */     throws MissingResourceException
/*     */   {
/* 484 */     return getString(key, attrName, 0);
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 495 */     Element e = (Element)getNode(key, index);
/*     */ 
/* 499 */     if (!e.hasAttribute(attrName))
/*     */     {
/* 501 */       String errorMessage = KEY_NOT_FOUND + key + ATTRIBUTE_NOT_FOUND + 
/* 502 */         attrName;
/*     */ 
/* 504 */       if (index != 0) {
/* 505 */         errorMessage = errorMessage + INDEX_NOT_FOUND + index;
/*     */       }
/*     */ 
/* 508 */       throw new MissingResourceException(errorMessage, CLASS_NAME, key);
/*     */     }
/*     */ 
/* 512 */     return e.getAttribute(attrName);
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue)
/*     */     throws MissingResourceException
/*     */   {
/* 523 */     return getString(key, attrName, attrValue, 0);
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 535 */     return getNodeValue(getNode(key, attrName, attrValue, index));
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue, String childKey)
/*     */     throws MissingResourceException
/*     */   {
/* 547 */     return getString(key, attrName, attrValue, childKey, 0);
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue, String childKey, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 560 */     return getNodeValue(getNode(key, attrName, attrValue, childKey, index));
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue, String childKey, String childAttrName)
/*     */     throws MissingResourceException
/*     */   {
/* 574 */     return getString(key, attrName, attrValue, childKey, childAttrName, 0);
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue, String childKey, String childAttrName, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 589 */     Element e = (Element)getNode(key, attrName, attrValue, childKey, index);
/*     */ 
/* 594 */     if (!e.hasAttribute(childAttrName))
/*     */     {
/* 596 */       String errorMessage = KEY_NOT_FOUND + key + ATTRIBUTE_NOT_FOUND + 
/* 597 */         attrName + "=" + attrValue + CHILD_NOT_FOUND + 
/* 598 */         childKey + ATTRIBUTE_NOT_FOUND + 
/* 599 */         childAttrName;
/*     */ 
/* 601 */       if (index != 0) {
/* 602 */         errorMessage = errorMessage + INDEX_NOT_FOUND + index;
/*     */       }
/*     */ 
/* 605 */       throw new MissingResourceException(errorMessage, CLASS_NAME, key);
/*     */     }
/*     */ 
/* 609 */     return e.getAttribute(childAttrName);
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue, String childKey, String childAttrName, String childAttrValue)
/*     */     throws MissingResourceException
/*     */   {
/* 624 */     return getString(
/* 625 */       key, attrName, attrValue, childKey, childAttrName, childAttrValue, 0);
/*     */   }
/*     */ 
/*     */   public String getString(String key, String attrName, String attrValue, String childKey, String childAttrName, String childAttrValue, int index)
/*     */     throws MissingResourceException
/*     */   {
/* 640 */     return getNodeValue(
/* 641 */       getNode(key, attrName, attrValue, childKey, 
/* 641 */       childAttrName, childAttrValue, index));
/*     */   }
/*     */ 
/*     */   private Vector getChildrenByTagName(Node parentNode, String childName)
/*     */   {
/* 654 */     Vector children = new Vector();
/*     */ 
/* 656 */     NodeList nl = parentNode.getChildNodes();
/* 657 */     for (int i = 0; i < nl.getLength(); ++i) {
/* 658 */       if (nl.item(i).getNodeName().equals(childName)) {
/* 659 */         children.add(nl.item(i));
/*     */       }
/*     */     }
/*     */ 
/* 663 */     return children;
/*     */   }
/*     */ 
/*     */   private String[] getKeyPath(String key)
/*     */     throws Exception
/*     */   {
/* 678 */     StringTokenizer st = new StringTokenizer(key, ".");
/* 679 */     String[] nodeNames = new String[st.countTokens()];
/* 680 */     int i = 0;
/* 681 */     while (st.hasMoreTokens()) {
/* 682 */       nodeNames[(i++)] = st.nextToken();
/*     */     }
/*     */ 
/* 685 */     return nodeNames;
/*     */   }
/*     */ 
/*     */   private Vector getKeyNodes(String[] keyPath, int depth, Vector children)
/*     */   {
/* 700 */     if (depth == keyPath.length - 1) {
/* 701 */       return children;
/*     */     }
/*     */ 
/* 705 */     String nextNodeName = keyPath[(++depth)];
/*     */ 
/* 707 */     Vector newChildren = new Vector();
/*     */ 
/* 709 */     for (int i = 0; i < children.size(); ++i) {
/* 710 */       Vector tmp = getChildrenByTagName((Node)children.get(i), nextNodeName);
/* 711 */       for (int j = 0; j < tmp.size(); ++j) {
/* 712 */         newChildren.add(tmp.get(j));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 717 */     return getKeyNodes(keyPath, depth, newChildren);
/*     */   }
/*     */ 
/*     */   private Vector getKeyNodes(String key)
/*     */     throws Exception
/*     */   {
/* 731 */     String[] keyPath = getKeyPath(key);
/*     */ 
/* 734 */     Vector firstChildren = new Vector();
/* 735 */     if (this.document.getDocumentElement().getNodeName().equals(keyPath[0])) {
/* 736 */       firstChildren.add(this.document.getDocumentElement());
/*     */     }
/*     */ 
/* 740 */     return getKeyNodes(keyPath, 0, firstChildren);
/*     */   }
/*     */ 
/*     */   private Vector getKeyNodes(String key, String attrName, String attrValue)
/*     */     throws Exception
/*     */   {
/* 757 */     Vector keyNodes = getKeyNodes(key);
/*     */ 
/* 760 */     Vector finalNodes = new Vector();
/*     */ 
/* 762 */     Enumeration en = keyNodes.elements();
/* 763 */     while (en.hasMoreElements()) {
/* 764 */       Element node = (Element)en.nextElement();
/*     */ 
/* 766 */       if (node.getAttribute(attrName).equals(attrValue)) {
/* 767 */         finalNodes.add(node);
/*     */       }
/*     */     }
/*     */ 
/* 771 */     return finalNodes;
/*     */   }
/*     */ 
/*     */   private Vector getKeyNodes(String key, String attrName, String attrValue, String childKey)
/*     */     throws Exception
/*     */   {
/* 791 */     Vector keyNodes = getKeyNodes(key, attrName, attrValue);
/*     */ 
/* 794 */     String[] childKeyPath = getKeyPath(childKey);
/*     */ 
/* 797 */     Vector children = new Vector();
/*     */ 
/* 799 */     Enumeration en = keyNodes.elements();
/* 800 */     while (en.hasMoreElements()) {
/* 801 */       Node node = (Node)en.nextElement();
/* 802 */       children.addAll(getChildrenByTagName(node, childKeyPath[0]));
/*     */     }
/*     */ 
/* 805 */     Vector finalNodes = getKeyNodes(childKeyPath, 0, children);
/*     */ 
/* 807 */     return finalNodes;
/*     */   }
/*     */ 
/*     */   private Vector getKeyNodes(String key, String attrName, String attrValue, String childKey, String childAttrName, String childAttrValue)
/*     */     throws Exception
/*     */   {
/* 831 */     Vector keyNodes = getKeyNodes(key, attrName, attrValue, childKey);
/*     */ 
/* 834 */     Vector finalNodes = new Vector();
/*     */ 
/* 836 */     Enumeration en = keyNodes.elements();
/* 837 */     while (en.hasMoreElements()) {
/* 838 */       Element node = (Element)en.nextElement();
/*     */ 
/* 840 */       if (node.getAttribute(childAttrName).equals(childAttrValue)) {
/* 841 */         finalNodes.add(node);
/*     */       }
/*     */     }
/*     */ 
/* 845 */     return finalNodes;
/*     */   }
/*     */ 
/*     */   private String getNodeValue(Node node)
/*     */   {
/* 857 */     String value = "";
/*     */     try
/*     */     {
/* 860 */       value = node.getFirstChild().getNodeValue().trim();
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 864 */     return value;
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.XMLResourceBundleImpl
 * JD-Core Version:    0.5.4
 */