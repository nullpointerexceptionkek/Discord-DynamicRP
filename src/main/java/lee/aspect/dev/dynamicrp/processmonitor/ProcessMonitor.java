/*
 * 2022-
 * MIT License
 *
 * Copyright (c) 2023 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.dynamicrp.processmonitor;


import lee.aspect.dev.dynamicrp.system.SystemHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * monitors a process with a given name and reports its open/close status to a listener.
 */
public class ProcessMonitor {
    private final ScheduledExecutorService scheduler;
    private final String processName;
    private final OpenCloseListener listener;
    private final long waitDuration;
    private final TimeUnit waitDurationUnit;
    private boolean isProcessOpen;
    private boolean processCloseCalled = true;
    private ScheduledFuture<?> scheduledFuture;

    public ProcessMonitor(ScheduledExecutorService scheduler, String processName, OpenCloseListener listener, long waitDuration, TimeUnit waitDurationUnit) {
        this.scheduler = scheduler;
        this.processName = processName;
        this.listener = listener;
        this.waitDuration = waitDuration;
        this.waitDurationUnit = waitDurationUnit;
        this.isProcessOpen = isProcessOpen(processName);
        if (isProcessOpen) {
            listener.onProcessOpen();
        }
    }

    public void startMonitoring() {
        Runnable monitorTask = () -> {
            boolean newIsProcessOpen = isProcessOpen(processName);
            if (newIsProcessOpen != isProcessOpen) {
                isProcessOpen = newIsProcessOpen;
                if (isProcessOpen) {
                    processCloseCalled = false;
                    listener.onProcessOpen();
                }
            } else if (!newIsProcessOpen) {
                if (!processCloseCalled) {
                    listener.onProcessClose();
                    processCloseCalled = true;
                }
            }
        };
        this.scheduledFuture = scheduler.scheduleAtFixedRate(monitorTask, 0, waitDuration, waitDurationUnit);
    }

    public void stopMonitoring() {
        if(scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }
    public static boolean isProcessOpen(String processName) {
        if (SystemHandler.isOnWindows()) {
            try {
                Process process = Runtime.getRuntime().exec("tasklist");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(processName)) {
                        reader.close();
                        return true;
                    }
                }
                reader.close();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else if (SystemHandler.isOnMac() || SystemHandler.isOnLinux()) {
            try {
                Process process = Runtime.getRuntime().exec("pgrep " + processName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                if(reader.readLine() != null) {
                    reader.close();
                    return true;
                }
                reader.close();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        throw new UnsupportedOperationException("This method is not supported on this operating system.");
    }
}

