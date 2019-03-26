package com.java.servicegroup;

public class ServiceGroup {
	
	/*
	 * '{
 "services": [
   {
     "apikey":      "4jggokgpepnvsb2uv4s40d59ov",
     "cbroker":     "http://orion:1026",
     "entity_type": "Thing",
     "resource":    "/iot/d"
   }
 ]
}'
	 */
	private Services[] services;
	
	public ServiceGroup(Services[] services) {
		this.services = services;
	}
	
	
	public static class Services{
		private String apikey;
		private String cbroker;
		private String entity_type;
		private String resource;
		
		public Services(String apikey, String cbroker, String entity_type, String resource) {
			this.apikey = apikey;
			this.cbroker = cbroker;
			this.entity_type = entity_type;
			this.resource = resource;
		}
		
		public String getApikey() {
			return apikey;
		}
		public void setApikey(String apikey) {
			this.apikey = apikey;
		}
		public String getCbroker() {
			return cbroker;
		}
		public void setCbroker(String cbroker) {
			this.cbroker = cbroker;
		}
		public String getEntity_type() {
			return entity_type;
		}
		public void setEntity_type(String entity_type) {
			this.entity_type = entity_type;
		}
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
		
	}


	public Services[] getServices() {
		return services;
	}


	public void setServices(Services[] services) {
		this.services = services;
	}
	
	
	
	

}
