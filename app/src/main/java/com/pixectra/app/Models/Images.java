package com.pixectra.app.Models;

/**
 * Created by Compu1 on 09-Feb-18.
 */

public class Images {
    String url;
    String thumbnail;

    public Images() {
    }

    public Images(String url, String thumbnail) {
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public Images(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
