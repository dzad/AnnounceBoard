package board.model;

import java.util.Collection;

/*
 * Gestor de mensajes
 * 
 * Ofrece las funciones necesarias para crear, modificar,
 * leer y borrar mensajes.
 * 
 * Esta clase mantiene una única instancia en memoria (Singleton)
 * para almacenar los mensajes creados por los servicios web.
 * Al almacenarse fuera de la clase AnuncioHandlingService evitamos
 * que los mensajes desaparezcan si se crea una instancia distinta
 * de AnuncioHandlingService en cada petición a un servicio.
 */
public interface IAnuncioBoard {
	public Anuncio newAnuncio(String user, String title, String text);

	public Anuncio getAnuncio(String id);

	public Anuncio updateAnuncio(String id, String user, String title, String text);

	public Anuncio deleteAnuncio(String id);

	public Collection<Anuncio> getAnuncios();
}
