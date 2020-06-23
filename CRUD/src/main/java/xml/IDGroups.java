package xml;

import util.XMLUtil;

/**
 * Enum класс содержит имена групп XML файла хранилища
 * */
public enum IDGroups {
    /**
     * Имя группы для хранения свободных идентификаторов в группе mainGroup
     * */
    MGID,
    /**
     * Имя группы для хранения свободных идентификаторов в группе firstNameGroup
     * */
    FNID,
    /**
     * Имя группы для хранения свободных идентификаторов в группе secondNameGroup
     * */
    SNID,
    /**
     * Имя группы для хранения свободных идентификаторов в группе birthDateGroup
     * */
    BDID;

    /**
     * Метод интерпритирует {@link SupportGroups} в {@link IDGroups}
     * @param groupName объект {@link IDGroups}, который будет интерпритирован.
     * @return интерпритированный объект {@link IDGroups}.
     * */
    public static IDGroups interpret(SupportGroups groupName)
    {
        switch (groupName)
        {
            case firstNameGroup:
                return FNID;
            case secondNameGroup:
                return SNID;
            case birthDateGroup:
                return BDID;
            default:
                return null;
        }
    }

    /**
     * Метод интерпритирует строку {@link String} в {@link IDGroups}
     * @param groupName строка {@link String}, которя содержит имя одной из группы хранилища
     *                  (mainGroup, firstNameGroup, secondNameGroup, birthDateGroup).
     * @return интерпритированный объект {@link IDGroups} или null, если groupName содержит неподходящее имя.
     * */
    public static IDGroups interpret(String groupName)
    {
        if (groupName.equals(XMLUtil.mainGroup)) return MGID;
        else if (groupName.equals(SupportGroups.firstNameGroup.toString())) return FNID;
        else if (groupName.equals(SupportGroups.secondNameGroup.toString())) return SNID;
        else if (groupName.equals(SupportGroups.birthDateGroup.toString())) return BDID;
        return null;
    }
}
