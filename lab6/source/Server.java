import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
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
			String input;
			while ((input = httpInput.readLine()) != null) {
				in.append(input).append(" ");
			}
			JSONObject response = handleRequest(new JSONObject(in.toString()));
			t.sendResponseHeaders(200, response.toString().length());
			OutputStream os = t.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}
	}

	public static JSONObject handleRequest(JSONObject input) {
		JSONObject result = new JSONObject();
		try {
		} catch (Exception e) {
			//ignore
		}
		
		return result;
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

		public JSONObject toJSON() {
			JSONObject res = new JSONObject();
			res.put("sum", sum);
			res.put("sub", sub);
			res.put("mul", mul);
			res.put("div", div);
			res.put("mod", mod);
			return res;
		}

	}
}

