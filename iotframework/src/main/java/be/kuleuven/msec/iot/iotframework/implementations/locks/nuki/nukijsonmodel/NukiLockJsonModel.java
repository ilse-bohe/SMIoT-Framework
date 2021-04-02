package be.kuleuven.msec.iot.iotframework.implementations.locks.nuki.nukijsonmodel;

public class NukiLockJsonModel {
    private int nukiId;
    private String name;
    private LockState lastKnownState;

    public int getNukiId() {
        return nukiId;
    }

    public void setNukiId(int nukiId) {
        this.nukiId = nukiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LockState getLastKnownState() {
        return lastKnownState;
    }

    public void setLastKnownState(LockState lastKnownState) {
        this.lastKnownState = lastKnownState;
    }
}


