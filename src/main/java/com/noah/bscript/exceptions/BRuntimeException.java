package com.noah.bscript.exceptions;

import com.noah.bscript.lang.BToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BRuntimeException extends RuntimeException {

    private BToken token;
    private String message;

}
