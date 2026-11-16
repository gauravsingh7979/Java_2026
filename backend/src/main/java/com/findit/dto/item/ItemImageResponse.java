package com.findit.dto.item;


public class ItemImageResponse {



    private Long id;
    private String fileName;
    private String originalFileName;
    private String url;
    private boolean primary;


    // Default constructor
    public ItemImageResponse() {}

    // All-args constructor
    public ItemImageResponse(Long id, String fileName, String originalFileName, String url, boolean primary) {
        this.id = id;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.url = url;
        this.primary = primary;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String fileName;
        private String originalFileName;
        private String url;
        private boolean primary;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder originalFileName(String originalFileName) { this.originalFileName = originalFileName; return this; }
        public Builder url(String url) { this.url = url; return this; }
        public Builder primary(boolean primary) { this.primary = primary; return this; }

        public ItemImageResponse build() {
            return new ItemImageResponse(id, fileName, originalFileName, url, primary);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }

}
