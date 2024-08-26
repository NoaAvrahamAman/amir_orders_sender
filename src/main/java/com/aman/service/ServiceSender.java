package com.aman.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.modelmapper.ModelMapper;

import com.aman.dto.DtoOrder;
import com.aman.dto.DtoOrderLine;
import com.aman.model.Order;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.aman.App;

public class ServiceSender implements IServiceSender {
    //private final ModelMapper modelMapper;
    private final Gson gson;
    private static final String CONFIG_FILE_NAME = "np.properties";
    private Properties properties;

    public ServiceSender() {
        loadProperties();
        //this.modelMapper = new ModelMapper();
        this.gson = new Gson();
    }

    @Override
    public String obtainOrders() {

       List<DtoOrder> dtoOrders = runAPIgetOrders();
       Order order;
        for (DtoOrder dtoOrder : dtoOrders) {
            if (!dtoOrder.getSTATDES().equals("מאושר כרמל") && !dtoOrder.getSTATDES().equals("מאושר")&&!dtoOrder.getSTATDES().equals("אושרה")) {
                continue;
            }
            List<Order> ApprovedOrder = new ArrayList<Order>();
            List<DtoOrderLine> dtoOrderLines = runAPIgetOrderLines(dtoOrder.getORDNAME());
           
            for (DtoOrderLine dtoOrderLine : dtoOrderLines) {
                order = new Order();
                order.setORDNAME(dtoOrder.getORDNAME());
                order.setCURDATE(dtoOrder.getCURDATE());
                order.setSUPNAME(dtoOrder.getSUPNAME());
                order.setBRANCHNAME(dtoOrder.getBRANCHNAME());
                order.setPARTNAME(dtoOrderLine.getPARTNAME());
                order.setTQUANT(dtoOrderLine.getTQUANT());
                order.setCOLINE(dtoOrderLine.getCOLINE());
                ApprovedOrder.add(order);
            }
            
            //runAPIupdStatus(dtoOrder.getORDNAME());
            Order.createXMLFile(dtoOrder.getORDNAME(), properties.getProperty("AmirOrdersINFolder"),
            ApprovedOrder);
        }

        return "";
    }

    private List<DtoOrder> runAPIgetOrders() {

        String url = properties.getProperty("URLGetAmirOrders"); // Replace with your URL
        String user = properties.getProperty("userAmirOrders"); // Replace with your username
        String password = properties.getProperty("passwordAmirOrders"); // Replace with your password

        try {
            // Create URL object
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set request method
            con.setRequestMethod("GET");

            // Add Authorization header
            String credentials = user + ":" + password;
            String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
            String basicAuth = "Basic " + encodedCredentials;
            con.setRequestProperty("Authorization", basicAuth);

            // Get response code
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            //return response.toString();

            String jsonData = response.toString();
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonArray ordersList = jsonObject.getAsJsonArray("value");
            Type orderListType = new TypeToken<List<DtoOrder>>() {
            }.getType();
            List<DtoOrder> dtoOrders = gson.fromJson(ordersList, orderListType);
            return dtoOrders;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void loadProperties() {
        properties = new Properties();
        try {
            String jarPath = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            jarPath = "file:" + jarPath;
            String configFilePath = Paths.get(new URI(jarPath)).getParent().resolve(CONFIG_FILE_NAME).toString();

            try (InputStream input = new FileInputStream(configFilePath)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Unable to load properties file: " + e.getMessage());
            }
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void runAPIupdStatus(String orderNum) {

        String url = properties.getProperty("URLupdStatusAmirOrders"); // Replace with your URL
        String user = properties.getProperty("userAmirOrders"); // Replace with your username
        String password = properties.getProperty("passwordAmirOrders"); // Replace with your password

        String jsonInput = "{\"STATDES\":\"נשלח לכרמל\"}";

        try {
            // Create URL object
            url += "('" + orderNum + "')";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set request method
            con.setRequestMethod("GET");

            // Add Authorization header
            String credentials = user + ":" + password;
            String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
            String basicAuth = "Basic " + encodedCredentials;
            con.setRequestProperty("Authorization", basicAuth);
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(jsonInput.getBytes(StandardCharsets.UTF_8));

            // Get response code
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("response code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<DtoOrderLine> runAPIgetOrderLines(String orderNum) {

        String url = properties.getProperty("URLGetAmirOrders"); // Replace with your URL
        url += "('" + orderNum + "')" + properties.getProperty("URLGetAmirOrderLines");
        String user = properties.getProperty("userAmirOrders"); // Replace with your username
        String password = properties.getProperty("passwordAmirOrders"); // Replace with your password

        try {
            // Create URL object
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set request method
            con.setRequestMethod("GET");

            // Add Authorization header
            String credentials = user + ":" + password;
            String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
            String basicAuth = "Basic " + encodedCredentials;
            con.setRequestProperty("Authorization", basicAuth);

            // Get response code
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            //return response.toString();

            String jsonDataLines = response.toString();
            JsonObject jsonObject2 = JsonParser.parseString(jsonDataLines).getAsJsonObject();
            JsonArray orderLinesList = jsonObject2.getAsJsonArray("value");
            Type orderLinesListType = new TypeToken<List<DtoOrderLine>>() {
            }.getType();
            List<DtoOrderLine> dtoOrderLines = gson.fromJson(orderLinesList, orderLinesListType);
            return dtoOrderLines;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
