package com.ecs.numbasst.manager.callback;

public class MessageEvent {

    private String mac;
    private String eventType;
    private byte[] data;

    public MessageEvent(String mac,String eventType) {
        this.eventType = eventType;
        this.mac = mac;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public byte[] getData() {
        return data;
    }

    public String getMac() {
        return mac;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
