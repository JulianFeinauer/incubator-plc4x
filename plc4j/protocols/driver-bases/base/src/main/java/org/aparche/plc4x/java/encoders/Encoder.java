package org.aparche.plc4x.java.encoders;

/**
 * Decoder that is able to encode a value to a byte Array.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
@FunctionalInterface
public interface Encoder {

    <T> byte[] encode(T value, Representation description);

}
