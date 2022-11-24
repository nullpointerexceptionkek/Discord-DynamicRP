/*
 *
 * MIT License
 *
 * Copyright (c) 2022 lee
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

package lee.aspect.dev.discordrpc;


import java.util.ArrayList;
import java.util.Arrays;

public class Script {
    public enum TimeStampMode {

        appLaunch,
        cdFromDayEnd,
        localTime,
        none,

        sinceUpdate,

        custom;

    }

    private static long customTimestamp;
    private static ArrayList<Updates> totalupdates;

    private static TimeStampMode timestampmode = TimeStampMode.appLaunch;

    public Script() {
        totalupdates = new ArrayList<>();
    }

    public static TimeStampMode getTimestampmode() {
        return timestampmode;
    }

    public static void setTimestampmode(TimeStampMode timestampmode) {
        Script.timestampmode = timestampmode;
    }

    public static ArrayList<Updates> getTotalupdates() {
        return totalupdates;
    }


    public static void setTotalupdates(ArrayList<Updates> u) {
        totalupdates = u;
    }

    public static void addUpdates(Updates... updates) {
        totalupdates.addAll(Arrays.asList(updates));

    }
    public static void addUpdates(int index,Updates... updates) {
        totalupdates.addAll(index,Arrays.asList(updates));

    }

    public static void setUpdates(int index,Updates u) {
        totalupdates.set(index, u);
    }

    public static Script fromTotalUpdates() {
        return new Script();
    }

    public void removeUpdates(int list) {
        totalupdates.remove(list);

    }

    public Updates getUpdates(int list) {
        return totalupdates.get(list);
    }

    public int getSize() {
        return totalupdates.size();

    }

    public static void setCustomTimestamp(long customTimestamp) {
        Script.customTimestamp = customTimestamp;
    }

    public static long getCustomTimestamp() {
        return customTimestamp;
    }

    @Override
    public String toString() {

        StringBuilder ts = new StringBuilder();

        for (Updates u : totalupdates) {
            ts.append(u).append("-");
        }

        return ts.toString();

    }

}
