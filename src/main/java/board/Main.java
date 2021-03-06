package board;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

	public static void main(String[] args) {
		ResourceConfig config = new ResourceConfig();
		config.packages("board.services");
		ServletHolder servlet = new ServletHolder(new ServletContainer(config));

		String port = System.getenv("PORT");
		Server server = new Server(Integer.valueOf(port));
		ServletContextHandler context = new ServletContextHandler(server, "/*");
		context.addServlet(servlet, "/board/*");

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.destroy();
		}
	}

}
