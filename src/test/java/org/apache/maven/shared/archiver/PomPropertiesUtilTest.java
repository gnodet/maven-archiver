/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.shared.archiver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PomPropertiesUtilTest {

    private PomPropertiesUtil util = new PomPropertiesUtil();

    @TempDir
    Path tempDirectory;

    @Test
    void testCreatePomProperties() throws IOException {
        Path pomPropertiesFile = tempDirectory.resolve("bar.properties");
        util.createPomProperties("org.foo", "bar", "2.1.5", new JarArchiver(), null, pomPropertiesFile);

        assertThat(pomPropertiesFile).exists();
        Properties actual = new Properties();
        actual.load(Files.newInputStream(pomPropertiesFile));
        assertEquals("org.foo", actual.getProperty("groupId"));
        assertEquals("bar", actual.getProperty("artifactId"));
        assertEquals("2.1.5", actual.getProperty("version"));

        // Now read the file directly to check for alphabetical order
        List<String> contents = Files.readAllLines(pomPropertiesFile, StandardCharsets.ISO_8859_1);
        assertEquals("artifactId=bar", contents.get(0));
        assertEquals("groupId=org.foo", contents.get(1));
        assertEquals("version=2.1.5", contents.get(2));
        assertEquals(3, contents.size());
    }

    @Test
    void testUnicodeEscape() throws IOException {
        Path pomPropertiesFile = tempDirectory.resolve("bar.properties");
        util.createPomProperties("org.foo", "こんにちは", "2.1.5", new JarArchiver(), null, pomPropertiesFile);

        assertThat(pomPropertiesFile).exists();
        Properties actual = new Properties();
        actual.load(Files.newInputStream(pomPropertiesFile));
        assertEquals("org.foo", actual.getProperty("groupId"));
        assertEquals("こんにちは", actual.getProperty("artifactId"));
        assertEquals("2.1.5", actual.getProperty("version"));

        // Now read the file directly to check for alphabetical order and encoding
        List<String> contents = Files.readAllLines(pomPropertiesFile, StandardCharsets.ISO_8859_1);
        assertEquals("artifactId=\\u3053\\u3093\\u306B\\u3061\\u306F", contents.get(0));
        assertEquals("groupId=org.foo", contents.get(1));
        assertEquals("version=2.1.5", contents.get(2));
        assertEquals(3, contents.size());
    }
}