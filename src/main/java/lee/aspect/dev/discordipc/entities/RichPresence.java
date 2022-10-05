/*
 * Copyright 2017 John Grosh (john.a.grosh@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lee.aspect.dev.discordipc.entities;

import java.time.OffsetDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An encapsulation of all data needed to properly construct a JSON RichPresence payload.
 *
 * <p>These can be built using {@link Builder}.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class RichPresence {
    private final String state;
    private final String details;
    private final long startTimestamp;
    private final long endTimestamp;
    private final String largeImageKey;
    private final String largeImageText;
    private final String smallImageKey;
    private final String smallImageText;
    private final String partyId;
    private final int partySize;
    private final int partyMax;
    private final String matchSecret;
    private final String joinSecret;
    private final String spectateSecret;
    private final boolean instance;

    private final String button1Text;
    private final String button1Url;

    private final String button2Text;
    private final String button2Url;
    public RichPresence(String state, String details, long startTimestamp, long endTimestamp,
                        String largeImageKey, String largeImageText, String smallImageKey, String smallImageText,
                        String partyId, int partySize, int partyMax, String matchSecret, String joinSecret,
                        String spectateSecret, boolean instance, String button1Text, String button1Url, String button2Text, String button2Url) {
        this.state = state;
        this.details = details;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.largeImageKey = largeImageKey;
        this.largeImageText = largeImageText;
        this.smallImageKey = smallImageKey;
        this.smallImageText = smallImageText;
        this.partyId = partyId;
        this.partySize = partySize;
        this.partyMax = partyMax;
        this.matchSecret = matchSecret;
        this.joinSecret = joinSecret;
        this.spectateSecret = spectateSecret;
        this.instance = instance;
        this.button1Text = button1Text;
        this.button1Url = button1Url;
        this.button2Text = button2Text;
        this.button2Url = button2Url;
    }

    /**
     * Constructs a {@link JSONObject} representing a payload to send to discord
     * to update a user's Rich Presence.
     *
     * <p>This is purely internal, and should not ever need to be called outside of
     * the library.
     *
     * @return A JSONObject payload for updating a user's Rich Presence.
     */
    public JSONObject toJson() {
        /*
            return new JSONObject()
                    .put("state", state)
                    .put("details", details)
                    .put("timestamps", new JSONObject()
                            .put("start", startTimestamp == null ? null : startTimestamp.toEpochSecond())
                            .put("end", endTimestamp == null ? null : endTimestamp.toEpochSecond()))
                    .put("assets", new JSONObject()
                            .put("large_image", largeImageKey)
                            .put("large_text", largeImageText)
                            .put("small_image", smallImageKey)
                            .put("small_text", smallImageText))
                    .put("party", partyId == null ? null : new JSONObject()
                            .put("id", partyId)
                            .put("size", new JSONArray().put(partySize).put(partyMax)))
                    .put("instance", instance)

                    .put("buttons", new JSONArray()
                            .put(new JSONObject()
                                    .put("label", button1Text)
                                    .put("url", button1Url))
                            .put(new JSONObject()
                                    .put("label", button2Text)
                                    .put("url", button2Url)));

         */
        JSONObject jsonObject = new JSONObject()
                .put("state", state)
                .put("details", details)
                .put("timestamps", new JSONObject()
                        .put("start", startTimestamp < 100 ? null : startTimestamp)
                        .put("end", endTimestamp <100 ? null : endTimestamp))
                .put("assets", new JSONObject()
                        .put("large_image", largeImageKey)
                        .put("large_text", largeImageText)
                        .put("small_image", smallImageKey)
                        .put("small_text", smallImageText))
                .put("party", partyId == null ? null : new JSONObject()
                        .put("id", partyId)
                        .put("size", new JSONArray().put(partySize).put(partyMax)))
                .put("instance", instance);
        if(button1Text !=null) {
            JSONArray buttons = new JSONArray()
                    .put(new JSONObject()
                    .put("label", button1Text)
                    .put("url", button1Url));

            if(button2Text != null) {
                buttons.put(new JSONObject()
                        .put("label", button2Text)
                        .put("url", button2Url));
            }
            jsonObject.put("buttons",buttons);
        }

        return jsonObject;
    }

    /**
     * A chain builder for a {@link RichPresence} object.
     *
     * <p>An accurate description of each field and it's functions can be found
     * <a href="https://discordapp.com/developers/docs/rich-presence/how-to#updating-presence-update-presence-payload-fields">here</a>
     */
    public static class Builder {
        private String state;
        private String details;
        private long startTimestamp;
        private long endTimestamp;
        private String largeImageKey;
        private String largeImageText;
        private String smallImageKey;
        private String smallImageText;
        private String partyId;
        private int partySize;
        private int partyMax;
        private String matchSecret;
        private String joinSecret;
        private String spectateSecret;
        private boolean instance;

        private String button1Text;
        private String button1Url;

        private String button2Text;
        private String button2Url;

        /**
         * Builds the {@link RichPresence} from the current state of this builder.
         *
         * @return The RichPresence built.
         */
        public RichPresence build() {
            return new RichPresence(state, details, startTimestamp, endTimestamp,
                    largeImageKey, largeImageText, smallImageKey, smallImageText,
                    partyId, partySize, partyMax, matchSecret, joinSecret,
                    spectateSecret, instance, button1Text, button1Url, button2Text, button2Url);
        }

        /**
         * Sets the state of the user's current party.
         *
         * @param state The state of the user's current party.
         * @return This Builder.
         */
        public Builder setState(String state) {
            this.state = state;
            return this;
        }

        /**
         * Sets the text of the first button.
         *
         * @param buttonText The Text that is displayed on the button
         * @return This Builder.
         */
        public Builder setButton1Text(String buttonText) {
            this.button1Text = buttonText;
            return this;
        }

        /**
         * Sets the url of the button.
         *
         * @param buttonUrl The url of the button
         * @return This Builder.
         */
        public Builder setButton1Url(String buttonUrl) {
            this.button1Url = buttonUrl;
            return this;
        }

        /**
         * Sets the text of the first button.
         *
         * @param buttonText The Text that is displayed on the button
         * @return This Builder.
         */
        public Builder setButton2Text(String buttonText) {
            this.button2Text = buttonText;
            return this;
        }

        /**
         * Sets the url of the button.
         *
         * @param buttonUrl The url of the button
         * @return This Builder.
         */
        public Builder setButton2Url(String buttonUrl) {
            this.button2Url = buttonUrl;
            return this;
        }

        /**
         * Sets details of what the player is currently doing.
         *
         * @param details The details of what the player is currently doing.
         * @return This Builder.
         */
        public Builder setDetails(String details) {
            this.details = details;
            return this;
        }

        /**
         * Sets the time that the player started a match or activity.
         *
         * @param startTimestamp The time the player started a match or activity.
         * @return This Builder.
         */
        public Builder setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        /**
         * Sets the time that the player's current activity will end.
         *
         * @param endTimestamp The time the player's activity will end.
         * @return This Builder.
         */
        public Builder setEndTimestamp(long endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        /**
         * Sets the key of the uploaded image for the large profile artwork, as well as
         * the text tooltip shown when a cursor hovers over it.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param largeImageKey  A key to an image to display.
         * @param largeImageText Text displayed when a cursor hovers over the large image.
         * @return This Builder.
         */
        public Builder setLargeImage(String largeImageKey, String largeImageText) {
            this.largeImageKey = largeImageKey;
            this.largeImageText = largeImageText;
            return this;
        }

        /**
         * Sets the key of the uploaded image for the large profile artwork.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param largeImageKey A key to an image to display.
         * @return This Builder.
         */
        public Builder setLargeImage(String largeImageKey) {
            return setLargeImage(largeImageKey, null);
        }

        /**
         * Sets the key of the uploaded image for the small profile artwork, as well as
         * the text tooltip shown when a cursor hovers over it.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param smallImageKey  A key to an image to display.
         * @param smallImageText Text displayed when a cursor hovers over the small image.
         * @return This Builder.
         */
        public Builder setSmallImage(String smallImageKey, String smallImageText) {
            this.smallImageKey = smallImageKey;
            this.smallImageText = smallImageText;
            return this;
        }

        /**
         * Sets the key of the uploaded image for the small profile artwork.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param smallImageKey A key to an image to display.
         * @return This Builder.
         */
        public Builder setSmallImage(String smallImageKey) {
            return setSmallImage(smallImageKey, null);
        }

        /**
         * Sets party configurations for a team, lobby, or other form of group.
         *
         * <p>The {@code partyId} is ID of the player's party.
         * <br>The {@code partySize} is the current size of the player's party.
         * <br>The {@code partyMax} is the maximum number of player's allowed in the party.
         *
         * @param partyId   The ID of the player's party.
         * @param partySize The current size of the player's party.
         * @param partyMax  The maximum number of player's allowed in the party.
         * @return This Builder.
         */
        public Builder setParty(String partyId, int partySize, int partyMax) {
            this.partyId = partyId;
            this.partySize = partySize;
            this.partyMax = partyMax;
            return this;
        }

        /**
         * Sets the unique hashed string for Spectate and Join.
         *
         * @param matchSecret The unique hashed string for Spectate and Join.
         * @return This Builder.
         */
        public Builder setMatchSecret(String matchSecret) {
            this.matchSecret = matchSecret;
            return this;
        }

        /**
         * Sets the unique hashed string for chat invitations and Ask to Join.
         *
         * @param joinSecret The unique hashed string for chat invitations and Ask to Join.
         * @return This Builder.
         */
        public Builder setJoinSecret(String joinSecret) {
            this.joinSecret = joinSecret;
            return this;
        }

        /**
         * Sets the unique hashed string for Spectate button.
         *
         * @param spectateSecret The unique hashed string for Spectate button.
         * @return This Builder.
         */
        public Builder setSpectateSecret(String spectateSecret) {
            this.spectateSecret = spectateSecret;
            return this;
        }

        /**
         * Marks the {@link #setMatchSecret(String) matchSecret} as a game
         * session with a specific beginning and end.
         *
         * @param instance Whether or not the {@code matchSecret} is a game
         *                 with a specific beginning and end.
         * @return This Builder.
         */
        public Builder setInstance(boolean instance) {
            this.instance = instance;
            return this;
        }
    }
}
