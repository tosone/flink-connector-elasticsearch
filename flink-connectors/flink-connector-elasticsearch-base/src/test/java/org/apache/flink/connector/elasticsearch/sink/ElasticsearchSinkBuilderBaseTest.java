/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.connector.elasticsearch.sink;

import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connectors.test.common.junit.extensions.TestLoggerExtension;

import org.apache.flink.shaded.guava30.com.google.common.collect.Lists;

import org.apache.http.HttpHost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests for {@link ElasticsearchSinkBuilderBase}. */
@ExtendWith(TestLoggerExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ElasticsearchSinkBuilderBaseTest {

    @ParameterizedTest
    @MethodSource("validBuilders")
    void testBuildElasticsearchSink(ElasticsearchSinkBuilderBase<?> builder) {
        builder.build();
    }

    @Test
    void testThrowIfExactlyOnceConfigured() {
        assertThrows(
                IllegalStateException.class,
                () -> createMinimalBuilder().setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE));
    }

    @Test
    void testThrowIfHostsNotSet() {
        assertThrows(
                NullPointerException.class,
                () -> createEmptyBuilder().setEmitter((element, indexer, context) -> {}).build());
    }

    @Test
    void testThrowIfEmitterNotSet() {
        assertThrows(
                NullPointerException.class,
                () -> createEmptyBuilder().setHosts(new HttpHost("localhost:3000")).build());
    }

    private List<ElasticsearchSinkBuilderBase<?>> validBuilders() {
        return Lists.newArrayList(
                createMinimalBuilder(),
                createMinimalBuilder().setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE),
                createMinimalBuilder().setBulkFlushBackoffStrategy(FlushBackoffType.CONSTANT, 1, 1),
                createMinimalBuilder()
                        .setConnectionUsername("username")
                        .setConnectionPassword("password"));
    }

    abstract ElasticsearchSinkBuilderBase<?> createEmptyBuilder();

    abstract ElasticsearchSinkBuilderBase<?> createMinimalBuilder();
}