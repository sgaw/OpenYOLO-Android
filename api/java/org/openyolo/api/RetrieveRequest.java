/*
 * Copyright 2016 The OpenYOLO Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openyolo.api;

import static java.util.Collections.EMPTY_SET;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.openyolo.api.internal.CollectionConverter.toList;
import static org.openyolo.api.internal.CollectionConverter.toMap;
import static org.openyolo.api.internal.CollectionConverter.toSet;
import static org.openyolo.api.internal.CustomMatchers.isValidAuthenticationMethod;
import static org.openyolo.api.internal.CustomMatchers.notNullOrEmptyString;
import static org.openyolo.api.internal.KeyValuePairConverters.CONVERTER_ENTRY_TO_KVP;
import static org.openyolo.api.internal.KeyValuePairConverters.CONVERTER_KVP_TO_PAIR;
import static org.openyolo.api.internal.UriConverters.CONVERTER_STRING_TO_URI;
import static org.openyolo.api.internal.UriConverters.CONVERTER_URI_TO_STRING;
import static org.valid4j.Validation.validate;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openyolo.api.internal.NoopValueConverter;
import org.openyolo.proto.CredentialRetrieveRequest;

/**
 * A request for credentials, to be sent to credential providers on the device.
 */
public class RetrieveRequest implements Parcelable {

    /**
     * Parcelable reader for {@link RetrieveRequest} instances.
     *
     * @see android.os.Parcelable
     */
    public static final Creator<RetrieveRequest> CREATOR = new RetrieveRequestCreator();

    @NonNull
    private final Set<Uri> mAuthMethods;

    @NonNull
    private final Map<String, byte[]> mAdditionalParams;

    private RetrieveRequest(
            @NonNull Set<Uri> authMethods,
            @NonNull Map<String, byte[]> additionalParams) {
        mAuthMethods = authMethods;
        mAdditionalParams = additionalParams;
    }

    /**
     * Creates a {@link RetrieveRequest} from the given authentication methods.
     */
    public static RetrieveRequest forAuthenticationMethods(
            @NonNull Set<Uri> authenticationMethods) {
        return new RetrieveRequest.Builder(authenticationMethods).build();
    }

    /**
     * Creates a {@link RetrieveRequest} from the given authentication methods.
     */
    public static RetrieveRequest forAuthenticationMethods(@NonNull Uri... authenticationMethods) {
        return new RetrieveRequest.Builder(authenticationMethods).build();
    }

    /**
     * The set of authentication methods that the requestor supports. This is used to filter the set
     * of credentials saved by the provider. At least one authentication method must be specified.
     */
    @NonNull
    public Set<Uri> getAuthenticationMethods() {
        return mAuthMethods;
    }

    /**
     * The map of additional, non-standard properties included with this request.
     */
    @NonNull
    public Map<String, byte[]> getAdditionalParameters() {
        return mAdditionalParams;
    }

    /**
     * Retrieves the value of the named additional parameter, where the value is a UTF-8 encoded
     * string.
     */
    @Nullable
    public String getAdditionalParameterAsString(@NonNull String key) {
        validate(key, notNullValue(), IllegalArgumentException.class);

        if (mAdditionalParams.containsKey(key)) {
            return new String(mAdditionalParams.get(key), Charset.forName("UTF-8"));
        }
        return null;
    }

    /**
     * Retrieves the raw, byte-array value of the named additional parameter.
     */
    @Nullable
    public byte[] getAdditionalParameter(@NonNull String key) {
        validate(key, notNullValue(), IllegalArgumentException.class);

        return mAdditionalParams.get(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        byte[] protoBytes = toProtocolBuffer().encode();
        dest.writeInt(protoBytes.length);
        dest.writeByteArray(protoBytes);
    }

    /**
     * Converts the request into a protocol buffer, for storage or transmission.
     */
    public CredentialRetrieveRequest toProtocolBuffer() {
        return new CredentialRetrieveRequest.Builder()
                .authMethods(toList(mAuthMethods, CONVERTER_URI_TO_STRING))
                .additionalParams(toList(mAdditionalParams.entrySet(), CONVERTER_ENTRY_TO_KVP))
                .build();
    }

    /**
     * Creates {@link RetrieveRequest} instances.
     */
    public static final class Builder {

        private Set<Uri> mAuthMethods = new HashSet<>();
        private Map<String, byte[]> mAdditionalParams = new HashMap<>();

        /**
         * Starts the process of creating a retrieve request using the data contained in the
         * provided protocol buffer representation.
         */
        public Builder(@NonNull CredentialRetrieveRequest requestProto) {
            validate(requestProto, notNullValue(), IllegalArgumentException.class);

            setAuthenticationMethods(toSet(requestProto.authMethods, CONVERTER_STRING_TO_URI));
            setAdditionalParameters(toMap(requestProto.additionalParams, CONVERTER_KVP_TO_PAIR));
        }

        /**
         * Starts the process of describing a retrieve request. At least one authentication method
         * must be provided.
         */
        public Builder(@NonNull Uri... authMethods) {
            setAuthenticationMethods(
                    toSet(null, authMethods, NoopValueConverter.<Uri>getInstance()));
        }

        /**
         * Starts the process of describing a retrieve request. At least one authentication method
         * must be provided.
         */
        public Builder(@NonNull Set<Uri> authMethods) {
            setAuthenticationMethods(authMethods);
        }

        /**
         * Specifies the authentication methods supported by the requester. At least one
         * authentication method must be provided.
         */
        private Builder setAuthenticationMethods(@NonNull Set<Uri> authMethods) {
            validate(authMethods, notNullValue(), IllegalArgumentException.class);
            validate(authMethods, not(equalTo(EMPTY_SET)), IllegalArgumentException.class);
            validate(
                    authMethods,
                    everyItem(isValidAuthenticationMethod()),
                    IllegalArgumentException.class);

            mAuthMethods = authMethods;
            return this;
        }

        /**
         * Specifies additional, non-standard retrieve request parameters. The provided map must be
         * non-null, and contain only non-null keys and values.
         */
        public Builder setAdditionalParameters(
                @NonNull Map<String, byte[]> additionalParams) {
            validate(additionalParams, notNullValue(), IllegalArgumentException.class);
            validate(
                    additionalParams.keySet(),
                    everyItem(notNullOrEmptyString()),
                    IllegalArgumentException.class);
            validate(
                    additionalParams.values(),
                    everyItem(notNullValue()),
                    IllegalArgumentException.class);

            mAdditionalParams = additionalParams;
            return this;
        }

        /**
         * Adds an additional parameter, where the value will be encoded as a UTF-8 string. Both the
         * parameter name and value must be non-null.
         *
         * @see {@link RetrieveRequest#getAdditionalParameterAsString(String)}
         */
        public Builder addAdditionalParameter(
                @NonNull String name,
                @NonNull String value) {
            validate(value, notNullValue(), IllegalArgumentException.class);

            addAdditionalParameter(name, value.getBytes(Charset.forName("UTF-8")));
            return this;
        }

        /**
         * Adds an additional parameter. Both the parameter name and value must be non-null.
         */
        public Builder addAdditionalParameter(String name, byte[] value) {
            validate(name, notNullOrEmptyString(), IllegalArgumentException.class);
            validate(value, notNullValue(), IllegalArgumentException.class);

            mAdditionalParams.put(name, value);
            return this;
        }

        /**
         * Creates a {@link RetrieveRequest} using the properties set on the builder.
         */
        @NonNull
        public RetrieveRequest build() {
            return new RetrieveRequest(
                    unmodifiableSet(mAuthMethods),
                    unmodifiableMap(mAdditionalParams));
        }
    }

    private static final class RetrieveRequestCreator implements Creator<RetrieveRequest> {

        @Override
        public RetrieveRequest createFromParcel(Parcel in) {

            int protoLength = in.readInt();
            byte[] protoBytes = new byte[protoLength];
            in.readByteArray(protoBytes);

            try {
                return new RetrieveRequest.Builder(
                        CredentialRetrieveRequest.ADAPTER.decode(protoBytes))
                        .build();
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to read proto from parcel", ex);
            }
        }

        @Override
        public RetrieveRequest[] newArray(int size) {
            return new RetrieveRequest[size];
        }
    }
}
