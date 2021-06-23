package com.calamar.ecom2;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class DistributionCenter {
    public Optional<Integer> id;

    private Optional<String> name;

    // Serialize with this name
    @JsonProperty(value = "name")
    public Optional<String> getName() {
        return name;
    }

    // Read from this property
    @JsonProperty(value = "F_1234")
    public void setName(Optional<String> name) {
        this.name = name;
    }
}
