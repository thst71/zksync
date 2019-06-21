package net.beanfactory.zksync;

import org.apache.zookeeper.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Master implements Closeable, Watcher {

    private final ZkConfig zkConfig;
    private ZooKeeper zookeeper;

    Master(ZkConfig zkConfig) {
        this.zkConfig = zkConfig;
    }

    boolean apply() throws IOException {
        this.zookeeper = new ZooKeeper(zkConfig.getServers(), zkConfig.getTimeout(), this);

        int retries = zkConfig.getRetries() + 1;
        while (retries > 0) {
            try {
                zookeeper.create(zkConfig.getContext(), zkConfig.getPayload().getBytes(StandardCharsets.UTF_8), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                return true;
            } catch (KeeperException.ConnectionLossException e) {
                // Should check if we received the master's key....
            } catch (KeeperException.NodeExistsException e) {
                if (zkConfig.isBlocking()) {
                    try {
                        Thread.sleep(1000); // this should be controlled by watch...
                        System.err.printf("retries left: %02d\n", retries - 1);
                        retries--;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    retries = 0;
                }
            } catch (KeeperException.SessionExpiredException e) {
                try {
                    zookeeper.close();
                } catch (InterruptedException ex) {
                    Thread.interrupted();
                }
                zookeeper = new ZooKeeper(zkConfig.getServers(), zkConfig.getTimeout(), this);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.err.println();
        return false;
    }

    @Override
    public void close() {
        if (this.zookeeper != null) {
            try {
                this.zookeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.err.println(event);
    }
}