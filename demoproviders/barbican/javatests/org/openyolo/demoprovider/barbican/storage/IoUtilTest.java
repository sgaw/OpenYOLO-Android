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

package org.openyolo.demoprovider.barbican.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class IoUtilTest {

    @Test
    public void testCrypto_emptyMessage() throws Exception {
        byte[] testData = new byte[0];
        assertThat(testData).isEqualTo(writeAndRead(testData, generateTestKey()));
    }

    @Test
    public void testCrypto_subBlock() throws Exception {
        byte[] testData = generateArray(IoUtil.BLOCK_SIZE - 2);
        assertThat(testData).isEqualTo(writeAndRead(testData, generateTestKey()));
    }

    @Test
    public void testCrypto_singleBlock() throws Exception {
        byte[] testData = generateArray(IoUtil.BLOCK_SIZE);
        assertThat(testData).isEqualTo(writeAndRead(testData, generateTestKey()));
    }

    @Test
    public void testCrypto_multiBlock() throws Exception {
        byte[] testData = generateArray(4 * IoUtil.BLOCK_SIZE);
        assertThat(testData).isEqualTo(writeAndRead(testData, generateTestKey()));
    }

    @Test
    public void testCrypto_multiBlockWithSubBlock() throws Exception {
        byte[] testData = generateArray(4 * IoUtil.BLOCK_SIZE + 3);
        assertThat(testData).isEqualTo(writeAndRead(testData, generateTestKey()));
    }

    @Test
    public void testCryptoLongStream() throws Exception {
        byte[] longData = generateArray(1024 * 1024);
        assertThat(longData).isEqualTo(writeAndRead(longData, generateTestKey()));
    }

    private byte[] writeAndRead(byte[] data, byte[] key) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Sink outSink = Okio.sink(baos);
        BufferedSink cryptoSink = Okio.buffer(IoUtil.encryptTo(outSink, key));
        cryptoSink.write(data);
        cryptoSink.close();

        byte[] cipherText = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(cipherText);
        Source inSource = Okio.source(bais);
        BufferedSource cryptoSource = Okio.buffer(IoUtil.decryptFrom(inSource, generateTestKey()));

        byte[] result = cryptoSource.readByteArray();
        cryptoSource.close();
        return result;
    }

    private byte[] generateArray(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }

        return bytes;
    }

    private byte[] generateTestKey() {
        return generateArray(IoUtil.KEY_SIZE);
    }
}
