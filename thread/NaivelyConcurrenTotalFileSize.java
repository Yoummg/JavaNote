package thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * �������������� TimeoutException
 * ��Ŀ¼�������̳߳ش�Сʱ���ͻᷢ�������̶߳��ڵȴ���ײ���Ŀ¼�ļ�����������ײ���Ŀ¼
 * �ļ���������û�ж�����߳���ִ�У������γ�������
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
