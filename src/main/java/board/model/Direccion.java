package board.model;

public class Direccion {
	private String street, province, zipcode,_id;

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getZipCode() {
		return zipcode;
	}

	public void setZipCode(String zipCode) {
		this.zipcode = zipCode;
	}

	public Direccion(String street, String province, String zipCode) {
		super();
		this.street = street;
		this.province = province;
		this.zipcode = zipCode;
	}
	
	public Direccion(String street, String province, String zipCode, String _id) {
		super();
		this.street = street;
		this.province = province;
		this.zipcode = zipCode;
		this._id = _id;
	}
	
}
