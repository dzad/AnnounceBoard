package board.model;

public class Anuncio {
	private String _id; // El identificador en la base de datos se llama _id
	private String userid;
	private String title;
	private String text;
	
	public Anuncio() {
		this._id = "";
		this.userid = "";
		this.title = "";
		this.text = "";
	}
	
	public Anuncio(String title, String text) {
		super();
		this.title = title;
		this.text = text;
	}

	public Anuncio(String userid, String title, String text) {
		this.userid = userid;
		this.title = title;
		this.text = text;
	}
	

	
	public String getId() {
		return _id;
	}
	
	public void setId(String id) {
		this._id = id;
	}

	public String getUserId() {
		return userid;
	}

	public void setUserId(String user) {
		this.userid = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
