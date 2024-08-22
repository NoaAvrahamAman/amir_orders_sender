package com.aman.service;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.aman.model.Order;


public class ServiceSender implements IServiceSender {
    private final ModelMapper modelMapper;

    public ServiceSender() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public String obtainOrders() {
        String data = sendJSON();

        
        // List<Order> orders = data.stream().map(e -> modelMapper.map(e, Order.class))
        //         .collect(Collectors.toList());
        // data.stream().forEach(e -> System.out.println(e));
        // orders.stream().forEach((e) -> e.createXMLFile("name", "./"));
        return "";
    }

    private String sendJSON() {
        return "[\n" +
                "    {\n" +
                "        \"SUPNAME\": \"62008300000\",\n" +
                "        \"CDES\": \"(1995) שיווק כבלים מסילות\",\n" +
                "        \"CURDATE\": \"2024-07-17T00:00:00+03:00\",\n" +
                "        \"ORDNAME\": \"PO24NET000010\",\n" +
                "        \"STATDES\": \"סגורה\",\n" +
                "        \"OWNERLOGIN\": \"hila\",\n" +
                "        \"CODE\": \"ש\\\"ח\",\n" +
                "        \"BRANCHNAME\": \"11\",\n" +
                "        \"QPRICE\": 6130.00,\n" +
                "        \"TOTPRICE\": 7172.10,\n" +
                "        \"TOTQUANT\": 16.000\n" +
                "    },\n" +
                "    {\n" +
                "        \"SUPNAME\": \"62008300000\",\n" +
                "        \"CDES\": \"(1995) שיווק כבלים מסילות\",\n" +
                "        \"CURDATE\": \"2024-07-17T00:00:00+03:00\",\n" +
                "        \"ORDNAME\": \"PO24NET000009\",\n" +
                "        \"STATDES\": \"מבוטלת\",\n" +
                "        \"OWNERLOGIN\": \"hila\",\n" +
                "        \"CODE\": \"ש\\\"ח\",\n" +
                "        \"BRANCHNAME\": \"11\",\n" +
                "        \"QPRICE\": 0.00,\n" +
                "        \"TOTPRICE\": 0.00,\n" +
                "        \"TOTQUANT\": 0.000\n" +
                "    }\n" +
                "]";
    }

}
