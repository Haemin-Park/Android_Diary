package phm.example.project_Diary;

public class Diary {
    private String id;
    private String displayname;
    private String imageURL;
    private String title;
    private String timestamp;
    private String mainText;

    public Diary(String id, String timestamp, String displayname, String imageURL) {
        this.id = id;
        this.displayname = displayname;
        this.imageURL = imageURL;
        this.timestamp= timestamp;
        this.title = title;
        this.mainText = mainText;
    }
    public Diary(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

}
