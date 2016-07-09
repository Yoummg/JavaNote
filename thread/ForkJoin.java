package thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeoutException;

/**
 * ForkJoinPool����Ը��ݿ��õĴ�������������������̬�ض��߳̽��й���Fork-joinʹ����work-stealing���ԣ�
 * ���߳�������Լ�������֮�󣬷��������̻߳��л�û���꣬��������������һ��ɡ�
 * ��Fork-join API�У������active task��������������������������������������ͬ����һ�׷�����������ȵġ�
 * ��һ��������ֻ���õ�һ��fork-join�����������������ڸó�ʹ�����ػ��̣߳������ù�֮��Ҳ����ִ�йرղ�����
 * fork-join�ǳ��ʺϽ����Щ���Եݹ�ֽ���С������˳�����е����⡣
 * @author Administrator
 *
 */
public class ForkJoin {
	private final static ForkJoinPool forkJoinPool= new ForkJoinPool();

	private static class FileSizeFinder extends RecursiveTask<Long> {
		final File file;

		public FileSizeFinder (final File theFile) {
			file = theFile;
		}

		@Override
		protected Long compute() {
			long size = 0;
			if ( file.isFile() )
				size = file.length();
			else {
				final File[] children = file.listFiles();
				if ( children != null ) {
					List<ForkJoinTask<Long>> tasks =
							new ArrayList<>();
					for ( final File child : children ) {
						if ( child.isFile() )
							size += child.length();
						else
							tasks.add(new FileSizeFinder(child));
					}

					for ( final ForkJoinTask<Long> task : invokeAll(tasks) )
						size += task.join();
				}
			}
			return size;
		}

	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		 final long start = System.nanoTime();
		 final long total = forkJoinPool.invoke(
				 new FileSizeFinder(new File("F:")));
	     final long end = System.nanoTime();
	     System.out.println("Total Size: " + total);
	     System.out.println("Time taken: "+ (end - start)/1.0e9);

	}
}
