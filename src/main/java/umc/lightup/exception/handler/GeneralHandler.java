package umc.lightup.exception.handler;

import umc.lightup.api.code.BaseErrorCode;
import umc.lightup.exception.GeneralException;

public class GeneralHandler extends GeneralException {

    public GeneralHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
