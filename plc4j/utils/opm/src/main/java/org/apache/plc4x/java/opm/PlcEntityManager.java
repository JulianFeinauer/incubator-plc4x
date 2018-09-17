package org.apache.plc4x.java.opm;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.connection.PlcConnection;
import org.apache.plc4x.java.api.connection.PlcReader;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.exceptions.PlcInvalidFieldException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;

/**
 * Manages Entities.
 */
public class PlcEntityManager {

    private final PlcDriverManager driverManager;

    public PlcEntityManager() {
        this.driverManager = new PlcDriverManager();
    }

    public PlcEntityManager(PlcDriverManager driverManager) {
        this.driverManager = driverManager;
    }

    public <T> T read(Class<T> clazz) throws OPMException {
        PlcEntity annotation = clazz.getAnnotation(PlcEntity.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Given Class is no Plc Entity, i.e., not annotated with @PlcEntity");
        }
        // Check if default constructor exists
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Cannot use PlcEntity without default constructor");
        }
        String source = annotation.value();

        PlcReader reader;

        try (PlcConnection connection = driverManager.getConnection(source)) {

            if (!connection.getReader().isPresent()) {
                throw new OPMException("Unable to get Reader for connection with url '" + source + "'");
            }

            reader = connection.getReader().get();

            PlcReadRequest.Builder requestBuilder = reader.readRequestBuilder();

            // Do the necessary queries for all fields
            // HashMap<ReadRequestItem<?>, Field> requestItems = new HashMap<>();
            for (Field field : clazz.getDeclaredFields()) {
                PlcField fieldAnnotation = field.getAnnotation(PlcField.class);
                if (fieldAnnotation == null) {
                    // Ignore that field
                    continue;
                }
                // Create the suitable Request
                String query = fieldAnnotation.value();
                Class<?> expectedType;
                if (field.getType().isPrimitive()) {
                    if (field.getType() == long.class) {
                        expectedType = Long.class;
                    } else {
                        throw new OPMException("Unable to work on fields with type " + field.getType());
                    }
                } else {
                    expectedType = field.getType();
                }

                requestBuilder.addItem(field.getName(), query);
            }

            // Build the request
            PlcReadRequest request;
            try {
                request = requestBuilder.build();
            } catch (PlcInvalidFieldException e) {
                throw new OPMException("Unable to parse one field request", e);
            }

            // Perform the request
            PlcReadResponse response;
            try {
                response = reader.read(request).get(1_000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                throw new OPMException("Request fetching not able", e);
            } catch (TimeoutException e) {
                throw new OPMException("Timeout during fetching values", e);
            }

            // Construct the Object
            T instance = clazz.getConstructor().newInstance();

            // Assign values to all fields
            for (String field : request.getFieldNames()) {
                Object value = response.getObject(field);

                if (value == null) {
                    throw new OPMException("Unable to fetch value for field '" + field + "'");
                }

                // Fetch first value
                Field objectField = clazz.getDeclaredField(field);
                objectField.setAccessible(true);
                objectField.set(instance, value);
            }
            return instance;
        } catch (PlcConnectionException e) {
            throw new OPMException("Unable to get connection with url '" + source + "'", e);
        } catch (Exception e) {
            throw new OPMException("Unable to fetch PlcEntity " + clazz.getName(), e);
        }
    }

    /**
     * Returns a connected proxy.
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws OPMException
     */
    public <T> T connect(Class<T> clazz) throws OPMException {
        if (!clazz.isInterface()) {
            throw new OPMException("Only interfaces can be connected!");
        }
        PlcEntity annotation = clazz.getAnnotation(PlcEntity.class);
        if (annotation == null) {
            throw new OPMException("Need to be a PLC Entity, please add Annotation.");
        }
        try {
            return new ByteBuddy()
                .subclass(clazz)
                .method(isAnnotatedWith(PlcField.class)).intercept(MethodDelegation.to(this))
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded()
                .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new OPMException("Unable to instantiate Proxy", e);
        }
    }

    @RuntimeType
    public Object intercept(@Origin Method m, @This Object o) throws OPMException {
        PlcField annotation = m.getAnnotation(PlcField.class);
        System.out.println("You wanted field: " + annotation.value());
        PlcEntity plcEntity = m.getDeclaringClass().getAnnotation(PlcEntity.class);
        System.out.println("For source: " + plcEntity.value());
        System.out.println("Using the DriverManager: " + driverManager);

        Optional<PlcReader> reader;
        try {
            reader = driverManager.getConnection(plcEntity.value()).getReader();
        } catch (PlcConnectionException e) {
            throw new OPMException("Unable to aquire connection", e);
        }

        // Assume to do the query here...

        // Finished
        return 1L;
    }

}