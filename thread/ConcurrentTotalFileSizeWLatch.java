package thread;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用了CountDownLatch，当扫描到一个文件时，线程不再返回一个计算结果，
 * 而是去更新一个AtomicLong类型的共享变量totalSize。AtomicLong提供了更改并取回一个简单long
 * 型变量值得线程安全的方法，此外还用到另一个饺子pendingFileVisits的AtomicLong型变量,其作用
 * 是保存当前访问文件（或子目录）的数量，当该变量值变为0，就调用countDown()来释放线程闩。
 * @author Administrator
 *
 */
public class ConcurrentTotalFileSizeWLatch {
	private ExecutorService service;
	final private AtomicLong pendingFileVisits = new AtomicLong();
	final private AtomicLong totalSize = new AtomicLong();
	final private CountDownLatch latch = new CountDownLatch(1);

	private void updateTotalSizeOfFilesInDir (final File file) {
		long fileSize = 0;
		if ( file.isFile() )
			fileSize = file.length();
		else {
			final File[] children = file.listFiles();
			if ( children != null ) {
				for ( final File child : children ) {
					if ( child.isFile() )
						fileSize += child.length();
					else {
						pendingFileVisits.incrementAndGet();
						service.execute(new Runnable() {
							public void run() {
								updateTotalSizeOfFilesInDir(child);
							}
						});
					}
				}
			}
		}
		totalSize.addAndGet(fileSize);
		if ( pendingFileVisits.decrementAndGet() == 0 )
			latch.countDown();
	}

	private long getTotalSizeOfFile (final String filename) throws InterruptedException {
		service = Executors.newFixedThreadPool(100);
		pendingFileVisits.incrementAndGet();
		try {
			updateTotalSizeOfFilesInDir(new File(filename));
			latch.await(100, TimeUnit.SECONDS);
			return totalSize.longValue();
		} finally {
			service.shutdown();
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		 final long start = System.nanoTime();
		 final long total = new ConcurrentTotalFileSizeWLatch()
				 .getTotalSizeOfFile("F:");
	     final long end = System.nanoTime();
	     System.out.println("Total Size: " + total);
	     System.out.println("Time taken: "+ (end - start)/1.0e9);

	}

}
