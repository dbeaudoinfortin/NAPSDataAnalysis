package com.dbf.naps.data.download;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NAPSCatalogue {
	
	@SerializedName("path_catalogue_id") 
    private String pathCatalogueId;
	
	@SerializedName("path_contents") 
    private List<PathContent> pathContents;
	
	@SerializedName("path_parent") 
    private String pathParent;

    // Getters and Setters
    public String getPathCatalogueId() {
        return pathCatalogueId;
    }

    public void setPathCatalogueId(String pathCatalogueId) {
        this.pathCatalogueId = pathCatalogueId;
    }

    public List<PathContent> getPathContents() {
        return pathContents;
    }

    public void setPathContents(List<PathContent> pathContents) {
        this.pathContents = pathContents;
    }

    public String getPathParent() {
        return pathParent;
    }

    public void setPathParent(String pathParent) {
        this.pathParent = pathParent;
    }

    public static class PathContent {
    	
        private String name;
        private String path;
        
        @SerializedName("is_directory") 
        private boolean isDirectory;
        
        @SerializedName("last_modified") 
        private String lastModified;
        
        @SerializedName("content_length") 
        private String contentLength;
        

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isDirectory() {
            return isDirectory;
        }

        public void setDirectory(boolean isDirectory) {
            this.isDirectory = isDirectory;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }

        public String getContentLength() {
            return contentLength;
        }

        public void setContentLength(String contentLength) {
            this.contentLength = contentLength;
        }
    }
}