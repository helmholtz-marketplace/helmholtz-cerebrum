package de.helmholtz.marketplace.cerebrum.entities;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Image {
    @Id
    @GeneratedValue
    private Long id;
    private String base64;
    private String url;

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return this.base64;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
