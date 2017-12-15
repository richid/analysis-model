package edu.hm.hafner.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Base class to test the serialization of instances of {@link Serializable}.
 *
 * @param <T>
 *         concrete type of the {@link Serializable} under test
 *
 * @author Ullrich Hafner
 */
public abstract class SerializableTest<T extends Serializable> extends ResourceTest {
    /**
     * Factory method to create an instance of the {@link Serializable} under test. The instance returned by this method
     * will be serialized to a byte stream, deserialized into an object again, and finally compared to another instance
     * using {@link Object#equals(Object)}.
     *
     * @return the subject under test
     */
    protected abstract T createSerializable();

    @Test
    @DisplayName("should be serializable: instance -> byte array -> instance")
    void shouldBeSerializable() {
        T serializableInstance = createSerializable();

        byte[] bytes = toByteArray(serializableInstance);

        assertThatSerializableCanBeRestoredFrom(bytes);
    }

    /**
     * Resolves the subject under test from an array of bytes and compares the created instance with the original
     * subject under test.
     *
     * @param serializedInstance
     *         the byte stream of the serializable
     */
    protected void assertThatSerializableCanBeRestoredFrom(final byte... serializedInstance) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(serializedInstance))) {
            Object resolved = inputStream.readObject();

            assertThat(resolved).isEqualTo(createSerializable());
        }
        catch (IOException | ClassNotFoundException e) {
            throw new AssertionError("Can' resolve instance from byte array", e);
        }
    }

    /**
     * Write the specified object to a byte array using an {@link ObjectOutputStream}. The class of the object, the
     * signature of the class, and the values of the non-transient and non-static fields of the class and all of its
     * supertypes are written. Objects referenced by this object are written transitively so that a complete equivalent
     * graph of objects can be reconstructed by an ObjectInputStream.
     *
     * @param object
     *         the object to serialize
     *
     * @return the object serialization
     */
    protected byte[] toByteArray(final Serializable object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream stream = new ObjectOutputStream(out)) {
            stream.writeObject(object);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Can't serialize object " + object, exception);
        }
        return out.toByteArray();
    }
}