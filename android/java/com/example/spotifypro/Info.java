package com.example.spotifypro;

import java.util.Objects;

public class Info{
    private String IP;
    private int port;
    private int brokerKey;

    public Info(String IP, int port){
        this.IP=IP;
        this.port=port;
    }

    public String getIP(){
        return IP;
    }

    public int getPort(){
        return port;
    }

    public void setBrokerKey(int brokerKey){
        this.brokerKey=brokerKey;
    }

    public int getBrokerKey(){
        return brokerKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Info that = (Info) o;
        return port == that.port &&
                Objects.equals(IP, that.IP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IP, port);
    }
}
