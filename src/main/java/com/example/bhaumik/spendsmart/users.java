package com.example.bhaumik.spendsmart;

public class users {

    private String contact;
    private String email;
    private String name;
    private String pic;
    private String uid;


    public users()
    {

    }
    public users(String con,String email,String name)
    {
        this.contact=con;
        this.email=email;
        this.name=name;
    }

    public users(String con,String email,String name,String pic,String uid)
    {
        this.contact=con;
        this.email=email;
        this.name=name;
        this.pic=pic;
        this.uid=uid;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
