import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Server {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(4080), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null);
		server.start();
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			BufferedReader httpInput = new BufferedReader(new InputStreamReader(
					t.getRequestBody(), StandardCharsets.UTF_8));
			StringBuilder in = new StringBuilder();
			char[] charBuffer = new char[8 * 1024];
			int numCharsRead;
			while ((numCharsRead = httpInput.read(charBuffer, 0, charBuffer.length)) != -1) {
				in.append(charBuffer, 0, numCharsRead);
			}

			Data data;

			InputStream stream = new ByteArrayInputStream(in.toString().getBytes(StandardCharsets.UTF_8));
			try {
				data = handleRequest(stream);
			} catch (XPathExpressionException | ParserConfigurationException | SAXException e) {
				throw new RuntimeException(e);
			}

			MathResult m = null;
			Statistics s = null;
			if (data.isMath)
				m = new MathResult(data.num1, data.num2);
			if (data.isString)
				s = calculateString(data.str);


			t.sendResponseHeaders(200, 0);
			try (OutputStream os = t.getResponseBody()) {
				Document doc = writeXml(os, m, s);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(os);
				transformer.transform(source, result);
			} catch (TransformerException | ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
			stream.close();

		}

		public Data handleRequest(InputStream stream) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document doc = builder.parse(stream); //input stream of response.

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();

			XPathExpression expr = xpath.compile("//str"); // Look for status tag value.
			String str = expr.evaluate(doc);

			XPathExpression expr1 = xpath.compile("//num1"); // Look for status tag value.
			XPathExpression expr2 = xpath.compile("//num2"); // Look for status tag value.
			int num1;
			int num2;
			try {
				num1 = Integer.parseInt(expr1.evaluate(doc));
				num2 = Integer.parseInt(expr2.evaluate(doc));
				if (!str.isEmpty())
					return new Data(num1, num2, str);
				else
					return new Data(num1, num2);
			} catch (Exception e) {
				//ignore
			}
			return new Data(str);
		}

		private static Document writeXml(OutputStream output, MathResult math, Statistics stats)
				throws TransformerException, ParserConfigurationException {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element rootElement = doc.createElement("root");
			doc.appendChild(rootElement);

			if (stats != null) {
				doc.createElement("lowercase");
				rootElement.appendChild(doc.createElement(String.valueOf(stats.lowercase)));
				doc.createElement("uppercase");
				rootElement.appendChild(doc.createElement(String.valueOf(stats.uppercase)));
				doc.createElement("digits");
				rootElement.appendChild(doc.createElement(String.valueOf(stats.digits)));
				doc.createElement("special");
				rootElement.appendChild(doc.createElement(String.valueOf(stats.special)));
			}

			if (math != null) {
				doc.createElement("num1+num2");
				rootElement.appendChild(doc.createElement(String.valueOf(math.sum)));
				doc.createElement("num1-num2");
				rootElement.appendChild(doc.createElement(String.valueOf(math.sub)));
				doc.createElement("num1*num2");
				rootElement.appendChild(doc.createElement(String.valueOf(math.mul)));
				doc.createElement("num1/num2");
				rootElement.appendChild(doc.createElement(String.valueOf(math.div)));
				doc.createElement("num1%num2");
				rootElement.appendChild(doc.createElement(String.valueOf(math.mod)));
			}
			return doc;
		}


		public Statistics calculateString(String text) {
			int lowercase = 0;
			int uppercase = 0;
			int digits = 0;
			int special = 0;

			char[] arr = text.toCharArray();
			for (char c : arr) {
				if (Character.isLowerCase(c)) {
					lowercase++;
				} else if (Character.isUpperCase(c)) {
					uppercase++;
				} else if (Character.isDigit(c)) {
					digits++;
				} else {
					special++;
				}
			}
			return new Statistics(lowercase, uppercase, digits, special);
		}

		class Statistics {
			private final int lowercase;
			private final int uppercase;
			private final int digits;
			private final int special;

			public Statistics(int lowercase, int uppercase, int digits, int special) {
				this.lowercase = lowercase;
				this.uppercase = uppercase;
				this.digits = digits;
				this.special = special;
			}

		}

		class MathResult {
			private int sum;
			private int sub;
			private int mul;
			private int div;
			private int mod;

			int num1;
			int num2;

			public MathResult(int num1, int num2) {
				this.num1 = num1;
				this.num2 = num2;
				calculate();
			}

			private void calculate() {
				sum = num1 + num2;
				sub = num1 - num2;
				mul = num1 * num2;
				div = num1 / num2;
				mod = num1 % num2;
			}
		}

		class Data {
			int num1;
			int num2;
			String str;

			boolean isMath = false;
			boolean isString = false;

			public Data(int num1, int num2, String str) {
				this.num1 = num1;
				this.num2 = num2;
				this.str = str;
				isMath = true;
				isString = true;
			}

			public Data(String str) {
				this.str = str;
				isString = true;
			}

			public Data(int num1, int num2) {
				this.num1 = num1;
				this.num2 = num2;
				isMath = true;
			}

			@Override
			public String toString() {
				return "Data{" +
						"num1=" + num1 +
						", num2=" + num2 +
						", str='" + str + '\'' +
						'}';
			}
		}
	}
}

