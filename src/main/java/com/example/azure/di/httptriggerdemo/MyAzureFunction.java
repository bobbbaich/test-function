/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.azure.di.httptriggerdemo;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

import static com.microsoft.azure.functions.HttpMethod.POST;

@Component
public class MyAzureFunction {

    /**
     * Plain Spring bean (not Spring Cloud Functions!)
     */
    @Autowired
    private Function<String, String> echo;

    /**
     * Plain Spring bean (not Spring Cloud Functions!)
     */
    @Autowired
    private Function<String, String> uppercase;

    /**
     * The FunctionCatalog leverages the Spring Cloud Function framework.
     */
    @Autowired
    private FunctionCatalog functionCatalog;

    @FunctionName("bean")
    public String plainBeans(
            @HttpTrigger(name = "req", methods = {
                    POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        return echo.andThen(uppercase).apply(request.getBody().get());
    }

    @FunctionName("scf")
    public HttpResponseMessage springCloudFunction(
            @HttpTrigger(name = "req", methods = {POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        context.getLogger().info("Request body: " + request.getBody().get());

        // Use SCF composition. Composed functions are not just spring beans but SCF such.
        Function composed = this.functionCatalog.lookup("echo|reverse|uppercase");

        return request.createResponseBuilder(HttpStatus.OK)
                .body(composed.apply(request.getBody().get()))
                .build();
    }
}
