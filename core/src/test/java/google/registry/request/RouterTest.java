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

package google.registry.request;

import static com.google.common.truth.Truth.assertThat;
import static google.registry.request.auth.Auth.AUTH_ADMIN;
import static org.junit.jupiter.api.Assertions.assertThrows;

import google.registry.request.Action.GaeService;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Router}. */
public final class RouterTest {

  ////////////////////////////////////////////////////////////////////////////////////////////////

  public interface Empty {}

  @Test
  void testRoute_noRoutes_throws() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> Router.create(Empty.class));
    assertThat(thrown)
        .hasMessageThat()
        .contains("No routes found for class: google.registry.request.RouterTest.Empty");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////

  @Action(service = GaeService.DEFAULT, path = "/sloth", auth = AUTH_ADMIN)
  public static final class SlothTask implements Runnable {
    @Override
    public void run() {}
  }

  public interface SlothComponent {
    SlothTask slothTask();
  }

  @Test
  void testRoute_pathMatch_returnsRoute() {
    Optional<Route> route = Router.create(SlothComponent.class).route("/sloth");
    assertThat(route).isPresent();
    assertThat(route.get().action().path()).isEqualTo("/sloth");
    assertThat(route.get().instantiator()).isInstanceOf(Function.class);
  }

  @Test
  void testRoute_pathMismatch_returnsEmpty() {
    assertThat(Router.create(SlothComponent.class).route("/doge")).isEmpty();
  }

  @Test
  void testRoute_pathIsAPrefix_notAllowedByDefault() {
    assertThat(Router.create(SlothComponent.class).route("/sloth/extra")).isEmpty();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////

  @Action(service = GaeService.DEFAULT, path = "/prefix", isPrefix = true, auth = AUTH_ADMIN)
  public static final class PrefixTask implements Runnable {
    @Override
    public void run() {}
  }

  public interface PrefixComponent {
    PrefixTask prefixTask();
  }

  @Test
  void testRoute_prefixMatches_returnsRoute() {
    assertThat(Router.create(PrefixComponent.class).route("/prefix")).isPresent();
    assertThat(Router.create(PrefixComponent.class).route("/prefix/extra")).isPresent();
  }

  @Test
  void testRoute_prefixDoesNotMatch_returnsEmpty() {
    assertThat(Router.create(PrefixComponent.class).route("")).isEmpty();
    assertThat(Router.create(PrefixComponent.class).route("/")).isEmpty();
    assertThat(Router.create(PrefixComponent.class).route("/ulysses")).isEmpty();
    assertThat(Router.create(PrefixComponent.class).route("/man/of/sadness")).isEmpty();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////

  @Action(service = GaeService.DEFAULT, path = "/prefix/long", isPrefix = true, auth = AUTH_ADMIN)
  public static final class LongTask implements Runnable {
    @Override
    public void run() {}
  }

  public interface LongPathComponent {
    PrefixTask prefixTask();
    LongTask longTask();
  }

  @Test
  void testRoute_prefixAndLongPathMatch_returnsLongerPath() {
    Optional<Route> route = Router.create(LongPathComponent.class).route("/prefix/long");
    assertThat(route).isPresent();
    assertThat(route.get().action().path()).isEqualTo("/prefix/long");
  }

  @Test
  void testRoute_prefixAndLongerPrefixMatch_returnsLongerPrefix() {
    Optional<Route> route = Router.create(LongPathComponent.class).route("/prefix/longer");
    assertThat(route).isPresent();
    assertThat(route.get().action().path()).isEqualTo("/prefix/long");
  }

  @Test
  void testRoute_onlyShortPrefixMatches_returnsShortPrefix() {
    Optional<Route> route = Router.create(LongPathComponent.class).route("/prefix/cat");
    assertThat(route).isPresent();
    assertThat(route.get().action().path()).isEqualTo("/prefix");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////

  public interface WeirdMethodsComponent {
    SlothTask hasAnArgumentWhichIsIgnored(boolean lol);
    Callable<?> notARunnableWhichIsIgnored();
  }

  @Test
  void testRoute_methodsInComponentAreIgnored_throws() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> Router.create(WeirdMethodsComponent.class));
    assertThat(thrown)
        .hasMessageThat()
        .contains(
            "No routes found for class: google.registry.request.RouterTest.WeirdMethodsComponent");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////

  @Action(service = GaeService.DEFAULT, path = "/samePathAsOtherTask", auth = AUTH_ADMIN)
  public static final class DuplicateTask1 implements Runnable {
    @Override
    public void run() {}
  }

  @Action(service = GaeService.DEFAULT, path = "/samePathAsOtherTask", auth = AUTH_ADMIN)
  public static final class DuplicateTask2 implements Runnable {
    @Override
    public void run() {}
  }

  public interface DuplicateComponent {
    DuplicateTask1 duplicateTask1();
    DuplicateTask2 duplicateTask2();
  }

  @Test
  void testCreate_twoTasksWithSameMethodAndPath_resultsInError() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> Router.create(DuplicateComponent.class));
    assertThat(thrown).hasMessageThat().contains("Multiple entries with same key");
  }
}
