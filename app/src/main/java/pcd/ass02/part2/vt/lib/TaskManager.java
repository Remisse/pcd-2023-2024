package pcd.ass02.part2.vt.lib;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import pcd.common.Flag;

public class TaskManager {

	private String word;
	private int maxLevels;	
	private ReportImpl pages;
	private AtomicLong nTasks;
	private String rootAddress;
	private Optional<Flag> stopFlag;
	
	public TaskManager() {
		pages = new ReportImpl(word);
		stopFlag = Optional.empty();
	}

	public TaskManager(Flag stopFlag) {
		this.stopFlag = Optional.of(stopFlag);
		pages = new ReportImpl(word);
	}

	public void startSearching(String word, String address, int maxLevels, FutureImpl fut) {
		this.word = word;
		this.maxLevels = maxLevels;
		this.rootAddress = address;
		nTasks = new AtomicLong(0);
		pages.reset();
		
		try {
			log("Start searching " + word + " from " + address + " depth " + maxLevels + "...");
			
			if (stopFlag.isPresent() && stopFlag.get().isSet()) { 
				fut.notifyError("interrupted");
				return;
			}
			Thread t = spawnSearchTask(rootAddress, 1);
			Thread
				.ofVirtual()
				.start(() -> {
					try {
						t.join();
						log("Search completed.");
						pages.polish();
						fut.notifyCompletion(pages);
					} catch (Exception ex) {
						ex.printStackTrace();
						fut.notifyError(ex.getMessage());
					} 
				});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected Thread spawnSearchTask(String address, int level) throws PageAlreadyRegisteredException, InterruptedException  {
		boolean alreadyRegistered = pages.registerPage(address);
		if (!alreadyRegistered) {
			if (stopFlag.isPresent() && stopFlag.get().isSet()) { 
				throw new InterruptedException();
			}

			long id = nTasks.incrementAndGet();
			try {
				Thread t = Thread
							.ofVirtual()
							.name("searcher-task-" + id)
							.unstarted(new SearchAndAnalyseTask(word, address, level, maxLevels, pages, this));
				t.start();
				return t;
			} catch (Exception ex) {
				ex.printStackTrace();
				nTasks.decrementAndGet();
				// log("FAILED to spawn " + "searcher-task-" + id);
				throw ex;
			}
		} else {
			throw new PageAlreadyRegisteredException();
		}
	}
	
	protected ReportImpl getSnapshot() {
		return pages;
	}
	
	protected void taskDone(String name) {
		// log("Task " + name + " DONE - remaining: " + nTasks.longValue());
		nTasks.decrementAndGet();
	}

	public long getOngoingTasks() {
		return nTasks.longValue();
	}
	
	
	protected void log(String msg) {
		System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
	}
	
}
