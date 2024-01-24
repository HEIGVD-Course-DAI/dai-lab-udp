package dai.udp;

public class Main {
    public static void main(String[] args) {
        MusiciansHandler musiciansHandler = new MusiciansHandler("239.255.22.5", 9904);
        TCPServer tcpHandler = new TCPServer(2205, musiciansHandler.getMusiciansView());

        Thread t1 = new Thread(musiciansHandler);
        Thread t2 = new Thread(tcpHandler);
        t1.start();
        t2.start();
    }
}