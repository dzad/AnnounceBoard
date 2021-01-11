package board.model;

public class User {
	private String _id;
	private String username;
	private String country;
	private String email;	
	private Direccion[] direccion;
	
	public Direccion[] getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion[] direccion) {
		this.direccion = direccion;
	}

	public User(String username, String email, String country, Direccion[] direccion) {
		super();
		this.username = username;
		this.country = country;
		this.email = email;
		this.direccion = direccion;
	}
	
	public User(String username, String country, String email, String street, String province, String zipcode) {
		super();
		this.username = username;
		this.country = country;
		this.email = email;
		this.direccion = new Direccion[] {new Direccion(street, province, zipcode)};
	}

	User(String username, String email, String country){
		this.username = username;
		this.country = country;
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public String getId() {
		return _id;
	}
	public void setId(String id) {
		this._id = id;
	}
	public String getName() {
		return username;
	}
	public void setName(String name) {
		this.username = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	
	
}
