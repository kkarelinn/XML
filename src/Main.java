import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Main {
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws ParserConfigurationException {

        ArrayList<Student> group = new ArrayList<>();        //list of ALL students from *.xml

        Document document = getDocument();

        readXML(document, group);

        System.out.print("\nWould you like print data(y/n): ");
        if (getBooFromLine()) printList(group);

        writeXML(document);

    }

    private static boolean getBooFromLine() {
        while (true) {
            try {
                String temp = br.readLine();
                if (temp.matches("[yn]{1}")) {
                    return temp.equals("y");
                }
            } catch (IOException e) {
                System.out.println("Some mistake");
            }
            System.out.print("Wrong parameter. Enter again: ");
        }
    }

    private static File getSourceFileFromLine() {
        File source = null;
        System.out.print("Enter *.xml source file name: ");
        while (true) {
            try {
                String str = br.readLine();
                if (str.matches("\\w+.xml")) {
//                if (str.equals("+.xml")){
                    source = new File(str);
                    return source;
                }
                System.out.print("Wrong file name. Enter again: ");
            } catch (IOException e) {
                System.out.print("Wrong file name. Enter again: ");
            }
        }
    }

    private static File getDestXMLFile() {
        File dest = null;
        System.out.print("Enter *.xml destination file name: ");
        while (true) {
            try {
                String str = br.readLine();
                if (str.matches("\\w+.xml")) {
                    dest = new File(str);
                    return dest;
                }
                System.out.print("Wrong file name. Enter again: ");
            } catch (IOException e) {
                System.out.print("Wrong file name. Enter again: ");
            }
        }
    }

    private static boolean checkAverage(double average, List<Subject> list) {
        double avgList = list.stream().mapToDouble(Subject::getMark).average().orElse(0.0);
        avgList = Math.round(avgList * 10) / 10d;
        return avgList == average;
    }

    private static double getActAvg(ArrayList<Subject> subjectList) {
        double d = subjectList.stream().mapToDouble(Subject::getMark).average().orElse(0.0);
        return Math.round(d * 10) / 10d;
    }

    private static void printList(List<Student> list) {
        System.out.println("Actual data of students is:\n");
        for (
                Student st : list) {
            System.out.println(st);
            System.out.println();
        }
    }

    private static Document getDocument() throws ParserConfigurationException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
            public void warning(SAXParseException e) throws SAXException {
                System.out.println("warning " + e.getMessage());
                throw new SAXException();
            }

            public void error(SAXParseException e) throws SAXException {
                System.out.println(e.getMessage());
                throw new SAXException();
            }

            public void fatalError(SAXParseException e) throws SAXException {
                System.out.println(e.getMessage());
                throw new SAXException();
            }
        });
        File file = null;

        while (true) {
            try {
               file = getSourceFileFromLine();
               return builder.parse(file);

            } catch (IOException | SAXException e) {
                System.out.println("The file " + file.getName() + " does not match the DTD schema");
            }
        }

    }

    private static void readXML(Document document, ArrayList<Student> group) {
        NodeList studentElements = document.getElementsByTagName("student");
        String firstName;
        String lastName;
        String groupNumber;
        ArrayList<Subject> subList;
        double average = 0.0;

        for (int i = 0; i < studentElements.getLength(); i++) {
            Node studentNode = studentElements.item(i);
            NamedNodeMap studAttr = studentNode.getAttributes();
            subList = new ArrayList<>();         //subjects of each student

            firstName = getStringValue(studAttr, "firstname");
            lastName = getStringValue(studAttr, "lastname");
            groupNumber = getStringValue(studAttr, "groupnumber");

            NodeList children = studentNode.getChildNodes();

            for (int j = 0; j < children.getLength(); j++) {
                if (children.item(j).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Node child = children.item(j);
                NamedNodeMap childAttr = child.getAttributes();

                switch (child.getNodeName()) {
                    case "subject" -> {
                        String title = getStringValue(childAttr, "title");
                        int mark = Integer.parseInt(getStringValue(childAttr, "mark"));
                        subList.add(new Subject(title, mark));
                    }
                    case "average" -> {
                        average = Double.parseDouble(child.getTextContent());
                        if (!checkAverage(average, subList)) {
                            System.out.println(" At student " + firstName.toUpperCase(Locale.ROOT)
                                    + " " + lastName.toUpperCase()
                                    + " was incorrect average. Was: " + average
                                    + " - Correct: " + getActAvg(subList) + " -> It has been corrected.");
                            child.setTextContent(getActAvg(subList) + "");
                        }
                    }
                }
            }
            group.add(new Student(firstName, lastName, groupNumber, subList, getActAvg(subList)));
        }

    }

    private static void writeXML(Document document) {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "group.dtd");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(getDestXMLFile());
            transformer.transform(domSource, streamResult);
            System.out.println("Data was write successfully");
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private static String getStringValue(NamedNodeMap attr, String title) {
        return attr.getNamedItem(title).getNodeValue();
    }
}
