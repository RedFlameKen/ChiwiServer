package com.voxopus.chiwiserver.session_state;

import com.voxopus.chiwiserver.enums.ConfirmEnum;

public class SessionStateHelper {

    public static ConfirmEnum checkConfirm(String confirm) {
        switch (confirm) {
            case "yes":
            case "yup":
            case "yuh":
            case "yeah":
                return ConfirmEnum.YES;
            case "no":
            case "nope":
            case "nah":
                return ConfirmEnum.NO;
            default:
                return ConfirmEnum.MISUNDERSTOOD;
        }
    }

}
