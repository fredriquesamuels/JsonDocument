package org.tect.platform.document;

enum ReservedFieldName implements QueryFieldName {
    DOC_ID(QueryCondition.DOC_VAR + ".id"),
    DOC_TYPE(QueryCondition.DOC_VAR + ".type");

    private final String field;

    ReservedFieldName(String field) {
        this.field = field;
    }

    @Override
    public String getName() {
        return field;
    }

    @Override
    public boolean isReservedField() {
        return true;
    }

    @Override
    public boolean isPresent() {
        return true;
    }
}