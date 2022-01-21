package model;

/**
 * All information for Contact
 */
public class Contact {
    private int contactID;
    private String name;
    private String email;

    public Contact() {
        this.contactID = -1;
        this.name = null;
        this.email = null;
    }

    /**
     *
     * @param contactID Contact_ID
     * @param name Contact_Name
     * @param email Email
     */
    public Contact(int contactID, String name, String email){
        this.contactID = contactID;
        this.name = name;
        this.email = email;
    }

    /**
     *To get and set all variables in Contact table
     */
    public int getContactID(){return contactID;}
    public void setContactID(int contactID){this.contactID = contactID;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email; }

    /**
     * @return Name as String
     */
    @Override
    public String toString(){
        return name;

    }
}
