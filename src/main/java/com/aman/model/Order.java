package com.aman.model;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private String ORDNAME;
    private String CURDATE;
    private String SUPNAME;
    private String TQUANT;
    private String PARTNAME;
    private String COLINE;
    private String BRANCHNAME;
    
    public static boolean createXMLFile(String fileName, String path, List<Order> orders) {
        try {
            // Create DocumentBuilderFactory and DocumentBuilder
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            // Create a new document
            Document document = documentBuilder.newDocument();

            // Create root element
            Element root = document.createElement("CartOrderFinishedGoods");
            document.appendChild(root);

            // Iterate over the list of orders
            for (Order order : orders) {
                // Create line element for each order
                Element lineElement = document.createElement("Line");
                root.appendChild(lineElement);

                Element itemElement = document.createElement("Item");
                lineElement.appendChild(itemElement);

                // Add ordName element
                Element CUNOElement = document.createElement("CUNO");
                String supName = order.getSUPNAME() != null ? order.getSUPNAME() : "";
                CUNOElement.appendChild(document.createTextNode(supName));
                itemElement.appendChild(CUNOElement);

                // Add ordName element
                Element CUORElement = document.createElement("CUOR");
                String ordName = order.getORDNAME() != null ? order.getORDNAME() : "";
                CUORElement.appendChild(document.createTextNode(ordName));
                itemElement.appendChild(CUORElement);

                // Add ordName element
                Element CUPOElement = document.createElement("CUPO");
                String coLine = order.getCOLINE() != null ? order.getCOLINE() : "";
                CUPOElement.appendChild(document.createTextNode(coLine));
                itemElement.appendChild(CUPOElement);

                Element POPNElement = document.createElement("POPN");
                String partName = order.getPARTNAME() != null ? order.getPARTNAME() : "";
                POPNElement.appendChild(document.createTextNode(partName));
                itemElement.appendChild(POPNElement);

                Element ORDTElement = document.createElement("ORDT");
                String curDate = order.getCURDATE() != null ? order.getCURDATE() : "";
                ORDTElement.appendChild(document.createTextNode(curDate));
                itemElement.appendChild(ORDTElement);

                Element ADIDElement = document.createElement("ADID");
                String branchName = order.getBRANCHNAME() != null ? order.getBRANCHNAME() : "";
                ADIDElement.appendChild(document.createTextNode(branchName));
                itemElement.appendChild(ADIDElement);

                Element QuantityElement = document.createElement("Quantity");
                String tQuant = order.getTQUANT() != null ? order.getTQUANT() : "";
                QuantityElement.appendChild(document.createTextNode(tQuant));
                itemElement.appendChild(QuantityElement);

                

                // Add any other elements you need here, following the same pattern
            }

            // Create the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);

            // Specify the file name and path
            File file = new File(path + File.separator + fileName + ".xml");
            StreamResult streamResult = new StreamResult(file);

            // Transform the document to an XML file
            transformer.transform(domSource, streamResult);

            return true;

        } catch (ParserConfigurationException | javax.xml.transform.TransformerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
