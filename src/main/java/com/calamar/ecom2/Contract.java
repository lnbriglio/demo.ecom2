package com.calamar.ecom2;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Optional;

public class Contract {

    private Optional<Integer> id;
    public Optional<String> text;
    public Optional<String> subtext;
    public Optional<String> footer;
    public Optional<Person> person;

    public Optional<Boolean> flag;

    public Optional<Integer> getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = Optional.of(id);
    }

    public Optional<Boolean> getFlag() {
        return flag;
    }
    
    public void setFlag(Object flag) {
        if (flag.getClass() == Boolean.class) {
            this.flag = Optional.of((Boolean) flag);
        } else {
            if (flag != null) {
                String flagAsString = (String) flag;
                if (flagAsString.equalsIgnoreCase("Y")) {
                    this.flag = Optional.of(true);
                } else if (flagAsString.equalsIgnoreCase("N")) {
                    this.flag = Optional.of(false);
                }
            }
        }
    }
}
