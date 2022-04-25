package com.personal.aibot;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.configuration.BotConfiguration;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;

public class Bootstrap {
    public static void main(String[] args) {
        Bot bot = new Bot(BotConfiguration.builder()
                .name("super")
                .path("src/main/resources")
                .build()
        );
        try (ZContext context = new ZContext()) {
            ZMQ.Socket requester = context.createSocket(SocketType.PUB);
            ZMQ.Socket listener = context.createSocket(SocketType.PULL);
            requester.bind("tcp://127.0.0.1:9991");
            listener.bind("tcp://127.0.0.1:9990");
            String topic = "chat";
            while (true) {
                String response = listener.recvStr();
                System.out.println("Sender Request :" + response);

                if (response.equals("exit") || response.equals("Exit") || response.equals("EXIT")) {
                    //requester.send(sendMessage);
                    break;
                }
                Chat chatSession = new Chat(bot);
                String reply = chatSession.multisentenceRespond(response);
                System.out.println("Our Reply: " + reply);
                String messageTosend = String.format(topic + reply);
                byte[] arr = messageTosend.getBytes(StandardCharsets.UTF_8);
                requester.send(arr);
            }
            requester.close();
            listener.close();
        }
    }
}
