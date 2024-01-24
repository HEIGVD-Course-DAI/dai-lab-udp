package dai.udp;

public class Musician {

    private String uuid;
    private String instrument;
    private long lastActivity;

    public Musician(String uuid, String instrument, long lastActivity) {
        this.uuid = uuid;
        this.instrument = instrument;
        this.lastActivity = lastActivity;
    }

    public String getUuid() {
        return uuid;
    }

    public String getInstrument() {
        return instrument;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

}
