package org.tect.platform.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class QueryContext {

    private int varIdSeed = 0;
    private final Map<String, Object> paramsMap;
    private final List<String> attributeNamesList;

    QueryContext() {
        paramsMap = new HashMap<>();
        attributeNamesList = new ArrayList<>();
    }

    String generateVar() {
        return "var" + (++varIdSeed);
    }
    String generateAttributeName() {
        return "a";
    }

    List<String> getAttributeNames() {
        return attributeNamesList;
    }

    Map<String,Object> getParamsMap() {
        return paramsMap;
    }

    void setParam(String var, Object value) {
        paramsMap.put(var, value);
    }
}
