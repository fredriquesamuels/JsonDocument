package org.tect.platform.document;

import java.util.Date;
import java.util.Optional;

/**
 * A leaf value for the {@link JsonDocument} structure.
 */
public interface JsonAttribute {

    /**
     * @return The unique id local to the document.
     */
    long getId();

    /**
     * @return <code>true</code> if attribute has been saved.
     */
    boolean isPersisted();

    /**
     * @return The attribute type.
     *
     * @see AttributeType
     */
    AttributeType type();

    /**
     * @return The parent @{@link JsonDocument}.
     */
    JsonDocument getDocument();

    /**
     * @return Get the internal object.
     */
    Object value();

    /**
     * Delete attribute from document.
     */
    void delete();

    /**
     * @return The lookup id for this attribute. <code>-1</code> if
     *  attribute has not been saved.
     */
    long getPersistedId();

    /**
     * Get the attribute value as a {@link String}.
     *
     * Return {@link Optional#empty()} if the attribute value is not of {@link AttributeType#TEXT}.
     *
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<String> getTextValue();

    /**
     * Get the attribute value as a {@link Long}.
     *
     * Return {@link Optional#empty()} if the attribute value is not of {@link AttributeType#NUMBER}.
     *
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Long> getNumberValue();

    /**
     * Get the attribute value as a {@link Double}.
     *
     * Return {@link Optional#empty()} if the attribute value is not of {@link AttributeType#DECIMAL}.
     *
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Double> getDecimalValue();

    /**
     * Get the attribute value as a {@link Boolean}.
     *
     * Return {@link Optional#empty()} if the attribute value is not of {@link AttributeType#BOOLEAN}.
     *
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Boolean> getBoolValue();

    /**
     * Get the attribute value as a {@link Date}.
     *
     * Return {@link Optional#empty()} if the attribute value is not of {@link AttributeType#DATE}.
     *
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Date> getDateValue();

    /**
     * Get the attribute value as a {@link DocumentReference}.
     *
     * Return {@link Optional#empty()} if the attribute value is not of {@link AttributeType#REFERENCE}.
     *
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<DocumentReference> getReferenceValue();

    /**
     * Get the attribute as a {@link JsonArray}.
     *
     * Return {@link Optional#empty()} if the attribute is not of {@link AttributeType#ARRAY}.
     *
     * @return <code>Optional.of(this)</code> or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<JsonArray> getArrayValue();

    /**
     * Get the attribute as a {@link JsonElement}.
     *
     * Return {@link Optional#empty()} if the attribute value is not of {@link AttributeType#ELEMENT}.
     *
     * @return <code>Optional.of(this)</code> or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<JsonElement> getElementValue();


    Object toObject();
}
