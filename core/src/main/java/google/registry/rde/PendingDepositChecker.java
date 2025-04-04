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

package google.registry.rde;

import static com.google.common.base.Preconditions.checkArgument;
import static google.registry.persistence.transaction.TransactionManagerFactory.tm;
import static google.registry.util.DateTimeUtils.isBeforeOrAt;

import com.google.common.collect.ImmutableSetMultimap;
import google.registry.config.RegistryConfig.Config;
import google.registry.model.common.Cursor;
import google.registry.model.common.Cursor.CursorType;
import google.registry.model.rde.RdeMode;
import google.registry.model.tld.Tld;
import google.registry.model.tld.Tld.TldType;
import google.registry.model.tld.Tlds;
import google.registry.util.Clock;
import jakarta.inject.Inject;
import java.util.Optional;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Utility class that determines which RDE or BRDA deposits need to be created.
 *
 * <p>This class is called by {@link RdeStagingAction} at the beginning of its execution. Since it
 * stages everything in a single run, it needs to know what's awaiting deposit.
 *
 * <p>We start off by getting the list of TLDs with escrow enabled. We then check {@code cursor} to
 * see when it was due for a deposit. If that's in the past, then we know that we need to generate a
 * deposit. If it's really far in the past, we might have to generate multiple deposits for that
 * TLD, based on the configured interval.
 *
 * <p><i>However</i> we will only generate one interval forward per run, since the reduce phase
 * rolls forward a TLD's cursor, and we can't have that happening in parallel.
 *
 * <p>If no deposits have been made so far, then {@code startingPoint} is used as the watermark of
 * the next deposit. If that's a day in the future, then escrow won't start until that date. This
 * first deposit time will be set to the database in a transaction.
 */
public final class PendingDepositChecker {

  @Inject Clock clock;
  @Inject @Config("brdaDayOfWeek") int brdaDayOfWeek;
  @Inject @Config("brdaInterval") Duration brdaInterval;
  @Inject @Config("rdeInterval") Duration rdeInterval;
  @Inject PendingDepositChecker() {}

  /** Returns multimap of TLDs to all RDE and BRDA deposits that need to happen. */
  public ImmutableSetMultimap<String, PendingDeposit>
      getTldsAndWatermarksPendingDepositForRdeAndBrda() {
    return new ImmutableSetMultimap.Builder<String, PendingDeposit>()
        .putAll(
            getTldsAndWatermarksPendingDeposit(
                RdeMode.FULL,
                CursorType.RDE_STAGING,
                rdeInterval,
                clock.nowUtc().withTimeAtStartOfDay()))
        .putAll(
            getTldsAndWatermarksPendingDeposit(
                RdeMode.THIN,
                CursorType.BRDA,
                brdaInterval,
                advanceToDayOfWeek(clock.nowUtc().withTimeAtStartOfDay(), brdaDayOfWeek)))
        .build();
  }

  private ImmutableSetMultimap<String, PendingDeposit> getTldsAndWatermarksPendingDeposit(
      RdeMode mode, CursorType cursorType, Duration interval, DateTime startingPoint) {
    checkArgument(interval.isLongerThan(Duration.ZERO));
    ImmutableSetMultimap.Builder<String, PendingDeposit> builder =
        new ImmutableSetMultimap.Builder<>();
    DateTime now = clock.nowUtc();
    for (String tldStr : Tlds.getTldsOfType(TldType.REAL)) {
      Tld tld = Tld.get(tldStr);
      if (!tld.getEscrowEnabled()) {
        continue;
      }
      // Avoid creating a transaction unless absolutely necessary.
      Optional<Cursor> maybeCursor =
          tm().transact(() -> tm().loadByKeyIfPresent(Cursor.createScopedVKey(cursorType, tld)));
      DateTime cursorValue = maybeCursor.map(Cursor::getCursorTime).orElse(startingPoint);
      if (isBeforeOrAt(cursorValue, now)) {
        DateTime watermark =
            maybeCursor
                .map(Cursor::getCursorTime)
                .orElse(transactionallyInitializeCursor(tld, cursorType, startingPoint));
        if (isBeforeOrAt(watermark, now)) {
          builder.put(tldStr, PendingDeposit.create(tldStr, watermark, mode, cursorType, interval));
        }
      }
    }
    return builder.build();
  }

  private DateTime transactionallyInitializeCursor(
      final Tld tld, final CursorType cursorType, final DateTime initialValue) {
    return tm().transact(
            () -> {
              Optional<Cursor> maybeCursor =
                  tm().loadByKeyIfPresent(Cursor.createScopedVKey(cursorType, tld));
              if (maybeCursor.isPresent()) {
                return maybeCursor.get().getCursorTime();
              }
              tm().put(Cursor.createScoped(cursorType, initialValue, tld));
              return initialValue;
            });
  }

  private static DateTime advanceToDayOfWeek(DateTime date, int dayOfWeek) {
    while (date.getDayOfWeek() != dayOfWeek) {
      date = date.plusDays(1);
    }
    return date;
  }
}
