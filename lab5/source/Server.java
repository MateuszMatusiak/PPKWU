import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

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
			JSONObject response = new JSONObject();
			if (t.getRequestURI().getQuery() != null && !t.getRequestURI().getQuery().isEmpty()) {
				JSONObject params = queryToMap(t.getRequestURI().getQuery());
				if (!params.isEmpty()) {
					String str1 = params.getString("num1");
					String str2 = params.getString("num2");
					response = handleRequest(str1, str2);
				}
			}
			System.out.println(response.toString());
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}
	}

	public static JSONObject queryToMap(String query) {
		if (query == null) {
			return null;
		}


		Map<String, String> map = new HashMap<>();
		for (String param : query.split("&")) {
			String[] entry = param.split("=");
			if (entry.length > 1) {
				map.put(entry[0], entry[1]);
			} else {
				map.put(entry[0], "");
			}
		}
		return new JSONObject(map);
	}

	public static JSONObject handleRequest(String str1, String str2) {
		try {
			int num1, num2;
			num1 = Integer.parseInt(str1);
			num2 = Integer.parseInt(str2);
			if (num2 == 0) {
				return null;
			}
			MathResult m = new MathResult(num1, num2);
			return m.toJSON();
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static JSONObject calculateString(String text) {
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
		return new Statistics(lowercase, uppercase, digits, special).toJSON();
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

	public JSONObject toJSON() {
		JSONObject res = new JSONObject();
		res.put("lowercase", lowercase);
		res.put("uppercase", uppercase);
		res.put("digits", digits);
		res.put("special", special);
		return res;
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

	public JSONObject toJSON(){
		JSONObject res = new JSONObject();
		res.put("sum", sum);
		res.put("sub", sub);
		res.put("mul", mul);
		res.put("div", div);
		res.put("mod", mod);
		return res;
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
