package de.htwsaarland.xmlgrep;

import org.w3c.dom.Node;

/**
 * <p>LSDebuger class.</p>
 *
 * @author hbui
 * @version $Id: $Id
 */
public final class LSDebuger {
	/** Constant <code>UNDEFINED=0</code> */
	public static final int UNDEFINED = 0;
	/** Constant <code>LINE_NUMBER_KEY_NAME="lineNumber"</code> */
	public static final String LINE_NUMBER_KEY_NAME = "lineNumber";
	/** Constant <code>CANNOT_PARSE_LINE=-1</code> */
	public static final int CANNOT_PARSE_LINE = -1;
	/** Constant <code>UNDEFINED_LINE_STRING="0"</code> */
	public static final String UNDEFINED_LINE_STRING = "0";

	/**
	 * <p>getXPath.</p>
	 *
	 * @param node a {@link org.w3c.dom.Node} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getXPath(Node node) {
		Node parent = node.getParentNode();
		if (parent != null) {
			return getXPath(parent) + "|" + node.getNodeName();
		} else {
			return node.getNodeName();
		}
	}

	/**
	 * Hold die Zeile-Nummer von dem Node wenn vorhanden isextentedPoint raus,
	 * oder gibextentedPoint "0" zurueckextentedPoint wenn keine.
	 *
	 * @param e a {@link org.w3c.dom.Node} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getLineNumberOfNode(Node e) {
		return Integer.toString(getIntLineNumberOfNode(e));
	}
	
	/**
	 * <p>getIntLineNumberOfNode.</p>
	 *
	 * @param e a {@link org.w3c.dom.Node} object.
	 * @return a int.
	 */
	public static int getIntLineNumberOfNode(Node e) {
		Object nummer = e.getUserData(LINE_NUMBER_KEY_NAME);
		try{
			if (nummer == null) {
				return UNDEFINED;
			} else {
				return  Integer.parseInt(  nummer.toString() );
			}
		}catch(NumberFormatException ex){
			return CANNOT_PARSE_LINE;
		}
	}
}
