package XML;

import Application.User;
import CRUD.*;
import XML.Util.XMLUtil;
import XML.Exceptions.XMLProcessException;
import lombok.Getter;
        import lombok.Setter;
        import org.apache.log4j.LogManager;
        import org.apache.log4j.Logger;
        import org.javatuples.Triplet;
        import org.w3c.dom.Document;
        import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.ParserConfigurationException;
        import javax.xml.transform.TransformerException;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

public class XMLHandler implements CRUD {

    private static Logger logger = LogManager.getLogger(XML.XMLHandler.class);
    @Getter @Setter private String path;
    private Document xmlDocument;

    public XMLHandler(String inPath) throws XMLProcessException
    {
        this.path = inPath;
        if (!(new File(this.path).exists()))
        {
            this.xmlDocument = XMLUtil.initXML(this.path);
        }
        else
        {
            try {
                this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                logger.error(e);
                throw new XMLProcessException(XMLProcessException.XML_INIT_EXCEPTION, e);
            }
            logger.info("Open and parse xml file successfully");
        }
    }

    @Override
    public Integer create(User user) throws XMLProcessException {
        logger.info(new StringBuilder().append("Create request -\t").append(user.toString()).toString());

        Integer fid = XMLUtil.checkStorage(this.xmlDocument, SupportGroups.firstNameGroup, user.getFirstName());
        Integer sid = XMLUtil.checkStorage(this.xmlDocument, SupportGroups.secondNameGroup, user.getSecondName());
        Integer bid = XMLUtil.checkStorage(this.xmlDocument, SupportGroups.birthDateGroup, user.getBirthDate());

        if (fid == null) fid = XMLUtil.addItemToStorage(this.xmlDocument, SupportGroups.firstNameGroup, user.getFirstName());
        if (sid == null) sid = XMLUtil.addItemToStorage(this.xmlDocument, SupportGroups.secondNameGroup, user.getSecondName());
        if (bid == null) bid = XMLUtil.addItemToStorage(this.xmlDocument, SupportGroups.birthDateGroup, user.getBirthDate());

        Integer id = XMLUtil.addItemToStorage(this.xmlDocument, fid, sid, bid);

        try
        {
            XMLUtil.writeDocument(this.xmlDocument, this.path);
        }
        catch (TransformerException | FileNotFoundException e)
        {
            logger.error(e);
            throw new XMLProcessException(XMLProcessException.XML_UPDATE_FILE_EXCEPTION, e);
        }

        logger.info(new StringBuilder().append("Create successfully - ")
                .append("\tFID: ")
                .append(fid)
                .append("\tSID: ")
                .append(sid)
                .append("\tBID: ")
                .append(bid)
                .append("\tID: ")
                .append(id).toString());

        return id;
    }

    @Override
    public List<Integer> read() throws XMLProcessException
    {
        logger.info(new StringBuilder().append("Read all users info request\t").toString());

        List<Integer> usersId = new ArrayList<Integer>();
        XMLUtil.getAllUsersId(xmlDocument, usersId);

        logger.info(new StringBuilder().append("Read all users info successfully\t").toString());

        return usersId;
    }

    @Override
    public List<Integer> read(User parameters) throws XMLProcessException
    {
        StringBuilder sb = new StringBuilder().append("Read users info request with params -");

        if (parameters.getFirstName() != null) sb.append("\tFirstName: ").append(parameters.getFirstName());
        if (parameters.getSecondName() != null) sb.append("\tSecondName: ").append(parameters.getSecondName());
        if (parameters.getBirthDate() != null) sb.append("\tBirthDate: ").append(parameters.getBirthDate());

        logger.info(sb.toString());

        List<Integer> usersId = new ArrayList<Integer>();

        if (parameters.getFirstName() == null &&
                parameters.getSecondName() == null &&
                parameters.getBirthDate() == null)
        {
            XMLUtil.getAllUsersId(xmlDocument, usersId);
        }
        else
        {
            XMLUtil.getAllUsersIdParametrized(xmlDocument, usersId, parameters);
        }

        logger.info(new StringBuilder().append("Read all users info successfully\t").toString());

        return usersId;
    }

    @Override
    public User read(Integer id) throws XMLProcessException
    {
        logger.info(new StringBuilder().append("Read info about user - ID: ").append(id.toString()).toString());

        Triplet<Integer, Integer, Integer> userInfo = XMLUtil.getUser(this.xmlDocument, id);

        String firstNameValue = XMLUtil.getUserDetail(xmlDocument, SupportGroups.firstNameGroup, userInfo.getValue0());
        String secondNameValue = XMLUtil.getUserDetail(xmlDocument, SupportGroups.secondNameGroup, userInfo.getValue1());
        String birthDateValue = XMLUtil.getUserDetail(xmlDocument, SupportGroups.birthDateGroup, userInfo.getValue2());

        User user = new User(firstNameValue, secondNameValue, birthDateValue);

        logger.info(new StringBuilder().append("Found user info - ").append(user.toString()).toString());

        return user;
    }

    @Override
    public void delete(Integer id) throws XMLProcessException
    {
        logger.info(new StringBuilder().append("Delete request - ID: ").append(id.toString()).toString());

        Triplet<Integer, Integer, Integer> userInfo = XMLUtil.getUser(this.xmlDocument, id);

        XMLUtil.deleteUserWithCheck(xmlDocument, SupportGroups.firstNameGroup.toString(), XMLUtil.firstNameIdField, userInfo.getValue0());
        XMLUtil.deleteUserWithCheck(xmlDocument, SupportGroups.secondNameGroup.toString(), XMLUtil.secondNameIdField, userInfo.getValue1());
        XMLUtil.deleteUserWithCheck(xmlDocument, SupportGroups.birthDateGroup.toString(), XMLUtil.birthDateIdField, userInfo.getValue2());
        XMLUtil.deleteUserWithCheck(xmlDocument, XMLUtil.mainGroup, XMLUtil.idField, id);

        try
        {
            XMLUtil.writeDocument(this.xmlDocument, this.path);
        }
        catch (TransformerException| FileNotFoundException e)
        {
            logger.error(e);
            throw new XMLProcessException(XMLProcessException.XML_UPDATE_FILE_EXCEPTION, e);
        }

        logger.info(new StringBuilder().append("Delete successfully ID: ").append(id.toString()).toString());
    }

    @Override
    public User update(Integer id, User newUserInfo)
            throws XMLProcessException
    {
        logger.info(new StringBuilder().append("Update request - ID: ")
                .append(id.toString())
                .append("\tUser info: ")
                .append(newUserInfo).toString());

        Triplet<Integer, Integer, Integer> userInfo = XMLUtil.getUser(this.xmlDocument, id);

        if (newUserInfo.getFirstName() != null)
        {
            XMLUtil.deleteUserWithCheck(xmlDocument, SupportGroups.firstNameGroup.toString(), XMLUtil.firstNameIdField, userInfo.getValue0());
            Integer newFID = XMLUtil.checkStorage(this.xmlDocument, SupportGroups.firstNameGroup, newUserInfo.getFirstName());;
            if (newFID == null) newFID = XMLUtil.addItemToStorage(this.xmlDocument, SupportGroups.firstNameGroup, newUserInfo.getFirstName());
            XMLUtil.updateAttributeValue(this.xmlDocument, XMLUtil.mainGroup, id, XMLUtil.firstNameIdField, newFID.toString());
        }

        if (newUserInfo.getSecondName() != null)
        {
            XMLUtil.deleteUserWithCheck(xmlDocument, SupportGroups.secondNameGroup.toString(), XMLUtil.secondNameIdField, userInfo.getValue1());
            Integer newSID = XMLUtil.checkStorage(this.xmlDocument, SupportGroups.secondNameGroup, newUserInfo.getSecondName());;
            if (newSID == null) newSID = XMLUtil.addItemToStorage(this.xmlDocument, SupportGroups.secondNameGroup, newUserInfo.getSecondName());
            XMLUtil.updateAttributeValue(this.xmlDocument, XMLUtil.mainGroup, id, XMLUtil.secondNameIdField, newSID.toString());
        }
        if (newUserInfo.getBirthDate() != null)
        {
            XMLUtil.deleteUserWithCheck(xmlDocument, SupportGroups.birthDateGroup.toString(), XMLUtil.birthDateIdField, userInfo.getValue2());
            Integer newBID = XMLUtil.checkStorage(this.xmlDocument, SupportGroups.birthDateGroup, newUserInfo.getBirthDate());;
            if (newBID == null) newBID = XMLUtil.addItemToStorage(this.xmlDocument, SupportGroups.birthDateGroup, newUserInfo.getBirthDate());
            XMLUtil.updateAttributeValue(this.xmlDocument, XMLUtil.mainGroup, id, XMLUtil.birthDateIdField, newBID.toString());
        }

        try {
            XMLUtil.writeDocument(this.xmlDocument, this.path);
        } catch (TransformerException | FileNotFoundException e) {
            logger.error(e);
            throw new XMLProcessException(XMLProcessException.XML_UPDATE_FILE_EXCEPTION, e);
        }

        User newUser = this.read(id);
        logger.info(new StringBuilder().append("Update successfully - ").append(newUser.toString()).toString());
        return newUser;
    }
}

