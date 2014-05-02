package de.htwsaarland.xmlgrep;

import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Xmlgrep extends DefaultHandler {

	

	public static void main(String[] args) 
			throws SAXException, IOException, XPathExpressionException, ParserConfigurationException {

		Document doc = PositionalXMLReader.readXML(new FileInputStream(args[0]));

		XPath xpath = XPathFactory.newInstance().newXPath();
		System.out.println("query: " + args[1]);
		NodeList nl = (NodeList) xpath.evaluate(args[1], doc, XPathConstants.NODESET);
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String defLine = LSDebuger.getLineNumberOfNode(n);
			String path = LSDebuger.getXPath(n);
			System.out.println(path + " " + defLine);
		}
		System.out.println("Found " + nl.getLength() + " node(s) in " + args[0]);
	}
}
