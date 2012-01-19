package mosaic.core.ops;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mosaic.core.exceptions.ExceptionTracer;
import mosaic.core.exceptions.NullCompletionCallback;

/**
 * Basic implementation of an asynchronous operation. It uses Java
 * {@link FutureTask} to implement the asynchronism.
 * 
 * @author Georgiana Macariu
 * @param <T>
 *            The type of the actual result of the asynchronous operation.
 */
public class GenericOperation<T> implements IOperation<T> {
	private IOperationCompletionHandler<T> complHandler;
	private final FutureTask<T> operation;
	private CountDownLatch cHandlerSet = new CountDownLatch(1);

	/**
	 * Creates a new operation.
	 * 
	 * @param op
	 *            the code to run
	 */
	public GenericOperation(Callable<T> op) {
		super();
		this.operation = new GenericTask(op);
	}

	/**
	 * Cancels the asynchronous operation.
	 * 
	 * @return <code>true</code> if operation was cancelled
	 */
	@Override
	public boolean cancel() {
		boolean cancelled = true;
		if (this.operation != null) {
			if ((cancelled = this.operation.cancel(true))) {
				assert getHandler() != null : "Operation callback is NULL.";
				getHandler().onFailure(
						new NullCompletionCallback(
								"Operation callback is NULL."));
			}
		}
		return cancelled;
	}

	/**
	 * Returns <code>true</code> if this task was cancelled before it completed
	 * normally.
	 * 
	 * @return <code>true</code> if this task was cancelled before it completed
	 */
	@Override
	public boolean isCancelled() {
		boolean cancelled = true;
		if (this.operation != null) {
			cancelled = this.operation.isCancelled();
		}
		return cancelled;
	}

	/**
	 * Returns <code>true</code> if this task completed. Completion may be due
	 * to normal termination, an exception, or cancellation -- in all of these
	 * cases, this method will return <code>true</code>.
	 * 
	 * @return <code>true</code> if this task completed
	 */
	@Override
	public boolean isDone() {
		boolean done = false;
		if (this.operation != null) {
			done = this.operation.isDone();
		}
		return done;
	}

	/**
	 * Waits if necessary for the computation to complete, and then retrieves
	 * its result.
	 * 
	 * @return the computed result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Override
	public T get() throws InterruptedException, ExecutionException {
		T result = null;
		if (this.operation != null) {
			try {
				result = this.operation.get();
			} catch (ExecutionException e) {
				// TODO customize exception
				if (e.getCause() instanceof UnsupportedOperationException) {
					getHandler().onFailure(e.getCause());
				}
			}
		}
		return result;
	}

	/**
	 * Waits if necessary for at most the given time for the computation to
	 * complete, and then retrieves its result, if available.
	 * 
	 * @param timeout
	 *            the maximum time to wait
	 * @param unit
	 *            the time unit of the timeout argument
	 * @return the computed result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * @throws TimeoutException
	 *             if the wait timed out
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		T result = null;
		if (this.operation != null) {
			try {
				result = this.operation.get(timeout, unit);
			} catch (ExecutionException e) {
				// TODO customize exception
				if (e.getCause() instanceof UnsupportedOperationException) {
					getHandler().onFailure(e.getCause());
				}
			}
		}
		return result;
	}

	/**
	 * Returns the completion handler to be called when operation completes.
	 * 
	 * @return the completion handler to be called when operation completes
	 */
	public IOperationCompletionHandler<T> getHandler() {
		return this.complHandler;
	}

	/**
	 * Sets the completion handler to be called when operation completes.
	 * 
	 * @param compHandler
	 *            the completion handler to be called when operation completes
	 */
	public void setHandler(IOperationCompletionHandler<T> complHandler) {
		this.complHandler = complHandler;
		this.cHandlerSet.countDown();
	}

	/**
	 * Returns the enclosed {@link FutureTask} object which manages the actual
	 * execution of the operation.
	 * 
	 * @return the enclosed {@link FutureTask} object
	 */
	public FutureTask<T> getOperation() {
		return this.operation;
	}

	private final class GenericTask extends FutureTask<T> {

		public GenericTask(Callable<T> callable) {
			super(callable);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.FutureTask#run()
		 */
		@Override
		public void run() {
			super.run();
			try {
				GenericOperation.this.cHandlerSet.await();
				GenericOperation.this.complHandler.onSuccess(this.get());
			} catch (InterruptedException e) {
				ExceptionTracer.traceHandled(e);
			} catch (ExecutionException e) {
				// TODO customize exception
				if (e.getCause() instanceof UnsupportedOperationException) {
					getHandler().onFailure(e.getCause());
				} else {
					ExceptionTracer.traceHandled(e);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.FutureTask#set(java.lang.Object)
		 */
		@Override
		protected void set(T t) {
			super.set(t);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.util.concurrent.FutureTask#setException(java.lang.Throwable)
		 */
		@Override
		protected void setException(Throwable t) {
			super.setException(t);
			try {
				GenericOperation.this.cHandlerSet.await();
			} catch (InterruptedException e) {
				ExceptionTracer.traceHandled(e);
			}
			GenericOperation.this.complHandler.onFailure(t);
		}

	}
}