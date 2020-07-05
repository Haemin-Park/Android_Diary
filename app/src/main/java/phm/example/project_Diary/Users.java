package phm.example.project_Diary;

public class Users {
    private String id;
    private String displayname;
    private String imageURL;

    public Users(String id, String displayname, String imageURL) {
        this.id = id;
        this.displayname = displayname;
        this.imageURL = imageURL;
    }
    public Users(){

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

    public void setDisplayame(String username) {
        this.displayname = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
