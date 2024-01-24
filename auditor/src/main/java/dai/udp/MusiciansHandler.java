package dai.udp;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
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

    private record UDPMusician(String uuid, String sound, long time) {

    }

    private final String address;
    private final int port;
    private ArrayList<Musician> musicians = new ArrayList<>();

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

                Musician targetMusician = getMusician(musician);
                if (targetMusician != null) {
                    targetMusician.setLastActivity(System.currentTimeMillis());
                } else {
                    musicians.add(musician);
                }

            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public IMusiciansView getMusiciansView() {

        return () -> {
            purgeInactiveMusicians();
            return new Gson().toJson(musicians) + "\n";
        };
    }

    private void purgeInactiveMusicians() {
        musicians.removeIf(m -> System.currentTimeMillis() - m.getLastActivity() > 5000);
    }

    private Musician stringToMusician(String json) {

        UDPMusician udpMusician = new Gson().fromJson(json, UDPMusician.class);
        return new Musician(udpMusician.uuid(), soundToMusicianMap.get(udpMusician.sound()), udpMusician.time());
    }

    private Musician getMusician(Musician target) {
        for (Musician m : musicians) {
            if (m.getUuid().equals(target.getUuid())) {
                return m;
            }
        }
        return null;
    }
}
