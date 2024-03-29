
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(GameWebSocketHandler.class);
            }
        };
        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/server");
        ctx.addServlet(GameServiceSocketServlet.class, "/socket");

        ContextHandler staticContextHandler = new ContextHandler("/main");
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("/static");
        ctx.setHandler(resourceHandler);
 
        server.setHandler(ctx);
        server.setHandler(staticContextHandler);
 
        server.start();
        server.join();
    }
    
    public static class GameServiceSocketServlet extends WebSocketServlet {
    	 
        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.register(GameWebSocketHandler.class);
        }
    }
}