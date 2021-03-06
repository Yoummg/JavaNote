package thread;

import java.io.File;


public class TotalFileSizeSequential {
	private long getToalSizeOfFileInDir ( final File file ) {
		if ( file.isFile() )
			return file.length();

		final File[] children = file.listFiles();
		long total = 0;
		if ( children != null )
			for ( final File child : children )
				total += getToalSizeOfFileInDir(child);
		return total;
	}
	public static void main(String[] args) {
		 final long start = System.nanoTime();
		 final long total = new TotalFileSizeSequential()
				 .getToalSizeOfFileInDir(new File("E:"));
	     final long end = System.nanoTime();
	     System.out.println("Total Size: " + total);
	     System.out.println("Time taken: "+ (end - start)/1.0e9);

	}

}
