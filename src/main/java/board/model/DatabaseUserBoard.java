package board.model;

import java.util.List;
import java.util.Optional;

import kong.unirest.Body;
import kong.unirest.GenericType;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;


public class DatabaseUserBoard {
	
	private String DB_URL         = "https://proyectofinal-5932.restdb.io";
	private String COLLECTION_URL = DB_URL + "/rest/usuario";
	private String COLLECTION_ANUNCIOS_URL = DB_URL + "/rest/anuncio";
	private String COLLECTION_DIRECCIONES_URL = DB_URL + "/rest/direccion";
	private String DOCUMENT_URL   = DB_URL + "/rest/usuario/{id}";
	private String ANUNCIOS_URL   = DB_URL + "/rest/usuario/{id}/anuncios";
	private String APIKEY         = "e5b59f794b8468996d4cba5dd808f45f7f205";
	private HttpResponse response;
	
	// Recupera todos los mensajes con GET y los devuelve en una lista
	public List<User> getUsers() {
		List<User> users = Unirest.get(COLLECTION_URL)
				.header("x-apikey", APIKEY)  // Env�a la clave de autentificaci�n
				.header("cache-control", "no-cache") // Evita que los proxys cache devuelvan una copia no actualizada de los mensajes
				.asObject(new GenericType<List<User>>(){}) // Transforma la respuesta JSON a una lista de mensajes
				.getBody(); // Devuelve la lista de mensajes
		return users;
	}

	// Crea un nuevo mensaje con POST
	// El servidor devuelve el mensaje creado incluyendo el identificador
	// La funci�n devuelve el mensaje creado o null si ha habido alg�n error
	public User newUser(String username, String email, String country) {
		
		User user = new User(username, email, country);
		
		if(!userExists(username)) {		
			
			User response = Unirest.post(COLLECTION_URL)
					.header("x-apikey", APIKEY)
					.header("cache-control", "no-cache")
					.header("content-type", "application/json")
					.body(user) // El mensaje se transforma a JSON y se env�a en el cuerpo de la petici�n
					.asObject(User.class) // Lee el JSON de la respuesta y lo transforma a una instancia de la clase User
					.getBody(); // Devuelve el mensaje de la respuesta o null si no lo ha podido transformar (p.ej. porque ha habido alg�n error)
	
			return response;
		}else {
			return user;
		}
	}
	
	public User newUser(String username, String email, String country, Direccion direccion) {
		
		Direccion dir = Unirest.post(COLLECTION_DIRECCIONES_URL)
				.header("x-apikey", APIKEY)
				.header("cache-control", "no-cache")
				.header("content-type", "application/json")
				.body(direccion) // El mensaje se transforma a JSON y se env�a en el cuerpo de la petici�n
				.asObject(Direccion.class) // Lee el JSON de la respuesta y lo transforma a una instancia de la clase User
				.getBody(); // Devuelve el mensaje de la respuesta o null si no lo ha podido transformar (p.ej. porque ha habido alg�n error)
		User user = new User(username, email, country, new Direccion[] {dir});
		if(!userExists(username)) {		
			System.out.println("oooo");
			User response = Unirest.post(COLLECTION_URL)
					.header("x-apikey", APIKEY)
					.header("cache-control", "no-cache")
					.header("content-type", "application/json")
					.body(user) // El mensaje se transforma a JSON y se env�a en el cuerpo de la petici�n
					.asObject(User.class) // Lee el JSON de la respuesta y lo transforma a una instancia de la clase User
					.getBody(); // Devuelve el mensaje de la respuesta o null si no lo ha podido transformar (p.ej. porque ha habido alg�n error)
			return response;
		}else {
			return user;
		}
	}
	

	public User getUser(String id) {
		User user = Unirest.get(DOCUMENT_URL)
				.routeParam("id", id)
				.header("x-apikey", APIKEY)  // Env�a la clave de autentificaci�n
				.header("cache-control", "no-cache") // Evita que los proxys cache devuelvan una copia no actualizada de los mensajes
				.asObject(new GenericType<User>(){}) // Transforma la respuesta JSON a una lista de mensajes
				.getBody(); // Devuelve la lista de mensajes
		return user;
	
	}
	

	public User updateUser(String id, String username, String email, String country) {
		User user = new User(username, email, country);
		if(!userExists(username)) {
			User response = Unirest.put(DOCUMENT_URL)
					.routeParam("id", id)
					.header("x-apikey", APIKEY)
					.header("cache-control", "no-cache")
					.header("content-type", "application/json") // Env�a la clave de autentificaci�n
					.body(user)
					.asObject(User.class).getBody();
			return response;
		}else {
			return user;
		}
	}
	
	public User patchUser(String id, String country) {
		User response = Unirest.patch(DOCUMENT_URL)
				.routeParam("id", id)
				.header("x-apikey", APIKEY) // Env�a la clave de autentificaci�n
				.field("country", country)
				.asObject(User.class).getBody();
		return response;

	}

	public boolean deleteUser(String id) {
		// Esta funci�n debe eliminar el mensaje con el identificador
		//   que recibe como par�metro.
		// Debe devolver verdadero si el mensaje se ha eliminado
		//   o falso si ha habido alg�n error.
		// Ver la documentaci�n de unirest para obtener el c�digo de estado
		//   de una petici�n.
		
		Unirest.delete(DOCUMENT_URL)
				.routeParam("id", id)
				.header("x-apikey", APIKEY)
				.asEmpty();
		
		deleteAnunciosByUserID(id);
				
		return false;
	}
	
	public HttpResponse deleteAll() {
		// Esta funci�n debe borrar todos los mensajes en la base de datos
		//   con una sola petici�n al API
		// No devuelve nada.
		
		// Unirest.delete(COLLECTION_URL+"/*?q={}").header("x-apikey", APIKEY).getBody();
		HttpResponse response = Unirest.delete(COLLECTION_URL+"/*?q={p}")
				.routeParam("p", "{}")
				.header("x-apikey", APIKEY)
				.asEmpty();
		deleteAllAnuncios();
		return response;
	}

	public Anuncio newAnuncio(String title, String text, String userid) {
		
		Anuncio anuncio = new Anuncio(userid, title, text);
		
		Anuncio response = Unirest.post(ANUNCIOS_URL)
				.routeParam("id",userid)
				.header("x-apikey", APIKEY)
				.header("cache-control", "no-cache")
				.header("content-type", "application/json")
				.body(anuncio) // El mensaje se transforma a JSON y se env�a en el cuerpo de la petici�n
				.asObject(Anuncio.class) // Lee el JSON de la respuesta y lo transforma a una instancia de la clase User
				.getBody(); // Devuelve el mensaje de la respuesta o null si no lo ha podido transformar (p.ej. porque ha habido alg�n error)

		return response;
	}

	public void deleteAnunciosByUserID(String userid) {
		// Esta funci�n debe borrar todos los mensajes en la base de datos
		//   con una sola petici�n al API
		// No devuelve nada.
		
		// Unirest.delete(COLLECTION_URL+"/*?q={}").header("x-apikey", APIKEY).getBody();
		Unirest.delete(COLLECTION_ANUNCIOS_URL+"/*?q={p}")
				.routeParam("p", "{\"userid\":\""+userid+"\"}")
				.header("x-apikey", APIKEY)
				.asEmpty();
	}
	
	public void deleteAllAnuncios() {
		// Esta funci�n debe borrar todos los mensajes en la base de datos
		//   con una sola petici�n al API
		// No devuelve nada.
		
		// Unirest.delete(COLLECTION_URL+"/*?q={}").header("x-apikey", APIKEY).getBody();
		Unirest.delete(COLLECTION_ANUNCIOS_URL+"/*?q={p}")
				.routeParam("p", "{}")
				.header("x-apikey", APIKEY)
				.asEmpty();
	}
	
	public boolean userExists(String username) {
		boolean exists = false;
	  
	    for(User user : getUsers()) {
	    	if(user.getName() != null)
		    	if(user.getName().equals(username)) {
		    		exists = true;
		    		break;
		    	}
	    }
	  
	    return exists;
	}

}