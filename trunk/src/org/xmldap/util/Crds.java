package org.xmldap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import nu.xom.Attribute;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.ValidityException;

public class Crds {

	static public final String CRDS_CHAR_ENCODING = "UTF-8";

	private Crds() {
	}

	static public Crds getInstance() {
		return new Crds();
	}

	private Node findInNodes(Nodes nodes, String ppi) {
		for (int i = 0; i < nodes.size() - 1; i++) {
			Node node = nodes.get(i);
			String p = node.getValue();
			if (ppi.equals(p)) {
				return node;
			}
		}
		return null;
	}

	private static void xmlAdd(Element to, Element element) {
		Element newElt = new Element(element.getLocalName());
		// Show the tag, along with its attributes
		for (int i = 0; i < element.getAttributeCount(); i++) {
			Attribute attr = element.getAttribute(i);
			newElt.addAttribute(attr);
		}
		to.appendChild(newElt);
		// Now loop through child nodes
		for (int i = 0; i < element.getChildCount(); i++) {
			Node node = element.getChild(i);

			if (node instanceof Text) {
				String value = node.getValue();
				Text text = new Text(value);
				newElt.appendChild(text);
			} else if (node instanceof Element) {
				xmlAdd(newElt, (Element) node);
			}
		}
	}

	public String getAllCards(String dirName, String password) {
		try {
			Document cardStore = XmlFileUtil.readXmlFile(dirName
					+ "cardStore.xml");
			return cardStore.toXML();
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getCard(String dirName, String password, String id) {
		try {
			Document cardStore = XmlFileUtil.readXmlFile(dirName
					+ "cardStore.xml");
			Nodes nodes = cardStore.query("//infocard/id");
			Node card = findInNodes(nodes, id);
			if (card != null) {
				return card.toXML();
			} else {
				return null;
			}
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String newCard(String dirName, String password, Document infocard) {
		try {
			File file = new File(dirName + "cardStore.xml");

			// Create file if it does not exist
			boolean success = file.createNewFile();
			if (success) {
				// File did not exist and was created
				Element root = new Element("infocards");
				Document cardStore = new Document(root);
				Element fromRoot = infocard.getRootElement();
				System.out.println(fromRoot.toXML());
				xmlAdd(cardStore.getRootElement(), fromRoot);
				FileOutputStream fos = new FileOutputStream(file);
				Serializer ser = new Serializer(fos, "UTF-8");
				ser.setIndent(1);
				ser.write(cardStore);
				ser.flush();
				fos.close();
			} else {
				// File already exists
				try {
					String ppi = infocard.query(
							"//infocard/privatepersonalidentifier").get(0)
							.getValue();

					Document cardStore = XmlFileUtil.readXmlFile(dirName
							+ "cardStore.xml");
					Nodes ppis = cardStore
							.query("//infocard/privatepersonalidentifier");
					Node ppiNode = findInNodes(ppis, ppi);
					if (ppiNode != null) {
						// this should never happen as the ppi is updated when a
						// card is edited
					} else {
						// infocard is a new card. Add it
						Element fromRoot = infocard.getRootElement();
						xmlAdd(cardStore.getRootElement(), fromRoot);
					}
					FileOutputStream fos = new FileOutputStream(file);
					Serializer ser = new Serializer(fos, "UTF-8");
					ser.setIndent(1);
					ser.write(cardStore);
					ser.flush();
					fos.close();
				} catch (ValidityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParsingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
		}

		return "";
	}

}
