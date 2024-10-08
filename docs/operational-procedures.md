# Operational procedures

This document covers procedures that are typically used when running a
production registry system.

## Stackdriver monitoring

[Stackdriver Monitoring](https://cloud.google.com/monitoring/docs/) is used to
instrument internal state within the Nomulus internal environment. This is
broadly called white-box monitoring. EPP, DNS, and WHOIS are instrumented. The
metrics monitored are as follows:

*   `/custom/dns/publish_domain_requests` -- A count of publish domain requests,
    described by the target TLD and the return status code from the underlying
    DNS implementation.
*   `/custom/dns/publish_host_requests` -- A count of publish host requests,
    described by the target TLD and the return status code from the underlying
    DNS implementation.
*   `/custom/epp/requests` -- A count of EPP requests, described by command
    name, client id, and return status code.
*   `/custom/epp/processing_time` -- A [Distribution][distribution] representing
    the processing time for EPP requests, described by command name, client id,
    and return status code.
*   `/custom/whois/requests` -- A count of WHOIS requests, described by command
    name, number of returned results, and return status code.
*   `/custom/whois/processing_time` -- A [Distribution][distribution]
    representing the processing time for WHOIS requests, described by command
    name, number of returned results, and return status code.

Follow the guide to [set up a Stackdriver
account](https://cloud.google.com/monitoring/accounts/guide) and associate it
with the GCP project containing the Nomulus App Engine app. Once the two have
been linked, monitoring will start automatically. For now, because the
visualization of custom metrics in Stackdriver is embryronic, you can retrieve
and visualize the collected metrics with a script, as described in the guide on
[Reading Time
Series](https://cloud.google.com/monitoring/custom-metrics/reading-metrics) and
the [custom metric code
sample](https://github.com/GoogleCloudPlatform/python-docs-samples/blob/master/monitoring/api/v3/custom_metric.py).

In addition to the included white-box monitoring, black-box monitoring should be
set up to exercise the functionality of the registry platform as a user would
see it. This monitoring should, for example, create a new domain name every few
minutes via EPP and then verify that the domain exists in DNS and WHOIS. For
now, no black-box monitoring implementation is provided with the Nomulus
platform.

## Updating cursors

In most cases, cursors will not advance if a task that utilizes a cursor fails
(so that the task can be retried for that given timestamp). However, there are
some cases where a cursor is updated at the end of a job that produces bad
output (for example, RDE export), and in order to re-run a job, the cursor will
need to be rolled back.

In rare cases it might be useful to roll a cursor forward if there is some bad
data at a given time that prevents a task from completing successfully, and an
acceptable solution is to simply skip the bad data.

Cursors can be updated as follows:

```shell
$ nomulus -e {ENVIRONMENT} update_cursors exampletld --type RDE_STAGING \
    --timestamp 2016-09-01T00:00:00Z
Update Cursor@ahFzfmRvbWFpbi1yZWdpc3RyeXIzCxIPRW50aXR5R3JvdXBSb290Igljcm9zcy10bGQMCxIIUmVnaXN0cnkiB3lvdXR1YmUM_RDE_STAGING
cursorTime: 2016-09-23T00:00:00.000Z -> 2016-09-01T00:00:00.000Z

Perform this command? (y/N): Y
Updated 1 entities.
```

## gTLD reporting

gTLD registry operators are required by ICANN to provide various reports (ccTLDs
are not generally subject to these requirements). The Nomulus system provides
some of these reports, but others will need to be implemented using custom
scripts.

### Registry Data Escrow (RDE)

[RDE](https://newgtlds.icann.org/en/applicants/data-escrow) is a daily deposit
of the contents of the registry, sent to a third-party escrow provider. The
details are contained in Specification 2 of the [registry
agreement][registry-agreement].

Nomulus provides [code to generate and send these
deposits](./operational-procedures/rde-deposits.md).

### Monthly registry activity and transaction reporting

ICANN requires monthly activity and transaction reporting. The details are
contained in Specification 3 of the [registry agreement][registry-agreement].

These reports are mostly generated by querying the Cloud SQL database. There is
currently a Google proprietary class to query DNS related activities that is
not included in the open source Nomulus release.

### Zone File Access (ZFA)

ICANN requires a mechanism for them to be able to retrieve DNS zone file
information. The details are contained in part 2 of Specification 4 of the
[registry agreement][registry-agreement].

This information will come from the DNS server, rather than Nomulus itself, so
ZFA is not part of the Nomulus release.

### Bulk Registration Data Access (BRDA)

BRDA is a weekly archive of the contents of the registry. The details are
contained in part 3 of Specification 4 of the [registry
agreement][registry-agreement].

ICANN uses sFTP to retrieve BRDA data from a server provided by the registry.
Nomulus provides [code to generate these
deposits](./operational-procedures/brda-deposits.md), but a separate sFTP server
must be configured, and the deposits must be moved onto the server for access by
ICANN.

### Spec 11 reporting

[Spec 11][spec-11] reporting must be provided to ICANN as part of their
anti-abuse efforts. This is covered in Specification 11 of the
[registry agreement][registry-agreement], but the details are little spotty.

The Nomulus release does not generate these reports.

[distribution]: https://cloud.google.com/monitoring/api/ref_v3/rest/v3/TypedValue#Distribution
[registry-agreement]: https://newgtlds.icann.org/sites/default/files/agreements/agreement-approved-09jan14-en.pdf
[spec-11]: https://newgtlds.icann.org/en/applicants/agb/base-agreement-specs-pic-faqs
