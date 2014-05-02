package de.htwsaarland.xmlgrep;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.Locator2Impl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>PositionalXMLReader class.</p>
 *
 * @author hbui Angepasst aus
 * http://stackoverflow.com/questions/4915422/get-line-number-from-xml-node-java
 * suggested by Michael Kay
 * @version $Id: $Id
 */
public final class PositionalXMLReader {
	private static final EntityResolver resolver = new IgnoreDTDInDOCTYPE();
			
	
	/**
	 * <p>readXML.</p>
	 *
	 * @param is a {@link java.io.InputStream} object.
	 * @return a {@link org.w3c.dom.Document} object.
	 * @throws java.io.IOException if any.
	 * @throws org.xml.sax.SAXException if any.
	 * @throws javax.xml.parsers.ParserConfigurationException if any.
	 */
	public static Document readXML(final InputStream is)
			throws IOException, SAXException, ParserConfigurationException {
		final Document doc;


		final DocumentBuilderFactory docBuilderFactory =
				DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringComments(true);
		docBuilderFactory.setExpandEntityReferences(false);
		docBuilderFactory.setIgnoringElementContentWhitespace(true);



		final DocumentBuilder docBuilder =
				docBuilderFactory.newDocumentBuilder();

		doc = docBuilder.newDocument();


		final Stack<Element> elementStack = new Stack<Element>();

		final DefaultHandler2 handler = new DefaultHandler2() {
			private Locator locator = new Locator2Impl();
			final StringBuilder textBuffer = new StringBuilder();
			private boolean inCDATA = false;
			final StringBuilder cDATABuffer = new StringBuilder();

			@Override
			public void setDocumentLocator(final Locator locator) {
				this.locator = locator;
				// Save the locator, so that it can be used later 
				// for line tracking when traversing nodes.
			}

			@Override
			public void startCDATA() throws SAXException {
				this.inCDATA = true;
			}

			@Override
			public void endCDATA() throws SAXException {
				this.inCDATA = false;
			}

			@Override
			public void startElement(
					final String uri,
					final String localName,
					final String qName,
					final Attributes attributes)
					throws SAXException {

				addTextIfNeeded();
				final Element el = doc.createElement(qName);
				for (int i = 0; i < attributes.getLength(); i++) {
					el.setAttribute(attributes.getQName(i), attributes.getValue(i));
				}

				el.setUserData(LSDebuger.LINE_NUMBER_KEY_NAME,
						String.valueOf(locator.getLineNumber()), null);

				elementStack.push(el);
			}

			@Override
			public void endElement(final String uri, final String localName, final String qName) {
				addTextIfNeeded();
				final Element closedEl = elementStack.pop();
				if (elementStack.isEmpty()) { // Is this the root element?
					doc.appendChild(closedEl);
				} else {
					final Element parentEl = elementStack.peek();
					parentEl.appendChild(closedEl);
				}
			}

			@Override
			public void characters(final char ch[], final int start, final int length) throws SAXException {
				//System.out.println(inCDATA);
				//System.out.println(new String(ch).trim());
				if (inCDATA) {
					cDATABuffer.append(ch, start, length);
				} else {
					textBuffer.append(ch, start, length);
				}
			}

			// Outputs text accumulated under the current node
			private void addTextIfNeeded() {
				if (!elementStack.isEmpty()) {
					final Element el = elementStack.peek();
					if (textBuffer.length() > 0) {
						final Node textNode = doc.createTextNode(textBuffer.toString());
						el.appendChild(textNode);
						textBuffer.delete(0, textBuffer.length());
						textNode.setUserData(LSDebuger.LINE_NUMBER_KEY_NAME, String.valueOf(locator.getLineNumber()), null);
					}
					if (cDATABuffer.length() > 0) {
						final Node textNode = doc.createCDATASection(cDATABuffer.toString());
						el.appendChild(textNode);
						cDATABuffer.delete(0, cDATABuffer.length());
						textNode.setUserData(LSDebuger.LINE_NUMBER_KEY_NAME, String.valueOf(locator.getLineNumber()), null);
					}
				}
			}
		};

		final XMLReader reader = XMLReaderFactory.createXMLReader();
		//SAXParser parser;
		//SAXParser parser = factory.newSAXParser(); //.parse(is, handler);
		//parser.parse(is, handler);
		// ignore den Tag <!DOCTYPE .... > Es beschleunigt den Parsen-Vorgang
		reader.setEntityResolver(resolver);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		reader.setContentHandler(handler);
		reader.parse(new InputSource(is));
		return doc;
	}
}

/**
 * Diese Klasse wirkt, dass der Parser den DTD ignoriert. Somit werden die 
 * Test beschleunigt. Mehrere Informationen kann man hier lesen:
 * 
 * 		http://www.w3.org/blog/systeam/2008/02/08/w3c_s_excessive_dtd_traffic/
 * 
 * Also spart man doch die Traffic. 
 * 
 */
class IgnoreDTDInDOCTYPE implements EntityResolver {

	/** {@inheritDoc} */
	@Override
	public InputSource resolveEntity(String publicId, String systemId) {
		return new InputSource(new ByteArrayInputStream(new byte[]{}));
	}
}
