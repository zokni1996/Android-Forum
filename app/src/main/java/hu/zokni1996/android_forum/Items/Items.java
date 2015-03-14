package hu.zokni1996.android_forum.Items;

public class Items {
    private String Name;
    private int iconID;

    public Items(String Name, int iconID) {
        super();
        this.Name = Name;
        this.iconID = iconID;

    }

    public String getName() {
        return Name;
    }

    public int getIconID() {
        return iconID;
    }

}