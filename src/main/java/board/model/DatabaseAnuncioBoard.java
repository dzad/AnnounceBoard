package board.model;

import java.util.List;
import java.util.Optional;

import kong.unirest.Body;
import kong.unirest.GenericType;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;


public class DatabaseAnuncioBoard {
	
	private String DB_URL         = "https://proyectofinal-5932.restdb.io";
	private String COLLECTION_URL = DB_URL + "/rest/anuncio";
	private String DOCUMENT_URL   = DB_URL + "/rest/anuncio/{id}";
	private String APIKEY         = "e5b59f794b8468996d4cba5dd808f45f7f205";
	private HttpResponse responce;
	
	// Recupera todos los mensajes con GET y los devuelve en una lista
	public List<Anuncio> getAnuncios() {
		List<Anuncio> anuncios = Unirest.get(COLLECTION_URL)
				.header("x-apikey", APIKEY)  // Env�a la clave de autentificaci�n
				.header("cache-control", "no-cache") // Evita que los proxys cache devuelvan una copia no actualizada de los mensajes
				.asObject(new GenericType<List<Anuncio>>(){}) // Transforma la respuesta JSON a una lista de mensajes
				.getBody(); // Devuelve la lista de mensajes
		return anuncios;
	}

	// Crea un nuevo mensaje con POST
	// El servidor devuelve el mensaje creado incluyendo el identificador
	// La funci�n devuelve el mensaje creado o null si ha habido alg�n error
	public Anuncio newAnuncio(String user, String title, String text) {
		
		Anuncio anuncio = new Anuncio(user, title, text);
		
		Anuncio response = Unirest.post(COLLECTION_URL)
				.header("x-apikey", APIKEY)
				.header("cache-control", "no-cache")
				.header("content-type", "application/json")
				.body(anuncio) // El mensaje se transforma a JSON y se env�a en el cuerpo de la petici�n
				.asObject(Anuncio.class) // Lee el JSON de la respuesta y lo transforma a una instancia de la clase Anuncio
				.getBody(); // Devuelve el mensaje de la respuesta o null si no lo ha podido transformar (p.ej. porque ha habido alg�n error)

		return response;
	}
	

	public Anuncio getAnuncio(String id) {
		Anuncio anuncio = Unirest.get(DOCUMENT_URL)
				.routeParam("id", id)
				.header("x-apikey", APIKEY)  // Env�a la clave de autentificaci�n
				.header("cache-control", "no-cache") // Evita que los proxys cache devuelvan una copia no actualizada de los mensajes
				.asObject(new GenericType<Anuncio>(){}) // Transforma la respuesta JSON a una lista de mensajes
				.getBody(); // Devuelve la lista de mensajes
		return anuncio;
	
	}
	

	public Anuncio updateAnuncio(String id, String user, String title, String text) {
		Anuncio anuncio = new Anuncio(user, title, text);
		Anuncio responce = Unirest.put(DOCUMENT_URL)
				.routeParam("id", id)
				.header("x-apikey", APIKEY)
				.header("cache-control", "no-cache")
				.header("content-type", "application/json") // Env�a la clave de autentificaci�n
				.body(anuncio)
				.asObject(Anuncio.class).getBody();
		return responce;

	}
	public Anuncio patchAnuncio(String id, String user, String title, String text) {
		Anuncio anuncio = new Anuncio(user, title, text);
		Anuncio responce = Unirest.patch(DOCUMENT_URL)
				.routeParam("id", id)
				.header("x-apikey", APIKEY) // Env�a la clave de autentificaci�n
				.field("text", text)
				.asObject(Anuncio.class).getBody();
		return responce;

	}

	public boolean deleteAnuncio(String id) {
		// Esta funci�n debe eliminar el mensaje con el identificador
		//   que recibe como par�metro.
		// Debe devolver verdadero si el mensaje se ha eliminado
		//   o falso si ha habido alg�n error.
		// Ver la documentaci�n de unirest para obtener el c�digo de estado
		//   de una petici�n.
		
		Anuncio responce = Unirest.delete(DOCUMENT_URL)
				.routeParam("id", id)
				.header("x-apikey", APIKEY)
				.asObject(Anuncio.class).getBody();
				
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
		return response;
	}



}