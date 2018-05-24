# JSON Document Storage and Query


## Description

This library allows for the storage and query of JSON structured data. It is
designed to allows easy schema-less storage(on the part of the developer's overhead) of data without the need to install a
third party storage management tool like MongoDB or MySql(Can be used if required). The intended RDMS is Sqlite.

A query api is provided which makes use of [JSAN](https://github.com/fredriquesamuels/JSAN/wiki) notation to denote query fields.

# QUICK GUIDE

### Create Credentials

*Java*
```java
DbCredentials credentials = DbCredentials.builder("jdbc:sqlite:test-db.db", DbTarget.DRIVER_SQLITE).build();
```

### Connect to database

The required tables will automatically be created on construction
if they do not exist already.

*Java*
```java
DocumentDatabase database = new DocumentDatabase(credentials);
```

### Save a JSON string

The save operation creates a new document in the database and returns
the entry's *ID*.

*Java*
```java
long id = database.saveJson("{\"name\" :\"Mike\", \"age\": 24 }");
```

### Query documents

In this example I query all documents that have a attribute "name" that equals
the string "Mike".

*Java*
```java
DocumentQuery query = new DocumentQuery();
query.addCondition(QueryCondition.compare("name", CompareOperation.EQUALS, "Mike"));

List<String> results = database.queryJson(query);
```

If you print the results you would see the following output ...

*Console*
```
{"name":"Mike","age":24,"__persisted_id__":1}
```

### Updating an entry

You can update an entry using the *ID*.
Here we a updating the age.

*Java*
```java
database.updateJson(id, "{\"name\" :\"Mike\", \"age\": 31 }");
```

As you can we we need to provide the entire string when updating
an entry. This is not very efficient. It is for this reason it is recommended
you use the class *JsonDocument* to manage data going in and out of the
database.

## JsonDocument.java

*JsonDocument* is an object used to manage changes in the json structure.
Internally it is just a fancy map of key values and/or arrays. However, crucially
it stores the needed information to the database to delete attributes that
are removed from the document was last changed. There are also convenient methods for accessing and writing fields in addition to methods for converting to and from JSON strings.

Here is a quick guide to how the above operation can be performed
using the document object instead the raw string.

### Create a new document

*Java*
```java
JsonDocument document = new JSonDocument();
```

Populate the data.

*From a string*
```java
document.populateFromJson( "{\"name\" :\"Mike\", \"age\": 24 }");
```

*Using the SET methods*
```
document.set("name", "Mike");
document.set("age", 24);
```

### Save a document

```java
database.saveDocument(document);
```

### Query the document
```java
List<JsonDocument> results = database.query(query);
```

### Accessing Values

The *get[type]Value* methods return the Java 8 *Optional* object. This
gives the caller the option to decide what to do if the attribute they
wanted does not exists.

```java
System.out.println("Document ID? : " +  document.getPersistedId());
System.out.println("Name : " +  document.getTextValue("name").get());
System.out.println("Age : " +  document.getNumberValue("age").get());
System.out.println("Is Saved? : " +  document.isPersisted());
```