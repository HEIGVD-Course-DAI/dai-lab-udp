package dai;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import com.google.gson.Gson;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.*;

public class Musician {
    final static String IPADDR = "239.255.22.5";
    final static int PORT = 9904;

    final static HashMap<String, String> instrumentSounds;
    static {
        HashMap<String, String> temp = new HashMap<>();
        temp.put("piano", "ti-ta-ti");
        temp.put("trumpet", "pouet");
        temp.put("flute", "trulu");
        temp.put("violin", "gzi-gzi");
        temp.put("drum", "boum-boum");
        instrumentSounds = temp;
    }

    public static void main(String[] args) {
        if (args.length != 1)
            return;

        String sound = instrumentSounds.get(args[0]);

        if (sound == null)
            return;

        String uuid = UUID.randomUUID().toString();
        Gson gson = new Gson();

        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                long currentTimeMillis = System.currentTimeMillis();

                Payload p = new Payload(uuid, sound, currentTimeMillis);

                String message = gson.toJson(p);
                byte[] payload = message.getBytes(UTF_8);
                var dest_address = new InetSocketAddress(IPADDR, PORT);
                var packet = new DatagramPacket(payload,
                        payload.length,
                        dest_address);
                socket.send(packet);

                Thread.sleep(1000);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

record Payload(String uuid, String sound, long time) {

}