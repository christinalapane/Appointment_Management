package model;
import java.time.LocalDateTime;

/**
 * All information needed for Appointment table
 */
public class Appointment {
   public  int appointmentID;
   private  String title;
   private  String description;
   private  String location;
   private  String type;
   private LocalDateTime startTime;
   private LocalDateTime endTime;
   private  LocalDateTime created;
   private  String createdBy;
   private  LocalDateTime   lastUpdate;
   private  String updatedBy;
   private  int customerID;
   private String customerName;
   private  int contactID;
   private  String contactName;
   private  int userID;
   private  String username;


   /**
    *
    * @param appointmentID Appointment_ID
    * @param title Title
    * @param description Description
    * @param location Location
    * @param type Type
    * @param startTime Start
    * @param endTime End
    * @param created Create_Date
    * @param createdBy Created_By
    * @param lastUpdate Last_Update
    * @param updatedBy Last_Updated_By
    * @param customerID Customer_ID
    * @param customerName Customer_Name
    * @param contactID Contact_ID
    * @param contactName Contact_Name
    * @param userID User_ID
    * @param username User_Name
    */
   public Appointment(int appointmentID, String title, String description, String location, String type, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime created, String createdBy, LocalDateTime lastUpdate, String updatedBy,
                      int customerID, String customerName, int userID, String username, int contactID, String contactName) {
      this.appointmentID = appointmentID;
      this.title = title;
      this.description = description;
      this.location = location;
      this.type = type;
      this.startTime = startTime;
      this.endTime = endTime;
      this.created = created;
      this.createdBy = createdBy;
      this.lastUpdate = lastUpdate;
      this.updatedBy = updatedBy;
      this.customerID = customerID;
      this.customerName = customerName;
      this.userID = userID;
      this.username = username;
      this.contactID = contactID;
      this.contactName = contactName;


   }


   /**
    *to set and get all variables from Appointment
    */

   public  int getAppointmentID(){return appointmentID;}
   public  void setAppointmentID(int appointmentID){
      this.appointmentID = appointmentID;}

   public  String getTitle(){return title;}
   public void setTitle(String title){this.title = title;}

   public  String getDescription(){return description;}
   public void setDescription(String description){this.description = description;}

   public String getLocation(){return location;}
   public void setLocation(String location){this.location = location;}

   public String getType(){return type;}
   public void setType(){this.type = type;}

   public  LocalDateTime getStartTime(){return startTime;}
   public void setStartTime(LocalDateTime startTime){this.startTime = startTime;}

   public  LocalDateTime getEndTime(){return endTime;}
   public void setEndTime(LocalDateTime endTime){this.endTime = endTime;}

   public  LocalDateTime getCreated(){return created;}
   public void setCreated(LocalDateTime created){this.created = created;}

   public  String getCreatedBy(){return createdBy;}
   public void setCreatedBy(String createdBy){this.createdBy = createdBy;}

   public  LocalDateTime getLastUpdate(){return lastUpdate;}
   public void setLastUpdate(LocalDateTime lastUpdate){this.lastUpdate = lastUpdate;}

   public  String getUpdatedBy(){return updatedBy;}
   public void setUpdatedBy(String updatedBy){this.updatedBy = updatedBy;}

   public  int getCustomerID(){return customerID;}
   public void setCustomerID(int customerID){this.customerID = customerID;}

   public String getCustomerName(){return customerName;}
   public void setCustomerName(String customerName){this.customerName = customerName;}


   public  int getContactID(){return contactID;}
   public void setContactID(int contactID){this.contactID = contactID;}

   public  String getContactName(){return contactName;}
   public void setContactName(String contactName){this.contactName = contactName;}

   public  int getUserID(){return userID;}
   public void setUserID(int userID){this.userID = userID;}

   public String getUsername(){return username;}
   public void setUserName(String username){this.username = username;}


   /**
    * @return Location as  a String
    */
   @Override
   public  String toString(){
     return location;
      }

}








