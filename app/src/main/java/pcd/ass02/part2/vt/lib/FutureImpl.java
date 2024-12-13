package pcd.ass02.part2.vt.lib;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pcd.ass02.part2.common.Report;

public class FutureImpl implements FutureWithSnapshots<Report>{

	private Report report;
	private Lock lock;
	private Condition reportAvailable;
	private boolean isReportAvailable;
	private boolean isError;
	private String error;
	
	public FutureImpl() {
		lock = new ReentrantLock();
		reportAvailable = lock.newCondition();
		isReportAvailable = false;
		isError = false;
	}

	public FutureImpl(Report initialSnapshot) {
		this();
		this.report = initialSnapshot;
	}

	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		try {
			lock.lockInterruptibly();
			return isReportAvailable;
		} catch (Exception ex) {
			return false;
		} finally {
			lock.unlock();
		}	
	}

	@Override
	public Report get() throws InterruptedException, ExecutionException {
		try {
			lock.lockInterruptibly();
			while (!isReportAvailable && !isError) {
				reportAvailable.await();
			}
		} finally {
			lock.unlock();
		}
		if (!isError) {
			return report;
		} else {
			throw new InterruptedException(error);
		}
	}

	@Override
	public Report get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void notifyCompletion(Report report) {
		try {
			lock.lockInterruptibly();
			this.report = report;
			isReportAvailable = true;
			reportAvailable.signalAll();
		} catch (Exception ex) {
		} finally {
			lock.unlock();
		}
	}

	protected void notifyNewSnapshot(Report report) {
		try {
			lock.lockInterruptibly();
			this.report = report;
		} catch (Exception ex) {
		} finally {
			lock.unlock();
		}
	}

	protected void notifyError(String error) {
		try {
			lock.lockInterruptibly();
			this.error = error;
			isReportAvailable = true;
			reportAvailable.signalAll();
		} catch (Exception ex) {
		} finally {
			lock.unlock();
		}
	}
	@Override
	public Report getSnapshot() {
		try {
			lock.lock();
			return report;
		} finally {
			lock.unlock();
		}
	}

}
