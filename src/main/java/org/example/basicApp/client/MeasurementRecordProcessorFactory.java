/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.example.basicApp.client;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import org.example.basicApp.client.DBWriter;


/**
 * Generates {@link CountingRecordProcessor}s for counting occurrences of unique values over a given range.
 *
 * @param <T> The type of records the processors this factory creates are capable of counting.
 */
public class MeasurementRecordProcessorFactory<T> implements IRecordProcessorFactory {
	

    private Class<T> recordType;
    private DBWriter<T> dbWriter;


    /**
     * Creates a new factory that uses the default configuration values for each
     * processor it creates.
     *
     * @see #CountingRecordProcessorFactory(Class, CountPersister, int, int, CountingRecordProcessorConfig)
     */

    public MeasurementRecordProcessorFactory(Class<T> recordType, DBWriter<T> dbWriter) {
        if (recordType == null) {
            throw new NullPointerException("recordType must not be null");
        }
        if (dbWriter == null) {
            throw new NullPointerException("dbWriter must not be null");
        }
        this.recordType = recordType;
        this.dbWriter = dbWriter;

    }
    
    @Override
    public IRecordProcessor createProcessor() {
        return new MeasurementRecordProcessor<>(recordType, dbWriter);
    }
}
