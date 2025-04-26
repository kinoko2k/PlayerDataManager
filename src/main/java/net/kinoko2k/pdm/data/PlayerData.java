package net.kinoko2k.pdm.data;

import java.util.UUID;
import java.time.LocalDateTime;

public class PlayerData {
    private int id;
    private String mcid;
    private UUID uuid;
    private LocalDateTime firstLoginDate;
    private long playtime;
    private String lastIpAddress;

    public PlayerData(String mcid, UUID uuid) {
        this.mcid = mcid;
        this.uuid = uuid;
        this.firstLoginDate = LocalDateTime.now();
        this.playtime = 0;
        this.lastIpAddress = "";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMcid() { return mcid; }
    public void setMcid(String mcid) { this.mcid = mcid; }
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public LocalDateTime getFirstLoginDate() { return firstLoginDate; }
    public void setFirstLoginDate(LocalDateTime firstLoginDate) { this.firstLoginDate = firstLoginDate; }
    public long getPlaytime() { return playtime; }
    public void setPlaytime(long playtime) { this.playtime = playtime; }

    public String getLastIpAddress() { 
        return lastIpAddress; 
    }
    
    public void setLastIpAddress(String lastIpAddress) { 
        if (lastIpAddress == null) {
            this.lastIpAddress = "";
        } else {
            this.lastIpAddress = lastIpAddress;
        }
    }
}