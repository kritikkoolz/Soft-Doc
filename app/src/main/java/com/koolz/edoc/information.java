package com.koolz.edoc;

public class information {
    private String Holder_name;
    private String Unique_Id;

    public information(){
    }
    public information(String holder_name, String unique_Id) {
        this.Holder_name = holder_name;
        this.Unique_Id = unique_Id;
    }

    public String getHolder_name() {
        return Holder_name;
    }

    public String getUnique_Id() {
        return Unique_Id;
    }

    public void setHolder_name(String holder_name) {
        Holder_name = holder_name;
    }

    public void setUnique_Id(String unique_Id) {
        Unique_Id = unique_Id;
    }
}
