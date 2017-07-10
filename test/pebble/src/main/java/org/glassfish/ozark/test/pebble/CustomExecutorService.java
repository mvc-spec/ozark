/*
 * Copyright © 2017 Ivar Grimstad (ivar.grimstad@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glassfish.ozark.test.pebble;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CustomExecutorService implements ExecutorService {

  @Override
  public void shutdown() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Runnable> shutdownNow() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isShutdown() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isTerminated() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Future<?> submit(Runnable task) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void execute(Runnable command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
