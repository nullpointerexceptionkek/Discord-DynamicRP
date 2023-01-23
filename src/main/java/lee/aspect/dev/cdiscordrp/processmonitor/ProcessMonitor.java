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

package lee.aspect.dev.cdiscordrp.processmonitor;


import lee.aspect.dev.cdiscordrp.util.system.StartLaunch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * monitors a process with a given name and reports its open/close status to a listener.
 */
public class ProcessMonitor {
    private final Object lock = new Object();
    private boolean isProcessOpen;
    private boolean processCloseCalled = true;
    private Thread thread;

    /**
     * Constructs a new `ProcessMonitor` with the specified value for the `processCloseCalled` field.
     * if `processCloseCalled` is true, the `onProcessClose` method of the listener will not be called if
     * the program was closed before the `startMonitoring` method was called.
     * if `processCloseCalled` is false, the `onProcessClose` method of the listener will be called if
     * the program was closed before the `startMonitoring` method was called.
     *
     * @param processCloseCalled a boolean indicating whether the `onProcessClose` callback should be called
     *                           if the process is already closed when the `startMonitoring` method is called.
     */
    public ProcessMonitor(boolean processCloseCalled) {
        this.processCloseCalled = processCloseCalled;
    }

    /**
     * Constructs a new `ProcessMonitor` with the `processCloseCalled` field set to true.
     * the `onProcessClose` method of the listener will not be called if
     * the program was closed before the `startMonitoring` method was called.
     */
    public ProcessMonitor() {
    }

    /**
     * Determines whether a process with the given name is open.
     * The method will use isContains() to check if the process name match on Windows
     *
     * @param processName the name of the process to check
     * @return true if the process is open, false otherwise
     * @throws UnsupportedOperationException if the current operating system is not supported
     */
    public static boolean isProcessOpen(String processName) {
        if (StartLaunch.isOnWindows()) {
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
        } else if (StartLaunch.isOnMac() || StartLaunch.isOnLinux()) {
            try {
                //Process process = Runtime.getRuntime().exec("ps -e");
                Process process = Runtime.getRuntime().exec("pgrep " + processName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                if (reader.readLine() != null) {
                    reader.close();
                    return true;
                }
                /*
                String line;
                while ((line = reader.readLine()) != null) {

                    if (line.contains(processName)) {
                        return true;
                    }
                }

                 */
                reader.close();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        throw new UnsupportedOperationException("This method is not supported on this operating system.");
    }

    /**
     * Starts monitoring the process with the given name.
     *
     * @param processName      the name of the process to monitor
     * @param listener         the listener to be notified of open/close events
     * @param waitDuration     the duration to wait between checks for the process status
     * @param waitDurationUnit the unit of time for the waitDuration parameter
     */
    public void startMonitoring(String processName, OpenCloseListener listener, long waitDuration, TimeUnit waitDurationUnit) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        this.isProcessOpen = isProcessOpen(processName);
        if (isProcessOpen) {
            listener.onProcessOpen();
        }

        thread = new Thread(() -> {
            while (true) {
                synchronized (lock) {
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
                    try {
                        lock.wait(waitDurationUnit.toMillis(waitDuration));
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Stops monitoring the process.
     */
    public void stopMonitoring() {
        if (thread != null) {
            thread.interrupt();
        }
    }
}

