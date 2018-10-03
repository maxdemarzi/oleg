package com.olegs;

public class TripleResult {
    public String first_title;
    public String first_ucid;
    public String second_title;
    public String second_ucid;
    public String third_title;
    public String third_ucid;

    public TripleResult(String first_title, String first_ucid,
                        String second_title, String second_ucid,
                        String third_title, String third_ucid) {
        this.first_title = first_title;
        this.first_ucid = first_ucid;
        this.second_title = second_title;
        this.second_ucid = second_ucid;
        this.third_title = third_title;
        this.third_ucid = third_ucid;

    }

}
