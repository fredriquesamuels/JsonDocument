package org.tect.platform.document;

import org.tect.platform.document.hibernate.HibernateData;
import org.tect.platform.jsan.JSANKeyValue;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name="attribute")
public class AttributeHbm extends HibernateData implements JSANKeyValue {
    @Id
    @GeneratedValue
    @Column(name = "id")
    long id;

    @Column(name="document_id")
    long documentId;

    @Column(name="attribute_id")
    long attributeId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    AttributeType type;

    @Column(name = "name")
    String name;

    @Column(name = "text")
    String text;

    @Column(name = "number")
    Long number;

    @Column(name = "decimal")
    Double decimal;

    @Column(name = "bool")
    Boolean bool;

    @Column(name = "group_ids")
    String groupIds;

    @Temporal(TemporalType.TIMESTAMP)
    Date date;

    @Transient
    AttributeImpl attribute;

    @Transient
    List<Long> groupIdsList;


    //hibernate constructor
    public AttributeHbm() {
    }

    @Override
    public Long getId() {
        return id;
    }

    AttributeHbm(String name, AttributeImpl attribute, List<Long> groupIds, long documentId) {
        this.id = attribute.getPersistedId();
        this.attributeId = attribute.getId();
        this.documentId = documentId;
        this.name = name;
        this.groupIds = String.join(",",
                groupIds.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));
        this.attribute = attribute;
        this.groupIdsList = groupIds;
        this.type = attribute.type();

        populate(attribute);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        if(attribute!=null) {
            return attribute.value();
        }
        return this;
    }

    @Override
    public List<Long> getGroupIds() {
        if(groupIdsList!=null) {
            return groupIdsList;
        }
        if(groupIds==null || groupIds.isEmpty()) {
            return Collections.emptyList();
        }
        return Stream.of(groupIds.split(","))
                .map(s ->  Long.valueOf(s))
                .collect(Collectors.toList());
    }

    private void populate(AttributeImpl attribute) {
        Object value = attribute.value();

        switch (attribute.type()) {
            case TEXT:
                text = value.toString();
                break;
            case NUMBER:
                number = (Long) value;
                break;
            case DECIMAL:
                decimal = (Double) value;
                break;
            case BOOLEAN:
                bool = (Boolean) value;
                break;
            case DATE:
                date = (Date)value;
                break;
            case REFERENCE:
                text = ((DocumentReference)value).getType();
                number = ((DocumentReference)value).getId();
                break;

        }

    }
}
