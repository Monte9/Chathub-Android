/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.csc780.chathub.model;

import java.util.Date;

public class ChatMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String nickname;
    private User user;

    public long getTimestamp() {
        return timestamp;
    }

    private long timestamp;
    public static final long NO_TIMESTAMP = -1;

    public String getImageUrl() {
        return imageUrl;
    }

    private String imageUrl;

    public ChatMessage() {
    }

    public ChatMessage(String text, User user) {
        this.text = text;
        this.timestamp = new Date().getTime();
        this.user = user;
    }

    public ChatMessage(String text, User user, String imageUrl) {
        this(text, user);
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
