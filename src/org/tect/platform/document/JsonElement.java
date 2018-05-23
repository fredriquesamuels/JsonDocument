package org.tect.platform.document;

import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Public interface for updating and inspecting a json object.
 */
public interface JsonElement {

    /**
     * Set a attribute value. If and attribute with the given key does not exists it is created.
     *
     * @param key The attribute key.
     * @param value The new value.
     * @return The created attribute.
     */
    JsonAttribute set(String key, String value);

    /**
     * Set a attribute value. If and attribute with the given key does not exists it is created.
     *
     * @param key The attribute key.
     * @param value The new value.
     * @return The created attribute.
     */
    JsonAttribute set(String key, Long value);

    /**
     * Set a attribute value. If and attribute with the given key does not exists it is created.
     *
     * @param key The attribute key.
     * @param value The new value.
     * @return The created attribute.
     */
    JsonAttribute set(String key, Date value);

    /**
     * Set a attribute value. If and attribute with the given key does not exists it is created.
     *
     * @param key The attribute key.
     * @param value The new value.
     * @return The created attribute.
     */
    JsonAttribute set(String key, Double value);

    /**
     * Set a attribute value. If and attribute with the given key does not exists it is created.
     *
     * @param key The attribute key.
     * @param value The new value.
     * @return The created attribute.
     */
    JsonAttribute set(String key, DocumentReference value);

    /**
     * Set a attribute value. If and attribute with the given key does not exists it is created.
     *
     * @param key The attribute key.
     * @param value The new value.
     * @return The created attribute.
     */
    JsonAttribute set(String key, Boolean value);

    /**
     * Get an attributes value.
     * @param key The attribute name.
     * @return The attribute or <code>null</code> if no such attribute exits.
     */
    JsonAttribute get(String key);

    /**
     * Create a new attribute that is also an element.
     *
     *
     * @param key The attribute key.
     * @return The create element.
     */
    JsonElement createElement(String key);

    /**
     * Create a new attribute that is also an array.
     *
     * @param key The attribute key.
     * @return The create array.
     */
    JsonArray createArray(String key);

    /**
     * Remove all the attributes from the object.
     */
    void clear();

    /**
     * Get the attribute value as a {@link String}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<String> getTextValue(String key);

    /**
     * Get the attribute value as a {@link Long}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a {@link Long}.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Long> getNumberValue(String key);

    /**
     * Get the attribute value as a {@link Double}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a {@link Double}.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Double> getDecimalValue(String key);

    /**
     * Get the attribute value as a {@link Boolean}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a {@link Boolean}.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Boolean> getBoolValue(String key);

    /**
     * Get the attribute value as a {@link Date}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a {@link Date}.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Date> getDateValue(String key);

    /**
     * Get the attribute value as a {@link DocumentReference}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a {@link DocumentReference}.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<DocumentReference> getReferenceValue(String key);

    /**
     * Get the attribute value as a {@link JsonArray}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a {@link JsonArray}.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<JsonArray> getArrayValue(String key);

    /**
     * Get the attribute value as a {@link JsonElement}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a {@link JsonElement}.
     *
     * @param key The attribute key.
     * @return A string value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<JsonElement> getElementValue(String key);

    /**
     * Iterates through all the attributes
     * and invokes <code>consumer.accept(key, attribute)</code>
     *
     * @param consumer The consumer that will acceot hte attributes.
     */
    void forEach(BiConsumer<String, JsonAttribute> consumer);

    /**
     * @return The amount of attributes in this element.
     */
    int size();

    void populate(Object object);
}
