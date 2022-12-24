package io.github.techstreet.dfscript.network.remote;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.network.AuthHandler;
import io.github.techstreet.dfscript.network.request.ForbiddenException;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteScript {
    private String ID;
    private String name;
    private String owner;
    private String description = "N/A";
    private int version = 0;
    private boolean verified;

    private URL url;

    private RemoteScript(String ID, boolean verified) {
        this.ID = ID;
        this.verified = verified;
        try {this.url = new URL(DFScript.BACKEND + "/script/" + ID);}
        catch (MalformedURLException e) {DFScript.LOGGER.error("Backend is invalid:" + DFScript.BACKEND);}
    }

    public boolean isVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) throws ForbiddenException {
        try {URL url = new URL(this.url.toString() + "/verify");}
        catch (MalformedURLException e) {DFScript.LOGGER.error("Backend is invalid:" + DFScript.BACKEND); return;}
//        AuthHandler.instance.addAuthorization();
        this.verified = verified;
    }
}
