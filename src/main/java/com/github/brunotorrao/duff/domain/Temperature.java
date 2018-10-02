package com.github.brunotorrao.duff.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Temperature {

    private BigDecimal min;
    private BigDecimal max;
}
