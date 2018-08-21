/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.apache.plc4x.java.examples.plclogger;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.connection.PlcConnection;
import org.apache.plc4x.java.api.connection.PlcReader;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.items.ReadRequestItem;
import org.apache.plc4x.java.api.messages.specific.TypeSafePlcReadRequest;
import org.apache.plc4x.java.api.messages.specific.TypeSafePlcReadResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlcDemo {

    public static void main(String[] args) throws Exception {
        PlcDriverManager manager = new PlcDriverManager();
        try (PlcConnection conn = manager.getConnection("s7://192.168.167.211/0/0")) {

            Optional<PlcReader> optionalReader = conn.getReader();
            if (!optionalReader.isPresent()) {
                return;
            }

            PlcReader reader = optionalReader.get();

            TypeSafePlcReadRequest<Byte> request = new TypeSafePlcReadRequest<>(Byte.class, conn.parseAddress("DATA_BLOCKS/501/0"), 268);
            CompletableFuture<TypeSafePlcReadResponse<Byte>> future = reader.read(request);
            TypeSafePlcReadResponse<Byte> response = future.get();

            System.out.println(response);
            assert response.getResponseItem().isPresent();
            List<Byte> bytes = response.getResponseItem().get().getValues();
            byte[] stringBytes = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                stringBytes[i] = bytes.get(i);
            }

            List<ReadRequestItem<?>> items = Arrays.asList(
                new ReadRequestItem<>(Short.class, conn.parseAddress("DATA_BLOCKS/501/0"), 100),
                new ReadRequestItem<>(Short.class, conn.parseAddress("DATA_BLOCKS/501/0"), 100),
                new ReadRequestItem<>(Short.class, conn.parseAddress("DATA_BLOCKS/501/0"), 40)
            );

            PlcReadRequest plcReadRequest = new PlcReadRequest(items);

            long start = System.currentTimeMillis();
            IntStream.range(1, 100)
                .mapToObj(i -> {
                    try {
                        return reader.read(plcReadRequest).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());

//            futures.stream().forEach(fut -> {
//                try {
//                    ((CompletableFuture) fut).get();
//                    System.out.print(".");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            });

            long stop = System.currentTimeMillis();

            System.out.println((double) (stop - start) / 1000 + " s");

//            System.out.println(new String(stringBytes));
        }
    }
}
