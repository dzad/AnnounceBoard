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

import board.model.Anuncio;
import board.model.DatabaseAnuncioBoard;
import board.model.PATCH;

/*
 * Todos los servicios ofrecidos por esta clase estÃ¡n
 * disponibles en la URI http://[servidor]/board/message
 *   - [servidor]: direcciÃ³n del servidor web
 *   - /board: ruta raÃ­z de todos los servicios de esta aplicaciÃ³n
 *             segÃºn la configuraciÃ³n del fichero web.xml
 *   - /message: direcciÃ³n de esta clase establecida con @Path
 *   
 *   El cuerpo de las respuestas estarÃ¡ codificado con JSON.
 *   Al devolver un objeto Java, la transformaciÃ³n a JSON se hace
 *   de forma automÃ¡tica si la clase del objeto tiene
 *   la anotaciÃ³n @XmlRootElement (ver aosgc.board.model.Anuncio)
 */
@Path("/anuncio")
@Produces(MediaType.APPLICATION_JSON)
public class AnuncioService {
	
	/*
	 *  Creamos una instancia del gestor de anuncios.
	 *  La variable board debe ser estÃ¡tica para que no se
	 *  destruya despuÃ©s de cada llamada a un servicio.
	 */
	private static DatabaseAnuncioBoard board = new DatabaseAnuncioBoard();
	/*
	 * La anotaciÃ³n @Context hace que se almacene la informaciÃ³n
	 * sobre la peticiÃ³n y la URI del servidor en la variable.
	 * Como algunos servicios devuelven la URI para acceder a los
	 * anuncios, esto permite obtener la direcciÃ³n del servidor
	 * para no tener que codificar la URI manualmente, lo que darÃ­a
	 * problemas si el servidor cambia de direcciÃ³n. 
	 */
	@Context
	UriInfo uriInfo;

	/*
	 * Los mÃ©todos que no llevan la anotaciÃ³n @Path estÃ¡n accesibles
	 * en la URI base de la clase http://[servidor]/board/message 
	 */
	
	/*
	 * MÃ©todo GET sobre /board/message
	 * Devuelve la lista completa de anuncios
	 */
	@GET
	public Response get() {
		/*
		 *  Obtenemos la lista completa de anuncios
		 */
		Collection<Anuncio> anuncios = board.getAnuncios();
		
		/*
		 * Se construye la respuesta con el cÃ³digo 200 OK
		 *  y devolviendo la lista de anuncios. La lista se transforma
		 *  automÃ¡ticamente a un array de objetos JSON
		 */
		return Response.ok(anuncios).build();
	}

	/*
	 * El mÃ©todo PUT sobre la direcciÃ³n base no debe hacer nada
	 */
	@PUT
	public Response put() {
		
		return Response.status(Status.NOT_FOUND).build();
	}

	/*
	 * El mÃ©todo POST sobre la direcciÃ³n base crea un nuevo anuncio
	 * Los datos del formulario se obtienen como parÃ¡metros de la
	 * funciÃ³n usando la anotaciÃ³n @FormParam
	 */
	
	@POST
	public Response post(@FormParam("user") String user,
			@FormParam("title") String title, @FormParam("text") String text) {
		
		// Pasamos los datos al gestor para crear un nuevo anuncio
		Anuncio message = board.newAnuncio(user, title, text);
		
		/*
		 * Construimos la URI del nuevo anuncio utilizando la
		 *  direcciÃ³n del servidor que nos da uriInfo
		 */		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder();
		URI location = ub.path(message.getId()).build();
		
		/*
		 * Construimos la respuesta con el cÃ³digo 201 Created
		 *  y la cabecera HTTP 'Location' con la direcciÃ³n
		 *  del anuncio creado
		 */
		return Response.created(location).build();
	}
	
	@DELETE
	public void deleteAll() {
		System.out.println("jhdhvkrsghskjgk");
		board.deleteAll();
	}

	@GET
	@Path("{id}")
	public Response getWithId(@PathParam("id") String id) {
		
		/*
		 *  Buscamos el anuncio con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn anuncio
		 *   devuelve el valor null
		 */
		Anuncio message = board.getAnuncio(id);
		
		if (message != null) {
			/*
			 * Si el anuncio existe lo devuelve con el cÃ³digo 200 OK
			 * La transformaciÃ³n a JSON es automÃ¡tica
			 */
			return Response.ok(message).build();
		} else {
			// Si el anuncio no existe devuelve el cÃ³digo 404 Not Found
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	
	@PUT
	@Path("{id}")
	public Response postWithId(@PathParam("id") String id, @FormParam("user") String user,
			@FormParam("title") String title, @FormParam("text") String text) {
		
		/*
		 *  Buscamos el anuncio con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn anuncio
		 *   devuelve el valor null
		 */
		Anuncio message = board.updateAnuncio(id, user,title,text);
		
		if (message != null) {
			/*
			 * Si el anuncio existe lo devuelve con el cÃ³digo 200 OK
			 * La transformaciÃ³n a JSON es automÃ¡tica
			 */
			return Response.ok(message).build();
		} else {
			// Si el anuncio no existe devuelve el cÃ³digo 404 Not Found
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@PATCH
	@Path("{id}")
	public Response patchWithId(@PathParam("id") String id, @FormParam("user") String user,
			@FormParam("title") String title, @FormParam("text") String text) {
		
		/*
		 *  Buscamos el anuncio con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn anuncio
		 *   devuelve el valor null
		 */
		Anuncio message = board.patchAnuncio(id, user,title,text);
		
		if (message != null) {
			/*
			 * Si el anuncio existe lo devuelve con el cÃ³digo 200 OK
			 * La transformaciÃ³n a JSON es automÃ¡tica
			 */
			return Response.ok(message).build();
		} else {
			// Si el anuncio no existe devuelve el cÃ³digo 404 Not Found
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	/*
	 *  TODO: Escribir aquÃ­ la implementaciÃ³n del mÃ©todo POST
	 *        recibiendo el identificador de un anuncio 
	 */

	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") String id) {
		
		/*
		 *  Buscamos el anuncio con el identificador recibido.
		 *  Si el identificador no corresponde a ningÃºn anuncio
		 *   devuelve el valor null
		 */
		boolean message = board.deleteAnuncio(id);
		return null;
		
	}
	

}
