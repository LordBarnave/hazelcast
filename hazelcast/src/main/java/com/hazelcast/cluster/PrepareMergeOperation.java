/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.cluster;

import com.hazelcast.instance.Node;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;

import java.io.IOException;
import java.util.logging.Level;

public class PrepareMergeOperation extends AbstractClusterOperation {

    private Address newTargetAddress = null;

    public PrepareMergeOperation() {
    }

    public PrepareMergeOperation(Address newTargetAddress) {
        super();
        this.newTargetAddress = newTargetAddress;
    }

    public void run() {
        final Address caller = getCallerAddress();
        final NodeEngineImpl nodeEngine = (NodeEngineImpl) getNodeEngine();
        final Node node = nodeEngine.getNode();
        final Address masterAddress = node.getMasterAddress();
        final ILogger logger = node.loggingService.getLogger(this.getClass().getName());
        if (caller != null && !caller.equals(masterAddress)) { // caller null means local invocation.
            logger.warning("Prepare-merge instruction sent from non-master endpoint: " + caller);
            return;
        }
        logger.warning("Preparing to merge... Waiting for merge instruction...");
        node.getClusterService().prepareToMerge(newTargetAddress);
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        newTargetAddress = new Address();
        newTargetAddress.readData(in);
    }

    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        newTargetAddress.writeData(out);
    }
}
