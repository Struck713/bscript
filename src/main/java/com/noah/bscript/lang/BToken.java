package com.noah.bscript.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BToken {

    private BType type;
    private String text;
    private int position;

}
