package com.sdu.spark.scheduler;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

/**
 * @author hanhan.zhang
 * */
public abstract class TaskLocation implements Serializable {

    private static final String executorLocationTag = "executor_";

    private static final String inMemoryLocationTag = "hdfs_cache_";

    public abstract String host();

    private static class ExecutorCacheTaskLocation extends TaskLocation {
        private String host;
        public String executorId;

        private ExecutorCacheTaskLocation(String host, String executorId) {
            this.host = host;
            this.executorId = executorId;
        }

        @Override
        public String host() {
            return host;
        }

        @Override
        public String toString() {
            return format("%s%s_%s", executorLocationTag, host, executorId);
        }
    }

    private static class HostTaskLocation extends TaskLocation {
        private String host;

        private HostTaskLocation(String host) {
            this.host = host;
        }

        @Override
        public String host() {
            return host;
        }

        @Override
        public String toString() {
            return host;
        }
    }

    private static class HDFSCacheTaskLocation extends TaskLocation {
        private String host;

        private HDFSCacheTaskLocation(String host) {
            this.host = host;
        }

        @Override
        public String host() {
            return host;
        }

        @Override
        public String toString() {
            return format("%s%s", inMemoryLocationTag, host);
        }
    }

    public static TaskLocation apply(String host, String executorId) {
        return new ExecutorCacheTaskLocation(host, executorId);
    }

    /**
     * Create a TaskLocation from a string returned by getPreferredLocations.
     * These strings have the form executor_[hostname]_[executorid], [hostname], or
     * hdfs_cache_[hostname], depending on whether the location is cached.
     * */
    public static TaskLocation apply(String str) {
        assert str != null && str.length() > 0 : "Illegal task location !!";
        if (str.startsWith(inMemoryLocationTag)) {
            String[] fields = str.split("_");
            checkArgument(fields.length == 3, format("Illegal hdfs location format: %s", str));
            return new HDFSCacheTaskLocation(fields[2]);
        } else if (str.startsWith(executorLocationTag)) {
            String[] fields = str.split("_");
            checkArgument(fields.length == 3, format("Illegal executor location format: %s", str));
            return new ExecutorCacheTaskLocation(fields[1], fields[2]);
        } else {
            return new HostTaskLocation(str);
        }
    }
}
