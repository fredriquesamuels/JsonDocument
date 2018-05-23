package org.tect.platform.document;

import java.util.function.Supplier;

public final class DocumentReference {
    private final Supplier<Long> idSupplier;
    private final Supplier<String> typeSupplier;

    DocumentReference(long id, String type) {
        this.idSupplier = () -> id;
        this.typeSupplier = () -> type;
    }

    DocumentReference(JsonDocument document) {
        this.idSupplier = () -> document.getPersistedId();
        this.typeSupplier = () -> document.getType();
    }

    public final String getType() {
        return typeSupplier.get();
    }

    public final long getId() {
        return idSupplier.get();
    }

    public boolean isPersisted() {
        return getId() > 0;
    }
}
