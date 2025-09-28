package com.zrlog.plugin.common.model;

public class PublicInfo {
    private String title;
    private String secondTitle;
    private String homeUrl;
    private String apiHomeUrl;
    private String apiAdminUrl;
    private String adminColorPrimary;
    private Boolean darkMode;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getAdminColorPrimary() {
        return adminColorPrimary;
    }

    public void setAdminColorPrimary(String adminColorPrimary) {
        this.adminColorPrimary = adminColorPrimary;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }

    public String getApiHomeUrl() {
        return apiHomeUrl;
    }

    public void setApiHomeUrl(String apiHomeUrl) {
        this.apiHomeUrl = apiHomeUrl;
    }

    public String getApiAdminUrl() {
        return apiAdminUrl;
    }

    public void setApiAdminUrl(String apiAdminUrl) {
        this.apiAdminUrl = apiAdminUrl;
    }
}
