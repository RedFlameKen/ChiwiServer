package com.voxopus.chiwiserver.session_state;

import com.voxopus.chiwiserver.enums.ConfirmEnum;

public class SessionStateHelper {

    public static ConfirmEnum checkConfirm(String confirm) {
        switch (confirm) {
            case "yes":
            case "yup":
            case "yep":
            case "yuh":
            case "yeah":
                return ConfirmEnum.YES;
            case "no":
            case "nope":
            case "nah":
                return ConfirmEnum.NO;
            case "":
            default:
                return ConfirmEnum.MISUNDERSTOOD;
        }
    }

}
