package thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 程序会出现死锁， TimeoutException
 * 子目录数超过线程池大小时，就会发生所有线程都在等待最底层子目录的计算结果，而最底层子目录
 * 的计算任务又没有额外的线程来执行，以至形成死锁。
 * @author Administrator
 *
 */
public class NaivelyConcurrenTotalFileSize {
	private long getToalSizeOfFileInDir (
			final ExecutorService service, final File file) throws InterruptedException, ExecutionException, TimeoutException {
		if ( file.isFile() )
			return file.length();

		final File[] children = file.listFiles();
		long total = 0;

		if ( children != null ) {
			final List<Future<Long>> partialTotalFutures = new ArrayList<Future<Long>>();
			for ( final File child : children ) {
				partialTotalFutures.add(service.submit(new Callable<Long>() {
					public Long call() throws Exception {
						return getToalSizeOfFileInDir(service,child);
					}

				}));
			}
			for (final Future<Long> partialTotalFuture : partialTotalFutures ) {
				total += partialTotalFuture.get(100, TimeUnit.SECONDS);
			}
		}
		return total;
	}

	private long getTotalSizeOfFile (final String filename) throws InterruptedException, ExecutionException, TimeoutException {
		final ExecutorService service = Executors.newFixedThreadPool(100);
		try {
			return getToalSizeOfFileInDir(service,new File(filename));
		} finally {
			service.shutdown();
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		 final long start = System.nanoTime();
		 final long total = new NaivelyConcurrenTotalFileSize()
				 .getTotalSizeOfFile("F:");
	     final long end = System.nanoTime();
	     System.out.println("Total Size: " + total);
	     System.out.println("Time taken: "+ (end - start)/1.0e9);

	}
}
