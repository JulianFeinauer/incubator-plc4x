package org.aparche.plc4x.java.encoders;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static org.aparche.plc4x.java.encoders.Representation.DataRepresentation.*;

/**
 * Static class which holds often used Representations.
 *
 * @author julian
 * Created by julian on 16.08.18
 */
public class Representations {

    private Representations() {
        throw new UnsupportedOperationException();
    }

    // Boolean
    public static Representation BOOLEAN = new Representation(BIG_ENDIAN, 1, INTEGER);

    // Signed Integer
    public static Representation INT_1 = new Representation(BIG_ENDIAN, 8, INTEGER);
    public static Representation INT_2 = new Representation(BIG_ENDIAN, 16, INTEGER);
    public static Representation INT_4 = new Representation(BIG_ENDIAN, 32, INTEGER);
    public static Representation INT_8 = new Representation(BIG_ENDIAN, 64, INTEGER);

    // Unsigned Integer
    public static Representation UINT_1 = new Representation(BIG_ENDIAN, 8, UNSIGNED_INTEGER);
    public static Representation UINT_2 = new Representation(BIG_ENDIAN, 16,UNSIGNED_INTEGER);
    public static Representation UINT_4 = new Representation(BIG_ENDIAN, 32,UNSIGNED_INTEGER);
    public static Representation UINT_8 = new Representation(BIG_ENDIAN, 64,UNSIGNED_INTEGER);

    // DECIMAL
    public static Representation IEEE_FLOAT_4 = new Representation(BIG_ENDIAN, 32, DECIMAL);
    public static Representation IEEE_FLOAT_8 = new Representation(BIG_ENDIAN, 64, DECIMAL);

}
