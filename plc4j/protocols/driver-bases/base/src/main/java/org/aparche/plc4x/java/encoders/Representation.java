package org.aparche.plc4x.java.encoders;

import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Case class containing information about the byte array to encode / decode.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
public class Representation {

    private ByteOrder byteOrder;
    private int numberOfBits;
    private DataRepresentation dataType;

    public Representation(ByteOrder byteOrder, int numberOfBits, DataRepresentation dataType) {
        this.byteOrder = byteOrder;
        this.numberOfBits = numberOfBits;
        this.dataType = dataType;
    }

    /**
     * @return LITTLE / BIG ENDIANESS
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    /**
     * Number of bits, i.e., java int would have 32 bit.
     * @return Number of bits, the value contains of.
     */
    public int getNumberOfBits() {
        return numberOfBits;
    }

    /**
     * Physical data representation
     * @return Physical data representation
     */
    public DataRepresentation getDataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Representation that = (Representation) o;
        return getNumberOfBits() == that.getNumberOfBits() &&
            Objects.equals(getByteOrder(), that.getByteOrder()) &&
            getDataType() == that.getDataType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getByteOrder(), getNumberOfBits(), getDataType());
    }

    /**
     * Enum to describe the "physical datatypes"
     */
    enum DataRepresentation {
        BOOLEAN,
        DECIMAL,
        INTEGER,
        UNSIGNED_INTEGER
    }
}
