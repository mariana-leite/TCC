package com.java.square;

public class Square {
	
	/*
	 * 
    {
  "id": "Square01",
  "type": "Square",
  "name": {
    "value": "Praça Oliveira Belo",
    "type": "Text"
  },
  "location": {
    "value": "-10.936273, -37.061228",
    "type": "geo:point"
  },
  "radius": {
    "value": 45,
    "type": "Float"
  }
}
	 */
	
	private String id;
	private String type;
	private Attrs name;
	private Attrs location;
	private Attrs radius;
	
	
	public Square(String id, String type, Attrs name, Attrs location, Attrs radius) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.location = location;
		this.radius = radius;
	}


	public static class Attrs {
		private String value;
		private String type;
		
		
		public Attrs(String value, String type) {
			this.value = value;
			this.type = type;
		}
		
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		
		
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Attrs getName() {
		return name;
	}


	public void setName(Attrs name) {
		this.name = name;
	}


	public Attrs getLocation() {
		return location;
	}


	public void setLocation(Attrs location) {
		this.location = location;
	}


	public Attrs getRadius() {
		return radius;
	}


	public void setRadius(Attrs radius) {
		this.radius = radius;
	}
	

}
