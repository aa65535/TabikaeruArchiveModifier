package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.GameData.Data;

import java.util.Calendar;

public class Mail extends Data {
    String title;
    Calendar datetime;

    public Mail(long offset, String title, Calendar datetime) {
        super(offset);
        this.title = title;
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getDatetime() {
        return datetime;
    }
}
