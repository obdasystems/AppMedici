package com.obdasystems.pocmedici.network.request;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class UserDeviceRegistrationRequest {
    @SerializedName("registrationToken")
    protected String registrationToken;

    @SerializedName("description")
    protected String deviceDescription;

    public UserDeviceRegistrationRequest() {

    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public UserDeviceRegistrationRequest setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
        return this;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public UserDeviceRegistrationRequest setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserDeviceRegistrationRequest that = (UserDeviceRegistrationRequest)obj;
        return Objects.equals(registrationToken, that.registrationToken) &&
                Objects.equals(deviceDescription, that.deviceDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationToken, deviceDescription);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("registrationToken", registrationToken)
                .add("deviceDescription", deviceDescription)
                .toString();
    }

}

