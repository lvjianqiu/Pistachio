/*
 * Copyright 2014 Yahoo! Inc. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */

package com.yahoo.ads.pb.customization;

public class LookupCallbackRegistry extends CustomizationRegistry<LookupCallback> {
    static public String lookupCallbackPath = "/pistachio_zk/lookupcallback_registry/info";
    private static LookupCallbackRegistry instance = null;

    protected String getZKPath() {
        return lookupCallbackPath;
    }

    public static LookupCallbackRegistry getInstance() {
        if (instance == null) {
            synchronized (lookupCallbackPath) {
                if (instance == null) {
                    instance = new LookupCallbackRegistry();
                    instance.init();
                    if (instance.processor == null)
                        instance.processor = new DefaultLookupCallback();
                }
            }
        }
        return instance;
    }

    public LookupCallback getLookupCallback() {
        return processor;
    }

    static class DefaultLookupCallback implements LookupCallback {

        @Override
        public byte[] onLookup(byte[] key, byte[] currentValue) {
            return currentValue;
        }
    }
}
