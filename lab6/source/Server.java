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
}

