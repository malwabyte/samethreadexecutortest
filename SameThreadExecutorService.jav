import java.util.*;
import java.util.concurrent.*;

public class SameThreadExecutorService implements ExecutorService {

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        try {
            return CompletableFuture.completedFuture(task.call());
        } catch (Exception e) {
            CompletableFuture<T> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        task.run();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        task.run();
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        // Since we're synchronous, timeout doesn't matter
        return invokeAll(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        for (Callable<T> task : tasks) {
            try {
                return task.call();
            } catch (Exception e) {
                // ignore and try next
            }
        }
        throw new ExecutionException(new NoSuchElementException("No successful task"));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return invokeAny(tasks);
    }

    // Shutdown methods (no-op for tests)
    @Override public void shutdown() {}
    @Override public List<Runnable> shutdownNow() { return Collections.emptyList(); }
    @Override public boolean isShutdown() { return false; }
    @Override public boolean isTerminated() { return false; }
    @Override public boolean awaitTermination(long timeout, TimeUnit unit) { return true; }
}
