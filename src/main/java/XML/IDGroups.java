package XML;

import XML.Util.XMLUtil;

public enum IDGroups {
    MGID,
    FNID,
    SNID,
    BDID;

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

    public static IDGroups interpret(String groupName)
    {
        if (groupName.equals(XMLUtil.mainGroup)) return MGID;
        else if (groupName.equals(SupportGroups.firstNameGroup.toString())) return FNID;
        else if (groupName.equals(SupportGroups.secondNameGroup.toString())) return SNID;
        else if (groupName.equals(SupportGroups.birthDateGroup.toString())) return BDID;
        return null;
    }
}
