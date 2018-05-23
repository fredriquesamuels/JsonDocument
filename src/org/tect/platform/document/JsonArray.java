package org.tect.platform.document;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Public interface representing a JSON array.
 */
public interface JsonArray {

    /**
     * Add a new value to the end of the array.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute add(String value);

    /**
     * Add a new value to the end of the array.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute add(Long value);

    /**
     * Add a new value to the end of the array.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute add(Double value);

    /**
     * Add a new value to the end of the array.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute add(Date value);

    /**
     * Add a new value to the end of the array.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute add(Boolean value);

    /**
     * Add a new value to the end of the array.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute add(DocumentReference value);

    /**
     * Replace a the value of a specific array element.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute set(int index, String value);

    /**
     * Replace a the value of a specific array element.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute set(int index, Long value);

    /**
     * Replace a the value of a specific array element.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute set(int index, Double value);

    /**
     * Replace a the value of a specific array element.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute set(int index, Boolean value);

    /**
     * Replace a the value of a specific array element.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute set(int index, Date value);

    /**
     * Replace a the value of a specific array element.
     *
     * @param value
     * @return The attribute created by the operation.
     */
    JsonAttribute set(int index, DocumentReference value);

    /**
     * Get the attribute at the given index.
     * @param index
     * @return The attribute at the given index.
     */
    JsonAttribute get(int index);

    /**
     * Add a {@link JsonElement} to the array.
     *
     * @return The element created.
     */
    JsonElement createElement();

    /**
     * Add a {@link JsonArray} to the array.
     *
     * @return The created array.
     */
    JsonArray createArray();

    /**
     * Delete all the attributes int he array.
     */
    void clear();

    /**
     * The size the the array.
     * @return The array size.
     */
    int size();

    /**
     * Iterates through all the attributes
     * and invokes <code>consumer.accept(attribute)</code>
     *
     * @param consumer The consumer that will accept hte attributes.
     */
    boolean forEach(Consumer<JsonAttribute> consumer);

    /**
     * @return A stream of the internal attributes.
     */
    Stream<JsonAttribute> stream();

    /**
     * Get the attribute value as a {@link String}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<String> getTextValue(int index);

    /**
     * Get the attribute value as a {@link Long}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Long> getNumberValue(int index);

    /**
     * Get the attribute value as a {@link Double}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Double> getDecimalValue(int index);

    /**
     * Get the attribute value as a {@link Boolean}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Boolean> getBoolValue(int index);

    /**
     * Get the attribute value as a {@link Date}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<Date> getDateValue(int index);

    /**
     * Get the attribute value as a {@link DocumentReference}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<DocumentReference> getReferenceValue(int index);

    /**
     * Get the attribute value as a {@link JsonArray}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<JsonArray> getArrayValue(int index);

    /**
     * Get the attribute value as a {@link JsonElement}.
     *
     * Return {@link Optional#empty()} if the attribute does not exists
     * or the attribute value is not a string.
     *
     * @param index the item index in the array.
     * @return A non <code>null</code> value or {@link Optional#empty()}.
     * @see {@link Optional#isPresent()}
     */
    Optional<JsonElement> getElementValue(int index);
}
