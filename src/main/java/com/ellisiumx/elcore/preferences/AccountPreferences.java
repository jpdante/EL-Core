package com.ellisiumx.elcore.preferences;

public class AccountPreferences {

    private String language = "en-US";
    private boolean filterChat = true;
    private boolean hubGames = true;
    private boolean showPlayers = true;
    private boolean showChat = true;
    private boolean friendChat = true;
    private boolean privateMessaging = true;
    private boolean partyRequests = true;
    private boolean invisibility = false;
    private boolean hubForceField = false;
    private boolean showMacReports = false;
    private boolean ignoreVelocity = false;
    private boolean pendingFriendRequests = true;
    private boolean friendDisplayInventoryUI = true;
    private boolean updated = false;

    public AccountPreferences() {}

    public AccountPreferences(String lang, boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h, boolean i, boolean j, boolean k, boolean l, boolean m) {
        language = lang;
        filterChat = a;
        hubGames = b;
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

    public boolean getHubGames() {
        return hubGames;
    }

    public void setHubGames(boolean hubGames) {
        this.updated = true;
        this.hubGames = hubGames;
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
}
