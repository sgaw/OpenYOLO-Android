/*
 * Copyright 2016 The OpenYOLO Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openyolo.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openyolo.api.AuthenticationMethods.ID_AND_PASSWORD;

import android.net.Uri;
import android.os.Parcel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.valid4j.errors.RequireViolation;

/**
 * Tests for {@link Credential}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CredentialTest {

    public static final String EMAIL_ID = "alice@example.com";
    public static final AuthenticationDomain AUTH_DOMAIN =
            new AuthenticationDomain("https://www.example.com");

    @Test(expected = RequireViolation.class)
    @SuppressWarnings("ConstantConditions")
    public void testBuilder_nullIdentifier() {
        new Credential.Builder(null, ID_AND_PASSWORD, AUTH_DOMAIN);
    }

    @Test(expected = RequireViolation.class)
    @SuppressWarnings("ConstantConditions")
    public void testBuilder_nullAuthMethod() {
        new Credential.Builder(EMAIL_ID, null, AUTH_DOMAIN);
    }

    @Test(expected = RequireViolation.class)
    @SuppressWarnings("ConstantConditions")
    public void testBuilder_nullAuthDomain() {
        new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, null);
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_emptyIdentifier() {
        new Credential.Builder("", ID_AND_PASSWORD, AUTH_DOMAIN);
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_authMethodWithoutScheme() {
        new Credential.Builder(EMAIL_ID, Uri.parse("www.example.com"), AUTH_DOMAIN);
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_authMethodWithPath() {
        new Credential.Builder(EMAIL_ID, Uri.parse("https://www.example.com/path"), AUTH_DOMAIN);
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_authMethodWithQuery() {
        new Credential.Builder(EMAIL_ID, Uri.parse("https://www.example.com?a=b"), AUTH_DOMAIN);
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_authMethodWithFragment() {
        new Credential.Builder(EMAIL_ID, Uri.parse("https://www.example.com#a"), AUTH_DOMAIN);
    }

    @Test
    public void testBuilder_mandatoryPropsOnly() {
        Credential cr = new Credential.Builder(
                EMAIL_ID,
                ID_AND_PASSWORD,
                new AuthenticationDomain("https://www.example.com")).build();
        assertThat(cr).isNotNull();
        assertThat(cr.getIdentifier()).isEqualTo(EMAIL_ID);
        assertThat(cr.getAuthenticationMethod()).isEqualTo(ID_AND_PASSWORD);
        assertThat(cr.getAuthenticationDomain()).isNotNull();
        assertThat(cr.getDisplayName()).isNull();
        assertThat(cr.getDisplayPicture()).isNull();
        assertThat(cr.getAdditionalProperties()).isEmpty();
    }

    @Test
    public void testBuilder_setIdentifier() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setIdentifier("bob@example.com")
                .build();
        assertThat(cr.getIdentifier()).isEqualTo("bob@example.com");
    }

    @Test(expected = RequireViolation.class)
    @SuppressWarnings("all")
    public void testBuilder_setIdentifier_toNull() {
        new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
            .setIdentifier(null)
            .build();
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_setIdentifier_toEmpty() {
        new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setIdentifier("")
                .build();
    }

    @Test
    public void testBuilder_setPassword() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setPassword("CorrectHorseBatteryStaple")
                .build();
        assertThat(cr.getPassword()).isEqualTo("CorrectHorseBatteryStaple");
    }

    @Test
    public void testBuilder_setPassword_twice() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setPassword("CorrectHorseBatteryStaple")
                .setPassword("password1")
                .build();
        assertThat(cr.getPassword()).isEqualTo("password1");
    }

    @Test
    public void testBuilder_setPassword_toNull() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setPassword("CorrectHorseBatteryStaple")
                .setPassword(null)
                .build();
        assertThat(cr.getPassword()).isNull();
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_setPassword_toEmpty() {
        new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setPassword("")
                .build();
    }

    @Test
    public void testBuilder_setDisplayName() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setDisplayName("Alice")
                .build();
        assertThat(cr.getDisplayName()).isEqualTo("Alice");
    }

    @Test
    public void testBuilder_setDisplayName_twice() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setDisplayName("Alice")
                .setDisplayName("Alicia")
                .build();
        assertThat(cr.getDisplayName()).isEqualTo("Alicia");
    }

    @Test
    public void testBuilder_setDisplayName_toNull() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setDisplayName("Alice")
                .setDisplayName(null)
                .build();
        assertThat(cr.getDisplayName()).isNull();
    }

    @Test(expected = RequireViolation.class)
    public void testBuilder_setDisplayName_toEmpty() {
        new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setDisplayName("")
                .build();
    }

    public void testBuilder_setDisplayPicture() {
        Uri pictureUri = Uri.parse("https://robohash.org/alice@example.com");
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setDisplayPicture(pictureUri)
                .build();

        assertThat(cr.getDisplayPicture()).isSameAs(pictureUri);
    }

    public void testBuilder_setDisplayPicture_twice() {
        Uri first = Uri.parse("https://robohash.org/alice@example.com");
        Uri second = Uri.parse("https://robohash.org/bob@example.com");
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setDisplayPicture(first)
                .setDisplayPicture(second)
                .build();

        assertThat(cr.getDisplayPicture()).isSameAs(second);
    }

    public void testBuilder_setDisplayPicture_toNull() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setDisplayPicture(Uri.parse("https://robohash.org/alice@example.com"))
                .setDisplayPicture((Uri)null)
                .build();

        assertThat(cr.getDisplayPicture()).isNull();
    }

    @Test
    public void testBuilder_setAuthenticationMethod() {
        Credential cr = new Credential.Builder(EMAIL_ID, ID_AND_PASSWORD, AUTH_DOMAIN)
                .setAuthenticationMethod(AuthenticationMethods.GOOGLE)
                .build();

        assertThat(cr.getAuthenticationMethod()).isEqualTo(AuthenticationMethods.GOOGLE);
    }

    @Test
    public void testGetProto() {
        Credential cr = new Credential.Builder(
                EMAIL_ID,
                ID_AND_PASSWORD,
                new AuthenticationDomain("https://www.example.com")).build();
        org.openyolo.proto.Credential proto = cr.getProto();
        assertThat(proto).isNotNull();
    }

    @Test
    public void testWriteToParcel(){
        Credential cr = new Credential.Builder(
                EMAIL_ID,
                ID_AND_PASSWORD,
                new AuthenticationDomain("https://www.example.com")).build();
        Parcel parcel = mock(Parcel.class);
        cr.writeToParcel(parcel, 0);
        verify(parcel).writeInt(anyInt());
        verify(parcel).writeByteArray(any(byte[].class));
    }
    private Set<String> keys(String... keys) {
        return new HashSet<>(Arrays.asList(keys));
    }
}
