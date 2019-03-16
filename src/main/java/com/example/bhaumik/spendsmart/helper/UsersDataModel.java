package com.example.bhaumik.spendsmart.helper;

/**
 * Created by Shailesh
 */

public class UsersDataModel {
    private String userId;
    private String friendId;
    private String createdDt;
    private String updatedDt;

    private String pic;
    private String frndName;


    public UsersDataModel()
    {

    }

    public UsersDataModel( String uid,String frndId,String name,String createdDt,String updatedDt)
    {
        this.userId=uid;
        this.friendId=frndId;
        this.createdDt=createdDt;
        this.frndName=name;
        this.updatedDt=updatedDt;

    }

    public String getPic() {
        return pic;
    }

    public String getFrndName() {
        return frndName;
    }





    public String getUserId() {
        return userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getCreatedDt() {
        return createdDt;
    }

    public String getUpdatedDt() {
        return updatedDt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setFrndName(String frndName) {
        this.frndName = frndName;
    }
}
