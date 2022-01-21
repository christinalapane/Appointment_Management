package model;


/**
 * all variables for First Level table
 */
public class FirstLevel {
    private int divisionID;
    private String division;
    private int countryID;


    /**
     * @param divisionID Division_ID
     * @param division Division
     * @param countryID Country_ID
     */
    public FirstLevel(int divisionID, String division, int countryID){
        this.divisionID = divisionID;
        this.division = division;
        this.countryID = countryID;
    }

    /**
     *Set and get all variables for first level table
     */
    public int getDivisionID(){return divisionID;}
    public void setDivisionID(int divisionID){this.divisionID = divisionID;}

    public String getDivision(){return division;}
    public void setDivision(String division){this.division = division;}

    public int getCountryID(){return countryID;}
    public void setCountryID(int countryID){this.countryID = countryID;}

    /**
     * @return division as string
     */
    @Override
    public String toString(){
        return division;
    }
}
