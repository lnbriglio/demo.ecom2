package com.calamar.ecom2;

import java.util.Optional;

public class Contract {

    private Optional<Integer> id;
    public Optional<String> text;
    public Optional<String> subtext;
    public Optional<String> footer;
    public Optional<Person> person;

    public Optional<Integer> getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = Optional.of(id);
    }
}
