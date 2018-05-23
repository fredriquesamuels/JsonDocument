package org.tect.platform.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class JsonUtils {

    public static Map<String, Object> parseToMap(String var0) {
        try {
            ObjectMapper var1 = new ObjectMapper();
            return (Map)var1.readValue(var0, new TypeReference<Map<String, Object>>() {
            });
        } catch (FileNotFoundException var2) {
            var2.printStackTrace();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return null;
    }

    public static String writeToString(Object var0) {
        ObjectMapper var1 = new ObjectMapper();

        try {
            return var1.writeValueAsString(var0);
        } catch (JsonProcessingException var3) {
            throw new JsonUtils.JsonError(var3);
        }
    }

    public static final class JsonError extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public JsonError(Exception var1) {
            super(var1);
        }
    }
}
