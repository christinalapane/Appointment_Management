package model;

/**
 * All information for Countries table
 */
public class Countries {
    int countryID;
    String countryName;


    /**
     * @param countryID Country_ID
     * @param countryName Country
     */
    public Countries(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     * get and set all variables for country table
     */
    public int getCountryID() {return countryID;}

    public void setCountryID(int countryID) {this.countryID = countryID;}

    public String getCountryName() {return countryName;}

    public void setCountryName(String countryName) {this.countryName = countryName;}

    /**
     * @return Country as string
     */
    @Override
    public String toString(){
        return countryName;
    }
}
