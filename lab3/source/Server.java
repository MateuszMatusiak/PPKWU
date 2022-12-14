import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(4080), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			String response = "";
			if (t.getRequestURI().getQuery() != null && !t.getRequestURI().getQuery().isEmpty()) {
				Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
				if(!params.isEmpty()) {
					String text = params.get("str");
					response = calculateString(text);
				}
			}
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
			System.out.println("Served hello world...");
		}
	}

	public static Map<String, String> queryToMap(String query) {
		if (query == null) {
			return null;
		}
		Map<String, String> result = new HashMap<>();
		for (String param : query.split("&")) {
			String[] entry = param.split("=");
			if (entry.length > 1) {
				result.put(entry[0], entry[1]);
			} else {
				result.put(entry[0], "");
			}
		}
		return result;
	}

	public static String calculateString(String text) {
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
		return new Statistics(lowercase, uppercase, digits, special).toString();
	}

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

	@Override
	public String toString() {
		return "{" +
				"\"lowercase: \"" + lowercase +
				"\", uppercase: \"" + uppercase +
				"\", digits: \"" + digits +
				"\", special: \"" + special +
				'}';
	}
}
