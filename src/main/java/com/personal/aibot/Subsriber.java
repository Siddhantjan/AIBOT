package com.personal.aibot;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Subsriber {
    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket requester = context.createSocket(SocketType.PUSH);
            ZMQ.Socket listener = context.createSocket(SocketType.SUB);
            requester.connect("tcp://127.0.0.1:9990");
            listener.connect("tcp://127.0.0.1:9991");
            Scanner sc = new Scanner(System.in);
            String topic ="chat";
            String sendMessage ="";
            while(true){
                sendMessage=sc.nextLine();
                if(sendMessage.equals("exit")|| sendMessage.equals("Exit") || sendMessage.equals("EXIT")){
                    requester.send(sendMessage);
                    break;
                }
                requester.send(sendMessage);
                System.out.println("message sent successfully");
                System.out.println("Waiting for reply");
                byte[] byteMessage = topic.getBytes(StandardCharsets.UTF_8);
                listener.subscribe(byteMessage);
                String message = new String(listener.recv());
                String[] messageArray = message.split(topic);
                System.out.println("bot :"+messageArray[1]);
            }
            requester.close();
            listener.close();

        }
    }
}
