package com.java.application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.java.createdevice.CreateDevice;
import com.java.createdevice.CreateDevice.Attribute;
import com.java.createdevice.CreateDevice.Device;
import com.java.createdevice.CreateDevice.StaticAttribute;
import com.java.notificationresponse.NotificationResponse;
import com.java.notificationresponse.NotificationResponse.Data;
import com.java.responsefromget.ResponseFromGET;
import com.java.servicegroup.ServiceGroup;
import com.java.servicegroup.ServiceGroup.Services;
import com.java.square.Square;
import com.java.square.Square.Attrs;
import com.java.subscription.Subscription;
import com.java.subscription.Subscription.Condition;
import com.java.subscription.Subscription.EntitiesIdPattern;
import com.java.subscription.Subscription.Http;
import com.java.subscription.Subscription.Notification;
import com.java.update.AttributeUpdate;
import com.java.update.EntitieUpdate;
import com.java.update.UpdateLuminosity;



public class Application {

	private ServerSocket server;
    
    public Application() throws Exception {
        this.server = new ServerSocket(0, 1, InetAddress.getByName("172.18.1.1"));
    }
    
    private String listen() throws Exception {
        String data = null;
        String data2 = null;
        Socket client = this.server.accept();
        
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));        
        while ( (data = in.readLine()) != null ) {
        	data2 = data;
        }
        return data2;
    }
    
    public static void postRequest(int port, String msg, URL url, String content_type, String requestMethod) throws IOException {
    	
    	HttpURLConnection httpURLConnection = null;
    	DataOutputStream dataOutputStream = null;
    	byte[] postData = msg.getBytes(StandardCharsets.UTF_8);
    	try {
    	    httpURLConnection = (HttpURLConnection) url.openConnection();
    	    httpURLConnection.setRequestProperty("Content-Type", content_type);
    	    httpURLConnection.setRequestProperty("fiware-service", "openiot");
    	    httpURLConnection.setRequestProperty("fiware-servicepath", "/");
    	    httpURLConnection.setRequestMethod(requestMethod);
    	    httpURLConnection.setDoInput(true);
    	    httpURLConnection.setDoOutput(true);
    	    dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
    	    dataOutputStream.write(postData);
    	    StringBuilder content;
    	    try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
    	} catch (IOException exception) {
    	    exception.printStackTrace();
    	}  finally {
    	    if (dataOutputStream != null) {
    	        try {
    	            dataOutputStream.flush();
    	            dataOutputStream.close();
    	        } catch (IOException exception) {
    	            exception.printStackTrace();
    	        }
    	    }
    	    if (httpURLConnection != null) {
    	    	httpURLConnection.disconnect();
    	    }
    	}
    	
    }
    
    
    private static String getByLocation(String coords) throws IOException {
		URL url = new URL("http://localhost:1026/v2/entities?type=Lamp&georel=near;maxDistance:9&geometry=point&coords="+coords+"&options=keyValues");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Java client");
		con.setRequestProperty("fiware-service", "openiot");
        con.setRequestProperty("fiware-servicepath", "/");
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} else {
			System.out.println("GET request not worked");
			return null;
		}

	}
    
    private static String getAll() throws IOException {
		URL url = new URL("http://localhost:1026/v2/entities?type=Lamp&options=keyValues");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Java client");
		con.setRequestProperty("fiware-service", "openiot");
        con.setRequestProperty("fiware-servicepath", "/");
		int responseCode = con.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} else {
			System.out.println("GET request not worked");
			return null;
		}

	}

   
    
    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }
    
    public int getPort() {
        return this.server.getLocalPort();
    }
    
    public static List<ResponseFromGET> stringToResponseFromGET(String response){
    	List<ResponseFromGET> getAllLamps_list = new ArrayList<>();
    	while(response.indexOf("{")>0) {
			int begin = response.indexOf("{");
			int end = response.indexOf("}");
			Gson responseFromGet_gson = new Gson();
			getAllLamps_list.add(responseFromGet_gson.fromJson(response.substring(begin, end+1), ResponseFromGET.class));		
			response = response.substring(end+1);
		}
    	return getAllLamps_list;
    }
    
    public static void printLamps() throws IOException {
    	String response = Application.getAll();
    	List<ResponseFromGET> getAllLamps_list = Application.stringToResponseFromGET(response);
    	
		int k = 0;
		System.out.println("------------------------");
		for(int i = 0; i<3; i++) {
			for(int j = 0; j<5; j++) {
				System.out.print(getAllLamps_list.get(k).getLuminosity() + "  ");
				k++;
			}
			System.out.println("");
		}
		System.out.println("------------------------");
    }
    
    public static void updateLuminosity(String id, String luminosity, int port) throws Exception {
    	AttributeUpdate attrLuminosity = new AttributeUpdate(luminosity);
    	EntitieUpdate[] entitieLuminosity = {new EntitieUpdate(id,attrLuminosity)};
    	UpdateLuminosity updateLuminosity = new UpdateLuminosity(entitieLuminosity);
    	Gson gson = new Gson();
        String json = gson.toJson(updateLuminosity);
        URL url = new URL("http://localhost:1026/v2/op/update");
        Application.postRequest(port,json,url,"application/json","POST");
    	
    }
    
    public static void updateLuminosityCount(String id, String luminosity, String count, int port) throws Exception {
    	AttributeUpdate attrLuminosity = new AttributeUpdate(luminosity);
    	AttributeUpdate attrCount = new AttributeUpdate(count);
    	EntitieUpdate[] entitieLuminosityCount = {new EntitieUpdate(id,attrLuminosity,attrCount)};
    	UpdateLuminosity updateLuminosityCount = new UpdateLuminosity(entitieLuminosityCount);
    	Gson gson = new Gson();
        String json = gson.toJson(updateLuminosityCount);
        URL url = new URL("http://localhost:1026/v2/op/update");
        Application.postRequest(port,json,url,"application/json","POST");
    	
    }
    
    public static void initializeLamps(Application app, int quantity) throws Exception {
    	for(int i = 0; i < quantity; i++)
        	app.listen();
    }
    
    public static void lampOff(Data notificationData, String[] previousState, List<ResponseFromGET> getLamps, int port) throws Exception {
    	if(!notificationData.getState().getValue().equals(previousState[Integer.parseInt(notificationData.getNumber().getValue())])) {
			updateLuminosity(notificationData.getId(),"0", port);
			
			String response_string = Application.getByLocation(notificationData.getLocation().getValue());
			getLamps = Application.stringToResponseFromGET(response_string);
			
			for(ResponseFromGET lamp : getLamps) {
				int count_int = lamp.getCount() + 1; 				
				if(lamp.getState().equals("off") && !lamp.getId().equals(notificationData.getId())) 
					updateLuminosityCount(lamp.getId(),"0",String.valueOf(count_int), port);
				//se tiver uma lampada X por perto com status on incrementa o contador da lampada X e aumenta a luminosidade da lampada X
				else if (lamp.getState().equals("on"))
					updateLuminosityCount(lamp.getId(),"3",String.valueOf(count_int), port);

			}
		}
    }
    
    public static void lampOn(Data notificationData, String[] previousState, List<ResponseFromGET> getLamps, int port) throws Exception {
    	if (!notificationData.getState().getValue().equals(previousState[Integer.parseInt(notificationData.getNumber().getValue())])) {
			
			String response_string = Application.getByLocation(notificationData.getLocation().getValue());
			getLamps = Application.stringToResponseFromGET(response_string);
			
			for(ResponseFromGET lamp : getLamps) {
				if(lamp.getTimeinstant()!=null && !lamp.getId().equals(notificationData.getId())) {
					int count_int = lamp.getCount() - 1;
					if(count_int==0 && lamp.getState().equals("on"))
						updateLuminosityCount(lamp.getId(),"2",String.valueOf(count_int), port);
					else
						updateLuminosityCount(lamp.getId(),lamp.getLuminosity(),String.valueOf(count_int), port);
				}
			}
			
			String luminosity;
			//Se uma lampada volta pra ON mas tem uma OFF no raio 
			if(!notificationData.getCount().getValue().equals("0"))
				luminosity = "3";
			//Se uma lampada volta pra ON e não tem uma OFF no raio 
			else 
				luminosity = "2";
			updateLuminosity(notificationData.getId(),luminosity, port);
		}
    }
    
    public static void main(String[] args) throws Exception {

    	Application app = new Application();
        
        //Service Group
        Services[] services = {new Services("4jggokgpepnvsb2uv4s40d59ov","http://orion:1026","Thing","/iot/d")};
        ServiceGroup servicegroup = new ServiceGroup(services);
        Gson gson = new Gson();
        String json = gson.toJson(servicegroup);
        URL url = new URL("http://localhost:4041/iot/services");
        Application.postRequest(app.getPort(),json,url,"application/json","POST");
  
        //Square
        Attrs name = new Attrs("Praça Oliveira Belo","Text");
        Attrs location = new Attrs("-10.936245,-37.061224","geo:point");
        Attrs radius = new Attrs("45","Float");
        Square square01 = new Square("urn:ngsi-ld:Square:1","Square",name,location,radius);
        gson = new Gson();
        json = gson.toJson(square01);
        url = new URL("http://localhost:1026/v2/entities");
        Application.postRequest(app.getPort(),json,url,"application/json","POST");
               
        //Create Devices
        Attribute[] attributes = {new Attribute("s","state","Text"), new Attribute("l","luminosity","Integer"),new Attribute("lo","location","geo:point"),
        		new Attribute("c", "count", "Integer"), new Attribute("n", "number", "Integer")};
        StaticAttribute[] static_attributes = {new StaticAttribute("refSquare", "Relationship", "urn:ngsi-ld:Square:1")};
        int quantity = 15;
        for(int i = 0; i<quantity;i++) {
        	Device[] device = {new Device("lamp"+(i+1),"urn:ngsi-ld:Lamp:"+(i+1),"Lamp",attributes,static_attributes)};
            CreateDevice createDevice = new CreateDevice(device);
            Gson gsonDevice = new Gson();
            String jsonDevice = gsonDevice.toJson(createDevice);
            URL urlLamp = new URL("http://localhost:4041/iot/devices");
            Application.postRequest(app.getPort(),jsonDevice,urlLamp,"application/json","POST");
        }
        
        
        //Subscription
        EntitiesIdPattern[] entitiesIdPattern = {new EntitiesIdPattern(".*", "Lamp")};
    	Subscription subscription = new Subscription("Mudança de estado das lampadas", entitiesIdPattern, new Condition(new String[] {"state"}),
				new Notification(new Http("http://172.18.1.1:"+ app.getPort()),
				new String[] {"state","location","number","count"}) , 0);   
    	gson = new Gson();
        json = gson.toJson(subscription);
        url = new URL("http://localhost:1026/v2/subscriptions");
        Application.postRequest(app.getPort(),json,url,"application/json","POST");
        app.listen();
        
        
        initializeLamps(app, quantity);
        
        
    	String[] previousState = new String[quantity+1];
    	String response_string = Application.getAll();
    	List<ResponseFromGET> getLamps =  Application.stringToResponseFromGET(response_string);	
    	for(ResponseFromGET aux : getLamps)
    		previousState[aux.getNumber()] = aux.getState();
    	
    	
    	NotificationResponse notification = new NotificationResponse();
    	
        while(true) {
        	String response = app.listen();
        	gson = new Gson();
        	notification = gson.fromJson(response, NotificationResponse.class);
        	for(Data notificationData : notification.getData()) {
        		System.out.println("***"+notificationData.getId() +" "+ notificationData.getState().getValue()+"***");
        		
        		if(notificationData.getState().getValue().equals("off"))
        			lampOff(notificationData,previousState,getLamps,app.getPort());	
        		
        		else if (notificationData.getState().getValue().equals("on")) 
        			lampOn(notificationData,previousState,getLamps,app.getPort());
        		
        		previousState[Integer.parseInt(notificationData.getNumber().getValue())] = notificationData.getState().getValue();
        	}       	
        	Application.printLamps();
        }      
    }   
}












