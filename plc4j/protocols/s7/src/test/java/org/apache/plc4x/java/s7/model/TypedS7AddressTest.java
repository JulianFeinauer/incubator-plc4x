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

package org.apache.plc4x.java.s7.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TypedS7AddressTest {

    @Test
    public void parse() {
        List<String> strings = Arrays.asList(
            "%DB8.DBW7:INT",
            "%DB8.DBL7:INT",
            "%DB8.DBL7:LINT",
            "%DB8.DBX3.1",
            "%DB8.DBX3.1:BOOL",
            "%DB8.DBW7:BOOL",
            "%DB8.DBW7.1:BOOL",
            "%DB8.DBW7:REAL",
            "%DB8.DBW7.3:UINT",
            "%DB8.DBL8:UINT",
            "%DB8.DBL7:ULINT",
            "%DB.DBL7:ULINT",
            "%DB8.DBJ7:ULINT",
            "%ASDF8.DBJ7:ULINT"
        );

        for (String s : strings) {
            try {
                System.out.print(s + " ");
                TypedS7Address parse = TypedS7Address.parse(s);
                System.out.println(parse);
            } catch (S7ParseException e) {
                System.out.println(e);
            }
        }
    }
}