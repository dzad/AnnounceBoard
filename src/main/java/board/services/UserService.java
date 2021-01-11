package board.services;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.Collection;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import board.model.User;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import board.model.Anuncio;
import board.model.DatabaseUserBoard;
import board.model.Direccion;
import board.model.PATCH;

/*
 * Todos los servicios ofrecidos por esta clase estÃ¡n
 * disponibles en la URI http://[servidor]/board/user
 *   - [servidor]: direcciÃ³n del servidor web
 *   - /board: ruta raÃ­z de todos los servicios de esta aplicaciÃ³n
 *             segÃºn la configuraciÃ³n del fichero web.xml
 *   - /user: direcciÃ³n de esta clase establecida con @Path
 *   
 *   El cuerpo de las respuestas estarÃ¡ codificado con JSON.
 *   Al devolver un objeto Java, la transformaciÃ³n a JSON se hace
 *   de forma automÃ¡tica si la clase del objeto tiene
 *   la anotaciÃ³n @XmlRootElement (ver aosgc.board.model.User)
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
	
	private String ACCESS_KEY = "ec6e41d0f6f56a5337760174f0cacfd4";
								 
	/*
	 *  Creamos una instancia del gestor de users.
	 *  La variable board debe ser estÃ¡tica para que no se
	 *  destruya despuÃ©s de cada llamada a un servicio.
	 */
	private static DatabaseUserBoard board = new DatabaseUserBoard();
	/*
	 * La anotaciÃ³n @Context hace que se almacene la informaciÃ³n
	 * sobre la peticiÃ³n y la URI del servidor en la variable.
	 * Como algunos servicios devuelven la URI para acceder a los
	 * users, esto permite obtener la direcciÃ³n del servidor
	 * para no tener que codificar la URI manualmente, lo que darÃ­a
	 * problemas si el servidor cambia de direcciÃ³n. 
	 */
	@Context
	UriInfo uriInfo;

	/*
	 * Los mÃ©todos que no llevan la anotaciÃ³n @Path estÃ¡n accesibles
	 * en la URI base de la clase http://[servidor]/board/user 
	 */
	
	/*
	 * MÃ©todo GET sobre /board/user
	 * Devuelve la lista completa de users
	 */
	@GET
	public Response get() {
		/*
		 *  Obtenemos la lista completa de users
		 */
		Collection<User> users = board.getUsers();
		
		/*
		 * Se construye la respuesta con el cÃ³digo 200 OK
		 *  y devolviendo la lista de users. La lista se transforma
		 *  automÃ¡ticamente a un array de objetos JSON
		 */
		return Response.ok(users).build();
	}

	/*
	 * El mÃ©todo PUT sobre la direcciÃ³n base no debe hacer nada
	 */
	@PUT
	public Response put() {
		
		return Response.status(Status.NOT_FOUND).build();
	}

	/*
	 * El mÃ©todo POST sobre la direcciÃ³n base crea un nuevo user
	 * Los datos del formulario se obtienen como parÃ¡metros de la
	 * funciÃ³n usando la anotaciÃ³n @FormParam
	 */
	
	@POST
	public Response post(@Context HttpServletRequest req,
			@FormParam("username") String username,
			@FormParam("email") String email, @FormParam("country") String country, 
			@FormParam("street") String street, @FormParam("province") String province, @FormParam("zipcode") String zipcode) {
		User user;
		String ip = req.getHeader("X-Forwarded-For");
		  if (ip != null) {
			  country = detectCountry(ip);
		  }
		if(street!=null && province != null && zipcode!=null) {
			System.out.println("coco");
			Direccion direccion = new Direccion(street, province, zipcode);
			user = board.newUser(username, email, country, direccion);
		}else {
			// Pasamos los datos al gestor para crear un nuevo user
			user = board.newUser(username, email, country);
		}
		if(user != null) {
			if(user.getId() != null) {
				/*
				 * Construimos la URI del nuevo user utilizando la
				 *  direcciÃ³n del servidor que nos da uriInfo
				 */		
				UriBuilder ub = uriInfo.getAbsolutePathBuilder();
				String user_id = user.getId();
				URI location = ub.path(user_id).build();
				
				/*
				 * Construimos la respuesta con el cÃ³digo 201 Created
				 *  y la cabecera HTTP 'Location' con la direcciÃ³n
				 *  del user creado
				 */
				return Response.created(location).build();
			}else {
				return Response.status(Status.CONFLICT).build();
			}
		}else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@DELETE
	public void deleteAll() {
		board.deleteAll();
	}

	@GET
	@Path("{id}")
	public Response getWithId(@PathParam("id") String id) {
		
		/*
		 *  Buscamos el user con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn user
		 *   devuelve el valor null
		 */
		User user = board.getUser(id);
		
		if (user != null) {
			/*
			 * Si el user existe lo devuelve con el cÃ³digo 200 OK
			 * La transformaciÃ³n a JSON es automÃ¡tica
			 */
			return Response.ok(user).build();
		} else {
			// Si el user no existe devuelve el cÃ³digo 404 Not Found
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	
	@PUT
	@Path("{id}")
	public Response postWithId(@PathParam("id") String id, @FormParam("user") String username,
			@FormParam("title") String title, @FormParam("text") String text) {
		
		/*
		 *  Buscamos el user con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn user
		 *   devuelve el valor null
		 */
		User user = board.updateUser(id, username,title,text);
		
		if (user != null) {
			/*
			 * Si el user existe lo devuelve con el cÃ³digo 200 OK
			 * La transformaciÃ³n a JSON es automÃ¡tica
			 */
			if(user.getId() != null) {
				return Response.ok(user).build();
			}else {
				return Response.status(Status.CONFLICT).build();
			}
		} else  {
			// Si el user no existe devuelve el cÃ³digo 404 Not Found
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@PATCH
	@Path("{id}")
	public Response patchWithId(@PathParam("id") String id, @FormParam("country") String country) {
		
		/*
		 *  Buscamos el user con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn user
		 *   devuelve el valor null
		 */
		User user = board.patchUser(id,country);
		
		if (user != null) {
			/*
			 * Si el user existe lo devuelve con el cÃ³digo 200 OK
			 * La transformaciÃ³n a JSON es automÃ¡tica
			 */
			return Response.ok(user).build();
		} else {
			// Si el user no existe devuelve el cÃ³digo 404 Not Found
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Path("{id}/anuncios")
	public Response postAnuncio(@PathParam("id") String userid, @FormParam("title") String title, @FormParam("text") String text) {
		
		// Pasamos los datos al gestor para crear un nuevo user
		Anuncio anuncio = board.newAnuncio(title, text, userid);
		/*
		 * Construimos la URI del nuevo user utilizando la
		 *  direcciÃ³n del servidor que nos da uriInfo
		 */		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder();
		URI location = ub.path(anuncio.getId()).build();
		
		/*
		 * Construimos la respuesta con el cÃ³digo 201 Created
		 *  y la cabecera HTTP 'Location' con la direcciÃ³n
		 *  del user creado
		 */
		return Response.created(location).build();
	}
	
	/*
	 *  TODO: Escribir aquÃ­ la implementaciÃ³n del mÃ©todo POST
	 *        recibiendo el identificador de un user 
	 */
	@POST
	@Path("{id}")
	public Response postId() {
		return Response.status(Status.NOT_FOUND).build();
	}

	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") String id) {
		
		/*
		 *  Buscamos el user con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn user
		 *   devuelve el valor null
		 */
		boolean user = board.deleteUser(id);
		return null;
		
	}
	
	public String detectCountry(String ip) {
	    
		JsonNode response = Unirest.get("http://api.ipstack.com/{ip}?access_key={key}&fields=country_name")
		.routeParam("ip",ip)
		.routeParam("key",ACCESS_KEY)
		.asJson()
		.getBody();
		
		System.out.println(Unirest.get("http://api.ipstack.com/{ip}?access_key={key}")
		.routeParam("ip",ip)
		.routeParam("key",ACCESS_KEY).getUrl());
		
		String country = response.getObject().getString("country_name");
		System.out.println(country);
		return country;
	}
	
}
