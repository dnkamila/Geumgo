package com.idn.ict.geumgo;

public class GridViewItem {
    private int logId;
    private String imageUrl;
    private String capuredAt;
    private String level;
    private String description;

    public GridViewItem() {
        super();
    }

    public String getCapuredAt() {
        return capuredAt;
    }

    public void setCapuredAt(String capuredAt) {
        this.capuredAt = capuredAt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }
}

