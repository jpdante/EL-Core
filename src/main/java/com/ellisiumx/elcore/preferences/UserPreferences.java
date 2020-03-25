package com.ellisiumx.elcore.preferences;

import com.ellisiumx.elcore.redis.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPreferences implements Data {

    @SerializedName("lang")
    private String language = "en-US";
    @SerializedName("a")
    private boolean filterChat = true;
    @SerializedName("b")
    private boolean autoLanguage = true;
    @SerializedName("c")
    private boolean showPlayers = true;
    @SerializedName("d")
    private boolean showChat = true;
    @SerializedName("e")
    private boolean friendChat = true;
    @SerializedName("f")
    private boolean privateMessaging = true;
    @SerializedName("g")
    private boolean partyRequests = true;
    @SerializedName("h")
    private boolean invisibility = false;
    @SerializedName("i")
    private boolean hubForceField = false;
    @SerializedName("j")
    private boolean showMacReports = false;
    @SerializedName("k")
    private boolean ignoreVelocity = false;
    @SerializedName("l")
    private boolean pendingFriendRequests = true;
    @SerializedName("m")
    private boolean friendDisplayInventoryUI = true;
    @SerializedName("n")
    @Expose(serialize = false)
    private boolean updated = false;
    @Expose(serialize = false)
    private String uuid = "";
    @Expose(serialize = false)
    private int accountId = -1;

    public UserPreferences() { }

    public UserPreferences(String lang, boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h, boolean i, boolean j, boolean k, boolean l, boolean m) {
        language = lang;
        filterChat = a;
        autoLanguage = b;
        showPlayers = c;
        showChat = d;
        friendChat = e;
        privateMessaging = f;
        partyRequests = g;
        invisibility = h;
        hubForceField = i;
        showMacReports = j;
        ignoreVelocity = k;
        pendingFriendRequests = l;
        friendDisplayInventoryUI = m;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.updated = true;
        this.language = language;
    }

    public boolean getFilterChat() {
        return filterChat;
    }

    public void setFilterChat(boolean filterChat) {
        this.updated = true;
        this.filterChat = filterChat;
    }

    public boolean getAutoLanguage() {
        return autoLanguage;
    }

    public void setHubGames(boolean hubGames) {
        this.updated = true;
        this.autoLanguage = hubGames;
    }

    public boolean getShowPlayers() {
        return showPlayers;
    }

    public void setShowPlayers(boolean showPlayers) {
        this.updated = true;
        this.showPlayers = showPlayers;
    }

    public boolean getShowChat() {
        return showChat;
    }

    public void setShowChat(boolean showChat) {
        this.updated = true;
        this.showChat = showChat;
    }

    public boolean getFriendChat() {
        return friendChat;
    }

    public void setFriendChat(boolean friendChat) {
        this.updated = true;
        this.friendChat = friendChat;
    }

    public boolean getPrivateMessaging() {
        return privateMessaging;
    }

    public void setPrivateMessaging(boolean privateMessaging) {
        this.updated = true;
        this.privateMessaging = privateMessaging;
    }

    public boolean getPartyRequests() {
        return partyRequests;
    }

    public void setPartyRequests(boolean partyRequests) {
        this.updated = true;
        this.partyRequests = partyRequests;
    }

    public boolean getInvisibility() {
        return invisibility;
    }

    public void setInvisibility(boolean invisibility) {
        this.updated = true;
        this.invisibility = invisibility;
    }

    public boolean getHubForceField() {
        return hubForceField;
    }

    public void setHubForceField(boolean hubForceField) {
        this.updated = true;
        this.hubForceField = hubForceField;
    }

    public boolean getShowMacReports() {
        return showMacReports;
    }

    public void setShowMacReports(boolean showMacReports) {
        this.updated = true;
        this.showMacReports = showMacReports;
    }

    public boolean getIgnoreVelocity() {
        return ignoreVelocity;
    }

    public void setIgnoreVelocity(boolean ignoreVelocity) {
        this.updated = true;
        this.ignoreVelocity = ignoreVelocity;
    }

    public boolean getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public void setPendingFriendRequests(boolean pendingFriendRequests) {
        this.updated = true;
        this.pendingFriendRequests = pendingFriendRequests;
    }

    public boolean getFriendDisplayInventoryUI() {
        return friendDisplayInventoryUI;
    }

    public void setFriendDisplayInventoryUI(boolean friendDisplayInventoryUI) {
        this.updated = true;
        this.friendDisplayInventoryUI = friendDisplayInventoryUI;
    }

    public boolean wasUpdated() {
        return this.updated;
    }

    public void resetUpdateTrigger() {
        this.updated = false;
    }

    protected void setUUID(String uuid) {
        this.uuid = uuid;
    }

    protected void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return this.accountId;
    }

    public String getUUID() {
        return this.uuid;
    }

    @Override
    public String getDataId() {
        return uuid;
    }
}
