import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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

			InputStream stream = new ByteArrayInputStream(in.toString().getBytes(StandardCharsets.UTF_8));
			try {
				handleRequest(stream);
			} catch (XPathExpressionException | ParserConfigurationException | SAXException e) {
				throw new RuntimeException(e);
			}

			stream.close();
//			t.sendResponseHeaders(200, response.toString().length());
//			OutputStream os = t.getResponseBody();
//			os.write(response.toString().getBytes());
//			os.close();

		}
	}

	public static void handleRequest(InputStream stream) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		Document doc = builder.parse(stream); //input stream of response.

		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();

		XPathExpression expr = xpath.compile("//str"); // Look for status tag value.
		String str =  expr.evaluate(doc);

		XPathExpression expr1 = xpath.compile("//num1"); // Look for status tag value.
		int num1 = Integer.parseInt(expr1.evaluate(doc));

		XPathExpression expr2 = xpath.compile("//num2"); // Look for status tag value.
		int num2 = Integer.parseInt(expr2.evaluate(doc));

		System.out.println(str + num1 + num2);
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
}

