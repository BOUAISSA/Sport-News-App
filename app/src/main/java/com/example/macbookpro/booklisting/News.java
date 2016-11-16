package com.example.macbookpro.booklisting;

import java.net.URL;

/**
 * Created by macbookpro on 6/28/16.
 */
public class News {
    private String mtitle;
    private String mdate;
    private URL image;
    private String forBrowser;
    public News(String mtitle, String mdate, URL image, String forBrowser) {
        this.mtitle = mtitle;
        this.mdate = mdate;
        this.image = image;
        this.forBrowser = forBrowser;
    }
    public void setForBrowser(String forBrowser) {
        this.forBrowser = forBrowser;
    }
    public String getForBrowser() {
        return forBrowser;
    }
    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }
    public void setMdate(String mdate) {
        this.mdate = mdate;
    }
    public void setImage(URL image) {
        this.image = image;
    }
    public String getMtitle() {
        return mtitle;}
    public String getMdate() {
        return mdate;
    }
    public URL getImage() {
        return image;}
}