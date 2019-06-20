package net.beanfactory.zksync;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
class ZkConfig {
    private String servers;
    private String context;
    private String payload;
    private int timeout;
    private int retries;
    private boolean blocking;
}

class ZkConfigCmdlineParser {

    ZkConfig parseCommandline(String[] args) {

        String servers = "localhost:2181";
        String context = "/zksync";
        String payload;
        int timeout = 15000;
        int retries = 15;
        boolean blocking = true;

        StringBuilder payloadBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-server")) {
                servers = args[++i];
            } else if (args[i].equalsIgnoreCase("-context")) {
                context = args[++i];
            } else if (args[i].equalsIgnoreCase("-payload")) {
                payloadBuilder.append(args[++i]);
            } else if (args[i].equalsIgnoreCase("-timeout")) {
                timeout = Integer.parseInt(args[++i]);
            } else if (args[i].equalsIgnoreCase("-retries")) {
                retries = Integer.parseInt(args[++i]);
            } else if (args[i].equalsIgnoreCase("-nonblocking")) {
                blocking = false;
            } else {
                payloadBuilder.append(args[i]);
            }
        }
        payload = payloadBuilder.toString();

        if (payload.isBlank()) {
            payload = UUID.randomUUID().toString();
        }

        System.err.printf("Config used: \n" +
                        "\tServer:   %s\n" +
                        "\tContext:  %s\n" +
                        "\tPayload:  %s\n" +
                        "\tTimeout:  %d\n" +
                        "\tRetries:  %d\n" +
                        "\tBlocking: %s\n" +
                        "",
                servers, context, payload, timeout, retries, String.valueOf(blocking));

        return ZkConfig.builder()
                .servers(servers)
                .context(context)
                .payload(payload)
                .timeout(timeout)
                .retries(retries)
                .blocking(blocking)
                .build();
    }
}
