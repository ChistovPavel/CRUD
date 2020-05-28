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

/**
 * Класс содержит набор статических методов, полезных для работы с XML хранилищем.
 * */
public class XMLUtil
{
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

    /**
     * Метод для инициализации XML хранилища.
     * <p>XML хранилище будет иметь следующую структуры:</p>
     * <pre>
     * {@code <Group>
     *      <mainGroup></mainGroup> группа хранения основной информации о пользователях
     *      <firsNameGroup></firsNameGroup> группа для хранения имен пользователей
     *      <secondNameGroup></secondNameGroup> группа для хранения фамилий пользователей
     *      <birthDateGroup></birthDateGroup> группа для хранения дат рождения пользователй
     *      <MGID></MGID> группа для хранения свободных индексов в группе mainGroup
     *      <FNID></FNID> группа для хранения свободных индексов в группе firsNameGroup
     *      <SNID></SNID> группа для хранения свободных индексов в группе secondNameGroup
     *      <BDID></BDID> группа для хранения свободных индексов в группе birthDateGroup
     * </Group>}
     * </pre>
     * @param path путь к файлу XML хранилища.
     * @return объект реализующий интерфейс {@link Document}, который содержит в себе XML структуру зранилища.
     * @exception XMLProcessException throws в случае ошибки при создании XML файла.
     * */
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

    /**
    * Метод обнолвения файла хранилища на основе объекта реализующего интерфейс {@link Document}.
     * @param document объект, который содержит XML хранилище, которое будет записано в файл;
     * @param path путь к XML файлу хранилища.
     * @exception FileNotFoundException throws в случае отсутствия файла.
     * @exception  TransformerException throws в случае ошибки инстанцирования объект {@link Transformer}.
    * */
    public static void writeDocument(Document document, String path) throws FileNotFoundException, TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new FileOutputStream(path));
        transformer.transform(new DOMSource(document), result);
    }

    /**
     * Метод проверки наличия в определенной вспомогательной группе элемента с атрибутом, равным переданому значению.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link SupportGroups}, содержащий имя вспомагательной группы;
     * @param value значение, которое нужно проверить.
     * @return идентификатор найденного значения в соответствующей группе или null, если значение не найдено.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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

    /**
     * Метод добавления в вспомогательную группу переданного значения.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link SupportGroups}, содержащий имя вспомагательной группы;
     * @param value значение, которое нужно добавить.
     * @return идентификатор добавленного значения в соответствую группу.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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

    /**
     * Метод добавления в группу mainGroup переданных значений.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param fid идентификатор имени из вспомогательной группы firstNameGroup;
     * @param sid идентификатор фамилии из вспомогательной группы secondNameGroup;
     * @param bid идентификатор даты рождения из вспомогательной группы birthDateGroup.
     * @return идентификатор добавленного значения в группу mainGroup.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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

    /**
     * Метод поиска группы в XML хранилище по заданному имени.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName строка {@link String}, содержащая имя искомой группы.
     * @return объект, реализующий интерфейс {@link Node}, который указывает на искому группу в XML хранилище.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища или отсутсвия искомой группы.
     * */
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

    /**
     * Метод поиска информации о пользователе в группе mainGroup по заданному id.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param id идентификатор искомого пользователя.
     * @return объект {@link Triplet}, который содержит идентификаторы для вспомогательных групп
     * firstNameGroup, secondNameGroup, birthDateGroup.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища или отстутсвия пользователя с заданным id.
     * */
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

    /**
     * Метод извлечения идентификатор всех пользователей из группу mainGroup.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param holdersId объект, реализующий интерфейс {@link List}, в который будут помещены идентификаторы пользователей.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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

    /**
     * Метод извлечения идентификатор всех пользователей из группу mainGroup по заданным параметрам.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param holdersId объект, реализующий интерфейс {@link List}, в который будут помещены идентификаторы пользователей;
     * @param parameters объект {@link User}, который содержит параметры, по которым будет производиться поиск.
     *                   Некоторые атрибуты могут быть null. В выходной список добавляются идентификаторы тех пользователей,
     *                   данные которых совпали с соответствующими ненулевыми атрибутами объекта parameters.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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

    /**
     * Метод получения детальной информации пользователя из вспомогательной группы по заданному id.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link SupportGroups}, содержащий имя вспомагательной группы;
     * @param id идентификатор искомого значения.
     * @return найденное строковое значение.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища или остутсвия данных с искомым id.
     * */
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

    /**
     * Метод удаления информации пользователя из указанной группы по указанному id и имени атрибута.
     * Перед удаление производится проверка группы mainGroup на наличие элемента, у которого атрибут с заданным именем равняется id.
     * Если таких элементов нет, то будет броше исключение {@link XMLProcessException}.
     * Если таких элементов несколько, то удаление не производим.
     * Если такой элемент один, то производим удаление.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link SupportGroups}, содержащий имя вспомагательной группы;
     * @param field строка {@link String}, которая содержит имя атрибута;
     * @param id идентификатор искомого значения.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища или остутсвия данных с искомым id.
     * */
    public static void deleteUserWithCheck(Document xmlDocument, String groupName, String field, Integer id)
            throws XMLProcessException
    {
        Integer count = XMLUtil.groupHasValue(xmlDocument, mainGroup, field, id.toString());

        if (count == 0)
        {
            XMLProcessException ex = new XMLProcessException(XMLProcessException.XML_USER_SEARCH_EXCEPTION, new StringBuilder().append("The group ")
                    .append(mainGroup.toString())
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
                    .append(mainGroup.toString())
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

    /**
     * Метод проверки заданной группы на наличие элемента с заданным именем атрибута и значением атрибута.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link SupportGroups}, содержащий имя вспомагательной группы;
     * @param attributeName строка {@link String}, которая содержит имя атрибута.
     * @param attributeValue проверяемое значение.
     * @return количество подходящих элементов указанной группы.
     * @exception  XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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

    /**
     * Метод обновления заданного атрибута элемента в заданной группе по заданному id.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link SupportGroups}, содержащий имя вспомагательной группы;
     * @param id идентификатор элемента в заданной группе;
     * @param attributeName строка {@link String}, которая содержит имя атрибута;
     * @param attributeValue обновляемое значение.
     * @exception XMLProcessException throws в случае нарушения структуры XML хранилища или отсутсвия искомого элемента.
     * */
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

    /*private static String tryGetAttribute(NamedNodeMap attributes, String attributeName) throws XMLProcessException {
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
    }*/

    /**
     * Метод добавления свободного id в заданную группу хранилища.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link IDGroups}, содержащий имя группы для хранения свободных id;
     * @param value свободный идентификатор.
     * @exception XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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

    /**
     * Метод получение свободного id из заданой группы хранилища.
     * @param xmlDocument объект, который содержит XML хранилище;
     * @param groupName объект {@link IDGroups}, содержащий имя группы для хранения свободных id.
     * @return свободый идентификатор для соответствующей группы.
     * @exception XMLProcessException throws в случае нарушения структуры XML хранилища.
     * */
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
