package com.example.DataPyramid.apptrack;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ActiveWindowDetector {
    private static final User32 user32 = User32.INSTANCE;

    public static String getActiveWindowTitle() {
        char[] buffer = new char[1024];
        HWND hwnd = user32.GetForegroundWindow();
        user32.GetWindowText(hwnd, buffer, 1024);
        return Native.toString(buffer);
    }

    public static String getActiveProcessName() {
        HWND hwnd = user32.GetForegroundWindow();
        IntByReference pid = new IntByReference();
        user32.GetWindowThreadProcessId(hwnd, pid);
        return getProcessName(pid.getValue());
    }

    private static String getProcessName(int pid) {
        Map<Integer, String> processes = listProcesses();
        return processes.get(pid);
    }

    private static Map<Integer, String> listProcesses() {
        Map<Integer, String> processes = new HashMap<>();
        try {
            java.lang.Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\",\"");
                    if (parts.length >= 2) {
                        int pid = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                        String processName = parts[0].replaceAll("\"", "").trim();
                        processes.put(pid, processName);
                    }
                }
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return processes;
    }
}
