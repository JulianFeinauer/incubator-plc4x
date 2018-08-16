package org.aparche.plc4x.java.encoders;

/**
 * Decoder that is able to encode a byte array with given physical representation to given target class.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
@FunctionalInterface
public interface Decoder<T> {

    T decode(Class<T> target, Representation description, byte[] data);

}
