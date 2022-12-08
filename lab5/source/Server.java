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
				if (!params.isEmpty()) {
					String str1 = params.get("num1");
					String str2 = params.get("num2");
					response = handleRequest(str1, str2);
				}
			}
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
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

	public static String handleRequest(String str1, String str2) {
		try {
			int num1, num2;
			num1 = Integer.parseInt(str1);
			num2 = Integer.parseInt(str2);
			if(num2==0){
				return "Cannot divide by 0";
			}
			MathResult m = new MathResult(num1, num2);
			return m.toString();
		} catch (NumberFormatException e) {
			return "Not a number";
		}
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

	@Override
	public String toString() {
		return "{" +
				"\"sum : \"" + sum +
				"\", sub : \"" + sub +
				"\", mul : \"" + mul +
				"\", div : \"" + div +
				"\", mod : \"" + mod +
				'}';
	}
}
