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

package google.registry.testing;

/** Utility methods for tests that involve certificates. */
public final class CertificateSamples {

  /*
   * openssl req -new -nodes -x509 -days 10000 -newkey rsa:2048 -keyout client1.key -out client1.crt
   * -subj "/C=US/ST=New York/L=New York/O=Google/OU=domain-registry-test/CN=client1"
   */
  public static final String SAMPLE_CERT =
      """
      -----BEGIN CERTIFICATE-----
      MIIDvTCCAqWgAwIBAgIJAK/PgPT0jTwRMA0GCSqGSIb3DQEBCwUAMHUxCzAJBgNV
      BAYTAlVTMREwDwYDVQQIDAhOZXcgWW9yazERMA8GA1UEBwwITmV3IFlvcmsxDzAN
      BgNVBAoMBkdvb2dsZTEdMBsGA1UECwwUZG9tYWluLXJlZ2lzdHJ5LXRlc3QxEDAO
      BgNVBAMMB2NsaWVudDEwHhcNMTUwODI2MTkxODA4WhcNNDMwMTExMTkxODA4WjB1
      MQswCQYDVQQGEwJVUzERMA8GA1UECAwITmV3IFlvcmsxETAPBgNVBAcMCE5ldyBZ
      b3JrMQ8wDQYDVQQKDAZHb29nbGUxHTAbBgNVBAsMFGRvbWFpbi1yZWdpc3RyeS10
      ZXN0MRAwDgYDVQQDDAdjbGllbnQxMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB
      CgKCAQEAvoE/IoFJyzb0dU4NFhL8FYgy+B/GnUd5aA66CMx5xKRMbEAtIgxU8TTO
      W+9jdTsE00Grk3Ct4KdY73CYW+6IFXL4O0K/m5S+uajh+I2UMVZJV38RAIqNxue0
      Egv9M4haSsCVIPcX9b+6McywfYSF1bzPb2Gb2FAQO7Jb0BjlPhPMIROCrbG40qPg
      LWrl33dz+O52kO+DyZEzHqI55xH6au77sMITsJe+X23lzQcMFUUm8moiOw0EKrj/
      GaMTZLHP46BCRoJDAPTNx55seIwgAHbKA2VVtqrvmA2XYJQA6ipdhfKRoJFy8Z8H
      DYsorGtazQL2HhF/5uJD25z1m5eQHQIDAQABo1AwTjAdBgNVHQ4EFgQUParEmiSR
      U/Oqy8hr7k+MBKhZwVkwHwYDVR0jBBgwFoAUParEmiSRU/Oqy8hr7k+MBKhZwVkw
      DAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAojsUhF6PtZrStnHBFWNR
      ryzvANB8krZlYeX9Hkqn8zIVfAkpbVmL8aZQ7yj17jSpw47PQh3x5gwA9yc/SS0G
      E1rGuxYH02UGbua8G0+vviSQfLtskPQzK7EIR63WNhHEo/Q9umLJkZ0LguWEBf3L
      q8CoXv2i/RNvqVPcTNp/zCKXJZAa8wAjNRJs834AZj4k5xwyYZ3F8D5PGz+YMOmV
      M9Qd+NdXSC/Qn7HQzFhE8p5elBV35P8oX5dXEfn0S7zOXDenp5JvvLoggOWOcKsq
      KiWDQrsT+TMKmHL94/h4t7FghtQLMzY5SGYJsYTv/LG8tewrz6KRb/Wj3JNojyEw
      Ug==
      -----END CERTIFICATE-----
      """;

  /*
   * python -c "import sys;print sys.argv[1].decode('hex').encode('base64').strip('\n=')" $(openssl
   * x509 -fingerprint -sha256 -in client1.cert | grep -Po '(?<=Fingerprint=).*' | sed s/://g)
   */
  public static final String SAMPLE_CERT_HASH = "vue+ZFJC2R7/LedIDQ53NbMoIMSVpqjEJA1CAJVumos";

  /*
   * openssl req -new -nodes -x509 -days 10000 -newkey rsa:2048 -keyout client2.key -out client2.crt
   * -subj "/C=US/ST=New York/L=New York/O=Google/OU=domain-registry-test/CN=client2"
   */
  public static final String SAMPLE_CERT2 =
      """
      -----BEGIN CERTIFICATE-----
      MIIDvTCCAqWgAwIBAgIJANoEy6mYwalPMA0GCSqGSIb3DQEBCwUAMHUxCzAJBgNV
      BAYTAlVTMREwDwYDVQQIDAhOZXcgWW9yazERMA8GA1UEBwwITmV3IFlvcmsxDzAN
      BgNVBAoMBkdvb2dsZTEdMBsGA1UECwwUZG9tYWluLXJlZ2lzdHJ5LXRlc3QxEDAO
      BgNVBAMMB2NsaWVudDIwHhcNMTUwODI2MTkyODU3WhcNNDMwMTExMTkyODU3WjB1
      MQswCQYDVQQGEwJVUzERMA8GA1UECAwITmV3IFlvcmsxETAPBgNVBAcMCE5ldyBZ
      b3JrMQ8wDQYDVQQKDAZHb29nbGUxHTAbBgNVBAsMFGRvbWFpbi1yZWdpc3RyeS10
      ZXN0MRAwDgYDVQQDDAdjbGllbnQyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB
      CgKCAQEAw2FtuDyoR+rUJHp6k7KwaoHGHPV1xnC8IpG9O0SZubOXrFrnBHggBsbu
      +DsknbHXjmoihSFFem0KQqJg5y34aDAHXQV3iqa7nDfb1x4oc5voVz9gqjdmGKNm
      WF4MTIPNMu8KY52M852mMCxODK+6MZYp7wCmVa63KdCm0bW/XsLgoA/+FVGwKLhf
      UqFzt10Cf+87zl4VHrSaJqcHBYM6yAO5lvkr5VC6g8rRQ+dJ+pBT2D99YpSF1aFc
      rWbBreIypixZAnXm/Xoogu6RnohS29VCJp2dXFAJmKXGwyKNQFXfEKxZBaBi8uKH
      XF459795eyF9xHgSckEgu7jZlxOk6wIDAQABo1AwTjAdBgNVHQ4EFgQUv26AsQyc
      kLOjkhqcFLOuueB33l4wHwYDVR0jBBgwFoAUv26AsQyckLOjkhqcFLOuueB33l4w
      DAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEANBuV+QDISSnGAEHKbR40
      zUYdOjdZ399zcFNqTSPHwmE0Qu8pbmXhofpBfjzrcv0tkVbhSLYnT22qhx7aDmhb
      bOS8CeVYCwl5eiDTkJly3pRZLzJpy+UT5z8SPxO3MrTqn+wuj0lBpWRTBCWYAUpr
      IFRmgVB3IwVb60UIuxhmuk8TVss2SzNrdhdt36eAIPJ0RWEb0KHYHi35Y6lt4f+t
      iVk+ZR0cCbHUs7Q1RqREXHd/ICuMRLY/MsadVQ9WDqVOridh198X/OIqdx/p9kvJ
      1R80jDcVGNhYVXLmHu4ho4xrOaliSYvUJSCmaaSEGVZ/xE5PI7S6A8RMdj0iXLSt
      Bg==
      -----END CERTIFICATE-----
      """;

  /*
   * python -c "import sys;print sys.argv[1].decode('hex').encode('base64').strip('\n=')" $(openssl
   * x509 -fingerprint -sha256 -in client2.crt | grep -Po '(?<=Fingerprint=).*' | sed s/://g)
   */
  public static final String SAMPLE_CERT2_HASH = "GNd6ZP8/n91t9UTnpxR8aH7aAW4+CpvufYx9ViGbcMY";

  /*
   * openssl req -new -nodes -x509 -days 200 -newkey rsa:2048 -keyout client1.key -out client1.crt
   * -subj "/C=US/ST=New York/L=New York/O=Google/OU=domain-registry-test/CN=client1"
   */
  public static final String SAMPLE_CERT3 =
      """
          -----BEGIN CERTIFICATE-----
          MIIDyzCCArOgAwIBAgIUJnhiVrxAxgwkLJzHPm1w/lBoNs4wDQYJKoZIhvcNAQEL
          BQAwdTELMAkGA1UEBhMCVVMxETAPBgNVBAgMCE5ldyBZb3JrMREwDwYDVQQHDAhO
          ZXcgWW9yazEPMA0GA1UECgwGR29vZ2xlMR0wGwYDVQQLDBRkb21haW4tcmVnaXN0
          cnktdGVzdDEQMA4GA1UEAwwHY2xpZW50MTAeFw0yMDEwMTIxNzU5NDFaFw0yMTA0
          MzAxNzU5NDFaMHUxCzAJBgNVBAYTAlVTMREwDwYDVQQIDAhOZXcgWW9yazERMA8G
          A1UEBwwITmV3IFlvcmsxDzANBgNVBAoMBkdvb2dsZTEdMBsGA1UECwwUZG9tYWlu
          LXJlZ2lzdHJ5LXRlc3QxEDAOBgNVBAMMB2NsaWVudDEwggEiMA0GCSqGSIb3DQEB
          AQUAA4IBDwAwggEKAoIBAQC0msirO7kXyGEC93stsNYGc02Z77Q2qfHFwaGYkUG8
          QvOF5SWN+jwTo5Td6Jj26A26a8MLCtK45TCBuMRNcUsHhajhT19ocphO20iY3zhi
          ycwV1id0iwME4kPd1m57BELRE9tUPOxF81/JQXdR1fwT5KRVHYRDWZhaZ5aBmlZY
          3t/H9Ly0RBYyApkMaGs3nlb94OOug6SouUfRt02S59ja3wsE2SVF/Eui647OXP7O
          QdYXofxuqLoNkE8EnAdl43/enGLiCIVd0G2lABibFF+gbxTtfgbg7YtfUZJdL+Mb
          RAcAtuLXEamNQ9H63JgVF16PlQVCDz2XyI3uCfPpDDiBAgMBAAGjUzBRMB0GA1Ud
          DgQWBBQ26bWk8qfEBjXs/xZ4m8JZyalnITAfBgNVHSMEGDAWgBQ26bWk8qfEBjXs
          /xZ4m8JZyalnITAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQAZ
          VcsgslBKanKOieJ5ik2d9qzOMXKfBuWPRFWbkC3t9i5awhHqnGAaj6nICnnMZIyt
          rdx5lZW5aaQyf0EP/90JAA8Xmty4A6MXmEjQAMiCOpP3A7eeS6Xglgi8IOZl4/bg
          LonW62TUkilo5IiFt/QklFTeHIjXB+OvA8+2Quqyd+zp7v6KnhXjvaomim78DhwE
          0PIUnjmiRpGpHfTVioTdfhPHZ2Y93Y8K7juL93sQog9aBu5m9XRJCY6wGyWPE83i
          kmLfGzjcnaJ6kqCd9xQRFZ0JwHmGlkAQvFoeengbNUqSyjyVgsOoNkEsrWwe/JFO
          iqBvjEhJlvRoefvkdR98
          -----END CERTIFICATE-----
          """;

  /*
   * python -c "import sys;print sys.argv[1].decode('hex').encode('base64').strip('\n=')" $(openssl
   * x509 -fingerprint -sha256 -in client1.crt | grep -Po '(?<=Fingerprint=).*' | sed s/://g)
   */
  public static final String SAMPLE_CERT3_HASH = "GM2tYFuzdpDXN0lqpUXlsvrqk8OdMayryV+4/DOFZ0M";

  private CertificateSamples() {}
}
