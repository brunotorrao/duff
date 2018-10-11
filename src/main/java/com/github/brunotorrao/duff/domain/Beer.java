package com.github.brunotorrao.duff.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "beers")
@EqualsAndHashCode
public class Beer {

    @Id
    private String id;
    @Indexed(unique = true)
    private String style;
    private Temperature idealTemperature;
}
