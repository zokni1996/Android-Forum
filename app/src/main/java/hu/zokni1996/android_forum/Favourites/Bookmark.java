package hu.zokni1996.android_forum.Favourites;

public class Bookmark {

    private int id;
    private String name;
    private String url;

    public Bookmark() {
    }

    public Bookmark(String title, String url) {
        super();
        this.name = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Bookmark [id=" + id + ", name=" + name + ", url=" + url + "]";
    }
}
