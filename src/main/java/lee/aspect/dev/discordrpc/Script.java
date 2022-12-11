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

    public enum UpdateType {
        Loop, Reverse, Stop, Random
    }

    public enum TimeStampMode {

        appLaunch,
        cdFromDayEnd,
        localTime,
        none,

        sinceUpdate,

        custom

    }

    private UpdateType updateType = UpdateType.Loop;

    private long customTimestamp;
    private ArrayList<Updates> totalupdates;

    private TimeStampMode timestampmode = TimeStampMode.appLaunch;

    public Script() {
        totalupdates = new ArrayList<>();
    }

    public TimeStampMode getTimestampmode() {
        return timestampmode;
    }

    public void setTimestampmode(TimeStampMode timestampmode) {
        this.timestampmode = timestampmode;
    }

    public ArrayList<Updates> getTotalupdates() {
        return totalupdates;
    }


    public void setTotalupdates(ArrayList<Updates> u) {
        totalupdates = u;
    }

    public void addUpdates(Updates... updates) {
        totalupdates.addAll(Arrays.asList(updates));

    }
    public void addUpdates(int index,Updates... updates) {
        totalupdates.addAll(index,Arrays.asList(updates));

    }

    public void setUpdates(int index,Updates u) {
        totalupdates.set(index, u);
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

    public void setCustomTimestamp(long customTimestamp) {
        this.customTimestamp = customTimestamp;
    }

    public long getCustomTimestamp() {
        return customTimestamp;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
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
