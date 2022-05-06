package infoviewer.infoviewer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class InfoViewer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        int port = 8080;
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/", new MyHandler());
        System.out.println("MyServer wakes up: port=" + port);
        server.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // HTTP リクエストを処理するために呼び出されるハンドラ
    private static class MyHandler implements HttpHandler {

        // HTTP リクエストを処理する
        public void handle(HttpExchange t) throws IOException {

            System.out.println("**************************************************");

            // 開始行を取得
            String startLine =
                    t.getRequestMethod() + " " +
                            t.getRequestURI().toString() + " " +
                            t.getProtocol();
            System.out.println(startLine);

            // Content-Length 以外のレスポンスヘッダを設定
            Headers resHeaders = t.getResponseHeaders();
            resHeaders.set("Content-Type", "application/json");
            resHeaders.set("Last-Modified",
                    ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
            resHeaders.set("Server",
                    "MyServer (" +
                            System.getProperty("java.vm.name") + " " +
                            System.getProperty("java.vm.vendor") + " " +
                            System.getProperty("java.vm.version") + ")");

            // レスポンスヘッダを送信
            int statusCode = 200;
            String resBody = "{\\\"message\\\": \\\"Hello, World!\\\"}";
            long contentLength = resBody.getBytes(StandardCharsets.UTF_8).length;
            t.sendResponseHeaders(statusCode, contentLength);

            // レスポンスボディを送信
            OutputStream os = t.getResponseBody();
            os.write(resBody.getBytes());
            os.close();
        }
    }
}
