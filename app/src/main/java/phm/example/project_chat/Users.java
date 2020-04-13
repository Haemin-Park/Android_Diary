package phm.example.project_chat;

public class Users {
    private String id;
    private String displayname;
    private String imageURL;
    private String room;

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


    public String getRoom() {
        return room;
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
