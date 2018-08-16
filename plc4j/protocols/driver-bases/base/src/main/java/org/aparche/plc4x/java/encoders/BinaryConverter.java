package org.aparche.plc4x.java.encoders;

/**
 * Encoder and Decoder for Binary Values.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
class BinaryConverter implements Decoder, Encoder {

    public static BinaryConverter INSTANCE = new BinaryConverter();

    @Override
    public <T> T decode(Class<T> target, Representation description, byte[] data) {
        assert data.length == 1;
        assert target.isInstance(Boolean.class);
        return target.cast((data[0] & 0x01) == 0x01);
    }

    @Override
    public <T> byte[] encode(T value, Representation description) {
        return new byte[0];
    }
}
