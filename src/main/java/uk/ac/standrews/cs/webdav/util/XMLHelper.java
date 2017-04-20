package uk.ac.standrews.cs.webdav.util;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Ben Catherall, graham
 */
public class XMLHelper {

    private static XMLHelper instance = null;
    
	private Document document;
    private OutputFormat outputFormat;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;

    public XMLHelper() {
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setExpandEntityReferences(false);
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            outputFormat = new OutputFormat(document);
            outputFormat.setLineSeparator("\r\n");
            outputFormat.setLineWidth(80);
            outputFormat.setIndenting(true);
            outputFormat.setIndent(2);
            outputFormat.setEncoding("utf-8");
        } catch (Exception e) {
            ErrorHandling.exceptionError(e, "Unable to create document: ", e);
        }
    }
    
    // Methods synchronized since DocumentBuilderFactory isn't thread-safe.

    public synchronized Element createElement(String elName) {
        return document.createElement(elName);
    }

    public synchronized Element createElement(String namespace, String elName) {
        Element e = document.createElementNS(namespace, elName);
        return e;
    }

    public synchronized Text createText(String text) {
        return document.createTextNode(text);
    }

    public synchronized CDATASection createCDATA(String text) {
        return document.createCDATASection(text);
    }

    public synchronized void writeXML(Element element, Writer writer) {
        try {
            XMLSerializer serializer = new XMLSerializer(writer, outputFormat);
            serializer.serialize(element);
        } catch (IOException e) {
            ErrorHandling.exceptionError(e, "While serializing XML", e);
        }
    }
    
    public synchronized String flattenXML(Element element) {

		StringWriter string_writer = new StringWriter();

		// Don't want a full XML document, just a fragment.
		OutputFormat format = new OutputFormat();
		format.setOmitDocumentType(true);
		format.setOmitXMLDeclaration(true);
		
		XMLSerializer output = new XMLSerializer(string_writer, format);
		
		try {
			output.serialize(element);
		} catch (IOException e) {
			ErrorHandling.exceptionError(e, "IO exception", e);
			return "";
		}
		return string_writer.getBuffer().toString();
	}
    
    public synchronized Element createElementWithText(String elName, String text) {
        Element el = createElement(elName);
        el.appendChild(createText(text));
        return el;
    }

    public synchronized Element createElementWithText(String namespace, String elName, String text) {
        Element el = createElement(namespace, elName);
        el.appendChild(createText(text));
        return el;
    }

    public synchronized Element createElementWithCDATA(String elName, String text) {
        Element el = createElement(elName);
        el.appendChild(createCDATA(text));
        return el;
    }

    public synchronized Document parse(InputStream in) throws IOException {
        try {
            Document d = documentBuilder.parse(in);
            return d;
        } catch (SAXException e) {
            throw new IOException(e.toString());
        }
    }
    
    public static synchronized XMLHelper getInstance() {
    	
    	if (instance  == null) instance = new XMLHelper();
    	return instance;
    }
}
