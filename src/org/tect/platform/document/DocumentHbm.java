package org.tect.platform.document;

import org.tect.platform.document.hibernate.HibernateData;
import org.tect.platform.jsan.JSANParser;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="document")
public final class DocumentHbm extends HibernateData {

    @Transient
    private List<AttributeHbm> attributes;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name="type")
    String type;

    @Column(name = "attribute_id_seed")
    long attributeIdSeed;

    @Override
    public Long getId() {
        return id;
    }

    public void update(JsonDocument document) {
        this.id = document.getPersistedId();;
        this.type = document.getType();
        this.attributeIdSeed = document.getAttributeIdSeed();
    }

    public JsonDocument toDocument() {
        JsonDocument document = new JsonDocument(type);
        populatedDocument(document);
        return document;
    }

    public <T extends JsonDocument> T toDocument(Class<T> aClass) {
        T t;
        try {
            t = aClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        populatedDocument(t);
        return t;
    }

    private void populatedDocument(JsonDocument document) {
        document.setPersistedId(id);
        document.setAttributeIdSeed(attributeIdSeed);

        JSANParser<AttributeHbm> parser = new JSANParser();
        parser.parse(this.attributes, new DocumentJSANReader(document));
    }

    void setAttributes(List<AttributeHbm> attributes) {
        this.attributes = attributes;
    }


}
