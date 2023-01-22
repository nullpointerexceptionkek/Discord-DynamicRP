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

import lee.aspect.dev.Launch;
import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordipc.IPCClient;
import lee.aspect.dev.discordipc.IPCListener;
import lee.aspect.dev.discordipc.entities.Callback;
import lee.aspect.dev.discordipc.entities.RichPresence;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;

import java.util.Calendar;

import static java.lang.Thread.currentThread;


public class DiscordRP {

    public static final boolean autoReconnect = true;
    public static IPCClient client;
    public static Callback callback;
    private long created = -1;
    private long current;

    public void LaunchReadyCallBack(Updates updates) throws NoDiscordClientException {
        Calendar calendar = Calendar.getInstance();
        switch (Script.getScript().getTimestampmode()) {
            case appLaunch:
                this.created = (long) Math.ceil(System.currentTimeMillis());
                break;
            case none:
                created = -1;
                break;

            case localTime:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                this.created = (long) Math.ceil(calendar.getTimeInMillis());
                break;

            case cdFromDayEnd:
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                this.created = (long) Math.ceil(calendar.getTimeInMillis());
                break;
            case sinceUpdate:
            case custom:
                created = 0;
                break;
        }
        current = (long) Math.ceil(System.currentTimeMillis());

        /*
         * official discord ipc doc
         * https://discord.com/developers/docs/topics/rpc#rpc
         */

        callback = new Callback();
        setIPCClient(updates);

        client.connect();
    }


    public void shutdown() {
        client.close();
    }


    public void update(Updates updates) {
        try {
            setBuilder(updates, client);
        } catch (IllegalStateException | NullPointerException e) {
            if (autoReconnect && RunLoopManager.isRunning) {
                setIPCClient(updates);
                try {
                    client.connect();
                } catch (NoDiscordClientException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                currentThread().interrupt();
                RunLoopManager.setRunLoop(null);
                RunLoopManager.isRunning = false;
            }
        }
    }

    private void setBuilder(Updates updates, IPCClient client) {
        RichPresence.Builder builder = new RichPresence.Builder();
        long currentTime = (long) Math.ceil(System.currentTimeMillis());
        builder.setState(updates.getSl())
                .setDetails(updates.getFl())
                .setLargeImage(updates.getImage(), updates.getImagetext())
                .setSmallImage(updates.getSmallimage(), updates.getSmalltext())
                .setButton1Text(updates.getButton1Text())
                .setButton1Url(updates.getButton1Url())
                .setButton2Text(updates.getButton2Text())
                .setButton2Url(updates.getButton2Url());
        if (!(created == -1)) {
            switch (Script.getScript().getTimestampmode()) {
                case custom:
                    Launch.LOGGER.debug("custom" + Script.getScript().getCustomTimestamp());
                    Launch.LOGGER.debug("current" + current);
                    if (Script.getScript().getCalculatedTimestamp() > current) {
                        builder.setEndTimestamp(Script.getScript().getCalculatedTimestamp());
                    } else {
                        builder.setStartTimestamp(Script.getScript().getCalculatedTimestamp());
                    }
                    break;
                case sinceUpdate:
                    builder.setStartTimestamp(currentTime);
                    break;
                default:
                    if (created > current) {
                        builder.setEndTimestamp(created);
                    } else {
                        builder.setStartTimestamp(created);
                    }
                    break;
            }
        }
        client.sendRichPresence(builder.build(), callback);
    }

    private void setIPCClient(Updates updates) {
        client = new IPCClient(Long.parseLong(Script.getScript().getDiscordAPIKey()));
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                setBuilder(updates, client);
            }
        });
    }


}

