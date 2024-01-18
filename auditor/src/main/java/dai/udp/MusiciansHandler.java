package dai.udp;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.HashMap;

import com.google.gson.Gson;

import java.net.DatagramPacket;
import static java.nio.charset.StandardCharsets.*;

public class MusiciansHandler implements Runnable {

    private static final HashMap<String, String> soundToMusicianMap = new HashMap<>() {
        {
            put("ti-ta-ti", "piano");
            put("pouet", "trumpet");
            put("trulu", "flute");
            put("gzi-gzi", "violin");
            put("boum-boum", "drum");
        }
    };

    private class UDPMusician extends Musician {
        public UDPMusician(String uuid, String instrumentSound, long timestamp) {
            super(uuid, soundToMusicianMap.get(instrumentSound), timestamp);
        }
    }

    private final String address;
    private final int port;
    private HashMap<String, Musician> musicians = new HashMap<>();

    public MusiciansHandler(String address, int port) {
        this.address = address;
        this.port = port;

    }

    @Override
    public void run() {

        try (MulticastSocket socket = new MulticastSocket(port)) {

            InetSocketAddress group_address = new InetSocketAddress(address, port);
            NetworkInterface netif = NetworkInterface.getByName("eth0");
            socket.joinGroup(group_address, netif);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);

                Musician musician = stringToMusician(message);

                if (musicians.containsKey(musician.getUuid())) {
                    musicians.get(musician.getUuid()).setLastActivity(System.currentTimeMillis());
                } else {
                    musicians.put(musician.getUuid(), musician);
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public IMusiciansView getMusiciansView() {
        return () -> new Gson().toJson(musicians);
    }

    private Musician stringToMusician(String json) {
        return new Gson().fromJson(json, UDPMusician.class);
    }

}
