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
 * ForkJoinPool类可以根据可用的处理器数量和任务需求动态地对线程进行管理。Fork-join使用了work-stealing策略，
 * 即线程在完成自己的任务之后，发现其他线程还有活没干完，就主动帮其他人一起干。
 * 在Fork-join API中，活动任务（active task）所创建的子任务都是由与所创建主任务所不同的另一套方法来负责调度的。
 * 在一个程序中只会用到一个fork-join池来调度任务，且由于该池使用了守护线程，所以用过之后也无需执行关闭操作。
 * fork-join非常适合解决那些可以递归分解至小都足以顺序运行的问题。
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
