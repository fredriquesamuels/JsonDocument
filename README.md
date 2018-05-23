# JSON Document Storage and Query


## Description

This library allows for the storage and query of JSON structured data. It is
designed to allows easy schema-less storage(on the part of the developer's overhead) of data without the need to install a
third party storage management tool like MongoDB or MySql(Can be used if required). It's primary storage database is

You can read the specification docs [here](https://github.com/fredriquesamuels/JSAN/wiki)

## API

### Test JSON

```java
String TEST_JSON = "{\"names\": { \"first\":\"jack\", \"last\":\"black\" }, \"age\":5 }"
```

### Convert JSON to JSAN

**Java**
```java
//convert to key and value pairs
List<JSANKeyValue> keyValues = JSANMapper.toKeyValues(TEST_JSON);

//print values
for (JSANKeyValue kv : keyValues) {
    System.out.println(kv.getName() + " " + kv.getValue());
}
```
**console**
```
names.first jack
names.last black
age 5
```

### Convert JSAN to JSON

**Java**
```java
String json = JSANParser.parseToJson(keyValues);
System.out.println(json);
```

**console**
```
{"names":{"first":"jack","last":"black"},"age":5}
```

