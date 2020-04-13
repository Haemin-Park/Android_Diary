package phm.example.project_chat;

public class Rooms {
    private String roomUserList;
    private String myusernm;
    private String yourusernm;


    public Rooms(String roomUserList, String myusernm, String yourusernm) {

        this.roomUserList =roomUserList;
        this.myusernm = myusernm;
        this.yourusernm = yourusernm;
    }

    public Rooms(){

    }
    public String getRoomUserList() {
        return roomUserList;
    }

    public void setRoomUserList(String roomUserList) {
        this.roomUserList = roomUserList;
    }

    public String getMyusernm() {
        return myusernm;
    }

    public void setMyusernm(String myusernm) {
        this.myusernm = myusernm;
    }

    public String getYourusernm () {
        return yourusernm ;
    }

    public void setYourusernm (String yourusernm ) { this.yourusernm  = yourusernm ;  }
}
