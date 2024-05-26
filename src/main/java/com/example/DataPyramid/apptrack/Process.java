package com.example.DataPyramid.apptrack;

//TODO: See if this class is actually needed? Doesn't appear to be implemented anywhere - Rachael
public class Process {
    private String name;
    private int pid;
    private String sessionName;
    private String sessionId;
    private String memUsage;

    public Process(String name, int pid, String sessionName, String sessionId, String memUsage) {
        this.name = name;
        this.pid = pid;
        this.sessionName = sessionName;
        this.sessionId = sessionId;
        this.memUsage = memUsage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(String memUsage) {
        this.memUsage = memUsage;
    }
}
