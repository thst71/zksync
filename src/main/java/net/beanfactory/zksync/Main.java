package net.beanfactory.zksync;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ZkConfig config = new ZkConfigCmdlineParser().parseCommandline(args);

        int exitCode = -1;

        try (Master master = new Master(config)) {

            boolean isMaster = master.apply();

            if (isMaster) {
                System.err.println("I am MASTER now... (Enter to continue)");
                System.out.print(System.in.read());
//                Thread.sleep(5000000L);
            }

            exitCode = isMaster ? 0 : -1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(exitCode);
    }
}
