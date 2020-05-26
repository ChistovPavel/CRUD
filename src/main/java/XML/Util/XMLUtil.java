package XML.Util;

import XML.Exceptions.XMLProcessException;
import Application.User;
import XML.IDGroups;
import XML.SupportGroups;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.javatuples.Triplet;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class XMLUtil {

    private static Logger logger = LogManager.getLogger(XMLUtil.class);

    public static final String root = "Groups";

    public static final String mainGroup = "mainGroup";

    public static final String mainValueField = "value";
    public static final String idField = "ID";
    public static final String firstNameIdField = "FID";
    public static final String secondNameIdField = "SID";
    public static final String birthDateIdField = "BID";

    public static final String baseStorageItem = "user";

    public static final Integer maxIdValuePosition = 0;
    public static final Integer priorityFreeIdPosition = 1;

    public static Document initXML(String path) throws XMLProcessException
    {
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error(e);
            throw new XMLProcessException(XMLProcessException.XML_INIT_EXCEPTION, e);
        }
        Document xmlDocument = documentBuilder.newDocument();

        Element groups = xmlDocument.createElement(root);
        Element mainGroupElement = xmlDocument.createElement(mainGroup);
        Element firstNameGroupElement = xmlDocument.createElement(SupportGroups.firstNameGroup.toString());
        Element secondNameGroupElement = xmlDocument.createElement(SupportGroups.secondNameGroup.toString());
        Element birthDateGroupElement = xmlDocument.createElement(SupportGroups.birthDateGroup.toString());
        Element mainGroupIdElement = xmlDocument.createElement(IDGroups.MGID.toString());
        Element firstNameIdGroupElement = xmlDocument.createElement(IDGroups.FNID.toString());
        Element secondNameIdGroupElement = xmlDocument.createElement(IDGroups.SNID.toString());
        Element birthDateIdGroupElement = xmlDocument.createElement(IDGroups.BDID.toString());

        Element base = xmlDocument.createElement(idField);
        base.setTextContent("1");

        groups.appendChild(mainGroupElement);
        groups.appendChild(firstNameGroupElement);
        groups.appendChild(secondNameGroupElement);
        groups.appendChild(birthDateGroupElement);

        mainGroupIdElement.appendChild(base);
        groups.appendChild(mainGroupIdElement);

        firstNameIdGroupElement.appendChild(base.cloneNode(true));
        groups.appendChild(firstNameIdGroupElement);

        secondNameIdGroupElement.appendChild(base.cloneNode(true));
        groups.appendChild(secondNameIdGroupElement);

        birthDateIdGroupElement.appendChild(base.cloneNode(true));
        groups.appendChild(birthDateIdGroupElement);

        xmlDocument.appendChild(groups);

        try {
            writeDocument(xmlDocument, path);
        }
        catch (TransformerException| FileNotFoundException e) {
            logger.error(e);
            throw new XMLProcessException(XMLProcessException.XML_INIT_EXCEPTION, e);
        }
        logger.debug("XML storage init successfully");
        return xmlDocument;
    }

    public static void writeDocument(Document document, String path) throws FileNotFoundException, TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new FileOutputStream(path));
        transformer.transform(new DOMSource(document), result);
    }

    public static Integer checkStorage(Document xmlDocument, SupportGroups groupName, String value) throws XMLProcessException
    {
        logger.debug(new StringBuilder().append("GroupName: ").append(groupName.toString()).append("\tValue: ").append(value).toString());

        Integer id = null;

        NodeList targetItems = findGroup(xmlDocument, groupName.toString()).getChildNodes();

        for (int i = 0; i < targetItems.getLength(); i++)
        {
            NamedNodeMap attributes = targetItems.item(i).getAttributes();
            if (attributes != null) {
                /*logger.debug(new StringBuilder().append(attributes.getNamedItem("value").getTextContent())
                    .append(" ")
                    .append(attributes.getNamedItem("ID").getTextContent()).toString());*/
                if (attributes.getNamedItem(mainValueField).getTextContent().equals(value))
                {
                    id = Integer.valueOf(attributes.getNamedItem(idField).getTextContent());
                    break;
                }
            }
        }

        logger.debug(new StringBuilder().append("Found ID: ").append(id).toString());

        return id;
    }

    public static Integer addItemToStorage(Document xmlDocument, SupportGroups groupName, String value) throws XMLProcessException
    {
        Integer id = null;

        Node targetGroup = findGroup(xmlDocument, groupName.toString());

        id = getFreeId(xmlDocument, IDGroups.interpret(groupName));

        Element item = xmlDocument.createElement(baseStorageItem);
        item.setAttribute(mainValueField, value);
        item.setAttribute(idField, id.toString());

        targetGroup.appendChild(item);

        return id;
    }

    public static Integer addItemToStorage(Document xmlDocument, Integer fid, Integer sid, Integer bid) throws XMLProcessException {

        Integer id = getFreeId(xmlDocument, IDGroups.MGID);

        Node targetGroup = findGroup(xmlDocument, mainGroup);

        Element item = xmlDocument.createElement(baseStorageItem);
        item.setAttribute(firstNameIdField, fid.toString());
        item.setAttribute(secondNameIdField, sid.toString());
        item.setAttribute(birthDateIdField, bid.toString());
        item.setAttribute(idField, id.toString());

        targetGroup.appendChild(item);

        return id;
    }

    private static Node findGroup(Document xmlDocument, String groupName) throws XMLProcessException {
        NodeList groups = xmlDocument.getDocumentElement().getChildNodes();

        if (groups == null)
        {
            XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_FORMAT_EXCEPTION, "XML file <Groups> (root group) is null");
            logger.error(ex);
            throw ex;
        }

        if (groups.getLength() == 0)
        {
            XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_FORMAT_EXCEPTION, "XML file <Groups> children is null");
            logger.error(ex);
            throw ex;
        }

        Node targetGroup = null;

        for (int i = 0; i < groups.getLength(); i++)
        {
            if (groups.item(i).getNodeName().equals(groupName))
            {
                targetGroup = groups.item(i);
                logger.debug(new StringBuilder("Found group: ").append(targetGroup.getNodeName()).toString());
                return targetGroup;
            }
        }

        XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_FORMAT_EXCEPTION, new StringBuilder("Can't find group: ").append(groupName).toString());
        logger.error(ex);
        throw ex;
    }

    public static Triplet<Integer, Integer, Integer> getUser(Document xmlDocument, Integer id) throws XMLProcessException {

        NodeList group = findGroup(xmlDocument, mainGroup).getChildNodes();

        String fid = null, sid = null, bid = null;

        for (int i = 0; i < group.getLength(); i++)
        {
            NamedNodeMap attributes = group.item(i).getAttributes();
            if (attributes != null)
            {
                if (Integer.valueOf(attributes.getNamedItem(idField).getTextContent()) == id)
                {
                    fid = attributes.getNamedItem(firstNameIdField).getTextContent();
                    sid = attributes.getNamedItem(secondNameIdField).getTextContent();
                    bid = attributes.getNamedItem(birthDateIdField).getTextContent();
                    logger.debug(new StringBuilder("Found user info - FID: ")
                            .append(fid)
                            .append("\tSID: ")
                            .append(sid)
                            .append("\tBID: ")
                            .append(bid).toString());
                    return new Triplet<>(Integer.valueOf(fid), Integer.valueOf(sid), Integer.valueOf(bid));
                }
            }
        }
        XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_USER_SEARCH_EXCEPTION,
                new StringBuilder()
                        .append("Storage doesn't have info about user - ID: ")
                        .append(id.toString())
                        .toString());
        logger.info(ex);
        throw ex;
    }

    public static void getAllUsersId(Document xmlDocument, List<Integer> holdersId) throws XMLProcessException
    {

        NodeList group = findGroup(xmlDocument, mainGroup).getChildNodes();

        for (int i = 0; i < group.getLength(); i++)
        {
            NamedNodeMap attributes = group.item(i).getAttributes();
            if (attributes != null)
            {
                holdersId.add(Integer.valueOf(attributes.getNamedItem(idField).getTextContent()));
            }
        }
    }

    public static void getAllUsersIdParametrized(Document xmlDocument, List<Integer> holdersId, User parameters)
            throws XMLProcessException
    {
        NodeList group = findGroup(xmlDocument, mainGroup).getChildNodes();

        for (int i = 0; i < group.getLength(); i++)
        {
            NamedNodeMap attributes = group.item(i).getAttributes();
            if (attributes != null)
            {
                String firstNameValue = null;
                String secondNameValue = null;
                String birthDateValue = null;

                if (parameters.getFirstName() != null)
                {
                    firstNameValue = getUserDetail(xmlDocument,
                            SupportGroups.firstNameGroup,
                            Integer.valueOf(attributes.getNamedItem(firstNameIdField).getTextContent()));
                }
                if (parameters.getSecondName() != null)
                {
                    secondNameValue = getUserDetail(xmlDocument,
                            SupportGroups.secondNameGroup,
                            Integer.valueOf(attributes.getNamedItem(secondNameIdField).getTextContent()));
                }
                if (parameters.getBirthDate() != null)
                {
                    birthDateValue = getUserDetail(xmlDocument,
                            SupportGroups.birthDateGroup,
                            Integer.valueOf(attributes.getNamedItem(birthDateIdField).getTextContent()));
                }

                if (firstNameValue.equals(parameters.getFirstName()) &&
                    secondNameValue.equals(parameters.getSecondName()) &&
                    birthDateValue.equals(parameters.getBirthDate()))
                {
                    holdersId.add(Integer.valueOf(attributes.getNamedItem(idField).getTextContent()));
                }
            }
        }
    }

    public static String getUserDetail(Document xmlDocument, SupportGroups groupName, Integer id) throws XMLProcessException
    {
        NodeList group = findGroup(xmlDocument, groupName.toString()).getChildNodes();

        for (int i = 0; i < group.getLength(); i++)
        {
            NamedNodeMap attributes = group.item(i).getAttributes();
            if (attributes != null)
            {
                if (Integer.valueOf(attributes.getNamedItem(idField).getTextContent()) == id)
                {
                    String result = attributes.getNamedItem(mainValueField).getTextContent();
                    logger.debug(new StringBuilder().append("Found successfully - ID: ")
                            .append(id.toString())
                            .append("\tvalue: ")
                            .append(result).toString());
                    return result;
                }
            }
        }

        XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_USER_SEARCH_EXCEPTION,
                new StringBuilder("Can't find user info - Group: ")
                .append(groupName.toString())
                .append("\tID: ")
                .append(id.toString()).toString());
        logger.error(ex);
        throw ex;
    }

    public static void deleteUserWithCheck(Document xmlDocument, String groupName, String field, Integer id) throws XMLProcessException
    {
        Integer count = XMLUtil.groupHasValue(xmlDocument, mainGroup, field, id.toString());

        if (count == 0)
        {
            XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_USER_SEARCH_EXCEPTION, new StringBuilder().append("The group ")
                    .append(mainGroup)
                    .append(" must have more than zero user info with ")
                    .append(field)
                    .append(" ")
                    .append(id.toString()).toString());
            logger.error(ex);
            throw ex;
        }
        else if (count != 1)
        {
            logger.debug(new StringBuilder("More that one user info found - Group: ")
                    .append(mainGroup)
                    .append("\t")
                    .append(field).append(": ")
                    .append(id.toString())
                    .append("\tcount: ")
                    .append(count.toString())
                    .toString());
            return;
        }

        Node node = findGroup(xmlDocument, groupName);
        NodeList group = node.getChildNodes();

        for (int i = 0; i < group.getLength(); i++)
        {
            NamedNodeMap attributes = group.item(i).getAttributes();
            if (attributes != null)
            {
                if (Integer.valueOf(attributes.getNamedItem(idField).getTextContent()) == id)
                {
                    addFreeId(xmlDocument, IDGroups.interpret(groupName), id);
                    node.removeChild(group.item(i));
                    logger.debug(new StringBuilder("Delete successfully - Group: ")
                            .append(groupName)
                            .append("\tID: ")
                            .append(id).toString());
                    return;
                }
            }
        }

        XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_USER_SEARCH_EXCEPTION, new StringBuilder("Can't find user info - Group: ")
                .append(groupName.toString())
                .append("\tID: ")
                .append(id.toString()).toString());
        logger.error(ex);
        throw ex;
    }

    private static Integer groupHasValue(Document xmlDocument, String groupName, String attributeName, String attributeValue)
            throws XMLProcessException
    {
        Integer counter = 0;

        NodeList group = findGroup(xmlDocument, groupName).getChildNodes();

        if (group.getLength() == 0)
        {
            return 0;
        }

        for (int i = 0; i < group.getLength(); i++)
        {
            NamedNodeMap attributes = group.item(i).getAttributes();
            if (attributes != null)
            {
                if (attributes.getNamedItem(attributeName).getTextContent().equals(attributeValue))
                {
                    counter++;
                }
            }
        }

        return counter;
    }

    public static void updateAttributeValue(Document xmlDocument, String groupName, Integer id, String attributeName, String attributeValue)
            throws XMLProcessException
    {
        NodeList group = findGroup(xmlDocument, groupName).getChildNodes();

        for (int i = 0; i < group.getLength(); i++)
        {
            NamedNodeMap attributes = group.item(i).getAttributes();
            if (attributes != null)
            {
                if (Integer.valueOf(attributes.getNamedItem(idField).getTextContent()) == id)
                {
                    ((Element)group.item(i)).setAttribute(attributeName, attributeValue);
                    logger.debug(new StringBuilder("Update attribute - Group: ")
                            .append(groupName).append("\tID: ")
                            .append(id.toString()).append("\tattribute name: ")
                            .append(attributeName).append("\tnew attribute value: ")
                            .append(attributeValue).toString());
                    return;
                }
            }
        }

        XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_USER_SEARCH_EXCEPTION, new StringBuilder("Can't find user info - Group: ")
                .append(groupName)
                .append("\tID: ")
                .append(id.toString()).toString());
        logger.error(ex);
        throw ex;
    }

    private static String tryGetAttribute(NamedNodeMap attributes, String attributeName) throws XMLProcessException {
        Node node = attributes.getNamedItem(attributeName);
        if (node == null)
        {
            XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_ATTRIBUTE_EXCEPTION, new StringBuilder("Can't find attribute: ")
                    .append(attributeName).toString());
            logger.error(ex);
            throw ex;
        }
        String value = null;
        try {
            value = node.getTextContent();
        }
        catch (DOMException ex)
        {
            logger.error(ex);
            throw new XMLProcessException(XMLProcessException.XML_ATTRIBUTE_EXCEPTION, ex);
    }
        logger.debug(new StringBuilder("Attribute: ").append(attributeName)
                .append("\tvalue: ")
                .append(value).toString());
        return value;
    }

    public static void addFreeId(Document xmlDocument, IDGroups groupName, Integer value) throws XMLProcessException
    {
        logger.debug(new StringBuilder("Set free ID to ").append(groupName.toString()).toString());

        Node group = findGroup(xmlDocument, groupName.toString());
        NodeList items = group.getChildNodes();

        Element item = xmlDocument.createElement(idField);
        item.setTextContent(value.toString());

        if (items.getLength() == 1)
        {
            group.appendChild(item);
        }
        else
        {
            int i = 0;
            for (i = priorityFreeIdPosition; i < items.getLength(); i++)
            {
                if (Integer.valueOf(items.item(i).getTextContent()) > value)
                {
                    group.insertBefore(item, items.item(i));
                    break;
                }
            }
            if (i == items.getLength())
            {
                group.appendChild(item);
            }
        }

        logger.debug("Set free ID successfully");
    }

    public static Integer getFreeId(Document xmlDocument, IDGroups groupName) throws XMLProcessException
    {
        logger.debug(new StringBuilder("Get ID for new value from ").append(groupName.toString()).toString());

        Node group = findGroup(xmlDocument, groupName.toString());
        NodeList items = group.getChildNodes();

        Integer returnValue = null;

        if (items.getLength() == 0)
        {
            XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_FORMAT_EXCEPTION,
                    new StringBuilder("Group ").append(groupName.toString()).append("is null").toString());
            throw ex;
        }
        else if (items.getLength() == 1)
        {
            returnValue = Integer.valueOf(items.item(maxIdValuePosition).getTextContent());
            items.item(maxIdValuePosition).setTextContent(((Integer)(returnValue+1)).toString());
        }
        else
        {
            returnValue = Integer.valueOf(items.item(priorityFreeIdPosition).getTextContent());
            group.removeChild(items.item(priorityFreeIdPosition));
        }

        logger.debug(new StringBuilder("ID for new value - ").append("ID: ").append(returnValue.toString()).toString());

        return returnValue;
    }
}
