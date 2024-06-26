// Copyright 2017 The Nomulus Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package google.registry.tools;

import static com.google.common.truth.Truth.assertThat;
import static google.registry.model.registrar.RegistrarBase.State.ACTIVE;
import static google.registry.testing.DatabaseHelper.createTld;
import static google.registry.testing.DatabaseHelper.loadRegistrar;
import static google.registry.testing.DatabaseHelper.persistResource;
import static google.registry.testing.EppExceptionSubject.assertAboutEppExceptions;
import static google.registry.util.DateTimeUtils.START_OF_TIME;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.beust.jcommander.ParameterException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import google.registry.flows.EppException;
import google.registry.flows.TransportCredentials.BadRegistrarPasswordException;
import google.registry.flows.certs.CertificateChecker;
import google.registry.model.registrar.Registrar;
import google.registry.model.registrar.RegistrarBase.State;
import google.registry.testing.CertificateSamples;
import google.registry.util.CidrAddressBlock;
import java.nio.file.Files;
import java.nio.file.Path;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ValidateLoginCredentialsCommand}. */
class ValidateLoginCredentialsCommandTest extends CommandTestCase<ValidateLoginCredentialsCommand> {

  private static final String PASSWORD = "foo-BAR2";
  private static final String CERT_HASH = CertificateSamples.SAMPLE_CERT3_HASH;
  private static final String CLIENT_IP = "1.2.3.4";

  @BeforeEach
  void beforeEach() {
    createTld("tld");
    persistResource(
        loadRegistrar("NewRegistrar")
            .asBuilder()
            .setPassword(PASSWORD)
            .setClientCertificate(CertificateSamples.SAMPLE_CERT3, DateTime.now(UTC))
            .setIpAddressAllowList(ImmutableList.of(new CidrAddressBlock(CLIENT_IP)))
            .setState(ACTIVE)
            .setAllowedTlds(ImmutableSet.of("tld"))
            .build());
    fakeClock.setTo(DateTime.parse("2020-11-01T00:00:00Z"));
    command.certificateChecker =
        new CertificateChecker(
            ImmutableSortedMap.of(START_OF_TIME, 825, DateTime.parse("2020-09-01T00:00:00Z"), 398),
            30,
            15,
            2048,
            ImmutableSet.of("secp256r1", "secp384r1"),
            fakeClock);
  }

  @Test
  void testSuccess() throws Exception {
    runCommand(
        "--client=NewRegistrar",
        "--password=" + PASSWORD,
        "--cert_file=" + getCertFilename(CertificateSamples.SAMPLE_CERT3),
        "--ip_address=" + CLIENT_IP);
  }

  @Test
  void testFailure_registrarIsDisabled() {
    persistResource(
        Registrar.loadByRegistrarId("NewRegistrar")
            .get()
            .asBuilder()
            .setState(State.DISABLED)
            .build());
    IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class,
            () ->
                runCommand(
                    "--client=NewRegistrar",
                    "--password=" + PASSWORD,
                    "--cert_file=" + getCertFilename(CertificateSamples.SAMPLE_CERT3),
                    "--ip_address=" + CLIENT_IP));
    assertThat(thrown)
        .hasMessageThat()
        .isEqualTo("Registrar NewRegistrar has non-live state: DISABLED");
  }

  @Test
  void testFailure_loginWithBadPassword() throws Exception {
    EppException thrown =
        assertThrows(
            BadRegistrarPasswordException.class,
            () ->
                runCommand(
                    "--client=NewRegistrar",
                    "--password=" + new StringBuilder(PASSWORD).reverse(),
                    "--cert_file=" + getCertFilename(CertificateSamples.SAMPLE_CERT3),
                    "--ip_address=" + CLIENT_IP));
    assertAboutEppExceptions().that(thrown).marshalsToXml();
  }

  @Test
  void testFailure_loginWithBadCertificateHash() {
    EppException thrown =
        assertThrows(
            EppException.class,
            () ->
                runCommand(
                    "--client=NewRegistrar",
                    "--password=" + PASSWORD,
                    "--cert_hash=" + new StringBuilder(CERT_HASH).reverse(),
                    "--ip_address=" + CLIENT_IP));
    assertAboutEppExceptions().that(thrown).marshalsToXml();
  }

  @Test
  void testFailure_loginWithBadIp() {
    EppException thrown =
        assertThrows(
            EppException.class,
            () ->
                runCommand(
                    "--client=NewRegistrar",
                    "--password=" + PASSWORD,
                    "--cert_hash=" + CERT_HASH,
                    "--ip_address=" + new StringBuilder(CLIENT_IP).reverse()));
    assertAboutEppExceptions().that(thrown).marshalsToXml();
  }

  @Test
  void testFailure_missingClientId() {
    assertThrows(
        ParameterException.class,
        () ->
            runCommand(
                "--password=" + PASSWORD, "--cert_hash=" + CERT_HASH, "--ip_address=" + CLIENT_IP));
  }

  @Test
  void testFailure_missingPassword() {
    assertThrows(
        ParameterException.class,
        () ->
            runCommand(
                "--client=NewRegistrar", "--cert_hash=" + CERT_HASH, "--ip_address=" + CLIENT_IP));
  }

  @Test
  void testFailure_unknownFlag() {
    assertThrows(
        ParameterException.class,
        () ->
            runCommand(
                "--client=NewRegistrar",
                "--password=" + PASSWORD,
                "--cert_hash=" + CERT_HASH,
                "--ip_address=" + CLIENT_IP,
                "--unrecognized_flag=foo"));
  }

  @Test
  void testFailure_certHashAndCertFile() throws Exception {
    Path certFile = Files.createFile(tmpDir.resolve("temp.crt"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            runCommand(
                "--client=NewRegistrar",
                "--password=" + PASSWORD,
                "--cert_hash=" + CERT_HASH,
                "--cert_file=" + certFile.toString(),
                "--ip_address=" + CLIENT_IP));
  }
}
