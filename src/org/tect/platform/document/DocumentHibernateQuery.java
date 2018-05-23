package org.tect.platform.document;

import java.util.Map;

public interface DocumentHibernateQuery {
    Map<String,Object> getParamsMap();

    String getQueryString();
}
