

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

package org.openyolo.spi.assetlinks.loader;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openyolo.spi.assetlinks.data.AssetStatement;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.valid4j.errors.RequireViolation;

import java.util.Collections;
import java.util.List;

/**
 * Tests for {@link TargetAssetStatementLoader}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TargetAssetStatementLoaderTest {

    @Mock
    Context mockContext;

    private final List<AssetStatement> assetStatements = Collections.emptyList();

    @Test (expected = RequireViolation.class)
    public void testNullContext() {
        new TargetAssetStatementLoader(null, assetStatements);
    }

    @Test (expected = RequireViolation.class)
    public void testNullAssetStatements() {
        new TargetAssetStatementLoader(mockContext, null);
    }
}
