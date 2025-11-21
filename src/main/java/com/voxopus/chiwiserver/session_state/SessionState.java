package com.voxopus.chiwiserver.session_state;

public abstract class SessionState<P> {

    protected P session;

    protected SessionState(P session){
        this.session = session;
    }

    public abstract StateResult handleStates(String input);
    
}
