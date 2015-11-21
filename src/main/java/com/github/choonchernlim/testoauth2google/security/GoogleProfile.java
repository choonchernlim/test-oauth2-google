package com.github.choonchernlim.testoauth2google.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class GoogleProfile {
    protected String id;
    protected String email;
    @JsonProperty("verified_email")
    protected Boolean verifiedEmail;
    protected String name;
    @JsonProperty("given_name")
    protected String givenName;
    @JsonProperty("family_name")
    protected String familyName;
    protected String link;
    protected String picture;
    protected String gender;
    protected String locale;
    protected String hd;

    public GoogleProfile() {
    }

    public GoogleProfile(GoogleProfile googleProfile) {
        this.id = googleProfile.id;
        this.email = googleProfile.email;
        this.verifiedEmail = googleProfile.verifiedEmail;
        this.name = googleProfile.name;
        this.givenName = googleProfile.givenName;
        this.familyName = googleProfile.familyName;
        this.link = googleProfile.link;
        this.picture = googleProfile.picture;
        this.gender = googleProfile.gender;
        this.locale = googleProfile.locale;
        this.hd = googleProfile.hd;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getVerifiedEmail() {
        return verifiedEmail;
    }

    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getLink() {
        return link;
    }

    public String getPicture() {
        return picture;
    }

    public String getGender() {
        return gender;
    }

    public String getLocale() {
        return locale;
    }

    public String getHd() {
        return hd;
    }
}
