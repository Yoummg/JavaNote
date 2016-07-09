package thread;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用阻塞队列来并发地处理计算文件大小的问题，并且这里没有使用想AtomicLong这样的变量，而是把
 * 每个线程计算的所得的部分文件大小的值插入到一个队列中。随后主线程可以遍历改队列获得每部分结果并进行累加。
 * 阻塞队列帮我们完成了线程间的数据交换和同步操作。
 * @author Administrator
 *
 */
public class ConcurrentTotalFileSizeWQueue {
	private ExecutorService service;
	final private BlockingQueue<Long> fileSizes =
			new ArrayBlockingQueue<>(500);
	final AtomicLong pendingFileVisits = new AtomicLong();

	private void startExploreDir (final File file) {
		pendingFileVisits.incrementAndGet();
		service.execute(new Runnable() {
			public void run() {
				exploreDir(file);
			}
		});
	}

	private void exploreDir (final File file) {
		long fileSize = 0;
		if ( file.isFile() )
			fileSize = file.length();
		else {
			final File[] children = file.listFiles();
			if ( children != null )
				for ( final File child : children )
					if ( child.isFile() )
						fileSize += child.length();
					else {
						startExploreDir(child);
					}
		}
		try {
			fileSizes.put(fileSize);
		} catch ( Exception e) {
			throw new RuntimeException();
		}
		pendingFileVisits.decrementAndGet();
	}

	private long getTotalSizeOfFile (final String filename) throws InterruptedException {
		service = Executors.newFixedThreadPool(100);
		try {
			startExploreDir(new File (filename));
			long totalSize = 0;
			while ( pendingFileVisits.get() > 0 || fileSizes.size() > 0 ) {
				final Long size = fileSizes.poll(10, TimeUnit.SECONDS);
				totalSize += size;
			}
			return totalSize;
		} finally {
			service.shutdown();
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		 final long start = System.nanoTime();
		 final long total = new ConcurrentTotalFileSizeWQueue()
				 .getTotalSizeOfFile("F:");
	     final long end = System.nanoTime();
	     System.out.println("Total Size: " + total);
	     System.out.println("Time taken: "+ (end - start)/1.0e9);

	}
}
