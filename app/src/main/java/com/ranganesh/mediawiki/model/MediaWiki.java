package com.ranganesh.mediawiki.model;

public class MediaWiki {
    private String title, thumbnailUrl, description;

    public MediaWiki() {
    }

    public MediaWiki(String title, String thumbnailUrl, String description) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
