import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class DOMValidator {
	public static void main(String[] args) {
		// combine two files for validating
		combineFiles(args);
	 
		try {
			File xmlFile = new File("conbined.xml");
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();

			docBuilderFactory.setValidating(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder(); 
			ErrorHandler errorHandler = new MyErrorHandler();
			docBuilder.setErrorHandler(errorHandler); //
			Document d = docBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			System.out.println(e.toString());
		} catch (SAXException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		} 

	}

	private static void combineFiles(String[] args) {
		String contents = "";
		String s, firstLine = "";
		try {

			for (int i =0; i< args.length;i++) {
				BufferedReader in = new BufferedReader(new FileReader(args[i]));
				while ((s = in.readLine()) != null) {
					if (s.contains("xml version")) {
						firstLine = s;
					} else {
						contents += s +"\n";
						//System.out.println(s);
					}
				}
				in.close();
			}

			BufferedWriter out = new BufferedWriter(new FileWriter(
					"conbined.xml"));

			out.write(firstLine);
			out.newLine();
			out.write(contents);

			out.close();

		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}

	}

	private static class MyErrorHandler implements ErrorHandler {
		public void warning(SAXParseException e) throws SAXException {
			System.out.println("Warning: ");
			printInfo(e);
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println("Error: ");
			printInfo(e);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println("Fattal error: ");
			printInfo(e);
		}

		private void printInfo(SAXParseException e) {
			System.out.println("   Public ID: " + e.getPublicId());
			System.out.println("   System ID: " + e.getSystemId());
			System.out.println("   Line number: " + e.getLineNumber());
			System.out.println("   Column number: " + e.getColumnNumber());
			System.out.println("   Message: " + e.getMessage());
		}
	}

}